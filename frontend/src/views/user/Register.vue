<template>
  <div class="auth-page">
    <div class="auth-visual">
      <img src="/images/pet4.jpg" class="visual-bg" alt="" />
      <div class="visual-shade"></div>
      <div class="visual-top">
        <BrandLogo light />
        <router-link to="/home" class="back-home">返回首页</router-link>
      </div>
      <div class="visual-copy">
        <span class="visual-eyebrow">JOIN THE FAMILY</span>
        <h1>成为它们<br />回家路上的一束光。</h1>
        <p>注册后，你可以领养、上报救助、申请寄养，也可以参与志愿活动，认识一群同样温柔的人。</p>
      </div>
      <div class="steps-mini">
        <div><span>01</span><p><strong>创建账号</strong><small>使用邮箱完成验证</small></p></div>
        <div><span>02</span><p><strong>完善资料</strong><small>让我们更了解你</small></p></div>
        <div><span>03</span><p><strong>开始相遇</strong><small>浏览待领养的小家伙</small></p></div>
      </div>
    </div>

    <div class="auth-form-side">
      <div class="mobile-brand"><BrandLogo /></div>
      <div class="auth-card">
        <span class="eyebrow">CREATE ACCOUNT</span>
        <h2>加入暖爪之家</h2>
        <p class="auth-sub">只需一分钟，开启你与毛孩子的故事。</p>

        <el-form ref="registerForm" :model="form" :rules="rules" label-position="top">
          <div class="form-grid">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="怎么称呼你" />
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="用于登录，字母或数字" />
            </el-form-item>
          </div>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="name@example.com" prefix-icon="el-icon-message" />
          </el-form-item>
          <el-form-item label="邮箱验证码" prop="code">
            <div class="code-row">
              <el-input v-model="form.code" placeholder="6 位验证码" maxlength="6" />
              <button type="button" class="code-btn" :disabled="countdown > 0" @click="sendCode">{{ countdown > 0 ? countdown + 's' : '获取验证码' }}</button>
            </div>
          </el-form-item>
          <div class="form-grid">
            <el-form-item label="设置密码" prop="password">
              <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirm">
              <el-input v-model="form.confirm" type="password" show-password placeholder="再次输入" />
            </el-form-item>
          </div>
          <el-checkbox v-model="agree" class="agree">我已阅读并同意《用户协议》与《隐私政策》，愿意善待每一个生命。</el-checkbox>
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">创建账号</el-button>
        </el-form>

        <p class="switch-note">已经有账号了？<router-link to="/login">直接登录</router-link></p>
      </div>
      <p class="auth-foot">我们不会向任何第三方出售你的个人信息</p>
    </div>
  </div>
</template>

<script>
import BrandLogo from '../../components/BrandLogo.vue'
import request from '../../utils/request'

export default {
  name: 'Register',
  components: { BrandLogo },
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) callback(new Error('两次输入的密码不一致'))
      else callback()
    }
    return {
      form: { nickname: '', username: '', email: '', code: '', password: '', confirm: '' },
      rules: {
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '用户名为 3–20 个字符', trigger: 'blur' }],
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
        password: [{ required: true, message: '请设置密码', trigger: 'blur' }, { min: 6, message: '密码至少 6 位', trigger: 'blur' }],
        confirm: [{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
      },
      loading: false,
      agree: true,
      countdown: 0,
      timer: null,
      demoCode: false
    }
  },
  created() { document.title = '注册 · 暖爪之家' },
  beforeDestroy() { clearInterval(this.timer) },
  methods: {
    async sendCode() {
      if (!this.form.email || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(this.form.email)) {
        return this.$message.warning('请先输入正确的邮箱')
      }
      try {
        await request.post('/user/sendEmailCode', { email: this.form.email })
        this.$message.success('验证码已发送，请查收邮箱')
      } catch (e) {
        this.demoCode = true
        this.form.code = '123456'
        this.$message.info('演示环境：验证码已自动填入 123456')
      }
      this.countdown = 60
      this.timer = setInterval(() => {
        this.countdown -= 1
        if (this.countdown <= 0) clearInterval(this.timer)
      }, 1000)
    },
    handleRegister() {
      this.$refs.registerForm.validate(async valid => {
        if (!valid) return
        if (!this.agree) return this.$message.warning('请先同意用户协议')
        this.loading = true
        try {
          await request.post('/user/register', {
            username: this.form.username,
            nickname: this.form.nickname,
            email: this.form.email,
            code: this.form.code,
            password: this.form.password
          })
          this.$message.success('注册成功，欢迎加入暖爪之家')
          this.$router.push('/login')
        } catch (e) {
          if (this.demoCode) {
            this.$store.dispatch('demoLogin', 0)
            this.$message.success('演示模式：已为你创建体验账号')
            this.$router.push('/home')
          }
        }
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: grid; grid-template-columns: 46% 54%; background: var(--cream); }
.auth-visual { position: relative; min-height: 100vh; overflow: hidden; display: flex; flex-direction: column; padding: 42px 52px; color: #fff; }
.visual-bg { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; object-position: center; transform: scale(1.02); }
.visual-shade { position: absolute; inset: 0; background: linear-gradient(160deg, rgba(24,42,33,.76) 0%, rgba(24,42,33,.35) 45%, rgba(24,42,33,.85) 100%); }
.visual-top { position: relative; z-index: 2; display: flex; align-items: center; justify-content: space-between; }
.back-home { font-size: 12px; color: rgba(255,255,255,.65); border-bottom: 1px solid rgba(255,255,255,.25); padding-bottom: 2px; }
.back-home:hover { color: #fff; }
.visual-copy { position: relative; z-index: 2; margin-top: auto; padding-top: 100px; max-width: 470px; }
.visual-eyebrow { font-size: 10px; letter-spacing: .22em; color: var(--amber); }
.visual-copy h1 { color: #fff; font-size: clamp(34px, 3.5vw, 50px); line-height: 1.3; letter-spacing: -.045em; margin-top: 14px; }
.visual-copy p { color: rgba(255,255,255,.62); font-size: 13px; line-height: 1.9; margin-top: 16px; max-width: 400px; }
.steps-mini { position: relative; z-index: 2; display: flex; flex-direction: column; gap: 16px; margin-top: 42px; padding: 22px 24px; border-radius: 18px; background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.1); backdrop-filter: blur(10px); }
.steps-mini div { display: flex; align-items: center; gap: 14px; }
.steps-mini span { font-family: var(--font-display); font-size: 18px; color: var(--amber); width: 28px; }
.steps-mini p { display: flex; flex-direction: column; line-height: 1.4; }
.steps-mini strong { font-size: 12px; color: rgba(255,255,255,.88); }
.steps-mini small { font-size: 10px; color: rgba(255,255,255,.4); margin-top: 2px; }

.auth-form-side { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px; }
.mobile-brand { display: none; margin-bottom: 24px; }
.auth-card { width: min(430px, 100%); }
.auth-card h2 { font-size: 34px; letter-spacing: -.04em; margin-top: 10px; }
.auth-sub { font-size: 13px; color: var(--muted); margin-top: 8px; margin-bottom: 24px; }
.submit-btn { width: 100%; height: 48px; border-radius: 12px; background: var(--primary); border-color: var(--primary); font-size: 14px; letter-spacing: .08em; margin-top: 18px; }
.submit-btn:hover { background: var(--primary-dark); }
.code-row { display: flex; gap: 10px; }
.code-btn { width: 116px; border-radius: 10px; border: 1px solid var(--line); background: var(--paper); color: var(--ink-2); font-size: 11.5px; cursor: pointer; }
.code-btn:hover:not(:disabled) { border-color: var(--primary); color: var(--primary); }
.code-btn:disabled { color: var(--muted-2); cursor: not-allowed; }
.agree { font-size: 11px; color: var(--muted); white-space: normal; line-height: 1.6; margin-top: 6px; }
.switch-note { text-align: center; font-size: 12px; color: var(--muted); margin-top: 24px; }
.switch-note a { color: var(--primary); font-weight: 600; }
.auth-foot { font-size: 10px; color: var(--muted-2); margin-top: 34px; }

@media (max-width: 980px) {
  .auth-page { grid-template-columns: 1fr; }
  .auth-visual { display: none; }
  .auth-form-side { min-height: 100vh; padding: 28px 22px; }
  .mobile-brand { display: block; align-self: flex-start; }
  .auth-card { background: var(--paper); padding: 30px 24px; border-radius: 22px; border: 1px solid var(--line); box-shadow: var(--shadow-sm); }
  .auth-card .form-grid { grid-template-columns: 1fr; gap: 0; }
}
</style>
