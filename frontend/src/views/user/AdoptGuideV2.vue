<template>
  <div class="guide-page">
    <section class="page-hero">
      <div class="container guide-hero">
        <div>
          <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>领养指南</span></div>
          <span class="eyebrow" style="margin-top:22px">ADOPTION GUIDE</span>
          <h1>在说“我愿意”之前，<br />先认真读完这一页。</h1>
          <p>领养不是把一只小动物带回家那么简单，而是承诺在接下来的十几年里，为另一个生命负责。我们希望每一份申请，都是想清楚之后的决定。</p>
          <div class="guide-actions">
            <router-link to="/pets" class="btn btn-primary">看看待领养的孩子</router-link>
            <a href="#checklist" class="btn btn-ghost">领取准备清单</a>
          </div>
        </div>
        <div class="guide-photo">
          <img src="/media/animals/scene-guide.jpg" alt="等待领养的小狗" />
          <div class="photo-tag"><strong>不是一时心动</strong><span>是一生的约定</span></div>
        </div>
      </div>
    </section>

    <section class="container guide-body">
      <aside class="toc">
        <span class="toc-label">本页内容</span>
        <a v-for="s in sections" :key="s.id" :href="'#' + s.id" :class="{ active: activeSection === s.id }">{{ s.title }}</a>
        <div class="help-card">
          <strong>还有疑问？</strong>
          <p>可以随时问问我们的 AI 助手，或者拨打救助热线。</p>
          <router-link to="/chat">问问助手 →</router-link>
        </div>
      </aside>

      <div class="guide-content">
        <section id="process" class="g-section">
          <span class="g-kicker">01 · PROCESS</span>
          <h2>领养流程</h2>
          <p class="g-intro">从提交申请到正式领养，通常需要 3–7 天。我们会尽量让每一步都清晰、透明。</p>
          <div class="process-list">
            <div v-for="(p, i) in process" :key="p.title" class="process-item">
              <span class="p-num">{{ String(i + 1).padStart(2, '0') }}</span>
              <div>
                <h3>{{ p.title }}</h3>
                <p>{{ p.text }}</p>
                <span class="p-time">{{ p.time }}</span>
              </div>
            </div>
          </div>
        </section>

        <section id="checklist" class="g-section">
          <span class="g-kicker">02 · CHECKLIST</span>
          <h2>接它回家之前</h2>
          <p class="g-intro">不需要一次买齐所有东西，但下面这些基础准备，会让它更快适应新家。</p>
          <div class="checklist">
            <label v-for="item in checklist" :key="item.title" class="check-item" :class="{ done: checked[item.title] }">
              <input v-model="checked[item.title]" type="checkbox" />
              <span class="check-box">{{ checked[item.title] ? '✓' : '' }}</span>
              <span class="check-copy"><strong>{{ item.title }}</strong><small>{{ item.text }}</small></span>
            </label>
          </div>
          <div class="progress-line">
            <span>准备进度</span>
            <div class="bar"><i :style="{ width: progress + '%' }"></i></div>
            <strong>{{ progress }}%</strong>
          </div>
        </section>

        <section id="space" class="g-section">
          <span class="g-kicker">03 · FIRST DAYS</span>
          <h2>给它一个温柔的适应期</h2>
          <p class="g-intro">刚到家的 3–7 天，是建立安全感最重要的时间。请把节奏放慢一点。</p>
          <div class="tips-grid">
            <div v-for="t in tips" :key="t.title" class="tip-card">
              <span class="tip-icon">{{ t.icon }}</span>
              <h3>{{ t.title }}</h3>
              <p>{{ t.text }}</p>
            </div>
          </div>
        </section>

        <section id="responsibility" class="g-section">
          <span class="g-kicker">04 · RESPONSIBILITY</span>
          <h2>领养意味着什么</h2>
          <div class="resp-list">
            <div v-for="r in responsibilities" :key="r.title">
              <span class="r-dot"></span>
              <div><h3>{{ r.title }}</h3><p>{{ r.text }}</p></div>
            </div>
          </div>
          <div class="warn-box">
            <strong>如果暂时做不到这些，也没关系。</strong>
            <p>你可以先成为志愿者、参与寄养或物资捐赠。等生活稳定下来，再来领养也不迟。对自己和动物都诚实，才是真正的温柔。</p>
          </div>
        </section>

        <section id="faq" class="g-section">
          <span class="g-kicker">05 · FAQ</span>
          <h2>常见问题</h2>
          <el-collapse v-model="openFaq" accordion class="faq-collapse">
            <el-collapse-item v-for="(f, i) in faqs" :key="i" :name="i">
              <template slot="title"><span class="faq-title">{{ f.q }}</span></template>
              <p class="faq-answer">{{ f.a }}</p>
            </el-collapse-item>
          </el-collapse>
        </section>

        <div class="end-cta">
          <div>
            <span class="eyebrow light">READY TO MEET?</span>
            <h2>如果你已经准备好了，<br />去看看它们吧。</h2>
          </div>
          <router-link to="/pets" class="btn btn-primary btn-lg">进入领养中心 →</router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'AdoptGuide',
  data() {
    return {
      activeSection: 'process',
      openFaq: 0,
      checked: {},
      sections: [
        { id: 'process', title: '领养流程' },
        { id: 'checklist', title: '准备清单' },
        { id: 'space', title: '适应期建议' },
        { id: 'responsibility', title: '需要承担的责任' },
        { id: 'faq', title: '常见问题' }
      ],
      process: [
        { title: '浏览档案并提交申请', text: '在领养中心找到你喜欢的它，阅读完整档案后填写申请。请真实填写居住情况和养宠经验。', time: '约 10 分钟' },
        { title: '志愿者沟通', text: '我们会在 1–2 个工作日内联系你，了解家庭成员意见、房屋条件和日常陪伴时间。', time: '1–2 天' },
        { title: '家访与见面', text: '安排一次线下见面，确认彼此的性格是否合适，并完成简单的居家安全检查。', time: '1–3 天' },
        { title: '签署协议，进入试养期', text: '签署领养协议后，带它回家开始 7–14 天试养。期间请定期上传照片或视频打卡。', time: '7–14 天' },
        { title: '正式领养与回访', text: '试养结束后，我们会进行回访。正式领养后，也欢迎你随时和我们分享它的近况。', time: '长期' }
      ],
      checklist: [
        { title: '安全的独立空间', text: '一个安静的房间或角落，远离电线、易碎品和有毒植物。' },
        { title: '合适的食物与水碗', text: '沿用救助站正在使用的粮，7 天内慢慢过渡，避免肠胃不适。' },
        { title: '猫砂盆或尿垫', text: '猫砂盆数量建议为猫的数量 + 1，放在安静、通风的位置。' },
        { title: '航空箱 / 牵引绳', text: '接它回家和外出就医都需要，提前让它在里面留下好印象。' },
        { title: '基础医疗预算', text: '预留疫苗、驱虫、绝育和突发就医的费用。' },
        { title: '家庭成员的同意', text: '确认同住的人都愿意接纳它，并了解可能出现的掉毛、叫声和破坏行为。' }
      ],
      tips: [
        { icon: '🕯️', title: '第一晚，少一点打扰', text: '不要频繁抱它或邀请朋友来看。把食物、水和厕所放好，让它自己探索。' },
        { icon: '🧺', title: '保留熟悉的气味', text: '向救助站要一件它用过的毯子或玩具，熟悉的气味能显著降低应激。' },
        { icon: '🚪', title: '先小范围活动', text: '前几天只开放一个房间，等它放松后再逐步扩大活动范围。' },
        { icon: '🩺', title: '一周内做健康检查', text: '带它去熟悉的医院建立档案，确认疫苗和驱虫计划。' }
      ],
      responsibilities: [
        { title: '十几年的陪伴', text: '猫狗的平均寿命在 12–18 年。搬家、结婚、生子、工作变动，都不是遗弃的理由。' },
        { title: '稳定的经济投入', text: '除了食物和用品，还需要定期疫苗、驱虫、绝育以及可能的医疗支出。' },
        { title: '科学养宠', text: '不喂人类剩饭，不散养，不放任繁殖。适龄绝育，定期体检。' },
        { title: '接受回访', text: '我们会在领养后定期回访，了解它的生活状况。这不是不信任，而是对生命负责。' }
      ],
      faqs: [
        { q: '领养需要交费吗？', a: '领养本身不收取任何费用。领养人需要承担宠物此后的日常开销和医疗费用。救助站不会以领养名义收取“押金”或“品种费”。' },
        { q: '租房可以领养吗？', a: '可以，但需要房东同意，并确保房屋允许养宠。我们会在沟通时了解居住稳定性，合租情况需要所有室友知情并同意。' },
        { q: '家里有小孩可以领养吗？', a: '可以。我们会优先匹配性格温和、有与儿童相处经验的动物，并建议家长教会孩子如何正确接触动物，全程有成人陪同。' },
        { q: '试养期不合适怎么办？', a: '试养期就是为了确认彼此是否合适。如果确实不合适，请及时联系救助站，我们会接回并重新评估，不会责怪你。' },
        { q: '可以指定领养某个品种吗？', a: '可以浏览品种筛选，但我们更鼓励你了解动物的性格与生活习惯。很多中华田园猫犬同样健康、聪明、亲人。' }
      ]
    }
  },
  computed: {
    progress() {
      const total = this.checklist.length
      const done = this.checklist.filter(i => this.checked[i.title]).length
      return Math.round(done / total * 100)
    }
  },
  created() { document.title = '领养指南 · 暖爪之家' },
  mounted() {
    window.addEventListener('scroll', this.onScroll, { passive: true })
  },
  beforeDestroy() { window.removeEventListener('scroll', this.onScroll) },
  methods: {
    onScroll() {
      let current = 'process'
      for (const s of this.sections) {
        const el = document.getElementById(s.id)
        if (el && el.getBoundingClientRect().top < 160) current = s.id
      }
      this.activeSection = current
    }
  }
}
</script>

<style scoped>
.guide-hero { display: grid; grid-template-columns: 1.05fr .95fr; gap: 60px; align-items: center; }
.guide-hero h1 { font-size: clamp(38px, 4.5vw, 60px); line-height: 1.2; margin-top: 16px; }
.guide-actions { display: flex; gap: 12px; margin-top: 28px; flex-wrap: wrap; }
.guide-photo { position: relative; border-radius: 160px 160px 26px 26px; overflow: hidden; height: 420px; box-shadow: var(--shadow); }
.guide-photo img { width: 100%; height: 100%; object-fit: cover; }
.photo-tag { position: absolute; left: 20px; right: 20px; bottom: 20px; background: rgba(255,253,249,.92); backdrop-filter: blur(10px); border-radius: 14px; padding: 12px 16px; display: flex; flex-direction: column; }
.photo-tag strong { font-size: 12px; }
.photo-tag span { font-size: 10.5px; color: var(--muted); }
.guide-body { display: grid; grid-template-columns: 220px 1fr; gap: 60px; padding-top: 60px; padding-bottom: 90px; }
.toc { position: sticky; top: 110px; align-self: start; display: flex; flex-direction: column; gap: 4px; }
.toc-label { font-size: 10px; letter-spacing: .15em; color: var(--muted); margin-bottom: 10px; }
.toc > a { font-size: 12.5px; color: var(--muted); padding: 8px 0 8px 14px; border-left: 1px solid var(--line); transition: all .2s; }
.toc > a.active { color: var(--primary); border-left: 2px solid var(--primary); font-weight: 600; }
.help-card { margin-top: 24px; background: var(--cream-2); border-radius: 14px; padding: 16px; }
.help-card strong { font-size: 12px; }
.help-card p { font-size: 10.5px; color: var(--muted); line-height: 1.7; margin-top: 4px; }
.help-card a { display: inline-block; font-size: 11px; color: var(--primary); margin-top: 8px; }
.g-section { padding-bottom: 58px; margin-bottom: 58px; border-bottom: 1px solid var(--line); scroll-margin-top: 100px; }
.g-section:last-of-type { border-bottom: none; }
.g-kicker { font-size: 9.5px; letter-spacing: .18em; color: var(--primary); }
.g-section h2 { font-size: 30px; letter-spacing: -.035em; margin-top: 10px; }
.g-intro { font-size: 13.5px; color: var(--muted); line-height: 1.9; margin-top: 10px; max-width: 620px; }
.process-list { margin-top: 30px; display: flex; flex-direction: column; gap: 0; }
.process-item { display: flex; gap: 22px; padding: 22px 0; border-top: 1px solid var(--line); }
.process-item:first-child { border-top: none; padding-top: 8px; }
.p-num { font-family: var(--font-display); font-size: 15px; color: var(--primary); padding-top: 2px; width: 26px; flex-shrink: 0; }
.process-item h3 { font-size: 16px; }
.process-item p { font-size: 12.5px; color: var(--muted); line-height: 1.85; margin-top: 6px; max-width: 620px; }
.p-time { display: inline-block; margin-top: 8px; font-size: 10px; color: var(--sage-dark); background: var(--sage-soft); border-radius: 999px; padding: 3px 9px; }
.checklist { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 26px; }
.check-item { display: flex; gap: 12px; align-items: flex-start; background: var(--paper); border: 1px solid var(--line); border-radius: 14px; padding: 16px; cursor: pointer; transition: all .2s; }
.check-item:hover { border-color: var(--line-strong); }
.check-item.done { background: var(--sage-soft); border-color: rgba(127,156,134,.3); }
.check-item input { display: none; }
.check-box { width: 20px; height: 20px; border-radius: 6px; border: 1px solid var(--line-strong); display: flex; align-items: center; justify-content: center; font-size: 11px; color: #fff; flex-shrink: 0; margin-top: 1px; }
.done .check-box { background: var(--sage); border-color: var(--sage); }
.check-copy { display: flex; flex-direction: column; }
.check-copy strong { font-size: 12.5px; }
.check-copy small { font-size: 10.5px; color: var(--muted); line-height: 1.7; margin-top: 3px; }
.progress-line { display: flex; align-items: center; gap: 14px; margin-top: 20px; font-size: 11px; color: var(--muted); }
.bar { flex: 1; height: 5px; background: var(--cream-2); border-radius: 99px; overflow: hidden; }
.bar i { display: block; height: 100%; background: var(--sage); transition: width .4s; }
.tips-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-top: 26px; }
.tip-card { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; padding: 22px; }
.tip-icon { font-size: 22px; }
.tip-card h3 { font-size: 14px; margin-top: 10px; }
.tip-card p { font-size: 11.5px; color: var(--muted); line-height: 1.8; margin-top: 6px; }
.resp-list { margin-top: 24px; display: flex; flex-direction: column; gap: 18px; }
.resp-list > div { display: flex; gap: 14px; }
.r-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--primary); margin-top: 7px; flex-shrink: 0; }
.resp-list h3 { font-size: 14px; }
.resp-list p { font-size: 12px; color: var(--muted); line-height: 1.8; margin-top: 3px; }
.warn-box { margin-top: 28px; background: var(--primary-soft); border-radius: 16px; padding: 20px 22px; }
.warn-box strong { font-size: 12.5px; color: var(--primary-dark); }
.warn-box p { font-size: 11.5px; color: var(--primary-dark); opacity: .8; line-height: 1.8; margin-top: 4px; }
.faq-collapse { margin-top: 20px; border: none; }
.faq-title { font-size: 13.5px; font-weight: 600; }
.faq-answer { font-size: 12.5px; color: var(--muted); line-height: 1.9; }
.end-cta { background: var(--ink); color: #fff; border-radius: 26px; padding: 42px 40px; display: flex; align-items: center; justify-content: space-between; gap: 30px; }
.end-cta h2 { color: #fff; font-size: 28px; line-height: 1.45; margin-top: 12px; }
.eyebrow.light { color: var(--amber); }

@media (max-width: 900px) {
  .guide-hero { grid-template-columns: 1fr; gap: 30px; }
  .guide-photo { height: 320px; }
  .guide-body { grid-template-columns: 1fr; gap: 30px; }
  .toc { position: static; flex-direction: row; overflow-x: auto; gap: 6px; padding-bottom: 6px; }
  .toc-label, .help-card { display: none; }
  .toc > a { white-space: nowrap; border-left: none; border-bottom: 1px solid var(--line); padding: 6px 10px; }
  .toc > a.active { border-bottom: 2px solid var(--primary); }
}
@media (max-width: 640px) {
  .checklist, .tips-grid { grid-template-columns: 1fr; }
  .end-cta { flex-direction: column; align-items: flex-start; padding: 30px 24px; }
  .g-section h2 { font-size: 25px; }
}
</style>
