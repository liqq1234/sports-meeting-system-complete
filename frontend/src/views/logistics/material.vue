<template>
  <div class="app-container">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>物资库存管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleAdd">新增物资</el-button>
      </div>
      
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="物资名称">
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="materialList" border stripe>
        <el-table-column label="ID" align="center" prop="id" width="80" />
        <el-table-column label="名称" align="center" prop="name" />
        <el-table-column label="规格" align="center" prop="spec" />
        <el-table-column label="单位" align="center" prop="unit" width="80" />
        <el-table-column label="库存" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.stock <= scope.row.threshold ? 'danger' : 'success'">
              {{ scope.row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阈值" align="center" prop="threshold" />
        <el-table-column label="更新时间" align="center" prop="updateTime" width="160" />
        <el-table-column label="操作" align="center" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
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
    <el-dialog :title="dialogTitle" :visible.sync="open" width="500px">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="form.spec" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位 (如: 箱, 件)" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="预警阈值" prop="threshold">
          <el-input-number v-model="form.threshold" :min="0" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMaterialPage, addMaterial, updateMaterial, deleteMaterial } from '@/api/logistics'

export default {
  name: 'MaterialList',
  data() {
    return {
      loading: true,
      materialList: [],
      total: 0,
      queryParams: {
        current: 1,
        size: 10,
        name: undefined
      },
      open: false,
      dialogTitle: '',
      form: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getMaterialPage(this.queryParams).then(res => {
        this.materialList = res.data.records
        this.total = res.data.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.name = ''
      this.handleQuery()
    },
    handleAdd() {
      this.form = { stock: 0, threshold: 10 }
      this.dialogTitle = '新增物资'
      this.open = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑物资'
      this.open = true
    },
    submitForm() {
      if (this.form.id) {
        updateMaterial(this.form).then(() => {
          this.$message.success('更新成功')
          this.open = false
          this.getList()
        })
      } else {
        addMaterial(this.form).then(() => {
          this.$message.success('添加成功')
          this.open = false
          this.getList()
        })
      }
    },
    handleDelete(row) {
      this.$confirm(`确认删除物资 "${row.name}" 吗？`, '警告', { type: 'warning' }).then(() => {
        deleteMaterial(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    }
  }
}
</script>
