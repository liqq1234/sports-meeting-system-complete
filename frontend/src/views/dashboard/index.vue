<template>
  <div class="page-container">
    <!-- 系统概览卡片 -->
    <el-row :gutter="16" class="overview-cards">
      <el-col :span="6" v-for="(item, index) in overviewList" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" :style="{ background: item.color }"><i :class="item.icon"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ item.value || 0 }}</p>
              <p class="stat-label">{{ item.label }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 运动会选择 -->
    <el-card shadow="never" class="meeting-selector">
      <el-select v-model="selectedMeeting" placeholder="选择运动会查看详细数据" @change="loadDashboard" style="width: 360px">
        <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
    </el-card>

    <!-- 运动会详细数据 -->
    <template v-if="selectedMeeting">
      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="6" v-for="(item, index) in detailStats" :key="index">
          <el-card shadow="hover" class="stat-card mini">
            <div class="stat-mini"><span class="num">{{ item.value || 0 }}</span><span class="txt">{{ item.label }}</span></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>各项目报名统计</b></div>
            <e-chart :options="regOptions" height="350px" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>项目分类统计</b></div>
            <e-chart :options="eventOptions" height="350px" />
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>学院报名统计</b></div>
            <e-chart :options="collegeOptions" height="350px" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>参赛运动员性别分布</b></div>
            <e-chart :options="genderOptions" height="350px" />
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="24">
          <el-card shadow="never">
            <div slot="header"><b>学院积分排名</b></div>
            <e-chart :options="rankOptions" height="400px" />
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never" style="margin-top: 16px">
        <div slot="header"><b>优秀运动员 TOP10</b></div>
        <el-table :data="dashboard.topAthletes || []" stripe size="small">
          <el-table-column type="index" label="排名" width="60" />
          <el-table-column prop="real_name" label="姓名" />
          <el-table-column prop="college" label="学院" />
          <el-table-column prop="class_name" label="班级" />
          <el-table-column prop="total_points" label="总积分" width="80" />
          <el-table-column prop="event_count" label="参赛项目" width="80" />
          <el-table-column prop="gold" label="金牌" width="60" />
          <el-table-column prop="silver" label="银牌" width="60" />
          <el-table-column prop="bronze" label="铜牌" width="60" />
        </el-table>
      </el-card>
    </template>
  </div>
</template>

<script>
import { getOverview, getDashboard } from '@/api/statistics'
import { getMeetingList } from '@/api/meeting'
import EChart from '@/components/Charts/EChart.vue'

export default {
  name: 'Dashboard',
  components: { EChart },
  data() {
    return {
      overview: {},
      meetingList: [],
      selectedMeeting: null,
      dashboard: {}
    }
  },
  computed: {
    overviewList() {
      return [
        { label: '用户总数', value: this.overview.totalUsers, icon: 'el-icon-user', color: '#409EFF' },
        { label: '运动会总数', value: this.overview.meetingCount, icon: 'el-icon-trophy', color: '#67C23A' },
        { label: '运动员数', value: this.overview.athleteCount, icon: 'el-icon-s-custom', color: '#E6A23C' },
        { label: '报名总数', value: this.overview.totalRegistrations, icon: 'el-icon-document-checked', color: '#F56C6C' }
      ]
    },
    detailStats() {
      return [
        { label: '比赛项目', value: this.dashboard.eventCount },
        { label: '报名人次', value: this.dashboard.registrationTotal },
        { label: '已通过审核', value: this.dashboard.registrationApproved },
        { label: '已公布成绩', value: this.dashboard.scorePublished }
      ]
    },
    regOptions() {
      const data = this.dashboard.registrationStats || []
      return {
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map(d => d.event_name), axisLabel: { rotate: 30, fontSize: 11 } },
        yAxis: { type: 'value', name: '人数' },
        series: [
          { name: '待审核', type: 'bar', stack: 'total', data: data.map(d => d.pending || 0), color: '#E6A23C' },
          { name: '已通过', type: 'bar', stack: 'total', data: data.map(d => d.approved || 0), color: '#67C23A' },
          { name: '已驳回', type: 'bar', stack: 'total', data: data.map(d => d.rejected || 0), color: '#F56C6C' }
        ],
        legend: { bottom: 0 },
        grid: { bottom: 60, left: 50, right: 20 }
      }
    },
    eventOptions() {
      const data = this.dashboard.eventStats || []
      return {
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie', radius: ['40%', '65%'],
          data: data.map(d => ({ name: d.category, value: d.event_count })),
          label: { formatter: '{b}: {c} ({d}%)' }
        }]
      }
    },
    collegeOptions() {
      const data = this.dashboard.collegeRegistrationStats || []
      return {
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map(d => d.college), axisLabel: { rotate: 30, fontSize: 11 } },
        yAxis: { type: 'value', name: '人数' },
        series: [
          { name: '运动员数', type: 'bar', data: data.map(d => d.athlete_count || 0), color: '#409EFF' },
          { name: '报名项目数', type: 'bar', data: data.map(d => d.event_count || 0), color: '#67C23A' }
        ],
        legend: { bottom: 0 },
        grid: { bottom: 60, left: 50, right: 20 }
      }
    },
    genderOptions() {
      const gender = this.dashboard.genderDistribution || {}
      return {
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie', radius: ['40%', '65%'],
          data: [
            { name: '男', value: gender.male || 0, itemStyle: { color: '#409EFF' } },
            { name: '女', value: gender.female || 0, itemStyle: { color: '#F56C6C' } }
          ],
          label: { formatter: '{b}: {c}人 ({d}%)' }
        }]
      }
    },
    rankOptions() {
      const data = [...(this.dashboard.collegeRanking || [])].reverse()
      return {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: 120, right: 40, bottom: 20, top: 20 },
        xAxis: { type: 'value', name: '积分' },
        yAxis: { type: 'category', data: data.map(d => d.college), axisLabel: { fontSize: 12 } },
        series: [{
          name: '总积分', type: 'bar', data: data.map(d => d.total_points || 0),
          itemStyle: {
            color: (params) => ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de'][params.dataIndex % 5]
          },
          label: { show: true, position: 'right' }
        }]
      }
    }
  },
  created() {
    this.loadOverview()
    this.loadMeetings()
  },
  methods: {
    async loadOverview() {
      const res = await getOverview()
      this.overview = res.data || {}
    },
    async loadMeetings() {
      const res = await getMeetingList()
      this.meetingList = res.data || []
      if (this.meetingList.length > 0 && !this.selectedMeeting) {
        this.selectedMeeting = this.meetingList[0].id
        this.loadDashboard()
      }
    },
    async loadDashboard() {
      if (!this.selectedMeeting) return
      const res = await getDashboard(this.selectedMeeting)
      this.dashboard = res.data || {}
    }
  }
}
</script>

<style lang="scss" scoped>
.overview-cards { margin-bottom: 16px; }
.stat-card {
  .stat-item {
    display: flex;
    align-items: center;
    .stat-icon {
      width: 56px; height: 56px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
      i { font-size: 28px; color: #fff; }
    }
    .stat-info {
      margin-left: 16px;
      .stat-value { font-size: 28px; font-weight: bold; color: #333; line-height: 1; }
      .stat-label { font-size: 13px; color: #999; margin-top: 4px; }
    }
  }
  &.mini .stat-mini {
    text-align: center;
    .num { display: block; font-size: 32px; font-weight: bold; color: #409EFF; }
    .txt { font-size: 13px; color: #999; }
  }
}
.meeting-selector { margin-bottom: 0; }
</style>
