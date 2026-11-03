<template>
  <div class="foster-page">
    <section class="page-hero foster-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>宠物寄养</span></div>
        <div class="hero-grid">
          <div>
            <span class="eyebrow">PET BOARDING</span>
            <h1>你安心出门，<br />它在这里被好好照顾。</h1>
            <p>出差、旅行或临时有事，把毛孩子交给我们。独立房间、每日清洁、定时喂食与健康记录，让它像在家一样安心。</p>
            <div class="hero-points">
              <span>✓ 独立猫舍 / 狗舍</span>
              <span>✓ 每日健康记录</span>
              <span>✓ 可自带粮食</span>
            </div>
          </div>
          <div class="hero-imgs">
            <img src="/images/pet6.jpg" alt="寄养猫咪" class="img-a" />
            <img src="/images/pet4.jpg" alt="寄养狗狗" class="img-b" />
            <div class="price-tag"><strong>¥38</strong><span>起 / 天</span></div>
          </div>
        </div>
      </div>
    </section>

    <section class="container foster-body">
      <div class="form-column">
        <div class="form-card">
          <div class="card-head">
            <div><span class="eyebrow">BOOK A STAY</span><h2>填写寄养申请</h2></div>
            <span class="step-note">提交后 1 个工作日内联系你</span>
          </div>
          <el-form ref="form" :model="form" :rules="rules" label-position="top">
            <div class="form-grid">
              <el-form-item label="宠物名字" prop="petName"><el-input v-model="form.petName" placeholder="它叫什么" /></el-form-item>
              <el-form-item label="品种" prop="categoryId">
                <el-select v-model="form.categoryId" placeholder="选择品种" style="width:100%">
                  <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="性别" prop="petGender">
                <el-radio-group v-model="form.petGender"><el-radio label="公">男孩</el-radio><el-radio label="母">女孩</el-radio></el-radio-group>
              </el-form-item>
              <el-form-item label="年龄（月）" prop="petAge"><el-input v-model.number="form.petAge" type="number" placeholder="例如：18" /></el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="入住日期" prop="startDate">
                <el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%" />
              </el-form-item>
              <el-form-item label="接回日期" prop="endDate">
                <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%" />
              </el-form-item>
            </div>
            <el-form-item label="特殊需求 / 备注">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="饮食习惯、性格、是否绝育、正在使用的药物，或其他需要照顾的地方…" maxlength="300" show-word-limit />
            </el-form-item>
            <el-checkbox v-model="agree" class="agree">我确认宠物已完成基础疫苗和驱虫，并同意寄养期间的日常照护安排。</el-checkbox>
            <el-button type="primary" class="submit-btn" :loading="submitting" @click="submit">提交寄养申请</el-button>
          </el-form>
        </div>

        <div class="records">
          <div class="section-head">
            <div><span class="eyebrow">MY RECORDS</span><h2 class="section-title">我的寄养记录</h2></div>
          </div>
          <div v-if="records.length" class="record-list">
            <div v-for="r in records" :key="r.id" class="record-card">
              <div class="rec-head">
                <div><strong>{{ r.petName }}</strong><span>{{ r.categoryName || '宠物' }} · {{ ageText(r.petAge) }}</span></div>
                <span class="pill" :class="statusClass(r.status)">{{ statusLabel(r.status) }}</span>
              </div>
              <div class="rec-info">
                <div><span>入住</span><strong>{{ r.startDate }}</strong></div>
                <div><span>接回</span><strong>{{ r.endDate }}</strong></div>
                <div><span>房间</span><strong>{{ r.roomNumber || '待分配' }}</strong></div>
              </div>
              <p v-if="r.description" class="rec-note">{{ r.description }}</p>
            </div>
          </div>
          <div v-else class="empty-state card"><div class="icon">🏠</div><p>还没有寄养记录。</p></div>
        </div>
      </div>

      <aside class="side-column">
        <div class="side-photo">
          <img src="/images/pet5.jpg" alt="寄养环境" />
          <div class="photo-caption"><strong>暖爪寄养间</strong><span>每天消毒 · 恒温 · 24 小时监控</span></div>
        </div>
        <div class="side-card">
          <h3>寄养包含</h3>
          <div v-for="s in services" :key="s.title" class="service-item">
            <span>{{ s.icon }}</span>
            <div><strong>{{ s.title }}</strong><p>{{ s.text }}</p></div>
          </div>
        </div>
        <div class="side-card room-card">
          <h3>房型参考</h3>
          <div class="room-row"><span>猫舍 · 标准间</span><strong>¥38 / 天</strong></div>
          <div class="room-row"><span>狗舍 · 标准间</span><strong>¥58 / 天</strong></div>
          <div class="room-row"><span>隔离观察间</span><strong>¥68 / 天</strong></div>
          <p class="room-note">多只宠物同时寄养可享 9 折。实际房间由管理员根据体型与性格分配。</p>
        </div>
        <div class="help-line">
          <span>有特殊需求？</span>
          <strong>400-123-4567</strong>
          <small>每天 09:00 – 21:00</small>
        </div>
      </aside>
    </section>
  </div>
</template>

<script>
import { getData, postData, requireLogin } from '../../utils/api'
import { demoCategories, demoFosters, ageText } from '../../utils/demo'

export default {
  name: 'Foster',
  data() {
    return {
      categories: demoCategories,
      records: demoFosters,
      agreeing: true,
      agree: true,
      submitting: false,
      form: { petName: '', categoryId: null, petGender: '母', petAge: null, startDate: '', endDate: '', description: '' },
      rules: {
        petName: [{ required: true, message: '请输入宠物名字', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择品种', trigger: 'change' }],
        startDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择接回日期', trigger: 'change' }]
      },
      services: [
        { icon: '🍽️', title: '定时喂食', text: '按你的习惯准备，也可以自带粮食。' },
        { icon: '🧹', title: '每日清洁', text: '房间消毒、猫砂更换、饮水 refresh。' },
        { icon: '📋', title: '健康记录', text: '每天记录精神、食欲和排泄情况。' },
        { icon: '🎾', title: '陪伴互动', text: '狗狗每天两次户外活动，猫咪有独立玩耍时间。' }
      ]
    }
  },
  created() {
    document.title = '宠物寄养 · 暖爪之家'
    this.agree = true
    this.load()
  },
  methods: {
    ageText,
    statusLabel(s) { return ['待处理', '已分配房间', '已完成', '已拒绝'][s] || '待确认' },
    statusClass(s) { return s === 1 ? 'pill' : s === 3 ? 'pill pill-danger' : 'pill pill-amber' },
    async load() {
      const cats = await getData('/category/list', null, demoCategories)
      if (cats && cats.length) this.categories = cats
      const data = await getData('/foster/my', { pageNum: 1, pageSize: 20 }, { list: demoFosters })
      if (data && data.list && data.list.length) this.records = data.list
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (!this.agree) return this.$message.warning('请确认寄养须知')
        if (this.form.endDate < this.form.startDate) return this.$message.warning('接回日期不能早于入住日期')
        if (!requireLogin(this, '/foster')) return
        this.submitting = true
        await postData('/foster', this.form, { ok: true })
        this.records.unshift(Object.assign({}, this.form, {
          id: Date.now(), status: 0, categoryName: (this.categories.find(c => c.id === this.form.categoryId) || {}).name, createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
        }))
        this.submitting = false
        this.$message.success('寄养申请已提交，管理员会尽快安排房间')
        this.form = { petName: '', categoryId: null, petGender: '母', petAge: null, startDate: '', endDate: '', description: '' }
      })
    }
  }
}
</script>

<style scoped>
.foster-hero { padding-bottom: 46px; }
.hero-grid { display: grid; grid-template-columns: 1.05fr .95fr; gap: 60px; align-items: center; margin-top: 16px; }
.page-hero h1 { font-size: clamp(38px, 4.5vw, 58px); line-height: 1.2; }
.hero-points { display: flex; gap: 18px; margin-top: 22px; flex-wrap: wrap; }
.hero-points span { font-size: 10.5px; color: var(--sage-dark); }
.hero-imgs { position: relative; height: 330px; }
.hero-imgs img { position: absolute; border-radius: 22px; object-fit: cover; border: 5px solid var(--paper); box-shadow: var(--shadow); }
.img-a { right: 0; top: 0; width: 72%; height: 270px; border-radius: 140px 140px 22px 22px !important; }
.img-b { left: 0; bottom: 0; width: 44%; height: 190px; }
.price-tag { position: absolute; right: 8%; bottom: 6px; background: var(--primary); color: #fff; border-radius: 14px; padding: 10px 16px; box-shadow: 0 12px 26px rgba(201,102,63,.3); }
.price-tag strong { font-family: var(--font-display); font-size: 24px; }
.price-tag span { font-size: 9px; opacity: .8; margin-left: 2px; }
.foster-body { display: grid; grid-template-columns: 1fr 330px; gap: 34px; padding-top: 38px; padding-bottom: 30px; align-items: start; }
.form-card { background: var(--paper); border: 1px solid var(--line); border-radius: 24px; padding: 30px 34px 34px; }
.card-head { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.card-head h2 { font-size: 26px; letter-spacing: -.03em; margin-top: 8px; }
.step-note { font-size: 9.5px; color: var(--muted-2); }
.agree { font-size: 11px; color: var(--muted); white-space: normal; line-height: 1.6; margin-top: 4px; }
.submit-btn { width: 100%; height: 46px; border-radius: 12px; margin-top: 18px; background: var(--primary); border-color: var(--primary); }
.records { margin-top: 46px; }
.record-list { display: flex; flex-direction: column; gap: 14px; }
.record-card { background: var(--paper); border: 1px solid var(--line); border-radius: 16px; padding: 20px 22px; }
.rec-head { display: flex; justify-content: space-between; align-items: center; }
.rec-head > div { display: flex; flex-direction: column; }
.rec-head strong { font-size: 15px; }
.rec-head span { font-size: 10px; color: var(--muted); }
.rec-info { display: flex; gap: 40px; margin-top: 14px; }
.rec-info div { display: flex; flex-direction: column; gap: 2px; }
.rec-info span { font-size: 9px; color: var(--muted-2); }
.rec-info strong { font-size: 11.5px; }
.rec-note { font-size: 11px; color: var(--muted); margin-top: 12px; padding-top: 10px; border-top: 1px solid var(--line); line-height: 1.7; }
.side-column { display: flex; flex-direction: column; gap: 16px; }
.side-photo { position: relative; height: 190px; border-radius: 20px; overflow: hidden; }
.side-photo img { width: 100%; height: 100%; object-fit: cover; }
.photo-caption { position: absolute; inset: auto 0 0 0; padding: 24px 18px 14px; background: linear-gradient(transparent, rgba(20,30,25,.75)); color: #fff; display: flex; flex-direction: column; }
.photo-caption strong { font-size: 12px; }
.photo-caption span { font-size: 9px; color: rgba(255,255,255,.65); }
.side-card { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; padding: 20px; }
.side-card h3 { font-size: 14px; margin-bottom: 14px; }
.service-item { display: flex; gap: 10px; padding: 8px 0; }
.service-item span { font-size: 15px; }
.service-item strong { font-size: 11.5px; }
.service-item p { font-size: 9.5px; color: var(--muted); line-height: 1.6; margin-top: 2px; }
.room-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--line); font-size: 11px; }
.room-row strong { color: var(--primary); font-weight: 600; }
.room-note { font-size: 9.5px; color: var(--muted); line-height: 1.7; margin-top: 10px; }
.help-line { background: var(--cream-2); border-radius: 16px; padding: 16px 18px; display: flex; flex-direction: column; }
.help-line span { font-size: 9.5px; color: var(--muted); }
.help-line strong { font-family: var(--font-display); font-size: 20px; margin-top: 2px; }
.help-line small { font-size: 9px; color: var(--muted-2); }

@media (max-width: 980px) {
  .hero-grid { grid-template-columns: 1fr; gap: 30px; }
  .hero-imgs { max-width: 500px; }
  .foster-body { grid-template-columns: 1fr; }
  .side-column { display: grid; grid-template-columns: 1fr 1fr; }
  .side-photo { grid-column: span 2; }
}
@media (max-width: 640px) {
  .form-card { padding: 24px 20px; }
  .card-head { flex-direction: column; gap: 6px; }
  .rec-info { gap: 22px; }
  .side-column { grid-template-columns: 1fr; }
  .side-photo { grid-column: span 1; }
}
</style>
