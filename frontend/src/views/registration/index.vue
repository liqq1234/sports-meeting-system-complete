<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="运动会">
          <el-select v-model="queryParams.meetingId" placeholder="全部" clearable>
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="queryParams.keyword" placeholder="姓名/学号" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-row style="margin-bottom:12px" v-if="role === 0 || role === 1">
        <el-button type="success" size="small" :disabled="!selectedIds.length" @click="handleBatchReview(1)">批量通过</el-button>
        <el-button type="danger" size="small" :disabled="!selectedIds.length" @click="handleBatchReview(2)">批量驳回</el-button>
      </el-row>
      <el-table :data="tableData" v-loading="loading" stripe border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" v-if="role === 0 || role === 1" />
        <el-table-column prop="userRealName" label="运动员" width="100" />
        <el-table-column prop="userName" label="学号" width="100" />
        <el-table-column prop="userCollege" label="学院" width="120" show-overflow-tooltip />
        <el-table-column prop="userClassName" label="班级" width="110" />
        <el-table-column prop="eventName" label="比赛项目" width="130" />
        <el-table-column prop="meetingName" label="运动会" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.status === 4" type="danger" size="small" effect="dark">因伤退赛</el-tag>
            <el-tag v-else :type="{0:'warning',1:'success',2:'danger',3:'info'}[row.status]" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="160" />
        <el-table-column prop="reviewerName" label="审核人" width="90" />
        <el-table-column prop="rejectReason" label="驳回原因" width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right" v-if="role === 0 || role === 1">
          <template slot-scope="{ row }">
            <template v-if="row.status === 0">
              <el-button size="mini" type="text" style="color:#67C23A" @click="handleReview(row, 1)">通过</el-button>
              <el-button size="mini" type="text" style="color:#F56C6C" @click="handleReview(row, 2)">驳回</el-button>
            </template>
            <span v-else style="color:#999;font-size:12px">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper"
          :total="total" :page-size.sync="queryParams.pageSize" :current-page.sync="queryParams.pageNum"
          :page-sizes="[10, 20, 50]" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <!-- 驳回原因对话框 -->
    <el-dialog title="驳回原因" :visible.sync="rejectVisible" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
      <div slot="footer">
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确认驳回</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getRegistrationPage, reviewRegistration, batchReview } from '@/api/registration'
import { getMeetingList } from '@/api/meeting'

export default {
  name: 'RegistrationList',
  data() {
    return {
      loading: false, tableData: [], total: 0, meetingList: [], selectedIds: [],
      queryParams: { pageNum: 1, pageSize: 10, meetingId: null, status: null, keyword: '' },
      rejectVisible: false, rejectReason: '', rejectTarget: null, batchMode: false
    }
  },
  computed: { role() { return this.$store.getters.role } },
  created() { this.loadData(); this.loadMeetings() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getRegistrationPage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() { const res = await getMeetingList(); this.meetingList = res.data || [] },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, meetingId: null, status: null, keyword: '' }; this.loadData() },
    handleSelectionChange(rows) { this.selectedIds = rows.filter(r => r.status === 0).map(r => r.id) },
    async handleReview(row, status) {
      if (status === 2) {
        this.rejectTarget = row; this.rejectReason = ''; this.batchMode = false; this.rejectVisible = true
        return
      }
      await reviewRegistration({ id: row.id, status, rejectReason: '' })
      this.$message.success('审核通过'); this.loadData()
    },
    async submitReject() {
      if (this.batchMode) {
        await batchReview({ ids: this.selectedIds, status: 2, rejectReason: this.rejectReason })
        this.$message.success('批量驳回成功')
      } else {
        await reviewRegistration({ id: this.rejectTarget.id, status: 2, rejectReason: this.rejectReason })
        this.$message.success('驳回成功')
      }
      this.rejectVisible = false; this.loadData()
    },
    async handleBatchReview(status) {
      if (!this.selectedIds.length) return
      if (status === 2) {
        this.batchMode = true; this.rejectReason = ''; this.rejectVisible = true; return
      }
      this.$confirm(`确定批量通过 ${this.selectedIds.length} 条记录？`, '提示', { type: 'warning' }).then(async () => {
        await batchReview({ ids: this.selectedIds, status: 1, rejectReason: '' })
        this.$message.success('批量通过成功'); this.loadData()
      })
    }
  }
}
</script>
