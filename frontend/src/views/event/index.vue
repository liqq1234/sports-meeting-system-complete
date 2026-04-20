<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="运动会">
          <el-select v-model="queryParams.meetingId" placeholder="全部" clearable>
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="queryParams.name" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.category" placeholder="全部" clearable>
            <el-option label="径赛" value="径赛" />
            <el-option label="田赛" value="田赛" />
            <el-option label="趣味赛" value="趣味赛" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleAdd" v-if="role === 0">新增项目</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="name" label="项目名称" width="130" />
        <el-table-column prop="meetingName" label="所属运动会" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="80" align="center" />
        <el-table-column label="性别限制" width="90" align="center">
          <template slot-scope="{ row }">
            {{ {0:'男',1:'女',2:'不限'}[row.genderLimit] || '不限' }}
          </template>
        </el-table-column>
        <el-table-column label="参赛人数" width="100" align="center">
          <template slot-scope="{ row }">{{ row.currentParticipants || 0 }} / {{ row.maxParticipants || '不限' }}</template>
        </el-table-column>
        <el-table-column prop="eventDate" label="比赛日期" width="110" />
        <el-table-column label="比赛时间" width="120" align="center">
          <template slot-scope="{ row }">{{ row.startTime }} - {{ row.endTime }}</template>
        </el-table-column>
        <el-table-column prop="venue" label="场地" width="100" show-overflow-tooltip />
        <el-table-column prop="refereeName" label="裁判员" width="100" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'info',1:'success',2:'danger'}[row.status]" size="small">
              {{ {0:'未开始',1:'进行中',2:'已结束'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right" v-if="role === 0">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleEnroll(row)" v-if="role === 2">报名</el-button>
            <el-dropdown trigger="click" @command="cmd => handleStatusChange(row, cmd)" style="margin: 0 4px">
              <el-button size="mini" type="text">状态<i class="el-icon-arrow-down"></i></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="0">未开始</el-dropdown-item>
                <el-dropdown-item :command="1">进行中</el-dropdown-item>
                <el-dropdown-item :command="2">已结束</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" v-if="role === 2">
          <template slot-scope="{ row }">
            <el-button size="mini" type="primary" @click="handleEnroll(row)" :disabled="row.enrolled">
              {{ row.enrolled ? '已报名' : '报名' }}
            </el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="所属运动会" prop="meetingId">
          <el-select v-model="form.meetingId" placeholder="请选择" style="width:100%">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="form.name" placeholder="如：男子100米" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择">
                <el-option label="径赛" value="径赛" />
                <el-option label="田赛" value="田赛" />
                <el-option label="趣味赛" value="趣味赛" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别限制" prop="genderLimit">
              <el-select v-model="form.genderLimit" placeholder="请选择">
                <el-option label="男" :value="0" />
                <el-option label="女" :value="1" />
                <el-option label="不限" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最大人数">
              <el-input-number v-model="form.maxParticipants" :min="0" :max="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最少人数">
              <el-input-number v-model="form.minParticipants" :min="0" :max="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="比赛日期" prop="eventDate">
          <el-date-picker v-model="form.eventDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-time-picker v-model="form.startTime" value-format="HH:mm:ss" placeholder="开始时间" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-time-picker v-model="form.endTime" value-format="HH:mm:ss" placeholder="结束时间" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="比赛场地">
          <el-input v-model="form.venue" placeholder="请输入场地" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成绩类型" prop="scoreType">
              <el-select v-model="form.scoreType" placeholder="请选择">
                <el-option label="计时(越小越好)" :value="0" />
                <el-option label="计距/计高(越大越好)" :value="1" />
                <el-option label="计分(越大越好)" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成绩单位">
              <el-input v-model="form.scoreUnit" placeholder="秒/米/分" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="裁判员">
          <el-select v-model="form.refereeId" placeholder="请选择裁判员" clearable filterable style="width:100%">
            <el-option v-for="r in refereeList" :key="r.id" :label="r.realName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则说明">
          <el-input v-model="form.rules" type="textarea" :rows="2" placeholder="请输入比赛规则" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 报名对话框 -->
    <el-dialog title="确认报名" :visible.sync="enrollVisible" width="400px">
      <p>确定报名参加「{{ enrollEvent.name }}」？</p>
      <el-input v-model="enrollRemark" type="textarea" :rows="2" placeholder="备注（选填）" style="margin-top:12px" />
      <div slot="footer">
        <el-button @click="enrollVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEnroll">确认报名</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEventPage, addEvent, updateEvent, deleteEvent, updateEventStatus } from '@/api/event'
import { getMeetingList } from '@/api/meeting'
import { getUserPage } from '@/api/user'
import { enroll } from '@/api/registration'

export default {
  name: 'EventList',
  data() {
    return {
      loading: false, tableData: [], total: 0, meetingList: [], refereeList: [],
      queryParams: { pageNum: 1, pageSize: 10, meetingId: null, name: '', category: null, status: null },
      dialogVisible: false, dialogTitle: '', form: {}, submitLoading: false,
      formRules: {
        meetingId: [{ required: true, message: '请选择运动会', trigger: 'change' }],
        name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        scoreType: [{ required: true, message: '请选择成绩类型', trigger: 'change' }]
      },
      enrollVisible: false, enrollEvent: {}, enrollRemark: ''
    }
  },
  computed: { role() { return this.$store.getters.role } },
  created() { this.loadData(); this.loadMeetings(); this.loadReferees() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getEventPage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() {
      const res = await getMeetingList(); this.meetingList = res.data || []
    },
    async loadReferees() {
      try {
        const res = await getUserPage({ pageNum: 1, pageSize: 100, role: 1 })
        this.refereeList = res.data.records || []
      } catch (e) { /* ignore */ }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, meetingId: null, name: '', category: null, status: null }; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增比赛项目'
      this.form = { meetingId: null, name: '', category: '', genderLimit: 2, maxParticipants: 0, minParticipants: 0, eventDate: '', startTime: '', endTime: '', venue: '', scoreType: 0, scoreUnit: '', refereeId: null, rules: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '编辑比赛项目'; this.form = { ...row }; this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) { await updateEvent(this.form); this.$message.success('修改成功') }
          else { await addEvent(this.form); this.$message.success('新增成功') }
          this.dialogVisible = false; this.loadData()
        } finally { this.submitLoading = false }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除项目「${row.name}」？`, '提示', { type: 'warning' }).then(async () => {
        await deleteEvent(row.id); this.$message.success('删除成功'); this.loadData()
      })
    },
    async handleStatusChange(row, status) {
      await updateEventStatus(row.id, status); this.$message.success('状态变更成功'); this.loadData()
    },
    handleEnroll(row) {
      this.enrollEvent = row; this.enrollRemark = ''; this.enrollVisible = true
    },
    async submitEnroll() {
      await enroll(this.enrollEvent.id, this.enrollRemark)
      this.$message.success('报名成功'); this.enrollVisible = false; this.loadData()
    }
  }
}
</script>
