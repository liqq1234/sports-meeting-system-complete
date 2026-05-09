<template>
  <div class="page-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span><i class="el-icon-first-aid-kit" style="margin-right: 8px"></i>我的健康档案</span>
      </div>
      
      <el-timeline v-if="recordList.length > 0">
        <el-timeline-item
          v-for="(item, index) in recordList"
          :key="index"
          :timestamp="item.visitTime"
          placement="top"
          :type="item.disposition > 0 ? 'danger' : 'primary'"
        >
          <el-card shadow="hover">
            <el-row>
              <el-col :span="18">
                <h4 style="margin-top: 0">诊断结果：{{ item.diagnosis }}</h4>
                <p style="color: #666"><i class="el-icon-user"></i> 医生：{{ item.doctor }}</p>
                <p><strong>主诉：</strong>{{ item.symptoms }}</p>
                <p><strong>治疗方案：</strong>{{ item.treatment }}</p>
                <div v-if="item.temperature || item.heartRate" style="background: #f8f9fa; padding: 10px; border-radius: 4px; margin: 10px 0;">
                  <span style="margin-right: 20px">🌡️ 体温: <b :style="{color: item.temperature > 37.3 ? 'red' : 'green'}">{{ item.temperature }}℃</b></span>
                  <span>💓 心率: <b>{{ item.heartRate }} bpm</b></span>
                </div>
              </el-col>
              <el-col :span="6" style="text-align: right">
                <el-tag :type="getDispositionType(item.disposition)" effect="dark" size="medium">
                  {{ getDispositionName(item.disposition) }}
                </el-tag>
              </el-col>
            </el-row>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      
      <el-empty v-else description="暂无就诊记录，身体倍儿棒！" :image-size="200"></el-empty>
    </el-card>
  </div>
</template>

<script>
import { getMedicalRecords } from '@/api/medical'

export default {
  name: 'MyMedicalRecords',
  data() {
    return {
      recordList: [],
      loading: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      const userId = this.$store.getters.userId
      const params = {}
      if (userId) {
        params.patientId = userId
      }
      getMedicalRecords(params).then(res => {
        this.recordList = res.data.records || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getDispositionName(val) {
      const map = { 0: '普通就诊', 1: '留院观察', 2: '转院处理' }
      return map[val] || '未知状态'
    },
    getDispositionType(val) {
      const map = { 0: 'success', 1: 'warning', 2: 'danger' }
      return map[val] || 'info'
    }
  }
}
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}
.box-card {
  max-width: 900px;
  margin: 0 auto;
}
h4 {
  font-size: 18px;
  color: #303133;
}
p {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
}
</style>
