<template>
  <div>
    <div class="admin-page-head">
      <div><h1>用户管理</h1><p>管理平台用户、角色与账号状态</p></div>
      <div class="actions"><button class="a-btn" @click="load">刷新</button></div>
    </div>

    <div class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-top"><span>{{ s.label }}</span><span class="stat-icon" v-html="s.icon"></span></div>
        <div class="stat-val">{{ s.value }}</div>
        <div class="stat-foot">{{ s.note }}</div>
      </div>
    </div>

    <div class="a-filter">
      <el-input v-model="keyword" size="small" placeholder="搜索用户名 / 昵称 / 邮箱" clearable style="width:220px" />
      <el-select v-model="roleFilter" size="small" clearable placeholder="全部角色" style="width:130px">
        <el-option label="普通用户" :value="0" /><el-option label="普通管理员" :value="1" /><el-option label="超级管理员" :value="2" />
      </el-select>
      <button class="a-btn sm" @click="load">查询</button>
      <span class="spacer"></span>
      <span class="table-note">共 {{ filtered.length }} 位用户</span>
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>用户</th><th>邮箱</th><th>手机号</th><th>角色</th><th>状态</th><th>注册时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="u in filtered" :key="u.id">
            <td>
              <div class="main-cell">
                <span class="user-avatar" :class="'role-' + u.role">{{ (u.nickname || u.username || '用').slice(0, 1) }}</span>
                <div><strong>{{ u.nickname || u.username }}</strong><small>@{{ u.username }} · ID {{ u.id }}</small></div>
              </div>
            </td>
            <td>{{ u.email || '—' }}</td>
            <td>{{ u.phone || '—' }}</td>
            <td>
              <el-select v-if="isSuper" v-model="u.role" size="mini" class="role-select" @change="changeRole(u)">
                <el-option label="普通用户" :value="0" /><el-option label="普通管理员" :value="1" /><el-option label="超级管理员" :value="2" />
              </el-select>
              <span v-else class="status-tag" :class="u.role === 2 ? 'warn' : u.role === 1 ? 'info' : ''">{{ roleLabel(u.role) }}</span>
            </td>
            <td><span class="status-tag" :class="u.status === 0 ? 'ok' : 'danger'">{{ u.status === 0 ? '正常' : '已禁用' }}</span></td>
            <td>{{ formatDate(u.createTime) }}</td>
            <td>
              <div class="op">
                <button class="a-link" @click="openReset(u)">重置密码</button>
                <button class="a-link" :class="{ danger: u.status === 0 }" @click="toggle(u)">{{ u.status === 0 ? '禁用' : '启用' }}</button>
                <button v-if="isSuper" class="a-link danger" @click="remove(u)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">👥</div><p>没有找到符合条件的用户</p></div>
    </div>

    <el-dialog title="重置用户密码" :visible.sync="resetVisible" width="420px">
      <p class="reset-tip">将为用户 <strong>{{ currentUser && currentUser.nickname }}</strong> 设置一个新密码。请通过安全渠道告知用户，并建议其登录后修改。</p>
      <el-form label-position="top">
        <el-form-item label="新密码">
          <div class="pwd-row">
            <el-input v-model="newPassword" placeholder="至少 6 位" />
            <button class="a-btn sm" @click="randomPwd">随机生成</button>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="resetVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetting" @click="confirmReset">确认重置</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, putData, deleteData, formatDate } from '../../utils/api'
import { demoUsers } from '../../utils/demo'

export default {
  name: 'UserManage',
  data() {
    return {
      list: demoUsers,
      keyword: '',
      roleFilter: null,
      resetVisible: false,
      resetting: false,
      currentUser: null,
      newPassword: '123456'
    }
  },
  computed: {
    isSuper() { return this.$store.state.role >= 2 },
    filtered() {
      let list = this.list.slice()
      if (this.keyword) {
        const k = this.keyword.toLowerCase()
        list = list.filter(u => (u.username || '').toLowerCase().indexOf(k) >= 0 || (u.nickname || '').indexOf(k) >= 0 || (u.email || '').toLowerCase().indexOf(k) >= 0)
      }
      if (this.roleFilter !== null && this.roleFilter !== '') list = list.filter(u => u.role === this.roleFilter)
      return list
    },
    stats() {
      return [
        { label: '注册用户', value: this.list.length, note: '累计注册账号', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0M17 5.5a3.5 3.5 0 0 1 0 7M18.5 14.5A5.5 5.5 0 0 1 22 20"/></svg>' },
        { label: '普通用户', value: this.list.filter(u => u.role === 0).length, note: '近 30 天新增 48 人', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg>' },
        { label: '管理员', value: this.list.filter(u => u.role >= 1).length, note: '含超级管理员', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M12 3 4 6v6c0 5 3.4 8 8 9 4.6-1 8-4 8-9V6l-8-3Z"/><path d="m9 12 2 2 4-4"/></svg>' },
        { label: '已禁用', value: this.list.filter(u => u.status === 1).length, note: '无法登录平台', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="12" cy="12" r="9"/><path d="m6 6 12 12"/></svg>' }
      ]
    }
  },
  created() {
    document.title = '用户管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    formatDate,
    roleLabel(r) { return ['普通用户', '普通管理员', '超级管理员'][r] || '普通用户' },
    async load() {
      const data = await getData('/user/page', { pageNum: 1, pageSize: 100 }, { list: demoUsers })
      if (data && data.list && data.list.length) this.list = data.list
    },
    async changeRole(u) {
      await putData('/user/role/' + u.id, { role: u.role }, { ok: true })
      this.$message.success('角色已更新为' + this.roleLabel(u.role))
    },
    async toggle(u) {
      const action = u.status === 0 ? '禁用' : '启用'
      try {
        await this.$confirm(`确定要${action}用户「${u.nickname || u.username}」吗？`, action + '账号', { type: 'warning' })
      } catch (e) { return }
      await putData('/user/toggleStatus/' + u.id, {}, { ok: true })
      u.status = u.status === 0 ? 1 : 0
      this.$message.success('已' + action)
    },
    async remove(u) {
      try {
        await this.$confirm(`删除后该用户将无法登录，确定删除「${u.nickname || u.username}」吗？`, '删除用户', { type: 'warning' })
      } catch (e) { return }
      await deleteData('/user/' + u.id, { ok: true })
      this.list = this.list.filter(i => i.id !== u.id)
      this.$message.success('用户已删除')
    },
    openReset(u) {
      this.currentUser = u
      this.newPassword = '123456'
      this.resetVisible = true
    },
    randomPwd() {
      this.newPassword = Math.random().toString(36).slice(2, 10)
    },
    async confirmReset() {
      if (this.newPassword.length < 6) return this.$message.warning('密码至少 6 位')
      this.resetting = true
      await putData('/user/resetPasswordByAdmin/' + this.currentUser.id, { newPassword: this.newPassword }, { ok: true })
      this.resetting = false
      this.resetVisible = false
      this.$message.success('密码已重置为：' + this.newPassword)
    }
  }
}
</script>

<style scoped>
.stat-icon { background: var(--cream-2); color: var(--ink-2); }
.table-note { font-size: 9.5px; color: var(--muted); }
.user-avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--cream-2); color: var(--ink-2); display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; }
.user-avatar.role-1 { background: var(--sage-soft); color: var(--sage-dark); }
.user-avatar.role-2 { background: var(--primary-soft); color: var(--primary); }
.role-select { width: 116px; }
.reset-tip { font-size: 11px; color: var(--muted); line-height: 1.8; margin-bottom: 16px; }
.reset-tip strong { color: var(--ink); }
.pwd-row { display: flex; gap: 8px; width: 100%; }
.pwd-row .el-input { flex: 1; }
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 820px; }
}
</style>
