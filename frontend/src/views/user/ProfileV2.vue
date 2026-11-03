<template>
  <div class="profile-page">
    <section class="profile-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>个人中心</span></div>
        <div class="profile-head">
          <div class="avatar-wrap">
            <img :src="avatarSrc" :alt="displayName" />
            <el-upload action="/api/upload" :show-file-list="false" :before-upload="beforeAvatar" :on-success="onAvatarSuccess" :on-error="onAvatarError" class="avatar-upload">
              <button class="avatar-edit">更换头像</button>
            </el-upload>
          </div>
          <div class="profile-info">
            <div class="name-line">
              <h1>{{ displayName }}</h1>
              <span class="role-pill">{{ roleLabel }}</span>
            </div>
            <p>@{{ user.username }} · 加入暖爪之家 {{ formatDate(user.createTime) }}</p>
            <div class="profile-quote">“愿每一份温柔，都能被好好接住。”</div>
          </div>
          <div class="profile-stats">
            <div><strong>{{ adoptions.length }}</strong><span>领养申请</span></div>
            <div><strong>{{ checkins.length }}</strong><span>试养打卡</span></div>
            <div><strong>{{ orders.length }}</strong><span>公益订单</span></div>
          </div>
        </div>
      </div>
    </section>

    <section class="container profile-body">
      <aside class="profile-nav">
        <button v-for="t in tabs" :key="t.key" :class="{ active: tab === t.key }" @click="tab = t.key">
          <span class="nav-icon" v-html="t.icon"></span>
          <span>{{ t.label }}</span>
          <span v-if="t.count" class="nav-count">{{ t.count }}</span>
        </button>
        <div class="nav-bottom">
          <router-link to="/pets" class="back-link">去看看待领养的孩子 →</router-link>
          <button class="logout-link" @click="logout">退出登录</button>
        </div>
      </aside>

      <div class="profile-content">
        <!-- 资料 -->
        <div v-if="tab === 'info'" class="panel">
          <div class="panel-head"><div><span class="eyebrow">PROFILE</span><h2>个人资料</h2></div><span class="panel-note">完善资料，让志愿者更了解你</span></div>
          <el-form :model="form" label-position="top" class="info-form">
            <div class="form-grid">
              <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
              <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="你希望我们怎么称呼你" /></el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="邮箱"><el-input v-model="form.email" prefix-icon="el-icon-message" /></el-form-item>
              <el-form-item label="手机号"><el-input v-model="form.phone" prefix-icon="el-icon-phone" placeholder="用于接收领养回访通知" /></el-form-item>
            </div>
            <el-form-item label="个人简介">
              <el-input v-model="bio" type="textarea" :rows="3" placeholder="介绍一下你与宠物的故事，或你希望参与的方式…" maxlength="160" show-word-limit />
            </el-form-item>
            <div class="form-actions"><el-button @click="loadUser">取消</el-button><el-button type="primary" :loading="saving" @click="saveInfo">保存修改</el-button></div>
          </el-form>
        </div>

        <!-- 领养申请 -->
        <div v-else-if="tab === 'adopt'" class="panel">
          <div class="panel-head"><div><span class="eyebrow">ADOPTION</span><h2>我的领养申请</h2></div><router-link to="/pets" class="link-more">再领养一只 →</router-link></div>
          <div v-if="adoptions.length" class="adopt-list">
            <div v-for="a in adoptions" :key="a.id" class="adopt-card">
              <img :src="imageUrl(a.petImage)" :alt="a.petName" />
              <div class="adopt-info">
                <div class="adopt-top">
                  <div><strong>{{ a.petName }}</strong><small>申请编号 #{{ String(a.id).padStart(4, '0') }} · {{ formatDate(a.createTime) }}</small></div>
                  <span class="pill" :class="adoptClass(a.status)">{{ adoptStatus(a.status) }}</span>
                </div>
                <div class="adopt-track">
                  <span class="done">提交申请</span><i></i>
                  <span :class="{ done: a.status !== 0 }">审核沟通</span><i></i>
                  <span :class="{ done: a.status === 1 }">{{ a.status === 1 ? '试养 / 已通过' : '等待结果' }}</span>
                </div>
                <p v-if="a.reply" class="reply"><strong>救助站回复：</strong>{{ a.reply }}</p>
                <p v-else class="reply pending">志愿者会在 1–2 个工作日内与你联系，请保持电话畅通。</p>
              </div>
            </div>
          </div>
          <div v-else class="empty-state"><div class="icon">🐾</div><p>还没有领养申请，去认识一下待领养的小伙伴吧。</p><router-link to="/pets" class="btn btn-primary btn-sm">进入领养中心</router-link></div>
        </div>

        <!-- 打卡 -->
        <div v-else-if="tab === 'checkin'" class="panel">
          <div class="panel-head">
            <div><span class="eyebrow">TRIAL CHECK-IN</span><h2>试养打卡</h2></div>
            <el-button type="primary" size="small" @click="checkinVisible = true">今日打卡</el-button>
          </div>
          <div class="checkin-banner">
            <div><strong>咪咪 · 试养第 12 天</strong><p>距离正式领养还有 2 天，继续保持每日打卡吧。</p></div>
            <div class="progress-ring"><span>12</span><small>/14 天</small></div>
          </div>
          <div class="checkin-list">
            <div v-for="c in checkins" :key="c.id" class="checkin-card">
              <div class="checkin-date"><strong>{{ c.date.slice(8, 10) }}</strong><small>{{ c.date.slice(5, 7) }} 月</small></div>
              <div class="checkin-main">
                <div class="checkin-top"><strong>{{ c.petName }} 的今日状态</strong><span class="pill">正常</span></div>
                <p>{{ c.content }}</p>
              </div>
              <img v-if="c.images" :src="imageUrl(c.images)" alt="" class="checkin-img" />
            </div>
          </div>
        </div>

        <!-- 寄养 -->
        <div v-else-if="tab === 'foster'" class="panel">
          <div class="panel-head"><div><span class="eyebrow">BOARDING</span><h2>我的寄养记录</h2></div><router-link to="/foster" class="link-more">申请寄养 →</router-link></div>
          <div v-if="fosters.length" class="simple-list">
            <div v-for="f in fosters" :key="f.id" class="simple-card">
              <div class="simple-icon">🏠</div>
              <div class="simple-main"><strong>{{ f.petName }}</strong><span>{{ f.categoryName }} · {{ f.startDate }} 至 {{ f.endDate }}</span></div>
              <div class="simple-side"><span class="pill" :class="f.status === 1 ? '' : 'pill-amber'">{{ ['待处理', '已分配房间', '已完成', '已拒绝'][f.status] }}</span><small>{{ f.roomNumber || '房间待分配' }}</small></div>
            </div>
          </div>
          <div v-else class="empty-state"><div class="icon">🏠</div><p>还没有寄养记录。</p></div>
        </div>

        <!-- 上报 -->
        <div v-else-if="tab === 'report'" class="panel">
          <div class="panel-head"><div><span class="eyebrow">REPORTS</span><h2>我的救助上报</h2></div><router-link to="/report" class="link-more">再次上报 →</router-link></div>
          <div v-if="reports.length" class="simple-list">
            <div v-for="r in reports" :key="r.id" class="simple-card report-simple">
              <img :src="imageUrl(r.images)" alt="" />
              <div class="simple-main"><strong>{{ r.location }}</strong><span>{{ r.description }}</span></div>
              <div class="simple-side"><span class="pill" :class="r.status === 1 ? '' : 'pill-amber'">{{ ['待处理', '已处理', '已拒绝'][r.status] }}</span><small>{{ formatDate(r.createTime) }}</small></div>
            </div>
          </div>
          <div v-else class="empty-state"><div class="icon">📮</div><p>还没有上报记录。</p></div>
        </div>

        <!-- 订单 -->
        <div v-else-if="tab === 'orders'" class="panel">
          <div class="panel-head"><div><span class="eyebrow">ORDERS</span><h2>我的公益订单</h2></div><router-link to="/orders" class="link-more">查看全部订单 →</router-link></div>
          <div v-if="orders.length" class="order-mini-list">
            <div v-for="o in orders.slice(0, 4)" :key="o.id" class="order-mini" @click="$router.push('/orders')">
              <div class="om-head"><span>{{ o.orderNo }}</span><span class="pill">{{ ['待付款', '待发货', '运输中', '已完成', '已取消'][o.status] }}</span></div>
              <div class="om-body">
                <img v-for="item in (o.items || []).slice(0, 3)" :key="item.productId" :src="imageUrl(item.productImage)" alt="" />
                <div class="om-total"><span>合计</span><strong>¥{{ Number(o.totalAmount).toFixed(2) }}</strong></div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state"><div class="icon">📦</div><p>还没有订单，去公益商城看看吧。</p></div>
        </div>

        <!-- 帖子 -->
        <div v-else-if="tab === 'posts'" class="panel">
          <div class="panel-head"><div><span class="eyebrow">MY POSTS</span><h2>我的社区分享</h2></div><router-link to="/community" class="link-more">去社区看看 →</router-link></div>
          <div class="post-simple">
            <div v-for="p in myPosts" :key="p.id" class="post-simple-item">
              <img v-if="p.images" :src="p.images" alt="" />
              <div><strong>{{ p.title }}</strong><p>{{ p.content }}</p><span>{{ formatDate(p.createTime) }} · {{ p.likeCount || 0 }} 个赞</span></div>
            </div>
          </div>
        </div>

        <!-- 安全 -->
        <div v-else class="panel">
          <div class="panel-head"><div><span class="eyebrow">SECURITY</span><h2>账号安全</h2></div></div>
          <div class="security-block">
            <h3>修改登录密码</h3>
            <p>建议定期更换密码，并避免与其他网站使用相同密码。</p>
            <el-form :model="pwdForm" label-position="top" class="pwd-form">
              <el-form-item label="当前密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
              <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少 6 位字符" /></el-form-item>
              <el-form-item label="确认新密码"><el-input v-model="pwdForm.confirm" type="password" show-password /></el-form-item>
              <el-button type="primary" :loading="savingPwd" @click="savePassword">更新密码</el-button>
            </el-form>
          </div>
          <div class="security-block">
            <h3>登录设备</h3>
            <div class="device-row"><span>💻</span><div><strong>当前浏览器 · Windows</strong><small>上海市 · 刚刚活跃</small></div><span class="pill">当前设备</span></div>
          </div>
        </div>
      </div>
    </section>

    <el-dialog title="今日试养打卡" :visible.sync="checkinVisible" width="480px">
      <el-form label-position="top">
        <el-form-item label="今天它怎么样？">
          <el-input v-model="checkinForm.content" type="textarea" :rows="4" placeholder="精神、食欲、排泄、互动情况…" />
        </el-form-item>
        <el-form-item label="上传照片">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="onCheckinUpload">
            <div class="upload-box"><img v-if="checkinForm.images" :src="checkinForm.images" alt="" /><template v-else><span>＋</span><small>添加照片</small></template></div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="checkinVisible = false">取消</el-button><el-button type="primary" @click="submitCheckin">提交打卡</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, putData, imageUrl, formatDate } from '../../utils/api'
import { demoUser, demoAdoptions, demoFosters, demoReports, demoOrders, demoPosts, demoCheckins } from '../../utils/demo'

export default {
  name: 'Profile',
  data() {
    return {
      tab: 'info',
      user: {},
      form: {},
      bio: '',
      saving: false,
      savingPwd: false,
      adoptions: demoAdoptions,
      fosters: demoFosters,
      reports: demoReports,
      orders: demoOrders,
      checkins: demoCheckins,
      pwdForm: { oldPassword: '', newPassword: '', confirm: '' },
      checkinVisible: false,
      checkinForm: { content: '', images: '' },
      tabs: [
        { key: 'info', label: '个人资料', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg>' },
        { key: 'adopt', label: '领养申请', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>' },
        { key: 'checkin', label: '试养打卡', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18M8 15l2 2 4-4"/></svg>' },
        { key: 'foster', label: '寄养记录', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="m3 11 9-7 9 7"/><path d="M5 10v10h14V10"/></svg>' },
        { key: 'report', label: '救助上报', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.4"/></svg>' },
        { key: 'orders', label: '我的订单', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M6 2 3 6v14h18V6l-3-4Z"/><path d="M3 6h18M16 10a4 4 0 0 1-8 0"/></svg>' },
        { key: 'posts', label: '我的分享', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M21 11.5a8.4 8.4 0 0 1-9 8.4 9.6 9.6 0 0 1-3.3-.6L4 21l1.3-3.7A8.2 8.2 0 0 1 3 11.5 8.4 8.4 0 0 1 12 3a8.4 8.4 0 0 1 9 8.5Z"/></svg>' },
        { key: 'security', label: '账号安全', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="4" y="10" width="16" height="11" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>' }
      ]
    }
  },
  computed: {
    displayName() { return this.user.nickname || this.user.username || '暖爪伙伴' },
    roleLabel() { return ['普通用户', '管理员', '超级管理员'][this.user.role] || '普通用户' },
    avatarSrc() {
      if (!this.user.avatar) return '/media/animals/scene-avatar.jpg'
      return this.user.avatar.indexOf('/upload') === 0 ? '/api' + this.user.avatar : this.user.avatar
    },
    myPosts() {
      return demoPosts.slice(0, 2)
    }
  },
  created() {
    document.title = '个人中心 · 暖爪之家'
    this.user = Object.assign({}, demoUser, this.$store.state.userInfo || {})
    this.form = Object.assign({}, this.user)
    this.bio = localStorage.getItem('userBio') || '喜欢猫，也喜欢在周末去救助站做志愿者。'
    this.loadAll()
  },
  methods: {
    imageUrl, formatDate,
    adoptStatus(s) { return ['审核中', '已通过', '未通过'][s] || '审核中' },
    adoptClass(s) { return s === 1 ? 'pill' : s === 2 ? 'pill pill-danger' : 'pill pill-amber' },
    async loadUser() {
      const data = await getData('/user/info', null, this.user)
      if (data && data.id) {
        this.user = Object.assign({}, this.user, data)
        this.form = Object.assign({}, this.user)
      }
    },
    async loadAll() {
      this.loadUser()
      const [a, f, r, o, c] = await Promise.all([
        getData('/adopt/my', { pageNum: 1, pageSize: 20 }, { list: demoAdoptions }),
        getData('/foster/my', { pageNum: 1, pageSize: 20 }, { list: demoFosters }),
        getData('/report/my', { pageNum: 1, pageSize: 20 }, { list: demoReports }),
        getData('/order/my', { pageNum: 1, pageSize: 20 }, { list: demoOrders }),
        getData('/checkin/my', null, demoCheckins)
      ])
      if (a && a.list) this.adoptions = a.list
      if (f && f.list) this.fosters = f.list
      if (r && r.list) this.reports = r.list
      if (o && o.list) this.orders = o.list
      if (c && c.length) {
        this.checkins = c.map(i => Object.assign({ date: i.checkinDate || String(i.createTime).slice(0, 10) }, i))
      }
      const saved = localStorage.getItem('myCheckins')
      if (saved) {
        try { this.checkins = JSON.parse(saved) } catch (e) {}
      }
    },
    async saveInfo() {
      if (!this.form.nickname) return this.$message.warning('昵称不能为空')
      this.saving = true
      await putData('/user/updateInfo', this.form, { ok: true })
      this.$store.commit('UPDATE_USER', { nickname: this.form.nickname, email: this.form.email, phone: this.form.phone })
      this.user = Object.assign({}, this.user, this.form)
      localStorage.setItem('userBio', this.bio)
      this.saving = false
      this.$message.success('资料已更新')
    },
    beforeAvatar(file) {
      if (!/image\//.test(file.type)) {
        this.$message.warning('请上传图片文件')
        return false
      }
      if (file.size > 3 * 1024 * 1024) {
        this.$message.warning('头像不能超过 3MB')
        return false
      }
      return true
    },
    onAvatarSuccess(res) {
      if (res && res.code === 200) {
        const url = '/api' + res.data.url
        this.user.avatar = url
        this.form.avatar = url
        this.$store.commit('UPDATE_USER', { avatar: url })
        putData('/user/updateInfo', { avatar: url }, { ok: true })
        this.$message.success('头像已更新')
      } else {
        this.$message.warning('头像上传失败')
      }
    },
    onAvatarError() {
      this.$message.warning('上传服务不可用，请确认后端已启动')
    },
    async savePassword() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) return this.$message.warning('请填写完整')
      if (this.pwdForm.newPassword.length < 6) return this.$message.warning('新密码至少 6 位')
      if (this.pwdForm.newPassword !== this.pwdForm.confirm) return this.$message.warning('两次输入的新密码不一致')
      this.savingPwd = true
      await putData('/user/updatePassword', this.pwdForm, { ok: true })
      this.savingPwd = false
      this.pwdForm = { oldPassword: '', newPassword: '', confirm: '' }
      this.$message.success('密码已更新')
    },
    onCheckinUpload(res) {
      if (res && res.code === 200) this.checkinForm.images = '/api' + res.data.url
    },
    async submitCheckin() {
      if (!this.checkinForm.content.trim()) return this.$message.warning('请描述一下今天的情况')
      const date = new Date().toISOString().slice(0, 10)
      await postData('/checkin', {
        petId: 1,
        petName: '咪咪',
        content: this.checkinForm.content,
        images: this.checkinForm.images,
        checkinDate: date
      }, { ok: true })
      this.checkins.unshift({
        id: Date.now(), date, petName: '咪咪', content: this.checkinForm.content, images: this.checkinForm.images
      })
      localStorage.setItem('myCheckins', JSON.stringify(this.checkins))
      this.checkinVisible = false
      this.checkinForm = { content: '', images: '' }
      this.$message.success('打卡成功，今天也辛苦啦')
    },
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/home')
    }
  }
}
</script>

<style scoped>
.profile-hero { background: linear-gradient(130deg, #22362d, #385342); color: #fff; padding: 28px 0 44px; position: relative; overflow: hidden; }
.profile-hero::after { content: ''; position: absolute; width: 500px; height: 500px; border-radius: 50%; right: -120px; top: -260px; border: 1px solid rgba(255,255,255,.08); box-shadow: 0 0 0 70px rgba(255,255,255,.02); }
.profile-hero .breadcrumb { color: rgba(255,255,255,.45); }
.profile-head { display: flex; align-items: center; gap: 22px; margin-top: 30px; position: relative; z-index: 2; }
.avatar-wrap { position: relative; width: 88px; height: 88px; flex-shrink: 0; }
.avatar-wrap img { width: 88px; height: 88px; border-radius: 50%; object-fit: cover; border: 3px solid rgba(255,255,255,.2); }
.avatar-edit { position: absolute; left: 0; right: 0; bottom: -4px; height: 24px; border: none; border-radius: 999px; background: rgba(255,255,255,.9); color: var(--ink); font-size: 8.5px; cursor: pointer; }
.profile-info { flex: 1; }
.name-line { display: flex; align-items: center; gap: 10px; }
.name-line h1 { color: #fff; font-size: 30px; letter-spacing: -.03em; }
.role-pill { font-size: 9px; border: 1px solid rgba(255,255,255,.24); border-radius: 999px; padding: 3px 9px; color: rgba(255,255,255,.7); }
.profile-info > p { font-size: 10.5px; color: rgba(255,255,255,.45); margin-top: 3px; }
.profile-quote { margin-top: 10px; font-family: var(--font-display); font-style: italic; font-size: 12px; color: rgba(255,255,255,.6); }
.profile-stats { display: flex; gap: 40px; padding-right: 20px; }
.profile-stats div { display: flex; flex-direction: column; align-items: center; }
.profile-stats strong { font-family: var(--font-display); font-size: 28px; font-weight: 400; }
.profile-stats span { font-size: 9.5px; color: rgba(255,255,255,.45); }
.profile-body { display: grid; grid-template-columns: 210px 1fr; gap: 30px; padding-top: 32px; padding-bottom: 60px; align-items: start; }
.profile-nav { display: flex; flex-direction: column; gap: 3px; position: sticky; top: 100px; }
.profile-nav > button { display: flex; align-items: center; gap: 10px; border: none; background: transparent; padding: 10px 13px; border-radius: 10px; font-size: 12px; color: var(--ink-2); cursor: pointer; text-align: left; }
.profile-nav > button:hover { background: var(--cream-2); }
.profile-nav > button.active { background: var(--paper); color: var(--ink); font-weight: 700; box-shadow: var(--shadow-sm); }
.nav-icon { width: 18px; height: 18px; color: var(--muted); }
.nav-icon ::v-deep svg { width: 18px; height: 18px; }
.active .nav-icon { color: var(--primary); }
.nav-count { margin-left: auto; font-size: 9px; background: var(--cream-2); border-radius: 999px; padding: 1px 6px; color: var(--muted); }
.nav-bottom { margin-top: 18px; padding-top: 14px; border-top: 1px solid var(--line); display: flex; flex-direction: column; gap: 6px; }
.back-link { font-size: 10.5px; color: var(--primary); padding: 4px 13px; }
.logout-link { border: none; background: none; text-align: left; padding: 4px 13px; font-size: 10.5px; color: var(--muted-2); cursor: pointer; }
.profile-content { min-width: 0; }
.panel { background: var(--paper); border: 1px solid var(--line); border-radius: 22px; padding: 30px 32px; min-height: 420px; }
.panel-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 20px; margin-bottom: 24px; }
.panel-head h2 { font-size: 25px; letter-spacing: -.035em; margin-top: 7px; }
.panel-note { font-size: 9.5px; color: var(--muted-2); }
.info-form { max-width: 680px; }
.form-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px; }
.adopt-list { display: flex; flex-direction: column; gap: 14px; }
.adopt-card { display: flex; gap: 16px; padding: 16px; border: 1px solid var(--line); border-radius: 16px; }
.adopt-card > img { width: 92px; height: 92px; border-radius: 12px; object-fit: cover; }
.adopt-info { flex: 1; }
.adopt-top { display: flex; justify-content: space-between; align-items: flex-start; }
.adopt-top strong { font-size: 15px; }
.adopt-top small { display: block; font-size: 9px; color: var(--muted-2); margin-top: 2px; }
.adopt-track { display: flex; align-items: center; gap: 7px; margin-top: 14px; font-size: 8.5px; color: var(--muted-2); }
.adopt-track span.done { color: var(--sage-dark); font-weight: 600; }
.adopt-track i { flex: 1; height: 1px; background: var(--line); }
.reply { font-size: 10px; color: var(--muted); background: var(--cream-2); border-radius: 8px; padding: 8px 10px; margin-top: 12px; line-height: 1.7; }
.reply.pending { background: #fbf0dc; color: #8a6a2a; }
.checkin-banner { display: flex; justify-content: space-between; align-items: center; background: var(--sage-soft); border-radius: 16px; padding: 20px 24px; }
.checkin-banner strong { font-size: 15px; color: var(--sage-dark); }
.checkin-banner p { font-size: 10.5px; color: var(--sage-dark); opacity: .75; margin-top: 3px; }
.progress-ring { width: 54px; height: 54px; border-radius: 50%; border: 4px solid rgba(127,156,134,.3); border-top-color: var(--sage); display: flex; flex-direction: column; align-items: center; justify-content: center; }
.progress-ring span { font-family: var(--font-display); font-size: 17px; color: var(--sage-dark); line-height: 1; }
.progress-ring small { font-size: 7px; color: var(--sage-dark); }
.checkin-list { display: flex; flex-direction: column; gap: 12px; margin-top: 20px; }
.checkin-card { display: flex; gap: 14px; align-items: flex-start; border-bottom: 1px solid var(--line); padding-bottom: 14px; }
.checkin-date { width: 38px; display: flex; flex-direction: column; align-items: center; background: var(--cream-2); border-radius: 8px; padding: 5px 0; }
.checkin-date strong { font-family: var(--font-display); font-size: 16px; }
.checkin-date small { font-size: 7.5px; color: var(--muted); }
.checkin-main { flex: 1; }
.checkin-top { display: flex; align-items: center; gap: 8px; }
.checkin-top strong { font-size: 11.5px; }
.checkin-main p { font-size: 11px; color: var(--muted); line-height: 1.7; margin-top: 4px; }
.checkin-img { width: 70px; height: 54px; object-fit: cover; border-radius: 8px; }
.simple-list { display: flex; flex-direction: column; gap: 10px; }
.simple-card { display: flex; align-items: center; gap: 14px; padding: 14px 16px; border: 1px solid var(--line); border-radius: 13px; }
.simple-icon { width: 38px; height: 38px; border-radius: 10px; background: var(--cream-2); display: flex; align-items: center; justify-content: center; font-size: 16px; }
.simple-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.simple-main strong { font-size: 12px; }
.simple-main span { font-size: 10px; color: var(--muted); margin-top: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.simple-side { display: flex; flex-direction: column; align-items: flex-end; gap: 3px; }
.simple-side small { font-size: 8.5px; color: var(--muted-2); }
.report-simple img { width: 54px; height: 54px; border-radius: 9px; object-fit: cover; }
.order-mini-list { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.order-mini { border: 1px solid var(--line); border-radius: 14px; padding: 14px; cursor: pointer; }
.om-head { display: flex; justify-content: space-between; font-size: 9px; color: var(--muted); }
.om-body { display: flex; gap: 8px; margin-top: 10px; align-items: center; }
.om-body img { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; }
.om-total { margin-left: auto; text-align: right; }
.om-total span { display: block; font-size: 8px; color: var(--muted-2); }
.om-total strong { font-family: var(--font-display); font-size: 17px; color: var(--primary); }
.post-simple { display: flex; flex-direction: column; gap: 14px; }
.post-simple-item { display: flex; gap: 14px; border-bottom: 1px solid var(--line); padding-bottom: 14px; }
.post-simple-item img { width: 82px; height: 68px; border-radius: 10px; object-fit: cover; }
.post-simple-item strong { font-size: 12.5px; }
.post-simple-item p { font-size: 10.5px; color: var(--muted); line-height: 1.7; margin-top: 3px; }
.post-simple-item span { font-size: 9px; color: var(--muted-2); }
.security-block { padding-bottom: 22px; margin-bottom: 22px; border-bottom: 1px solid var(--line); }
.security-block h3 { font-size: 15px; }
.security-block > p { font-size: 10.5px; color: var(--muted); margin-top: 4px; }
.pwd-form { max-width: 380px; margin-top: 14px; }
.device-row { display: flex; align-items: center; gap: 12px; margin-top: 14px; }
.device-row > span:first-child { font-size: 20px; }
.device-row div { flex: 1; display: flex; flex-direction: column; }
.device-row strong { font-size: 11.5px; }
.device-row small { font-size: 9px; color: var(--muted); }
.upload-box { width: 100px; height: 76px; border: 1px dashed var(--line-strong); border-radius: 9px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); overflow: hidden; }
.upload-box img { width: 100%; height: 100%; object-fit: cover; }

@media (max-width: 900px) {
  .profile-body { grid-template-columns: 1fr; }
  .profile-nav { position: static; flex-direction: row; overflow-x: auto; padding-bottom: 6px; }
  .profile-nav > button { white-space: nowrap; }
  .nav-count, .nav-bottom { display: none; }
  .profile-stats { display: none; }
}
@media (max-width: 600px) {
  .profile-head { flex-wrap: wrap; }
  .panel { padding: 22px 18px; }
  .adopt-card { flex-direction: column; }
  .adopt-card > img { width: 100%; height: 180px; }
  .order-mini-list { grid-template-columns: 1fr; }
  .profile-info { min-width: 0; }
  .name-line h1 { font-size: 24px; }
}
</style>
