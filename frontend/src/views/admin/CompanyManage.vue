<template>
  <div>
    <div class="admin-page-head">
      <div><h1>物流公司</h1><p>维护合作物流公司与客服信息</p></div>
      <div class="actions"><button class="a-btn primary" @click="openDialog()">＋ 添加公司</button></div>
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>公司</th><th>客服电话</th><th>说明</th><th>合作状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="c in list" :key="c.id">
            <td><div class="main-cell"><span class="company-logo">{{ c.name.slice(0, 1) }}</span><strong>{{ c.name }}</strong></div></td>
            <td>{{ c.phone }}</td>
            <td>{{ c.description }}</td>
            <td><span class="status-tag ok">合作中</span></td>
            <td><div class="op"><button class="a-link" @click="openDialog(c)">编辑</button><button class="a-link danger" @click="remove(c)">删除</button></div></td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="a-empty"><div class="icon">🚚</div><p>暂无物流公司</p></div>
    </div>

    <el-dialog :title="form.id ? '编辑物流公司' : '添加物流公司'" :visible.sync="dialogVisible" width="440px">
      <el-form :model="form" label-position="top">
        <el-form-item label="公司名称"><el-input v-model="form.name" placeholder="例如：顺丰速运" /></el-form-item>
        <el-form-item label="客服电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
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
import { demoCompanies } from '../../utils/demo'

export default {
  name: 'CompanyManage',
  data() { return { list: demoCompanies, dialogVisible: false, form: {} } },
  created() {
    document.title = '物流公司 · 暖爪管理后台'
    this.load()
  },
  methods: {
    async load() {
      const data = await getData('/company/list', null, demoCompanies)
      if (data && data.length) this.list = data
    },
    openDialog(c) {
      this.form = c ? Object.assign({}, c) : { name: '', phone: '', description: '' }
      this.dialogVisible = true
    },
    async save() {
      if (!this.form.name) return this.$message.warning('请填写公司名称')
      if (this.form.id) await putData('/company', this.form, { ok: true })
      else await postData('/company', this.form, { ok: true })
      if (this.form.id) {
        const i = this.list.findIndex(x => x.id === this.form.id)
        if (i >= 0) this.$set(this.list, i, Object.assign({}, this.form))
      } else {
        this.list.push(Object.assign({}, this.form, { id: Date.now() }))
      }
      this.dialogVisible = false
      this.$message.success('已保存')
    },
    async remove(c) {
      try { await this.$confirm(`确定删除「${c.name}」吗？`, '删除物流公司', { type: 'warning' }) } catch (e) { return }
      await deleteData('/company/' + c.id, { ok: true })
      this.list = this.list.filter(i => i.id !== c.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.company-logo { width: 32px; height: 32px; border-radius: 9px; background: var(--cream-2); color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; }
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 620px; }
}
</style>
