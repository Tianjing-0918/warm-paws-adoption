<template>
  <div class="community-page">
    <section class="page-hero community-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>宠物社区</span></div>
        <div class="hero-row">
          <div>
            <span class="eyebrow">WARM PAWS COMMUNITY</span>
            <h1>在这里，聊聊<br />我们和毛孩子的日常。</h1>
            <p>分享领养后的第一次撒娇，记录一场志愿活动，也可以在不知所措时向有经验的人求助。</p>
          </div>
          <div class="hero-stats">
            <div><strong>2,846</strong><span>社区伙伴</span></div>
            <div><strong>1,092</strong><span>条温暖分享</span></div>
            <div><strong>86</strong><span>今日新帖</span></div>
          </div>
        </div>
      </div>
    </section>

    <section class="container community-body">
      <div class="feed-col">
        <div class="feed-toolbar">
          <div class="chip-row">
            <button v-for="c in categories" :key="c" class="chip" :class="{ active: category === c }" @click="category = c">{{ c }}</button>
          </div>
          <div class="sort-tabs">
            <button :class="{ active: sort === 'new' }" @click="sort = 'new'">最新</button>
            <button :class="{ active: sort === 'hot' }" @click="sort = 'hot'">热门</button>
          </div>
        </div>

        <div v-if="filteredPosts.length" class="post-list">
          <article v-for="post in filteredPosts" :key="post.id" class="post-card" @click="openPost(post)">
            <div class="post-main">
              <div class="post-head">
                <span class="author-avatar">{{ (post.username || '暖').slice(0, 1) }}</span>
                <div class="author-info">
                  <strong>{{ post.username || '暖爪伙伴' }}</strong>
                  <small>{{ relativeTime(post.createTime) }} · {{ post.category || categoryOf(post) }}</small>
                </div>
                <span class="post-cat">{{ post.category || categoryOf(post) }}</span>
              </div>
              <h2>{{ post.title }}</h2>
              <div class="post-excerpt">{{ plain(post.content) }}</div>
              <div class="post-actions">
                <button :class="{ liked: liked[post.id] }" @click.stop="like(post)">
                  <svg width="15" height="15" viewBox="0 0 24 24" :fill="liked[post.id] ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>
                  {{ (post.likeCount || 0) + (liked[post.id] ? 1 : 0) }}
                </button>
                <button>
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M21 11.5a8.4 8.4 0 0 1-9 8.4 9.6 9.6 0 0 1-3.3-.6L4 21l1.3-3.7A8.2 8.2 0 0 1 3 11.5 8.4 8.4 0 0 1 12 3a8.4 8.4 0 0 1 9 8.5Z"/></svg>
                  {{ post.comments ? post.comments.length : commentCount(post) }}
                </button>
                <span class="read-more">阅读全文 →</span>
              </div>
            </div>
            <div v-if="post.images" class="post-image">
              <img :src="firstImage(post.images)" :alt="post.title" loading="lazy" />
            </div>
          </article>
        </div>
        <div v-else class="empty-state card">
          <div class="icon">💬</div>
          <p>这个分类还没有帖子，成为第一个分享的人吧。</p>
        </div>
      </div>

      <aside class="side-col">
        <div class="side-card publish-card">
          <span class="side-label">SHARE YOUR STORY</span>
          <h3>有想说的话？</h3>
          <p>一段领养日常、一张照片，或者一个需要帮助的问题。</p>
          <button class="btn btn-primary btn-block" @click="openCompose">发布新帖</button>
        </div>
        <div class="side-card">
          <div class="side-head"><h3>社区热榜</h3><span>本周</span></div>
          <button v-for="(p, i) in hotPosts" :key="p.id" class="hot-item" @click="openPost(p)">
            <span class="hot-num" :class="{ top: i < 3 }">{{ i + 1 }}</span>
            <span class="hot-title">{{ p.title }}</span>
            <span class="hot-like">{{ p.likeCount }} ♥</span>
          </button>
        </div>
        <div class="side-card event-card">
          <span class="side-label">UPCOMING</span>
          <h3>本周六 · 线下领养日</h3>
          <p>10 月 24 日 13:00<br />徐汇滨江绿地 · 3 号草坪</p>
          <div class="event-tags"><span>志愿者招募</span><span>领养咨询</span></div>
          <router-link to="/donate" class="event-link">了解活动 →</router-link>
        </div>
        <div class="side-note">
          <strong>社区公约</strong>
          <p>请友善交流，不发布买卖动物信息；领养代替购买，拒绝弃养。遇到紧急救助，请优先拨打救助热线。</p>
        </div>
      </aside>
    </section>

    <!-- 帖子详情 -->
    <el-dialog :visible.sync="postVisible" width="700px" custom-class="post-dialog">
      <div v-if="current" class="post-detail">
        <div class="detail-head">
          <span class="author-avatar">{{ (current.username || '暖').slice(0, 1) }}</span>
          <div><strong>{{ current.username || '暖爪伙伴' }}</strong><small>{{ formatDate(current.createTime, true) }} · {{ current.category || categoryOf(current) }}</small></div>
          <span class="post-cat">{{ current.category || categoryOf(current) }}</span>
        </div>
        <h2>{{ current.title }}</h2>
        <img v-if="current.images" :src="firstImage(current.images)" class="detail-image" :alt="current.title" />
        <div class="prose detail-content" v-html="markdown(current.content)"></div>
        <div class="detail-actions">
          <button :class="{ liked: liked[current.id] }" @click="like(current)">
            <svg width="15" height="15" viewBox="0 0 24 24" :fill="liked[current.id] ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>
            {{ (current.likeCount || 0) + (liked[current.id] ? 1 : 0) }} 人觉得有用
          </button>
          <button @click="copyLink">分享帖子</button>
        </div>
        <div class="comments">
          <h3>评论 <span>{{ comments.length }}</span></h3>
          <div v-for="c in comments" :key="c.id" class="comment">
            <span class="c-avatar">{{ (c.username || '暖').slice(0, 1) }}</span>
            <div>
              <div class="c-head"><strong>{{ c.username || '暖爪伙伴' }}</strong><small>{{ relativeTime(c.createTime) }}</small></div>
              <p>{{ c.content }}</p>
            </div>
          </div>
          <div v-if="!comments.length" class="no-comments">还没有评论，来说点温柔的话吧。</div>
          <div class="comment-input">
            <el-input v-model="newComment" placeholder="写下你的评论…" @keyup.enter.native="addComment" />
            <el-button type="primary" @click="addComment">发送</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 发布 -->
    <el-dialog title="发布新帖" :visible.sync="composeVisible" width="640px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="标题"><el-input v-model="form.title" placeholder="一句话说清楚你想分享什么" /></el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.category" style="width:100%">
              <el-option v-for="c in categories.slice(1)" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="正文">
          <div class="editor-tools">
            <button type="button" @click="insert('**粗体**')"><b>B</b></button>
            <button type="button" @click="insert('## 小标题')">H</button>
            <button type="button" @click="insert('- 列表项')">≡</button>
            <button type="button" @click="insert('[链接](https://)')">↗</button>
            <span>支持 Markdown</span>
          </div>
          <el-input ref="contentInput" v-model="form.content" type="textarea" :rows="7" placeholder="写下你的故事…&#10;&#10;可以用 **粗体**、## 小标题、- 列表 来排版。" />
        </el-form-item>
        <el-form-item label="配图（选填）">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="onUpload" :before-upload="beforeUpload">
            <div class="upload-box">
              <img v-if="form.images" :src="form.images" alt="" />
              <template v-else><span>＋</span><small>上传图片</small></template>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="composeVisible = false">取消</el-button>
        <el-button type="primary" :loading="posting" @click="submitPost">发布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, formatDate, relativeTime, requireLogin, imageUrl } from '../../utils/api'
import { demoPosts } from '../../utils/demo'

export default {
  name: 'Community',
  data() {
    return {
      posts: demoPosts,
      category: '全部',
      categories: ['全部', '领养故事', '新手求助', '养宠经验', '志愿日记', '闲置互助'],
      sort: 'new',
      liked: {},
      postVisible: false,
      composeVisible: false,
      current: null,
      comments: [],
      newComment: '',
      posting: false,
      form: { title: '', content: '', category: '领养故事', images: '' }
    }
  },
  computed: {
    filteredPosts() {
      let list = this.posts.slice()
      if (this.category !== '全部') list = list.filter(p => (p.category || this.categoryOf(p)) === this.category)
      if (this.sort === 'hot') list.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
      return list
    },
    hotPosts() {
      return this.posts.slice().sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0)).slice(0, 5)
    }
  },
  created() {
    document.title = '宠物社区 · 暖爪之家'
    const savedLikes = localStorage.getItem('postLikes')
    if (savedLikes) {
      try { this.liked = JSON.parse(savedLikes) } catch (e) {}
    }
    this.load()
  },
  methods: {
    formatDate,
    relativeTime,
    async load() {
      const data = await getData('/post/list', { pageNum: 1, pageSize: 20 }, { list: demoPosts })
      if (data && data.list && data.list.length) {
        this.posts = data.list.map(p => Object.assign({}, p, { category: p.category || this.categoryOf(p) }))
      }
    },
    categoryOf(post) {
      const text = (post.title || '') + (post.content || '')
      if (/求助|怎么办|急|请问/.test(text)) return '新手求助'
      if (/志愿|救助站|活动/.test(text)) return '志愿日记'
      if (/领养|到家|回家/.test(text)) return '领养故事'
      return '养宠经验'
    },
    commentCount(post) { return post.comments ? post.comments.length : (post.commentCount || 0) },
    plain(text) {
      return String(text || '').replace(/[#*_>`\-\[\]()]/g, '').replace(/\s+/g, ' ').slice(0, 120) + '…'
    },
    markdown(text) {
      let s = String(text || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      s = s.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, '<img alt="$1" src="$2" />')
      s = s.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>')
      s = s.replace(/^### (.+)$/gm, '<h3>$1</h3>')
      s = s.replace(/^## (.+)$/gm, '<h2>$1</h2>')
      s = s.replace(/^# (.+)$/gm, '<h1>$1</h1>')
      s = s.replace(/^&gt; (.+)$/gm, '<blockquote>$1</blockquote>')
      s = s.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      s = s.replace(/\*(.+?)\*/g, '<em>$1</em>')
      s = s.replace(/`([^`]+)`/g, '<code>$1</code>')
      s = s.replace(/^- (.+)$/gm, '<li>$1</li>')
      s = s.replace(/(<li>[\s\S]*?<\/li>)/g, '<ul>$1</ul>')
      s = s.split(/\n{2,}/).map(block => {
        if (/^<(h\d|ul|blockquote|img)/.test(block.trim())) return block
        return '<p>' + block.replace(/\n/g, '<br/>') + '</p>'
      }).join('')
      return s
    },
    firstImage(path) { return imageUrl(path) },
    like(post) {
      this.$set(this.liked, post.id, !this.liked[post.id])
      localStorage.setItem('postLikes', JSON.stringify(this.liked))
      if (this.liked[post.id]) {
        if (post.id < 1000) postData('/post/' + post.id + '/like', {}, { ok: true })
      }
    },
    async openPost(post) {
      this.current = post
      this.comments = post.comments || []
      this.newComment = ''
      this.postVisible = true
      if (!post.comments && post.id < 1000) {
        const data = await getData('/post/' + post.id, null, null)
        if (data) {
          this.current = Object.assign({}, post, data.post || {})
          this.comments = data.comments || []
        }
      }
    },
    async addComment() {
      if (!this.newComment.trim()) return
      if (!requireLogin(this, '/community')) return
      const content = this.newComment.trim()
      const c = {
        id: Date.now(),
        username: this.$store.getters.displayName,
        content,
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      this.comments.push(c)
      this.newComment = ''
      if (this.current.id < 1000) await postData('/post/' + this.current.id + '/comment', { content }, { ok: true })
      this.$message.success('评论已发布')
    },
    openCompose() {
      if (!requireLogin(this, '/community')) return
      this.composeVisible = true
    },
    insert(text) {
      this.form.content = (this.form.content || '') + (this.form.content ? '\n' : '') + text
    },
    beforeUpload(file) {
      const ok = /image\/(jpeg|png|gif|webp)/.test(file.type)
      if (!ok) this.$message.warning('请上传图片文件')
      return ok
    },
    onUpload(res) {
      if (res && res.code === 200) this.form.images = '/api' + res.data.url
      else this.$message.warning('图片上传失败，请检查后端服务')
    },
    async submitPost() {
      if (!this.form.title.trim() || !this.form.content.trim()) return this.$message.warning('请填写标题和正文')
      this.posting = true
      await postData('/post', { title: this.form.title, content: this.form.content, images: this.form.images }, { ok: true })
      const post = {
        id: Date.now(),
        title: this.form.title,
        content: this.form.content,
        images: this.form.images,
        category: this.form.category,
        username: this.$store.getters.displayName,
        likeCount: 0,
        comments: [],
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      this.posts.unshift(post)
      this.posting = false
      this.composeVisible = false
      this.form = { title: '', content: '', category: '领养故事', images: '' }
      this.$message.success('发布成功，谢谢你的分享')
    },
    copyLink() {
      const url = window.location.href
      if (navigator.clipboard) navigator.clipboard.writeText(url).then(() => this.$message.success('链接已复制'))
      else this.$message.info(url)
    }
  }
}
</script>

<style scoped>
.community-hero { padding-bottom: 40px; }
.hero-row { display: flex; align-items: flex-end; justify-content: space-between; gap: 40px; margin-top: 20px; }
.page-hero h1 { font-size: clamp(38px, 4.5vw, 58px); line-height: 1.2; }
.hero-stats { display: flex; gap: 40px; padding-bottom: 6px; }
.hero-stats div { display: flex; flex-direction: column; }
.hero-stats strong { font-family: var(--font-display); font-size: 27px; font-weight: 400; color: var(--primary); }
.hero-stats span { font-size: 10px; color: var(--muted); }
.community-body { display: grid; grid-template-columns: 1fr 300px; gap: 32px; padding-top: 34px; padding-bottom: 90px; align-items: start; }
.feed-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 20px; margin-bottom: 22px; }
.sort-tabs { display: flex; gap: 6px; background: var(--cream-2); border-radius: 999px; padding: 3px; }
.sort-tabs button { border: none; background: transparent; padding: 5px 14px; border-radius: 999px; font-size: 11px; color: var(--muted); cursor: pointer; }
.sort-tabs button.active { background: var(--paper); color: var(--ink); font-weight: 600; box-shadow: 0 2px 5px rgba(0,0,0,.05); }
.post-list { display: flex; flex-direction: column; gap: 16px; }
.post-card { display: grid; grid-template-columns: 1fr 190px; gap: 22px; background: var(--paper); border: 1px solid var(--line); border-radius: 20px; padding: 22px 24px; cursor: pointer; transition: all .3s; }
.post-card:hover { border-color: var(--line-strong); box-shadow: var(--shadow-sm); transform: translateY(-2px); }
.post-card:first-child { grid-template-columns: 1fr 230px; }
.post-head { display: flex; align-items: center; gap: 9px; }
.author-avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--sage-soft); color: var(--sage-dark); display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; flex-shrink: 0; }
.author-info { display: flex; flex-direction: column; line-height: 1.35; flex: 1; }
.author-info strong { font-size: 11.5px; }
.author-info small { font-size: 9.5px; color: var(--muted-2); }
.post-cat { font-size: 9.5px; color: var(--primary); background: var(--primary-soft); border-radius: 999px; padding: 3px 9px; }
.post-card h2 { font-size: 18px; line-height: 1.5; margin-top: 14px; letter-spacing: -.02em; }
.post-card:first-child h2 { font-size: 21px; }
.post-excerpt { font-size: 12px; color: var(--muted); line-height: 1.8; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.post-actions { display: flex; align-items: center; gap: 16px; margin-top: 16px; }
.post-actions button { display: inline-flex; align-items: center; gap: 5px; border: none; background: none; color: var(--muted); font-size: 11px; cursor: pointer; padding: 0; }
.post-actions button.liked { color: var(--primary); }
.read-more { margin-left: auto; font-size: 10.5px; color: var(--primary); opacity: 0; transition: opacity .2s; }
.post-card:hover .read-more { opacity: 1; }
.post-image { border-radius: 13px; overflow: hidden; min-height: 130px; max-height: 170px; }
.post-image img { width: 100%; height: 100%; object-fit: cover; }

.side-col { display: flex; flex-direction: column; gap: 16px; position: sticky; top: 100px; }
.side-card { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; padding: 20px; }
.side-label { font-size: 9px; letter-spacing: .16em; color: var(--primary); }
.publish-card { background: var(--ink); border-color: var(--ink); color: #fff; }
.publish-card h3 { color: #fff; font-size: 18px; margin-top: 10px; }
.publish-card p { font-size: 11px; color: rgba(255,255,255,.5); line-height: 1.8; margin: 8px 0 16px; }
.side-head { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px; }
.side-head h3 { font-size: 14px; }
.side-head span { font-size: 9px; color: var(--muted-2); }
.hot-item { width: 100%; display: flex; align-items: flex-start; gap: 9px; border: none; background: none; text-align: left; padding: 9px 0; border-bottom: 1px solid var(--line); cursor: pointer; }
.hot-item:last-child { border-bottom: none; }
.hot-num { font-family: var(--font-display); font-size: 13px; color: var(--muted-2); width: 13px; flex-shrink: 0; }
.hot-num.top { color: var(--primary); }
.hot-title { flex: 1; font-size: 11.5px; line-height: 1.55; color: var(--ink-2); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.hot-item:hover .hot-title { color: var(--primary); }
.hot-like { font-size: 9px; color: var(--muted-2); white-space: nowrap; }
.event-card { background: var(--primary-soft); border-color: transparent; }
.event-card h3 { font-size: 15px; margin-top: 8px; }
.event-card p { font-size: 11px; color: var(--primary-dark); opacity: .8; line-height: 1.8; margin-top: 6px; }
.event-tags { display: flex; gap: 6px; margin-top: 10px; }
.event-tags span { font-size: 9px; background: rgba(255,255,255,.5); color: var(--primary-dark); padding: 3px 8px; border-radius: 999px; }
.event-link { display: inline-block; font-size: 10.5px; color: var(--primary-dark); margin-top: 12px; font-weight: 600; }
.side-note { padding: 16px 18px; border: 1px dashed var(--line-strong); border-radius: 16px; }
.side-note strong { font-size: 11px; }
.side-note p { font-size: 10px; color: var(--muted); line-height: 1.8; margin-top: 4px; }

.detail-head { display: flex; align-items: center; gap: 10px; }
.detail-head > div { display: flex; flex-direction: column; line-height: 1.4; flex: 1; }
.detail-head strong { font-size: 12px; }
.detail-head small { font-size: 9.5px; color: var(--muted); }
.post-detail h2 { font-size: 27px; margin-top: 18px; line-height: 1.45; letter-spacing: -.03em; }
.detail-image { width: 100%; max-height: 340px; object-fit: cover; border-radius: 16px; margin-top: 18px; }
.detail-content { font-size: 13.5px; margin-top: 18px; }
.detail-actions { display: flex; gap: 12px; margin-top: 24px; }
.detail-actions button { border: 1px solid var(--line); background: var(--paper); border-radius: 999px; padding: 7px 14px; font-size: 11px; cursor: pointer; display: inline-flex; align-items: center; gap: 5px; }
.detail-actions button.liked { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }
.comments { margin-top: 30px; padding-top: 22px; border-top: 1px solid var(--line); }
.comments h3 { font-size: 14px; }
.comments h3 span { color: var(--muted-2); font-weight: 400; }
.comment { display: flex; gap: 10px; margin-top: 16px; }
.c-avatar { width: 28px; height: 28px; border-radius: 50%; background: var(--cream-2); color: var(--ink-2); display: flex; align-items: center; justify-content: center; font-size: 10px; flex-shrink: 0; }
.c-head { display: flex; gap: 8px; align-items: baseline; }
.c-head strong { font-size: 11px; }
.c-head small { font-size: 9px; color: var(--muted-2); }
.comment p { font-size: 12px; color: var(--ink-2); line-height: 1.75; margin-top: 3px; }
.no-comments { font-size: 11px; color: var(--muted-2); padding: 18px 0; }
.comment-input { display: flex; gap: 10px; margin-top: 18px; }
.editor-tools { display: flex; align-items: center; gap: 4px; padding: 6px 8px; background: var(--cream-2); border-radius: 8px 8px 0 0; }
.editor-tools button { width: 26px; height: 24px; border: none; background: transparent; border-radius: 5px; cursor: pointer; font-size: 11px; color: var(--ink-2); }
.editor-tools button:hover { background: var(--paper); }
.editor-tools span { margin-left: auto; font-size: 9px; color: var(--muted-2); }
.upload-box { width: 120px; height: 84px; border: 1px dashed var(--line-strong); border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--muted); overflow: hidden; }
.upload-box img { width: 100%; height: 100%; object-fit: cover; }
.upload-box span { font-size: 18px; }
.upload-box small { font-size: 9px; }

@media (max-width: 1000px) {
  .community-body { grid-template-columns: 1fr; }
  .side-col { position: static; display: grid; grid-template-columns: 1fr 1fr; }
  .side-note { display: none; }
}
@media (max-width: 680px) {
  .hero-row { flex-direction: column; align-items: flex-start; gap: 20px; }
  .hero-stats { gap: 22px; }
  .feed-toolbar { flex-direction: column; align-items: flex-start; }
  .post-card, .post-card:first-child { grid-template-columns: 1fr; }
  .post-image { max-height: 210px; order: -1; }
  .side-col { grid-template-columns: 1fr; }
}
</style>
