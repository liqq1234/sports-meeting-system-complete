<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="场地名称">
          <el-input v-model="queryParams.name" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="可用" :value="0" />
            <el-option label="维护中" :value="1" />
            <el-option label="不可用" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleAdd">新增场地</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="name" label="场地名称" width="150" />
        <el-table-column prop="location" label="位置" width="200" show-overflow-tooltip />
        <el-table-column prop="capacity" label="容纳人数" width="100" align="center" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'success',1:'warning',2:'danger'}[row.status]" size="small">
              {{ {0:'可用',1:'维护中',2:'不可用'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="场地名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="form.capacity" :min="0" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="form.type" placeholder="如：田径场/篮球馆" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="可用" :value="0" />
            <el-option label="维护中" :value="1" />
            <el-option label="不可用" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getVenuePage, addVenue, updateVenue, deleteVenue } from '@/api/venue'

export default {
  name: 'VenueList',
  data() {
    return {
      loading: false, tableData: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, name: '', status: null },
      dialogVisible: false, dialogTitle: '', form: {}, submitLoading: false,
      formRules: { name: [{ required: true, message: '请输入场地名称', trigger: 'blur' }] }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getVenuePage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, name: '', status: null }; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增场地'
      this.form = { name: '', location: '', capacity: 0, type: '', status: 0, description: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) { this.dialogTitle = '编辑场地'; this.form = { ...row }; this.dialogVisible = true },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) { await updateVenue(this.form); this.$message.success('修改成功') }
          else { await addVenue(this.form); this.$message.success('新增成功') }
          this.dialogVisible = false; this.loadData()
        } finally { this.submitLoading = false }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除场地「${row.name}」？`, '提示', { type: 'warning' }).then(async () => {
        await deleteVenue(row.id); this.$message.success('删除成功'); this.loadData()
      })
    }
  }
}
</script>
