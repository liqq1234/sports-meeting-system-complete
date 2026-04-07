<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="queryParams.isRead" placeholder="全部" clearable @change="handleQuery">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button type="success" @click="handleMarkAllRead">全部已读</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border :row-class-name="tableRowClassName">
        <el-table-column label="" width="40" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.isRead === 0" class="unread-dot"></span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="250">
          <template slot-scope="{ row }">
            <span :style="{ fontWeight: row.isRead === 0 ? 'bold' : 'normal' }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column label="类型" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="msgTypeTag(row.type)">{{ msgTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="130" align="center">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleMarkRead(row)" v-if="row.isRead === 0">标记已读</el-button>
            <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(row)">删除</el-button>
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
import { getMessagePage, markAsRead, markAllAsRead, deleteMessage } from '@/api/message'

export default {
  name: 'MessageList',
  data() {
    return {
      loading: false, tableData: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, isRead: null }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getMessagePage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    tableRowClassName({ row }) { return row.isRead === 0 ? 'unread-row' : '' },
    msgTypeName(type) {
      const m = { 0: '系统', 1: '报名', 2: '成绩', 3: '通知' }
      return m[type] || '其他'
    },
    msgTypeTag(type) {
      const m = { 0: 'info', 1: 'warning', 2: 'success', 3: '' }
      return m[type] || 'info'
    },
    async handleMarkRead(row) {
      await markAsRead(row.id); this.$message.success('已标记为已读'); this.loadData()
    },
    async handleMarkAllRead() {
      await markAllAsRead(); this.$message.success('已全部标记为已读'); this.loadData()
    },
    handleDelete(row) {
      this.$confirm('确定删除此消息？', '提示', { type: 'warning' }).then(async () => {
        await deleteMessage(row.id); this.$message.success('删除成功'); this.loadData()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.unread-dot {
  display: inline-block;
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #F56C6C;
}
::v-deep .unread-row { background-color: #fafafa; }
</style>
