import request from './request'

const demoMode = () => {
  const token = localStorage.getItem('token') || ''
  return token.indexOf('demo-') === 0
}

/** 静默 GET：优先取后端数据，失败时回退演示数据 */
export async function getData(url, params, fallback) {
  if (demoMode()) return fallback
  try {
    const res = await request.get(url, { params, silent: true })
    return res.data
  } catch (e) {
    return fallback
  }
}

/** 静默 POST：演示模式下直接返回 fallback，不影响页面演示 */
export async function postData(url, data, fallback) {
  if (demoMode()) return fallback
  try {
    const res = await request.post(url, data, { silent: true })
    return res.data
  } catch (e) {
    return fallback
  }
}

export async function putData(url, data, fallback) {
  if (demoMode()) return fallback
  try {
    const res = await request.put(url, data, { silent: true })
    return res.data
  } catch (e) {
    return fallback
  }
}

export async function deleteData(url, fallback) {
  if (demoMode()) return fallback
  try {
    const res = await request.delete(url, { silent: true })
    return res.data
  } catch (e) {
    return fallback
  }
}

/** 后端返回的 /upload/xx 图片地址转成可访问地址；本地演示图直接使用 */
export function imageUrl(path) {
  if (!path) return '/images/pet1.jpg'
  const first = String(path).split(',')[0].trim()
  if (!first) return '/images/pet1.jpg'
  if (first.startsWith('http') || first.startsWith('/images/')) return first
  if (first.startsWith('/upload/')) return '/api' + first
  if (first.startsWith('upload/')) return '/api/' + first
  return first
}

export function imageList(path) {
  if (!path) return []
  return String(path).split(',').filter(Boolean).map(imageUrl)
}

export function formatDate(value, withTime = false) {
  if (!value) return ''
  const date = new Date(String(value).replace(/-/g, '/'))
  if (isNaN(date.getTime())) return String(value).slice(0, 16)
  const pad = n => String(n).padStart(2, '0')
  const base = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
  return withTime ? `${base} ${pad(date.getHours())}:${pad(date.getMinutes())}` : base
}

export function relativeTime(value) {
  if (!value) return ''
  const date = new Date(String(value).replace(/-/g, '/'))
  const diff = Date.now() - date.getTime()
  if (isNaN(diff) || diff < 0) return formatDate(value)
  const min = Math.floor(diff / 60000)
  if (min < 1) return '刚刚'
  if (min < 60) return `${min} 分钟前`
  const hour = Math.floor(min / 60)
  if (hour < 24) return `${hour} 小时前`
  const day = Math.floor(hour / 24)
  if (day < 30) return `${day} 天前`
  return formatDate(value)
}

export function requireLogin(vm, redirect) {
  const token = localStorage.getItem('token')
  if (!token) {
    vm.$message.warning('请先登录后再操作')
    vm.$router.push({ path: '/login', query: { redirect: redirect || vm.$route.fullPath } })
    return false
  }
  return true
}
