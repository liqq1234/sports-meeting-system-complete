<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="标题">
          <el-input v-model="queryParams.title" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable>
            <el-option label="赛事通知" :value="0" />
            <el-option label="规则公告" :value="1" />
            <el-option label="成绩公告" :value="2" />
            <el-option label="其他" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleAdd" v-if="role === 0">发布通知</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column label="置顶" width="60" align="center">
          <template slot-scope="{ row }">
            <i v-if="row.isTop === 1" class="el-icon-top" style="color:#E6A23C;font-size:18px"></i>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <el-link type="primary" @click="handleView(row)">{{ row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="类型" width="100" align="center" />
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="meetingName" label="所属运动会" width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" align="center" fixed="right" v-if="role === 0">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="text" style="color:#67C23A" @click="handlePublish(row)" v-if="row.status === 0">发布</el-button>
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

    <!-- 查看通知详情 -->
    <el-dialog :title="viewNotice.title" :visible.sync="viewVisible" width="700px">
      <div class="notice-meta">
        <span>发布人: {{ viewNotice.publisherName }}</span>
        <span style="margin-left:16px">类型: {{ viewNotice.typeName }}</span>
        <span style="margin-left:16px">时间: {{ viewNotice.createTime }}</span>
      </div>
      <el-divider />
      <div class="notice-content" v-html="viewNotice.content"></div>
    </el-dialog>

    <!-- 新增/编辑通知 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择" style="width:100%">
            <el-option label="赛事通知" :value="0" />
            <el-option label="规则公告" :value="1" />
            <el-option label="成绩公告" :value="2" />
            <el-option label="其他" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属运动会">
          <el-select v-model="form.meetingId" placeholder="请选择(可选)" clearable style="width:100%">
            <el-option v-for="m in meetingList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标角色">
          <el-select v-model="form.targetRole" placeholder="全部(可选)" clearable style="width:100%">
            <el-option label="管理员" :value="0" />
            <el-option label="裁判员" :value="1" />
            <el-option label="运动员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入通知内容" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getNoticePage, getNoticeById, addNotice, updateNotice, deleteNotice, publishNotice } from '@/api/notice'
import { getMeetingList } from '@/api/meeting'

export default {
  name: 'NoticeList',
  data() {
    return {
      loading: false, tableData: [], total: 0, meetingList: [],
      queryParams: { pageNum: 1, pageSize: 10, title: '', type: null },
      viewVisible: false, viewNotice: {},
      dialogVisible: false, dialogTitle: '', form: {}, submitLoading: false,
      formRules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  computed: { role() { return this.$store.getters.role } },
  created() { this.loadData(); this.loadMeetings() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getNoticePage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMeetings() { const res = await getMeetingList(); this.meetingList = res.data || [] },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, title: '', type: null }; this.loadData() },
    async handleView(row) {
      const res = await getNoticeById(row.id)
      this.viewNotice = res.data || row; this.viewVisible = true
    },
    handleAdd() {
      this.dialogTitle = '发布通知'
      this.form = { title: '', type: 0, meetingId: null, targetRole: null, isTop: 0, content: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) { this.dialogTitle = '编辑通知'; this.form = { ...row }; this.dialogVisible = true },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) { await updateNotice(this.form); this.$message.success('保存成功') }
          else { await addNotice(this.form); this.$message.success('创建成功') }
          this.dialogVisible = false; this.loadData()
        } finally { this.submitLoading = false }
      })
    },
    handlePublish(row) {
      this.$confirm('确定发布此通知？', '提示', { type: 'warning' }).then(async () => {
        await publishNotice(row.id); this.$message.success('发布成功'); this.loadData()
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除通知「${row.title}」？`, '提示', { type: 'warning' }).then(async () => {
        await deleteNotice(row.id); this.$message.success('删除成功'); this.loadData()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.notice-meta { color: #999; font-size: 13px; }
.notice-content { line-height: 1.8; color: #333; white-space: pre-wrap; }
</style>
