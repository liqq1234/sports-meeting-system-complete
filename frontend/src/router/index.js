import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'
import Layout from '@/layout/index.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
      }
    ]
  },
  {
    path: '/meeting',
    component: Layout,
    redirect: '/meeting/list',
    meta: { title: '运动会管理', icon: 'el-icon-trophy' },
    children: [
      {
        path: 'list',
        name: 'MeetingList',
        component: () => import('@/views/meeting/index.vue'),
        meta: { title: '运动会列表' }
      }
    ]
  },
  {
    path: '/event',
    component: Layout,
    redirect: '/event/list',
    meta: { title: '比赛项目', icon: 'el-icon-medal' },
    children: [
      {
        path: 'list',
        name: 'EventList',
        component: () => import('@/views/event/index.vue'),
        meta: { title: '项目列表' }
      }
    ]
  },
  {
    path: '/registration',
    component: Layout,
    redirect: '/registration/list',
    meta: { title: '报名管理', icon: 'el-icon-edit-outline' },
    children: [
      {
        path: 'list',
        name: 'RegistrationList',
        component: () => import('@/views/registration/index.vue'),
        meta: { title: '报名列表' }
      },
      {
        path: 'my',
        name: 'MyRegistration',
        component: () => import('@/views/registration/my.vue'),
        meta: { title: '我的报名', roles: [2] }
      }
    ]
  },
  {
    path: '/schedule',
    component: Layout,
    redirect: '/schedule/list',
    meta: { title: '赛程管理', icon: 'el-icon-date' },
    children: [
      {
        path: 'list',
        name: 'ScheduleList',
        component: () => import('@/views/schedule/index.vue'),
        meta: { title: '赛程列表' }
      }
    ]
  },
  {
    path: '/score',
    component: Layout,
    redirect: '/score/list',
    meta: { title: '成绩管理', icon: 'el-icon-s-data' },
    children: [
      {
        path: 'list',
        name: 'ScoreList',
        component: () => import('@/views/score/index.vue'),
        meta: { title: '成绩列表' }
      },
      {
        path: 'my',
        name: 'MyScore',
        component: () => import('@/views/score/my.vue'),
        meta: { title: '我的成绩', roles: [2] }
      }
    ]
  },
  {
    path: '/logistics',
    component: Layout,
    redirect: '/logistics/material',
    meta: { title: '后勤管理', icon: 'el-icon-truck', roles: [0] },
    children: [
      {
        path: 'material',
        name: 'MaterialList',
        component: () => import('@/views/logistics/material.vue'),
        meta: { title: '物资库' }
      },
      {
        path: 'allocation',
        name: 'AllocationList',
        component: () => import('@/views/logistics/allocation.vue'),
        meta: { title: '物资发放' }
      }
    ]
  },
  {
    path: '/medical',
    component: Layout,
    redirect: '/medical/records',
    meta: { title: '医疗保障', icon: 'el-icon-first-aid-kit' },
    children: [
      {
        path: 'records',
        name: 'MedicalRecords',
        component: () => import('@/views/medical/records.vue'),
        meta: { title: '就诊记录' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
    meta: { title: '用户管理', icon: 'el-icon-user', roles: [0] },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户列表', roles: [0] }
      }
    ]
  },
  {
    path: '/venue',
    component: Layout,
    redirect: '/venue/list',
    meta: { title: '场地管理', icon: 'el-icon-place', roles: [0] },
    children: [
      {
        path: 'list',
        name: 'VenueList',
        component: () => import('@/views/venue/index.vue'),
        meta: { title: '场地列表', roles: [0] }
      }
    ]
  },
  {
    path: '/notice',
    component: Layout,
    redirect: '/notice/list',
    meta: { title: '通知公告', icon: 'el-icon-bell' },
    children: [
      {
        path: 'list',
        name: 'NoticeList',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '通知列表' }
      }
    ]
  },
  {
    path: '/message',
    component: Layout,
    redirect: '/message/list',
    meta: { title: '消息中心', icon: 'el-icon-message' },
    children: [
      {
        path: 'list',
        name: 'MessageList',
        component: () => import('@/views/message/index.vue'),
        meta: { title: '我的消息' }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'el-icon-setting' }
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

// 路由守卫
const whiteList = ['/login', '/register']
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 高校体育运动会管理系统` : '高校体育运动会管理系统'
  const token = store.getters.token
  if (token) {
    if (to.path === '/login' || to.path === '/register') {
      // 已登录用户访问登录/注册页时，如果来自系统内部页面则阻止跳转
      if (from.path && from.path !== '/' && !whiteList.includes(from.path)) {
        next(false)
      } else {
        next({ path: '/' })
      }
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
