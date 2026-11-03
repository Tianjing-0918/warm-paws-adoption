<template>
  <div>
    <div class="admin-page-head">
      <div><h1>订单与物流</h1><p>查看商城订单，填写发货信息并跟进物流状态</p></div>
      <div class="actions"><button class="a-btn" @click="load">刷新</button></div>
    </div>

    <div class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-top"><span>{{ s.label }}</span><span class="stat-icon" v-html="s.icon"></span></div>
        <div class="stat-val">{{ s.value }}</div>
        <div class="stat-foot">{{ s.note }}</div>
      </div>
    </div>

    <div class="a-filter">
      <div class="status-tabs">
        <button v-for="t in tabs" :key="t.value" :class="{ active: tab === t.value }" @click="tab = t.value">{{ t.label }}</button>
      </div>
      <span class="spacer"></span>
      <el-input v-model="keyword" size="small" placeholder="搜索订单号 / 收货人" clearable style="width:210px" />
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>订单</th><th>商品</th><th>金额</th><th>收货人</th><th>状态</th><th>下单时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="o in filtered" :key="o.id">
            <td><div class="order-no"><strong>{{ o.orderNo }}</strong><small>{{ o.username || '用户 #' + o.userId }}</small></div></td>
            <td><div class="item-preview"><img v-for="i in (o.items || []).slice(0, 3)" :key="i.id || i.productId" :src="imageUrl(i.productImage)" alt="" /><span>{{ (o.items || []).length }} 件</span></div></td>
            <td class="price">¥{{ Number(o.totalAmount).toFixed(2) }}</td>
            <td><div class="receiver"><strong>{{ o.receiverName }}</strong><small>{{ o.receiverPhone }}</small></div></td>
            <td><span class="status-tag" :class="statusClass(o.status)">{{ statusLabel(o.status) }}</span></td>
            <td>{{ formatDate(o.createTime, true) }}</td>
            <td>
              <div class="op">
                <button class="a-link" @click="openDetail(o)">详情</button>
                <button v-if="o.status === 1" class="a-link" @click="openShip(o)">发货</button>
                <button v-if="o.status === 2" class="a-link" @click="openLogistics(o)">物流</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">📦</div><p>没有符合条件的订单</p></div>
    </div>

    <el-dialog title="订单详情" :visible.sync="detailVisible" width="620px">
      <div v-if="current">
        <div class="detail-head">
          <div><span class="status-tag" :class="statusClass(current.status)">{{ statusLabel(current.status) }}</span><h2>{{ current.orderNo }}</h2></div>
          <strong class="d-amount">¥{{ Number(current.totalAmount).toFixed(2) }}</strong>
        </div>
        <div class="items">
          <div v-for="i in (current.items || [])" :key="i.id || i.productId" class="item-row">
            <img :src="imageUrl(i.productImage)" alt="" />
            <div><strong>{{ i.productName }}</strong><small>¥{{ Number(i.price).toFixed(2) }} × {{ i.quantity }}</small></div>
            <span>¥{{ (Number(i.price) * i.quantity).toFixed(2) }}</span>
          </div>
        </div>
        <div class="dialog-block"><span class="detail-label">收货信息</span><p>{{ current.receiverName }} · {{ current.receiverPhone }}<br />{{ current.receiverAddress }}</p></div>
        <div v-if="current.logistics" class="dialog-block"><span class="detail-label">物流信息</span><p>{{ current.logistics.companyName }} · {{ current.logistics.trackingNo }} · {{ current.logistics.status }}</p></div>
      </div>
    </el-dialog>

    <el-dialog title="填写发货信息" :visible.sync="shipVisible" width="560px">
      <div v-if="current" class="ship-order">
        <span>订单 {{ current.orderNo }}</span>
        <strong>¥{{ Number(current.totalAmount).toFixed(2) }}</strong>
      </div>
      <el-form :model="shipForm" label-position="top">
        <div class="dialog-grid">
          <el-form-item label="物流公司">
            <el-select v-model="shipForm.companyId" style="width:100%">
              <el-option v-for="c in companies" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="运单号"><el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" /></el-form-item>
        </div>
        <el-form-item label="发货地址"><el-input v-model="shipForm.senderAddress" /></el-form-item>
        <el-form-item label="收货地址"><el-input v-model="shipForm.receiverAddress" /></el-form-item>
        <el-form-item label="物流备注"><el-input v-model="shipForm.traceInfo" placeholder="例如：快件已揽收" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="ship">确认发货</el-button>
      </div>
    </el-dialog>

    <el-dialog title="物流轨迹" :visible.sync="logVisible" width="500px">
      <div v-if="logistics" class="log-box">
        <div class="log-top"><strong>{{ logistics.companyName }}</strong><span>{{ logistics.trackingNo }}</span></div>
        <div class="trace">
          <div class="trace-item done"><i></i><div><strong>已发货</strong><p>{{ logistics.senderAddress }}</p><small>今天 09:20</small></div></div>
          <div class="trace-item done"><i></i><div><strong>运输中</strong><p>{{ logistics.traceInfo || '快件运输中' }}</p><small>今天 14:36</small></div></div>
          <div class="trace-item"><i></i><div><strong>派送中</strong><p>等待派送</p></div></div>
          <div class="trace-item"><i></i><div><strong>已签收</strong><p>{{ logistics.receiverAddress }}</p></div></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, imageUrl, formatDate } from '../../utils/api'
import { demoAdminOrders, demoCompanies } from '../../utils/demo'

export default {
  name: 'OrderManage',
  data() {
    return {
      list: demoAdminOrders,
      companies: demoCompanies,
      tab: 'all',
      keyword: '',
      tabs: [
        { label: '全部', value: 'all' },
        { label: '待付款', value: 0 },
        { label: '待发货', value: 1 },
        { label: '运输中', value: 2 },
        { label: '已完成', value: 3 }
      ],
      detailVisible: false,
      shipVisible: false,
      logVisible: false,
      current: null,
      logistics: null,
      shipping: false,
      shipForm: {}
    }
  },
  computed: {
    filtered() {
      let list = this.list.slice()
      if (this.tab !== 'all') list = list.filter(o => o.status === this.tab)
      if (this.keyword) list = list.filter(o => (o.orderNo || '').indexOf(this.keyword) >= 0 || (o.receiverName || '').indexOf(this.keyword) >= 0)
      return list
    },
    stats() {
      return [
        { label: '今日订单', value: 18, note: '较昨日 +4', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M6 2 3 6v14h18V6l-3-4Z"/><path d="M3 6h18M16 10a4 4 0 0 1-8 0"/></svg>' },
        { label: '待发货', value: this.list.filter(o => o.status === 1).length, note: '请及时处理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M3 7h11v10H3zM14 10h4l3 3v4h-7z"/><circle cx="7" cy="18" r="1.6"/><circle cx="17" cy="18" r="1.6"/></svg>' },
        { label: '本月销售额', value: '¥8,642', note: '公益捐赠 ¥432', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M12 2v20M17 6H9.5a3.5 3.5 0 0 0 0 7H14a3.5 3.5 0 0 1 0 7H6"/></svg>' },
        { label: '平均发货时长', value: '6.2h', note: '目标 < 12 小时', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>' }
      ]
    }
  },
  created() {
    document.title = '订单物流 · 暖爪管理后台'
    this.load()
  },
  methods: {
    imageUrl, formatDate,
    statusLabel(s) { return ['待付款', '待发货', '运输中', '已完成', '已取消'][s] },
    statusClass(s) { return s === 1 ? 'warn' : s === 2 ? 'info' : s === 3 ? 'ok' : s === 4 ? 'danger' : '' },
    async load() {
      const [o, c] = await Promise.all([
        getData('/order/adminPage', { pageNum: 1, pageSize: 100 }, { list: demoAdminOrders }),
        getData('/company/list', null, demoCompanies)
      ])
      if (o && o.list && o.list.length) this.list = o.list
      if (c && c.length) this.companies = c
    },
    openDetail(o) { this.current = o; this.detailVisible = true },
    openShip(o) {
      this.current = o
      this.shipForm = {
        companyId: this.companies[0] && this.companies[0].id,
        trackingNo: 'SF' + Date.now().toString().slice(-10),
        senderAddress: '上海市徐汇区暖爪路 18 号 · 暖爪公益仓',
        receiverAddress: o.receiverAddress,
        traceInfo: '商家已发货，等待揽收'
      }
      this.shipVisible = true
    },
    async ship() {
      if (!this.shipForm.trackingNo) return this.$message.warning('请填写运单号')
      this.shipping = true
      const payload = Object.assign({ orderId: this.current.id }, this.shipForm)
      await postData('/logistics', payload, { ok: true })
      const company = this.companies.find(c => c.id === this.shipForm.companyId)
      this.current.status = 2
      this.current.logistics = Object.assign({}, payload, { companyName: company ? company.name : '物流', status: '已发货' })
      this.shipping = false
      this.shipVisible = false
      this.$message.success('发货信息已提交')
    },
    openLogistics(o) {
      this.logistics = o.logistics || {
        companyName: '顺丰速运', trackingNo: 'SF' + o.orderNo.slice(-10),
        senderAddress: '上海市徐汇区暖爪公益仓', receiverAddress: o.receiverAddress,
        traceInfo: '快件已到达上海徐汇中转场'
      }
      this.logVisible = true
    }
  }
}
</script>

<style scoped>
.stat-icon { background: var(--cream-2); color: var(--ink-2); }
.status-tabs { display: flex; gap: 4px; }
.status-tabs button { border: none; background: transparent; padding: 5px 11px; border-radius: 7px; font-size: 10px; color: var(--muted); cursor: pointer; }
.status-tabs button.active { background: var(--ink); color: #fff; }
.order-no { display: flex; flex-direction: column; }
.order-no strong { font-size: 10px; letter-spacing: .02em; }
.order-no small { font-size: 8.5px; color: var(--muted); margin-top: 2px; }
.item-preview { display: flex; align-items: center; gap: 4px; }
.item-preview img { width: 28px; height: 28px; border-radius: 6px; object-fit: cover; }
.item-preview span { font-size: 9px; color: var(--muted); margin-left: 2px; }
.price { color: var(--primary); font-weight: 600; }
.receiver { display: flex; flex-direction: column; }
.receiver strong { font-size: 10.5px; }
.receiver small { font-size: 8.5px; color: var(--muted); }
.detail-head { display: flex; justify-content: space-between; align-items: center; padding-bottom: 16px; border-bottom: 1px solid var(--line); }
.detail-head h2 { font-size: 18px; margin-top: 6px; }
.d-amount { font-family: var(--font-display); font-size: 26px; color: var(--primary); font-weight: 400; }
.items { margin: 8px 0; }
.item-row { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid var(--line); }
.item-row img { width: 46px; height: 46px; border-radius: 8px; object-fit: cover; }
.item-row div { flex: 1; display: flex; flex-direction: column; }
.item-row strong { font-size: 10.5px; }
.item-row small { font-size: 9px; color: var(--muted); margin-top: 2px; }
.item-row > span { font-size: 10.5px; }
.ship-order { display: flex; justify-content: space-between; align-items: center; background: var(--cream); border-radius: 9px; padding: 10px 14px; margin-bottom: 14px; font-size: 10px; }
.ship-order strong { font-family: var(--font-display); font-size: 18px; color: var(--primary); font-weight: 400; }
.log-box { padding: 4px 0; }
.log-top { display: flex; justify-content: space-between; padding-bottom: 14px; border-bottom: 1px solid var(--line); font-size: 11px; }
.log-top span { color: var(--muted); }
.trace { padding-top: 16px; }
.trace-item { display: flex; gap: 12px; position: relative; padding-bottom: 20px; }
.trace-item::before { content: ''; position: absolute; left: 5px; top: 13px; bottom: 0; width: 1px; background: var(--line); }
.trace-item:last-child::before { display: none; }
.trace-item i { width: 11px; height: 11px; border-radius: 50%; border: 2px solid var(--line-strong); background: var(--paper); margin-top: 2px; z-index: 1; }
.trace-item.done i { border-color: var(--sage); background: var(--sage); }
.trace-item strong { font-size: 10.5px; }
.trace-item p { font-size: 9.5px; color: var(--muted); margin-top: 2px; }
.trace-item small { font-size: 8.5px; color: var(--muted-2); }
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 820px; }
}
</style>
