<template>
  <div v-if="pet" class="detail-page">
    <section class="detail-head">
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/home">首页</router-link><span>/</span>
          <router-link to="/pets">领养中心</router-link><span>/</span>
          <span>{{ pet.name }}</span>
        </div>
      </div>
    </section>

    <section class="container detail-grid">
      <div class="gallery">
        <div class="main-photo">
          <img :src="activeImage" :alt="pet.name" @click="viewerVisible = true" />
          <span class="state-badge" :class="'s-' + pet.status">
            <i class="status-dot"></i>{{ statusLabel }}
          </span>
          <button class="expand" @click="viewerVisible = true">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M8 3H3v5M16 3h5v5M8 21H3v-5M16 21h5v-5"/></svg>
          </button>
        </div>
        <div v-if="images.length > 1" class="thumbs">
          <button v-for="(img, i) in images" :key="img + i" :class="{ active: activeIndex === i }" @click="activeIndex = i">
            <img :src="img" alt="" />
          </button>
        </div>
        <div class="gallery-note">
          <span>📷</span>
          <p>照片由救助站志愿者拍摄。如果你想了解更多生活视频，可以在咨询时向我们索取。</p>
        </div>
      </div>

      <aside class="info">
        <div class="info-top">
          <span class="pill pill-warm">{{ pet.categoryName || '待确认品种' }}</span>
          <button class="share-btn" @click="copyLink">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><path d="m8.7 10.6 6.6-3.8M8.7 13.4l6.6 3.8"/></svg>
            分享
          </button>
        </div>
        <h1>{{ pet.name }}</h1>
        <p class="subline">{{ pet.categoryName }} · {{ pet.gender || '未知性别' }} · {{ ageText(pet.age) }}</p>

        <div class="key-facts">
          <div><span>体重</span><strong>{{ pet.weight ? pet.weight + ' kg' : '待补充' }}</strong></div>
          <div><span>毛色</span><strong>{{ pet.color || '待补充' }}</strong></div>
          <div><span>所在地</span><strong>{{ shortLocation }}</strong></div>
        </div>

        <div class="health-chips">
          <span :class="{ done: pet.vaccinated !== false }"><i></i>疫苗</span>
          <span :class="{ done: pet.sterilized !== false }"><i></i>绝育</span>
          <span :class="{ done: pet.dewormed !== false }"><i></i>驱虫</span>
          <span class="done"><i></i>基础体检</span>
        </div>

        <div class="story-text">
          <h3>关于 {{ pet.name }}</h3>
          <p>{{ pet.description || '这个小家伙还在慢慢向我们打开心扉，欢迎来救助站和它见一面。' }}</p>
        </div>

        <div v-if="pet.traits && pet.traits.length" class="trait-row">
          <span v-for="t in pet.traits" :key="t">{{ t }}</span>
        </div>

        <div class="health-box">
          <div class="hb-icon">✚</div>
          <div>
            <strong>健康情况</strong>
            <p>{{ pet.healthStatus || '已完成基础体检，具体情况可在申请时咨询。' }}</p>
          </div>
        </div>

        <div class="actions">
          <button class="btn btn-primary btn-lg btn-block" :disabled="pet.status !== 0" @click="openApply">
            {{ pet.status === 0 ? '我想领养 ' + pet.name : '它已经找到家了' }}
            <svg v-if="pet.status === 0" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
          </button>
          <router-link to="/chat" class="btn btn-ghost btn-block">先咨询一下救助助手</router-link>
        </div>
        <p class="fee-note">领养免费 · 需年满 18 岁 · 接受定期回访</p>
      </aside>
    </section>

    <section class="container detail-lower">
      <div class="tabs-card">
        <div class="tab-head">
          <button v-for="t in tabs" :key="t.key" :class="{ active: tab === t.key }" @click="tab = t.key">{{ t.label }}</button>
        </div>
        <div v-if="tab === 'story'" class="tab-body">
          <div class="story-block">
            <h2>我们是怎么遇见 {{ pet.name }} 的</h2>
            <p>{{ rescueStory }}</p>
            <div class="timeline">
              <div v-for="(item, i) in timeline" :key="i" class="tl-item">
                <span class="tl-dot"></span>
                <div><strong>{{ item.title }}</strong><p>{{ item.text }}</p></div>
                <span class="tl-date">{{ item.date }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="tab === 'care'" class="tab-body care-grid">
          <div v-for="c in careList" :key="c.title" class="care-item">
            <span class="care-icon">{{ c.icon }}</span>
            <div><h3>{{ c.title }}</h3><p>{{ c.text }}</p></div>
          </div>
        </div>
        <div v-else class="tab-body">
          <div class="faq">
            <div v-for="(f, i) in faqs" :key="i" class="faq-item">
              <h3>{{ f.q }}</h3>
              <p>{{ f.a }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="side-card">
        <h3>领养 {{ pet.name }} 的流程</h3>
        <div class="side-steps">
          <div v-for="(s, i) in miniSteps" :key="i"><span>{{ i + 1 }}</span><p>{{ s }}</p></div>
        </div>
        <router-link to="/guide" class="link-more">完整领养指南 →</router-link>
        <div class="contact-box">
          <span>需要帮助？</span>
          <strong>400-123-4567</strong>
          <small>每天 09:00 – 21:00</small>
        </div>
      </div>
    </section>

    <section class="container related">
      <div class="section-head">
        <div><span class="eyebrow">MEET MORE FRIENDS</span><h2 class="section-title">也许你也会喜欢它们</h2></div>
        <router-link to="/pets" class="link-more">全部待领养 →</router-link>
      </div>
      <div class="related-grid">
        <PetCard v-for="p in related" :key="p.id" :pet="p" />
      </div>
    </section>

    <el-dialog :visible.sync="applyVisible" width="620px" custom-class="apply-dialog" :show-close="step === 1">
      <div v-if="step === 1" class="apply-box">
        <span class="eyebrow">ADOPTION APPLICATION</span>
        <h2>申请领养 {{ pet.name }}</h2>
        <p class="dialog-sub">请认真填写，志愿者会在 1–2 个工作日内与你联系。</p>
        <el-form ref="applyForm" :model="applyForm" :rules="rules" label-position="top">
          <el-form-item label="你为什么想领养它？" prop="reason">
            <el-input v-model="applyForm.reason" type="textarea" :rows="3" placeholder="说说你的想法、家庭情况和能给它的生活…" maxlength="300" show-word-limit />
          </el-form-item>
          <div class="form-grid">
            <el-form-item label="居住地址" prop="address">
              <el-input v-model="applyForm.address" placeholder="城市 / 区域，方便安排家访" />
            </el-form-item>
            <el-form-item label="养宠经验" prop="experience">
              <el-input v-model="applyForm.experience" placeholder="例如：养过 2 年猫 / 第一次养宠" />
            </el-form-item>
          </div>
          <el-checkbox v-model="agree" class="agree">我已阅读并同意《领养须知》，承诺不遗弃、不转送，并接受定期回访。</el-checkbox>
        </el-form>
        <div class="dialog-actions">
          <el-button @click="applyVisible = false">再想想</el-button>
          <el-button type="primary" :loading="submitting" @click="submitApply">提交申请</el-button>
        </div>
      </div>
      <div v-else class="success-box">
        <div class="success-icon">🎉</div>
        <h2>申请已提交</h2>
        <p>我们已经收到你对 {{ pet.name }} 的心意。志愿者会尽快与你联系，记得留意手机和消息。</p>
        <div class="success-actions">
          <el-button @click="applyVisible = false">继续看看</el-button>
          <el-button type="primary" @click="$router.push('/profile')">查看申请进度</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="viewerVisible" width="min(860px, 92vw)" append-to-body class="viewer-dialog">
      <img :src="activeImage" :alt="pet.name" style="width:100%;border-radius:14px" />
    </el-dialog>
  </div>
</template>

<script>
import PetCard from '../../components/PetCard.vue'
import { getData, postData, imageList, requireLogin } from '../../utils/api'
import { demoPets, ageText, statusText, enrichPet } from '../../utils/demo'

export default {
  name: 'PetDetail',
  components: { PetCard },
  data() {
    return {
      pet: null,
      fromApi: false,
      activeIndex: 0,
      tab: 'story',
      tabs: [
        { key: 'story', label: '它的故事' },
        { key: 'care', label: '养护建议' },
        { key: 'faq', label: '领养问答' }
      ],
      applyVisible: false,
      viewerVisible: false,
      step: 1,
      submitting: false,
      agree: false,
      applyForm: { reason: '', address: '', experience: '' },
      rules: {
        reason: [{ required: true, message: '请简单说说想领养的理由', trigger: 'blur' }, { min: 10, message: '再多写一点吧，至少 10 个字', trigger: 'blur' }],
        address: [{ required: true, message: '请填写居住地址', trigger: 'blur' }],
        experience: [{ required: true, message: '请填写养宠经验', trigger: 'blur' }]
      },
      careList: [
        { icon: '🍽️', title: '饮食', text: '定时定量，换粮需要 7 天过渡。幼宠建议少食多餐，准备充足的干净饮水。' },
        { icon: '🏠', title: '环境', text: '先准备一个安静、安全的小空间，放好猫砂盆或尿垫，不要急着带它认识所有房间。' },
        { icon: '💉', title: '健康', text: '按时疫苗和驱虫，出现拒食、呕吐、精神不振等情况及时就医。' },
        { icon: '🧶', title: '陪伴', text: '每天留出固定的互动时间。猫需要攀爬和躲藏空间，狗需要稳定的户外活动。' }
      ],
      faqs: [
        { q: '领养需要付费吗？', a: '不需要。领养本身完全免费。领养人需要自行承担宠物日常饮食、疫苗、驱虫和医疗费用。' },
        { q: '还没有养宠经验可以申请吗？', a: '可以。我们更看重你是否认真了解过养宠的责任，以及是否愿意学习。第一次养宠也可以在申请中说明，我们会提供对应的建议。' },
        { q: '如果之后实在无法继续养怎么办？', a: '请第一时间联系救助站，我们会协助重新安置。领养协议中明确约定不得私自转送或遗弃。' },
        { q: '试养期是什么意思？', a: '审核通过后通常有 7–14 天试养期，双方都可以在这段时间里确认是否合适。试养期间需要定期上传照片或视频打卡。' }
      ],
      miniSteps: ['浏览档案，提交申请', '志愿者沟通与家访', '签署协议，进入试养期', '定期回访，正式领养'],
      timeline: [
        { title: '被发现', text: '志愿者在社区附近发现了它，并带回了救助站。', date: '2026.08' },
        { title: '完成体检', text: '进行了基础体检、驱虫与疫苗，确认身体状况稳定。', date: '2026.09' },
        { title: '等待一个家', text: '经过一段时间的观察和陪伴，它已经准备好开启新的生活。', date: '现在' }
      ]
    }
  },
  computed: {
    images() {
      if (!this.pet) return []
      const imgs = imageList(this.pet.images)
      return imgs.length ? imgs : ['/media/animals/pet-01-mimi.jpg']
    },
    activeImage() { return this.images[this.activeIndex] || this.images[0] },
    statusLabel() { return statusText(this.pet.status) },
    shortLocation() { return (this.pet.location || '上海市 · 暖爪救助站').replace('上海市 · ', '上海 ').replace('上海市', '上海') },
    related() {
      return demoPets.filter(p => p.id !== this.pet.id && p.status === 0).slice(0, 4)
    },
    rescueStory() {
      const name = this.pet.name
      return `${name}刚到救助站的时候，对周围的一切都很警惕。志愿者每天固定时间来喂食、清理，也坐在不远处轻声说话。几天之后，它开始愿意靠近，甚至会主动蹭一蹭人的手。现在的${name}已经准备好了，只差一个愿意接纳它的家。`
    }
  },
  created() {
    document.title = '领养详情 · 暖爪之家'
    this.load()
  },
  methods: {
    ageText,
    async load() {
      const id = Number(this.$route.params.id)
      const fallback = demoPets.find(p => p.id === id) || demoPets[1]
      const data = await getData('/pet/detail/' + id, null, fallback)
      this.pet = data ? enrichPet(data) : fallback
      this.fromApi = !!(data && data.id)
      document.title = `${this.pet.name} · 等你带它回家`
    },
    openApply() {
      if (this.pet.status !== 0) return
      if (!requireLogin(this, this.$route.fullPath)) return
      this.step = 1
      this.applyVisible = true
    },
    submitApply() {
      this.$refs.applyForm.validate(async valid => {
        if (!valid) return
        if (!this.agree) return this.$message.warning('请先阅读并同意领养须知')
        this.submitting = true
        const payload = Object.assign({ petId: this.pet.id, status: 0 }, this.applyForm)
        await postData('/adopt', payload, { ok: true })
        this.submitting = false
        this.step = 2
        this.applyVisible = true
        this.$message.success('领养申请已提交')
      })
    },
    copyLink() {
      const url = window.location.href
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(url).then(() => this.$message.success('链接已复制')).catch(() => this.$message.info(url))
      } else {
        this.$message.info(url)
      }
    }
  }
}
</script>

<style scoped>
.detail-head { padding: 26px 0 8px; }
.detail-grid { display: grid; grid-template-columns: 1.15fr .85fr; gap: 54px; padding-top: 4px; }
.main-photo { position: relative; border-radius: 26px; overflow: hidden; aspect-ratio: 4 / 4.4; background: var(--cream-2); cursor: zoom-in; }
.main-photo img { width: 100%; height: 100%; object-fit: cover; }
.state-badge { position: absolute; top: 18px; left: 18px; display: inline-flex; align-items: center; gap: 6px; padding: 7px 14px; border-radius: 999px; background: rgba(255,255,255,.94); font-size: 12px; font-weight: 700; color: var(--sage-dark); box-shadow: 0 6px 18px rgba(0,0,0,.08); }
.state-badge.s-1 { color: #8a6a2a; }
.expand { position: absolute; right: 16px; bottom: 16px; width: 38px; height: 38px; border-radius: 50%; border: none; background: rgba(255,255,255,.9); color: var(--ink); display: flex; align-items: center; justify-content: center; cursor: pointer; }
.thumbs { display: flex; gap: 10px; margin-top: 12px; }
.thumbs button { width: 76px; height: 76px; padding: 0; border-radius: 13px; overflow: hidden; border: 2px solid transparent; background: none; cursor: pointer; opacity: .7; }
.thumbs button.active { border-color: var(--primary); opacity: 1; }
.thumbs img { width: 100%; height: 100%; object-fit: cover; }
.gallery-note { display: flex; gap: 10px; align-items: flex-start; margin-top: 18px; color: var(--muted); font-size: 11.5px; line-height: 1.7; }
.gallery-note span { font-size: 16px; }

.info { padding-top: 10px; }
.info-top { display: flex; justify-content: space-between; align-items: center; }
.share-btn { display: inline-flex; align-items: center; gap: 5px; border: none; background: transparent; color: var(--muted); font-size: 12px; cursor: pointer; }
.share-btn:hover { color: var(--primary); }
.info h1 { font-size: clamp(40px, 4vw, 56px); letter-spacing: -.05em; margin-top: 16px; }
.subline { color: var(--muted); font-size: 14px; margin-top: 6px; }
.key-facts { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-top: 26px; padding: 18px 0; border-top: 1px solid var(--line); border-bottom: 1px solid var(--line); }
.key-facts div { display: flex; flex-direction: column; gap: 4px; }
.key-facts span { font-size: 10.5px; color: var(--muted); letter-spacing: .05em; }
.key-facts strong { font-size: 13.5px; font-weight: 600; }
.health-chips { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 20px; }
.health-chips span { display: inline-flex; align-items: center; gap: 5px; font-size: 11px; color: var(--muted); }
.health-chips span i { width: 15px; height: 15px; border-radius: 50%; border: 1px solid var(--line-strong); display: inline-block; position: relative; }
.health-chips span.done { color: var(--sage-dark); }
.health-chips span.done i { background: var(--sage); border-color: var(--sage); }
.health-chips span.done i::after { content: '✓'; position: absolute; inset: 0; color: #fff; font-size: 9px; display: flex; align-items: center; justify-content: center; font-style: normal; }
.story-text { margin-top: 26px; }
.story-text h3 { font-size: 15px; }
.story-text p { font-size: 13px; color: var(--ink-2); line-height: 1.95; margin-top: 9px; }
.trait-row { display: flex; gap: 7px; flex-wrap: wrap; margin-top: 16px; }
.trait-row span { font-size: 11px; padding: 4px 10px; border-radius: 999px; background: var(--cream-2); color: var(--ink-2); }
.health-box { display: flex; gap: 12px; margin-top: 22px; padding: 14px 16px; background: var(--sage-soft); border-radius: 14px; }
.hb-icon { width: 30px; height: 30px; border-radius: 9px; background: var(--paper); color: var(--sage-dark); display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 14px; }
.health-box strong { font-size: 12px; color: var(--sage-dark); }
.health-box p { font-size: 11.5px; color: var(--sage-dark); opacity: .8; margin-top: 2px; }
.actions { margin-top: 26px; display: flex; flex-direction: column; gap: 10px; }
.fee-note { text-align: center; font-size: 10.5px; color: var(--muted-2); margin-top: 12px; }

.detail-lower { display: grid; grid-template-columns: 1fr 300px; gap: 26px; margin-top: 70px; }
.tabs-card { background: var(--paper); border: 1px solid var(--line); border-radius: 24px; overflow: hidden; }
.tab-head { display: flex; gap: 30px; padding: 0 30px; border-bottom: 1px solid var(--line); }
.tab-head button { position: relative; border: none; background: none; padding: 20px 0; font-size: 14px; color: var(--muted); cursor: pointer; }
.tab-head button.active { color: var(--ink); font-weight: 700; }
.tab-head button.active::after { content: ''; position: absolute; left: 0; right: 0; bottom: -1px; height: 2px; background: var(--primary); }
.tab-body { padding: 32px 34px 36px; }
.story-block h2 { font-size: 22px; letter-spacing: -.02em; }
.story-block > p { font-size: 13.5px; color: var(--ink-2); line-height: 2; margin-top: 14px; }
.timeline { margin-top: 30px; display: flex; flex-direction: column; gap: 22px; position: relative; padding-left: 20px; }
.timeline::before { content: ''; position: absolute; left: 4px; top: 6px; bottom: 6px; width: 1px; background: var(--line); }
.tl-item { position: relative; display: flex; gap: 12px; }
.tl-dot { position: absolute; left: -20px; top: 5px; width: 9px; height: 9px; border-radius: 50%; background: var(--paper); border: 2px solid var(--primary); }
.tl-item div { flex: 1; }
.tl-item strong { font-size: 13px; }
.tl-item p { font-size: 12px; color: var(--muted); margin-top: 3px; line-height: 1.7; }
.tl-date { font-size: 10px; color: var(--muted-2); }
.care-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.care-item { display: flex; gap: 14px; }
.care-icon { font-size: 22px; }
.care-item h3 { font-size: 14px; }
.care-item p { font-size: 12px; color: var(--muted); line-height: 1.8; margin-top: 4px; }
.faq { display: flex; flex-direction: column; gap: 22px; }
.faq-item h3 { font-size: 14px; }
.faq-item p { font-size: 12.5px; color: var(--muted); line-height: 1.85; margin-top: 6px; }

.side-card { background: var(--cream-2); border-radius: 24px; padding: 28px 26px; align-self: start; }
.side-card h3 { font-size: 16px; }
.side-steps { margin: 20px 0 24px; display: flex; flex-direction: column; gap: 14px; }
.side-steps div { display: flex; gap: 10px; align-items: flex-start; }
.side-steps span { width: 20px; height: 20px; border-radius: 50%; background: var(--paper); color: var(--primary); font-size: 10px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 2px; }
.side-steps p { font-size: 12px; color: var(--ink-2); line-height: 1.6; }
.contact-box { margin-top: 26px; padding-top: 20px; border-top: 1px solid var(--line-strong); display: flex; flex-direction: column; gap: 3px; }
.contact-box span { font-size: 10.5px; color: var(--muted); }
.contact-box strong { font-family: var(--font-display); font-size: 21px; letter-spacing: .01em; }
.contact-box small { font-size: 10px; color: var(--muted); }

.related { margin-top: 80px; padding-bottom: 20px; }
.related-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 22px; }
.apply-box h2, .success-box h2 { font-size: 26px; margin-top: 10px; letter-spacing: -.03em; }
.dialog-sub { font-size: 12.5px; color: var(--muted); margin: 8px 0 22px; }
.agree { font-size: 11.5px; color: var(--muted); line-height: 1.6; white-space: normal; margin-top: 4px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }
.success-box { text-align: center; padding: 10px 20px 18px; }
.success-icon { font-size: 48px; margin-bottom: 10px; }
.success-box p { font-size: 13px; color: var(--muted); line-height: 1.9; max-width: 420px; margin: 12px auto 0; }
.success-actions { display: flex; justify-content: center; gap: 10px; margin-top: 24px; }

@media (max-width: 980px) {
  .detail-grid { grid-template-columns: 1fr; gap: 30px; }
  .main-photo { aspect-ratio: 4 / 3.4; }
  .detail-lower { grid-template-columns: 1fr; }
  .related-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 560px) {
  .detail-grid { gap: 22px; }
  .key-facts { gap: 8px; }
  .tab-head { gap: 18px; padding: 0 20px; }
  .tab-body { padding: 24px 20px 28px; }
  .care-grid { grid-template-columns: 1fr; }
  .related-grid { grid-template-columns: 1fr 1fr; gap: 14px; }
  .gallery-note { display: none; }
}
</style>
