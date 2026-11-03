<template>
  <div>
    <div class="admin-page-head">
      <div><h1>寻宠管理</h1><p>管理走失与捡到启事，标记已找回或已认领</p></div>
      <div class="actions"><button class="a-btn" @click="load">刷新</button></div>
    </div>

    <div class="a-filter">
      <div class="status-tabs">
        <button v-for="t in tabs" :key="t.value" :class="{ active: tab === t.value }" @click="tab = t.value">{{ t.label }}</button>
      </div>
      <span class="spacer"></span>
      <el-input v-model="keyword" size="small" placeholder="搜索标题 / 地点" clearable style="width:200px" />
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>启事</th><th>类型</th><th>动物</th><th>地点</th><th>联系方式</th><th>发布时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="l in filtered" :key="l.id">
            <td><div class="main-cell"><img :src="imageUrl(l.images)" class="thumb" alt="" /><div><strong>{{ l.title }}</strong><small>{{ l.description }}</small></div></div></td>
            <td><span class="status-tag" :class="l.type === '走失' ? 'danger' : 'ok'">{{ l.type }}</span></td>
            <td>{{ l.petType }} · {{ l.color }}</td>
            <td>{{ l.location }}</td>
            <td>{{ l.contact }}</td>
            <td>{{ formatDate(l.createTime, true) }}</td>
            <td><span class="status-tag" :class="l.status === 0 ? 'warn' : 'ok'">{{ l.status === 0 ? '进行中' : '已完成' }}</span></td>
            <td>
              <div class="op">
                <button class="a-link" @click="openDetail(l)">查看</button>
                <button v-if="l.status === 0" class="a-link" @click="resolve(l)">标记完成</button>
                <button class="a-link danger" @click="remove(l)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">🔎</div><p>没有符合条件的启事</p></div>
    </div>

    <el-dialog title="寻宠启事详情" :visible.sync="detailVisible" width="620px">
      <div v-if="current">
        <img :src="imageUrl(current.images)" class="detail-img" alt="" />
        <div class="d-head"><span class="status-tag" :class="current.type === '走失' ? 'danger' : 'ok'">{{ current.type }}</span><span>{{ formatDate(current.createTime, true) }}</span></div>
        <h2>{{ current.title }}</h2>
        <p class="d-loc">📍 {{ current.location }} · {{ current.petType }} · {{ current.color }}</p>
        <p class="d-desc">{{ current.description }}</p>
        <div class="d-contact">联系方式：<strong>{{ current.contact }}</strong></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, putData, deleteData, imageUrl, formatDate } from '../../utils/api'
import { demoLostPets } from '../../utils/demo'

export default {
  name: 'LostManage',
  data() {
    return {
      list: demoLostPets,
      tab: 'all',
      keyword: '',
      detailVisible: false,
      current: null,
      tabs: [
        { label: '全部', value: 'all' },
        { label: '走失', value: '走失' },
        { label: '捡到', value: '捡到' }
      ]
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.tab !== 'all') list = list.filter(l => l.type === this.tab)
      if (this.keyword) list = list.filter(l => (l.title || '').indexOf(this.keyword) >= 0 || (l.location || '').indexOf(this.keyword) >= 0)
      return list
    }
  },
  created() {
    document.title = '寻宠管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl, formatDate,
    async load() {
      const data = await getData('/lost/list', { pageNum: 1, pageSize: 100 }, { list: demoLostPets })
      if (data && data.list && data.list.length) this.list = data.list
    },
    openDetail(l) { this.current = l; this.detailVisible = true },
    async resolve(l) {
      await putData('/lost/' + l.id, { status: 1 }, { ok: true })
      l.status = 1
      this.$message.success('已标记为完成')
    },
    async remove(l) {
      try { await this.$confirm(`确定删除「${l.title}」吗？`, '删除启事', { type: 'warning' }) } catch (e) { return }
      await deleteData('/lost/' + l.id, { ok: true })
      this.list = this.list.filter(i => i.id !== l.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.status-tabs { display: flex; gap: 4px; }
.status-tabs button { border: none; background: transparent; padding: 5px 11px; border-radius: 7px; font-size: 10px; color: var(--muted); cursor: pointer; }
.status-tabs button.active { background: var(--ink); color: #fff; }
.detail-img { width: 100%; max-height: 300px; object-fit: cover; border-radius: 12px; }
.d-head { display: flex; justify-content: space-between; align-items: center; margin-top: 14px; font-size: 9px; color: var(--muted); }
.d-head h2 { margin-top: 8px; }
.d-head + h2 { font-size: 22px; }
.d-loc { font-size: 10.5px; color: var(--muted); margin-top: 6px; }
.d-desc { font-size: 12.5px; color: var(--ink-2); line-height: 1.9; margin-top: 12px; }
.d-contact { font-size: 11px; background: var(--cream-2); border-radius: 8px; padding: 10px 12px; margin-top: 14px; }
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 900px; }
}
</style>
