import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user
  },
  getters: {
    token: state => state.user.token,
    userInfo: state => state.user.userInfo,
    userId: state => state.user.userInfo ? state.user.userInfo.id : null,
    role: state => state.user.userInfo ? state.user.userInfo.role : null
  }
})
