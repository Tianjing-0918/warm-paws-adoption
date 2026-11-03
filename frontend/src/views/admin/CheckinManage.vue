<template>
  <div>
    <div class="admin-page-head">
      <div><h1>试养打卡</h1><p>跟进试养期宠物的适应情况，及时发现异常</p></div>
      <div class="actions"><button class="a-btn" @click="load">刷新</button></div>
    </div>

    <div class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-top"><span>{{ s.label }}</span><span class="stat-icon" v-html="s.icon"></span></div>
        <div class="stat-val">{{ s.value }}</div>
        <div class="stat-foot">{{ s.note }}</div>
      </div>
    </div>

    <div class="checkin-layout">
      <div class="a-card">
        <div class="a-card-head">
          <div><h3>打卡记录</h3><span class="sub">最近 100 条</span></div>
          <el-input v-model="keyword" size="small" placeholder="搜索用户 / 宠物" clearable style="width:180px" />
        </div>
        <table class="a-table">
          <thead><tr><th>打卡日期</th><th>领养人</th><th>宠物</th><th>今日记录</th><th>照片</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="c in filtered" :key="c.id">
              <td><div class="date-cell"><strong>{{ (c.checkinDate || c.date || '').slice(8, 10) }}</strong><small>{{ (c.checkinDate || c.date || '').slice(5, 7) }} 月</small></div></td>
              <td>{{ c.username || '橘子汽水' }}</td>
              <td><span class="status-tag">{{ c.petName }}</span></td>
              <td><span class="content-cell">{{ c.content }}</span></td>
              <td><img v-if="c.images" :src="imageUrl(c.images)" class="thumb" alt="" /><span v-else class="no-img">—</span></td>
              <td><span class="status-tag ok">正常</span></td>
              <td><button class="a-link" @click="openDetail(c)">查看</button></td>
            </tr>
          </tbody>
        </table>
        <div v-if="!filtered.length" class="a-empty"><div class="icon">📷</div><p>暂无打卡记录</p></div>
      </div>

      <div class="side-col">
        <div class="a-card progress-card">
          <div class="a-card-head"><h3>试养进度</h3><span class="sub">进行中</span></div>
          <div class="trial-item">
            <div class="trial-head"><span>咪咪</span><strong>12 / 14 天</strong></div>
            <div class="bar"><i style="width: 86%"></i></div>
            <small>距离正式领养还有 2 天</small>
          </div>
          <div class="trial-item">
            <div class="trial-head"><span>雪球</span><strong>5 / 14 天</strong></div>
            <div class="bar"><i style="width: 36%"></i></div>
            <small>最近一次打卡：昨天 21:30</small>
          </div>
        </div>
        <div class="a-card tip-card">
          <div class="a-card-head"><h3>异常信号</h3></div>
          <div class="tip-list">
            <p><span>01</span>连续 2 天拒食、呕吐或腹泻</p>
            <p><span>02</span>持续躲藏、攻击行为明显加重</p>
            <p><span>03</span>伤口、皮肤问题或呼吸异常</p>
          </div>
          <div class="tip-foot">出现以上情况，请及时联系领养人并建议就医。</div>
        </div>
      </div>
    </div>

    <el-dialog title="打卡详情" :visible.sync="detailVisible" width="560px">
      <div v-if="current" class="checkin-detail">
        <div class="cd-head">
          <span class="pill">{{ current.petName }}</span>
          <strong>{{ current.checkinDate || current.date }}</strong>
          <span class="cd-user">{{ current.username || '橘子汽水' }}</span>
        </div>
        <img v-if="current.images" :src="imageUrl(current.images)" class="cd-img" alt="" />
        <p class="cd-content">{{ current.content }}</p>
        <div class="cd-meta"><span class="status-tag ok">状态正常</span><small>已同步至领养档案</small></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, imageUrl } from '../../utils/api'
import { demoCheckins } from '../../utils/demo'

export default {
  name: 'CheckinManage',
  data() {
    return {
      list: demoCheckins.map(c => Object.assign({ username: '橘子汽水' }, c)),
      keyword: '',
      detailVisible: false,
      current: null
    }
  },
  computed: {
    filtered() {
      if (!this.keyword) return this.list
      const k = this.keyword
      return this.list.filter(c => (c.username || '').indexOf(k) >= 0 || (c.petName || '').indexOf(k) >= 0)
    },
    stats() {
      const today = new Date().toISOString().slice(0, 10)
      return [
        { label: '今日打卡', value: this.list.filter(c => (c.checkinDate || c.date) === today).length, note: '含补录记录', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18M8 15l2 2 4-4"/></svg>' },
        { label: '试养中', value: 2, note: '2 只宠物在试养期', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="m3 11 9-7 9 7"/><path d="M5 10v10h14V10"/></svg>' },
        { label: '累计打卡', value: this.list.length, note: '照片与文字记录', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="9" cy="10" r="2"/><path d="m21 15-5-5L5 20"/></svg>' },
        { label: '需关注', value: 0, note: '暂无异常反馈', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M12 9v4M12 17h.01"/><path d="M10.3 3.9 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0Z"/></svg>' }
      ]
    }
  },
  created() {
    document.title = '试养打卡 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl,
    async load() {
      const data = await getData('/checkin/adminPage', null, null)
      if (data && data.length) this.list = data
    },
    openDetail(c) {
      this.current = c
      this.detailVisible = true
    }
  }
}
</script>

<style scoped>
.stat-icon { background: var(--sage-soft); color: var(--sage-dark); }
.checkin-layout { display: grid; grid-template-columns: 1fr 280px; gap: 14px; align-items: start; }
.date-cell { display: flex; flex-direction: column; align-items: center; background: var(--cream-2); border-radius: 8px; width: 38px; padding: 4px 0; }
.date-cell strong { font-family: var(--font-display); font-size: 15px; line-height: 1.1; }
.date-cell small { font-size: 7.5px; color: var(--muted); }
.content-cell { display: block; max-width: 270px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--muted); font-size: 10px; }
.no-img { color: var(--muted-2); }
.side-col { display: flex; flex-direction: column; gap: 14px; }
.trial-item { padding: 0 18px 16px; }
.trial-head { display: flex; justify-content: space-between; font-size: 10.5px; margin-bottom: 7px; }
.trial-head strong { color: var(--primary); font-weight: 600; }
.bar { height: 6px; background: var(--cream-2); border-radius: 99px; overflow: hidden; }
.bar i { display: block; height: 100%; background: var(--sage); border-radius: 99px; }
.trial-item small { display: block; font-size: 8.5px; color: var(--muted); margin-top: 5px; }
.tip-list { padding: 8px 18px; }
.tip-list p { font-size: 9.5px; color: var(--muted); line-height: 1.7; margin: 7px 0; }
.tip-list span { color: var(--primary); font-family: var(--font-display); margin-right: 6px; }
.tip-foot { margin: 0 18px 16px; padding: 9px 11px; background: #fbf0dc; color: #8a6a2a; font-size: 8.5px; border-radius: 8px; line-height: 1.6; }
.cd-head { display: flex; align-items: center; gap: 10px; font-size: 11px; }
.cd-user { margin-left: auto; color: var(--muted); font-size: 9.5px; }
.cd-img { width: 100%; max-height: 280px; object-fit: cover; border-radius: 12px; margin-top: 14px; }
.cd-content { font-size: 12.5px; line-height: 1.9; color: var(--ink-2); margin-top: 14px; }
.cd-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 16px; }
.cd-meta small { font-size: 9px; color: var(--muted-2); }
@media (max-width: 1000px) {
  .checkin-layout { grid-template-columns: 1fr; }
  .side-col { display: grid; grid-template-columns: 1fr 1fr; }
}
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 760px; }
  .side-col { grid-template-columns: 1fr; }
}
</style>
