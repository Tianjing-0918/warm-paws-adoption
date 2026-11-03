<template>
  <div class="report-page">
    <section class="page-hero report-hero">
      <div class="container hero-grid">
        <div>
          <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>流浪上报</span></div>
          <span class="eyebrow">STREET RESCUE</span>
          <h1>你多看一眼，<br />它可能就多一次机会。</h1>
          <p>发现流浪、受伤或被遗弃的动物时，请尽量停留在安全距离，记录位置和状态。我们会尽快联系志愿者跟进。</p>
          <div class="hero-steps">
            <span><b>1</b> 拍下清晰照片</span>
            <span><b>2</b> 填写发现地点</span>
            <span><b>3</b> 等待志愿者联系</span>
          </div>
        </div>
        <div class="hero-visual">
          <img src="/media/animals/scene-report.jpg" alt="流浪橘猫" />
          <div class="urgent-card">
            <span class="urgent-dot"></span>
            <div><strong>紧急情况？</strong><small>请直接拨打 400-123-4567</small></div>
          </div>
          <div class="quote-tag">“它可能只是走丢了，<br />也正在等有人发现它。”</div>
        </div>
      </div>
    </section>

    <section class="container report-body">
      <div class="form-card">
        <div class="card-head">
          <div><span class="eyebrow">REPORT A STRAY</span><h2>上报流浪动物</h2></div>
          <span class="privacy">信息仅用于救助跟进</span>
        </div>
        <el-form ref="form" :model="form" :rules="rules" label-position="top">
          <el-form-item label="发现地点" prop="location">
            <div class="loc-input">
              <el-input v-model="form.location" placeholder="例如：徐汇区田林路 xx 小区北门" />
              <button type="button" class="loc-btn" @click="locate">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/></svg>
                {{ locating ? '定位中' : '定位' }}
              </button>
            </div>
          </el-form-item>
          <el-form-item label="现场情况" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="5" placeholder="动物种类、大小、毛色、是否受伤、精神状态、周围环境，以及你是否能暂时看护…" maxlength="500" show-word-limit />
          </el-form-item>
          <div class="form-grid">
            <el-form-item label="你的联系方式" prop="contact">
              <el-input v-model="form.contact" placeholder="手机号或微信号" />
            </el-form-item>
            <el-form-item label="可提供的帮助">
              <el-select v-model="helpType" style="width:100%">
                <el-option label="可以暂时看护" value="暂时看护" />
                <el-option label="可以协助送医" value="协助送医" />
                <el-option label="只能提供信息" value="提供信息" />
              </el-select>
            </el-form-item>
          </div>
          <el-form-item label="现场照片（最多 6 张）">
            <div class="upload-area">
              <div v-for="(img, i) in images" :key="i" class="preview">
                <img :src="img" alt="" />
                <button @click="removeImage(i)">×</button>
              </div>
              <el-upload v-if="images.length < 6" action="/api/upload" :show-file-list="false" :on-success="onUpload" :before-upload="beforeUpload" :disabled="uploading">
                <div class="upload-box"><span>{{ uploading ? '…' : '＋' }}</span><small>添加照片</small></div>
              </el-upload>
            </div>
            <p class="upload-tip">请保持安全距离拍摄。不要为了拍照惊扰或追赶动物。</p>
          </el-form-item>
          <el-button type="primary" class="submit-btn" :loading="submitting" @click="submit">提交上报</el-button>
        </el-form>
      </div>

      <aside class="records">
        <div class="records-head">
          <h3>我的上报记录</h3>
          <span>{{ records.length }} 条</span>
        </div>
        <div v-if="records.length" class="record-list">
          <div v-for="r in records" :key="r.id" class="record-card">
            <div class="rec-top">
              <span class="pill" :class="statusClass(r.status)">{{ statusLabel(r.status) }}</span>
              <span class="rec-date">{{ formatDate(r.createTime) }}</span>
            </div>
            <div class="rec-body">
              <img :src="imageUrl(r.images)" alt="" />
              <div>
                <strong>{{ r.location }}</strong>
                <p>{{ r.description }}</p>
              </div>
            </div>
            <div v-if="r.reply" class="reply"><strong>救助站回复</strong><p>{{ r.reply }}</p></div>
            <div class="rec-track">
              <span class="done">已提交</span><i></i><span :class="{ done: r.status >= 1 }">志愿者跟进</span><i></i><span :class="{ done: r.status === 1 }">已处理</span>
            </div>
          </div>
        </div>
        <div v-else class="empty-state"><div class="icon">📮</div><p>暂未上报过流浪动物。</p></div>

        <div class="safety-box">
          <h4>救助安全提醒</h4>
          <p>不要徒手接触陌生动物；不要强行追赶；如动物受伤严重，请先联系附近医院或救助热线，并在安全距离内等待。</p>
        </div>
      </aside>
    </section>
  </div>
</template>

<script>
import { getData, postData, imageUrl, formatDate, requireLogin } from '../../utils/api'
import { demoReports } from '../../utils/demo'

export default {
  name: 'Report',
  data() {
    return {
      form: { location: '', description: '', contact: '' },
      helpType: '暂时看护',
      images: [],
      uploading: false,
      submitting: false,
      locating: false,
      records: demoReports,
      rules: {
        location: [{ required: true, message: '请填写发现地点', trigger: 'blur' }],
        description: [{ required: true, message: '请描述现场情况', trigger: 'blur' }, { min: 10, message: '请再多描述一点，至少 10 个字', trigger: 'blur' }],
        contact: [{ required: true, message: '请留下联系方式', trigger: 'blur' }]
      }
    }
  },
  created() {
    document.title = '流浪上报 · 暖爪之家'
    this.load()
  },
  methods: {
    imageUrl, formatDate,
    statusLabel(s) { return ['待处理', '已处理', '已拒绝'][s] || '待处理' },
    statusClass(s) { return s === 1 ? 'pill' : s === 2 ? 'pill pill-danger' : 'pill pill-amber' },
    async load() {
      const data = await getData('/report/my', { pageNum: 1, pageSize: 20 }, { list: demoReports })
      if (data && data.list && data.list.length) this.records = data.list
    },
    locate() {
      if (!navigator.geolocation) return this.$message.warning('当前浏览器不支持定位')
      this.locating = true
      navigator.geolocation.getCurrentPosition(pos => {
        this.form.location = `已定位（${pos.coords.latitude.toFixed(4)}, ${pos.coords.longitude.toFixed(4)}），请补充具体位置`
        this.locating = false
      }, () => {
        this.locating = false
        this.$message.warning('定位失败，请手动填写')
      }, { timeout: 8000 })
    },
    beforeUpload(file) {
      const ok = /image\/(jpeg|png|gif|webp)/.test(file.type)
      if (!ok) this.$message.warning('请上传图片文件')
      this.uploading = ok
      return ok
    },
    onUpload(res) {
      this.uploading = false
      if (res && res.code === 200) this.images.push('/api' + res.data.url)
      else {
        this.$message.warning('图片上传失败，已使用本地预览')
        this.images.push('/media/animals/scene-report.jpg')
      }
    },
    removeImage(i) { this.images.splice(i, 1) },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (!requireLogin(this, '/report')) return
        this.submitting = true
        const payload = Object.assign({}, this.form, {
          images: this.images.join(','),
          description: this.form.description + '\n可提供帮助：' + this.helpType
        })
        await postData('/report', payload, { ok: true })
        this.records.unshift(Object.assign({}, payload, {
          id: Date.now(), status: 0, createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
        }))
        this.submitting = false
        this.$message.success('上报成功，志愿者会尽快跟进')
        this.form = { location: '', description: '', contact: '' }
        this.images = []
      })
    }
  }
}
</script>

<style scoped>
.report-hero { padding-bottom: 46px; }
.hero-grid { display: grid; grid-template-columns: 1.05fr .95fr; gap: 60px; align-items: center; }
.page-hero h1 { font-size: clamp(38px, 4.4vw, 56px); line-height: 1.2; margin-top: 12px; }
.hero-steps { display: flex; gap: 22px; margin-top: 26px; flex-wrap: wrap; }
.hero-steps span { display: flex; align-items: center; gap: 7px; font-size: 10.5px; color: var(--muted); }
.hero-steps b { width: 20px; height: 20px; border-radius: 50%; background: var(--paper); border: 1px solid var(--line); display: flex; align-items: center; justify-content: center; font-size: 9px; color: var(--primary); }
.hero-visual { position: relative; height: 350px; border-radius: 160px 160px 24px 24px; overflow: visible; }
.hero-visual > img { width: 100%; height: 100%; object-fit: cover; border-radius: inherit; box-shadow: var(--shadow); }
.urgent-card { position: absolute; left: -20px; bottom: 34px; display: flex; align-items: center; gap: 10px; background: rgba(255,253,249,.95); border-radius: 14px; padding: 12px 16px; box-shadow: var(--shadow); }
.urgent-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--primary); box-shadow: 0 0 0 4px var(--primary-soft); }
.urgent-card div { display: flex; flex-direction: column; }
.urgent-card strong { font-size: 11px; }
.urgent-card small { font-size: 9px; color: var(--muted); }
.quote-tag { position: absolute; right: -14px; top: 40px; background: var(--sage-dark); color: #fff; border-radius: 12px; padding: 10px 14px; font-size: 10px; line-height: 1.6; box-shadow: var(--shadow-sm); }

.report-body { display: grid; grid-template-columns: 1fr 360px; gap: 30px; padding-top: 38px; padding-bottom: 30px; align-items: start; }
.form-card { background: var(--paper); border: 1px solid var(--line); border-radius: 24px; padding: 30px 34px 34px; }
.card-head { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 22px; }
.card-head h2 { font-size: 26px; letter-spacing: -.03em; margin-top: 8px; }
.privacy { font-size: 9px; color: var(--muted-2); }
.loc-input { display: flex; gap: 10px; width: 100%; }
.loc-input .el-input { flex: 1; }
.loc-btn { display: flex; align-items: center; gap: 5px; border: 1px solid var(--line); background: var(--cream); border-radius: 10px; padding: 0 12px; font-size: 10px; color: var(--ink-2); cursor: pointer; white-space: nowrap; }
.loc-btn:hover { border-color: var(--primary); color: var(--primary); }
.upload-area { display: flex; gap: 10px; flex-wrap: wrap; }
.preview { position: relative; width: 88px; height: 88px; border-radius: 10px; overflow: hidden; }
.preview img { width: 100%; height: 100%; object-fit: cover; }
.preview button { position: absolute; right: 4px; top: 4px; width: 18px; height: 18px; border: none; border-radius: 50%; background: rgba(0,0,0,.5); color: #fff; font-size: 11px; cursor: pointer; }
.upload-box { width: 88px; height: 88px; border: 1px dashed var(--line-strong); border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); cursor: pointer; }
.upload-box span { font-size: 18px; }
.upload-box small { font-size: 8.5px; }
.upload-tip { font-size: 9px; color: var(--muted-2); margin-top: 6px; }
.submit-btn { width: 100%; height: 46px; border-radius: 12px; background: var(--primary); border-color: var(--primary); margin-top: 6px; }

.records { display: flex; flex-direction: column; gap: 14px; }
.records-head { display: flex; justify-content: space-between; align-items: baseline; }
.records-head h3 { font-size: 16px; }
.records-head span { font-size: 9px; color: var(--muted-2); }
.record-card { background: var(--paper); border: 1px solid var(--line); border-radius: 16px; padding: 16px; }
.rec-top { display: flex; justify-content: space-between; align-items: center; }
.rec-date { font-size: 9px; color: var(--muted-2); }
.rec-body { display: flex; gap: 10px; margin-top: 10px; }
.rec-body img { width: 62px; height: 62px; border-radius: 9px; object-fit: cover; flex-shrink: 0; }
.rec-body strong { font-size: 11px; display: block; line-height: 1.5; }
.rec-body p { font-size: 9.5px; color: var(--muted); line-height: 1.7; margin-top: 3px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.reply { margin-top: 10px; padding: 9px 11px; background: var(--sage-soft); border-radius: 8px; }
.reply strong { font-size: 9px; color: var(--sage-dark); }
.reply p { font-size: 9.5px; color: var(--sage-dark); line-height: 1.6; margin-top: 2px; }
.rec-track { display: flex; align-items: center; gap: 6px; margin-top: 12px; font-size: 8px; color: var(--muted-2); }
.rec-track span.done { color: var(--sage-dark); font-weight: 600; }
.rec-track i { flex: 1; height: 1px; background: var(--line); }
.safety-box { background: #fbf0dc; border-radius: 14px; padding: 16px 18px; }
.safety-box h4 { font-size: 11px; color: #8a6a2a; }
.safety-box p { font-size: 9.5px; color: #8a6a2a; opacity: .8; line-height: 1.7; margin-top: 4px; }

@media (max-width: 980px) {
  .hero-grid { grid-template-columns: 1fr; gap: 30px; }
  .hero-visual { max-width: 520px; height: 300px; }
  .report-body { grid-template-columns: 1fr; }
}
@media (max-width: 600px) {
  .form-card { padding: 24px 20px; }
  .card-head { flex-direction: column; gap: 6px; }
  .hero-visual { height: 260px; }
  .quote-tag { display: none; }
}
</style>
