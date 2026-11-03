<template>
  <div class="home">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-glow"></div>
      <div class="container hero-grid">
        <div class="hero-copy">
          <span class="hero-script rise">A home for every paw</span>
          <h1 class="rise rise-1">
            给流浪的它，<br />
            一个<span class="accent">温暖的家</span>。
          </h1>
          <p class="hero-lead rise rise-2">
            每一只流浪的毛孩子，都在等一个愿意蹲下来的人。在这里，你可以领养、救助、寄养，也可以只是分享一段故事，让更多人看见它们。
          </p>
          <div class="hero-actions rise rise-3">
            <router-link to="/pets" class="btn btn-primary btn-lg">
              认识它们
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
            </router-link>
            <router-link to="/report" class="btn btn-ghost btn-lg">我发现了流浪动物</router-link>
          </div>
          <div class="hero-trust rise rise-3">
            <div class="avatars">
              <img src="/media/animals/lost-01.jpg" alt="" />
              <img src="/media/animals/lost-02.jpg" alt="" />
              <img src="/media/animals/lost-03.jpg" alt="" />
              <span class="more">+</span>
            </div>
            <div class="trust-text">
              <strong>{{ stats.userTotal || 12 }} 位爱心伙伴</strong>
              <span>正在一起，让流浪止步</span>
            </div>
          </div>
        </div>

        <div class="hero-visual">
          <div class="sun"></div>
          <div class="dot-grid"></div>
          <div class="photo-main" @click="$router.push(heroBanner.link || '/pets')">
            <img :src="heroImage" :alt="heroBanner.title" />
            <div class="photo-label">
              <span class="pill pill-warm"><i class="status-dot"></i>等待一个家</span>
              <span class="photo-name">{{ heroBanner.title }} · {{ heroBanner.subtitle }}</span>
            </div>
            <div class="hero-dots">
              <button v-for="(b, i) in banners" :key="b.id || i" :class="{ active: i === heroIndex }" @click.stop="heroIndex = i"></button>
            </div>
          </div>
          <div class="photo-small">
            <img src="/media/animals/pet-15-xueli.jpg" alt="布偶雪梨" />
          </div>
          <div class="floating-card">
            <span class="fc-label">本月已回家</span>
            <div class="fc-num">{{ stats.petAdoptedThisMonth || 0 }}<span>只</span></div>
            <span class="fc-sub">每一个数字，都是一次重逢</span>
          </div>
          <div class="quote-card">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M9.6 5C6 6.6 4 9.6 4 13.4 4 16.5 5.8 19 8.4 19c2.2 0 3.8-1.6 3.8-3.7 0-2-1.4-3.4-3.3-3.4-.4 0-.8.1-1 .1.4-2 1.7-3.6 3.6-4.7L9.6 5Zm9 0C15 6.6 13 9.6 13 13.4c0 3.1 1.8 5.6 4.4 5.6 2.2 0 3.8-1.6 3.8-3.7 0-2-1.4-3.4-3.3-3.4-.4 0-.8.1-1 .1.4-2 1.7-3.6 3.6-4.7L18.6 5Z"/></svg>
            <p>领养不是拯救，<br />是我们彼此选择。</p>
          </div>
          <div class="stamp">
            <span>ADOPT</span>
            <span>· LOVE ·</span>
          </div>
        </div>
      </div>

      <div class="container quick-wrap">
        <div class="quick-actions">
          <router-link v-for="(q, i) in quickActions" :key="q.title" :to="q.to" class="quick-item" :class="'q-' + i">
            <span class="q-icon" v-html="q.icon"></span>
            <span class="q-text"><strong>{{ q.title }}</strong><small>{{ q.sub }}</small></span>
            <svg class="q-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
          </router-link>
        </div>
      </div>
    </section>

    <!-- 待领养 -->
    <section class="section pets-section">
      <div class="container">
        <SectionHead
          eyebrow="FIND YOUR FRIEND"
          title="它们在等一个家"
          sub="它们已经做好准备，只差一个愿意打开家门的人。"
          action-text="查看全部待领养"
          action-to="/pets"
        />
        <div class="filter-line">
          <div class="chip-row">
            <button v-for="f in filters" :key="f.value" class="chip" :class="{ active: petFilter === f.value }" @click="petFilter = f.value">
              {{ f.label }}
            </button>
          </div>
          <span class="count-note">共 {{ filteredPets.length }} 位小伙伴</span>
        </div>
        <div v-if="filteredPets.length" class="pet-grid">
          <PetCard v-for="pet in filteredPets" :key="pet.id" :pet="pet" />
        </div>
        <div v-else class="empty-state card">
          <div class="icon">🐾</div>
          <p>这个分类下暂时没有待领养的小伙伴，换一个看看？</p>
        </div>
      </div>
    </section>

    <!-- 数据 -->
    <section class="container impact-wrap">
      <div class="impact">
        <div class="impact-copy">
          <span class="eyebrow light">TOGETHER WE CHANGE LIVES</span>
          <h2>每一次伸手，<br />都在改变一个生命。</h2>
          <p>从发现、救助、医疗到重新回家，每一步都离不开志愿者和爱心伙伴。</p>
          <router-link to="/donate" class="btn btn-light btn-sm">支持我们</router-link>
        </div>
        <div class="impact-stats">
          <div v-for="s in impactStats" :key="s.label" class="impact-item">
            <strong>{{ s.value }}<small>{{ s.unit }}</small></strong>
            <span>{{ s.label }}</span>
          </div>
        </div>
        <svg class="impact-paw" viewBox="0 0 48 48" fill="none" aria-hidden="true">
          <ellipse cx="14" cy="17" rx="5.2" ry="6.6" transform="rotate(-20 14 17)" fill="currentColor"/>
          <ellipse cx="24" cy="13" rx="5" ry="6.8" fill="currentColor"/>
          <ellipse cx="34" cy="17" rx="5.2" ry="6.6" transform="rotate(20 34 17)" fill="currentColor"/>
          <ellipse cx="40" cy="27" rx="4" ry="5" transform="rotate(34 40 27)" fill="currentColor"/>
          <path d="M24 22c6.4 0 12 5.1 12 10.6C36 38 31.4 41 24 41s-12-3-12-8.4C12 27.1 17.6 22 24 22Z" fill="currentColor"/>
        </svg>
      </div>
    </section>

    <!-- 流程 -->
    <section class="section process-section">
      <div class="container">
        <div class="process-head">
          <SectionHead eyebrow="HOW IT WORKS" title="领养，是一件认真而温柔的事" sub="我们不追求最快，只希望每一次相遇都经过认真考虑。" />
          <router-link to="/guide" class="link-more">完整领养指南 <span>→</span></router-link>
        </div>
        <div class="steps">
          <div v-for="(step, i) in steps" :key="step.title" class="step">
            <div class="step-index">0{{ i + 1 }}</div>
            <div class="step-icon" v-html="step.icon"></div>
            <h3>{{ step.title }}</h3>
            <p>{{ step.text }}</p>
            <span v-if="i < steps.length - 1" class="step-line"></span>
          </div>
        </div>
      </div>
    </section>

    <!-- 故事 -->
    <section class="section stories-section">
      <div class="container">
        <SectionHead
          eyebrow="HAPPY TAILS"
          title="被爱之后，它们变了"
          sub="这些是领养家庭写下的真实记录。故事还在继续，也许下一章会由你来写。"
          action-text="阅读更多故事"
          action-to="/stories"
        />
        <div class="stories-grid">
          <router-link to="/stories" class="story-main">
            <img :src="stories[0].images" :alt="stories[0].title" />
            <div class="story-overlay">
              <span class="pill pill-warm">{{ stories[0].category }}</span>
              <h3>{{ stories[0].title }}</h3>
              <p>{{ stories[0].content }}</p>
              <span class="story-meta">{{ stories[0].author }} 与 {{ stories[0].petName }}</span>
            </div>
          </router-link>
          <div class="story-side">
            <router-link v-for="story in stories.slice(1, 3)" :key="story.id" to="/stories" class="story-mini">
              <img :src="story.images" :alt="story.title" />
              <div>
                <span class="pill">{{ story.category }}</span>
                <h4>{{ story.title }}</h4>
                <p>{{ story.content }}</p>
                <span class="mini-meta">{{ story.author }} · {{ story.petName }}</span>
              </div>
            </router-link>
            <div class="story-quote">
              <p>“它没有选择出生在哪里，<br />但可以选择和谁一起长大。”</p>
              <span>—— 暖爪志愿者 小林</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI -->
    <section class="container ai-wrap">
      <div class="ai-banner">
        <div class="ai-copy">
          <span class="eyebrow light">AI RESCUE ASSISTANT</span>
          <h2>拿不准的事，<br />先问问救助助手。</h2>
          <p>领养前需要准备什么？刚捡到的小猫怎么喂？疫苗、驱虫、绝育……24 小时在线的 AI 助手，用温柔而专业的方式回答你。</p>
          <router-link to="/chat" class="btn btn-primary">和它聊聊 <span>→</span></router-link>
        </div>
        <div class="chat-mock">
          <div class="chat-head">
            <span class="bot-avatar">🐾</span>
            <div><strong>暖爪助手</strong><small>在线 · 随时回答</small></div>
            <span class="online-dot"></span>
          </div>
          <div class="bubble bot">你好呀，我是暖爪救助助手。今天想了解领养、寄养，还是宠物护理？</div>
          <div class="bubble user">我第一次养猫，有点紧张……</div>
          <div class="bubble bot">紧张说明你很在意它。我们可以从「接猫前的 7 件准备」开始，我一条条陪你确认。</div>
          <div class="typing"><i></i><i></i><i></i></div>
        </div>
      </div>
    </section>

    <!-- 公告 + 捐赠 -->
    <section class="section notice-section">
      <div class="container notice-grid">
        <div>
          <SectionHead eyebrow="LATEST NEWS" title="救助站最近在发生什么" action-text="查看全部" action-to="/community" />
          <div class="notice-list card">
            <button v-for="(n, i) in notices.slice(0, 4)" :key="n.id" class="notice-row" @click="openNotice(n)">
              <span class="notice-date"><strong>{{ day(n.createTime) }}</strong><small>{{ month(n.createTime) }}月</small></span>
              <span class="notice-copy">
                <strong>{{ n.title }}</strong>
                <small>{{ excerpt(n.content, 54) }}</small>
              </span>
              <span class="notice-arrow">↗</span>
            </button>
          </div>
        </div>
        <div class="donate-card">
          <span class="donate-eyebrow">GIVE A LITTLE LOVE</span>
          <h2>一杯咖啡的钱，<br />可以是它今天的一顿饭。</h2>
          <p>所有捐赠用于救助站的粮食、医疗与绝育项目，每月公开明细。</p>
          <div class="donate-amounts">
            <button v-for="a in [20, 50, 100]" :key="a" @click="$router.push('/donate')">¥{{ a }}</button>
          </div>
          <router-link to="/donate" class="btn btn-dark">我要捐赠</router-link>
          <span class="donate-foot">已有 {{ stats.userTotal || 12 }} 位伙伴参与</span>
        </div>
      </div>
    </section>

    <el-dialog :title="currentNotice.title" :visible.sync="noticeVisible" width="620px">
      <p class="notice-dialog-time">{{ currentNotice.createTime }}</p>
      <div class="prose notice-content">{{ currentNotice.content }}</div>
      <div slot="footer"><el-button type="primary" @click="noticeVisible = false">我知道了</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import SectionHead from '../../components/SectionHead.vue'
import PetCard from '../../components/PetCard.vue'
import { getData, imageUrl } from '../../utils/api'
import { demoStats, demoPets, demoStories, demoNotices, demoBanners, getPetType, enrichPet } from '../../utils/demo'

export default {
  name: 'Home',
  components: { SectionHead, PetCard },
  data() {
    return {
      stats: Object.assign({}, demoStats),
      pets: demoPets,
      stories: demoStories,
      notices: demoNotices,
      banners: demoBanners,
      heroIndex: 0,
      heroTimer: null,
      petFilter: 'all',
      filters: [
        { label: '全部', value: 'all' },
        { label: '猫咪', value: '猫' },
        { label: '狗狗', value: '狗' },
        { label: '一岁以下', value: 'baby' }
      ],
      noticeVisible: false,
      currentNotice: {},
      quickActions: [
        { title: '领养一只', sub: '遇见你的家人', to: '/pets', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>' },
        { title: '流浪上报', sub: '让它被看见', to: '/report', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.4"/></svg>' },
        { title: '宠物寄养', sub: '安心出门几天', to: '/foster', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="m3 11 9-7 9 7"/><path d="M5 10v10h14V10M9 20v-6h6v6"/></svg>' },
        { title: '爱心捐赠', sub: '让救助继续', to: '/donate', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="3" y="8" width="18" height="4" rx="1"/><path d="M5 12v8h14v-8M12 8v12M12 8s-4.5-.5-4.5-3A2.5 2.5 0 0 1 12 5a2.5 2.5 0 0 1 4.5 0C16.5 7.5 12 8 12 8Z"/></svg>' }
      ],
      steps: [
        { title: '看看它', text: '浏览待领养档案，了解性格、健康与生活习惯，找到真正适合彼此的那一个。', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>' },
        { title: '写下你', text: '提交领养申请，告诉我们你的居住环境、养宠经验与未来计划。', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M4 20h4l11-11a2.8 2.8 0 0 0-4-4L4 16v4Z"/><path d="m13.5 6.5 4 4"/></svg>' },
        { title: '聊一聊', text: '志愿者会与你沟通并安排家访，确认家里每个人都准备好迎接新成员。', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 11.5a8.4 8.4 0 0 1-9 8.4 9.6 9.6 0 0 1-3.3-.6L4 21l1.3-3.7A8.2 8.2 0 0 1 3 11.5 8.4 8.4 0 0 1 12 3a8.4 8.4 0 0 1 9 8.5Z"/></svg>' },
        { title: '一起回家', text: '签署领养协议，进入试养期。我们会持续回访，陪你度过适应期。', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 11 12 4l9 7"/><path d="M5 10v10h14V10M9 20v-5h6v5"/></svg>' }
      ]
    }
  },
  computed: {
    filteredPets() {
      let list = this.pets.filter(p => p.status === 0)
      if (this.petFilter === 'baby') list = list.filter(p => Number(p.age) < 12)
      else if (this.petFilter !== 'all') list = list.filter(p => getPetType(p) === this.petFilter)
      return list.slice(0, 8)
    },
    heroBanner() {
      return this.banners[this.heroIndex] || this.banners[0] || demoBanners[0]
    },
    heroImage() {
      return imageUrl(this.heroBanner.image)
    },
    impactStats() {
      return [
        { value: this.stats.petAtStation || 0, unit: '', label: '在站动物' },
        { value: this.stats.petAdopted || 0, unit: '', label: '成功找到家' },
        { value: this.stats.petTotal || 0, unit: '', label: '累计救助' },
        { value: this.stats.userTotal || 0, unit: '', label: '爱心注册用户' }
      ]
    }
  },
  created() {
    document.title = '暖爪之家 · 给流浪的它一个温暖的家'
    this.load()
    this.heroTimer = setInterval(() => {
      if (this.banners.length > 1) this.heroIndex = (this.heroIndex + 1) % this.banners.length
    }, 6500)
  },
  beforeDestroy() {
    clearInterval(this.heroTimer)
  },
  methods: {
    async load() {
      const stats = await getData('/dashboard/stats', null, demoStats)
      this.stats = Object.assign({}, demoStats, stats || {})
      if (this.stats.notices && this.stats.notices.length) this.notices = this.stats.notices

      const petData = await getData('/pet/list', { pageNum: 1, pageSize: 8, status: 0 }, { list: demoPets.filter(p => p.status === 0) })
      if (petData && petData.list && petData.list.length) {
        this.pets = petData.list.map(enrichPet)
      }
      const bannerData = await getData('/banner/list', null, demoBanners)
      if (bannerData && bannerData.length) {
        this.banners = bannerData
        this.heroIndex = 0
      }
    },
    openNotice(n) {
      this.currentNotice = n
      this.noticeVisible = true
    },
    excerpt(text, len) {
      const t = String(text || '').replace(/\s+/g, ' ')
      return t.length > len ? t.slice(0, len) + '…' : t
    },
    day(v) { return v ? String(v).slice(8, 10) : '--' },
    month(v) { return v ? String(v).slice(5, 7) : '--' }
  }
}
</script>

<style scoped>
.hero { position: relative; padding: 54px 0 0; overflow: hidden; }
.hero-glow {
  position: absolute; width: 780px; height: 780px; right: -240px; top: -300px;
  background: radial-gradient(circle, rgba(229,178,104,.25), rgba(229,178,104,0) 68%);
  pointer-events: none;
}
.hero-grid { display: grid; grid-template-columns: 1.02fr .98fr; gap: 50px; align-items: center; min-height: 570px; }
.hero-copy { position: relative; z-index: 2; padding-bottom: 70px; }
.hero-script { font-family: var(--font-display); font-style: italic; font-size: 20px; color: var(--primary); letter-spacing: .02em; }
.hero h1 { font-size: clamp(46px, 5.1vw, 74px); line-height: 1.14; letter-spacing: -.055em; margin-top: 20px; font-weight: 700; }
.accent { position: relative; color: var(--primary); white-space: nowrap; }
.accent::after {
  content: ''; position: absolute; left: 2px; right: 2px; bottom: 4px; height: 9px;
  background: rgba(229,178,104,.35); z-index: -1; border-radius: 6px;
}
.hero-lead { font-size: 15.5px; color: var(--ink-2); max-width: 520px; margin-top: 24px; line-height: 1.9; }
.hero-actions { display: flex; gap: 12px; margin-top: 32px; flex-wrap: wrap; }
.hero-trust { display: flex; align-items: center; gap: 14px; margin-top: 38px; }
.avatars { display: flex; }
.avatars img, .more {
  width: 38px; height: 38px; border-radius: 50%; object-fit: cover;
  border: 2px solid var(--cream); margin-left: -10px; background: var(--sage);
}
.avatars img:first-child { margin-left: 0; }
.more { display: flex; align-items: center; justify-content: center; color: #fff; font-size: 14px; background: var(--ink); }
.trust-text { display: flex; flex-direction: column; line-height: 1.4; }
.trust-text strong { font-size: 13px; }
.trust-text span { font-size: 11.5px; color: var(--muted); }

.hero-visual { position: relative; height: 570px; }
.sun { position: absolute; width: 340px; height: 340px; border-radius: 50%; background: var(--cream-2); right: 6%; top: 6%; }
.dot-grid {
  position: absolute; width: 150px; height: 150px; right: 0; top: 46px;
  background-image: radial-gradient(rgba(33,48,41,.22) 1px, transparent 1px);
  background-size: 13px 13px; opacity: .5;
}
.photo-main {
  position: absolute; right: 4%; top: 6%; width: 350px; height: 440px;
  border-radius: 170px 170px 30px 30px; overflow: hidden;
  box-shadow: 0 40px 70px rgba(35,48,42,.18); border: 7px solid var(--paper);
  animation: float 7s ease-in-out infinite;
  cursor: pointer;
}
.photo-main img { width: 100%; height: 100%; object-fit: cover; }
.photo-label {
  position: absolute; left: 0; right: 0; bottom: 0; padding: 26px 20px 16px;
  color: #fff; display: flex; flex-direction: column; gap: 7px;
  background: linear-gradient(transparent, rgba(20,30,25,.72));
}
.photo-name { font-size: 12px; color: rgba(255,255,255,.85); }
.hero-dots { position: absolute; top: 16px; right: 16px; display: flex; gap: 5px; z-index: 5; }
.hero-dots button { width: 6px; height: 6px; padding: 0; border-radius: 999px; border: none; background: rgba(255,255,255,.45); cursor: pointer; transition: all .25s; }
.hero-dots button.active { width: 18px; background: var(--amber); }
.photo-small {
  position: absolute; left: 7%; bottom: 44px; width: 178px; height: 178px;
  border-radius: 50%; overflow: hidden; border: 6px solid var(--paper);
  box-shadow: var(--shadow); z-index: 3;
}
.photo-small img { width: 100%; height: 100%; object-fit: cover; }
.floating-card {
  position: absolute; right: -4px; top: 210px; z-index: 4;
  background: rgba(255,253,249,.94); backdrop-filter: blur(14px);
  border: 1px solid rgba(255,255,255,.8); border-radius: 18px;
  padding: 14px 18px; box-shadow: 0 18px 40px rgba(35,48,42,.14);
  display: flex; flex-direction: column; min-width: 168px;
}
.fc-label { font-size: 10.5px; color: var(--muted); letter-spacing: .06em; }
.fc-num { font-family: var(--font-display); font-size: 32px; line-height: 1.2; color: var(--primary); }
.fc-num span { font-size: 12px; font-family: var(--font-sans); color: var(--muted); margin-left: 3px; }
.fc-sub { font-size: 9.5px; color: var(--muted-2); }
.quote-card {
  position: absolute; left: 1%; top: 86px; z-index: 4; max-width: 188px;
  background: var(--sage-dark); color: #fff; border-radius: 18px;
  padding: 16px 18px; box-shadow: 0 18px 40px rgba(35,48,42,.2);
}
.quote-card svg { opacity: .5; }
.quote-card p { font-size: 12.5px; line-height: 1.65; margin-top: 6px; color: rgba(255,255,255,.86); }
.stamp {
  position: absolute; right: 18%; bottom: 6px; width: 86px; height: 86px;
  border-radius: 50%; border: 1px solid rgba(201,102,63,.45); color: var(--primary);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  font-size: 9px; letter-spacing: .18em; transform: rotate(-12deg);
}
.stamp span:first-child { font-size: 13px; font-weight: 700; letter-spacing: .12em; }
@keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }

.quick-wrap { position: relative; z-index: 5; margin-top: -34px; padding-bottom: 8px; }
.quick-actions { display: grid; grid-template-columns: repeat(4, 1fr); background: var(--paper); border: 1px solid var(--line); border-radius: 22px; box-shadow: var(--shadow); overflow: hidden; }
.quick-item { display: flex; align-items: center; gap: 14px; padding: 22px 24px; border-right: 1px solid var(--line); transition: background .25s; }
.quick-item:last-child { border-right: none; }
.quick-item:hover { background: #fdf9f3; }
.q-icon { width: 44px; height: 44px; border-radius: 14px; display: flex; align-items: center; justify-content: center; background: var(--primary-soft); color: var(--primary); flex-shrink: 0; }
.q-icon svg { width: 22px; height: 22px; }
.q-1 .q-icon { background: var(--sage-soft); color: var(--sage-dark); }
.q-2 .q-icon { background: #fbf0dc; color: #9a6b22; }
.q-3 .q-icon { background: #edf1f6; color: #52708e; }
.q-text { display: flex; flex-direction: column; line-height: 1.4; flex: 1; }
.q-text strong { font-size: 14px; }
.q-text small { font-size: 11px; color: var(--muted); margin-top: 2px; }
.q-arrow { color: var(--muted-2); opacity: 0; transform: translateX(-5px); transition: all .25s; }
.quick-item:hover .q-arrow { opacity: 1; transform: none; color: var(--primary); }

.pets-section { padding-top: 72px; }
.filter-line { display: flex; align-items: center; justify-content: space-between; gap: 18px; margin-bottom: 28px; }
.count-note { font-size: 12px; color: var(--muted); }
.pet-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 22px; }

.impact-wrap { margin-top: 10px; }
.impact {
  position: relative; background: var(--ink); border-radius: 30px; color: #fff;
  padding: 54px 58px; display: grid; grid-template-columns: .9fr 1.1fr; gap: 60px;
  overflow: hidden;
}
.impact-copy h2 { color: #fff; font-size: 32px; letter-spacing: -.03em; margin-top: 14px; }
.impact-copy p { color: rgba(255,255,255,.55); font-size: 13px; margin-top: 14px; max-width: 360px; line-height: 1.9; }
.impact-copy .btn { margin-top: 24px; }
.eyebrow.light { color: var(--amber); }
.impact-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 30px 24px; align-content: center; }
.impact-item { border-left: 1px solid rgba(255,255,255,.12); padding-left: 22px; }
.impact-item strong { display: block; font-family: var(--font-display); font-size: 44px; line-height: 1.05; color: #fff; font-weight: 400; }
.impact-item small { font-size: 15px; color: var(--amber); margin-left: 3px; }
.impact-item span { display: block; font-size: 12px; color: rgba(255,255,255,.45); margin-top: 6px; }
.impact-paw { position: absolute; right: -30px; bottom: -50px; width: 220px; color: rgba(255,255,255,.035); transform: rotate(-12deg); }

.process-section { background: linear-gradient(180deg, var(--cream) 0%, #f8f2e8 100%); }
.process-head { display: flex; align-items: flex-end; justify-content: space-between; gap: 24px; }
.process-head .section-head { flex: 1; margin-bottom: 0; }
.steps { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; margin-top: 48px; }
.step { position: relative; background: rgba(255,253,249,.7); border: 1px solid var(--line); border-radius: 22px; padding: 26px 24px 28px; }
.step-index { font-family: var(--font-display); font-size: 13px; color: var(--primary); letter-spacing: .1em; }
.step-icon { width: 46px; height: 46px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: var(--paper); border: 1px solid var(--line); color: var(--ink); margin: 20px 0 18px; }
.step-icon svg { width: 22px; height: 22px; }
.step h3 { font-size: 17px; }
.step p { font-size: 12.5px; color: var(--muted); line-height: 1.8; margin-top: 9px; }
.step-line { position: absolute; top: 52px; right: -19px; width: 19px; height: 1px; background: var(--line-strong); }

.stories-grid { display: grid; grid-template-columns: 1.12fr .88fr; gap: 24px; }
.story-main { position: relative; border-radius: 26px; overflow: hidden; min-height: 480px; display: block; }
.story-main > img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; transition: transform .8s; }
.story-main:hover > img { transform: scale(1.04); }
.story-overlay { position: absolute; inset: 0; display: flex; flex-direction: column; justify-content: flex-end; padding: 34px; background: linear-gradient(transparent 30%, rgba(20,30,25,.82)); color: #fff; }
.story-overlay h3 { color: #fff; font-size: 27px; margin-top: 14px; letter-spacing: -.03em; }
.story-overlay p { font-size: 13px; color: rgba(255,255,255,.68); margin-top: 10px; line-height: 1.8; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.story-meta { font-size: 11px; color: rgba(255,255,255,.5); margin-top: 16px; }
.story-side { display: flex; flex-direction: column; gap: 18px; }
.story-mini { display: grid; grid-template-columns: 150px 1fr; gap: 18px; background: var(--paper); border: 1px solid var(--line); border-radius: 20px; padding: 12px; transition: all .3s; }
.story-mini:hover { box-shadow: var(--shadow-sm); transform: translateX(3px); }
.story-mini img { width: 150px; height: 140px; object-fit: cover; border-radius: 14px; }
.story-mini h4 { font-size: 16px; margin-top: 10px; line-height: 1.45; }
.story-mini p { font-size: 11.5px; color: var(--muted); margin-top: 7px; line-height: 1.7; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.mini-meta { display: block; font-size: 10px; color: var(--muted-2); margin-top: 8px; }
.story-quote { background: var(--primary-soft); border-radius: 20px; padding: 22px 24px; margin-top: auto; }
.story-quote p { font-family: var(--font-display); font-size: 17px; line-height: 1.7; color: var(--primary-dark); }
.story-quote span { display: block; font-size: 10.5px; color: var(--primary); margin-top: 10px; }

.ai-wrap { padding-top: 12px; }
.ai-banner {
  position: relative; background: linear-gradient(120deg, #22362d 0%, #345042 62%, #40614f 100%);
  border-radius: 30px; padding: 54px 58px; display: grid; grid-template-columns: 1fr .9fr; gap: 50px;
  color: #fff; overflow: hidden;
}
.ai-banner::after {
  content: ''; position: absolute; width: 460px; height: 460px; right: -100px; bottom: -260px;
  border-radius: 50%; border: 1px solid rgba(255,255,255,.08);
  box-shadow: 0 0 0 60px rgba(255,255,255,.02), 0 0 0 140px rgba(255,255,255,.015);
}
.ai-copy h2 { color: #fff; font-size: 34px; letter-spacing: -.035em; margin-top: 14px; line-height: 1.3; }
.ai-copy p { color: rgba(255,255,255,.58); font-size: 13px; line-height: 1.9; margin-top: 16px; max-width: 440px; }
.ai-copy .btn { margin-top: 26px; }
.chat-mock { position: relative; z-index: 2; background: rgba(255,255,255,.07); border: 1px solid rgba(255,255,255,.12); border-radius: 22px; padding: 18px; backdrop-filter: blur(10px); align-self: center; }
.chat-head { display: flex; align-items: center; gap: 10px; padding-bottom: 14px; border-bottom: 1px solid rgba(255,255,255,.1); position: relative; }
.bot-avatar { width: 34px; height: 34px; border-radius: 12px; background: var(--amber); display: flex; align-items: center; justify-content: center; font-size: 16px; }
.chat-head div { display: flex; flex-direction: column; line-height: 1.35; }
.chat-head strong { font-size: 12.5px; }
.chat-head small { font-size: 9.5px; color: rgba(255,255,255,.4); }
.online-dot { position: absolute; right: 4px; width: 7px; height: 7px; border-radius: 50%; background: #8ed09a; box-shadow: 0 0 0 4px rgba(142,208,154,.12); }
.bubble { max-width: 84%; padding: 10px 13px; border-radius: 13px; font-size: 11.5px; line-height: 1.7; margin-top: 12px; }
.bubble.bot { background: rgba(255,255,255,.1); color: rgba(255,255,255,.8); border-top-left-radius: 4px; }
.bubble.user { background: var(--primary); color: #fff; margin-left: auto; border-top-right-radius: 4px; }
.typing { display: flex; gap: 4px; margin-top: 10px; padding-left: 4px; }
.typing i { width: 4px; height: 4px; border-radius: 50%; background: rgba(255,255,255,.4); animation: blink 1.3s infinite; }
.typing i:nth-child(2) { animation-delay: .2s; }
.typing i:nth-child(3) { animation-delay: .4s; }
@keyframes blink { 0%, 80%, 100% { opacity: .2; } 40% { opacity: 1; } }

.notice-section { padding-bottom: 90px; }
.notice-grid { display: grid; grid-template-columns: 1.35fr .65fr; gap: 26px; align-items: start; }
.notice-list { overflow: hidden; }
.notice-row { display: flex; align-items: center; gap: 18px; width: 100%; padding: 19px 24px; border: none; border-bottom: 1px solid var(--line); background: transparent; text-align: left; cursor: pointer; transition: background .2s; }
.notice-row:last-child { border-bottom: none; }
.notice-row:hover { background: #fdf9f3; }
.notice-date { width: 44px; height: 48px; border-radius: 12px; background: var(--cream-2); display: flex; flex-direction: column; align-items: center; justify-content: center; line-height: 1.1; flex-shrink: 0; }
.notice-date strong { font-family: var(--font-display); font-size: 20px; font-weight: 400; color: var(--ink); }
.notice-date small { font-size: 9px; color: var(--muted); }
.notice-copy { display: flex; flex-direction: column; flex: 1; min-width: 0; }
.notice-copy strong { font-size: 13.5px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notice-copy small { font-size: 11px; color: var(--muted); margin-top: 3px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notice-arrow { color: var(--muted-2); font-size: 15px; }

.donate-card { background: var(--primary); color: #fff; border-radius: 26px; padding: 34px 32px; position: relative; overflow: hidden; }
.donate-card::after { content: '♡'; position: absolute; right: -10px; bottom: -40px; font-size: 180px; color: rgba(255,255,255,.07); line-height: 1; }
.donate-eyebrow { font-size: 9.5px; letter-spacing: .2em; color: rgba(255,255,255,.6); }
.donate-card h2 { color: #fff; font-size: 25px; line-height: 1.45; margin-top: 16px; letter-spacing: -.02em; }
.donate-card p { font-size: 12px; color: rgba(255,255,255,.65); line-height: 1.8; margin-top: 12px; }
.donate-amounts { display: flex; gap: 8px; margin-top: 22px; }
.donate-amounts button { flex: 1; height: 42px; border-radius: 10px; border: 1px solid rgba(255,255,255,.25); background: rgba(255,255,255,.08); color: #fff; font-size: 14px; font-weight: 600; cursor: pointer; transition: all .2s; }
.donate-amounts button:hover { background: #fff; color: var(--primary); }
.donate-card .btn { margin-top: 16px; background: #fff; color: var(--ink); }
.donate-foot { display: block; font-size: 10px; color: rgba(255,255,255,.5); margin-top: 14px; }
.notice-dialog-time { font-size: 12px; color: var(--muted); margin-bottom: 14px; }
.notice-content { max-height: 420px; overflow-y: auto; white-space: pre-wrap; }

@media (max-width: 1080px) {
  .hero-grid { grid-template-columns: 1fr 1fr; gap: 20px; }
  .hero-visual { transform: scale(.88); transform-origin: center right; }
  .photo-main { width: 310px; height: 400px; }
  .pet-grid { grid-template-columns: repeat(3, 1fr); }
  .quick-actions { grid-template-columns: repeat(2, 1fr); }
  .quick-item:nth-child(2) { border-right: none; }
  .quick-item:nth-child(-n+2) { border-bottom: 1px solid var(--line); }
  .stories-grid { grid-template-columns: 1fr; }
  .story-main { min-height: 420px; }
}
@media (max-width: 820px) {
  .hero { padding-top: 30px; }
  .hero-grid { grid-template-columns: 1fr; min-height: 0; }
  .hero-copy { padding-bottom: 20px; }
  .hero h1 { font-size: 46px; }
  .hero-visual { height: 470px; max-width: 540px; margin: 0 auto; width: 100%; transform: none; }
  .photo-main { width: 300px; height: 390px; right: 2%; }
  .photo-small { width: 140px; height: 140px; left: 2%; bottom: 20px; }
  .quote-card { left: 0; top: 50px; max-width: 160px; padding: 12px 14px; }
  .floating-card { right: 0; top: 190px; }
  .impact { grid-template-columns: 1fr; gap: 34px; padding: 40px 32px; }
  .steps { grid-template-columns: 1fr 1fr; }
  .step-line { display: none; }
  .ai-banner { grid-template-columns: 1fr; padding: 40px 32px; }
  .notice-grid { grid-template-columns: 1fr; }
  .process-head { flex-direction: column; align-items: flex-start; }
  .pet-grid { grid-template-columns: repeat(2, 1fr); gap: 16px; }
}
@media (max-width: 560px) {
  .hero h1 { font-size: 38px; }
  .hero-actions .btn { width: 100%; }
  .hero-visual { height: 410px; }
  .photo-main { width: 250px; height: 330px; }
  .photo-small { width: 116px; height: 116px; }
  .quote-card { display: none; }
  .floating-card { top: 150px; padding: 10px 13px; }
  .fc-num { font-size: 25px; }
  .quick-actions { grid-template-columns: 1fr; }
  .quick-item { border-right: none; border-bottom: 1px solid var(--line); padding: 16px 18px; }
  .quick-item:last-child { border-bottom: none; }
  .impact-stats { grid-template-columns: 1fr 1fr; gap: 22px 10px; }
  .impact-item strong { font-size: 34px; }
  .steps { grid-template-columns: 1fr; }
  .story-mini { grid-template-columns: 104px 1fr; }
  .story-mini img { width: 104px; height: 120px; }
  .story-main { min-height: 380px; }
  .story-overlay { padding: 22px; }
  .story-overlay h3 { font-size: 21px; }
  .ai-banner, .impact { padding: 34px 24px; }
  .donate-card { padding: 28px 24px; }
  .filter-line { flex-direction: column; align-items: flex-start; }
}
</style>
