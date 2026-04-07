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
        :current-page="queryParams.current"
        :page-size="queryParams.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="getList"
      />
    </el-card>

    <!-- 弹窗 -->
    <el-dialog title="就诊登记 (伤病记录)" :visible.sync="open" width="600px">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="所属运动会" prop="meetingId">
          <el-input v-model="form.meetingId" placeholder="运动会ID" />
        </el-form-item>
        <el-form-item label="就诊人ID" prop="patientId">
          <el-input v-model="form.patientId" placeholder="由参赛号或ID录入" />
        </el-form-item>
        <el-form-item label="主诉症状" prop="symptoms">
          <el-input v-model="form.symptoms" placeholder="如：左踝扭伤, 中暑..." />
        </el-form-item>
        <el-form-item label="诊断" prop="diagnosis">
          <el-input v-model="form.diagnosis" placeholder="临床判断" />
        </el-form-item>
        <el-form-item label="处置结果" prop="disposition">
          <el-radio-group v-model="form.disposition">
            <el-radio :label="0">返回比赛</el-radio>
            <el-radio :label="1">留院观察</el-radio>
            <el-radio :label="2">转院处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="治疗细节" prop="treatment">
          <el-input v-model="form.treatment" type="textarea" placeholder="用药、冰敷、包扎等内容" />
        </el-form-item>
        <el-form-item label="接诊医生" prop="doctor">
          <el-input v-model="form.doctor" placeholder="接诊人姓名" />
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

export default {
  name: 'MedicalRecords',
  data() {
    return {
      loading: true,
      open: false,
      recordList: [],
      total: 0,
      analytics: {
        totalRecords: 0,
        dispositionStats: { return: 0, observe: 0, transfer: 0 }
      },
      queryParams: {
        current: 1,
        size: 10,
        meetingId: 1
      },
      form: {},
      disMap: {
        0: { label: '返回比赛', type: 'success' },
        1: { label: '留院观察', type: 'warning' },
        2: { label: '转院', type: 'danger' }
      }
    }
  },
  created() {
    this.getList()
    this.loadAnalytics()
  },
  methods: {
    getList() {
      this.loading = true
      getMedicalRecords(this.queryParams).then(res => {
        this.recordList = res.data.records
        this.total = res.data.total
        this.loading = false
      })
    },
    loadAnalytics() {
      getMedicalAnalytics({ meetingId: this.queryParams.meetingId || 1 }).then(res => {
        this.analytics = res.data
      })
    },
    handleAdd() {
      this.form = { meetingId: 1, disposition: 0, visitTime: new Date() }
      this.open = true
    },
    submitForm() {
      addMedicalRecord(this.form).then(() => {
        this.$message.success('登记成功')
        this.open = false
        this.getList()
        this.loadAnalytics()
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
