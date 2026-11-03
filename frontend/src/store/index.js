import Vue from 'vue'
import Vuex from 'vuex'
import request from '../utils/request'
import { demoUser, demoAdmin } from '../utils/demo'

Vue.use(Vuex)

function readUser() {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || 'null')
  } catch (e) {
    return null
  }
}

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: readUser(),
    role: parseInt(localStorage.getItem('role') || '0', 10)
  },
  getters: {
    isLogin: state => !!state.token,
    isAdmin: state => state.role >= 1,
    isSuperAdmin: state => state.role >= 2,
    displayName: state => state.userInfo ? (state.userInfo.nickname || state.userInfo.username) : '游客',
    isDemo: state => (state.token || '').indexOf('demo-') === 0
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      state.role = Number(userInfo.role || 0)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      localStorage.setItem('role', userInfo.role)
    },
    UPDATE_USER(state, patch) {
      state.userInfo = Object.assign({}, state.userInfo || {}, patch)
      localStorage.setItem('userInfo', JSON.stringify(state.userInfo))
    },
    LOGOUT(state) {
      state.token = ''
      state.userInfo = null
      state.role = 0
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('role')
    }
  },
  actions: {
    async login({ commit }, loginForm) {
      const res = await request.post('/user/login', loginForm)
      commit('SET_TOKEN', res.data.token)
      commit('SET_USER_INFO', res.data.userInfo)
      return res
    },
    demoLogin({ commit }, role) {
      const user = role >= 1 ? Object.assign({}, demoAdmin, { role }) : Object.assign({}, demoUser)
      commit('SET_TOKEN', 'demo-token-' + role)
      commit('SET_USER_INFO', user)
      return user
    },
    logout({ commit }) {
      commit('LOGOUT')
    }
  }
})
