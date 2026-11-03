<template>
  <div>
    <div class="admin-page-head">
      <div><h1>领养审核</h1><p>审核领养申请，跟进试养打卡与回访进度</p></div>
      <div class="actions">
        <button class="a-btn" @click="load">刷新申请</button>
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
      <div class="status-tabs">
        <button v-for="t in tabs" :key="t.label" :class="{ active: tab === t.value }" @click="setTab(t.value)">
          {{ t.label }}<span>{{ countBy(t.value) }}</span>
        </button>
      </div>
      <span class="spacer"></span>
      <el-input v-model="keyword" size="small" placeholder="搜索申请人 / 宠物" clearable style="width:190px" />
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>申请人</th><th>申请领养</th><th>居住地</th><th>养宠经验</th><th>提交时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in filtered" :key="a.id">
            <td><div class="main-cell"><span class="mini-avatar">{{ (a.username || '用').slice(0, 1) }}</span><div><strong>{{ a.username || '用户 #' + a.userId }}</strong><small>申请编号 #{{ String(a.id).padStart(4, '0') }}</small></div></div></td>
            <td><div class="main-cell"><img :src="imageUrl(a.petImage)" class="thumb" /><strong>{{ a.petName }}</strong></div></td>
            <td>{{ a.address }}</td>
            <td><span class="exp-text">{{ a.experience }}</span></td>
            <td>{{ formatDate(a.createTime, true) }}</td>
            <td><span class="status-tag" :class="statusClass(a.status)">{{ statusLabel(a.status) }}</span></td>
            <td>
              <div class="op">
                <button class="a-link" @click="openDetail(a)">查看</button>
                <template v-if="a.status === 0">
                  <button class="a-link" @click="openAudit(a, 1)">通过</button>
                  <button class="a-link danger" @click="openAudit(a, 2)">拒绝</button>
                </template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">📋</div><p>没有符合条件的申请</p></div>
    </div>

    <el-dialog title="领养申请详情" :visible.sync="detailVisible" width="680px">
      <div v-if="current" class="detail">
        <div class="detail-top">
          <img :src="imageUrl(current.petImage)" :alt="current.petName" />
          <div>
            <span class="status-tag" :class="statusClass(current.status)">{{ statusLabel(current.status) }}</span>
            <h2>{{ current.username }} 申请领养 {{ current.petName }}</h2>
            <p>申请编号 #{{ String(current.id).padStart(4, '0') }} · 提交于 {{ formatDate(current.createTime, true) }}</p>
          </div>
        </div>
        <div class="dialog-grid">
          <div class="dialog-block"><span class="detail-label">居住地址</span><strong>{{ current.address || '未填写' }}</strong></div>
          <div class="dialog-block"><span class="detail-label">养宠经验</span><strong>{{ current.experience || '未填写' }}</strong></div>
        </div>
        <div class="dialog-block"><span class="detail-label">领养理由</span><p class="detail-desc">{{ current.reason || '未填写' }}</p></div>
        <div v-if="current.reply" class="dialog-block reply-block"><span class="detail-label">审核回复</span><p class="detail-desc">{{ current.reply }}</p></div>
        <div class="timeline">
          <div class="tl done"><i></i><div><strong>提交申请</strong><small>{{ formatDate(current.createTime, true) }}</small></div></div>
          <div class="tl" :class="{ done: current.status !== 0 }"><i></i><div><strong>志愿者沟通与家访</strong><small>{{ current.status === 0 ? '等待处理' : '已完成' }}</small></div></div>
          <div class="tl" :class="{ done: current.status === 1 }"><i></i><div><strong>试养期打卡</strong><small>{{ current.status === 1 ? '进行中 · 请跟进 14 天打卡' : '通过后开始' }}</small></div></div>
          <div class="tl"><i></i><div><strong>正式领养与回访</strong><small>试养结束后</small></div></div>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <template v-if="current && current.status === 0">
          <el-button type="danger" plain @click="openAudit(current, 2)">拒绝申请</el-button>
          <el-button type="primary" @click="openAudit(current, 1)">通过申请</el-button>
        </template>
      </div>
    </el-dialog>

    <el-dialog :title="auditStatus === 1 ? '通过领养申请' : '拒绝领养申请'" :visible.sync="auditVisible" width="460px">
      <p class="audit-tip">{{ auditStatus === 1 ? '通过后，该动物将标记为已领养，并进入试养打卡流程。' : '请填写拒绝原因，用户可在个人中心查看。' }}</p>
      <el-form label-position="top">
        <el-form-item :label="auditStatus === 1 ? '给申请人的回复' : '拒绝原因'">
          <el-input v-model="reply" type="textarea" :rows="4" :placeholder="auditStatus === 1 ? '例如：资料审核通过，已安排周六家访。' : '例如：目前居住条件暂不适合养宠，建议先积累经验。'" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button :type="auditStatus === 1 ? 'primary' : 'danger'" :loading="auditing" @click="confirmAudit">确认{{ auditStatus === 1 ? '通过' : '拒绝' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, putData, imageUrl, formatDate } from '../../utils/api'
import { demoAdminAdoptions } from '../../utils/demo'

export default {
  name: 'AdoptManage',
  data() {
    return {
      list: demoAdminAdoptions,
      tab: 0,
      keyword: '',
      detailVisible: false,
      auditVisible: false,
      auditing: false,
      current: null,
      auditStatus: 1,
      reply: '',
      tabs: [
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已拒绝', value: 2 },
        { label: '全部申请', value: 'all' }
      ]
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.tab !== 'all') list = list.filter(a => a.status === this.tab)
      if (this.keyword) list = list.filter(a => (a.username || '').indexOf(this.keyword) >= 0 || (a.petName || '').indexOf(this.keyword) >= 0)
      return list
    },
    stats() {
      return [
        { label: '待审核申请', value: this.list.filter(a => a.status === 0).length, note: '建议 24 小时内处理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>' },
        { label: '本月通过', value: this.list.filter(a => a.status === 1).length, note: '试养跟进中', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M20 6 9 17l-5-5"/></svg>' },
        { label: '试养打卡', value: 2, note: '今日 1 份待提交', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18M8 15l2 2 4-4"/></svg>' },
        { label: '逾期未处理', value: 1, note: '已超过 48 小时', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M12 9v4M12 17h.01"/><path d="M10.3 3.9 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0Z"/></svg>' }
      ]
    }
  },
  created() {
    document.title = '领养审核 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl, formatDate,
    statusLabel(s) { return ['待审核', '已通过', '已拒绝'][s] || '未知' },
    statusClass(s) { return s === 1 ? 'ok' : s === 2 ? 'danger' : 'warn' },
    countBy(s) { return s === 'all' ? this.list.length : this.list.filter(a => a.status === s).length },
    setTab(v) { this.tab = v },
    async load() {
      const data = await getData('/adopt/adminPage', { pageNum: 1, pageSize: 100 }, { list: demoAdminAdoptions })
      if (data && data.list && data.list.length) this.list = data.list
    },
    openDetail(a) { this.current = a; this.detailVisible = true },
    openAudit(a, status) {
      this.current = a
      this.auditStatus = status
      this.reply = status === 1 ? '资料审核通过，我们会尽快与你联系并安排家访。' : ''
      this.detailVisible = false
      this.auditVisible = true
    },
    async confirmAudit() {
      if (!this.reply.trim()) return this.$message.warning('请填写回复内容')
      this.auditing = true
      await putData('/adopt/audit/' + this.current.id, { status: this.auditStatus, reply: this.reply }, { ok: true })
      this.current.status = this.auditStatus
      this.current.reply = this.reply
      this.auditing = false
      this.auditVisible = false
      this.$message.success(this.auditStatus === 1 ? '已通过领养申请' : '已拒绝领养申请')
    }
  }
}
</script>

<style scoped>
.stat-icon { background: var(--cream-2); color: var(--ink-2); }
.status-tabs { display: flex; gap: 4px; }
.status-tabs button { border: none; background: transparent; padding: 5px 12px; border-radius: 7px; font-size: 10px; color: var(--muted); cursor: pointer; }
.status-tabs button.active { background: var(--ink); color: #fff; }
.status-tabs span { margin-left: 4px; opacity: .6; }
.mini-avatar { width: 27px; height: 27px; border-radius: 50%; background: var(--cream-2); color: var(--ink-2); display: flex; align-items: center; justify-content: center; font-size: 9px; }
.exp-text { display: block; max-width: 160px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--muted); }
.detail-top { display: flex; gap: 16px; align-items: center; padding-bottom: 18px; border-bottom: 1px solid var(--line); }
.detail-top img { width: 78px; height: 78px; border-radius: 14px; object-fit: cover; }
.detail-top h2 { font-size: 20px; margin-top: 6px; letter-spacing: -.02em; }
.detail-top p { font-size: 9.5px; color: var(--muted); margin-top: 3px; }
.reply-block { background: var(--sage-soft); }
.timeline { margin-top: 20px; padding-left: 4px; }
.tl { display: flex; gap: 12px; position: relative; padding-bottom: 18px; }
.tl::before { content: ''; position: absolute; left: 5px; top: 14px; bottom: 0; width: 1px; background: var(--line); }
.tl:last-child::before { display: none; }
.tl i { width: 11px; height: 11px; border-radius: 50%; border: 2px solid var(--line-strong); background: var(--paper); margin-top: 3px; z-index: 1; }
.tl.done i { border-color: var(--sage); background: var(--sage); }
.tl div { display: flex; flex-direction: column; }
.tl strong { font-size: 10.5px; }
.tl small { font-size: 9px; color: var(--muted-2); margin-top: 1px; }
.audit-tip { font-size: 11px; color: var(--muted); line-height: 1.7; margin-bottom: 14px; }
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 860px; }
}
</style>
