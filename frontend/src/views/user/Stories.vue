<template>
  <div class="stories-page">
    <section class="page-hero stories-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>领养故事</span></div>
        <div class="hero-flex">
          <div>
            <span class="eyebrow">HAPPY TAILS</span>
            <h1>被爱之后，<br />它们变成了什么样子？</h1>
            <p>这里记录着领养家庭与毛孩子的日常：第一次靠近、第一个晚安、第一次一起出门。故事很普通，却足够温暖。</p>
          </div>
          <button class="btn btn-primary" @click="openShare">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5Z"/></svg>
            分享我的故事
          </button>
        </div>
        <div class="hero-numbers">
          <div><strong>214</strong><span>个家庭完成了领养</span></div>
          <div><strong>186</strong><span>篇真实故事被记录</span></div>
          <div><strong>4.8k</strong><span>次点赞与祝福</span></div>
        </div>
      </div>
    </section>

    <section class="container stories-body">
      <div class="story-toolbar">
        <div class="chip-row">
          <button v-for="c in categories" :key="c" class="chip" :class="{ active: category === c }" @click="category = c">{{ c }}</button>
        </div>
        <el-select v-model="sort" size="small" class="sort-select">
          <el-option label="最新发布" value="new" />
          <el-option label="最多点赞" value="hot" />
        </el-select>
      </div>

      <div v-if="filtered.length" class="story-grid">
        <article v-for="(story, i) in filtered" :key="story.id" class="story-card" :class="{ featured: i === 0 }" @click="openStory(story)">
          <div class="story-img">
            <img :src="story.images" :alt="story.title" loading="lazy" />
            <span class="story-cat">{{ story.category }}</span>
            <span class="story-date">{{ formatDate(story.createTime) }}</span>
          </div>
          <div class="story-content">
            <h3>{{ story.title }}</h3>
            <p>{{ story.content }}</p>
            <div class="story-foot">
              <span class="author"><span class="a-avatar">{{ story.author.slice(0, 1) }}</span>{{ story.author }}<small>与 {{ story.petName }}</small></span>
              <button class="like-btn" :class="{ liked: liked[story.id] }" @click.stop="like(story)">
                <svg width="15" height="15" viewBox="0 0 24 24" :fill="liked[story.id] ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>
                {{ story.likes + (liked[story.id] ? 1 : 0) }}
              </button>
            </div>
          </div>
        </article>
      </div>
      <div v-else class="empty-state card">
        <div class="icon">🌿</div>
        <p>这个分类下还没有故事，来写下第一篇吧。</p>
      </div>

      <div class="quote-band">
        <span class="q-mark">“</span>
        <p>我们以为是在救它，<br />后来才发现，是它教会了我们如何去爱。</p>
        <span class="q-author">—— 暖爪之家 · 领养家庭留言</span>
      </div>
    </section>

    <el-dialog :visible.sync="storyVisible" width="680px" custom-class="story-dialog">
      <div v-if="current">
        <img :src="current.images" class="dialog-cover" :alt="current.title" />
        <div class="dialog-body">
          <span class="pill pill-warm">{{ current.category }}</span>
          <h2>{{ current.title }}</h2>
          <div class="dialog-meta">{{ current.author }} · 与 {{ current.petName }} 一起</div>
          <p class="dialog-text">{{ current.content }}</p>
          <div class="dialog-actions">
            <button @click="like(current)">♥ {{ current.likes + (liked[current.id] ? 1 : 0) }}</button>
            <router-link to="/pets">我也想领养 →</router-link>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog title="分享你的领养故事" :visible.sync="shareVisible" width="560px">
      <el-form :model="form" label-position="top">
        <el-form-item label="故事标题"><el-input v-model="form.title" placeholder="给这段故事起一个名字" /></el-form-item>
        <div class="form-grid">
          <el-form-item label="你的昵称"><el-input v-model="form.author" placeholder="怎么称呼你" /></el-form-item>
          <el-form-item label="毛孩子的名字"><el-input v-model="form.petName" placeholder="它叫什么" /></el-form-item>
        </div>
        <el-form-item label="故事内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="从第一次见面说起吧…" maxlength="800" show-word-limit />
        </el-form-item>
        <el-form-item label="配图">
          <div class="upload-row">
            <el-upload action="/api/upload" :show-file-list="false" :on-success="onUpload" :before-upload="beforeUpload">
              <div class="upload-box">
                <img v-if="form.images" :src="form.images" alt="" />
                <template v-else><span>＋</span><small>上传一张照片</small></template>
              </div>
            </el-upload>
            <p>建议上传横向照片，展示你们最日常的一刻。</p>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="shareVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStory">发布故事</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { demoStories } from '../../utils/demo'
import { formatDate, requireLogin } from '../../utils/api'

export default {
  name: 'Stories',
  data() {
    return {
      stories: demoStories,
      category: '全部',
      categories: ['全部', '领养故事', '日常记录', '志愿者手记'],
      sort: 'new',
      liked: {},
      storyVisible: false,
      shareVisible: false,
      current: null,
      form: { title: '', author: '', petName: '', content: '', images: '', category: '领养故事', likes: 0 }
    }
  },
  computed: {
    filtered() {
      let list = this.category === '全部' ? this.stories.slice() : this.stories.filter(s => s.category === this.category)
      if (this.sort === 'hot') list.sort((a, b) => b.likes - a.likes)
      return list
    }
  },
  created() {
    document.title = '领养故事 · 暖爪之家'
    const saved = localStorage.getItem('myStories')
    if (saved) {
      try { this.stories = JSON.parse(saved).concat(demoStories) } catch (e) {}
    }
  },
  methods: {
    formatDate,
    openStory(story) { this.current = story; this.storyVisible = true },
    like(story) {
      if (this.liked[story.id]) {
        this.$set(this.liked, story.id, false)
      } else {
        this.$set(this.liked, story.id, true)
        if (!(story.id > 1000)) this.$message.success('已送出一个小小的祝福')
      }
    },
    openShare() {
      if (!requireLogin(this, '/stories')) return
      this.shareVisible = true
    },
    beforeUpload(file) {
      const ok = /image\/(jpeg|png|gif|webp)/.test(file.type)
      if (!ok) this.$message.warning('请上传图片文件')
      return ok
    },
    onUpload(res) {
      if (res && res.code === 200) this.form.images = '/api' + res.data.url
      else this.$message.warning('上传失败，请稍后重试')
    },
    submitStory() {
      if (!this.form.title || !this.form.content) return this.$message.warning('请填写标题和故事内容')
      const story = Object.assign({}, this.form, {
        id: Date.now(),
        author: this.form.author || '匿名伙伴',
        petName: this.form.petName || '毛孩子',
        images: this.form.images || '/images/story3.jpg',
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      })
      this.stories.unshift(story)
      const mine = this.stories.filter(s => s.id > 1000).map(s => Object.assign({}, s, { images: s.images }))
      localStorage.setItem('myStories', JSON.stringify(mine))
      this.shareVisible = false
      this.form = { title: '', author: '', petName: '', content: '', images: '', category: '领养故事', likes: 0 }
      this.$message.success('故事已发布，谢谢你的分享')
    }
  }
}
</script>

<style scoped>
.stories-hero { padding-bottom: 40px; }
.hero-flex { display: flex; align-items: flex-end; justify-content: space-between; gap: 40px; margin-top: 18px; }
.page-hero h1 { font-size: clamp(40px, 4.6vw, 62px); line-height: 1.18; }
.hero-numbers { display: flex; gap: 52px; margin-top: 38px; padding-top: 24px; border-top: 1px solid var(--line); }
.hero-numbers div { display: flex; flex-direction: column; }
.hero-numbers strong { font-family: var(--font-display); font-size: 30px; font-weight: 400; color: var(--primary); }
.hero-numbers span { font-size: 11px; color: var(--muted); margin-top: 3px; }
.stories-body { padding: 44px 0 90px; }
.story-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 20px; margin-bottom: 28px; }
.sort-select { width: 120px; }
.story-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.story-card { background: var(--paper); border: 1px solid var(--line); border-radius: 22px; overflow: hidden; cursor: pointer; transition: all .35s; }
.story-card:hover { transform: translateY(-6px); box-shadow: var(--shadow); }
.story-card.featured { grid-column: span 2; display: grid; grid-template-columns: 1.15fr .85fr; }
.story-img { position: relative; aspect-ratio: 4 / 3; overflow: hidden; }
.story-card.featured .story-img { aspect-ratio: auto; min-height: 360px; }
.story-img img { width: 100%; height: 100%; object-fit: cover; transition: transform .7s; }
.story-card:hover .story-img img { transform: scale(1.04); }
.story-cat { position: absolute; top: 14px; left: 14px; background: rgba(255,253,249,.92); color: var(--primary); font-size: 10px; font-weight: 700; padding: 5px 11px; border-radius: 999px; }
.story-date { position: absolute; right: 14px; bottom: 12px; font-size: 10px; color: rgba(255,255,255,.85); text-shadow: 0 1px 4px rgba(0,0,0,.3); }
.story-content { padding: 20px 22px 18px; display: flex; flex-direction: column; }
.story-content h3 { font-size: 18px; line-height: 1.5; letter-spacing: -.02em; }
.featured .story-content { padding: 28px 26px; }
.featured .story-content h3 { font-size: 24px; }
.story-content > p { font-size: 12px; color: var(--muted); line-height: 1.8; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.story-foot { display: flex; align-items: center; justify-content: space-between; margin-top: auto; padding-top: 16px; border-top: 1px solid var(--line); }
.author { display: flex; align-items: center; gap: 8px; font-size: 11.5px; font-weight: 600; }
.author small { font-weight: 400; color: var(--muted); font-size: 10px; }
.a-avatar { width: 25px; height: 25px; border-radius: 50%; background: var(--sage-soft); color: var(--sage-dark); display: flex; align-items: center; justify-content: center; font-size: 10px; }
.like-btn { display: inline-flex; align-items: center; gap: 5px; border: none; background: none; color: var(--muted); font-size: 11px; cursor: pointer; }
.like-btn.liked { color: var(--primary); }
.quote-band { margin-top: 60px; background: var(--sage-dark); color: #fff; border-radius: 26px; padding: 48px 40px; text-align: center; position: relative; overflow: hidden; }
.quote-band .q-mark { position: absolute; left: 36px; top: 10px; font-family: Georgia, serif; font-size: 160px; line-height: 1; color: rgba(255,255,255,.06); }
.quote-band p { font-family: var(--font-display); font-size: clamp(22px, 2.6vw, 34px); line-height: 1.7; }
.q-author { display: block; font-size: 11px; color: rgba(255,255,255,.45); margin-top: 18px; }
.dialog-cover { width: 100%; height: 300px; object-fit: cover; }
.dialog-body { padding: 4px 6px 6px; }
.dialog-body h2 { font-size: 24px; margin-top: 10px; }
.dialog-meta { font-size: 11px; color: var(--muted); margin-top: 6px; }
.dialog-text { font-size: 13.5px; line-height: 2; color: var(--ink-2); margin-top: 16px; white-space: pre-wrap; }
.dialog-actions { display: flex; align-items: center; justify-content: space-between; margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--line); }
.dialog-actions button { border: none; background: var(--primary-soft); color: var(--primary); border-radius: 999px; padding: 7px 14px; cursor: pointer; }
.dialog-actions a { font-size: 12px; color: var(--primary); }
.upload-row { display: flex; gap: 16px; align-items: flex-start; }
.upload-box { width: 160px; height: 110px; border: 1px dashed var(--line-strong); border-radius: 12px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); overflow: hidden; }
.upload-box img { width: 100%; height: 100%; object-fit: cover; }
.upload-box span { font-size: 20px; }
.upload-box small { font-size: 10px; }
.upload-row p { font-size: 10.5px; color: var(--muted); max-width: 240px; line-height: 1.7; }

@media (max-width: 980px) {
  .story-grid { grid-template-columns: 1fr 1fr; }
  .story-card.featured { grid-column: span 2; }
}
@media (max-width: 680px) {
  .hero-flex { flex-direction: column; align-items: flex-start; }
  .hero-numbers { gap: 24px; flex-wrap: wrap; }
  .story-grid { grid-template-columns: 1fr; }
  .story-card.featured { grid-column: span 1; display: block; }
  .story-card.featured .story-img { aspect-ratio: 4/3; min-height: 0; }
  .story-toolbar { flex-direction: column; align-items: flex-start; }
}
</style>
