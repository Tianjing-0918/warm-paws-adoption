<template>
  <div>
    <div class="admin-page-head">
      <div><h1>商品管理</h1><p>维护公益商城商品、库存与上架状态</p></div>
      <div class="actions"><button class="a-btn primary" @click="openDialog()">＋ 添加商品</button></div>
    </div>

    <div class="product-summary">
      <div><span>在售商品</span><strong>{{ list.filter(p => p.status === 0).length }}</strong></div>
      <div><span>库存预警</span><strong class="warn">{{ list.filter(p => p.stock < 30).length }}</strong></div>
      <div><span>累计销量</span><strong>{{ list.reduce((n, p) => n + (p.sales || 0), 0) }}</strong></div>
      <div><span>公益捐赠比例</span><strong>5%</strong></div>
    </div>

    <div class="a-filter">
      <el-input v-model="keyword" size="small" placeholder="搜索商品名称" clearable style="width:200px" />
      <el-select v-model="categoryFilter" size="small" clearable placeholder="全部分类" style="width:120px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <span class="spacer"></span>
      <span class="table-note">共 {{ filtered.length }} 件商品</span>
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>商品</th><th>分类</th><th>价格</th><th>库存</th><th>销量</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="p in filtered" :key="p.id">
            <td><div class="main-cell"><img :src="imageUrl(p.image)" class="thumb" :alt="p.name" /><div><strong>{{ p.name }}</strong><small>#{{ String(p.id).padStart(4, '0') }}</small></div></div></td>
            <td><span class="status-tag">{{ p.category }}</span></td>
            <td class="price-cell">¥{{ Number(p.price).toFixed(2) }}</td>
            <td><span :class="{ low: p.stock < 30 }">{{ p.stock }}</span></td>
            <td>{{ p.sales || 0 }}</td>
            <td><span class="status-tag" :class="p.status === 0 ? 'ok' : ''">{{ p.status === 0 ? '上架中' : '已下架' }}</span></td>
            <td>
              <div class="op">
                <button class="a-link" @click="openDialog(p)">编辑</button>
                <button class="a-link" @click="toggle(p)">{{ p.status === 0 ? '下架' : '上架' }}</button>
                <button class="a-link danger" @click="remove(p)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">🛍️</div><p>没有找到商品</p></div>
    </div>

    <el-dialog :title="form.id ? '编辑商品' : '添加商品'" :visible.sync="dialogVisible" width="620px">
      <el-form :model="form" label-position="top">
        <div class="dialog-grid">
          <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.category" style="width:100%">
              <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
        </div>
        <div class="dialog-grid">
          <el-form-item label="价格（元）"><el-input v-model.number="form.price" type="number" /></el-form-item>
          <el-form-item label="库存"><el-input v-model.number="form.stock" type="number" /></el-form-item>
        </div>
        <el-form-item label="商品描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :label="0">上架</el-radio><el-radio :label="1">下架</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="商品图">
          <div class="upload-row">
            <img v-if="form.image" :src="imageUrl(form.image)" class="form-img" alt="" />
            <el-upload action="/api/upload" :show-file-list="false" :on-success="onUpload">
              <div class="upload-box"><span>＋</span><small>上传图片</small></div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存商品</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, putData, deleteData, imageUrl } from '../../utils/api'
import { demoProducts } from '../../utils/demo'

export default {
  name: 'ProductManage',
  data() {
    return {
      list: demoProducts,
      categories: ['食品', '玩具', '用品', '清洁', '出行'],
      keyword: '',
      categoryFilter: '',
      dialogVisible: false,
      saving: false,
      form: {}
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.keyword) list = list.filter(p => p.name.indexOf(this.keyword) >= 0)
      if (this.categoryFilter) list = list.filter(p => p.category === this.categoryFilter)
      return list
    }
  },
  created() {
    document.title = '商品管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl,
    async load() {
      const data = await getData('/product/page', { pageNum: 1, pageSize: 100 }, { list: demoProducts })
      if (data && data.list && data.list.length) this.list = data.list
    },
    openDialog(p) {
      this.form = p ? Object.assign({}, p) : { status: 0, category: '用品', price: 0, stock: 50 }
      this.dialogVisible = true
    },
    onUpload(res) {
      if (res && res.code === 200) this.form.image = '/api' + res.data.url
      else this.$message.warning('上传失败，请检查后端服务')
    },
    async save() {
      if (!this.form.name || !this.form.category) return this.$message.warning('请填写商品名称和分类')
      this.saving = true
      if (this.form.id) await putData('/product', this.form, { ok: true })
      else await postData('/product', this.form, { ok: true })
      if (this.form.id) {
        const i = this.list.findIndex(x => x.id === this.form.id)
        if (i >= 0) this.$set(this.list, i, Object.assign({}, this.form))
      } else {
        this.list.unshift(Object.assign({}, this.form, { id: Date.now() }))
      }
      this.saving = false
      this.dialogVisible = false
      this.$message.success('商品已保存')
    },
    async toggle(p) {
      const status = p.status === 0 ? 1 : 0
      await putData('/product', Object.assign({}, p, { status }), { ok: true })
      p.status = status
      this.$message.success(status === 0 ? '已上架' : '已下架')
    },
    async remove(p) {
      try { await this.$confirm(`确定删除「${p.name}」吗？`, '删除商品', { type: 'warning' }) } catch (e) { return }
      await deleteData('/product/' + p.id, { ok: true })
      this.list = this.list.filter(i => i.id !== p.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.product-summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px; }
.product-summary div { background: var(--paper); border: 1px solid var(--line); border-radius: 12px; padding: 13px 16px; display: flex; align-items: center; justify-content: space-between; }
.product-summary span { font-size: 9.5px; color: var(--muted); }
.product-summary strong { font-family: var(--font-display); font-size: 21px; font-weight: 400; }
.product-summary strong.warn { color: var(--primary); }
.table-note { font-size: 9.5px; color: var(--muted); }
.price-cell { color: var(--primary); font-weight: 600; }
.low { color: var(--primary); font-weight: 600; }
.upload-row { display: flex; gap: 10px; align-items: flex-start; }
.form-img { width: 86px; height: 86px; border-radius: 10px; object-fit: cover; }
.upload-box { width: 86px; height: 86px; border: 1px dashed var(--line-strong); border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); }
.upload-box span { font-size: 18px; }
.upload-box small { font-size: 8.5px; }
@media (max-width: 700px) {
  .product-summary { grid-template-columns: 1fr 1fr; }
  .a-card { overflow-x: auto; }
  .a-table { min-width: 760px; }
}
</style>
