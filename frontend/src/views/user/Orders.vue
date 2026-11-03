<template>
  <div class="orders-page">
    <section class="orders-head">
      <div class="container">
        <div class="breadcrumb"><router-link to="/home">首页</router-link><span>/</span><span>我的订单</span></div>
        <div class="head-row">
          <div><span class="eyebrow">MY ORDERS</span><h1>我的订单</h1><p>在这里查看订单状态、支付与物流信息。</p></div>
          <router-link to="/shop" class="btn btn-ghost">继续逛逛商城 →</router-link>
        </div>
      </div>
    </section>

    <section class="container orders-body">
      <div class="order-tabs">
        <button v-for="t in tabs" :key="t.value" :class="{ active: tab === t.value }" @click="tab = t.value">
          {{ t.label }}<span v-if="countByStatus(t.value)">{{ countByStatus(t.value) }}</span>
        </button>
      </div>

      <div v-if="filtered.length" class="order-list">
        <article v-for="o in filtered" :key="o.id" class="order-card">
          <div class="order-head">
            <div class="order-meta">
              <span class="order-no">订单号 {{ o.orderNo }}</span>
              <span class="order-date">{{ formatDate(o.createTime, true) }}</span>
            </div>
            <span class="status-pill" :class="'st-' + o.status">{{ statusLabel(o.status) }}</span>
          </div>
          <div class="order-items">
            <div v-for="item in (o.items || [])" :key="item.id || item.productId" class="order-item">
              <img :src="imageUrl(item.productImage)" :alt="item.productName" />
              <div class="oi-info">
                <strong>{{ item.productName }}</strong>
                <span>¥{{ Number(item.price).toFixed(2) }} × {{ item.quantity }}</span>
              </div>
              <span class="oi-subtotal">¥{{ (Number(item.price) * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <div class="order-foot">
            <div class="addr">
              <span>收货信息</span>
              <p>{{ o.receiverName }} · {{ o.receiverPhone }} · {{ o.receiverAddress }}</p>
            </div>
            <div class="totals">
              <span>共 {{ itemCount(o) }} 件，合计</span>
              <strong>¥{{ Number(o.totalAmount).toFixed(2) }}</strong>
            </div>
          </div>
          <div class="order-actions">
            <template v-if="o.status === 0">
              <button class="btn btn-ghost btn-sm" @click="cancelOrder(o)">取消订单</button>
              <button class="btn btn-primary btn-sm" @click="pay(o)">立即支付</button>
            </template>
            <template v-else-if="o.status === 1">
              <span class="hint">商家正在准备你的包裹</span>
              <button class="btn btn-ghost btn-sm" @click="contact">联系客服</button>
            </template>
            <template v-else-if="o.status === 2">
              <button class="btn btn-ghost btn-sm" @click="showLogistics(o)">查看物流</button>
              <button class="btn btn-dark btn-sm" @click="confirmReceive(o)">确认收货</button>
            </template>
            <template v-else-if="o.status === 3">
              <span class="hint">订单已完成，谢谢你的爱心</span>
              <button class="btn btn-ghost btn-sm" @click="buyAgain(o)">再来一单</button>
            </template>
            <template v-else>
              <span class="hint">订单已取消</span>
            </template>
          </div>
        </article>
      </div>
      <div v-else class="empty card">
        <div class="empty-icon">📦</div>
        <h3>这里还没有订单</h3>
        <p>去公益商城看看，给毛孩子挑点好东西吧。</p>
        <router-link to="/shop" class="btn btn-primary btn-sm">去逛逛</router-link>
      </div>
    </section>

    <el-dialog title="支付宝沙箱支付" :visible.sync="payVisible" width="440px">
      <div class="pay-panel">
        <div class="pay-logo">支</div>
        <span class="pay-title">支付宝沙箱</span>
        <div class="pay-amount">¥{{ current ? Number(current.totalAmount).toFixed(2) : '0.00' }}</div>
        <p>{{ current && current.orderNo }}</p>
        <div class="sandbox-note">毕业设计演示环境：点击确认后模拟支付成功，不会产生真实扣款。</div>
        <el-button type="primary" class="pay-btn" :loading="paying" @click="confirmPay">确认支付</el-button>
      </div>
    </el-dialog>

    <el-dialog title="物流信息" :visible.sync="logisticsVisible" width="520px">
      <div v-if="logistics" class="logistics-box">
        <div class="log-head">
          <div><span class="log-company">{{ logistics.companyName || '合作物流' }}</span><strong>{{ logistics.trackingNo || '暂无单号' }}</strong></div>
          <span class="pill">{{ logistics.status || '运输中' }}</span>
        </div>
        <div class="log-steps">
          <div class="log-step done"><i></i><div><strong>已发货</strong><p>{{ logistics.senderAddress || '暖爪之家公益仓' }}</p></div></div>
          <div class="log-step done"><i></i><div><strong>运输中</strong><p>{{ logistics.traceInfo || '快件正在运输途中' }}</p></div></div>
          <div class="log-step"><i></i><div><strong>待收货</strong><p>{{ logistics.receiverAddress }}</p></div></div>
        </div>
      </div>
      <div v-else class="no-log">暂无物流信息，商家发货后可在此查看。</div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, imageUrl, formatDate } from '../../utils/api'
import { demoOrders } from '../../utils/demo'

export default {
  name: 'Orders',
  data() {
    return {
      orders: demoOrders,
      tab: 'all',
      tabs: [
        { label: '全部订单', value: 'all' },
        { label: '待付款', value: 0 },
        { label: '待发货', value: 1 },
        { label: '待收货', value: 2 },
        { label: '已完成', value: 3 }
      ],
      payVisible: false,
      paying: false,
      current: null,
      logisticsVisible: false,
      logistics: null
    }
  },
  computed: {
    filtered() {
      if (this.tab === 'all') return this.orders
      return this.orders.filter(o => o.status === this.tab)
    }
  },
  created() {
    document.title = '我的订单 · 暖爪之家'
    this.load()
  },
  methods: {
    formatDate,
    imageUrl,
    async load() {
      const data = await getData('/order/my', { pageNum: 1, pageSize: 20 }, { list: demoOrders })
      if (data && data.list && data.list.length) this.orders = data.list
    },
    statusLabel(s) { return ['待付款', '待发货', '运输中', '已完成', '已取消'][s] || '未知' },
    countByStatus(s) { return this.orders.filter(o => o.status === s).length },
    itemCount(o) { return (o.items || []).reduce((n, i) => n + i.quantity, 0) },
    pay(o) { this.current = o; this.payVisible = true },
    async confirmPay() {
      this.paying = true
      await postData('/order/pay/' + this.current.id, {}, { ok: true })
      this.current.status = 1
      this.paying = false
      this.payVisible = false
      this.$message.success('支付成功，感谢你的爱心')
    },
    async cancelOrder(o) {
      try {
        await this.$confirm('确定要取消这个订单吗？', '取消订单', { type: 'warning' })
      } catch (e) { return }
      await postData('/order/cancel/' + o.id, {}, { ok: true })
      o.status = 4
      this.$message.success('订单已取消')
    },
    showLogistics(o) {
      this.logistics = o.logistics || {
        companyName: '顺丰速运', trackingNo: 'SF' + o.orderNo.slice(-10),
        status: '运输中', senderAddress: '暖爪公益仓 · 上海',
        receiverAddress: o.receiverAddress, traceInfo: '快件已到达上海徐汇中转场'
      }
      this.logisticsVisible = true
    },
    confirmReceive(o) {
      o.status = 3
      this.$message.success('已确认收货')
    },
    buyAgain(o) {
      this.$router.push('/shop')
    },
    contact() {
      this.$message.info('客服热线：400-123-4567')
    }
  }
}
</script>

<style scoped>
.orders-head { padding: 28px 0 34px; background: linear-gradient(180deg, #fdf9f3, var(--cream)); border-bottom: 1px solid var(--line); }
.head-row { display: flex; align-items: flex-end; justify-content: space-between; gap: 20px; margin-top: 18px; }
.head-row h1 { font-size: clamp(32px, 3.5vw, 44px); letter-spacing: -.04em; margin-top: 8px; }
.head-row p { font-size: 12px; color: var(--muted); margin-top: 4px; }
.orders-body { padding-top: 30px; padding-bottom: 80px; }
.order-tabs { display: flex; gap: 6px; border-bottom: 1px solid var(--line); margin-bottom: 22px; overflow-x: auto; }
.order-tabs button { border: none; background: none; padding: 12px 18px; font-size: 12.5px; color: var(--muted); cursor: pointer; position: relative; white-space: nowrap; }
.order-tabs button.active { color: var(--ink); font-weight: 700; }
.order-tabs button.active::after { content: ''; position: absolute; left: 14px; right: 14px; bottom: -1px; height: 2px; background: var(--primary); }
.order-tabs span { font-size: 9px; background: var(--cream-2); border-radius: 999px; padding: 1px 6px; margin-left: 4px; }
.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card { background: var(--paper); border: 1px solid var(--line); border-radius: 18px; overflow: hidden; }
.order-head { display: flex; justify-content: space-between; align-items: center; padding: 15px 22px; border-bottom: 1px solid var(--line); }
.order-meta { display: flex; gap: 16px; align-items: center; }
.order-no { font-size: 11px; font-weight: 600; }
.order-date { font-size: 9.5px; color: var(--muted-2); }
.status-pill { font-size: 10px; padding: 4px 10px; border-radius: 999px; background: var(--cream-2); color: var(--ink-2); }
.status-pill.st-0 { background: #fbf0dc; color: #9a6b22; }
.status-pill.st-1 { background: #edf1f6; color: #52708e; }
.status-pill.st-2 { background: var(--primary-soft); color: var(--primary); }
.status-pill.st-3 { background: var(--sage-soft); color: var(--sage-dark); }
.status-pill.st-4 { background: #f0f0ee; color: var(--muted); }
.order-items { padding: 6px 22px; }
.order-item { display: flex; align-items: center; gap: 14px; padding: 12px 0; border-bottom: 1px solid var(--line); }
.order-item:last-child { border-bottom: none; }
.order-item img { width: 64px; height: 64px; border-radius: 10px; object-fit: cover; }
.oi-info { flex: 1; display: flex; flex-direction: column; }
.oi-info strong { font-size: 12px; }
.oi-info span { font-size: 10px; color: var(--muted); margin-top: 4px; }
.oi-subtotal { font-size: 12px; }
.order-foot { display: flex; justify-content: space-between; align-items: flex-end; gap: 20px; padding: 14px 22px; background: #fdfaf5; border-top: 1px solid var(--line); }
.addr span { font-size: 9px; color: var(--muted-2); }
.addr p { font-size: 10.5px; color: var(--ink-2); margin-top: 2px; }
.totals { text-align: right; white-space: nowrap; }
.totals span { font-size: 10px; color: var(--muted); }
.totals strong { display: block; font-family: var(--font-display); font-size: 22px; color: var(--primary); font-weight: 400; }
.order-actions { display: flex; justify-content: flex-end; align-items: center; gap: 10px; padding: 14px 22px; }
.hint { font-size: 10px; color: var(--muted); margin-right: auto; }
.empty { text-align: center; padding: 70px 20px; }
.empty-icon { font-size: 40px; }
.empty h3 { font-size: 17px; margin-top: 12px; }
.empty p { font-size: 12px; color: var(--muted); margin: 6px 0 18px; }
.pay-panel { text-align: center; padding: 6px 0 10px; }
.pay-logo { width: 44px; height: 44px; border-radius: 12px; background: #1677ff; color: #fff; font-size: 21px; display: flex; align-items: center; justify-content: center; margin: 0 auto 8px; }
.pay-title { font-size: 12px; color: var(--muted); }
.pay-amount { font-family: var(--font-display); font-size: 42px; margin-top: 12px; }
.pay-panel p { font-size: 10px; color: var(--muted-2); margin-top: 2px; }
.sandbox-note { background: #fbf0dc; color: #8a6a2a; font-size: 10px; border-radius: 10px; padding: 10px 12px; margin: 18px 0; line-height: 1.6; }
.pay-btn { width: 100%; height: 44px; border-radius: 10px; }
.log-head { display: flex; justify-content: space-between; align-items: center; padding-bottom: 16px; border-bottom: 1px solid var(--line); }
.log-head > div { display: flex; flex-direction: column; }
.log-company { font-size: 10px; color: var(--muted); }
.log-head strong { font-size: 14px; margin-top: 2px; }
.log-steps { padding-top: 18px; }
.log-step { display: flex; gap: 14px; padding-bottom: 22px; position: relative; }
.log-step::before { content: ''; position: absolute; left: 5px; top: 14px; bottom: 0; width: 1px; background: var(--line); }
.log-step:last-child::before { display: none; }
.log-step i { width: 11px; height: 11px; border-radius: 50%; border: 2px solid var(--line-strong); background: var(--paper); flex-shrink: 0; margin-top: 4px; z-index: 1; }
.log-step.done i { border-color: var(--sage); background: var(--sage); }
.log-step strong { font-size: 12px; }
.log-step p { font-size: 10.5px; color: var(--muted); margin-top: 2px; }
.no-log { text-align: center; color: var(--muted); font-size: 12px; padding: 30px 0; }

@media (max-width: 680px) {
  .head-row { flex-direction: column; align-items: flex-start; }
  .order-head { flex-direction: column; align-items: flex-start; gap: 8px; }
  .order-foot { flex-direction: column; align-items: flex-start; }
  .totals { text-align: left; }
  .order-actions { flex-wrap: wrap; }
}
</style>
