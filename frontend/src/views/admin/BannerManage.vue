<template>
  <div>
    <div class="admin-page-head">
      <div><h1>首页轮播</h1><p>管理首页主视觉的推荐动物与跳转链接</p></div>
      <div class="actions"><button class="a-btn primary" @click="openDialog()">＋ 添加轮播</button></div>
    </div>

    <div class="banner-layout">
      <div class="a-card">
        <div class="a-card-head"><div><h3>轮播列表</h3><span class="sub">按排序值从小到大展示</span></div></div>
        <table class="a-table">
          <thead><tr><th>预览</th><th>标题 / 副标题</th><th>跳转链接</th><th>排序</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="b in list" :key="b.id">
              <td><img :src="imageUrl(b.image)" class="banner-thumb" :alt="b.title" /></td>
              <td><div class="banner-text"><strong>{{ b.title }}</strong><small>{{ b.subtitle }}</small></div></td>
              <td><code>{{ b.link || '—' }}</code></td>
              <td>{{ b.sortOrder }}</td>
              <td><span class="status-tag" :class="b.status === 0 ? 'ok' : ''">{{ b.status === 0 ? '启用中' : '已禁用' }}</span></td>
              <td>
                <div class="op">
                  <button class="a-link" @click="openDialog(b)">编辑</button>
                  <button class="a-link" @click="toggle(b)">{{ b.status === 0 ? '禁用' : '启用' }}</button>
                  <button class="a-link danger" @click="remove(b)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="!list.length" class="a-empty"><div class="icon">🖼️</div><p>暂无轮播图</p></div>
      </div>

      <div class="side-col">
        <div class="a-card">
          <div class="a-card-head"><h3>首页预览</h3><span class="sub">主视觉效果</span></div>
          <div class="preview-box">
            <img :src="imageUrl((list[0] || {}).image)" alt="" />
            <div class="preview-shade"></div>
            <div class="preview-label">
              <span>等待一个家</span>
              <strong>{{ (list[0] || {}).title || '还没有轮播图' }}</strong>
            </div>
            <div class="preview-dots"><i class="active"></i><i></i><i></i></div>
          </div>
          <p class="preview-tip">建议使用 4:5 竖版照片，主体居中，避免裁掉动物的脸。</p>
        </div>
        <div class="a-card guide-card">
          <div class="a-card-head"><h3>使用建议</h3></div>
          <div class="guide-body">
            <p><strong>排序</strong>数字越小越靠前，首页每 6.5 秒自动切换。</p>
            <p><strong>链接</strong>填写站内路径，例如 <code>/pet/5</code>，点击图片即可进入动物详情。</p>
            <p><strong>状态</strong>禁用后不会出现在首页，但仍保留在后台。</p>
          </div>
        </div>
      </div>
    </div>

    <el-dialog :title="form.id ? '编辑轮播' : '添加轮播'" :visible.sync="dialogVisible" width="560px">
      <el-form :model="form" label-position="top">
        <div class="dialog-grid">
          <el-form-item label="标题"><el-input v-model="form.title" placeholder="例如：三只小暹罗" /></el-form-item>
          <el-form-item label="排序"><el-input v-model.number="form.sortOrder" type="number" /></el-form-item>
        </div>
        <el-form-item label="副标题"><el-input v-model="form.subtitle" placeholder="例如：5 个月 · 等待一个家" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.link" placeholder="/pet/5" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :label="0">启用</el-radio><el-radio :label="1">禁用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="图片">
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
        <el-button type="primary" :loading="saving" @click="save">保存轮播</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, putData, deleteData, imageUrl } from '../../utils/api'
import { demoBanners } from '../../utils/demo'

export default {
  name: 'BannerManage',
  data() {
    return {
      list: demoBanners,
      dialogVisible: false,
      saving: false,
      form: {}
    }
  },
  created() {
    document.title = '首页轮播 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl,
    async load() {
      const data = await getData('/banner/adminPage', null, demoBanners)
      if (data && data.length) this.list = data
    },
    openDialog(b) {
      this.form = b ? Object.assign({}, b) : { status: 0, sortOrder: this.list.length + 1, title: '', subtitle: '', link: '', image: '' }
      this.dialogVisible = true
    },
    onUpload(res) {
      if (res && res.code === 200) this.form.image = '/api' + res.data.url
      else this.$message.warning('上传失败，请检查后端服务')
    },
    async save() {
      if (!this.form.title || !this.form.image) return this.$message.warning('请填写标题并上传图片')
      this.saving = true
      if (this.form.id) await putData('/banner', this.form, { ok: true })
      else await postData('/banner', this.form, { ok: true })
      if (this.form.id) {
        const i = this.list.findIndex(x => x.id === this.form.id)
        if (i >= 0) this.$set(this.list, i, Object.assign({}, this.form))
      } else {
        this.list.push(Object.assign({}, this.form, { id: Date.now() }))
      }
      this.saving = false
      this.dialogVisible = false
      this.$message.success('轮播已保存')
      this.load()
    },
    async toggle(b) {
      const status = b.status === 0 ? 1 : 0
      await putData('/banner', Object.assign({}, b, { status }), { ok: true })
      b.status = status
      this.$message.success(status === 0 ? '已启用' : '已禁用')
    },
    async remove(b) {
      try { await this.$confirm(`确定删除轮播「${b.title}」吗？`, '删除轮播', { type: 'warning' }) } catch (e) { return }
      await deleteData('/banner/' + b.id, { ok: true })
      this.list = this.list.filter(i => i.id !== b.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.banner-layout { display: grid; grid-template-columns: 1fr 300px; gap: 14px; align-items: start; }
.banner-thumb { width: 64px; height: 78px; border-radius: 10px; object-fit: cover; }
.banner-text { display: flex; flex-direction: column; }
.banner-text strong { font-size: 11.5px; color: var(--ink); }
.banner-text small { font-size: 9px; color: var(--muted); margin-top: 3px; }
code { background: var(--cream-2); padding: 2px 6px; border-radius: 4px; font-size: 9.5px; color: var(--ink-2); }
.side-col { display: flex; flex-direction: column; gap: 14px; }
.preview-box { position: relative; margin: 16px; height: 260px; border-radius: 18px; overflow: hidden; background: var(--cream-2); }
.preview-box img { width: 100%; height: 100%; object-fit: cover; }
.preview-shade { position: absolute; inset: 0; background: linear-gradient(transparent 50%, rgba(20,30,25,.7)); }
.preview-label { position: absolute; left: 16px; bottom: 16px; display: flex; flex-direction: column; color: #fff; }
.preview-label span { font-size: 8px; background: rgba(255,255,255,.2); border-radius: 999px; padding: 2px 7px; align-self: flex-start; }
.preview-label strong { font-size: 14px; margin-top: 5px; }
.preview-dots { position: absolute; top: 12px; right: 12px; display: flex; gap: 4px; }
.preview-dots i { width: 5px; height: 5px; border-radius: 99px; background: rgba(255,255,255,.5); }
.preview-dots i.active { width: 14px; background: var(--amber); }
.preview-tip { font-size: 9px; color: var(--muted); line-height: 1.7; padding: 0 16px 16px; }
.guide-body { padding: 12px 18px 16px; }
.guide-body p { font-size: 9.5px; color: var(--muted); line-height: 1.8; margin: 6px 0; }
.guide-body strong { color: var(--ink-2); }
.upload-row { display: flex; gap: 10px; align-items: flex-start; }
.form-img { width: 92px; height: 116px; border-radius: 10px; object-fit: cover; }
.upload-box { width: 92px; height: 116px; border: 1px dashed var(--line-strong); border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); }
.upload-box span { font-size: 18px; }
.upload-box small { font-size: 8.5px; }
@media (max-width: 1000px) {
  .banner-layout { grid-template-columns: 1fr; }
  .side-col { display: none; }
}
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 680px; }
}
</style>
