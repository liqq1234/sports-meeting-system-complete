<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <i class="el-icon-trophy logo-icon"></i>
        <h2>高校体育运动会管理系统</h2>
        <p>College Sports Meeting Management System</p>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名" @keyup.enter.native="handleLogin" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" placeholder="请输入密码" show-password @keyup.enter.native="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleLogin">登 录</el-button>
        </el-form-item>
        <div class="login-tips">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
        <div class="demo-accounts">
          <el-divider content-position="center">演示账号</el-divider>
          <div class="account-tags">
            <el-tag @click="fillAccount('admin', '123456')" style="cursor:pointer">管理员: admin</el-tag>
            <el-tag type="success" @click="fillAccount('referee01', '123456')" style="cursor:pointer">裁判员: referee01</el-tag>
            <el-tag type="warning" @click="fillAccount('2024001', '123456')" style="cursor:pointer">运动员: 2024001</el-tag>
          </div>
          <p class="pwd-tip">默认密码均为: 123456</p>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: { username: '', password: '' },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    fillAccount(username, password) {
      this.loginForm.username = username
      this.loginForm.password = password
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return
        this.loading = true
        this.$store.dispatch('user/login', this.loginForm).then(() => {
          this.$message.success('登录成功')
          const redirect = this.$route.query.redirect || '/'
          this.$router.push(redirect)
        }).catch(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
  .logo-icon { font-size: 48px; color: #409EFF; }
  h2 { margin: 12px 0 4px; color: #333; font-size: 22px; }
  p { color: #999; font-size: 12px; }
}
.login-tips {
  text-align: center;
  margin-top: -8px;
  span { color: #999; font-size: 13px; }
}
.demo-accounts {
  margin-top: 16px;
  .account-tags {
    display: flex;
    justify-content: center;
    gap: 8px;
    flex-wrap: wrap;
  }
  .pwd-tip { text-align: center; color: #999; font-size: 12px; margin-top: 8px; }
}
</style>
