<template>
  <div>
    <div class="admin-page-head">
      <div><h1>公告管理</h1><p>发布系统公告、领养活动与救助资讯</p></div>
      <div class="actions"><button class="a-btn primary" @click="openDialog()">＋ 发布公告</button></div>
    </div>

    <div class="notice-layout">
      <div class="a-card">
        <div class="a-card-head">
          <div><h3>公告列表</h3><span class="sub">共 {{ list.length }} 条</span></div>
          <el-select v-model="statusFilter" size="small" clearable placeholder="全部状态" style="width:120px">
            <el-option label="已发布" :value="0" /><el-option label="草稿" :value="1" />
          </el-select>
        </div>
        <table class="a-table">
          <thead><tr><th>标题</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="n in filtered" :key="n.id">
              <td>
                <div class="notice-cell" @click="preview(n)">
                  <span class="notice-ico" :class="{ draft: n.status === 1 }">{{ n.status === 0 ? '📢' : '✎' }}</span>
                  <div><strong>{{ n.title }}</strong><small>{{ plain(n.content) }}</small></div>
                </div>
              </td>
              <td><span class="status-tag" :class="n.status === 0 ? 'ok' : ''">{{ n.status === 0 ? '已发布' : '草稿' }}</span></td>
              <td>{{ formatDate(n.createTime, true) }}</td>
              <td>
                <div class="op">
                  <button class="a-link" @click="preview(n)">预览</button>
                  <button class="a-link" @click="openDialog(n)">编辑</button>
                  <button class="a-link danger" @click="remove(n)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="!filtered.length" class="a-empty"><div class="icon">📢</div><p>暂无公告</p></div>
      </div>

      <div class="a-card preview-card">
        <div class="a-card-head"><h3>前台预览</h3><span class="sub">首页公告样式</span></div>
        <div class="phone-preview">
          <div class="pp-top"><span>暖爪之家</span><span class="pp-dot"></span></div>
          <div class="pp-label">最新公告</div>
          <button v-for="n in list.slice(0, 4)" :key="n.id" class="pp-item" @click="preview(n)">
            <span class="pp-date">{{ day(n.createTime) }}<small>{{ month(n.createTime) }}月</small></span>
            <span class="pp-title">{{ n.title }}</span>
            <span>›</span>
          </button>
        </div>
      </div>
    </div>

    <el-dialog :title="form.id ? '编辑公告' : '发布公告'" :visible.sync="dialogVisible" width="640px">
      <el-form :model="form" label-position="top">
        <el-form-item label="公告标题"><el-input v-model="form.title" placeholder="例如：10 月领养家庭回访安排" maxlength="60" show-word-limit /></el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="填写公告正文，支持换行…" />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status"><el-radio :label="0">立即发布</el-radio><el-radio :label="1">保存为草稿</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存公告</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="current.title" :visible.sync="previewVisible" width="600px">
      <p class="preview-time">{{ formatDate(current.createTime, true) }}</p>
      <div class="preview-content">{{ current.content }}</div>
      <div slot="footer"><el-button @click="previewVisible = false">关闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, putData, deleteData, formatDate } from '../../utils/api'
import { demoNotices } from '../../utils/demo'

export default {
  name: 'NoticeManage',
  data() {
    return {
      list: demoNotices.map(n => Object.assign({ status: 0 }, n)),
      statusFilter: null,
      dialogVisible: false,
      previewVisible: false,
      saving: false,
      form: {},
      current: {}
    }
  },
  computed: {
    filtered() {
      if (this.statusFilter === null || this.statusFilter === '') return this.list
      return this.list.filter(n => n.status === this.statusFilter)
    }
  },
  created() {
    document.title = '公告管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    formatDate,
    day(v) { return v ? String(v).slice(8, 10) : '--' },
    month(v) { return v ? String(v).slice(5, 7) : '--' },
    plain(t) { return String(t || '').replace(/\s+/g, ' ').slice(0, 46) + '…' },
    async load() {
      const data = await getData('/notice/page', { pageNum: 1, pageSize: 100 }, { list: this.list })
      if (data && data.list && data.list.length) this.list = data.list
    },
    openDialog(n) {
      this.form = n ? Object.assign({}, n) : { status: 0, title: '', content: '' }
      this.dialogVisible = true
    },
    preview(n) { this.current = n; this.previewVisible = true },
    async save() {
      if (!this.form.title || !this.form.content) return this.$message.warning('请填写标题和内容')
      this.saving = true
      if (this.form.id) await putData('/notice', this.form, { ok: true })
      else await postData('/notice', this.form, { ok: true })
      if (this.form.id) {
        const i = this.list.findIndex(x => x.id === this.form.id)
        if (i >= 0) this.$set(this.list, i, Object.assign({}, this.form))
      } else {
        this.list.unshift(Object.assign({}, this.form, { id: Date.now(), createTime: new Date().toISOString().slice(0, 19).replace('T', ' ') }))
      }
      this.saving = false
      this.dialogVisible = false
      this.$message.success('公告已保存')
    },
    async remove(n) {
      try { await this.$confirm(`确定删除公告「${n.title}」吗？`, '删除公告', { type: 'warning' }) } catch (e) { return }
      await deleteData('/notice/' + n.id, { ok: true })
      this.list = this.list.filter(i => i.id !== n.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.notice-layout { display: grid; grid-template-columns: 1fr 300px; gap: 14px; align-items: start; }
.notice-cell { display: flex; align-items: center; gap: 10px; cursor: pointer; max-width: 460px; }
.notice-ico { width: 32px; height: 32px; border-radius: 9px; background: var(--primary-soft); display: flex; align-items: center; justify-content: center; font-size: 13px; flex-shrink: 0; }
.notice-ico.draft { background: var(--cream-2); }
.notice-cell div { display: flex; flex-direction: column; min-width: 0; }
.notice-cell strong { font-size: 11px; color: var(--ink); }
.notice-cell small { font-size: 9px; color: var(--muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 2px; }
.phone-preview { padding: 16px; background: var(--cream); }
.pp-top { display: flex; justify-content: space-between; align-items: center; font-size: 11px; font-weight: 700; padding: 8px 10px; }
.pp-dot { width: 24px; height: 24px; border-radius: 50%; background: var(--primary-soft); }
.pp-label { font-size: 9px; color: var(--muted); padding: 12px 10px 6px; }
.pp-item { width: 100%; display: flex; align-items: center; gap: 8px; border: none; background: var(--paper); border-bottom: 1px solid var(--line); padding: 9px 10px; text-align: left; cursor: pointer; }
.pp-item:last-child { border-bottom: none; }
.pp-date { font-family: var(--font-display); font-size: 12px; color: var(--primary); display: flex; flex-direction: column; align-items: center; line-height: 1.1; }
.pp-date small { font-size: 7px; color: var(--muted); }
.pp-title { flex: 1; font-size: 9.5px; color: var(--ink-2); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pp-item > span:last-child { color: var(--muted-2); font-size: 12px; }
.preview-time { font-size: 10px; color: var(--muted); margin-bottom: 12px; }
.preview-content { font-size: 13px; line-height: 1.95; color: var(--ink-2); white-space: pre-wrap; max-height: 420px; overflow-y: auto; }
@media (max-width: 1000px) {
  .notice-layout { grid-template-columns: 1fr; }
  .preview-card { display: none; }
}
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 620px; }
}
</style>
