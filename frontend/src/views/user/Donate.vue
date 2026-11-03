<template>
  <div class="donate-page">
    <section class="donate-hero">
      <div class="container donate-hero-grid">
        <div class="hero-copy">
          <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>爱心捐赠</span></div>
          <span class="eyebrow light" style="margin-top:22px">GIVE A LITTLE LOVE</span>
          <h1>你的一点点，<br />是它整个冬天。</h1>
          <p>一笔捐赠可以是一袋猫粮、一支疫苗，或是一间温暖房间里的暖气。我们承诺每一笔善款都公开透明，用于真正需要的地方。</p>
          <div class="hero-badges">
            <span><i>✓</i> 每月公开明细</span>
            <span><i>✓</i> 支持开具捐赠证明</span>
            <span><i>✓</i> 可匿名捐赠</span>
          </div>
        </div>
        <div class="donate-panel">
          <div class="panel-head">
            <span>选择捐赠金额</span>
            <span class="secure">🔒 安全支付</span>
          </div>
          <div class="amounts">
            <button v-for="a in amounts" :key="a.value" :class="{ active: amount === a.value }" @click="amount = a.value">
              <strong>¥{{ a.value }}</strong>
              <small>{{ a.label }}</small>
            </button>
          </div>
          <div class="custom-amount">
            <span>¥</span>
            <input v-model.number="customAmount" type="number" min="1" placeholder="自定义金额" @focus="amount = 'custom'" />
          </div>
          <div class="frequency">
            <button :class="{ active: !monthly }" @click="monthly = false">单次捐赠</button>
            <button :class="{ active: monthly }" @click="monthly = true">每月捐赠</button>
          </div>
          <button class="btn btn-primary btn-block btn-lg" @click="openDonate">立即捐赠 ¥{{ finalAmount }}</button>
          <p class="panel-note">演示项目使用模拟支付，不会产生真实扣款。</p>
        </div>
      </div>
      <div class="container donor-strip">
        <div class="donor-avatars">
          <span v-for="(d, i) in donations.slice(0, 5)" :key="i">{{ d.anonymous ? '爱' : d.donor.slice(0, 1) }}</span>
          <span class="plus">+</span>
        </div>
        <p><strong>{{ donations.length }}</strong> 笔爱心留言，累计筹集 <strong>¥{{ totalRaised.toLocaleString() }}</strong></p>
        <span class="strip-quote">“愿每个小生命都被温柔以待。”</span>
      </div>
    </section>

    <section class="section impact-section">
      <div class="container">
        <SectionHead eyebrow="YOUR GIFT MATTERS" title="你的捐赠，会变成什么" sub="我们尽量把抽象的善意，变成看得见的改变。" />
        <div class="impact-grid">
          <div v-for="i in impacts" :key="i.title" class="impact-card">
            <span class="i-amount">{{ i.amount }}</span>
            <span class="i-icon">{{ i.icon }}</span>
            <h3>{{ i.title }}</h3>
            <p>{{ i.text }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="container transparency">
      <div class="trans-card">
        <div class="trans-copy">
          <span class="eyebrow">TRANSPARENCY</span>
          <h2>每一笔钱，<br />都有它的去处。</h2>
          <p>2026 年 9 月共收到爱心捐赠 ¥12,860，支出明细如下。完整账目可在救助站办公室查阅。</p>
          <router-link to="/community" class="link-more">查看月度公示 →</router-link>
        </div>
        <div class="trans-bars">
          <div v-for="b in breakdown" :key="b.label" class="trans-item">
            <div class="trans-top"><span>{{ b.label }}</span><strong>{{ b.percent }}%</strong></div>
            <div class="bar"><i :style="{ width: b.percent + '%', background: b.color }"></i></div>
            <small>¥{{ b.amount.toLocaleString() }}</small>
          </div>
        </div>
      </div>
    </section>

    <section class="section wall-section">
      <div class="container">
        <SectionHead eyebrow="WALL OF LOVE" title="爱心留言墙" sub="有些名字被记住，有些善意选择匿名。谢谢每一个你。" />
        <div class="wall">
          <div v-for="d in donations" :key="d.id" class="wall-item">
            <div class="wall-head">
              <span class="w-avatar">{{ d.anonymous ? '♥' : d.donor.slice(0, 1) }}</span>
              <div><strong>{{ d.anonymous ? '爱心人士' : d.donor }}</strong><small>{{ formatDate(d.createTime) }}</small></div>
              <span class="w-amount">¥{{ d.amount }}</span>
            </div>
            <p>{{ d.message || '愿它早日遇到那个对的人。' }}</p>
          </div>
        </div>
      </div>
    </section>

    <el-dialog title="确认捐赠" :visible.sync="dialogVisible" width="440px">
      <div class="pay-box">
        <div class="pay-amount">¥{{ finalAmount }}</div>
        <p>{{ monthly ? '每月自动捐赠（演示）' : '单次爱心捐赠' }}</p>
        <el-form label-position="top">
          <el-form-item label="留言（选填）">
            <el-input v-model="message" maxlength="50" show-word-limit placeholder="写一句给毛孩子的话" />
          </el-form-item>
          <el-checkbox v-model="anonymous">匿名捐赠</el-checkbox>
        </el-form>
        <div class="pay-methods">
          <label class="pay-method active"><span>支</span>支付宝沙箱<small>推荐</small></label>
          <label class="pay-method"><span>微</span>微信支付</label>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="paying" @click="confirmDonate">确认捐赠</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import SectionHead from '../../components/SectionHead.vue'
import { getData, postData, formatDate, requireLogin } from '../../utils/api'
import { demoDonations } from '../../utils/demo'

export default {
  name: 'Donate',
  components: { SectionHead },
  data() {
    return {
      amounts: [
        { value: 20, label: '一顿饱饭' },
        { value: 50, label: '一次驱虫' },
        { value: 100, label: '一针疫苗' },
        { value: 300, label: '一份体检' }
      ],
      amount: 50,
      customAmount: '',
      monthly: false,
      dialogVisible: false,
      message: '',
      anonymous: false,
      paying: false,
      donations: demoDonations.slice(),
      totalRaised: 58620,
      impacts: [
        { amount: '¥20', icon: '🍚', title: '一顿饱饭', text: '可以为一只中型犬提供约 3 天的口粮。' },
        { amount: '¥50', icon: '🪱', title: '一次驱虫', text: '帮助一只流浪动物完成体内外驱虫。' },
        { amount: '¥100', icon: '💉', title: '一针疫苗', text: '为它建立第一道健康防线。' },
        { amount: '¥300', icon: '🩺', title: '一次基础体检', text: '血常规、传染病筛查和基础检查。' }
      ],
      breakdown: [
        { label: '粮食与日常用品', percent: 42, amount: 5401, color: '#c9663f' },
        { label: '医疗与绝育', percent: 33, amount: 4244, color: '#7f9c86' },
        { label: '场地与水电', percent: 17, amount: 2186, color: '#e5b268' },
        { label: '宣传与运营', percent: 8, amount: 1029, color: '#8a9bb0' }
      ]
    }
  },
  computed: {
    finalAmount() {
      if (this.amount === 'custom') return Number(this.customAmount || 0)
      return this.amount
    }
  },
  created() {
    document.title = '爱心捐赠 · 暖爪之家'
    this.load()
  },
  methods: {
    formatDate,
    async load() {
      const data = await getData('/donation/list', null, { list: demoDonations, total: 58620 })
      if (data) {
        if (data.list && data.list.length) this.donations = data.list
        if (data.total) this.totalRaised = Number(data.total)
      }
    },
    openDonate() {
      if (!this.finalAmount || this.finalAmount < 1) return this.$message.warning('请选择或输入捐赠金额')
      if (!requireLogin(this, '/donate')) return
      this.dialogVisible = true
    },
    async confirmDonate() {
      this.paying = true
      await postData('/donation', {
        amount: this.finalAmount,
        message: this.message,
        anonymous: this.anonymous,
        monthly: this.monthly
      }, { ok: true })
      this.paying = false
      this.dialogVisible = false
      const donor = this.$store.state.userInfo
      this.donations.unshift({
        id: Date.now(),
        donor: this.anonymous ? '爱心人士' : (donor ? donor.nickname : '爱心伙伴'),
        amount: this.finalAmount,
        message: this.message,
        anonymous: this.anonymous,
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      })
      this.$message.success('谢谢你的善意，愿它被温柔以待')
      this.message = ''
    }
  }
}
</script>

<style scoped>
.donate-hero { background: linear-gradient(130deg, #22362d 0%, #35503f 55%, #4d6a54 100%); color: #fff; padding: 34px 0 0; position: relative; overflow: hidden; }
.donate-hero::before { content: ''; position: absolute; width: 700px; height: 700px; border-radius: 50%; right: -200px; top: -350px; background: radial-gradient(circle, rgba(229,178,104,.16), transparent 65%); }
.donate-hero .breadcrumb { color: rgba(255,255,255,.45); }
.donate-hero-grid { display: grid; grid-template-columns: 1.05fr .95fr; gap: 70px; align-items: center; padding: 50px 0 64px; position: relative; z-index: 2; }
.hero-copy h1 { color: #fff; font-size: clamp(42px, 5vw, 66px); line-height: 1.16; letter-spacing: -.05em; margin-top: 16px; }
.hero-copy p { color: rgba(255,255,255,.6); font-size: 13.5px; line-height: 1.95; max-width: 460px; margin-top: 20px; }
.eyebrow.light { color: var(--amber); }
.hero-badges { display: flex; gap: 18px; flex-wrap: wrap; margin-top: 28px; }
.hero-badges span { display: inline-flex; align-items: center; gap: 6px; font-size: 11px; color: rgba(255,255,255,.55); }
.hero-badges i { width: 16px; height: 16px; border-radius: 50%; background: rgba(255,255,255,.12); color: #a9d3b3; font-style: normal; font-size: 9px; display: flex; align-items: center; justify-content: center; }
.donate-panel { background: var(--paper); color: var(--ink); border-radius: 24px; padding: 26px; box-shadow: 0 30px 60px rgba(15,25,20,.28); }
.panel-head { display: flex; justify-content: space-between; font-size: 12px; font-weight: 600; }
.secure { font-size: 9.5px; color: var(--muted); font-weight: 400; }
.amounts { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 16px; }
.amounts button { border: 1px solid var(--line); background: var(--cream); border-radius: 12px; padding: 12px; display: flex; flex-direction: column; align-items: flex-start; cursor: pointer; transition: all .2s; }
.amounts button strong { font-family: var(--font-display); font-size: 21px; font-weight: 400; color: var(--ink); }
.amounts button small { font-size: 9.5px; color: var(--muted); margin-top: 2px; }
.amounts button.active { border-color: var(--primary); background: var(--primary-soft); }
.amounts button.active strong { color: var(--primary); }
.custom-amount { display: flex; align-items: center; gap: 6px; border: 1px solid var(--line); border-radius: 12px; padding: 0 14px; margin-top: 10px; height: 46px; font-family: var(--font-display); font-size: 18px; color: var(--muted); }
.custom-amount input { border: none; outline: none; flex: 1; background: transparent; font-size: 14px; font-family: var(--font-sans); color: var(--ink); }
.frequency { display: flex; gap: 6px; margin: 14px 0; padding: 4px; background: var(--cream-2); border-radius: 10px; }
.frequency button { flex: 1; height: 32px; border: none; background: transparent; border-radius: 7px; font-size: 11px; color: var(--muted); cursor: pointer; }
.frequency button.active { background: var(--paper); color: var(--ink); font-weight: 600; box-shadow: 0 2px 6px rgba(0,0,0,.05); }
.panel-note { font-size: 9.5px; color: var(--muted-2); text-align: center; margin-top: 10px; }
.donor-strip { display: flex; align-items: center; gap: 18px; padding: 18px 0 24px; border-top: 1px solid rgba(255,255,255,.1); position: relative; z-index: 2; }
.donor-avatars { display: flex; }
.donor-avatars span { width: 28px; height: 28px; border-radius: 50%; background: rgba(255,255,255,.15); border: 1px solid rgba(255,255,255,.25); display: flex; align-items: center; justify-content: center; font-size: 9px; margin-left: -6px; }
.donor-avatars span:first-child { margin-left: 0; }
.donor-avatars .plus { background: rgba(229,178,104,.25); color: var(--amber); }
.donor-strip p { font-size: 11.5px; color: rgba(255,255,255,.5); }
.donor-strip p strong { color: #fff; }
.strip-quote { margin-left: auto; font-family: var(--font-display); font-style: italic; font-size: 12px; color: rgba(255,255,255,.4); }

.impact-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.impact-card { background: var(--paper); border: 1px solid var(--line); border-radius: 22px; padding: 26px; position: relative; transition: all .3s; }
.impact-card:hover { transform: translateY(-5px); box-shadow: var(--shadow-sm); }
.i-amount { font-family: var(--font-display); font-size: 30px; color: var(--primary); }
.i-icon { position: absolute; right: 24px; top: 24px; font-size: 24px; opacity: .8; }
.impact-card h3 { font-size: 15px; margin-top: 14px; }
.impact-card p { font-size: 11.5px; color: var(--muted); line-height: 1.8; margin-top: 6px; }

.transparency { padding-bottom: 20px; }
.trans-card { display: grid; grid-template-columns: .9fr 1.1fr; gap: 60px; background: var(--cream-2); border-radius: 28px; padding: 48px 52px; align-items: center; }
.trans-copy h2 { font-size: 32px; line-height: 1.4; margin-top: 12px; letter-spacing: -.03em; }
.trans-copy p { font-size: 12.5px; color: var(--muted); line-height: 1.9; margin: 14px 0 20px; }
.trans-bars { display: flex; flex-direction: column; gap: 18px; }
.trans-top { display: flex; justify-content: space-between; font-size: 11.5px; margin-bottom: 6px; }
.trans-top strong { font-weight: 600; }
.trans-item .bar { height: 7px; background: rgba(255,255,255,.7); border-radius: 99px; overflow: hidden; }
.trans-item .bar i { display: block; height: 100%; border-radius: 99px; }
.trans-item small { font-size: 9.5px; color: var(--muted-2); }

.wall-section { background: #f7f1e6; }
.wall { columns: 3; column-gap: 18px; }
.wall-item { break-inside: avoid; background: var(--paper); border: 1px solid var(--line); border-radius: 16px; padding: 18px 20px; margin-bottom: 18px; }
.wall-head { display: flex; align-items: center; gap: 10px; }
.w-avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--primary-soft); color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 12px; }
.wall-head div { display: flex; flex-direction: column; line-height: 1.4; flex: 1; }
.wall-head strong { font-size: 11.5px; }
.wall-head small { font-size: 9px; color: var(--muted-2); }
.w-amount { font-family: var(--font-display); font-size: 17px; color: var(--primary); }
.wall-item > p { font-size: 12px; color: var(--ink-2); line-height: 1.8; margin-top: 12px; }
.pay-box { text-align: center; }
.pay-amount { font-family: var(--font-display); font-size: 44px; color: var(--primary); }
.pay-box > p { font-size: 11px; color: var(--muted); margin-bottom: 18px; }
.pay-box .el-form { text-align: left; }
.pay-methods { display: flex; gap: 10px; margin-top: 14px; }
.pay-method { flex: 1; border: 1px solid var(--line); border-radius: 10px; height: 46px; display: flex; align-items: center; justify-content: center; gap: 7px; font-size: 11.5px; cursor: pointer; }
.pay-method.active { border-color: var(--primary); background: var(--primary-soft); }
.pay-method span { width: 20px; height: 20px; border-radius: 5px; background: #1677ff; color: #fff; font-size: 9px; display: flex; align-items: center; justify-content: center; }
.pay-method small { font-size: 8px; color: var(--primary); }

@media (max-width: 980px) {
  .donate-hero-grid { grid-template-columns: 1fr; gap: 34px; }
  .impact-grid { grid-template-columns: 1fr 1fr; }
  .trans-card { grid-template-columns: 1fr; gap: 26px; padding: 34px 28px; }
  .wall { columns: 2; }
}
@media (max-width: 600px) {
  .impact-grid { grid-template-columns: 1fr; }
  .wall { columns: 1; }
  .strip-quote { display: none; }
  .donor-strip { flex-wrap: wrap; }
}
</style>
