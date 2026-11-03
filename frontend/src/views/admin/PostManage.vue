<template>
  <div>
    <div class="admin-page-head">
      <div><h1>社区管理</h1><p>审核、置顶与删除社区帖子，维护友好讨论氛围</p></div>
      <div class="actions"><button class="a-btn" @click="load">刷新</button></div>
    </div>

    <div class="post-stats">
      <div><span>总帖子</span><strong>{{ list.length }}</strong></div>
      <div><span>今日新增</span><strong>{{ todayCount }}</strong></div>
      <div><span>待审核</span><strong class="warn">{{ pendingCount }}</strong></div>
      <div><span>总互动</span><strong>{{ list.reduce((n, p) => n + (p.likeCount || 0), 0) }}</strong></div>
    </div>

    <div class="a-filter">
      <div class="chip-row">
        <button v-for="c in categories" :key="c" class="chip" :class="{ active: category === c }" @click="category = c">{{ c }}</button>
      </div>
      <span class="spacer"></span>
      <el-input v-model="keyword" size="small" placeholder="搜索标题 / 作者" clearable style="width:190px" />
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>帖子</th><th>作者</th><th>分类</th><th>互动</th><th>发布时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="p in filtered" :key="p.id">
            <td><div class="post-cell" @click="openDetail(p)"><img v-if="p.images" :src="imageUrl(p.images)" alt="" /><div><strong>{{ p.title }}</strong><small>{{ plain(p.content) }}</small></div></div></td>
            <td>{{ p.username || '用户' }}</td>
            <td><span class="status-tag">{{ p.category || categoryOf(p) }}</span></td>
            <td><span class="interact">赞 {{ p.likeCount || 0 }} · 评 {{ (p.comments || []).length }}</span></td>
            <td>{{ formatDate(p.createTime, true) }}</td>
            <td><span class="status-tag" :class="p.pinned ? 'warn' : 'ok'">{{ p.pinned ? '已置顶' : '已发布' }}</span></td>
            <td>
              <div class="op">
                <button class="a-link" @click="openDetail(p)">查看</button>
                <button class="a-link" @click="pin(p)">{{ p.pinned ? '取消置顶' : '置顶' }}</button>
                <button class="a-link danger" @click="remove(p)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">💬</div><p>没有符合条件的帖子</p></div>
    </div>

    <el-dialog :visible.sync="detailVisible" width="680px" custom-class="post-detail-dialog">
      <div v-if="current">
        <div class="d-head"><span class="status-tag">{{ current.category || categoryOf(current) }}</span><span class="d-time">{{ formatDate(current.createTime, true) }}</span></div>
        <h2>{{ current.title }}</h2>
        <p class="d-author">作者：{{ current.username }} · ♥ {{ current.likeCount || 0 }}</p>
        <img v-if="current.images" :src="imageUrl(current.images)" class="d-img" alt="" />
        <div class="prose d-content" v-html="markdown(current.content)"></div>
      </div>
      <div slot="footer"><el-button @click="detailVisible = false">关闭</el-button><el-button type="danger" plain @click="remove(current)">删除帖子</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, deleteData, imageUrl, formatDate } from '../../utils/api'
import { demoPosts } from '../../utils/demo'

export default {
  name: 'PostManage',
  data() {
    return {
      list: demoPosts.map(p => Object.assign({ pinned: false }, p)),
      category: '全部',
      categories: ['全部', '领养故事', '新手求助', '养宠经验', '志愿日记'],
      keyword: '',
      detailVisible: false,
      current: null
    }
  },
  computed: {
    todayCount() {
      const today = new Date().toISOString().slice(0, 10)
      return this.list.filter(p => String(p.createTime || '').slice(0, 10) === today).length
    },
    pendingCount() {
      return this.list.filter(p => p.status === 2).length
    },
    filtered() {
      let list = this.list.slice()
      if (this.category !== '全部') list = list.filter(p => (p.category || this.categoryOf(p)) === this.category)
      if (this.keyword) list = list.filter(p => (p.title || '').indexOf(this.keyword) >= 0 || (p.username || '').indexOf(this.keyword) >= 0)
      return list
    }
  },
  created() {
    document.title = '社区管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl, formatDate,
    plain(t) { return String(t || '').replace(/[#*_>`]/g, '').replace(/\s+/g, ' ').slice(0, 40) + '…' },
    categoryOf(p) {
      const t = (p.title || '') + (p.content || '')
      if (/求助|怎么办|急|请问/.test(t)) return '新手求助'
      if (/志愿|救助站|活动/.test(t)) return '志愿日记'
      if (/领养|到家|回家/.test(t)) return '领养故事'
      return '养宠经验'
    },
    markdown(t) {
      let s = String(t || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      s = s.replace(/^## (.+)$/gm, '<h2>$1</h2>').replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>').replace(/^- (.+)$/gm, '<li>$1</li>').replace(/(<li>[\s\S]*?<\/li>)/g, '<ul>$1</ul>')
      return s.split(/\n{2,}/).map(b => /^<(h|ul)/.test(b.trim()) ? b : '<p>' + b.replace(/\n/g, '<br/>') + '</p>').join('')
    },
    async load() {
      const data = await getData('/post/list', { pageNum: 1, pageSize: 50 }, { list: demoPosts })
      if (data && data.list && data.list.length) this.list = data.list.map(p => Object.assign({ pinned: false }, p))
    },
    openDetail(p) { this.current = p; this.detailVisible = true },
    pin(p) {
      p.pinned = !p.pinned
      this.list.sort((a, b) => (b.pinned ? 1 : 0) - (a.pinned ? 1 : 0))
      this.$message.success(p.pinned ? '已置顶' : '已取消置顶')
    },
    async remove(p) {
      try { await this.$confirm(`确定删除帖子「${p.title}」吗？`, '删除帖子', { type: 'warning' }) } catch (e) { return }
      await deleteData('/post/' + p.id, { ok: true })
      this.list = this.list.filter(i => i.id !== p.id)
      this.detailVisible = false
      this.$message.success('帖子已删除')
    }
  }
}
</script>

<style scoped>
.post-stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px; }
.post-stats div { background: var(--paper); border: 1px solid var(--line); border-radius: 12px; padding: 13px 16px; display: flex; justify-content: space-between; align-items: center; }
.post-stats span { font-size: 9.5px; color: var(--muted); }
.post-stats strong { font-family: var(--font-display); font-size: 20px; font-weight: 400; }
.post-stats strong.warn { color: var(--primary); }
.post-cell { display: flex; gap: 10px; align-items: center; cursor: pointer; max-width: 380px; }
.post-cell img { width: 38px; height: 38px; border-radius: 7px; object-fit: cover; flex-shrink: 0; }
.post-cell div { display: flex; flex-direction: column; min-width: 0; }
.post-cell strong { font-size: 10.5px; color: var(--ink); }
.post-cell small { font-size: 8.5px; color: var(--muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 2px; }
.interact { font-size: 9px; color: var(--muted); white-space: nowrap; }
.d-head { display: flex; justify-content: space-between; align-items: center; }
.d-time { font-size: 9px; color: var(--muted-2); }
.post-detail-dialog h2 { font-size: 22px; margin-top: 12px; }
.d-author { font-size: 10px; color: var(--muted); margin-top: 4px; }
.d-img { width: 100%; max-height: 300px; object-fit: cover; border-radius: 12px; margin-top: 14px; }
.d-content { font-size: 12.5px; margin-top: 14px; }
@media (max-width: 700px) {
  .post-stats { grid-template-columns: 1fr 1fr; }
  .a-card { overflow-x: auto; }
  .a-table { min-width: 800px; }
}
</style>
