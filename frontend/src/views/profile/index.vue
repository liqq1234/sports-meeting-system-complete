<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="profile-header">
            <el-avatar :size="80" icon="el-icon-user-solid" />
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <el-tag :type="{0:'danger',1:'warning',2:''}[userInfo.role]">{{ roleName }}</el-tag>
          </div>
          <el-divider />
          <div class="profile-info">
            <div class="info-item"><span class="label">用户名</span><span>{{ userInfo.username }}</span></div>
            <div class="info-item"><span class="label">性别</span><span>{{ userInfo.gender === 0 ? '男' : '女' }}</span></div>
            <div class="info-item"><span class="label">手机号</span><span>{{ userInfo.phone || '-' }}</span></div>
            <div class="info-item"><span class="label">邮箱</span><span>{{ userInfo.email || '-' }}</span></div>
            <div class="info-item"><span class="label">学院</span><span>{{ userInfo.college || '-' }}</span></div>
            <div class="info-item"><span class="label">班级</span><span>{{ userInfo.className || '-' }}</span></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="never">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="修改资料" name="profile">
              <el-form ref="profileForm" :model="profileForm" :rules="profileRules" label-width="80px" style="max-width:480px">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="profileForm.realName" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio :label="0">男</el-radio>
                    <el-radio :label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" />
                </el-form-item>
                <el-form-item label="学院">
                  <el-input v-model="profileForm.college" />
                </el-form-item>
                <el-form-item label="班级">
                  <el-input v-model="profileForm.className" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="profileLoading" @click="submitProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
              <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="100px" style="max-width:480px">
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="pwdLoading" @click="submitPassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { updateProfile, changePassword } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.pwdForm.newPassword) callback(new Error('两次输入的密码不一致'))
      else callback()
    }
    return {
      activeTab: 'profile',
      profileForm: {},
      profileRules: { realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }] },
      profileLoading: false,
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
      },
      pwdLoading: false
    }
  },
  computed: {
    userInfo() { return this.$store.getters.userInfo || {} },
    roleName() { return { 0: '管理员', 1: '裁判员', 2: '运动员' }[this.userInfo.role] || '' }
  },
  created() {
    this.profileForm = {
      realName: this.userInfo.realName,
      gender: this.userInfo.gender,
      phone: this.userInfo.phone,
      email: this.userInfo.email,
      college: this.userInfo.college,
      className: this.userInfo.className
    }
  },
  methods: {
    submitProfile() {
      this.$refs.profileForm.validate(async valid => {
        if (!valid) return
        this.profileLoading = true
        try {
          await updateProfile(this.profileForm)
          this.$message.success('修改成功')
          this.$store.dispatch('user/getUserInfo')
        } finally { this.profileLoading = false }
      })
    },
    submitPassword() {
      this.$refs.pwdForm.validate(async valid => {
        if (!valid) return
        this.pwdLoading = true
        try {
          await changePassword(this.pwdForm)
          this.$message.success('密码修改成功，请重新登录')
          this.$store.dispatch('user/logout').then(() => this.$router.push('/login'))
        } finally { this.pwdLoading = false }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-header {
  text-align: center;
  h3 { margin: 12px 0 8px; }
}
.profile-info {
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
    font-size: 14px;
    .label { color: #999; }
    &:last-child { border-bottom: none; }
  }
}
</style>
