<template>
  <div class="admin-shell" :class="{ collapsed, 'mobile-open': mobileOpen }">
    <aside class="admin-aside">
      <div class="admin-brand">
        <span class="mark">
          <svg viewBox="0 0 48 48" fill="none"><ellipse cx="14" cy="17" rx="5.2" ry="6.6" transform="rotate(-20 14 17)" fill="currentColor"/><ellipse cx="24" cy="13" rx="5" ry="6.8" fill="currentColor"/><ellipse cx="34" cy="17" rx="5.2" ry="6.6" transform="rotate(20 34 17)" fill="currentColor"/><ellipse cx="40" cy="27" rx="4" ry="5" transform="rotate(34 40 27)" fill="currentColor"/><path d="M24 22c6.4 0 12 5.1 12 10.6C36 38 31.4 41 24 41s-12-3-12-8.4C12 27.1 17.6 22 24 22Z" fill="currentColor"/></svg>
        </span>
        <div class="words"><strong>暖爪之家</strong><small>ADMIN CONSOLE</small></div>
      </div>

      <nav class="admin-menu">
        <div v-for="g in menus" :key="g.title" class="menu-group">
          <span class="menu-group-title">{{ g.title }}</span>
          <router-link v-for="m in g.items" :key="m.path" :to="m.path" class="menu-item" :class="{ active: $route.path === m.path }" :title="m.label">
            <span v-html="m.icon"></span>
            <span>{{ m.label }}</span>
            <span v-if="m.badge" class="badge">{{ m.badge }}</span>
          </router-link>
        </div>
      </nav>

      <div class="aside-foot">
        <div class="system-status"><i></i><span>系统运行正常</span></div>
        <div class="aside-version">v2.0 · 毕业设计演示版</div>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <button class="collapse-btn" @click="toggle">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M4 6h16M4 12h10M4 18h16"/></svg>
        </button>
        <div class="admin-crumb">
          <span>管理后台</span><span>/</span><strong>{{ pageTitle }}</strong>
        </div>
        <div class="topbar-right">
          <label class="top-search">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
            <input v-model="search" placeholder="搜索功能或数据…" @keyup.enter="doSearch" />
          </label>
          <el-dropdown trigger="click">
            <button class="top-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"/></svg>
              <span class="dot"></span>
            </button>
            <el-dropdown-menu slot="dropdown" class="notice-drop">
              <el-dropdown-item><strong>待审核领养申请</strong><br /><small>有 12 份新申请等待处理</small></el-dropdown-item>
              <el-dropdown-item><strong>新的流浪上报</strong><br /><small>杨浦区五角场 · 3 只幼猫</small></el-dropdown-item>
              <el-dropdown-item><strong>库存提醒</strong><br /><small>四季可拆洗宠物窝仅剩 18 件</small></el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="admin-user">
              <span class="admin-avatar">{{ initial }}</span>
              <div><strong>{{ displayName }}</strong><small>{{ roleLabel }}</small></div>
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="front">返回前台首页</el-dropdown-item>
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </header>

      <main class="admin-content">
        <transition name="fade-page" mode="out-in">
          <router-view :key="$route.fullPath" />
        </transition>
      </main>
    </div>
    <div v-if="mobileOpen" class="mobile-mask" @click="mobileOpen = false"></div>
  </div>
</template>

<script>
import { demoStats } from '../../utils/demo'

export default {
  name: 'AdminLayout',
  data() {
    return {
      collapsed: false,
      mobileOpen: false,
      search: '',
      pending: demoStats,
      menus: [
        {
          title: 'OVERVIEW',
          items: [
            { path: '/admin/dashboard', label: '数据概览', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="3" y="3" width="7" height="9" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="16" width="7" height="5" rx="1"/></svg>' }
          ]
        },
        {
          title: 'RESCUE',
          items: [
            { path: '/admin/adopt', label: '领养审核', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>', badge: 12 },
            { path: '/admin/checkins', label: '试养打卡', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18M8 15l2 2 4-4"/></svg>' },
            { path: '/admin/report', label: '救助上报', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.4"/></svg>', badge: 8 },
            { path: '/admin/foster', label: '寄养管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="m3 11 9-7 9 7"/><path d="M5 10v10h14V10"/></svg>', badge: 5 },
            { path: '/admin/lost', label: '寻宠管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>' }
          ]
        },
        {
          title: 'ANIMALS',
          items: [
            { path: '/admin/pets', label: '动物档案', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><ellipse cx="8" cy="9" rx="2" ry="3"/><ellipse cx="16" cy="9" rx="2" ry="3"/><ellipse cx="5" cy="15" rx="1.6" ry="2.4"/><ellipse cx="19" cy="15" rx="1.6" ry="2.4"/><path d="M12 13c3 0 5 2 5 4.2 0 1.6-1.6 2.8-5 2.8s-5-1.2-5-2.8C7 15 9 13 12 13Z"/></svg>' },
            { path: '/admin/categories', label: '品种管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M4 5h7v7H4zM13 5h7v7h-7zM4 14h7v6H4zM13 14h7v6h-7z"/></svg>' },
            { path: '/admin/rooms', label: '房间管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 10h16M10 10v10"/></svg>' }
          ]
        },
        {
          title: 'CONTENT',
          items: [
            { path: '/admin/banners', label: '首页轮播', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="m3 15 5-5 4 4 3-3 6 6"/><circle cx="8" cy="9" r="1.4"/></svg>' },
            { path: '/admin/posts', label: '社区管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M21 11.5a8.4 8.4 0 0 1-9 8.4 9.6 9.6 0 0 1-3.3-.6L4 21l1.3-3.7A8.2 8.2 0 0 1 3 11.5 8.4 8.4 0 0 1 12 3a8.4 8.4 0 0 1 9 8.5Z"/></svg>' },
            { path: '/admin/notices', label: '公告管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"/></svg>' },
            { path: '/admin/donations', label: '爱心捐赠', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="3" y="8" width="18" height="4" rx="1"/><path d="M5 12v8h14v-8M12 8v12M12 8s-4.5-.5-4.5-3A2.5 2.5 0 0 1 12 5a2.5 2.5 0 0 1 4.5 0C16.5 7.5 12 8 12 8Z"/></svg>' }
          ]
        },
        {
          title: 'SHOP',
          items: [
            { path: '/admin/products', label: '商品管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M6 2 3 6v14h18V6l-3-4Z"/><path d="M3 6h18M16 10a4 4 0 0 1-8 0"/></svg>' },
            { path: '/admin/orders', label: '订单物流', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M3 7h11v10H3zM14 10h4l3 3v4h-7z"/><circle cx="7" cy="18" r="1.6"/><circle cx="17" cy="18" r="1.6"/></svg>' },
            { path: '/admin/companies', label: '物流公司', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M4 21V5h9v16M13 10h6l2 3v8M4 9h9M4 14h9"/></svg>' }
          ]
        },
        {
          title: 'SYSTEM',
          items: [
            { path: '/admin/users', label: '用户管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0M17 5.5a3.5 3.5 0 0 1 0 7M18.5 14.5A5.5 5.5 0 0 1 22 20"/></svg>' }
          ]
        }
      ]
    }
  },
  computed: {
    pageTitle() { return (this.$route.meta && this.$route.meta.title) || '数据概览' },
    displayName() { return this.$store.getters.displayName },
    roleLabel() { return ['普通用户', '普通管理员', '超级管理员'][this.$store.state.role] || '管理员' },
    initial() { return (this.displayName || '管').slice(0, 1) }
  },
  methods: {
    toggle() {
      if (window.innerWidth <= 860) this.mobileOpen = !this.mobileOpen
      else this.collapsed = !this.collapsed
    },
    doSearch() {
      if (!this.search.trim()) return
      const all = this.menus.reduce((arr, g) => arr.concat(g.items), [])
      const hit = all.find(m => m.label.indexOf(this.search.trim()) >= 0)
      if (hit) this.$router.push(hit.path)
      else this.$message.info('没有找到相关功能')
      this.search = ''
    },
    handleCommand(cmd) {
      if (cmd === 'front') this.$router.push('/home')
      if (cmd === 'profile') this.$router.push('/profile')
      if (cmd === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.fade-page-enter-active, .fade-page-leave-active { transition: opacity .18s, transform .18s; }
.fade-page-enter { opacity: 0; transform: translateY(5px); }
.fade-page-leave-to { opacity: 0; }
.mobile-mask { position: fixed; inset: 0; background: rgba(20,30,25,.4); z-index: 99; }
</style>
