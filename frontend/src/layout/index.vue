<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-container">
        <svg v-if="!isCollapse" class="logo-svg" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
          <circle cx="16" cy="16" r="15" fill="#1890ff" stroke="#1890ff" stroke-width="2"/>
          <path d="M10 20l6-10 6 10" stroke="#fff" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          <circle cx="16" cy="8" r="2" fill="#FFD700"/>
        </svg>
        <h1 v-if="!isCollapse" class="logo-title">{{ role === 2 ? '智慧运动会' : '运动会管理系统' }}</h1>
        <i v-else class="el-icon-trophy logo-icon"></i>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="transparent"
        text-color="#303133"
        active-text-color="#1890ff"
        router
        unique-opened
        class="glass-menu"
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>

        <el-menu-item index="/meeting/list">
          <i class="el-icon-trophy"></i>
          <span slot="title">{{ role === 2 ? '赛事中心' : '运动会管理' }}</span>
        </el-menu-item>

        <el-menu-item index="/event/list">
          <i class="el-icon-medal"></i>
          <span slot="title">比赛项目</span>
        </el-menu-item>

        <el-submenu index="registration" v-if="role === 0 || role === 1">
          <template slot="title">
            <i class="el-icon-edit-outline"></i>
            <span>报名管理</span>
          </template>
          <el-menu-item index="/registration/list">报名列表</el-menu-item>
        </el-submenu>

        <el-menu-item index="/registration/my" v-if="role === 2">
          <i class="el-icon-edit-outline"></i>
          <span slot="title">报名记录</span>
        </el-menu-item>

        <el-menu-item index="/schedule/list">
          <i class="el-icon-date"></i>
          <span slot="title">{{ role === 2 ? '赛事日程' : '赛程管理' }}</span>
        </el-menu-item>

        <el-submenu index="score" v-if="role === 0 || role === 1">
          <template slot="title">
            <i class="el-icon-s-data"></i>
            <span>成绩管理</span>
          </template>
          <el-menu-item index="/score/list">成绩列表</el-menu-item>
        </el-submenu>

        <el-menu-item index="/score/my" v-if="role === 2">
          <i class="el-icon-s-data"></i>
          <span slot="title">我的成绩</span>
        </el-menu-item>

        <el-menu-item index="/health/my" v-if="role === 2">
          <i class="el-icon-first-aid-kit"></i>
          <span slot="title">我的健康</span>
        </el-menu-item>

        <el-menu-item index="/user/list" v-if="role === 0">
          <i class="el-icon-user"></i>
          <span slot="title">用户管理</span>
        </el-menu-item>

        <el-menu-item index="/venue/list" v-if="role === 0">
          <i class="el-icon-place"></i>
          <span slot="title">场地管理</span>
        </el-menu-item>

        <el-submenu index="logistics">
          <template slot="title">
            <i class="el-icon-truck"></i>
            <span>后勤服务</span>
          </template>
          <el-menu-item index="/logistics/material" v-if="role === 0">物资库</el-menu-item>
          <el-menu-item index="/logistics/allocation">{{ role === 0 ? '发放管理' : '物资领用' }}</el-menu-item>
        </el-submenu>

        <el-menu-item index="/medical/records" v-if="role === 0 || role === 1">
          <i class="el-icon-first-aid-kit"></i>
          <span slot="title">医疗保障</span>
        </el-menu-item>

        <el-menu-item index="/notice/list">
          <i class="el-icon-bell"></i>
          <span slot="title">通知公告</span>
        </el-menu-item>

        <el-menu-item index="/message/list">
          <i class="el-icon-message"></i>
          <span slot="title">消息中心</span>
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="menu-badge" />
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" class="collapse-btn" @click="toggleCollapse"></i>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title && $route.path !== '/dashboard'">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
              <span class="username">{{ userInfo ? userInfo.realName || userInfo.username : '' }}</span>
              <span class="role-tag">{{ roleName }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile" icon="el-icon-user">个人中心</el-dropdown-item>
              <el-dropdown-item command="password" icon="el-icon-lock">修改密码</el-dropdown-item>
              <el-dropdown-item divided command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <transition name="fade-transform" mode="out-in">
          <keep-alive>
            <router-view :key="$route.path" />
          </keep-alive>
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { countUnread } from '@/api/message'

export default {
  name: 'Layout',
  data() {
    return {
      isCollapse: false,
      unreadCount: 0
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    userInfo() {
      return this.$store.getters.userInfo
    },
    role() {
      return this.$store.getters.role
    },
    roleName() {
      const names = { 0: '管理员', 1: '裁判员', 2: '运动员' }
      return names[this.role] || ''
    }
  },
  created() {
    this.fetchUnread()
    this.timer = setInterval(this.fetchUnread, 60000)
    // 初始化 WebSocket 实时推送连接
    this.initWebSocket()
  },
  beforeDestroy() {
    clearInterval(this.timer)
    // 关闭 WebSocket 连接
    if (this.ws) {
      this.ws.close()
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    initWebSocket() {
      const userId = this.$store.getters.userInfo && this.$store.getters.userInfo.id
      if (!userId) return
      const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
      const wsUrl = `${protocol}://${location.hostname}:8080/api/ws/notify/${userId}`
      try {
        this.ws = new WebSocket(wsUrl)
        this.ws.onopen = () => {
          console.log('[WebSocket] 连接已建立')
          // 补齐拉取错过的消息
          this.fetchUnread()
        }
        this.ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            if (data.type === 'pong') return
            if (data.type === 'notice') {
              this.$message({ type: 'info', message: `《${data.title}》: ${data.content}`, duration: 6000, showClose: true })
              this.unreadCount++
            } else if (data.type === 'notice_published') {
              this.$message({ type: 'success', message: '有新公告已发布，请前往通知中心查看', duration: 5000 })
              this.unreadCount++
            } else if (data.type === 'schedule_changed') {
              this.$message({ type: 'warning', message: `赛程变更通知: ${data.content || '请查看最新赛程'}`, duration: 8000, showClose: true })
            }
          } catch (e) {
            // 心跳不解析
          }
        }
        this.ws.onerror = () => {
          console.warn('[WebSocket] 连接失误，将改用轮询模式')
        }
        // 心跳保洶00
        this.heartbeat = setInterval(() => {
          if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send('ping')
          }
        }, 30000)
      } catch (e) {
        console.warn('[WebSocket] 初始化失误', e)
      }
    },
    async fetchUnread() {
      try {
        const res = await countUnread()
        this.unreadCount = res.data || 0
      } catch (e) {
        // ignore
      }
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定退出登录？', '提示', { type: 'warning' }).then(() => {
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login').catch(() => {})
          })
        })
      } else if (command === 'profile' || command === 'password') {
        if (this.$route.path !== '/profile') {
          this.$router.push('/profile').catch(() => {})
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #e0f2fe 0%, #e6f7ff 100%);
}
.layout-aside {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 2px 0 8px rgba(0,0,0,0.02);
  transition: width 0.3s;
  overflow-x: hidden;
  .logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.3);
    border-bottom: 1px solid rgba(255, 255, 255, 0.5);
    .logo-svg { width: 32px; height: 32px; margin-right: 8px; }
    .logo-img { width: 32px; height: 32px; margin-right: 8px; }
    .logo-title { color: #1890ff; font-weight: bold; font-size: 16px; white-space: nowrap; }
    .logo-icon { color: #1890ff; font-size: 28px; }
  }
  .glass-menu { 
    border-right: none; 
    background: transparent;
  }
  /* overriding element ui menu hover/active in transparent mode */
  ::v-deep .el-menu-item:hover, ::v-deep .el-submenu__title:hover {
    background-color: rgba(24, 144, 255, 0.1) !important;
  }
  ::v-deep .el-menu-item.is-active {
    background-color: rgba(24, 144, 255, 0.15) !important;
    font-weight: bold;
  }
}
.layout-header {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  .header-left {
    display: flex;
    align-items: center;
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 16px;
      color: #666;
      &:hover { color: #409EFF; }
    }
  }
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      .username { margin: 0 8px; font-size: 14px; color: #333; }
      .role-tag {
        background: #ecf5ff;
        color: #409EFF;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        margin-right: 4px;
      }
    }
  }
}
.layout-main {
  background: transparent;
  padding: 16px;
}
.menu-badge {
  margin-left: 8px;
}
</style>
