import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: process.env.VUE_APP_STATIC_DEMO === 'true' ? 2500 : 20000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token && token.indexOf('demo-') !== 0) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(response => {
  const res = response.data
  if (res.code === 200) return res

  if (res.code === 401) {
    if (!response.config.silent) {
      Message.warning(res.message || '登录状态已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('role')
      router.push('/login')
    }
    return Promise.reject(res)
  }

  if (!response.config.silent) Message.error(res.message || '请求失败，请稍后再试')
  return Promise.reject(res)
}, error => {
  const silent = error.config && error.config.silent
  if (!silent) {
    const msg = error.response ? '服务暂时不可用，请稍后重试' : '无法连接服务器，请确认后端已启动'
    Message.error(msg)
  }
  return Promise.reject(error)
})

export default request
