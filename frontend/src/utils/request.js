import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import router from '@/router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    NProgress.start()
    const token = store.getters.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    NProgress.done()
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    NProgress.done()
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      if (res.code === 401) {
        MessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/logout').then(() => {
            router.push('/login').catch(() => {})
          })
        })
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    NProgress.done()
    if (error.response && error.response.status === 401) {
      store.dispatch('user/logout').then(() => {
        router.push('/login').catch(() => {})
      })
    } else {
      Message.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default service
