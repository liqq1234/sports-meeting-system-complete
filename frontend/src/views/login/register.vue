<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <i class="el-icon-trophy logo-icon"></i>
        <h2>运动员注册</h2>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入学号/工号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="学院" prop="college">
          <el-input v-model="form.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleRegister">注 册</el-button>
        </el-form-item>
        <div class="login-link">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth'

export default {
  name: 'Register',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: { username: '', password: '', confirmPassword: '', realName: '', gender: 0, phone: '', college: '', className: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        college: [{ required: true, message: '请输入学院', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.loading = true
        const data = { ...this.form }
        delete data.confirmPassword
        register(data).then(() => {
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        }).catch(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
}
.register-card {
  width: 500px;
  padding: 36px 40px;
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}
.register-header {
  text-align: center;
  margin-bottom: 24px;
  .logo-icon { font-size: 40px; color: #409EFF; }
  h2 { margin-top: 8px; color: #333; }
}
.login-link {
  text-align: center;
  span { color: #999; font-size: 13px; }
}
</style>
