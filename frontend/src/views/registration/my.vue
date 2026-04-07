<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="运动会">
          <el-select v-model="queryParams.meetingId" placeholder="全部" clearable @change="handleQuery">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable @change="handleQuery">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
            <el-option label="已取消" :value="3" />
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
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'warning',1:'success',2:'danger',3:'info'}[row.status]" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="160" />
        <el-table-column prop="rejectReason" label="驳回原因" width="200" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" style="color:#F56C6C"
              @click="handleCancel(row)" v-if="row.status === 0 || row.status === 1">取消</el-button>
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
import { getMyRegistrations, cancelRegistration } from '@/api/registration'
import { getMeetingList } from '@/api/meeting'

export default {
  name: 'MyRegistration',
  data() {
    return {
      loading: false, tableData: [], total: 0, meetingList: [],
      queryParams: { pageNum: 1, pageSize: 10, meetingId: null, status: null }
    }
  },
  created() { this.loadData(); this.loadMeetings() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getMyRegistrations(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() { const res = await getMeetingList(); this.meetingList = res.data || [] },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    handleCancel(row) {
      this.$confirm('确定取消此报名？', '提示', { type: 'warning' }).then(async () => {
        await cancelRegistration(row.id); this.$message.success('取消成功'); this.loadData()
      })
    }
  }
}
</script>
