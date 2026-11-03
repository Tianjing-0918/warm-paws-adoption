<template>
  <div class="dashboard">
    <div class="admin-page-head">
      <div>
        <h1>{{ greeting }}，{{ displayName }}</h1>
        <p>{{ today }} · 今天救助站有 {{ stats.adoptPending || 12 }} 份领养申请等待处理</p>
      </div>
      <div class="actions">
        <button class="a-btn" @click="refresh">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M21 12a9 9 0 1 1-3-6.7L21 8"/><path d="M21 3v5h-5"/></svg>
          刷新数据
        </button>
        <router-link to="/admin/pets" class="a-btn primary">＋ 添加动物</router-link>
      </div>
    </div>

    <div class="stat-grid">
      <div v-for="s in statCards" :key="s.label" class="stat-card">
        <div class="stat-top">
          <span>{{ s.label }}</span>
          <span class="stat-icon" :class="s.tone" v-html="s.icon"></span>
        </div>
        <div class="stat-val">{{ s.value }}</div>
        <div class="stat-foot"><em>{{ s.delta }}</em> {{ s.note }}</div>
      </div>
    </div>

    <div class="dash-grid top-grid">
      <div class="a-card trend-card">
        <div class="a-card-head">
          <div><h3>救助与领养趋势</h3><span class="sub">近 7 天申请与上报数量</span></div>
          <div class="legend">
            <span><i class="dot c1"></i>领养申请</span>
            <span><i class="dot c2"></i>救助上报</span>
          </div>
        </div>
        <div ref="trendChart" class="chart trend-chart"></div>
      </div>
      <div class="a-card">
        <div class="a-card-head"><div><h3>动物状态分布</h3><span class="sub">当前在站动物</span></div></div>
        <div ref="pieChart" class="chart pie-chart"></div>
      </div>
    </div>

    <div class="dash-grid bottom-grid">
      <div class="a-card">
        <div class="a-card-head"><div><h3>品种分布</h3><span class="sub">按品种统计在站动物</span></div></div>
        <div ref="barChart" class="chart bar-chart"></div>
      </div>
      <div class="a-card pending-card">
        <div class="a-card-head"><div><h3>待办事项</h3><span class="sub">需要你关注</span></div></div>
        <div class="pending-list">
          <router-link v-for="p in pendingItems" :key="p.title" :to="p.to" class="pending-item">
            <span class="p-icon" :class="p.tone" v-html="p.icon"></span>
            <div><strong>{{ p.title }}</strong><small>{{ p.sub }}</small></div>
            <span class="p-count">{{ p.count }}</span>
          </router-link>
        </div>
        <div class="notice-mini">
          <span class="nm-title">最新公告</span>
          <p>{{ notices[0] ? notices[0].title : '暂无公告' }}</p>
          <router-link to="/admin/notices">管理公告 →</router-link>
        </div>
      </div>
    </div>

    <div class="a-card recent-card">
      <div class="a-card-head">
        <div><h3>最近领养申请</h3><span class="sub">按提交时间倒序</span></div>
        <router-link to="/admin/adopt" class="a-btn sm">查看全部</router-link>
      </div>
      <table class="a-table">
        <thead><tr><th>申请人</th><th>宠物</th><th>居住地</th><th>提交时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in adoptions" :key="a.id">
            <td><div class="main-cell"><span class="mini-avatar">{{ (a.username || '用').slice(0, 1) }}</span><strong>{{ a.username }}</strong></div></td>
            <td><div class="main-cell"><img :src="imageUrl(a.petImage)" class="thumb" /><strong>{{ a.petName }}</strong></div></td>
            <td>{{ a.address }}</td>
            <td>{{ formatDate(a.createTime) }}</td>
            <td><span class="status-tag" :class="a.status === 0 ? 'warn' : a.status === 1 ? 'ok' : 'danger'">{{ ['待审核', '已通过', '已拒绝'][a.status] }}</span></td>
            <td><router-link to="/admin/adopt" class="a-link">去审核</router-link></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getData, imageUrl, formatDate } from '../../utils/api'
import { demoStats, demoAdminAdoptions } from '../../utils/demo'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: Object.assign({}, demoStats),
      adoptions: demoAdminAdoptions.slice(0, 4),
      notices: demoStats.notices,
      charts: []
    }
  },
  computed: {
    displayName() { return this.$store.getters.displayName },
    greeting() {
      const h = new Date().getHours()
      return h < 11 ? '早上好' : h < 14 ? '中午好' : h < 18 ? '下午好' : '晚上好'
    },
    today() {
      const d = new Date()
      const week = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()]
      return `${d.getFullYear()} 年 ${d.getMonth() + 1} 月 ${d.getDate()} 日 · 星期${week}`
    },
    statCards() {
      return [
        { label: '在站动物', value: this.stats.petAtStation || 0, delta: '实时', note: '当前在站', tone: 't1', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><ellipse cx="8" cy="9" rx="2" ry="3"/><ellipse cx="16" cy="9" rx="2" ry="3"/><ellipse cx="5" cy="15" rx="1.6" ry="2.4"/><ellipse cx="19" cy="15" rx="1.6" ry="2.4"/><path d="M12 13c3 0 5 2 5 4.2 0 1.6-1.6 2.8-5 2.8s-5-1.2-5-2.8C7 15 9 13 12 13Z"/></svg>' },
        { label: '成功领养', value: this.stats.petAdopted || 0, delta: '+' + (this.stats.petAdoptedThisMonth || 0), note: '本月', tone: 't2', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>' },
        { label: '注册用户', value: this.stats.userTotal || 0, delta: '累计', note: '全部注册用户', tone: 't3', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0M17 5.5a3.5 3.5 0 0 1 0 7M18.5 14.5A5.5 5.5 0 0 1 22 20"/></svg>' },
        { label: '待处理申请', value: this.stats.adoptPending || 0, delta: '需关注', note: '当前待审核', tone: 't4', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>' }
      ]
    },
    pendingItems() {
      return [
        { title: '领养申请待审核', sub: '最近一份来自夏天的风', count: this.stats.adoptPending || 12, to: '/admin/adopt', tone: 'warm', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>' },
        { title: '流浪上报待处理', sub: '五角场 · 三只幼猫', count: this.stats.reportPending || 8, to: '/admin/report', tone: 'sage', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.4"/></svg>' },
        { title: '寄养待分配房间', sub: '国庆假期寄养申请', count: this.stats.fosterPending || 5, to: '/admin/foster', tone: 'blue', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="m3 11 9-7 9 7"/><path d="M5 10v10h14V10"/></svg>' }
      ]
    }
  },
  created() {
    document.title = '数据概览 · 暖爪管理后台'
    this.load()
  },
  mounted() {
    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize)
    this.charts.forEach(c => c && c.dispose())
  },
  methods: {
    imageUrl, formatDate,
    async load() {
      const data = await getData('/dashboard/stats', null, demoStats)
      this.stats = Object.assign({}, demoStats, data || {})
      if (this.stats.notices && this.stats.notices.length) this.notices = this.stats.notices
      const adopt = await getData('/adopt/adminPage', { pageNum: 1, pageSize: 5, status: 0 }, { list: demoAdminAdoptions.slice(0, 4) })
      if (adopt && adopt.list && adopt.list.length) this.adoptions = adopt.list.slice(0, 4)
      this.$nextTick(this.initCharts)
    },
    refresh() {
      this.$message.success('数据已刷新')
      this.load()
    },
    resize() { this.charts.forEach(c => c && c.resize()) },
    initCharts() {
      this.charts.forEach(c => c && c.dispose())
      this.charts = []
      this.initTrend()
      this.initPie()
      this.initBar()
    },
    initTrend() {
      const el = this.$refs.trendChart
      if (!el) return
      const chart = echarts.init(el)
      const trend = this.stats.trend || demoStats.trend
      chart.setOption({
        grid: { left: 34, right: 18, top: 24, bottom: 28 },
        tooltip: { trigger: 'axis', backgroundColor: '#1f2f28', borderWidth: 0, textStyle: { color: '#fff', fontSize: 11 } },
        xAxis: { type: 'category', boundaryGap: false, data: trend.map(t => t.date), axisLine: { lineStyle: { color: '#ebe2d4' } }, axisTick: { show: false }, axisLabel: { color: '#9aa79f', fontSize: 10 } },
        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1ece3' } }, axisLabel: { color: '#9aa79f', fontSize: 10 } },
        series: [
          {
            name: '领养申请', type: 'line', smooth: true, symbolSize: 6,
            data: trend.map(t => t.adopt),
            lineStyle: { color: '#c9663f', width: 2.5 },
            itemStyle: { color: '#c9663f', borderColor: '#fff', borderWidth: 2 },
            areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(201,102,63,.22)' }, { offset: 1, color: 'rgba(201,102,63,0)' }]) }
          },
          {
            name: '救助上报', type: 'line', smooth: true, symbolSize: 6,
            data: trend.map(t => t.report),
            lineStyle: { color: '#7f9c86', width: 2.5 },
            itemStyle: { color: '#7f9c86', borderColor: '#fff', borderWidth: 2 },
            areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(127,156,134,.16)' }, { offset: 1, color: 'rgba(127,156,134,0)' }]) }
          }
        ]
      })
      this.charts.push(chart)
    },
    initPie() {
      const el = this.$refs.pieChart
      if (!el) return
      const chart = echarts.init(el)
      const data = [
        { value: this.stats.petAvailable || 46, name: '可领养', itemStyle: { color: '#c9663f' } },
        { value: this.stats.petAdopted || 72, name: '已领养', itemStyle: { color: '#7f9c86' } },
        { value: this.stats.petFoster || 7, name: '寄养中', itemStyle: { color: '#e5b268' } },
        { value: this.stats.petPending || 3, name: '待处理', itemStyle: { color: '#c9c3b8' } }
      ]
      chart.setOption({
        tooltip: { trigger: 'item', backgroundColor: '#1f2f28', borderWidth: 0, textStyle: { color: '#fff', fontSize: 11 } },
        legend: { bottom: 4, icon: 'circle', itemWidth: 7, itemHeight: 7, textStyle: { color: '#7b8b82', fontSize: 10 } },
        series: [{
          type: 'pie', radius: ['54%', '76%'], center: ['50%', '44%'],
          avoidLabelOverlap: true, label: { show: false },
          emphasis: { scale: true, scaleSize: 4 },
          data
        }],
        graphic: [
          { type: 'text', left: 'center', top: '35%', style: { text: String(this.stats.petTotal || 128), fontSize: 26, fontFamily: 'Georgia', fill: '#213029' } },
          { type: 'text', left: 'center', top: '47%', style: { text: '在站动物', fontSize: 9, fill: '#9aa79f' } }
        ]
      })
      this.charts.push(chart)
    },
    initBar() {
      const el = this.$refs.barChart
      if (!el) return
      const chart = echarts.init(el)
      const cats = (this.stats.categoryStats || demoStats.categoryStats).slice(0, 8)
      chart.setOption({
        grid: { left: 34, right: 18, top: 24, bottom: 42 },
        tooltip: { trigger: 'axis', backgroundColor: '#1f2f28', borderWidth: 0, textStyle: { color: '#fff', fontSize: 11 } },
        xAxis: { type: 'category', data: cats.map(c => c.name), axisLine: { lineStyle: { color: '#ebe2d4' } }, axisTick: { show: false }, axisLabel: { color: '#9aa79f', fontSize: 9, rotate: 24 } },
        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1ece3' } }, axisLabel: { color: '#9aa79f', fontSize: 9 } },
        series: [{
          type: 'bar', data: cats.map(c => c.count), barWidth: 18,
          itemStyle: { color: '#7f9c86', borderRadius: [5, 5, 0, 0] }
        }]
      })
      this.charts.push(chart)
    }
  }
}
</script>

<style scoped>
.stat-icon.t2 { background: var(--sage-soft); color: var(--sage-dark); }
.stat-icon.t3 { background: #eaf0f6; color: #52708e; }
.stat-icon.t4 { background: #fbf0dc; color: #9a6b22; }
.dash-grid { display: grid; gap: 14px; margin-bottom: 14px; }
.top-grid { grid-template-columns: 1.7fr 1fr; }
.bottom-grid { grid-template-columns: 1.3fr 1fr; }
.chart { width: 100%; }
.trend-chart { height: 300px; }
.pie-chart { height: 300px; }
.bar-chart { height: 270px; }
.legend { display: flex; gap: 14px; font-size: 9px; color: var(--muted); }
.legend span { display: flex; align-items: center; gap: 5px; }
.dot { width: 6px; height: 6px; border-radius: 50%; }
.c1 { background: var(--primary); }
.c2 { background: var(--sage); }
.pending-list { padding: 8px 20px; }
.pending-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--line); }
.pending-item:last-child { border-bottom: none; }
.pending-item:hover strong { color: var(--primary); }
.p-icon { width: 34px; height: 34px; border-radius: 10px; display: flex; align-items: center; justify-content: center; background: var(--primary-soft); color: var(--primary); flex-shrink: 0; }
.p-icon svg { width: 16px; height: 16px; }
.p-icon.sage { background: var(--sage-soft); color: var(--sage-dark); }
.p-icon.blue { background: #eaf0f6; color: #52708e; }
.pending-item div { flex: 1; display: flex; flex-direction: column; }
.pending-item strong { font-size: 10.5px; transition: color .2s; }
.pending-item small { font-size: 8.5px; color: var(--muted-2); margin-top: 2px; }
.p-count { font-family: var(--font-display); font-size: 18px; color: var(--ink); }
.notice-mini { margin: 4px 20px 18px; padding: 12px 14px; background: var(--cream); border-radius: 10px; }
.nm-title { font-size: 8px; color: var(--primary); letter-spacing: .08em; }
.notice-mini p { font-size: 10px; color: var(--ink-2); margin-top: 3px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notice-mini a { font-size: 8.5px; color: var(--muted); display: inline-block; margin-top: 5px; }
.recent-card { margin-bottom: 10px; }
.mini-avatar { width: 26px; height: 26px; border-radius: 50%; background: var(--cream-2); color: var(--ink-2); display: flex; align-items: center; justify-content: center; font-size: 9px; }

@media (max-width: 1100px) {
  .top-grid, .bottom-grid { grid-template-columns: 1fr; }
}
</style>
