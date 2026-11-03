<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-top">
        <div class="brand-col">
          <BrandLogo light />
          <p>让每一次相遇，都有归处。<br />我们相信，爱可以让流浪止步。</p>
          <div class="socials">
            <span class="social">微信</span>
            <span class="social">微博</span>
            <span class="social">小红书</span>
          </div>
        </div>
        <div class="link-col">
          <h4>快速导航</h4>
          <router-link to="/pets">领养中心</router-link>
          <router-link to="/stories">领养故事</router-link>
          <router-link to="/lost">寻宠寻主</router-link>
          <router-link to="/community">宠物社区</router-link>
        </div>
        <div class="link-col">
          <h4>参与救助</h4>
          <router-link to="/report">流浪上报</router-link>
          <router-link to="/foster">宠物寄养</router-link>
          <router-link to="/donate">爱心捐赠</router-link>
          <router-link to="/chat">AI 救助助手</router-link>
        </div>
        <div class="contact-col">
          <h4>联系我们</h4>
          <p class="contact-line"><span>救助热线</span>400-123-4567</p>
          <p class="contact-line"><span>服务时间</span>每天 09:00 – 21:00</p>
          <p class="contact-line"><span>救助站</span>上海市徐汇区暖爪路 18 号</p>
          <router-link to="/register" class="join-link">成为志愿者 →</router-link>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2026 暖爪之家 · 流浪动物救助领养平台</span>
        <span class="beian">沪ICP备2026000000号 · 本站为毕业设计演示项目</span>
      </div>
    </div>
  </footer>
</template>

<script>
import BrandLogo from './BrandLogo.vue'
export default { name: 'SiteFooter', components: { BrandLogo } }
</script>

<style scoped>
.footer { background: var(--ink); color: rgba(255,255,255,.68); margin-top: 40px; }
.footer-top { display: grid; grid-template-columns: 1.6fr .8fr .8fr 1.3fr; gap: 56px; padding: 64px 0 48px; }
.brand-col p { font-size: 13px; line-height: 1.9; margin-top: 18px; color: rgba(255,255,255,.5); }
.socials { display: flex; gap: 8px; margin-top: 22px; }
.social { font-size: 11px; padding: 5px 11px; border: 1px solid rgba(255,255,255,.16); border-radius: 999px; color: rgba(255,255,255,.6); }
h4 { font-size: 13px; color: #fff; letter-spacing: .08em; margin-bottom: 20px; }
.link-col { display: flex; flex-direction: column; gap: 12px; }
.link-col a { font-size: 13px; color: rgba(255,255,255,.55); transition: color .2s; }
.link-col a:hover { color: #fff; }
.contact-line { font-size: 12.5px; line-height: 1.9; color: rgba(255,255,255,.75); }
.contact-line span { display: inline-block; width: 64px; color: rgba(255,255,255,.38); }
.join-link { display: inline-block; margin-top: 18px; color: var(--amber); font-size: 13px; }
.footer-bottom { display: flex; justify-content: space-between; gap: 20px; padding: 22px 0 28px; border-top: 1px solid rgba(255,255,255,.09); font-size: 11.5px; color: rgba(255,255,255,.38); }
@media (max-width: 900px) {
  .footer-top { grid-template-columns: 1fr 1fr; gap: 36px; padding: 48px 0 36px; }
  .brand-col { grid-column: 1 / -1; }
  .footer-bottom { flex-direction: column; gap: 8px; }
}
@media (max-width: 560px) {
  .footer-top { grid-template-columns: 1fr; }
}
</style>
