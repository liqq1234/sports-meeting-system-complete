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
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleAdd" v-if="role === 0">新增赛程</el-button>
          <el-button type="warning" icon="el-icon-magic-stick" @click="handleAutoGenerate" v-if="role === 0">自动生成</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="eventName" label="比赛项目" width="140" />
        <el-table-column prop="meetingName" label="运动会" min-width="180" show-overflow-tooltip />
        <el-table-column prop="roundName" label="轮次" width="100" />
        <el-table-column prop="groupNo" label="组号" width="70" align="center" />
        <el-table-column prop="scheduleDate" label="日期" width="110" />
        <el-table-column label="时间" width="120" align="center">
          <template slot-scope="{ row }">{{ row.startTime }} - {{ row.endTime }}</template>
        </el-table-column>
        <el-table-column prop="venueName" label="场地" width="100" />
        <el-table-column prop="refereeName" label="裁判员" width="100" />
        <el-table-column prop="athleteCount" label="运动员数" width="90" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'info',1:'success',2:'danger'}[row.status]" size="small">
              {{ {0:'未开始',1:'进行中',2:'已结束'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right" v-if="role === 0">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleViewAthletes(row)">运动员</el-button>
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
      </el-table>
      <div class="pagination-container">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper"
          :total="total" :page-size.sync="queryParams.pageSize" :current-page.sync="queryParams.pageNum"
          :page-sizes="[10, 20, 50]" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="运动会" prop="meetingId">
          <el-select v-model="form.meetingId" placeholder="请选择" style="width:100%" @change="loadEvents">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="比赛项目" prop="eventId">
          <el-select v-model="form.eventId" placeholder="请选择" style="width:100%">
            <el-option v-for="e in eventList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="轮次" prop="roundName">
          <el-input v-model="form.roundName" placeholder="如：预赛/决赛" />
        </el-form-item>
        <el-form-item label="组号">
          <el-input-number v-model="form.groupNo" :min="1" />
        </el-form-item>
        <el-form-item label="日期" prop="scheduleDate">
          <el-date-picker v-model="form.scheduleDate" type="date" value-format="yyyy-MM-dd" style="width:100%" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker v-model="form.startTime" value-format="HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker v-model="form.endTime" value-format="HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="场地">
          <el-select v-model="form.venueId" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="v in venueList" :key="v.id" :label="v.name" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="裁判员">
          <el-select v-model="form.refereeId" placeholder="请选择" clearable filterable style="width:100%">
            <el-option v-for="r in refereeList" :key="r.id" :label="r.realName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 自动生成对话框 -->
    <el-dialog title="自动生成赛程" :visible.sync="autoVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="运动会">
          <el-select v-model="autoMeetingId" placeholder="请选择" style="width:100%">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="autoVisible = false">取消</el-button>
        <el-button type="primary" :loading="autoLoading" @click="submitAutoGenerate">生成</el-button>
      </div>
    </el-dialog>

    <!-- 运动员查看/分配对话框 -->
    <el-dialog title="赛程运动员" :visible.sync="athleteVisible" width="600px">
      <el-table :data="athletes" stripe size="small">
        <el-table-column prop="laneNumber" label="道次" width="60" align="center" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="className" label="班级" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getSchedulePage, addSchedule, updateSchedule, deleteSchedule, updateScheduleStatus, autoGenerate, getScheduleAthletes } from '@/api/schedule'
import { getMeetingList } from '@/api/meeting'
import { getEventsByMeeting } from '@/api/event'
import { getVenueList } from '@/api/venue'
import { getUserPage } from '@/api/user'

export default {
  name: 'ScheduleList',
  data() {
    return {
      loading: false, tableData: [], total: 0,
      meetingList: [], eventList: [], venueList: [], refereeList: [],
      queryParams: { pageNum: 1, pageSize: 10, meetingId: null, status: null },
      dialogVisible: false, dialogTitle: '', form: {}, submitLoading: false,
      formRules: {
        meetingId: [{ required: true, message: '请选择运动会', trigger: 'change' }],
        eventId: [{ required: true, message: '请选择项目', trigger: 'change' }],
        scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      },
      autoVisible: false, autoMeetingId: null, autoLoading: false,
      athleteVisible: false, athletes: []
    }
  },
  computed: { role() { return this.$store.getters.role } },
  created() { this.loadData(); this.loadMeetings(); this.loadVenues(); this.loadReferees() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getSchedulePage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() { const res = await getMeetingList(); this.meetingList = res.data || [] },
    async loadVenues() { try { const res = await getVenueList(); this.venueList = res.data || [] } catch(e) {} },
    async loadReferees() { try { const res = await getUserPage({ pageNum: 1, pageSize: 100, role: 1 }); this.refereeList = res.data.records || [] } catch(e) {} },
    async loadEvents() {
      if (!this.form.meetingId) { this.eventList = []; return }
      const res = await getEventsByMeeting(this.form.meetingId); this.eventList = res.data || []
    },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, meetingId: null, status: null }; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增赛程'
      this.form = { meetingId: null, eventId: null, roundName: '决赛', groupNo: 1, scheduleDate: '', startTime: '', endTime: '', venueId: null, refereeId: null, remark: '' }
      this.eventList = []
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '编辑赛程'; this.form = { ...row }
      if (row.meetingId) this.loadEvents()
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) { await updateSchedule(this.form); this.$message.success('修改成功') }
          else { await addSchedule(this.form); this.$message.success('新增成功') }
          this.dialogVisible = false; this.loadData()
        } finally { this.submitLoading = false }
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除此赛程？', '提示', { type: 'warning' }).then(async () => {
        await deleteSchedule(row.id); this.$message.success('删除成功'); this.loadData()
      })
    },
    async handleStatusChange(row, status) {
      await updateScheduleStatus(row.id, status); this.$message.success('状态变更成功'); this.loadData()
    },
    handleAutoGenerate() { this.autoMeetingId = null; this.autoVisible = true },
    async submitAutoGenerate() {
      if (!this.autoMeetingId) { this.$message.warning('请选择运动会'); return }
      this.autoLoading = true
      try {
        await autoGenerate(this.autoMeetingId)
        this.$message.success('自动生成成功'); this.autoVisible = false; this.loadData()
      } finally { this.autoLoading = false }
    },
    async handleViewAthletes(row) {
      const res = await getScheduleAthletes(row.id)
      this.athletes = res.data || []; this.athleteVisible = true
    }
  }
}
</script>
