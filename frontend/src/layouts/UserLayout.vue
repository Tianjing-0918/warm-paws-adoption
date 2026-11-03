<template>
  <div class="site-shell">
    <SiteHeader />
    <main class="site-main">
      <transition name="page" mode="out-in">
        <router-view :key="$route.fullPath" />
      </transition>
    </main>
    <SiteFooter />

    <router-link to="/chat" class="ai-float" title="AI 救助助手">
      <span class="pulse"></span>
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7">
        <path d="M21 11.5a8.4 8.4 0 0 1-9 8.4 9.6 9.6 0 0 1-3.3-.6L4 21l1.3-3.7A8.2 8.2 0 0 1 3 11.5 8.4 8.4 0 0 1 12 3a8.4 8.4 0 0 1 9 8.5Z"/>
        <path d="M8.5 10.5h.01M12 10.5h.01M15.5 10.5h.01" stroke-linecap="round" stroke-width="2.4"/>
      </svg>
      <span class="tip">问问救助助手</span>
    </router-link>

    <transition name="fade">
      <button v-show="showTop" class="to-top" aria-label="回到顶部" @click="toTop">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 19V5M6 11l6-6 6 6"/></svg>
      </button>
    </transition>
  </div>
</template>

<script>
import SiteHeader from '../components/SiteHeader.vue'
import SiteFooter from '../components/SiteFooter.vue'

export default {
  name: 'UserLayout',
  components: { SiteHeader, SiteFooter },
  data() { return { showTop: false } },
  mounted() { window.addEventListener('scroll', this.onScroll, { passive: true }) },
  beforeDestroy() { window.removeEventListener('scroll', this.onScroll) },
  methods: {
    onScroll() { this.showTop = window.scrollY > 620 },
    toTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
  }
}
</script>

<style scoped>
.site-shell { min-height: 100vh; display: flex; flex-direction: column; }
.site-main { flex: 1; }
.page-enter-active, .page-leave-active { transition: opacity .22s ease, transform .22s ease; }
.page-enter { opacity: 0; transform: translateY(8px); }
.page-leave-to { opacity: 0; }

.ai-float {
  position: fixed; right: 26px; bottom: 30px; z-index: 60;
  width: 54px; height: 54px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: var(--ink); color: #fff;
  box-shadow: 0 14px 34px rgba(33,48,41,.3);
  transition: transform .25s;
}
.ai-float:hover { transform: translateY(-4px); }
.ai-float .tip {
  position: absolute; right: 64px; white-space: nowrap;
  background: var(--paper); color: var(--ink); font-size: 12px;
  padding: 7px 12px; border-radius: 8px; box-shadow: var(--shadow-sm);
  opacity: 0; transform: translateX(6px); pointer-events: none; transition: all .2s;
}
.ai-float:hover .tip { opacity: 1; transform: none; }
.pulse { position: absolute; inset: -4px; border-radius: 50%; border: 1px solid rgba(33,48,41,.2); animation: pulse 2.6s infinite; }
@keyframes pulse { 0% { transform: scale(.9); opacity: .8; } 70% { transform: scale(1.25); opacity: 0; } 100% { opacity: 0; } }

.to-top {
  position: fixed; right: 30px; bottom: 96px; z-index: 59;
  width: 40px; height: 40px; border-radius: 50%;
  border: 1px solid var(--line); background: rgba(255,253,249,.9);
  color: var(--ink-2); cursor: pointer; display: flex; align-items: center; justify-content: center;
  box-shadow: var(--shadow-sm);
}
.fade-enter-active, .fade-leave-active { transition: opacity .2s; }
.fade-enter, .fade-leave-to { opacity: 0; }
@media (max-width: 720px) {
  .ai-float { right: 16px; bottom: 18px; width: 48px; height: 48px; }
  .to-top { display: none; }
}
</style>
