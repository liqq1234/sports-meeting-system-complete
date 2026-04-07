<template>
  <div class="page-container">
    <!-- 系统概览卡片 -->
    <el-row :gutter="16" class="overview-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #409EFF"><i class="el-icon-user"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ overview.totalUsers || 0 }}</p>
              <p class="stat-label">用户总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #67C23A"><i class="el-icon-trophy"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ overview.meetingCount || 0 }}</p>
              <p class="stat-label">运动会总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #E6A23C"><i class="el-icon-s-custom"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ overview.athleteCount || 0 }}</p>
              <p class="stat-label">运动员数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #F56C6C"><i class="el-icon-document-checked"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ overview.totalRegistrations || 0 }}</p>
              <p class="stat-label">报名总数</p>
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
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card mini">
            <div class="stat-mini"><span class="num">{{ dashboard.eventCount || 0 }}</span><span class="txt">比赛项目</span></div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card mini">
            <div class="stat-mini"><span class="num">{{ dashboard.registrationTotal || 0 }}</span><span class="txt">报名人次</span></div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card mini">
            <div class="stat-mini"><span class="num">{{ dashboard.registrationApproved || 0 }}</span><span class="txt">已通过审核</span></div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card mini">
            <div class="stat-mini"><span class="num">{{ dashboard.scorePublished || 0 }}</span><span class="txt">已公布成绩</span></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>各项目报名统计</b></div>
            <div ref="regChart" style="height: 350px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>项目分类统计</b></div>
            <div ref="eventChart" style="height: 350px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>学院报名统计</b></div>
            <div ref="collegeChart" style="height: 350px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>参赛运动员性别分布</b></div>
            <div ref="genderChart" style="height: 350px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="24">
          <el-card shadow="never">
            <div slot="header"><b>学院积分排名（ECharts 大屏）</b></div>
            <div ref="rankChart" style="height: 350px"></div>
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
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      overview: {},
      meetingList: [],
      selectedMeeting: null,
      dashboard: {}
    }
  },
  created() {
    this.loadOverview()
    this.loadMeetings()
  },
  mounted() {
    this._resizeHandler = () => {
      this._charts && this._charts.forEach(c => c && c.resize())
    }
    window.addEventListener('resize', this._resizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this._resizeHandler)
    this._charts && this._charts.forEach(c => c && c.dispose())
  },
  methods: {
    async loadOverview() {
      const res = await getOverview()
      this.overview = res.data || {}
    },
    async loadMeetings() {
      const res = await getMeetingList()
      this.meetingList = res.data || []
      if (this.meetingList.length > 0) {
        this.selectedMeeting = this.meetingList[0].id
        this.loadDashboard()
      }
    },
    async loadDashboard() {
      if (!this.selectedMeeting) return
      const res = await getDashboard(this.selectedMeeting)
      this.dashboard = res.data || {}
      this.$nextTick(() => {
        this.renderRegChart()
        this.renderEventChart()
        this.renderCollegeChart()
        this.renderGenderChart()
        this.renderRankChart()
      })
    },
    renderRegChart() {
      if (!this.$refs.regChart) return
      const chart = echarts.init(this.$refs.regChart)
      const data = this.dashboard.registrationStats || []
      chart.setOption({
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
      })
    },
    renderEventChart() {
      if (!this.$refs.eventChart) return
      const chart = echarts.init(this.$refs.eventChart)
      const data = this.dashboard.eventStats || []
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['40%', '65%'],
          data: data.map(d => ({ name: d.category, value: d.event_count })),
          label: { formatter: '{b}: {c}个 ({d}%)' }
        }]
      })
    },
    renderCollegeChart() {
      if (!this.$refs.collegeChart) return
      const chart = echarts.init(this.$refs.collegeChart)
      const data = this.dashboard.collegeRegistrationStats || []
      chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map(d => d.college), axisLabel: { rotate: 30, fontSize: 11 } },
        yAxis: { type: 'value', name: '人数' },
        series: [
          { name: '运动员数', type: 'bar', data: data.map(d => d.athlete_count || 0), color: '#409EFF' },
          { name: '报名项目数', type: 'bar', data: data.map(d => d.event_count || 0), color: '#67C23A' }
        ],
        legend: { bottom: 0 },
        grid: { bottom: 60, left: 50, right: 20 }
      })
      this._charts = this._charts || []
      this._charts.push(chart)
    },
    renderGenderChart() {
      if (!this.$refs.genderChart) return
      const chart = echarts.init(this.$refs.genderChart)
      const gender = this.dashboard.genderDistribution || {}
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['40%', '65%'],
          data: [
            { name: '男', value: gender.male || 0, itemStyle: { color: '#409EFF' } },
            { name: '女', value: gender.female || 0, itemStyle: { color: '#F56C6C' } }
          ],
          label: { formatter: '{b}: {c}人 ({d}%)' },
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' } }
        }]
      })
      this._charts = this._charts || []
      this._charts.push(chart)
    },
    renderRankChart() {
      if (!this.$refs.rankChart) return
      const chart = echarts.init(this.$refs.rankChart)
      const data = [...(this.dashboard.collegeRanking || [])].reverse()
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: 120, right: 20, bottom: 20, top: 20 },
        xAxis: { type: 'value', name: '积分' },
        yAxis: { type: 'category', data: data.map(d => d.college), axisLabel: { fontSize: 12 } },
        series: [{
          name: '总积分',
          type: 'bar',
          data: data.map(d => d.total_points || 0),
          itemStyle: {
            color: function(params) {
              const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
              return colors[params.dataIndex % colors.length]
            }
          },
          label: { show: true, position: 'right' }
        }]
      })
      this._charts = this._charts || []
      this._charts.push(chart)
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
