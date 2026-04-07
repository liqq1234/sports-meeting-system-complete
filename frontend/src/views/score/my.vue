<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="运动会">
          <el-select v-model="queryParams.meetingId" placeholder="全部" clearable @change="handleQuery">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="eventName" label="比赛项目" width="140" />
        <el-table-column prop="meetingName" label="运动会" min-width="200" show-overflow-tooltip />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.score != null">{{ row.score }} {{ row.scoreUnit || '' }}</span>
            <span v-else style="color:#999">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="ranking" label="名次" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.ranking === 1" type="warning" size="mini" effect="dark">🥇 第1名</el-tag>
            <el-tag v-else-if="row.ranking === 2" size="mini">🥈 第2名</el-tag>
            <el-tag v-else-if="row.ranking === 3" type="danger" size="mini">🥉 第3名</el-tag>
            <span v-else-if="row.ranking">第{{ row.ranking }}名</span>
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="70" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'info',1:'',2:'warning',3:'success'}[row.status]" size="small">
              {{ {0:'待录入',1:'已录入',2:'已确认',3:'已公布'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next"
          :total="total" :page-size.sync="queryParams.pageSize" :current-page.sync="queryParams.pageNum"
          @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script>
import { getMyScores } from '@/api/score'
import { getMeetingList } from '@/api/meeting'

export default {
  name: 'MyScore',
  data() {
    return {
      loading: false, tableData: [], total: 0, meetingList: [],
      queryParams: { pageNum: 1, pageSize: 10, meetingId: null }
    }
  },
  created() { this.loadData(); this.loadMeetings() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getMyScores(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() { const res = await getMeetingList(); this.meetingList = res.data || [] },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() }
  }
}
</script>
