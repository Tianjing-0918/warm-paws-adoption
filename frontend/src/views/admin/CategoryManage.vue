<template>
  <div>
    <div class="admin-page-head">
      <div><h1>品种管理</h1><p>维护猫、狗及其他动物的品种分类</p></div>
      <div class="actions"><button class="a-btn primary" @click="openDialog()">＋ 添加品种</button></div>
    </div>

    <div class="a-filter">
      <div class="chip-row">
        <button class="chip" :class="{ active: type === '全部' }" @click="type = '全部'">全部</button>
        <button class="chip" :class="{ active: type === '猫' }" @click="type = '猫'">猫咪</button>
        <button class="chip" :class="{ active: type === '狗' }" @click="type = '狗'">狗狗</button>
        <button class="chip" :class="{ active: type === '其他' }" @click="type = '其他'">其他</button>
      </div>
      <span class="spacer"></span>
      <el-input v-model="keyword" size="small" placeholder="搜索品种" clearable style="width:180px" />
    </div>

    <div class="category-grid">
      <div v-for="c in filtered" :key="c.id" class="category-card">
        <div class="cat-top">
          <span class="cat-icon">{{ c.type === '猫' ? '🐱' : c.type === '狗' ? '🐶' : '🐾' }}</span>
          <span class="status-tag">{{ c.type }}</span>
        </div>
        <h3>{{ c.name }}</h3>
        <p>{{ c.description || '暂无描述' }}</p>
        <div class="cat-foot">
          <span>品种 ID {{ c.id }}</span>
          <div class="op">
            <button class="a-link" @click="openDialog(c)">编辑</button>
            <button class="a-link danger" @click="remove(c)">删除</button>
          </div>
        </div>
      </div>
      <button class="category-card add-card" @click="openDialog()">
        <span>＋</span><strong>添加新品种</strong><small>支持猫、狗及其他动物</small>
      </button>
    </div>

    <el-dialog :title="form.id ? '编辑品种' : '添加品种'" :visible.sync="dialogVisible" width="460px">
      <el-form :model="form" label-position="top">
        <el-form-item label="品种名称"><el-input v-model="form.name" placeholder="例如：布偶猫" /></el-form-item>
        <el-form-item label="动物类别">
          <el-radio-group v-model="form.type"><el-radio label="猫">猫</el-radio><el-radio label="狗">狗</el-radio><el-radio label="其他">其他</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="品种描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="性格、体型、护理要点…" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, putData, deleteData } from '../../utils/api'
import { demoCategories } from '../../utils/demo'

export default {
  name: 'CategoryManage',
  data() {
    return {
      list: demoCategories,
      type: '全部',
      keyword: '',
      dialogVisible: false,
      form: {}
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.type !== '全部') list = list.filter(c => c.type === this.type)
      if (this.keyword) list = list.filter(c => c.name.indexOf(this.keyword) >= 0)
      return list
    }
  },
  created() {
    document.title = '品种管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    async load() {
      const data = await getData('/category/list', null, demoCategories)
      if (data && data.length) this.list = data
    },
    openDialog(c) {
      this.form = c ? Object.assign({}, c) : { type: '猫', name: '', description: '' }
      this.dialogVisible = true
    },
    async save() {
      if (!this.form.name) return this.$message.warning('请填写品种名称')
      if (this.form.id) await putData('/category', this.form, { ok: true })
      else await postData('/category', this.form, { ok: true })
      if (this.form.id) {
        const i = this.list.findIndex(x => x.id === this.form.id)
        if (i >= 0) this.$set(this.list, i, Object.assign({}, this.form))
      } else {
        this.list.push(Object.assign({}, this.form, { id: Date.now() }))
      }
      this.dialogVisible = false
      this.$message.success('品种已保存')
    },
    async remove(c) {
      try { await this.$confirm(`确定删除品种「${c.name}」吗？已有动物档案不受影响。`, '删除品种', { type: 'warning' }) } catch (e) { return }
      await deleteData('/category/' + c.id, { ok: true })
      this.list = this.list.filter(i => i.id !== c.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.category-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.category-card { background: var(--paper); border: 1px solid var(--line); border-radius: 14px; padding: 16px; display: flex; flex-direction: column; min-height: 150px; }
.cat-top { display: flex; justify-content: space-between; align-items: center; }
.cat-icon { font-size: 22px; }
.category-card h3 { font-size: 14px; margin-top: 10px; }
.category-card p { font-size: 9.5px; color: var(--muted); line-height: 1.7; margin-top: 4px; flex: 1; }
.cat-foot { display: flex; justify-content: space-between; align-items: center; padding-top: 10px; border-top: 1px solid var(--line); margin-top: 10px; }
.cat-foot > span { font-size: 8px; color: var(--muted-2); }
.add-card { border-style: dashed; align-items: center; justify-content: center; cursor: pointer; color: var(--muted); background: transparent; }
.add-card span { font-size: 24px; }
.add-card strong { font-size: 11px; margin-top: 6px; color: var(--ink-2); }
.add-card small { font-size: 8.5px; margin-top: 2px; }
.add-card:hover { border-color: var(--primary); color: var(--primary); }
@media (max-width: 1100px) { .category-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 700px) { .category-grid { grid-template-columns: 1fr 1fr; } }
</style>
