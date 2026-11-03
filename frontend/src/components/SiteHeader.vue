<template>
  <header class="site-head">
    <div class="topbar">
      <div class="container topbar-inner">
        <span class="top-note">
          <span class="live-dot"></span>
          10 月线下领养日 · 本周六 13:00 徐汇滨江
          <router-link to="/community">查看详情</router-link>
        </span>
        <div class="top-links">
          <a href="tel:4001234567">救助热线 400-123-4567</a>
          <span class="sep"></span>
          <router-link to="/donate">爱心捐赠</router-link>
        </div>
      </div>
    </div>

    <nav class="nav" :class="{ scrolled }">
      <div class="container nav-inner">
        <BrandLogo />

        <div class="links">
          <router-link v-for="item in navs" :key="item.path" :to="item.path" class="nav-link" :class="{ active: isActive(item) }">
            {{ item.label }}
          </router-link>
          <el-dropdown trigger="hover" class="more-drop" @command="go">
            <span class="nav-link more">更多服务
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/foster">宠物寄养</el-dropdown-item>
              <el-dropdown-item command="/report">流浪上报</el-dropdown-item>
              <el-dropdown-item command="/chat">AI 救助助手</el-dropdown-item>
              <el-dropdown-item command="/donate">爱心捐赠</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>

        <div class="actions">
          <button class="icon-btn search-btn" aria-label="搜索宠物" @click="$router.push('/pets')">
            <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
          </button>

          <template v-if="userInfo">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-chip">
                <span class="avatar">
                  <img v-if="userInfo.avatar" :src="avatarSrc" alt="" />
                  <template v-else>{{ initial }}</template>
                </span>
                <span class="uname">{{ displayName }}</span>
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="/profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="/orders">我的订单</el-dropdown-item>
                <el-dropdown-item v-if="role >= 1" command="/admin" divided>进入管理后台</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="login-link">登录</router-link>
            <router-link to="/register" class="btn btn-primary btn-sm">加入我们</router-link>
          </template>

          <button class="menu-toggle" aria-label="菜单" @click="mobileOpen = !mobileOpen">
            <span></span><span></span><span></span>
          </button>
        </div>
      </div>

      <div v-show="mobileOpen" class="mobile-panel">
        <router-link v-for="item in allNavs" :key="item.path" :to="item.path" class="mobile-link" @click.native="mobileOpen = false">
          {{ item.label }}
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
        </router-link>
        <div class="mobile-auth">
          <router-link v-if="!userInfo" to="/login" class="btn btn-ghost btn-sm" @click.native="mobileOpen = false">登录</router-link>
          <router-link v-if="!userInfo" to="/register" class="btn btn-primary btn-sm" @click.native="mobileOpen = false">注册</router-link>
          <button v-if="userInfo" class="btn btn-ghost btn-sm" @click="handleCommand('logout')">退出登录</button>
        </div>
      </div>
    </nav>
  </header>
</template>

<script>
import BrandLogo from './BrandLogo.vue'

export default {
  name: 'SiteHeader',
  components: { BrandLogo },
  data() {
    return {
      scrolled: false,
      mobileOpen: false,
      navs: [
        { label: '首页', path: '/home' },
        { label: '领养中心', path: '/pets' },
        { label: '领养故事', path: '/stories' },
        { label: '宠物社区', path: '/community' },
        { label: '寻宠寻主', path: '/lost' },
        { label: '公益商城', path: '/shop' }
      ]
    }
  },
  computed: {
    userInfo() { return this.$store.state.userInfo },
    role() { return this.$store.state.role },
    displayName() { return this.$store.getters.displayName },
    initial() { return (this.displayName || '暖').slice(0, 1) },
    avatarSrc() {
      const a = this.userInfo && this.userInfo.avatar
      if (!a) return ''
      return a.indexOf('/upload') === 0 ? '/api' + a : a
    },
    allNavs() {
      return this.navs.concat([
        { label: '宠物寄养', path: '/foster' },
        { label: '流浪上报', path: '/report' },
        { label: '爱心捐赠', path: '/donate' },
        { label: 'AI 救助助手', path: '/chat' }
      ])
    }
  },
  mounted() {
    window.addEventListener('scroll', this.onScroll, { passive: true })
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.onScroll)
  },
  watch: {
    '$route.path'() { this.mobileOpen = false }
  },
  methods: {
    onScroll() { this.scrolled = window.scrollY > 8 },
    isActive(item) {
      if (item.path === '/home') return this.$route.path === '/' || this.$route.path === '/home'
      if (item.path === '/pets') return this.$route.path.indexOf('/pet') === 0
      return this.$route.path === item.path
    },
    go(path) { this.$router.push(path) },
    handleCommand(cmd) {
      if (cmd === 'logout') {
        this.$store.dispatch('logout')
        this.$message.success('已退出登录')
        if (this.$route.path !== '/home') this.$router.push('/home')
      } else if (cmd) {
        this.$router.push(cmd)
      }
      this.mobileOpen = false
    }
  }
}
</script>

<style scoped>
.site-head { position: relative; z-index: 50; }
.topbar { background: var(--ink); color: rgba(255,255,255,.72); font-size: 12px; }
.topbar-inner { height: 36px; display: flex; align-items: center; justify-content: space-between; gap: 20px; }
.top-note { display: inline-flex; align-items: center; gap: 8px; letter-spacing: .02em; }
.top-note a { color: #fff; border-bottom: 1px solid rgba(255,255,255,.35); margin-left: 4px; }
.live-dot { width: 6px; height: 6px; border-radius: 50%; background: #e8b86d; box-shadow: 0 0 0 4px rgba(232,184,109,.13); }
.top-links { display: flex; align-items: center; gap: 14px; }
.top-links a:hover { color: #fff; }
.sep { width: 1px; height: 10px; background: rgba(255,255,255,.2); }

.nav { position: sticky; top: 0; background: rgba(251,247,240,.9); backdrop-filter: blur(18px); -webkit-backdrop-filter: blur(18px); border-bottom: 1px solid transparent; transition: box-shadow .3s, border-color .3s, background .3s; }
.nav.scrolled { border-bottom-color: var(--line); box-shadow: 0 10px 30px rgba(35,48,42,.05); background: rgba(251,247,240,.96); }
.nav-inner { height: 78px; display: flex; align-items: center; gap: 38px; }
.links { display: flex; align-items: center; gap: 4px; flex: 1; }
.nav-link { position: relative; display: inline-flex; align-items: center; gap: 5px; padding: 8px 12px; font-size: 13.5px; font-weight: 500; color: var(--ink-2); border-radius: 9px; transition: color .2s, background .2s; white-space: nowrap; }
.nav-link:hover { color: var(--primary); background: rgba(201,102,63,.05); }
.nav-link.active { color: var(--ink); font-weight: 700; }
.nav-link.active::after { content: ''; position: absolute; left: 12px; right: 12px; bottom: 1px; height: 2px; border-radius: 2px; background: var(--primary); }
.more-drop { cursor: pointer; }
.more { padding-right: 6px; }

.actions { display: flex; align-items: center; gap: 13px; }
.icon-btn { width: 38px; height: 38px; border-radius: 50%; border: 1px solid var(--line); background: transparent; color: var(--ink-2); cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all .2s; }
.icon-btn:hover { background: #fff; border-color: var(--line-strong); color: var(--primary); }
.login-link { font-size: 13.5px; color: var(--ink-2); padding: 6px 4px; }
.login-link:hover { color: var(--primary); }
.user-chip { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px 4px 4px; border-radius: 999px; transition: background .2s; }
.user-chip:hover { background: rgba(255,255,255,.7); }
.avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--sage); color: #fff; font-size: 13px; font-weight: 700; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.avatar img { width: 100%; height: 100%; object-fit: cover; }
.uname { font-size: 13px; max-width: 90px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.menu-toggle { display: none; width: 38px; height: 38px; border: none; background: transparent; flex-direction: column; align-items: center; justify-content: center; gap: 4px; cursor: pointer; }
.menu-toggle span { width: 19px; height: 1.6px; background: var(--ink); border-radius: 2px; }
.mobile-panel { display: none; padding: 8px 20px 22px; border-top: 1px solid var(--line); background: var(--cream); }
.mobile-link { display: flex; align-items: center; justify-content: space-between; padding: 13px 2px; border-bottom: 1px solid var(--line); font-size: 14px; }
.mobile-auth { display: flex; gap: 10px; margin-top: 18px; }

@media (max-width: 1180px) {
  .nav-inner { gap: 18px; }
  .links { gap: 0; }
  .nav-link { padding: 8px 9px; font-size: 13px; }
}
@media (max-width: 980px) {
  .links, .search-btn, .uname, .login-link, .actions > .btn { display: none; }
  .menu-toggle { display: flex; }
  .mobile-panel { display: block; }
  .nav-inner { justify-content: space-between; height: 70px; }
  .top-links { display: none; }
}
</style>
