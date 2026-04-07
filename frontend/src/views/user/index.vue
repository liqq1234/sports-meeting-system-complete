<template>
  <div class="page-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryParams.role" placeholder="全部" clearable>
            <el-option label="管理员" :value="0" />
            <el-option label="裁判员" :value="1" />
            <el-option label="运动员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="queryParams.college" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleAdd">新增用户</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column label="性别" width="60" align="center">
          <template slot-scope="{ row }">{{ row.gender === 0 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="roleName" label="角色" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="{0:'danger',1:'warning',2:''}[row.role]" size="small">{{ row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" width="120" show-overflow-tooltip />
        <el-table-column prop="className" label="班级" width="110" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="70" align="center">
          <template slot-scope="{ row }">
            <el-switch :value="row.status === 0" @change="val => handleStatusChange(row, val ? 0 : 1)" active-color="#67C23A" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleResetPwd(row)">重置密码</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="学号/工号" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="默认123456" show-password />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择" style="width:100%">
            <el-option label="管理员" :value="0" />
            <el-option label="裁判员" :value="1" />
            <el-option label="运动员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="form.college" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="form.className" placeholder="请输入" />
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
import { getUserPage, addUser, updateUser, deleteUser, resetPassword, updateStatus } from '@/api/user'

export default {
  name: 'UserList',
  data() {
    return {
      loading: false, tableData: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, username: '', realName: '', role: null, college: '', status: null },
      dialogVisible: false, dialogTitle: '', form: {}, submitLoading: false,
      formRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getUserPage(this.queryParams)
        this.tableData = res.data.records || []; this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.loadData() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, username: '', realName: '', role: null, college: '', status: null }; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增用户'
      this.form = { username: '', realName: '', password: '123456', role: 2, gender: 0, phone: '', email: '', college: '', className: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '编辑用户'; this.form = { ...row }; this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) { await updateUser(this.form); this.$message.success('修改成功') }
          else { await addUser(this.form); this.$message.success('新增成功') }
          this.dialogVisible = false; this.loadData()
        } finally { this.submitLoading = false }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除用户「${row.realName}」？`, '提示', { type: 'warning' }).then(async () => {
        await deleteUser(row.id); this.$message.success('删除成功'); this.loadData()
      })
    },
    handleResetPwd(row) {
      this.$confirm(`确定重置「${row.realName}」的密码为123456？`, '提示', { type: 'warning' }).then(async () => {
        await resetPassword(row.id); this.$message.success('密码已重置为123456')
      })
    },
    async handleStatusChange(row, status) {
      await updateStatus(row.id, status)
      this.$message.success(status === 0 ? '已启用' : '已禁用')
      this.loadData()
    }
  }
}
</script>
