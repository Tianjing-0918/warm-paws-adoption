<template>
  <div>
    <div class="admin-page-head">
      <div><h1>动物档案</h1><p>维护在站动物信息，管理可领养状态与健康档案</p></div>
      <div class="actions">
        <button class="a-btn" @click="load">刷新</button>
        <button class="a-btn primary" @click="openDialog()">＋ 添加动物</button>
      </div>
    </div>

    <div class="mini-stats">
      <div><span>全部动物</span><strong>{{ total }}</strong></div>
      <div><span>可领养</span><strong class="c-primary">{{ countStatus(0) }}</strong></div>
      <div><span>已领养</span><strong class="c-sage">{{ countStatus(1) }}</strong></div>
      <div><span>寄养 / 待处理</span><strong class="c-amber">{{ countStatus(2) + countStatus(3) }}</strong></div>
    </div>

    <div class="a-filter">
      <el-input v-model="query.name" size="small" placeholder="搜索动物名字" clearable style="width:180px" @keyup.enter.native="load" />
      <el-select v-model="query.categoryId" size="small" clearable placeholder="全部品种" style="width:130px" @change="load">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="query.status" size="small" clearable placeholder="全部状态" style="width:120px" @change="load">
        <el-option v-for="(s, i) in statuses" :key="i" :label="s" :value="i" />
      </el-select>
      <button class="a-btn sm" @click="load">查询</button>
      <button class="a-btn sm" @click="reset">重置</button>
      <span class="spacer"></span>
      <span class="table-note">共 {{ list.length }} 条记录</span>
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead>
          <tr><th>动物</th><th>品种</th><th>性别 / 年龄</th><th>健康情况</th><th>状态</th><th>入站时间</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="p in pagedList" :key="p.id">
            <td>
              <div class="main-cell">
                <img :src="imageUrl(p.images)" class="thumb" :alt="p.name" />
                <div><strong>{{ p.name }}</strong><small>#{{ String(p.id).padStart(4, '0') }} · {{ p.color || '毛色未记录' }}</small></div>
              </div>
            </td>
            <td>{{ p.categoryName || categoryName(p.categoryId) }}</td>
            <td>{{ p.gender || '—' }} · {{ ageText(p.age) }}</td>
            <td><span class="health-text">{{ p.healthStatus || '待体检' }}</span></td>
            <td><span class="status-tag" :class="statusClass(p.status)">{{ statuses[p.status] }}</span></td>
            <td>{{ formatDate(p.createTime) }}</td>
            <td>
              <div class="op">
                <button class="a-link" @click="openDialog(p)">编辑</button>
                <button class="a-link" @click="toggleStatus(p)">{{ p.status === 0 ? '下架' : '上架' }}</button>
                <button class="a-link danger" @click="remove(p)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!pagedList.length" class="a-empty"><div class="icon">🐾</div><p>没有找到符合条件的动物</p></div>
      <div class="a-pager">
        <el-pagination background layout="prev, pager, next" :current-page="pageNum" :page-size="pageSize" :total="list.length" @current-change="p => pageNum = p" />
      </div>
    </div>

    <el-dialog :title="form.id ? '编辑动物档案' : '添加动物'" :visible.sync="dialogVisible" width="720px">
      <el-form :model="form" label-position="top">
        <div class="dialog-grid">
          <el-form-item label="名字"><el-input v-model="form.name" placeholder="动物名字" /></el-form-item>
          <el-form-item label="品种">
            <el-select v-model="form.categoryId" placeholder="选择品种" style="width:100%">
              <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </div>
        <div class="dialog-grid">
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender"><el-radio label="公">公</el-radio><el-radio label="母">母</el-radio></el-radio-group>
          </el-form-item>
          <el-form-item label="年龄（月）"><el-input v-model.number="form.age" type="number" /></el-form-item>
        </div>
        <div class="dialog-grid">
          <el-form-item label="体重（kg）"><el-input v-model.number="form.weight" type="number" /></el-form-item>
          <el-form-item label="毛色"><el-input v-model="form.color" placeholder="例如：蓝灰色" /></el-form-item>
        </div>
        <el-form-item label="健康状况"><el-input v-model="form.healthStatus" placeholder="例如：健康，已疫苗，已绝育" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="性格、救助经过、适合的家庭…" maxlength="500" show-word-limit /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="(s, i) in statuses" :key="i" :label="i">{{ s }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="照片">
          <div class="img-upload-row">
            <div v-for="(img, i) in formImages" :key="i" class="img-item">
              <img :src="imageUrl(img)" alt="" />
              <button type="button" @click="removeImage(i)">×</button>
            </div>
            <el-upload action="/api/upload" :show-file-list="false" :on-success="onUpload" :before-upload="beforeUpload">
              <div class="upload-box"><span>＋</span><small>上传照片</small></div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存档案</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, putData, deleteData, imageUrl, formatDate } from '../../utils/api'
import { demoPets, demoCategories, ageText } from '../../utils/demo'

export default {
  name: 'PetManage',
  data() {
    return {
      list: demoPets.slice(),
      categories: demoCategories,
      statuses: ['可领养', '已领养', '寄养中', '待处理'],
      query: { name: '', categoryId: null, status: null },
      pageNum: 1,
      pageSize: 10,
      dialogVisible: false,
      saving: false,
      form: {}
    }
  },
  computed: {
    total() { return this.list.length },
    pagedList() {
      const start = (this.pageNum - 1) * this.pageSize
      return this.list.slice(start, start + this.pageSize)
    },
    formImages() {
      return this.form.images ? String(this.form.images).split(',').filter(Boolean) : []
    }
  },
  created() {
    document.title = '动物档案 · 暖爪管理后台'
    this.loadCategories()
    this.load()
  },
  methods: {
    imageUrl, formatDate, ageText,
    statusClass(s) { return s === 0 ? 'ok' : s === 1 ? 'info' : s === 2 ? 'warn' : '' },
    categoryName(id) { const c = this.categories.find(i => i.id === id); return c ? c.name : '—' },
    countStatus(s) { return this.list.filter(i => i.status === s).length },
    async loadCategories() {
      const data = await getData('/category/list', null, demoCategories)
      if (data && data.length) this.categories = data
    },
    async load() {
      const fallback = { list: demoPets, total: demoPets.length }
      const data = await getData('/pet/adminPage', { pageNum: 1, pageSize: 200, name: this.query.name, status: this.query.status }, fallback)
      let list = (data && data.list) || fallback.list
      if (this.query.categoryId) list = list.filter(p => p.categoryId === this.query.categoryId)
      this.list = list
      this.pageNum = 1
    },
    reset() {
      this.query = { name: '', categoryId: null, status: null }
      this.load()
    },
    openDialog(row) {
      this.form = row ? Object.assign({}, row) : { status: 0, gender: '母', images: '' }
      this.dialogVisible = true
    },
    beforeUpload(file) {
      const ok = /image\/(jpeg|png|gif|webp)/.test(file.type)
      if (!ok) this.$message.warning('请上传图片文件')
      return ok
    },
    onUpload(res) {
      if (res && res.code === 200) {
        const url = '/api' + res.data.url
        this.form.images = this.form.images ? this.form.images + ',' + url : url
      } else {
        this.$message.warning('上传失败，请检查后端服务')
      }
    },
    removeImage(i) {
      const list = this.formImages.slice()
      list.splice(i, 1)
      this.form.images = list.join(',')
    },
    async save() {
      if (!this.form.name) return this.$message.warning('请填写动物名字')
      if (!this.form.categoryId) return this.$message.warning('请选择品种')
      this.saving = true
      if (this.form.id) await putData('/pet', this.form, { ok: true })
      else await postData('/pet', this.form, { ok: true })
      if (this.form.id) {
        const idx = this.list.findIndex(p => p.id === this.form.id)
        if (idx >= 0) this.$set(this.list, idx, Object.assign({}, this.form))
      } else {
        this.list.unshift(Object.assign({}, this.form, { id: Date.now(), createTime: new Date().toISOString().slice(0, 19).replace('T', ' ') }))
      }
      this.saving = false
      this.dialogVisible = false
      this.$message.success('动物档案已保存')
      this.load()
    },
    async toggleStatus(p) {
      const next = p.status === 0 ? 3 : 0
      await putData('/pet', Object.assign({}, p, { status: next }), { ok: true })
      p.status = next
      this.$message.success(next === 0 ? '已重新上架' : '已下架')
    },
    async remove(p) {
      try {
        await this.$confirm(`确定删除「${p.name}」的档案吗？删除后可在回收站恢复。`, '删除档案', { type: 'warning' })
      } catch (e) { return }
      await deleteData('/pet/' + p.id, { ok: true })
      this.list = this.list.filter(i => i.id !== p.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.mini-stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px; }
.mini-stats div { background: var(--paper); border: 1px solid var(--line); border-radius: 12px; padding: 12px 16px; display: flex; align-items: center; justify-content: space-between; }
.mini-stats span { font-size: 9.5px; color: var(--muted); }
.mini-stats strong { font-family: var(--font-display); font-size: 21px; font-weight: 400; }
.c-primary { color: var(--primary); }
.c-sage { color: var(--sage-dark); }
.c-amber { color: #9a6b22; }
.table-note { font-size: 9.5px; color: var(--muted); }
.health-text { font-size: 10px; color: var(--muted); display: block; max-width: 160px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.img-upload-row { display: flex; gap: 10px; flex-wrap: wrap; }
.img-item { position: relative; width: 86px; height: 86px; border-radius: 10px; overflow: hidden; }
.img-item img { width: 100%; height: 100%; object-fit: cover; }
.img-item button { position: absolute; right: 4px; top: 4px; width: 18px; height: 18px; border-radius: 50%; border: none; background: rgba(0,0,0,.5); color: #fff; font-size: 11px; cursor: pointer; }
.upload-box { width: 86px; height: 86px; border: 1px dashed var(--line-strong); border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); }
.upload-box span { font-size: 18px; }
.upload-box small { font-size: 8.5px; }
@media (max-width: 700px) {
  .mini-stats { grid-template-columns: 1fr 1fr; }
  .a-table { min-width: 760px; }
  .a-card { overflow-x: auto; }
}
</style>
