<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="运动会名称">
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="筹备中" :value="0" />
            <el-option label="报名中" :value="1" />
            <el-option label="报名截止" :value="2" />
            <el-option label="进行中" :value="3" />
            <el-option label="已结束" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleAdd" v-if="role === 0">新增运动会</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="name" label="运动会名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="location" label="举办地点" width="120" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="eventCount" label="项目数" width="80" align="center" />
        <el-table-column prop="registrationCount" label="报名数" width="80" align="center" />
        <el-table-column label="操作" width="280" align="center" fixed="right" v-if="role === 0">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown trigger="click" @command="cmd => handleStatusChange(row, cmd)" style="margin: 0 8px">
              <el-button size="mini" type="text">状态变更<i class="el-icon-arrow-down"></i></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="0" :disabled="row.status===0">筹备中</el-dropdown-item>
                <el-dropdown-item :command="1" :disabled="row.status===1">报名中</el-dropdown-item>
                <el-dropdown-item :command="2" :disabled="row.status===2">报名截止</el-dropdown-item>
                <el-dropdown-item :command="3" :disabled="row.status===3">进行中</el-dropdown-item>
                <el-dropdown-item :command="4" :disabled="row.status===4">已结束</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper"
          :total="total" :page-size.sync="queryParams.pageSize" :current-page.sync="queryParams.pageNum"
          :page-sizes="[10, 20, 50]" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="运动会名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入运动会名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="举办日期" prop="startDate">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" style="width: 100%" />
        </el-form-item>
        <el-form-item label="举办地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入举办地点" />
        </el-form-item>
        <el-form-item label="报名时间">
          <el-date-picker v-model="enrollRange" type="datetimerange" range-separator="至" start-placeholder="报名开始" end-placeholder="报名截止" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMeetingPage, addMeeting, updateMeeting, deleteMeeting, updateMeetingStatus } from '@/api/meeting'

export default {
  name: 'MeetingList',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, name: '', status: null },
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      dateRange: [],
      enrollRange: [],
      rules: {
        name: [{ required: true, message: '请输入运动会名称', trigger: 'blur' }],
        startDate: [{ required: true, message: '请选择举办日期', trigger: 'change' }]
      },
      submitLoading: false
    }
  },
  computed: {
    role() { return this.$store.getters.role }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getMeetingPage(this.queryParams)
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, name: '', status: null }; this.loadData() },
    statusType(s) { return { 0: 'info', 1: 'warning', 2: '', 3: 'success', 4: 'danger' }[s] || 'info' },
    handleAdd() {
      this.dialogTitle = '新增运动会'
      this.form = { name: '', description: '', startDate: '', endDate: '', location: '', enrollStart: null, enrollEnd: null }
      this.dateRange = []
      this.enrollRange = []
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '编辑运动会'
      this.form = { ...row }
      this.dateRange = row.startDate && row.endDate ? [row.startDate, row.endDate] : []
      this.enrollRange = row.enrollStart && row.enrollEnd ? [row.enrollStart, row.enrollEnd] : []
      this.dialogVisible = true
    },
    handleSubmit() {
      if (this.dateRange && this.dateRange.length === 2) {
        this.form.startDate = this.dateRange[0]
        this.form.endDate = this.dateRange[1]
      }
      if (this.enrollRange && this.enrollRange.length === 2) {
        this.form.enrollStart = this.enrollRange[0]
        this.form.enrollEnd = this.enrollRange[1]
      }
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) {
            await updateMeeting(this.form)
            this.$message.success('修改成功')
          } else {
            await addMeeting(this.form)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.loadData()
        } finally { this.submitLoading = false }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除运动会「${row.name}」？`, '提示', { type: 'warning' }).then(async () => {
        await deleteMeeting(row.id)
        this.$message.success('删除成功')
        this.loadData()
      })
    },
    async handleStatusChange(row, status) {
      const names = { 0: '筹备中', 1: '报名中', 2: '报名截止', 3: '进行中', 4: '已结束' }
      this.$confirm(`确定将状态变更为「${names[status]}」？`, '提示', { type: 'warning' }).then(async () => {
        await updateMeetingStatus(row.id, status)
        this.$message.success('状态变更成功')
        this.loadData()
      })
    }
  }
}
</script>
