<template>
  <div class="shop-page">
    <section class="page-hero shop-hero">
      <div class="container hero-grid">
        <div>
          <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>公益商城</span></div>
          <span class="eyebrow">WARM PAWS SHOP</span>
          <h1>给毛孩子的好东西，<br />也能帮到更多孩子。</h1>
          <p>每一笔订单都会拿出 5% 支持流浪动物救助。我们只挑选自己也会给家里毛孩子用的东西。</p>
          <div class="hero-tags">
            <span>✓ 每单捐赠 5%</span>
            <span>✓ 满 99 元包邮</span>
            <span>✓ 7 天无理由</span>
          </div>
        </div>
        <div class="hero-card">
          <div class="hc-img">
            <img src="/images/prod6.jpg" alt="宠物窝" />
          </div>
          <div class="hc-info">
            <span class="pill pill-warm">本月推荐</span>
            <h3>四季可拆洗宠物窝</h3>
            <p>加厚软垫 · 可拆洗 · 中小型犬猫适用</p>
            <div class="hc-bottom"><strong>¥159</strong><button @click="add(productList[5] || productList[0])">加入购物袋</button></div>
          </div>
        </div>
      </div>
    </section>

    <section class="container shop-body">
      <div class="shop-toolbar">
        <div class="chip-row">
          <button v-for="c in categories" :key="c" class="chip" :class="{ active: category === c }" @click="category = c">{{ c }}</button>
        </div>
        <div class="toolbar-right">
          <el-input v-model="keyword" size="small" placeholder="搜索用品" prefix-icon="el-icon-search" clearable class="search-input" @input="search" />
          <button class="cart-btn" @click="cartOpen = true">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18M16 10a4 4 0 0 1-8 0"/></svg>
            购物袋
            <span v-if="count" class="cart-count">{{ count }}</span>
          </button>
        </div>
      </div>

      <div class="result-line"><span>共 {{ filtered.length }} 件商品</span><span class="sort">按推荐排序</span></div>

      <div class="product-grid">
        <article v-for="p in filtered" :key="p.id" class="product-card">
          <router-link :to="'/product/' + p.id" class="p-img">
            <img :src="imageUrl(p.image)" :alt="p.name" loading="lazy" />
            <span v-if="p.stock < 30" class="low-stock">仅剩 {{ p.stock }} 件</span>
          </router-link>
          <div class="p-body">
            <span class="p-cat">{{ p.category }}</span>
            <router-link :to="'/product/' + p.id"><h3>{{ p.name }}</h3></router-link>
            <p>{{ p.description }}</p>
            <div class="p-bottom">
              <div class="price"><strong>¥{{ Number(p.price).toFixed(2) }}</strong><small v-if="p.sales">已售 {{ p.sales }}</small></div>
              <button class="add-btn" @click="add(p)">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
              </button>
            </div>
          </div>
        </article>
      </div>

      <div class="shop-note">
        <div class="note-icon">🌱</div>
        <div>
          <h3>关于公益商城</h3>
          <p>商城的选品会优先考虑实用、安全与可持续。每笔订单的 5% 会进入救助基金，用于粮食、医疗和绝育项目，每月在社区公示。</p>
        </div>
        <router-link to="/donate" class="link-more">直接捐赠 →</router-link>
      </div>
    </section>

    <!-- 购物袋 -->
    <div v-if="cartOpen" class="cart-mask" @click="cartOpen = false"></div>
    <aside class="cart-drawer" :class="{ open: cartOpen }">
      <div class="cart-head">
        <div><h3>购物袋</h3><small>{{ cart.length }} 种商品</small></div>
        <button @click="cartOpen = false">×</button>
      </div>
      <div class="cart-items">
        <div v-if="!cart.length" class="cart-empty">
          <span>🛍️</span><p>购物袋还是空的</p><small>去看看毛孩子需要什么吧</small>
        </div>
        <div v-for="item in cart" :key="item.id" class="cart-item">
          <img :src="imageUrl(item.image)" :alt="item.name" />
          <div class="ci-info">
            <strong>{{ item.name }}</strong>
            <span>¥{{ item.price.toFixed(2) }}</span>
            <div class="qty">
              <button @click="changeQty(item, -1)">−</button>
              <span>{{ item.quantity }}</span>
              <button @click="changeQty(item, 1)">＋</button>
              <button class="rm" @click="remove(item)">移除</button>
            </div>
          </div>
        </div>
      </div>
      <div class="cart-foot">
        <div class="sum"><span>商品合计</span><strong>¥{{ total.toFixed(2) }}</strong></div>
        <p class="donate-tip">其中 ¥{{ (total * 0.05).toFixed(2) }} 将用于流浪动物救助</p>
        <button class="btn btn-primary btn-block" :disabled="!cart.length" @click="startCheckout">去结算</button>
        <span class="ship-note">{{ total >= 99 ? '已满足包邮条件' : '再买 ¥' + (99 - total).toFixed(2) + ' 即可包邮' }}</span>
      </div>
    </aside>

    <!-- 结算 -->
    <el-dialog :title="checkoutStep === 1 ? '确认订单' : '订单已创建'" :visible.sync="checkoutVisible" width="560px" :show-close="checkoutStep === 1">
      <div v-if="checkoutStep === 1">
        <div class="order-items">
          <div v-for="item in cart" :key="item.id" class="oi-row">
            <img :src="imageUrl(item.image)" alt="" />
            <span>{{ item.name }} × {{ item.quantity }}</span>
            <strong>¥{{ (item.price * item.quantity).toFixed(2) }}</strong>
          </div>
        </div>
        <el-form :model="orderForm" label-position="top" style="margin-top:18px">
          <div class="form-grid">
            <el-form-item label="收货人"><el-input v-model="orderForm.receiverName" placeholder="姓名" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="orderForm.receiverPhone" placeholder="联系电话" /></el-form-item>
          </div>
          <el-form-item label="收货地址"><el-input v-model="orderForm.receiverAddress" placeholder="省 / 市 / 区 / 详细地址" /></el-form-item>
        </el-form>
        <div class="checkout-sum"><span>应付金额</span><strong>¥{{ total.toFixed(2) }}</strong></div>
        <div class="checkout-methods"><span>支</span>支付宝沙箱支付 <small>演示环境</small></div>
      </div>
      <div v-else class="pay-success">
        <div class="success-icon">✓</div>
        <h3>订单创建成功</h3>
        <p>订单号：{{ createdOrder.orderNo }}</p>
        <div class="pay-amount">¥{{ total.toFixed(2) }}</div>
        <el-button type="primary" :loading="paying" @click="payNow">使用支付宝沙箱支付</el-button>
        <el-button @click="$router.push('/orders')">稍后支付</el-button>
        <p class="pay-hint">此项目为毕业设计演示，支付会模拟成功，不会产生真实扣款。</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, imageUrl, requireLogin } from '../../utils/api'
import { demoProducts } from '../../utils/demo'
import { readCart, addToCart, updateQty, removeItem, cartCount, cartTotal } from '../../utils/cart'

export default {
  name: 'Shop',
  data() {
    return {
      products: demoProducts,
      categories: ['全部', '食品', '用品', '玩具', '清洁', '出行'],
      category: '全部',
      keyword: '',
      cart: [],
      cartOpen: false,
      checkoutVisible: false,
      checkoutStep: 1,
      paying: false,
      createdOrder: {},
      orderForm: { receiverName: '', receiverPhone: '', receiverAddress: '' }
    }
  },
  computed: {
    productList() { return this.products },
    filtered() {
      let list = this.products.slice()
      if (this.category !== '全部') list = list.filter(p => p.category === this.category || (this.category === '用品' && ['窝', '砂', '盆', '胸背'].some(k => p.name.indexOf(k) >= 0)))
      if (this.keyword) list = list.filter(p => p.name.indexOf(this.keyword) >= 0 || (p.description || '').indexOf(this.keyword) >= 0)
      return list
    },
    count() { return cartCount(this.cart) },
    total() { return cartTotal(this.cart) }
  },
  created() {
    document.title = '公益商城 · 暖爪之家'
    this.cart = readCart()
    this.load()
    window.addEventListener('cart-updated', this.syncCart)
  },
  beforeDestroy() { window.removeEventListener('cart-updated', this.syncCart) },
  methods: {
    imageUrl,
    async load() {
      const data = await getData('/product/list', { pageNum: 1, pageSize: 30 }, { list: demoProducts })
      if (data && data.list && data.list.length) this.products = data.list
    },
    product() { return this.products[5] || this.products[0] },
    search() {},
    add(product) {
      if (!product) return
      if (product.stock !== undefined && product.stock <= 0) return this.$message.warning('该商品暂时缺货')
      addToCart(product)
      this.cart = readCart()
      this.$message.success('已加入购物袋')
      this.cartOpen = true
    },
    syncCart(e) { this.cart = e.detail || readCart() },
    changeQty(item, delta) {
      this.cart = updateQty(item.id, item.quantity + delta)
    },
    remove(item) {
      this.cart = removeItem(item.id)
    },
    startCheckout() {
      if (!requireLogin(this, '/shop')) return
      const user = this.$store.state.userInfo || {}
      this.orderForm.receiverName = user.nickname || user.username || ''
      this.orderForm.receiverPhone = user.phone || ''
      this.checkoutVisible = true
      this.checkoutStep = 1
    },
    async submitOrder() {
      if (!this.orderForm.receiverName || !this.orderForm.receiverPhone || !this.orderForm.receiverAddress) {
        return this.$message.warning('请填写完整的收货信息')
      }
      const items = this.cart.map(i => ({ productId: i.id, quantity: i.quantity }))
      const fallback = { orderId: Date.now(), orderNo: 'PET' + Date.now(), totalAmount: this.total }
      const data = await postData('/order', Object.assign({ items }, this.orderForm), fallback)
      this.createdOrder = data || fallback
      this.checkoutStep = 2
    },
    async payNow() {
      this.paying = true
      await postData('/order/pay/' + this.createdOrder.orderId, {}, { ok: true })
      this.paying = false
      localStorage.removeItem('warmpaws_cart')
      window.dispatchEvent(new CustomEvent('cart-updated', { detail: [] }))
      this.cart = []
      this.checkoutVisible = false
      this.cartOpen = false
      this.$message.success('支付成功，感谢你的爱心')
      this.$router.push('/orders')
    }
  }
}
</script>

<style scoped>
.shop-hero { padding-bottom: 44px; }
.hero-grid { display: grid; grid-template-columns: 1fr 430px; gap: 60px; align-items: center; }
.page-hero h1 { font-size: clamp(38px, 4.4vw, 56px); line-height: 1.22; margin-top: 12px; }
.hero-tags { display: flex; gap: 18px; margin-top: 22px; flex-wrap: wrap; }
.hero-tags span { font-size: 10.5px; color: var(--sage-dark); }
.hero-card { display: grid; grid-template-columns: 150px 1fr; gap: 16px; background: var(--paper); border: 1px solid var(--line); border-radius: 20px; padding: 12px; box-shadow: var(--shadow); }
.hc-img { border-radius: 14px; overflow: hidden; height: 150px; }
.hc-img img { width: 100%; height: 100%; object-fit: cover; }
.hc-info { display: flex; flex-direction: column; padding: 6px 4px; }
.hc-info h3 { font-size: 16px; margin-top: 8px; }
.hc-info p { font-size: 10.5px; color: var(--muted); margin-top: 4px; line-height: 1.6; }
.hc-bottom { display: flex; align-items: center; justify-content: space-between; margin-top: auto; }
.hc-bottom strong { font-family: var(--font-display); font-size: 23px; color: var(--primary); font-weight: 400; }
.hc-bottom button { border: none; background: var(--ink); color: #fff; font-size: 10px; padding: 7px 12px; border-radius: 999px; cursor: pointer; }

.shop-body { padding-top: 34px; padding-bottom: 80px; }
.shop-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 20px; margin-bottom: 18px; }
.toolbar-right { display: flex; align-items: center; gap: 10px; }
.search-input { width: 160px; }
.cart-btn { position: relative; display: flex; align-items: center; gap: 6px; border: 1px solid var(--line); background: var(--paper); border-radius: 999px; height: 34px; padding: 0 14px; font-size: 11px; color: var(--ink-2); cursor: pointer; }
.cart-btn:hover { border-color: var(--ink); }
.cart-count { background: var(--primary); color: #fff; font-size: 9px; width: 17px; height: 17px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.result-line { display: flex; justify-content: space-between; font-size: 10.5px; color: var(--muted); margin-bottom: 16px; padding: 0 2px; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; overflow: hidden; transition: all .3s; }
.product-card:hover { transform: translateY(-5px); box-shadow: var(--shadow-sm); }
.p-img { position: relative; aspect-ratio: 1/1; overflow: hidden; background: #f7f5f0; }
.p-img img { width: 100%; height: 100%; object-fit: cover; transition: transform .6s; }
.product-card:hover .p-img img { transform: scale(1.04); }
.low-stock { position: absolute; left: 12px; top: 12px; font-size: 9px; background: rgba(255,253,249,.92); color: var(--primary); padding: 3px 8px; border-radius: 999px; }
.p-body { padding: 15px 16px 14px; }
.p-cat { font-size: 9px; color: var(--muted-2); letter-spacing: .05em; }
.p-body h3 { font-size: 13.5px; margin-top: 4px; line-height: 1.5; }
.p-body > p { font-size: 10px; color: var(--muted); margin-top: 5px; line-height: 1.6; height: 32px; overflow: hidden; }
.p-bottom { display: flex; align-items: flex-end; justify-content: space-between; margin-top: 10px; }
.price { display: flex; flex-direction: column; }
.price strong { font-family: var(--font-display); font-size: 19px; color: var(--ink); font-weight: 400; }
.price small { font-size: 8.5px; color: var(--muted-2); }
.add-btn { width: 30px; height: 30px; border-radius: 50%; border: none; background: var(--ink); color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: background .2s; }
.add-btn:hover { background: var(--primary); }
.shop-note { display: flex; align-items: center; gap: 20px; background: var(--sage-soft); border-radius: 20px; padding: 26px 30px; margin-top: 50px; }
.note-icon { font-size: 24px; }
.shop-note div:nth-child(2) { flex: 1; }
.shop-note h3 { font-size: 15px; }
.shop-note p { font-size: 11.5px; color: var(--sage-dark); line-height: 1.8; margin-top: 4px; }

.cart-mask { position: fixed; inset: 0; background: rgba(24,36,30,.35); backdrop-filter: blur(2px); z-index: 200; }
.cart-drawer { position: fixed; top: 0; right: 0; bottom: 0; width: 380px; max-width: 92vw; background: var(--paper); z-index: 201; transform: translateX(105%); transition: transform .35s cubic-bezier(.2,.8,.2,1); display: flex; flex-direction: column; box-shadow: -20px 0 50px rgba(24,36,30,.15); }
.cart-drawer.open { transform: none; }
.cart-head { display: flex; justify-content: space-between; align-items: flex-start; padding: 24px 24px 16px; border-bottom: 1px solid var(--line); }
.cart-head h3 { font-size: 18px; }
.cart-head small { font-size: 10px; color: var(--muted); }
.cart-head button { border: none; background: var(--cream-2); width: 30px; height: 30px; border-radius: 50%; font-size: 18px; color: var(--ink-2); cursor: pointer; }
.cart-items { flex: 1; overflow-y: auto; padding: 16px 24px; }
.cart-empty { text-align: center; padding: 60px 0; }
.cart-empty span { font-size: 34px; }
.cart-empty p { font-size: 13px; margin-top: 10px; }
.cart-empty small { font-size: 10px; color: var(--muted); }
.cart-item { display: flex; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--line); }
.cart-item img { width: 72px; height: 72px; border-radius: 10px; object-fit: cover; }
.ci-info { display: flex; flex-direction: column; flex: 1; }
.ci-info strong { font-size: 11.5px; line-height: 1.4; }
.ci-info > span { font-size: 11px; color: var(--primary); margin-top: 3px; }
.qty { display: flex; align-items: center; gap: 8px; margin-top: auto; }
.qty button { width: 22px; height: 22px; border-radius: 6px; border: 1px solid var(--line); background: var(--paper); cursor: pointer; font-size: 12px; color: var(--ink-2); }
.qty span { font-size: 11px; min-width: 14px; text-align: center; }
.qty .rm { width: auto; border: none; background: none; color: var(--muted-2); font-size: 9px; margin-left: auto; }
.cart-foot { padding: 18px 24px 26px; border-top: 1px solid var(--line); }
.sum { display: flex; justify-content: space-between; align-items: baseline; }
.sum span { font-size: 12px; color: var(--muted); }
.sum strong { font-family: var(--font-display); font-size: 27px; color: var(--ink); font-weight: 400; }
.donate-tip { font-size: 9.5px; color: var(--sage-dark); margin: 4px 0 14px; }
.ship-note { display: block; text-align: center; font-size: 9px; color: var(--muted-2); margin-top: 8px; }
.order-items { border: 1px solid var(--line); border-radius: 12px; overflow: hidden; }
.oi-row { display: flex; align-items: center; gap: 10px; padding: 10px 12px; border-bottom: 1px solid var(--line); }
.oi-row:last-child { border-bottom: none; }
.oi-row img { width: 38px; height: 38px; border-radius: 7px; object-fit: cover; }
.oi-row span { flex: 1; font-size: 11px; }
.oi-row strong { font-size: 11px; }
.checkout-sum { display: flex; justify-content: space-between; align-items: baseline; padding: 14px 0; border-top: 1px solid var(--line); margin-top: 12px; }
.checkout-sum span { font-size: 12px; color: var(--muted); }
.checkout-sum strong { font-family: var(--font-display); font-size: 26px; color: var(--primary); }
.checkout-methods { display: flex; align-items: center; gap: 8px; border: 1px solid var(--primary); background: var(--primary-soft); border-radius: 10px; padding: 10px 14px; font-size: 11px; }
.checkout-methods span { width: 20px; height: 20px; border-radius: 5px; background: #1677ff; color: #fff; font-size: 9px; display: flex; align-items: center; justify-content: center; }
.checkout-methods small { margin-left: auto; color: var(--primary); font-size: 9px; }
.pay-success { text-align: center; padding: 10px 0; }
.success-icon { width: 48px; height: 48px; border-radius: 50%; background: var(--sage); color: #fff; font-size: 22px; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; }
.pay-success h3 { font-size: 18px; }
.pay-success p { font-size: 11px; color: var(--muted); margin-top: 6px; }
.pay-amount { font-family: var(--font-display); font-size: 36px; color: var(--primary); margin: 14px 0; }
.pay-hint { margin-top: 14px !important; font-size: 9.5px !important; }

@media (max-width: 1080px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
  .hero-grid { grid-template-columns: 1fr 360px; gap: 30px; }
}
@media (max-width: 820px) {
  .hero-grid { grid-template-columns: 1fr; }
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 14px; }
  .shop-toolbar { flex-direction: column; align-items: flex-start; }
  .toolbar-right { width: 100%; }
  .search-input { flex: 1; }
}
@media (max-width: 520px) {
  .hero-card { grid-template-columns: 110px 1fr; }
  .hc-img { height: 120px; }
  .shop-note { flex-direction: column; align-items: flex-start; }
}
</style>
