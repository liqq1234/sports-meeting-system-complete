<template>
  <div class="app-container">
    <!-- 统计面板 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="always" class="stats-card">
          <div class="stats-title">累计就诊 (人次)</div>
          <div class="stats-num">{{ analytics.totalRecords }}</div>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card shadow="always">
          <div slot="header">处置比例分布</div>
          <el-row style="text-align: center;">
            <el-col :span="8">
              <el-progress type="circle" :percentage="calcPercent('return')" status="success" />
              <div class="progress-label">返回比赛 ({{ analytics.dispositionStats?.return }})</div>
            </el-col>
            <el-col :span="8">
              <el-progress type="circle" :percentage="calcPercent('observe')" status="warning" />
              <div class="progress-label">留院观察 ({{ analytics.dispositionStats?.observe }})</div>
            </el-col>
            <el-col :span="8">
              <el-progress type="circle" :percentage="calcPercent('transfer')" status="exception" />
              <div class="progress-label">转院处理 ({{ analytics.dispositionStats?.transfer }})</div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 记录列表 -->
    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>就诊记录详情</span>
        <el-button style="float: right; padding: 3px 0" type="primary" @click="handleAdd">就诊登记</el-button>
      </div>

      <el-table v-loading="loading" :data="recordList" stripe border>
        <el-table-column label="就诊人ID" prop="patientId" width="100" />
        <el-table-column label="就诊时间" align="center" prop="visitTime" width="160" />
        <el-table-column label="主诉症状" prop="symptoms" show-overflow-tooltip />
        <el-table-column label="诊断结果" prop="diagnosis" show-overflow-tooltip />
        <el-table-column label="处置措施" align="center">
          <template slot-scope="scope">
            <el-tag :type="disMap[scope.row.disposition].type">
              {{ disMap[scope.row.disposition].label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接诊医生" prop="doctor" width="100" />
        <el-table-column label="详细记录" prop="treatment" show-overflow-tooltip />
      </el-table>

      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="getList"
      />
    </el-card>

    <!-- 弹窗 -->
    <el-dialog title="就诊登记 (伤病记录)" :visible.sync="open" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属运动会" prop="meetingId">
          <el-select v-model="form.meetingId" placeholder="请选择运动会" style="width:100%" @change="handleMeetingChange">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊人" prop="patientId">
          <el-select
            v-model="form.patientId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入姓名或学号搜索"
            :remote-method="searchUsers"
            :loading="userLoading"
            style="width:100%"
            @change="handleUserChange">
            <el-option
              v-for="item in userOptions"
              :key="item.id"
              :label="item.realName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关联项目" prop="eventId">
          <el-select v-model="form.eventId" placeholder="请选择该运动员参加的项目" style="width:100%" :disabled="!form.patientId">
            <el-option v-for="e in athleteEvents" :key="e.eventId" :label="e.eventName" :value="e.eventId" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="体温 (℃)" prop="temperature">
              <el-input-number v-model="form.temperature" :precision="1" :step="0.1" :max="45" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="心率 (bpm)" prop="heartRate">
              <el-input-number v-model="form.heartRate" :min="30" :max="250" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主诉症状" prop="symptoms">
          <el-input v-model="form.symptoms" placeholder="如：左踝扭伤, 中暑..." />
        </el-form-item>
        <el-form-item label="临床诊断" prop="diagnosis">
          <el-input v-model="form.diagnosis" placeholder="临床判断结果" />
        </el-form-item>
        <el-form-item label="处置结果" prop="disposition">
          <el-radio-group v-model="form.disposition">
            <el-radio :label="1">留院观察</el-radio>
            <el-radio :label="2">转院处理</el-radio>
            <el-radio :label="0">普通就诊</el-radio>
          </el-radio-group>
          <div v-if="form.disposition > 0" style="color: #F56C6C; font-size: 12px; margin-top: 5px;">
            提示：该处置将导致运动员自动退出关联比赛项目
          </div>
        </el-form-item>
        <el-form-item label="治疗细节" prop="treatment">
          <el-input v-model="form.treatment" type="textarea" placeholder="用药、冰敷、包扎等内容" />
        </el-form-item>
        <el-form-item label="接诊医生" prop="doctor">
          <el-input v-model="form.doctor" placeholder="接诊医生姓名" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认登记</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMedicalRecords, addMedicalRecord, getMedicalAnalytics } from '@/api/medical'
import { getMeetingList } from '@/api/meeting'
import { getUserPage } from '@/api/user'
import { getRegistrationPage, withdrawForMedical } from '@/api/registration'

export default {
  name: 'MedicalRecords',
  data() {
    return {
      loading: true,
      userLoading: false,
      open: false,
      recordList: [],
      meetingList: [],
      userOptions: [],
      athleteEvents: [],
      total: 0,
      analytics: {
        totalRecords: 0,
        dispositionStats: { return: 0, observe: 0, transfer: 0 }
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        meetingId: null
      },
      form: {},
      rules: {
        meetingId: [{ required: true, message: '请选择运动会', trigger: 'change' }],
        patientId: [{ required: true, message: '请选择就诊人', trigger: 'change' }],
        eventId: [{ required: true, message: '请选择关联项目', trigger: 'change' }],
        symptoms: [{ required: true, message: '请输入主诉症状', trigger: 'blur' }],
        diagnosis: [{ required: true, message: '请输入诊断结果', trigger: 'blur' }],
        doctor: [{ required: true, message: '请输入医生姓名', trigger: 'blur' }]
      },
      disMap: {
        0: { label: '普通就诊', type: 'success' },
        1: { label: '留院观察', type: 'warning' },
        2: { label: '转院', type: 'danger' }
      }
    }
  },
  created() {
    this.loadMeetings()
  },
  methods: {
    loadMeetings() {
      getMeetingList().then(res => {
        this.meetingList = res.data
        if (this.meetingList.length > 0) {
          this.queryParams.meetingId = this.meetingList[0].id
          this.getList()
          this.loadAnalytics()
        }
      })
    },
    getList() {
      this.loading = true
      console.log('--- [DEBUG] 正在拉取列表，参数:', this.queryParams)
      getMedicalRecords(this.queryParams).then(res => {
        console.log('--- [DEBUG] 列表响应:', res.data)
        this.recordList = res.data.records || []
        this.total = Number(res.data.total) || 0
        this.loading = false
      }).catch(err => {
        console.error('拉取失败:', err)
        this.loading = false
      })
    },
    loadAnalytics() {
      getMedicalAnalytics({ meetingId: this.queryParams.meetingId }).then(res => {
        this.analytics = res.data
      })
    },
    handleMeetingChange(val) {
      this.queryParams.meetingId = val
      this.getList()
      this.loadAnalytics()
    },
    handleAdd() {
      this.form = { 
        meetingId: this.queryParams.meetingId, 
        disposition: 0, 
        visitTime: new Date(),
        temperature: 36.5,
        heartRate: 75
      }
      this.athleteEvents = []
      this.userOptions = []
      this.open = true
    },
    async searchUsers(query) {
      if (query !== '') {
        this.userLoading = true
        try {
          const res = await getUserPage({ keyword: query, role: 2 })
          this.userOptions = res.data.records || []
        } finally {
          this.userLoading = false
        }
      }
    },
    handleUserChange(userId) {
      if (!userId) {
        this.athleteEvents = []
        return
      }
      console.log('--- [DEBUG] 开始拉取报名项目 ---')
      console.log('用户 ID:', userId, '类型:', typeof userId)
      
      getRegistrationPage({ userId }).then(res => {
        console.log('后端原始响应 Res:', res)
        const records = res.data.records || []
        console.log('解析后的记录列表 Records:', records)
        
        this.athleteEvents = records.map(r => {
          const id = r.eventId || r.event_id
          const name = r.eventName || r.event_name
          console.log(`处理单条记录 -> ID: ${id}, 名称: ${name}`)
          return {
            eventId: id,
            eventName: name ? name : `未知项目(ID:${id})`
          }
        })

        console.log('最终生成的下拉列表内容:', this.athleteEvents)

        if (this.athleteEvents.length > 0) {
          console.log(`成功找到 ${this.athleteEvents.length} 个关联项目，请手动选择。`)
        } else {
          this.form.eventId = null
          this.$notify({
            title: '未找到报名数据',
            message: `用户ID ${userId} 名下没有查到任何报名项目。`,
            type: 'warning'
          })
        }
      }).catch(err => {
        console.error('--- [DEBUG ERROR] 接口调用失败 ---', err)
        this.$message.error('拉取报名项目失败，请检查后端 Sports 服务是否正常')
      })
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            // 1. 保存就诊记录
            await addMedicalRecord(this.form)
            
            // 2. 如果伤势严重，自动办理退赛
            if (this.form.disposition > 0) {
              await withdrawForMedical({
                userId: this.form.patientId,
                eventId: this.form.eventId
              })
              this.$notify({
                title: '联动成功',
                message: '运动员已因伤自动退出该比赛项目',
                type: 'warning'
              })
            } else {
              this.$message.success('登记成功')
            }
            
            this.open = false
            this.getList()
            this.loadAnalytics()
          } catch (e) {
            console.error(e)
          }
        }
      })
    },
    calcPercent(key) {
      if (!this.analytics.totalRecords) return 0
      return Math.round((this.analytics.dispositionStats[key] / this.analytics.totalRecords) * 100)
    }
  }
}
</script>

<style scoped>
.stats-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  color: white;
}
.stats-title { font-size: 14px; opacity: 0.8; }
.stats-num { font-size: 36px; font-weight: bold; margin-top: 10px; }
.progress-label { margin-top: 10px; font-size: 12px; color: #666; }
</style>
