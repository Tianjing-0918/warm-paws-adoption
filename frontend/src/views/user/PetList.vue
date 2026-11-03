<template>
  <div class="pet-list-page">
    <section class="page-hero">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>领养中心</span></div>
        <div class="hero-row">
          <div>
            <h1>遇见你的<br /><span class="accent">毛茸茸家人</span></h1>
            <p>它们来自街头，也值得拥有一个家。慢慢看，不着急，那个对的眼神会认出你。</p>
          </div>
          <div class="search-box">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
            <input v-model="query.name" placeholder="输入名字，或你想找的品种…" @keyup.enter="search" />
            <button @click="search">搜索</button>
          </div>
        </div>
        <div class="hero-tags">
          <span><strong>{{ total }}</strong> 位小伙伴正在等待</span>
          <span class="dot"></span>
          <span>全部已完成基础体检</span>
          <span class="dot"></span>
          <span>领养不收取任何费用</span>
        </div>
      </div>
    </section>

    <section class="container list-body">
      <div class="filter-panel">
        <div class="filter-row">
          <span class="filter-label">我想领养</span>
          <div class="chip-row">
            <button v-for="f in typeFilters" :key="f.value" class="chip" :class="{ active: filters.type === f.value }" @click="setType(f.value)">
              {{ f.label }}
            </button>
          </div>
          <span class="filter-sep"></span>
          <el-select v-model="query.categoryId" clearable placeholder="全部品种" size="small" class="breed-select" @change="search">
            <el-option v-for="c in filteredCategories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-select v-model="filters.gender" clearable placeholder="性别" size="small" class="mini-select" @change="search">
            <el-option label="男孩" value="公" />
            <el-option label="女孩" value="母" />
          </el-select>
          <el-select v-model="filters.age" clearable placeholder="年龄" size="small" class="mini-select" @change="search">
            <el-option label="一岁以下" value="baby" />
            <el-option label="1–3 岁" value="young" />
            <el-option label="3 岁以上" value="adult" />
          </el-select>
          <div class="sort-wrap">
            <span class="sort-label">排序</span>
            <el-select v-model="filters.sort" size="small" class="mini-select" @change="search">
              <el-option label="最新加入" value="new" />
              <el-option label="年龄从小到大" value="ageAsc" />
              <el-option label="名字" value="name" />
            </el-select>
          </div>
        </div>
        <div v-if="hasFilter" class="active-filters">
          <span>已筛选：</span>
          <button v-if="query.name" @click="clearOne('name')">{{ query.name }} ×</button>
          <button v-if="filters.type !== 'all'" @click="setType('all')">{{ filters.type === '猫' ? '猫咪' : '狗狗' }} ×</button>
          <button v-if="query.categoryId" @click="clearOne('categoryId')">{{ currentCategoryName }} ×</button>
          <button v-if="filters.gender" @click="clearOne('gender')">{{ filters.gender === '公' ? '男孩' : '女孩' }} ×</button>
          <button v-if="filters.age" @click="clearOne('age')">年龄 ×</button>
          <button class="clear-all" @click="resetAll">清除全部</button>
        </div>
      </div>

      <div class="result-bar">
        <p>找到 <strong>{{ displayList.length }}</strong> 位适合认识的小伙伴</p>
        <div class="view-toggle">
          <span class="status-note"><i class="status-dot"></i> 仅展示可领养</span>
        </div>
      </div>

      <div v-if="loading" class="pet-grid">
        <div v-for="i in 8" :key="i" class="skeleton-card">
          <div class="skeleton sk-img"></div>
          <div class="skeleton sk-line"></div>
          <div class="skeleton sk-line short"></div>
        </div>
      </div>
      <div v-else-if="displayList.length" class="pet-grid">
        <PetCard v-for="pet in displayList" :key="pet.id" :pet="pet" />
      </div>
      <div v-else class="empty-state card">
        <div class="icon">🔎</div>
        <h3>暂时没有遇见合适的它</h3>
        <p>试着放宽一点筛选条件，或者去社区看看大家的分享。</p>
        <el-button type="primary" plain @click="resetAll">清除筛选</el-button>
      </div>

      <div v-if="totalPages > 1" class="pager">
        <el-pagination background layout="prev, pager, next" :current-page="query.pageNum" :page-size="query.pageSize" :total="totalPages * query.pageSize" @current-change="changePage" />
      </div>

      <div class="adopt-note">
        <div class="note-icon">🌿</div>
        <div>
          <h3>先了解，再心动</h3>
          <p>领养意味着十几年甚至更久的陪伴。请确认家庭成员都同意、居住环境允许养宠，并愿意承担疫苗、驱虫与日常医疗的费用。</p>
        </div>
        <router-link to="/guide" class="link-more">阅读领养指南 →</router-link>
      </div>
    </section>
  </div>
</template>

<script>
import PetCard from '../../components/PetCard.vue'
import { getData } from '../../utils/api'
import { demoPets, demoCategories, getPetType, enrichPet } from '../../utils/demo'

export default {
  name: 'PetList',
  components: { PetCard },
  data() {
    return {
      loading: false,
      list: [],
      allDemo: demoPets,
      total: 10,
      totalPages: 1,
      categories: demoCategories,
      query: { pageNum: 1, pageSize: 12, name: '', categoryId: null, status: 0 },
      filters: { type: 'all', gender: '', age: '', sort: 'new' },
      typeFilters: [
        { label: '全部', value: 'all' },
        { label: '猫咪', value: '猫' },
        { label: '狗狗', value: '狗' }
      ]
    }
  },
  computed: {
    filteredCategories() {
      if (this.filters.type === 'all') return this.categories
      return this.categories.filter(c => c.type === this.filters.type)
    },
    currentCategoryName() {
      const c = this.categories.find(item => item.id === this.query.categoryId)
      return c ? c.name : '品种'
    },
    hasFilter() {
      return this.query.name || this.filters.type !== 'all' || this.query.categoryId || this.filters.gender || this.filters.age
    },
    displayList() {
      let list = this.list.slice()
      if (this.filters.type !== 'all') list = list.filter(p => getPetType(p) === this.filters.type)
      if (this.filters.gender) list = list.filter(p => p.gender === this.filters.gender)
      if (this.filters.age === 'baby') list = list.filter(p => Number(p.age) < 12)
      if (this.filters.age === 'young') list = list.filter(p => Number(p.age) >= 12 && Number(p.age) < 36)
      if (this.filters.age === 'adult') list = list.filter(p => Number(p.age) >= 36)
      if (this.filters.sort === 'ageAsc') list.sort((a, b) => Number(a.age) - Number(b.age))
      if (this.filters.sort === 'name') list.sort((a, b) => String(a.name).localeCompare(String(b.name), 'zh'))
      return list
    }
  },
  created() {
    document.title = '领养中心 · 暖爪之家'
    if (this.$route.query.status !== undefined) {
      this.query.status = Number(this.$route.query.status)
    }
    this.loadCategories()
    this.load()
  },
  methods: {
    async loadCategories() {
      const cats = await getData('/category/list', null, demoCategories)
      if (cats && cats.length) this.categories = cats
    },
    async load() {
      this.loading = true
      const fallback = this.demoPage()
      const data = await getData('/pet/list', this.query, fallback)
      if (data && data.list) {
        this.list = data.list.map(enrichPet)
        this.total = data.total || data.list.length
        this.totalPages = Math.ceil(this.total / this.query.pageSize)
      } else {
        this.list = fallback.list
        this.total = fallback.total
        this.totalPages = Math.ceil(this.total / this.query.pageSize)
      }
      this.loading = false
    },
    demoPage() {
      let list = this.allDemo.slice()
      if (this.query.name) list = list.filter(p => p.name.indexOf(this.query.name) >= 0)
      if (this.query.categoryId) list = list.filter(p => p.categoryId === this.query.categoryId)
      const total = list.length
      const start = (this.query.pageNum - 1) * this.query.pageSize
      return { list: list.slice(start, start + this.query.pageSize), total }
    },
    search() {
      this.query.pageNum = 1
      this.load()
    },
    changePage(page) {
      this.query.pageNum = page
      this.load()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    setType(type) {
      this.filters.type = type
      this.query.categoryId = null
    },
    clearOne(key) {
      if (key === 'name') this.query.name = ''
      if (key === 'categoryId') this.query.categoryId = null
      if (key === 'gender') this.filters.gender = ''
      if (key === 'age') this.filters.age = ''
      this.search()
    },
    resetAll() {
      this.query.name = ''
      this.query.categoryId = null
      this.filters = { type: 'all', gender: '', age: '', sort: 'new' }
      this.search()
    }
  }
}
</script>

<style scoped>
.page-hero { padding-bottom: 46px; }
.hero-row { display: flex; align-items: flex-end; justify-content: space-between; gap: 40px; margin-top: 22px; }
.page-hero h1 { font-size: clamp(40px, 4.6vw, 62px); line-height: 1.15; }
.accent { color: var(--primary); }
.page-hero p { max-width: 480px; }
.search-box { display: flex; align-items: center; gap: 10px; background: var(--paper); border: 1px solid var(--line); border-radius: 999px; padding: 8px 8px 8px 20px; min-width: 380px; box-shadow: var(--shadow-sm); color: var(--muted); }
.search-box input { flex: 1; border: none; outline: none; background: transparent; height: 38px; font-size: 13px; color: var(--ink); }
.search-box button { height: 38px; padding: 0 20px; border-radius: 999px; border: none; background: var(--ink); color: #fff; font-size: 12px; cursor: pointer; }
.search-box button:hover { background: var(--primary); }
.hero-tags { display: flex; align-items: center; gap: 12px; margin-top: 32px; font-size: 12px; color: var(--muted); flex-wrap: wrap; }
.hero-tags strong { color: var(--ink); font-size: 14px; }
.dot { width: 3px; height: 3px; border-radius: 50%; background: var(--line-strong); }

.list-body { padding-top: 34px; padding-bottom: 90px; }
.filter-panel { background: var(--paper); border: 1px solid var(--line); border-radius: 20px; padding: 18px 22px; box-shadow: var(--shadow-sm); }
.filter-row { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
.filter-label { font-size: 12px; font-weight: 600; color: var(--ink-2); white-space: nowrap; }
.filter-sep { width: 1px; height: 22px; background: var(--line); margin: 0 4px; }
.breed-select { width: 140px; }
.mini-select { width: 112px; }
.sort-wrap { margin-left: auto; display: flex; align-items: center; gap: 8px; }
.sort-label { font-size: 11px; color: var(--muted); }
.active-filters { display: flex; flex-wrap: wrap; align-items: center; gap: 8px; margin-top: 14px; padding-top: 14px; border-top: 1px solid var(--line); font-size: 11.5px; color: var(--muted); }
.active-filters button { border: none; background: var(--cream-2); color: var(--ink-2); border-radius: 999px; padding: 4px 10px; cursor: pointer; font-size: 11px; }
.active-filters button:hover { background: var(--primary-soft); color: var(--primary); }
.active-filters .clear-all { background: transparent; color: var(--primary); text-decoration: underline; }

.result-bar { display: flex; align-items: center; justify-content: space-between; margin: 26px 2px 20px; }
.result-bar p { font-size: 13px; color: var(--muted); }
.result-bar strong { color: var(--ink); }
.status-note { display: inline-flex; align-items: center; gap: 6px; font-size: 11px; color: var(--sage-dark); }
.pet-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 22px; }
.skeleton-card { background: var(--paper); border: 1px solid var(--line); border-radius: 24px; padding: 12px; }
.sk-img { height: 290px; border-radius: 16px; }
.sk-line { height: 14px; margin-top: 14px; width: 70%; }
.sk-line.short { width: 45%; margin-top: 8px; }

.adopt-note { display: flex; align-items: center; gap: 22px; margin-top: 64px; padding: 28px 32px; background: var(--sage-soft); border-radius: 22px; }
.note-icon { width: 46px; height: 46px; border-radius: 50%; background: var(--paper); display: flex; align-items: center; justify-content: center; font-size: 21px; flex-shrink: 0; }
.adopt-note div:nth-child(2) { flex: 1; }
.adopt-note h3 { font-size: 16px; }
.adopt-note p { font-size: 12px; color: var(--sage-dark); margin-top: 5px; line-height: 1.8; max-width: 760px; }
.adopt-note .link-more { border-color: rgba(71,99,83,.25); color: var(--sage-dark); }

@media (max-width: 1080px) {
  .pet-grid { grid-template-columns: repeat(3, 1fr); }
  .search-box { min-width: 300px; }
}
@media (max-width: 820px) {
  .hero-row { flex-direction: column; align-items: flex-start; gap: 24px; }
  .search-box { width: 100%; min-width: 0; }
  .filter-row { gap: 10px; }
  .sort-wrap { margin-left: 0; }
  .pet-grid { grid-template-columns: repeat(2, 1fr); gap: 16px; }
  .adopt-note { flex-direction: column; align-items: flex-start; }
}
@media (max-width: 560px) {
  .pet-grid { grid-template-columns: 1fr 1fr; }
  .filter-sep { display: none; }
  .breed-select, .mini-select { width: calc(50% - 6px); }
  .adopt-note { padding: 22px; }
}
</style>
