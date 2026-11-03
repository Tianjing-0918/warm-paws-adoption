<template>
  <div>
    <div class="admin-page-head">
      <div><h1>爱心捐赠</h1><p>查看捐赠记录与善款汇总，跟进每月捐赠人</p></div>
      <div class="actions">
        <button class="a-btn" @click="exportCsv">导出记录</button>
        <button class="a-btn" @click="load">刷新</button>
      </div>
    </div>

    <div class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-top"><span>{{ s.label }}</span><span class="stat-icon" v-html="s.icon"></span></div>
        <div class="stat-val">{{ s.value }}</div>
        <div class="stat-foot">{{ s.note }}</div>
      </div>
    </div>

    <div class="a-filter">
      <el-input v-model="keyword" size="small" placeholder="搜索捐赠人 / 留言" clearable style="width:220px" />
      <el-select v-model="typeFilter" size="small" clearable placeholder="捐赠类型" style="width:130px">
        <el-option label="单次捐赠" value="once" />
        <el-option label="每月捐赠" value="monthly" />
      </el-select>
      <el-select v-model="anonFilter" size="small" clearable placeholder="是否匿名" style="width:130px">
        <el-option label="匿名" value="1" />
        <el-option label="公开姓名" value="0" />
      </el-select>
      <span class="spacer"></span>
      <span class="table-note">共 {{ filtered.length }} 笔记录</span>
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>捐赠人</th><th>金额</th><th>留言</th><th>类型</th><th>时间</th><th>状态</th></tr></thead>
        <tbody>
          <tr v-for="d in filtered" :key="d.id">
            <td>
              <div class="main-cell">
                <span class="donor-avatar" :class="{ anon: d.anonymous === 1 }">{{ d.anonymous === 1 ? '♥' : (d.donorName || '爱').slice(0, 1) }}</span>
                <div><strong>{{ d.anonymous === 1 ? '匿名爱心人士' : d.donorName }}</strong><small>记录 #{{ String(d.id).padStart(4, '0') }}</small></div>
              </div>
            </td>
            <td class="amount-cell">¥{{ Number(d.amount).toFixed(2) }}</td>
            <td><span class="message-cell">{{ d.message || '——' }}</span></td>
            <td><span class="status-tag" :class="d.monthly === 1 ? 'info' : ''">{{ d.monthly === 1 ? '每月捐赠' : '单次捐赠' }}</span></td>
            <td>{{ formatDate(d.createTime, true) }}</td>
            <td><span class="status-tag ok">已支付</span></td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">💝</div><p>暂无捐赠记录</p></div>
    </div>

    <div class="donation-note">
      <span>🌿</span>
      <div><strong>善款透明说明</strong><p>捐赠记录仅用于救助站内部核对与月度公示。对匿名捐赠人，前台只显示“爱心人士”，不会展示其账号信息。</p></div>
    </div>
  </div>
</template>

<script>
import { getData, formatDate } from '../../utils/api'
import { demoDonations } from '../../utils/demo'

export default {
  name: 'DonationManage',
  data() {
    return {
      list: demoDonations.map((d, i) => Object.assign({}, d, {
        donorName: d.donorName || d.donor,
        monthly: d.monthly ? 1 : (i === 2 ? 1 : 0),
        anonymous: d.anonymous ? 1 : 0,
        status: 1
      })),
      keyword: '',
      typeFilter: '',
      anonFilter: '',
      total: 370
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.keyword) {
        const k = this.keyword.toLowerCase()
        list = list.filter(d => (d.donorName || '').toLowerCase().indexOf(k) >= 0 || (d.message || '').indexOf(k) >= 0)
      }
      if (this.typeFilter === 'monthly') list = list.filter(d => d.monthly === 1)
      if (this.typeFilter === 'once') list = list.filter(d => d.monthly !== 1)
      if (this.anonFilter !== '' && this.anonFilter !== null) list = list.filter(d => String(d.anonymous) === String(this.anonFilter))
      return list
    },
    stats() {
      const total = this.list.reduce((n, d) => n + Number(d.amount || 0), 0)
      return [
        { label: '累计善款', value: '¥' + total.toFixed(2), note: '已支付捐赠总额', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M12 2v20M17 6H9.5a3.5 3.5 0 0 0 0 7H14a3.5 3.5 0 0 1 0 7H6"/></svg>' },
        { label: '捐赠笔数', value: this.list.length, note: '近 30 天记录', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>' },
        { label: '每月捐赠人', value: this.list.filter(d => d.monthly === 1).length, note: '可持续支持', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M21 12a9 9 0 1 1-3-6.7L21 8"/><path d="M21 3v5h-5"/></svg>' },
        { label: '匿名捐赠', value: this.list.filter(d => d.anonymous === 1).length, note: '保护捐赠人隐私', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="12" cy="12" r="9"/><path d="M8 15c1-1.6 2.3-2.4 4-2.4s3 .8 4 2.4M9.5 9h.01M14.5 9h.01"/></svg>' }
      ]
    }
  },
  created() {
    document.title = '爱心捐赠 · 暖爪管理后台'
    this.load()
  },
  methods: {
    formatDate,
    async load() {
      const data = await getData('/donation/adminPage', null, null)
      if (data && data.list) {
        this.list = data.list
        this.total = data.total
      }
    },
    exportCsv() {
      const rows = [['捐赠人', '金额', '留言', '类型', '时间', '匿名']]
      this.filtered.forEach(d => rows.push([
        d.anonymous === 1 ? '匿名爱心人士' : d.donorName,
        Number(d.amount).toFixed(2),
        (d.message || '').replace(/,/g, '，'),
        d.monthly === 1 ? '每月捐赠' : '单次捐赠',
        d.createTime,
        d.anonymous === 1 ? '是' : '否'
      ]))
      const csv = '\ufeff' + rows.map(r => r.join(',')).join('\n')
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
      const a = document.createElement('a')
      a.href = URL.createObjectURL(blob)
      a.download = '暖爪之家捐赠记录.csv'
      a.click()
      URL.revokeObjectURL(a.href)
      this.$message.success('捐赠记录已导出')
    }
  }
}
</script>

<style scoped>
.stat-icon { background: var(--primary-soft); color: var(--primary); }
.table-note { font-size: 9.5px; color: var(--muted); }
.donor-avatar { width: 30px; height: 30px; border-radius: 50%; background: var(--primary-soft); color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; }
.donor-avatar.anon { background: var(--cream-2); color: var(--muted); }
.amount-cell { font-family: var(--font-display); font-size: 15px; color: var(--primary); }
.message-cell { display: block; max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--muted); font-size: 10px; }
.donation-note { display: flex; gap: 12px; margin-top: 16px; padding: 14px 18px; border: 1px dashed var(--line-strong); border-radius: 12px; }
.donation-note span { font-size: 18px; }
.donation-note strong { font-size: 10.5px; }
.donation-note p { font-size: 9.5px; color: var(--muted); line-height: 1.7; margin-top: 2px; }
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 760px; }
}
</style>
