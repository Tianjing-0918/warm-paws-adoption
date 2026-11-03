<template>
  <div class="auth-page">
    <div class="auth-visual">
      <img src="/media/animals/scene-login.jpg" class="visual-bg" alt="" />
      <div class="visual-shade"></div>
      <div class="visual-top">
        <BrandLogo light />
        <router-link to="/home" class="back-home">返回首页</router-link>
      </div>
      <div class="visual-copy">
        <span class="visual-eyebrow">WELCOME HOME</span>
        <h1>每一次回来，<br />都离它们更近一点。</h1>
        <p>登录后，你可以提交领养申请、记录救助进度，也可以把和毛孩子的故事分享给更多人。</p>
      </div>
      <div class="visual-quote">
        <span>“</span>
        <p>领养不是一时心动，<br />是决定陪它走完一生。</p>
        <small>—— 暖爪之家领养协议</small>
      </div>
      <div class="visual-stats">
        <div><strong>386</strong><span>累计救助</span></div>
        <div><strong>214</strong><span>成功回家</span></div>
        <div><strong>1286</strong><span>爱心伙伴</span></div>
      </div>
    </div>

    <div class="auth-form-side">
      <div class="mobile-brand"><BrandLogo /></div>
      <div class="auth-card">
        <span class="eyebrow">WELCOME BACK</span>
        <h2>欢迎回来</h2>
        <p class="auth-sub">继续你与毛孩子的故事。</p>

        <el-form ref="loginForm" :model="form" :rules="rules" label-position="top" @submit.native.prevent>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password @keyup.enter.native="handleLogin" />
          </el-form-item>
          <div class="form-aux">
            <el-checkbox v-model="remember">记住我</el-checkbox>
            <button type="button" class="text-btn" @click="showReset = true">忘记密码？</button>
          </div>
          <el-button type="primary" class="submit-btn" :loading="loading" native-type="submit" @click="handleLogin">
            登 录
          </el-button>
        </el-form>

        <div class="demo-box">
          <div class="demo-title"><span></span>演示体验<span></span></div>
          <p>用于毕业设计展示，可一键进入不同角色（使用本地演示数据）。</p>
          <div class="demo-btns">
            <button @click="demoLogin(0)">普通用户</button>
            <button @click="demoLogin(2)">超级管理员</button>
          </div>
        </div>

        <p class="switch-note">还没有账号？<router-link to="/register">创建一个账号</router-link></p>
      </div>
      <p class="auth-foot">登录即表示你同意《用户协议》与《隐私政策》</p>
    </div>

    <el-dialog title="找回密码" :visible.sync="showReset" width="420px">
      <p class="dialog-tip">输入注册邮箱，我们会发送一封包含验证码的邮件。</p>
      <el-form :model="resetForm" label-position="top">
        <el-form-item label="注册邮箱">
          <el-input v-model="resetForm.email" placeholder="name@example.com" />
        </el-form-item>
        <el-form-item label="邮箱验证码">
          <div class="code-row">
            <el-input v-model="resetForm.code" placeholder="6 位验证码" maxlength="6" />
            <button class="code-btn" :disabled="countdown > 0" @click="sendCode">{{ countdown > 0 ? countdown + 's 后重发' : '获取验证码' }}</button>
          </div>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="resetForm.newPassword" type="password" show-password placeholder="至少 6 位字符" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showReset = false">取消</el-button>
        <el-button type="primary" @click="handleReset">重置密码</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import BrandLogo from '../../components/BrandLogo.vue'
import request from '../../utils/request'

const staticDemo = process.env.VUE_APP_STATIC_DEMO === 'true'
const demoAccountRoles = {
  admin: 2,
  linxiao: 1,
  test: 0,
  summer: 0,
  zhou: 0,
  momo: 0
}

export default {
  name: 'Login',
  components: { BrandLogo },
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false,
      remember: true,
      showReset: false,
      resetForm: { email: '', code: '', newPassword: '' },
      countdown: 0,
      timer: null,
      demoReset: false
    }
  },
  created() {
    document.title = '登录 · 暖爪之家'
    const remembered = localStorage.getItem('rememberedUser')
    if (remembered) this.form.username = remembered
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        if (staticDemo) {
          const role = demoAccountRoles[this.form.username]
          this.loading = false
          if (role === undefined || this.form.password !== '123456') {
            this.$message.warning('静态演示站请使用 admin / 123456 或 test / 123456')
            return
          }
          await this.demoLogin(role)
          return
        }
        try {
          await this.$store.dispatch('login', this.form)
          if (this.remember) localStorage.setItem('rememberedUser', this.form.username)
          else localStorage.removeItem('rememberedUser')
          this.$message.success('登录成功，欢迎回来')
          const role = Number(localStorage.getItem('role') || 0)
          const redirect = this.$route.query.redirect
          if (redirect) this.$router.push(redirect)
          else this.$router.push(role >= 1 ? '/admin' : '/home')
        } catch (e) {
          // 错误提示由请求拦截器统一处理
        }
        this.loading = false
      })
    },
    async demoLogin(role) {
      await this.$store.dispatch('demoLogin', role)
      this.$message.success(role >= 1 ? '已进入管理员演示模式' : '已进入用户演示模式')
      this.$router.push(role >= 1 ? '/admin' : '/home')
    },
    async sendCode() {
      if (!this.resetForm.email || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(this.resetForm.email)) {
        return this.$message.warning('请先输入正确的邮箱地址')
      }
      try {
        await request.post('/user/sendEmailCode', { email: this.resetForm.email })
        this.$message.success('验证码已发送，请查收邮箱')
      } catch (e) {
        this.demoReset = true
        this.$message.info('演示环境：验证码为 123456')
      }
      this.countdown = 60
      this.timer = setInterval(() => {
        this.countdown -= 1
        if (this.countdown <= 0) clearInterval(this.timer)
      }, 1000)
    },
    async handleReset() {
      const r = this.resetForm
      if (!r.email || !r.code || !r.newPassword) return this.$message.warning('请填写完整信息')
      if (r.newPassword.length < 6) return this.$message.warning('新密码至少 6 位')
      try {
        await request.post('/user/resetPassword', r)
        this.$message.success('密码重置成功，请重新登录')
        this.showReset = false
      } catch (e) {
        if (this.demoReset && r.code === '123456') {
          this.$message.success('演示模式：密码已重置，请使用演示登录')
          this.showReset = false
        }
      }
    }
  }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: grid; grid-template-columns: 46% 54%; background: var(--cream); }
.auth-visual { position: relative; min-height: 100vh; overflow: hidden; display: flex; flex-direction: column; padding: 42px 52px; color: #fff; }
.visual-bg { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; object-position: center 38%; transform: scale(1.02); }
.visual-shade { position: absolute; inset: 0; background: linear-gradient(160deg, rgba(24,42,33,.72) 0%, rgba(24,42,33,.45) 45%, rgba(24,42,33,.82) 100%); }
.visual-top { position: relative; z-index: 2; display: flex; align-items: center; justify-content: space-between; }
.back-home { font-size: 12px; color: rgba(255,255,255,.65); border-bottom: 1px solid rgba(255,255,255,.25); padding-bottom: 2px; }
.back-home:hover { color: #fff; }
.visual-copy { position: relative; z-index: 2; margin-top: auto; padding-top: 100px; max-width: 470px; }
.visual-eyebrow { font-size: 10px; letter-spacing: .22em; color: var(--amber); }
.visual-copy h1 { color: #fff; font-size: clamp(34px, 3.5vw, 50px); line-height: 1.3; letter-spacing: -.045em; margin-top: 14px; }
.visual-copy p { color: rgba(255,255,255,.62); font-size: 13px; line-height: 1.9; margin-top: 16px; max-width: 400px; }
.visual-quote { position: relative; z-index: 2; margin-top: 40px; padding: 22px 24px; border-left: 1px solid rgba(255,255,255,.25); background: rgba(255,255,255,.05); backdrop-filter: blur(8px); border-radius: 0 16px 16px 0; }
.visual-quote > span { position: absolute; top: 4px; left: 14px; font-family: Georgia, serif; font-size: 44px; color: rgba(229,178,104,.5); line-height: 1; }
.visual-quote p { font-family: var(--font-display); font-size: 16px; line-height: 1.7; color: rgba(255,255,255,.9); padding-left: 18px; }
.visual-quote small { display: block; font-size: 10px; color: rgba(255,255,255,.42); margin-top: 8px; padding-left: 18px; }
.visual-stats { position: relative; z-index: 2; display: flex; gap: 36px; margin-top: 44px; padding-top: 24px; border-top: 1px solid rgba(255,255,255,.14); }
.visual-stats div { display: flex; flex-direction: column; }
.visual-stats strong { font-family: var(--font-display); font-size: 25px; font-weight: 400; }
.visual-stats span { font-size: 10px; color: rgba(255,255,255,.45); margin-top: 3px; }

.auth-form-side { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px; position: relative; }
.mobile-brand { display: none; margin-bottom: 24px; }
.auth-card { width: min(400px, 100%); }
.auth-card h2 { font-size: 34px; letter-spacing: -.04em; margin-top: 10px; }
.auth-sub { font-size: 13px; color: var(--muted); margin-top: 8px; margin-bottom: 28px; }
.form-aux { display: flex; align-items: center; justify-content: space-between; margin: -4px 0 20px; }
.text-btn { border: none; background: none; color: var(--primary); font-size: 12px; cursor: pointer; padding: 0; }
.submit-btn { width: 100%; height: 48px; border-radius: 12px; background: var(--primary); border-color: var(--primary); font-size: 14px; letter-spacing: .12em; }
.submit-btn:hover { background: var(--primary-dark); }
.demo-box { margin-top: 28px; padding: 16px 18px; background: var(--cream-2); border-radius: 14px; }
.demo-title { display: flex; align-items: center; gap: 10px; font-size: 10px; color: var(--muted); letter-spacing: .1em; }
.demo-title span { flex: 1; height: 1px; background: var(--line-strong); }
.demo-box p { font-size: 10.5px; color: var(--muted); margin-top: 8px; line-height: 1.6; }
.demo-btns { display: flex; gap: 8px; margin-top: 12px; }
.demo-btns button { flex: 1; height: 34px; border-radius: 8px; border: 1px solid var(--line-strong); background: var(--paper); color: var(--ink-2); font-size: 11.5px; cursor: pointer; transition: all .2s; }
.demo-btns button:hover { border-color: var(--primary); color: var(--primary); background: #fff; }
.switch-note { text-align: center; font-size: 12px; color: var(--muted); margin-top: 24px; }
.switch-note a { color: var(--primary); font-weight: 600; }
.auth-foot { font-size: 10px; color: var(--muted-2); margin-top: 40px; }
.dialog-tip { font-size: 12px; color: var(--muted); margin-bottom: 18px; line-height: 1.7; }
.code-row { display: flex; gap: 10px; }
.code-btn { width: 120px; border-radius: 10px; border: 1px solid var(--line); background: var(--paper); color: var(--ink-2); font-size: 11.5px; cursor: pointer; }
.code-btn:hover:not(:disabled) { border-color: var(--primary); color: var(--primary); }
.code-btn:disabled { color: var(--muted-2); cursor: not-allowed; }

@media (max-width: 980px) {
  .auth-page { grid-template-columns: 1fr; }
  .auth-visual { display: none; }
  .auth-form-side { min-height: 100vh; padding: 28px 22px; }
  .mobile-brand { display: block; align-self: flex-start; }
  .auth-card { background: var(--paper); padding: 32px 26px; border-radius: 22px; border: 1px solid var(--line); box-shadow: var(--shadow-sm); }
  .auth-foot { margin-top: 24px; }
}
</style>
