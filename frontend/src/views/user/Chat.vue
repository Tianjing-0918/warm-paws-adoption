<template>
  <div class="chat-page">
    <section class="chat-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>AI 救助助手</span></div>
        <div class="hero-row">
          <div>
            <span class="eyebrow">AI RESCUE ASSISTANT</span>
            <h1>有些拿不准的事，<br />先问问它。</h1>
          </div>
          <p>领养准备、幼宠喂养、疫苗驱虫、流浪动物急救……用自然语言提问，助手会用温柔而清晰的方式回答你。</p>
        </div>
      </div>
    </section>

    <section class="container chat-body">
      <aside class="chat-side">
        <div class="side-card">
          <div class="bot-block">
            <span class="bot-avatar">🐾</span>
            <div><strong>暖爪助手</strong><small><i></i> 在线 · 24 小时</small></div>
          </div>
          <p class="bot-intro">你好，我是暖爪救助助手。我了解领养流程、宠物护理和流浪动物救助知识，希望可以帮到你。</p>
        </div>
        <div class="quick-block">
          <span class="side-label">你可以问我</span>
          <button v-for="q in quickQuestions" :key="q" @click="send(q)">{{ q }} <span>→</span></button>
        </div>
        <div class="safety-note">
          <span>⚠️</span>
          <p>如果动物出现严重外伤、抽搐或呼吸困难，请优先联系附近宠物医院，AI 建议不能替代兽医诊断。</p>
        </div>
        <button class="clear-btn" @click="clearChat">清空对话记录</button>
      </aside>

      <div class="chat-shell">
        <div class="chat-head">
          <div class="head-info">
            <span class="head-avatar">🐾</span>
            <div><strong>暖爪助手</strong><small>基于大模型的宠物救助咨询助手</small></div>
          </div>
          <span class="head-status"><i></i> 在线</span>
        </div>

        <div ref="messagesEl" class="messages">
          <div class="date-chip">今天</div>
          <div v-for="(m, i) in messages" :key="i" class="message" :class="m.role">
            <span v-if="m.role === 'assistant'" class="msg-avatar">🐾</span>
            <div class="msg-body">
              <div class="bubble" v-html="render(m.content)"></div>
              <span class="msg-time">{{ m.time }}</span>
            </div>
            <span v-if="m.role === 'user'" class="msg-avatar user-avatar">{{ userInitial }}</span>
          </div>
          <div v-if="typing" class="message assistant">
            <span class="msg-avatar">🐾</span>
            <div class="bubble typing-bubble"><i></i><i></i><i></i></div>
          </div>
        </div>

        <div class="composer">
          <div class="composer-box">
            <textarea v-model="input" rows="1" placeholder="输入你的问题，例如：刚捡到一只小猫，应该先做什么？" @keydown.enter.exact.prevent="send()" @input="autoGrow" ref="inputEl"></textarea>
            <button class="send-btn" :disabled="!input.trim() || typing" @click="send()">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m22 2-7 20-4-9-9-4 20-7Z"/><path d="M22 2 11 13"/></svg>
            </button>
          </div>
          <div class="composer-foot">
            <span>Enter 发送 · Shift + Enter 换行</span>
            <span>AI 内容仅供参考</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { postData } from '../../utils/api'

export default {
  name: 'Chat',
  data() {
    return {
      input: '',
      typing: false,
      messages: [],
      quickQuestions: [
        '第一次养猫需要准备什么？',
        '刚捡到流浪小猫怎么办？',
        '领养需要满足什么条件？',
        '幼犬什么时候打疫苗？',
        '猫咪一直躲着不出来'
      ]
    }
  },
  computed: {
    userInitial() {
      const name = this.$store.getters.displayName
      return name === '游客' ? '我' : name.slice(0, 1)
    }
  },
  created() {
    document.title = 'AI 救助助手 · 暖爪之家'
    const saved = localStorage.getItem('chatHistory')
    if (saved) {
      try {
        this.messages = JSON.parse(saved)
        return
      } catch (e) {}
    }
    this.messages = [{
      role: 'assistant',
      content: '你好呀，我是暖爪救助助手 🐾\n\n你可以问我关于**领养准备、幼宠喂养、疫苗驱虫、流浪动物救助**的问题。也可以直接告诉我：你现在遇到了什么情况？',
      time: this.now()
    }]
  },
  methods: {
    now() {
      const d = new Date()
      return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    },
    render(text) {
      const escaped = String(text || '')
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      return escaped
        .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
        .replace(/\n/g, '<br/>')
    },
    autoGrow() {
      const el = this.$refs.inputEl
      if (!el) return
      el.style.height = 'auto'
      el.style.height = Math.min(el.scrollHeight, 120) + 'px'
    },
    async send(text) {
      const content = (text || this.input).trim()
      if (!content || this.typing) return
      this.messages.push({ role: 'user', content, time: this.now() })
      this.input = ''
      this.$nextTick(() => {
        this.autoGrow()
        this.scrollBottom()
      })
      this.typing = true
      this.scrollBottom()

      const data = await postData('/chat', { content }, { reply: this.localReply(content) })
      const reply = (data && data.reply) || this.localReply(content)
      setTimeout(() => {
        this.typing = false
        this.messages.push({ role: 'assistant', content: reply, time: this.now() })
        this.save()
        this.scrollBottom()
      }, 650)
    },
    scrollBottom() {
      this.$nextTick(() => {
        const el = this.$refs.messagesEl
        if (el) el.scrollTop = el.scrollHeight + 200
      })
    },
    save() {
      localStorage.setItem('chatHistory', JSON.stringify(this.messages.slice(-30)))
    },
    clearChat() {
      this.messages = [{
        role: 'assistant',
        content: '对话已清空。我们可以重新开始，你想了解什么？',
        time: this.now()
      }]
      this.save()
    },
    localReply(q) {
      const text = q.toLowerCase()
      if (/捡到|流浪|救助/.test(text)) {
        return '先别急着把它带回家，按这个顺序判断：\n\n**1. 观察安全**：确认它没有明显外伤、抽搐或呼吸困难。如果有，先联系附近 24 小时宠物医院。\n**2. 判断是否有主人**：检查项圈、芯片，在小区群和「寻宠寻主」发布信息，观察 24–48 小时。\n**3. 临时安置**：准备纸箱、旧毛巾和温水，放在安静避光处，不要频繁围观。\n**4. 不要急着洗澡**：幼宠和应激状态下的动物洗澡风险很高。\n**5. 尽快体检**：检查寄生虫、传染病和基础健康。\n\n你可以在「流浪上报」页面留下位置和照片，我们会安排志愿者跟进。'
      }
      if (/疫苗|打针|免疫/.test(text)) {
        return '幼宠一般在 **6–8 周龄** 开始首针疫苗：\n\n- 猫：猫三联，间隔 3–4 周，共 3 针，之后每年加强\n- 狗：犬联苗，间隔 2–4 周，共 3 针，之后每年加强\n- 狂犬疫苗：通常 3 月龄后接种\n\n接种前需要确认精神状态和体温正常。刚到家的宠物建议先适应 7 天，再做疫苗计划。'
      }
      if (/领养|条件|申请/.test(text)) {
        return '领养的基本条件：\n\n- 年满 18 周岁，有稳定住所和经济来源\n- 家庭成员/室友同意养宠，租房需房东允许\n- 接受家访与定期回访\n- 承诺科学喂养、适龄绝育、不遗弃不转送\n\n流程是：**提交申请 → 志愿者沟通 → 家访见面 → 试养打卡 → 正式领养**，通常 3–7 天完成。你可以在「领养中心」挑选小伙伴。'
      }
      if (/躲|应激|不出来|害怕/.test(text)) {
        return '这是典型的应激反应，**不要强行把它抱出来**。\n\n可以这样做：\n1. 给它一个安静、封闭的小空间，放好食物、水和猫砂盆\n2. 人保持距离，正常说话、走动，让它熟悉你的声音\n3. 用逗猫棒或零食在远处建立正向联系\n4. 前几天不要邀请朋友来访，也不要频繁拍照\n\n多数猫会在 2–7 天内主动探索。如果超过 3 天完全不吃不喝，建议就医。'
      }
      if (/寄养/.test(text)) {
        return '寄养适合短期出差、搬家过渡或临时照护。\n\n在「宠物寄养」页面提交申请，填写宠物年龄、性格、寄养时间和特殊需求。管理员会根据房型和性别安排房间。建议自带正在吃的粮和熟悉的玩具，减少应激。'
      }
      if (/驱虫/.test(text)) {
        return '驱虫分为**体内**和**体外**：\n\n- 幼宠通常 2 周龄后开始，按体重用药\n- 成宠建议每 1–3 个月做一次体外驱虫，每 3–6 个月做一次体内驱虫\n- 刚到家的流浪动物建议先体检，再由医生决定驱虫方案\n\n不要自行使用人用驱虫药，尤其是幼猫和怀孕动物。'
      }
      if (/绝育/.test(text)) {
        return '一般建议在 **6–8 月龄** 左右评估绝育，具体时间取决于品种、体重和健康状况。绝育可以降低某些生殖系统疾病风险，也能减少走失和意外繁殖。\n\n术前需要禁食禁水（遵医嘱），术后准备伊丽莎白圈和安静恢复空间。'
      }
      if (/洗澡|清洁/.test(text)) {
        return '刚领养或刚救助的动物，**不建议立刻洗澡**。应激、低体温和未完成的疫苗都会增加风险。可以先用温毛巾擦拭，局部清理脏污。\n\n等它适应环境、完成疫苗并得到医生确认后，再安排洗澡。猫通常不需要频繁水洗，做好梳毛和环境清洁即可。'
      }
      if (/粮|吃|喂/.test(text)) {
        return '换粮要循序渐进：用 **7 天过渡法**，旧粮比例从 75% 逐步降到 0%。\n\n幼宠建议少食多餐，保证干净饮水；不要喂巧克力、葡萄、洋葱、木糖醇等对宠物有毒的食物。如果出现持续呕吐、腹泻或拒食超过 24 小时，请及时就医。'
      }
      if (/你好|hi|hello|在吗/.test(text)) {
        return '你好呀 🐾 我在的。你可以直接描述遇到的情况，比如「刚在楼下捡到一只小猫」「猫咪到家三天不吃东西」，我会尽量给你清晰的下一步建议。'
      }
      return '我理解你的问题了。为了给你更准确的建议，可以再补充一点信息吗？比如：\n\n- 是猫还是狗，大概多大？\n- 目前的精神、食欲和排泄情况？\n- 是刚救助、准备领养，还是已经在家？\n\n如果情况紧急，请优先联系附近宠物医院或拨打救助热线 400-123-4567。'
    }
  }
}
</script>

<style scoped>
.chat-hero { padding: 30px 0 30px; background: linear-gradient(180deg, #fdf9f3, var(--cream)); border-bottom: 1px solid var(--line); }
.hero-row { display: flex; align-items: flex-end; justify-content: space-between; gap: 40px; margin-top: 18px; }
.hero-row h1 { font-size: clamp(34px, 4vw, 50px); line-height: 1.22; letter-spacing: -.045em; margin-top: 10px; }
.hero-row > p { max-width: 420px; font-size: 13px; color: var(--muted); line-height: 1.9; padding-bottom: 6px; }
.chat-body { display: grid; grid-template-columns: 270px 1fr; gap: 22px; padding-top: 32px; padding-bottom: 80px; align-items: start; }
.chat-side { display: flex; flex-direction: column; gap: 14px; position: sticky; top: 100px; }
.side-card { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; padding: 20px; }
.bot-block { display: flex; align-items: center; gap: 10px; }
.bot-avatar { width: 38px; height: 38px; border-radius: 12px; background: var(--primary-soft); display: flex; align-items: center; justify-content: center; font-size: 18px; }
.bot-block div { display: flex; flex-direction: column; line-height: 1.35; }
.bot-block strong { font-size: 13px; }
.bot-block small { font-size: 9.5px; color: var(--muted); display: flex; align-items: center; gap: 4px; }
.bot-block small i { width: 5px; height: 5px; border-radius: 50%; background: #77b585; }
.bot-intro { font-size: 11.5px; color: var(--muted); line-height: 1.8; margin-top: 12px; }
.quick-block { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; padding: 18px 12px 12px; }
.side-label { display: block; font-size: 10px; color: var(--muted); letter-spacing: .08em; padding: 0 8px 8px; }
.quick-block button { width: 100%; display: flex; justify-content: space-between; gap: 8px; border: none; background: transparent; text-align: left; font-size: 11.5px; color: var(--ink-2); padding: 10px 8px; border-radius: 8px; cursor: pointer; line-height: 1.5; }
.quick-block button:hover { background: var(--cream-2); color: var(--primary); }
.quick-block button span { color: var(--muted-2); }
.safety-note { display: flex; gap: 9px; padding: 14px 16px; background: #fbf0dc; border-radius: 14px; }
.safety-note span { font-size: 14px; }
.safety-note p { font-size: 10px; color: #8a6a2a; line-height: 1.7; }
.clear-btn { border: none; background: transparent; color: var(--muted-2); font-size: 10.5px; cursor: pointer; text-decoration: underline; align-self: flex-start; padding-left: 4px; }

.chat-shell { background: var(--paper); border: 1px solid var(--line); border-radius: 22px; overflow: hidden; display: flex; flex-direction: column; height: 660px; box-shadow: var(--shadow-sm); }
.chat-head { display: flex; align-items: center; justify-content: space-between; padding: 16px 22px; border-bottom: 1px solid var(--line); }
.head-info { display: flex; align-items: center; gap: 10px; }
.head-avatar { width: 34px; height: 34px; border-radius: 10px; background: var(--ink); display: flex; align-items: center; justify-content: center; font-size: 15px; }
.head-info div { display: flex; flex-direction: column; line-height: 1.35; }
.head-info strong { font-size: 12.5px; }
.head-info small { font-size: 9.5px; color: var(--muted); }
.head-status { font-size: 10px; color: #5f8f6d; display: flex; align-items: center; gap: 5px; }
.head-status i { width: 5px; height: 5px; border-radius: 50%; background: #77b585; }
.messages { flex: 1; overflow-y: auto; padding: 22px 26px; scroll-behavior: smooth; }
.messages::-webkit-scrollbar { width: 5px; }
.messages::-webkit-scrollbar-thumb { background: var(--line-strong); border-radius: 99px; }
.date-chip { text-align: center; font-size: 9.5px; color: var(--muted-2); margin-bottom: 18px; }
.message { display: flex; gap: 10px; margin-bottom: 20px; align-items: flex-start; }
.message.user { justify-content: flex-end; }
.msg-avatar { width: 30px; height: 30px; border-radius: 9px; background: var(--cream-2); display: flex; align-items: center; justify-content: center; font-size: 14px; flex-shrink: 0; }
.user-avatar { background: var(--sage); color: #fff; font-size: 11px; font-weight: 600; }
.msg-body { max-width: 72%; display: flex; flex-direction: column; }
.user .msg-body { align-items: flex-end; }
.bubble { padding: 12px 16px; border-radius: 4px 16px 16px 16px; background: var(--cream); font-size: 12.5px; color: var(--ink-2); line-height: 1.85; }
.user .bubble { border-radius: 16px 4px 16px 16px; background: var(--primary); color: #fff; }
.bubble strong { color: var(--ink); }
.user .bubble strong { color: #fff; }
.msg-time { font-size: 9px; color: var(--muted-2); margin-top: 5px; }
.typing-bubble { display: flex; gap: 4px; padding: 15px 18px; }
.typing-bubble i { width: 5px; height: 5px; border-radius: 50%; background: var(--muted-2); animation: blink 1.2s infinite; }
.typing-bubble i:nth-child(2) { animation-delay: .15s; }
.typing-bubble i:nth-child(3) { animation-delay: .3s; }
@keyframes blink { 0%,80%,100% { opacity: .25; } 40% { opacity: 1; } }
.composer { padding: 14px 20px 12px; border-top: 1px solid var(--line); }
.composer-box { display: flex; align-items: flex-end; gap: 10px; border: 1px solid var(--line); border-radius: 16px; padding: 9px 10px 9px 16px; background: var(--cream); transition: border-color .2s; }
.composer-box:focus-within { border-color: var(--primary); background: #fff; }
.composer-box textarea { flex: 1; border: none; outline: none; background: transparent; resize: none; font-size: 12.5px; line-height: 1.7; max-height: 120px; color: var(--ink); padding: 6px 0; }
.send-btn { width: 36px; height: 36px; border-radius: 11px; border: none; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; cursor: pointer; flex-shrink: 0; }
.send-btn:disabled { background: var(--line-strong); cursor: not-allowed; }
.composer-foot { display: flex; justify-content: space-between; font-size: 9px; color: var(--muted-2); margin-top: 7px; padding: 0 4px; }

@media (max-width: 900px) {
  .chat-body { grid-template-columns: 1fr; }
  .chat-side { display: none; }
  .chat-shell { height: 640px; }
  .hero-row { flex-direction: column; align-items: flex-start; gap: 10px; }
}
@media (max-width: 560px) {
  .chat-shell { height: calc(100vh - 190px); min-height: 520px; border-radius: 18px; }
  .messages { padding: 16px; }
  .msg-body { max-width: 82%; }
  .chat-body { padding-top: 18px; }
}
</style>
