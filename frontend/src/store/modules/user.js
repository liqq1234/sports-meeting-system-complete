import { login, logout, getCurrentUser } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: getToken(),
  userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
  }
}

const actions = {
  login({ commit }, loginForm) {
    return new Promise((resolve, reject) => {
      login(loginForm).then(res => {
        const { access_token, user } = res.data
        const token = access_token // Alias for internal consistency
        commit('SET_TOKEN', token)
        commit('SET_USER_INFO', user)
        setToken(token)
        localStorage.setItem('userInfo', JSON.stringify(user))
        resolve(res)
      }).catch(err => reject(err))
    })
  },

  getUserInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getCurrentUser().then(res => {
        commit('SET_USER_INFO', res.data)
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        resolve(res)
      }).catch(err => reject(err))
    })
  },

  logout({ commit }) {
    return new Promise((resolve) => {
      logout().catch(() => {})
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', null)
      removeToken()
      localStorage.removeItem('userInfo')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
