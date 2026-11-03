<template>
  <div v-if="product" class="product-page">
    <section class="container product-top">
      <div class="breadcrumb">
        <router-link to="/home">首页</router-link><span>/</span>
        <router-link to="/shop">公益商城</router-link><span>/</span><span>{{ product.name }}</span>
      </div>
    </section>

    <section class="container product-grid">
      <div class="gallery">
        <div class="main-img">
          <img :src="cover" :alt="product.name" />
          <span class="donate-flag">每单捐赠 5%</span>
        </div>
      </div>

      <div class="info">
        <span class="p-cat">{{ product.category }} · 公益选品</span>
        <h1>{{ product.name }}</h1>
        <p class="sub">{{ product.description }}</p>
        <div class="rating-row">
          <span class="stars">★★★★★</span>
          <span>4.9 · {{ product.sales || 326 }} 人购买</span>
          <span class="stock">库存 {{ product.stock }} 件</span>
        </div>
        <div class="price-box">
          <strong>¥{{ Number(product.price).toFixed(2) }}</strong>
          <span>含公益捐赠 ¥{{ (product.price * 0.05).toFixed(2) }}</span>
        </div>
        <div class="service-row">
          <span>✓ 正品保障</span><span>✓ 满 99 包邮</span><span>✓ 7 天无理由</span>
        </div>
        <div class="qty-row">
          <span class="label">数量</span>
          <div class="qty">
            <button @click="qty > 1 && qty--">−</button>
            <span>{{ qty }}</span>
            <button @click="qty < product.stock && qty++">＋</button>
          </div>
          <span class="limit">每人限购 9 件</span>
        </div>
        <div class="actions">
          <button class="btn btn-primary btn-lg" @click="add(false)">加入购物袋</button>
          <button class="btn btn-dark btn-lg" @click="add(true)">立即购买</button>
        </div>
        <div class="benefit">
          <span>🌱</span>
          <p>你的这次购买，会为救助站捐出 ¥{{ (product.price * 0.05).toFixed(2) }}，用于流浪动物的粮食与医疗。</p>
        </div>
      </div>
    </section>

    <section class="container product-lower">
      <div class="desc-card">
        <div class="desc-tabs">
          <button :class="{ active: tab === 'desc' }" @click="tab = 'desc'">商品详情</button>
          <button :class="{ active: tab === 'service' }" @click="tab = 'service'">购买与配送</button>
        </div>
        <div v-if="tab === 'desc'" class="desc-body">
          <h3>关于这件商品</h3>
          <p>{{ product.description }}</p>
          <ul>
            <li>经过救助站志愿者实际试用，适合日常家庭使用。</li>
            <li>下单后 48 小时内发货，偏远地区物流时间可能延长。</li>
            <li>商品的一部分收益将进入「暖爪救助基金」。</li>
          </ul>
        </div>
        <div v-else class="desc-body">
          <h3>配送说明</h3>
          <p>订单支付后由合作物流公司发出，可在「我的订单」中查看物流轨迹。满 99 元包邮，未满收取 8 元运费。</p>
          <p>如收到商品有破损，请在 48 小时内拍照联系客服，我们会优先补发或退款。</p>
        </div>
      </div>

      <aside class="buy-box">
        <h3>购买前可以看看</h3>
        <router-link to="/donate" class="mini-link">直接支持救助站 →</router-link>
        <router-link to="/community" class="mini-link">看看大家的使用反馈 →</router-link>
        <router-link to="/chat" class="mini-link">问问救助助手 →</router-link>
      </aside>
    </section>

    <section class="container related">
      <div class="section-head">
        <div><span class="eyebrow">YOU MAY ALSO LIKE</span><h2 class="section-title">也许它也需要</h2></div>
        <router-link to="/shop" class="link-more">回到商城 →</router-link>
      </div>
      <div class="related-grid">
        <router-link v-for="p in related" :key="p.id" :to="'/product/' + p.id" class="rel-card">
          <div class="rel-img"><img :src="imageUrl(p.image)" :alt="p.name" /></div>
          <strong>{{ p.name }}</strong>
          <span>¥{{ Number(p.price).toFixed(2) }}</span>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script>
import { getData, imageUrl } from '../../utils/api'
import { demoProducts } from '../../utils/demo'
import { addToCart } from '../../utils/cart'

export default {
  name: 'ProductDetail',
  data() { return { product: null, qty: 1, tab: 'desc' } },
  computed: {
    cover() { return imageUrl(this.product && this.product.image) },
    related() { return demoProducts.filter(p => p.id !== this.product.id).slice(0, 4) }
  },
  created() {
    this.load()
  },
  methods: {
    imageUrl,
    async load() {
      const id = Number(this.$route.params.id)
      const fallback = demoProducts.find(p => p.id === id) || demoProducts[0]
      const data = await getData('/product/detail/' + id, null, fallback)
      this.product = data || fallback
      document.title = `${this.product.name} · 公益商城`
    },
    add(buyNow) {
      addToCart(this.product, this.qty)
      this.$message.success('已加入购物袋')
      if (buyNow) this.$router.push('/shop')
      else this.$router.push('/shop')
    }
  }
}
</script>

<style scoped>
.product-top { padding-top: 28px; }
.product-grid { display: grid; grid-template-columns: 1.05fr .95fr; gap: 60px; padding-top: 24px; }
.main-img { position: relative; aspect-ratio: 1/1; border-radius: 24px; overflow: hidden; background: #f7f5f0; }
.main-img img { width: 100%; height: 100%; object-fit: cover; }
.donate-flag { position: absolute; top: 18px; left: 18px; background: rgba(255,253,249,.94); color: var(--sage-dark); font-size: 10px; padding: 5px 12px; border-radius: 999px; }
.thumb-row { display: flex; gap: 10px; margin-top: 12px; }
.thumb-row button { width: 70px; height: 70px; border: 2px solid transparent; border-radius: 10px; overflow: hidden; padding: 0; cursor: pointer; opacity: .65; background: none; }
.thumb-row button.active { border-color: var(--primary); opacity: 1; }
.thumb-row img { width: 100%; height: 100%; object-fit: cover; }
.info { padding-top: 8px; }
.p-cat { font-size: 10px; letter-spacing: .1em; color: var(--primary); }
.info h1 { font-size: clamp(32px, 3.5vw, 44px); letter-spacing: -.045em; margin-top: 10px; line-height: 1.25; }
.sub { font-size: 13px; color: var(--muted); line-height: 1.9; margin-top: 12px; }
.rating-row { display: flex; align-items: center; gap: 12px; margin-top: 16px; font-size: 10.5px; color: var(--muted); }
.stars { color: var(--amber); letter-spacing: 2px; }
.stock { margin-left: auto; color: var(--sage-dark); background: var(--sage-soft); padding: 3px 9px; border-radius: 999px; }
.price-box { margin-top: 22px; padding: 18px 20px; background: var(--cream-2); border-radius: 14px; display: flex; align-items: baseline; gap: 14px; }
.price-box strong { font-family: var(--font-display); font-size: 40px; color: var(--primary); font-weight: 400; }
.price-box span { font-size: 10.5px; color: var(--sage-dark); }
.service-row { display: flex; gap: 16px; margin-top: 14px; font-size: 10px; color: var(--muted); flex-wrap: wrap; }
.qty-row { display: flex; align-items: center; gap: 14px; margin-top: 24px; }
.label { font-size: 11px; color: var(--muted); }
.qty { display: flex; align-items: center; border: 1px solid var(--line); border-radius: 10px; overflow: hidden; }
.qty button { width: 34px; height: 34px; border: none; background: var(--paper); font-size: 15px; cursor: pointer; color: var(--ink-2); }
.qty button:hover { background: var(--cream-2); }
.qty span { width: 36px; text-align: center; font-size: 12px; }
.limit { font-size: 9px; color: var(--muted-2); }
.actions { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 24px; }
.benefit { display: flex; gap: 10px; align-items: flex-start; margin-top: 18px; padding: 13px 15px; border: 1px dashed var(--line-strong); border-radius: 12px; }
.benefit span { font-size: 15px; }
.benefit p { font-size: 10.5px; color: var(--muted); line-height: 1.7; }

.product-lower { display: grid; grid-template-columns: 1fr 280px; gap: 24px; margin-top: 70px; }
.desc-card { background: var(--paper); border: 1px solid var(--line); border-radius: 20px; overflow: hidden; }
.desc-tabs { display: flex; gap: 28px; padding: 0 28px; border-bottom: 1px solid var(--line); }
.desc-tabs button { border: none; background: none; padding: 17px 0; font-size: 13px; color: var(--muted); cursor: pointer; position: relative; }
.desc-tabs button.active { color: var(--ink); font-weight: 700; }
.desc-tabs button.active::after { content: ''; position: absolute; left: 0; right: 0; bottom: -1px; height: 2px; background: var(--primary); }
.desc-body { padding: 28px 30px; }
.desc-body h3 { font-size: 16px; }
.desc-body p, .desc-body li { font-size: 12.5px; color: var(--ink-2); line-height: 1.95; margin-top: 10px; }
.desc-body ul { padding-left: 18px; }
.buy-box { background: var(--cream-2); border-radius: 20px; padding: 24px; height: fit-content; }
.buy-box h3 { font-size: 14px; }
.mini-link { display: block; font-size: 11px; color: var(--ink-2); padding: 10px 0; border-bottom: 1px solid var(--line-strong); }
.mini-link:hover { color: var(--primary); }
.related { margin-top: 70px; padding-bottom: 40px; }
.related-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.rel-card { background: var(--paper); border: 1px solid var(--line); border-radius: 16px; padding: 10px; display: flex; flex-direction: column; transition: all .3s; }
.rel-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-sm); }
.rel-img { aspect-ratio: 1/1; border-radius: 10px; overflow: hidden; background: #f7f5f0; }
.rel-img img { width: 100%; height: 100%; object-fit: cover; }
.rel-card strong { font-size: 12px; margin-top: 10px; line-height: 1.5; }
.rel-card span { font-size: 13px; color: var(--primary); margin-top: 3px; }

@media (max-width: 900px) {
  .product-grid { grid-template-columns: 1fr; gap: 28px; }
  .product-lower { grid-template-columns: 1fr; }
  .related-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
