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
          <el-input v-model="queryParams.eventName" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="待录入" :value="0" />
            <el-option label="已录入" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已公布" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="athleteName" label="运动员" width="100" />
        <el-table-column prop="athleteCollege" label="学院" width="120" show-overflow-tooltip />
        <el-table-column prop="eventName" label="比赛项目" width="140" />
        <el-table-column prop="meetingName" label="运动会" min-width="180" show-overflow-tooltip />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.score != null">{{ row.score }} {{ row.scoreUnit || '' }}</span>
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="ranking" label="名次" width="70" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.ranking === 1" type="warning" size="mini" effect="dark">🥇</el-tag>
            <el-tag v-else-if="row.ranking === 2" size="mini">🥈</el-tag>
            <el-tag v-else-if="row.ranking === 3" type="danger" size="mini">🥉</el-tag>
            <span v-else-if="row.ranking">{{ row.ranking }}</span>
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
        <el-table-column label="操作" width="240" align="center" fixed="right" v-if="role === 0 || role === 1">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleRecord(row)">录入</el-button>
            <el-button size="mini" type="text" @click="handleConfirm(row)" v-if="row.status === 1">确认</el-button>
            <el-button size="mini" type="text" @click="handlePublish(row)" v-if="role === 0">公布</el-button>
            <el-button size="mini" type="text" @click="handleRanking(row)">排名</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper"
          :total="total" :page-size.sync="queryParams.pageSize" :current-page.sync="queryParams.pageNum"
          :page-sizes="[10, 20, 50]" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <!-- 成绩录入对话框 -->
    <el-dialog title="成绩录入" :visible.sync="recordVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="运动员">{{ currentScore.athleteName }}</el-form-item>
        <el-form-item label="项目">{{ currentScore.eventName }}</el-form-item>
        <el-form-item label="成绩">
          <el-input v-model="scoreValue" placeholder="请输入成绩">
            <template slot="append">{{ currentScore.scoreUnit || '' }}</template>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="recordVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecord">保存</el-button>
      </div>
    </el-dialog>

    <!-- 项目排名对话框 -->
    <el-dialog title="项目排名" :visible.sync="rankingVisible" width="700px">
      <el-table :data="eventScores" stripe size="small">
        <el-table-column prop="ranking" label="名次" width="70" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.ranking === 1" type="warning" size="mini" effect="dark">🥇 第1名</el-tag>
            <el-tag v-else-if="row.ranking === 2" size="mini">🥈 第2名</el-tag>
            <el-tag v-else-if="row.ranking === 3" type="danger" size="mini">🥉 第3名</el-tag>
            <span v-else-if="row.ranking">第{{ row.ranking }}名</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="athleteName" label="运动员" width="100" />
        <el-table-column prop="athleteCollege" label="学院" width="120" />
        <el-table-column prop="score" label="成绩" width="100" align="center" />
        <el-table-column prop="points" label="积分" width="70" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'info',1:'',2:'warning',3:'success'}[row.status]" size="small">
              {{ {0:'待录入',1:'已录入',2:'已确认',3:'已公布'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getScorePage, recordScore, confirmScore, publishScore, getEventScores, calculateRanking } from '@/api/score'
import { getMeetingList } from '@/api/meeting'

export default {
  name: 'ScoreList',
  data() {
    return {
      loading: false, tableData: [], total: 0, meetingList: [],
      queryParams: { pageNum: 1, pageSize: 10, meetingId: null, eventName: '', status: null },
      recordVisible: false, currentScore: {}, scoreValue: '',
      rankingVisible: false, eventScores: []
    }
  },
  computed: { role() { return this.$store.getters.role } },
  created() { this.loadData(); this.loadMeetings() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getScorePage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() { const res = await getMeetingList(); this.meetingList = res.data || [] },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, meetingId: null, eventName: '', status: null }; this.loadData() },
    handleRecord(row) {
      this.currentScore = row; this.scoreValue = row.score != null ? String(row.score) : ''
      this.recordVisible = true
    },
    async submitRecord() {
      if (!this.scoreValue && this.scoreValue !== '0') { this.$message.warning('请输入成绩'); return }
      await recordScore({ id: this.currentScore.id, athleteId: this.currentScore.athleteId, eventId: this.currentScore.eventId, score: parseFloat(this.scoreValue) })
      this.$message.success('录入成功'); this.recordVisible = false; this.loadData()
    },
    async handleConfirm(row) {
      this.$confirm('确定确认该成绩？', '提示', { type: 'warning' }).then(async () => {
        await confirmScore(row.id); this.$message.success('确认成功'); this.loadData()
      })
    },
    async handlePublish(row) {
      this.$confirm('确定公布该项目所有成绩？公布后将自动计算排名和积分。', '提示', { type: 'warning' }).then(async () => {
        await publishScore(row.eventId); this.$message.success('公布成功'); this.loadData()
      })
    },
    async handleRanking(row) {
      const res = await getEventScores(row.eventId)
      this.eventScores = res.data || []; this.rankingVisible = true
    }
  }
}
</script>
