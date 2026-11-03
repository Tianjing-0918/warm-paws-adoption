<template>
  <div class="lost-page">
    <section class="page-hero lost-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>寻宠寻主</span></div>
        <div class="hero-grid">
          <div>
            <span class="eyebrow">LOST & FOUND</span>
            <h1>如果你也在找它，<br />让我们一起找。</h1>
            <p>走失的每一小时都很重要。发布清晰的启事，留下准确的位置与特征，让附近的人也能帮你留意。</p>
            <div class="hero-actions">
              <button class="btn btn-primary" @click="openPublish('走失')">我家的宠物走失了</button>
              <button class="btn btn-ghost" @click="openPublish('捡到')">我捡到一只宠物</button>
            </div>
          </div>
          <div class="map-card">
            <div class="map-grid"></div>
            <div class="map-road r1"></div>
            <div class="map-road r2"></div>
            <div class="map-road r3"></div>
            <span v-for="(p, i) in mapPins" :key="i" class="map-pin" :class="'pin-' + i" :style="{ left: p.x, top: p.y }">
              <i></i><b>{{ p.label }}</b>
            </span>
            <div class="map-me"><span>◎</span> 你在这里</div>
            <button class="locate-btn" @click="locate">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/></svg>
              {{ locating ? '定位中…' : '使用我的位置' }}
            </button>
            <span class="map-note">示意地图 · 根据启事位置生成</span>
          </div>
        </div>
      </div>
    </section>

    <section class="container lost-body">
      <div class="lost-toolbar">
        <div class="chip-row">
          <button v-for="f in filters" :key="f.value" class="chip" :class="{ active: filter === f.value }" @click="filter = f.value">{{ f.label }}</button>
        </div>
        <div class="toolbar-right">
          <el-select v-model="petType" clearable placeholder="全部动物" size="small" class="type-select">
            <el-option label="猫咪" value="猫" />
            <el-option label="狗狗" value="狗" />
            <el-option label="其他" value="其他" />
          </el-select>
          <span class="count">{{ filtered.length }} 条启事</span>
        </div>
      </div>

      <div class="lost-grid">
        <article v-for="item in filtered" :key="item.id" class="lost-card" @click="openDetail(item)">
          <div class="lost-img">
            <img :src="firstImage(item.images)" :alt="item.title" loading="lazy" />
            <span class="type-badge" :class="item.type === '走失' ? 'lost' : 'found'">{{ item.type }}</span>
          </div>
          <div class="lost-info">
            <div class="card-top">
              <span class="pet-type">{{ item.petType }}</span>
              <span class="time">{{ relativeTime(item.createTime) }}</span>
            </div>
            <h3>{{ item.title }}</h3>
            <p class="desc">{{ item.description }}</p>
            <div class="loc-row">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.4"/></svg>
              {{ item.location }}
            </div>
            <div class="card-foot">
              <span class="contact">联系方式：{{ item.contact }}</span>
              <span class="detail-link">查看详情 →</span>
            </div>
          </div>
        </article>
      </div>
      <div v-if="!filtered.length" class="empty-state card"><div class="icon">🔎</div><p>这个筛选下暂时没有启事。</p></div>

      <div class="tips-row">
        <div class="tip-card">
          <span class="tip-num">01</span>
          <h3>第一时间做什么</h3>
          <p>先在走失地点 1 公里范围内仔细寻找，尤其是车底、绿化带、楼道角落。猫通常不会走远，但会躲藏。</p>
        </div>
        <div class="tip-card">
          <span class="tip-num">02</span>
          <h3>让信息更有效</h3>
          <p>写清时间、地点、毛色、体型、项圈和明显特征，附上最近的照片。不要只写“寻狗启事”。</p>
        </div>
        <div class="tip-card">
          <span class="tip-num">03</span>
          <h3>保护好自己</h3>
          <p>核实对方身份，尽量在公共场所见面。不要提前转账，也不要泄露完整住址等敏感信息。</p>
        </div>
      </div>
    </section>

    <el-dialog :title="form.type === '走失' ? '发布走失启事' : '发布捡到启事'" :visible.sync="publishVisible" width="600px">
      <el-form :model="form" label-position="top">
        <div class="type-toggle">
          <button :class="{ active: form.type === '走失' }" @click="form.type = '走失'">我的宠物走失了</button>
          <button :class="{ active: form.type === '捡到' }" @click="form.type = '捡到'">我捡到了一只</button>
        </div>
        <div class="form-grid">
          <el-form-item label="动物类型">
            <el-select v-model="form.petType" style="width:100%">
              <el-option label="猫咪" value="猫" /><el-option label="狗狗" value="狗" /><el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="毛色 / 品种"><el-input v-model="form.color" placeholder="例如：橘色虎斑" /></el-form-item>
        </div>
        <el-form-item label="启事标题"><el-input v-model="form.title" placeholder="例如：寻找走失的巧克力色泰迪" /></el-form-item>
        <el-form-item label="走失 / 发现地点"><el-input v-model="form.location" placeholder="尽量精确到小区、路口或地标" /></el-form-item>
        <el-form-item label="详细描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="时间、体型、项圈、性格、是否受伤，以及任何能帮助辨认的特征…" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.contact" placeholder="手机号或微信号（公开展示）" /></el-form-item>
        <el-form-item label="照片">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="onUpload" :before-upload="beforeUpload">
            <div class="upload-box">
              <img v-if="form.images" :src="form.images" alt="" />
              <template v-else><span>＋</span><small>上传照片</small></template>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">发布启事</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="detailVisible" width="600px">
      <div v-if="current" class="detail">
        <img :src="firstImage(current.images)" :alt="current.title" />
        <div class="detail-body">
          <span class="type-badge inline" :class="current.type === '走失' ? 'lost' : 'found'">{{ current.type }}</span>
          <h2>{{ current.title }}</h2>
          <p class="detail-loc">{{ current.location }} · {{ formatDate(current.createTime) }}</p>
          <p class="detail-desc">{{ current.description }}</p>
          <div class="contact-box">
            <span>请联系</span><strong>{{ current.contact }}</strong>
          </div>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyContact">复制联系方式</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, imageUrl, formatDate, relativeTime, requireLogin } from '../../utils/api'
import { demoLostPets } from '../../utils/demo'

export default {
  name: 'LostPet',
  data() {
    return {
      list: demoLostPets,
      filter: '全部',
      petType: '',
      filters: [
        { label: '全部启事', value: '全部' },
        { label: '走失', value: '走失' },
        { label: '捡到', value: '捡到' }
      ],
      publishVisible: false,
      detailVisible: false,
      current: null,
      locating: false,
      mapPins: [
        { x: '22%', y: '28%', label: '徐汇' },
        { x: '64%', y: '38%', label: '静安' },
        { x: '46%', y: '66%', label: '浦东' },
        { x: '78%', y: '72%', label: '杨浦' }
      ],
      form: { type: '走失', petType: '狗', title: '', color: '', location: '', description: '', contact: '', images: '' }
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.filter !== '全部') list = list.filter(i => i.type === this.filter)
      if (this.petType) list = list.filter(i => i.petType === this.petType)
      return list
    }
  },
  created() {
    document.title = '寻宠寻主 · 暖爪之家'
    this.load()
  },
  methods: {
    formatDate, relativeTime,
    async load() {
      const data = await getData('/lost/list', { pageNum: 1, pageSize: 20 }, { list: demoLostPets })
      if (data && data.list && data.list.length) this.list = data.list
    },
    firstImage(path) { return imageUrl(path) },
    openPublish(type) {
      if (!requireLogin(this, '/lost')) return
      this.form = { type, petType: '狗', title: '', color: '', location: '', description: '', contact: '', images: '' }
      this.publishVisible = true
    },
    locate() {
      if (!navigator.geolocation) return this.$message.warning('当前浏览器不支持定位')
      this.locating = true
      navigator.geolocation.getCurrentPosition(pos => {
        const { latitude, longitude } = pos.coords
        this.form.location = `已定位（${latitude.toFixed(4)}, ${longitude.toFixed(4)}），请补充小区或地标`
        this.locating = false
        this.$message.success('已获取当前位置，请补充详细地点')
      }, () => {
        this.locating = false
        this.$message.warning('定位失败，请手动填写地点')
      }, { timeout: 8000 })
    },
    beforeUpload(file) {
      const ok = /image\/(jpeg|png|gif|webp)/.test(file.type)
      if (!ok) this.$message.warning('请上传图片文件')
      return ok
    },
    onUpload(res) {
      if (res && res.code === 200) this.form.images = '/api' + res.data.url
      else this.$message.warning('上传失败，请检查后端服务')
    },
    async submit() {
      if (!this.form.title || !this.form.location || !this.form.contact) return this.$message.warning('请填写标题、地点和联系方式')
      await postData('/lost', Object.assign({ status: 0 }, this.form), { ok: true })
      this.list.unshift(Object.assign({}, this.form, {
        id: Date.now(), createTime: new Date().toISOString().slice(0, 19).replace('T', ' '), status: 0
      }))
      this.publishVisible = false
      this.$message.success('启事已发布，希望它早日回家')
    },
    openDetail(item) { this.current = item; this.detailVisible = true },
    copyContact() {
      const text = this.current.contact
      if (navigator.clipboard) navigator.clipboard.writeText(text).then(() => this.$message.success('联系方式已复制'))
      else this.$message.info(text)
    }
  }
}
</script>

<style scoped>
.lost-hero { padding-bottom: 48px; }
.hero-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: center; margin-top: 18px; }
.page-hero h1 { font-size: clamp(38px, 4.5vw, 58px); line-height: 1.2; }
.hero-actions { display: flex; gap: 12px; margin-top: 28px; flex-wrap: wrap; }
.map-card { position: relative; height: 330px; border-radius: 26px; overflow: hidden; background: #eef1e9; border: 1px solid var(--line); box-shadow: var(--shadow); }
.map-grid { position: absolute; inset: 0; background-image: linear-gradient(rgba(127,156,134,.12) 1px, transparent 1px), linear-gradient(90deg, rgba(127,156,134,.12) 1px, transparent 1px); background-size: 40px 40px; }
.map-road { position: absolute; background: #fbfaf4; border: 1px solid rgba(127,156,134,.15); }
.r1 { width: 130%; height: 26px; top: 40%; left: -10%; transform: rotate(-8deg); }
.r2 { width: 22px; height: 130%; left: 56%; top: -15%; transform: rotate(12deg); }
.r3 { width: 100%; height: 14px; top: 70%; left: 0; transform: rotate(4deg); }
.map-pin { position: absolute; display: flex; flex-direction: column; align-items: center; gap: 3px; }
.map-pin i { width: 12px; height: 12px; border-radius: 50% 50% 50% 0; background: var(--primary); transform: rotate(-45deg); box-shadow: 0 3px 8px rgba(201,102,63,.3); }
.map-pin b { font-size: 9px; font-weight: 500; color: var(--ink-2); background: rgba(255,253,249,.85); border-radius: 4px; padding: 1px 5px; }
.pin-1 i { background: var(--sage); }
.pin-3 i { background: var(--amber); }
.map-me { position: absolute; left: 18px; bottom: 16px; font-size: 10px; color: var(--sage-dark); background: rgba(255,253,249,.88); padding: 5px 10px; border-radius: 999px; }
.map-me span { color: var(--primary); }
.locate-btn { position: absolute; right: 16px; top: 16px; display: flex; align-items: center; gap: 5px; background: rgba(255,253,249,.94); border: 1px solid var(--line); border-radius: 999px; padding: 7px 12px; font-size: 10px; color: var(--ink-2); cursor: pointer; }
.locate-btn:hover { border-color: var(--primary); color: var(--primary); }
.map-note { position: absolute; right: 16px; bottom: 16px; font-size: 8.5px; color: var(--muted-2); }

.lost-body { padding-top: 34px; padding-bottom: 80px; }
.lost-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 20px; margin-bottom: 24px; }
.toolbar-right { display: flex; align-items: center; gap: 14px; }
.type-select { width: 120px; }
.count { font-size: 11px; color: var(--muted); }
.lost-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 22px; }
.lost-card { background: var(--paper); border: 1px solid var(--line); border-radius: 20px; overflow: hidden; cursor: pointer; transition: all .3s; }
.lost-card:hover { transform: translateY(-5px); box-shadow: var(--shadow); }
.lost-img { position: relative; aspect-ratio: 16/11; overflow: hidden; }
.lost-img img { width: 100%; height: 100%; object-fit: cover; transition: transform .6s; }
.lost-card:hover .lost-img img { transform: scale(1.05); }
.type-badge { position: absolute; top: 12px; left: 12px; font-size: 10px; font-weight: 700; padding: 4px 11px; border-radius: 999px; color: #fff; }
.type-badge.lost { background: var(--primary); }
.type-badge.found { background: var(--sage-dark); }
.type-badge.inline { position: static; display: inline-block; }
.lost-info { padding: 18px 20px 16px; }
.card-top { display: flex; justify-content: space-between; align-items: center; }
.pet-type { font-size: 9.5px; background: var(--cream-2); padding: 3px 8px; border-radius: 999px; color: var(--ink-2); }
.time { font-size: 9px; color: var(--muted-2); }
.lost-info h3 { font-size: 15px; line-height: 1.5; margin-top: 10px; }
.desc { font-size: 11px; color: var(--muted); line-height: 1.7; margin-top: 6px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.loc-row { display: flex; gap: 5px; align-items: center; font-size: 10px; color: var(--muted); margin-top: 10px; }
.loc-row svg { color: var(--primary); flex-shrink: 0; }
.card-foot { display: flex; justify-content: space-between; gap: 10px; margin-top: 14px; padding-top: 12px; border-top: 1px solid var(--line); }
.contact { font-size: 9.5px; color: var(--muted-2); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.detail-link { font-size: 10px; color: var(--primary); white-space: nowrap; }

.tips-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-top: 60px; }
.tip-card { border-top: 2px solid var(--ink); padding-top: 20px; }
.tip-num { font-family: var(--font-display); font-size: 11px; color: var(--primary); }
.tip-card h3 { font-size: 15px; margin-top: 6px; }
.tip-card p { font-size: 11.5px; color: var(--muted); line-height: 1.8; margin-top: 7px; }
.type-toggle { display: flex; gap: 6px; padding: 4px; background: var(--cream-2); border-radius: 10px; margin-bottom: 18px; }
.type-toggle button { flex: 1; height: 34px; border: none; background: transparent; border-radius: 7px; font-size: 11px; color: var(--muted); cursor: pointer; }
.type-toggle button.active { background: var(--paper); color: var(--ink); font-weight: 600; box-shadow: 0 2px 6px rgba(0,0,0,.05); }
.upload-box { width: 120px; height: 84px; border: 1px dashed var(--line-strong); border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); overflow: hidden; }
.upload-box img { width: 100%; height: 100%; object-fit: cover; }
.upload-box span { font-size: 18px; }
.upload-box small { font-size: 9px; }
.detail img { width: 100%; max-height: 320px; object-fit: cover; border-radius: 16px; }
.detail-body { padding-top: 16px; }
.detail-body h2 { font-size: 24px; margin-top: 10px; }
.detail-loc { font-size: 11px; color: var(--muted); margin-top: 6px; }
.detail-desc { font-size: 13px; color: var(--ink-2); line-height: 1.9; margin-top: 14px; }
.contact-box { display: flex; align-items: center; gap: 12px; margin-top: 20px; background: var(--cream-2); border-radius: 12px; padding: 14px 16px; }
.contact-box span { font-size: 10px; color: var(--muted); }
.contact-box strong { font-size: 15px; }

@media (max-width: 980px) {
  .hero-grid { grid-template-columns: 1fr; gap: 30px; }
  .lost-grid { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 680px) {
  .lost-toolbar { flex-direction: column; align-items: flex-start; }
  .lost-grid, .tips-row { grid-template-columns: 1fr; }
  .map-card { height: 260px; }
}
</style>
