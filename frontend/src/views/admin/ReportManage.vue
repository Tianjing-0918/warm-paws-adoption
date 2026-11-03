<template>
  <div>
    <div class="admin-page-head">
      <div><h1>救助上报</h1><p>查看市民上报的流浪动物信息，安排志愿者跟进</p></div>
      <div class="actions"><button class="a-btn" @click="load">刷新</button></div>
    </div>

    <div class="report-layout">
      <div class="a-card report-table">
        <div class="a-card-head">
          <div><h3>上报记录</h3><span class="sub">共 {{ filtered.length }} 条</span></div>
          <el-select v-model="statusFilter" size="small" clearable placeholder="全部状态" style="width:120px">
            <el-option label="待处理" :value="0" /><el-option label="已处理" :value="1" /><el-option label="已拒绝" :value="2" />
          </el-select>
        </div>
        <table class="a-table">
          <thead><tr><th>现场</th><th>上报人</th><th>发现地点</th><th>时间</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="r in filtered" :key="r.id">
              <td><img :src="imageUrl(r.images)" class="thumb" :alt="r.location" /></td>
              <td><div class="main-cell"><span class="mini-avatar">{{ (r.username || '用').slice(0, 1) }}</span><strong>{{ r.username || '用户' }}</strong></div></td>
              <td><div class="loc-cell"><strong>{{ r.location }}</strong><small>{{ r.description }}</small></div></td>
              <td>{{ formatDate(r.createTime, true) }}</td>
              <td><span class="status-tag" :class="r.status === 1 ? 'ok' : r.status === 2 ? 'danger' : 'warn'">{{ ['待处理', '已处理', '已拒绝'][r.status] }}</span></td>
              <td><button class="a-link" @click="openDetail(r)">查看 / 处理</button></td>
            </tr>
          </tbody>
        </table>
        <div v-if="!filtered.length" class="a-empty"><div class="icon">🚨</div><p>暂无上报记录</p></div>
      </div>

      <div class="side-panel">
        <div class="a-card map-panel">
          <div class="a-card-head"><h3>上报分布</h3><span class="sub">上海地区</span></div>
          <div class="mini-map">
            <div class="grid-bg"></div>
            <span v-for="(p, i) in pins" :key="i" class="pin" :style="{ left: p.x, top: p.y }" :class="'p' + i"><i></i>{{ p.label }}</span>
            <span class="map-tip">示意地图</span>
          </div>
        </div>
        <div class="a-card quick-guide">
          <div class="a-card-head"><h3>处理建议</h3></div>
          <div class="guide-list">
            <div><span>1</span><p><strong>先确认安全</strong>检查是否受伤、是否在危险路段，必要时联系附近医院。</p></div>
            <div><span>2</span><p><strong>核实信息</strong>与上报人电话沟通，确认动物是否仍在原地。</p></div>
            <div><span>3</span><p><strong>安排跟进</strong>联系附近志愿者，记录处理结果并回复上报人。</p></div>
          </div>
          <div class="hotline">救助热线 <strong>400-123-4567</strong></div>
        </div>
      </div>
    </div>

    <el-dialog title="上报详情与处理" :visible.sync="detailVisible" width="700px">
      <div v-if="current" class="report-detail">
        <div class="rd-head">
          <div class="rd-imgs">
            <img :src="imageUrl(current.images)" alt="" />
          </div>
          <div class="rd-info">
            <span class="status-tag" :class="current.status === 1 ? 'ok' : current.status === 2 ? 'danger' : 'warn'">{{ ['待处理', '已处理', '已拒绝'][current.status] }}</span>
            <h2>{{ current.location }}</h2>
            <p>{{ formatDate(current.createTime, true) }} · 上报人 {{ current.username || '用户' }}</p>
            <p class="rd-contact">联系方式：{{ current.contact }}</p>
          </div>
        </div>
        <div class="dialog-block"><span class="detail-label">现场描述</span><p class="detail-desc">{{ current.description }}</p></div>
        <div v-if="current.reply" class="dialog-block reply-block"><span class="detail-label">已回复内容</span><p class="detail-desc">{{ current.reply }}</p></div>
        <div v-if="current.status === 0" class="handle-box">
          <span class="detail-label">处理结果</span>
          <el-radio-group v-model="handleStatus" style="margin:8px 0 12px">
            <el-radio :label="1">已处理 / 已救助</el-radio>
            <el-radio :label="2">无法处理 / 拒绝</el-radio>
          </el-radio-group>
          <el-input v-model="handleReply" type="textarea" :rows="3" placeholder="填写处理说明，将展示给上报人…" />
        </div>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="current && current.status === 0" type="primary" :loading="handling" @click="submitHandle">提交处理结果</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, putData, imageUrl, formatDate } from '../../utils/api'
import { demoAdminReports } from '../../utils/demo'

export default {
  name: 'ReportManage',
  data() {
    return {
      list: demoAdminReports,
      statusFilter: null,
      detailVisible: false,
      current: null,
      handleStatus: 1,
      handleReply: '',
      handling: false,
      pins: [
        { x: '24%', y: '34%', label: '长宁' },
        { x: '53%', y: '56%', label: '杨浦' },
        { x: '36%', y: '68%', label: '徐汇' },
        { x: '70%', y: '32%', label: '虹口' }
      ]
    }
  },
  computed: {
    filtered() {
      if (this.statusFilter === null || this.statusFilter === '') return this.list
      return this.list.filter(r => r.status === this.statusFilter)
    }
  },
  created() {
    document.title = '救助上报 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl, formatDate,
    async load() {
      const data = await getData('/report/adminPage', { pageNum: 1, pageSize: 100 }, { list: demoAdminReports })
      if (data && data.list && data.list.length) this.list = data.list
    },
    openDetail(r) {
      this.current = r
      this.handleStatus = 1
      this.handleReply = ''
      this.detailVisible = true
    },
    async submitHandle() {
      if (!this.handleReply.trim()) return this.$message.warning('请填写处理说明')
      this.handling = true
      await putData('/report/handle/' + this.current.id, { status: this.handleStatus, reply: this.handleReply }, { ok: true })
      this.current.status = this.handleStatus
      this.current.reply = this.handleReply
      this.handling = false
      this.detailVisible = false
      this.$message.success('处理结果已提交')
    }
  }
}
</script>

<style scoped>
.report-layout { display: grid; grid-template-columns: 1fr 290px; gap: 14px; align-items: start; }
.report-table { overflow: hidden; }
.mini-avatar { width: 26px; height: 26px; border-radius: 50%; background: var(--cream-2); color: var(--ink-2); display: flex; align-items: center; justify-content: center; font-size: 9px; }
.loc-cell { max-width: 220px; }
.loc-cell strong { display: block; font-size: 10.5px; color: var(--ink); }
.loc-cell small { display: block; font-size: 9px; color: var(--muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 2px; }
.side-panel { display: flex; flex-direction: column; gap: 14px; }
.mini-map { position: relative; height: 190px; background: #eef1e9; overflow: hidden; }
.grid-bg { position: absolute; inset: 0; background-image: linear-gradient(rgba(127,156,134,.14) 1px, transparent 1px), linear-gradient(90deg, rgba(127,156,134,.14) 1px, transparent 1px); background-size: 30px 30px; }
.pin { position: absolute; display: flex; flex-direction: column; align-items: center; gap: 4px; font-size: 8px; color: var(--ink-2); }
.pin i { width: 10px; height: 10px; border-radius: 50% 50% 50% 0; background: var(--primary); transform: rotate(-45deg); box-shadow: 0 2px 6px rgba(201,102,63,.3); }
.pin.p1 i { background: var(--sage); }
.pin.p2 i { background: var(--amber); }
.map-tip { position: absolute; left: 10px; bottom: 8px; font-size: 7.5px; color: var(--muted-2); }
.guide-list { padding: 14px 18px; display: flex; flex-direction: column; gap: 12px; }
.guide-list > div { display: flex; gap: 9px; }
.guide-list span { width: 18px; height: 18px; border-radius: 50%; background: var(--cream-2); font-size: 8px; color: var(--primary); display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 1px; }
.guide-list p { font-size: 9.5px; color: var(--muted); line-height: 1.7; }
.guide-list strong { color: var(--ink-2); display: block; font-size: 10px; }
.hotline { margin: 0 18px 16px; padding: 10px 12px; background: var(--primary-soft); border-radius: 8px; font-size: 9px; color: var(--primary-dark); }
.hotline strong { font-size: 11px; margin-left: 4px; }
.rd-head { display: flex; gap: 18px; padding-bottom: 18px; border-bottom: 1px solid var(--line); }
.rd-imgs img { width: 180px; height: 140px; border-radius: 12px; object-fit: cover; }
.rd-info h2 { font-size: 20px; margin-top: 8px; }
.rd-info p { font-size: 10px; color: var(--muted); margin-top: 4px; }
.rd-contact { color: var(--primary) !important; }
.reply-block { background: var(--sage-soft); }
.handle-box { margin-top: 18px; padding-top: 16px; border-top: 1px solid var(--line); }
@media (max-width: 1100px) {
  .report-layout { grid-template-columns: 1fr; }
  .side-panel { display: grid; grid-template-columns: 1fr 1fr; }
}
@media (max-width: 700px) {
  .report-table { overflow-x: auto; }
  .a-table { min-width: 760px; }
  .side-panel { grid-template-columns: 1fr; }
  .rd-head { flex-direction: column; }
}
</style>
