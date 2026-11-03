import Vue from 'vue'
import VueRouter from 'vue-router'
import UserLayout from '../layouts/UserLayout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: UserLayout,
    children: [
      { path: '', redirect: '/home' },
      { path: 'home', name: 'Home', component: () => import('../views/user/Home.vue') },
      { path: 'pets', name: 'PetList', component: () => import('../views/user/PetList.vue') },
      { path: 'pet/:id', name: 'PetDetail', component: () => import('../views/user/PetDetail.vue') },
      { path: 'stories', name: 'Stories', component: () => import('../views/user/Stories.vue') },
      { path: 'community', name: 'Community', component: () => import('../views/user/Community.vue') },
      { path: 'lost', name: 'LostPet', component: () => import('../views/user/LostPet.vue') },
      { path: 'shop', name: 'Shop', component: () => import('../views/user/Shop.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('../views/user/ProductDetail.vue') },
      { path: 'donate', name: 'Donate', component: () => import('../views/user/Donate.vue') },
      { path: 'chat', name: 'Chat', component: () => import('../views/user/Chat.vue') },
      { path: 'guide', name: 'Guide', component: () => import('../views/user/AdoptGuide.vue') },
      { path: 'orders', name: 'Orders', component: () => import('../views/user/Orders.vue'), meta: { auth: true } },
      { path: 'foster', name: 'Foster', component: () => import('../views/user/Foster.vue'), meta: { auth: true } },
      { path: 'report', name: 'Report', component: () => import('../views/user/Report.vue'), meta: { auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/user/Profile.vue'), meta: { auth: true } },
      { path: 'smart', redirect: '/pets' }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/user/Register.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    meta: { auth: true, admin: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '数据概览' } },
      { path: 'users', name: 'UserManage', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'pets', name: 'PetManage', component: () => import('../views/admin/PetManage.vue'), meta: { title: '动物档案' } },
      { path: 'categories', name: 'CategoryManage', component: () => import('../views/admin/CategoryManage.vue'), meta: { title: '品种管理' } },
      { path: 'rooms', name: 'RoomManage', component: () => import('../views/admin/RoomManage.vue'), meta: { title: '房间管理' } },
      { path: 'adopt', name: 'AdoptManage', component: () => import('../views/admin/AdoptManage.vue'), meta: { title: '领养审核' } },
      { path: 'checkins', name: 'CheckinManage', component: () => import('../views/admin/CheckinManage.vue'), meta: { title: '试养打卡' } },
      { path: 'foster', name: 'FosterManage', component: () => import('../views/admin/FosterManage.vue'), meta: { title: '寄养管理' } },
      { path: 'report', name: 'ReportManage', component: () => import('../views/admin/ReportManage.vue'), meta: { title: '救助上报' } },
      { path: 'products', name: 'ProductManage', component: () => import('../views/admin/ProductManage.vue'), meta: { title: '商品管理' } },
      { path: 'orders', name: 'OrderManage', component: () => import('../views/admin/OrderManage.vue'), meta: { title: '订单物流' } },
      { path: 'companies', name: 'CompanyManage', component: () => import('../views/admin/CompanyManage.vue'), meta: { title: '物流公司' } },
      { path: 'notices', name: 'NoticeManage', component: () => import('../views/admin/NoticeManage.vue'), meta: { title: '公告管理' } },
      { path: 'donations', name: 'DonationManage', component: () => import('../views/admin/DonationManage.vue'), meta: { title: '爱心捐赠' } },
      { path: 'banners', name: 'BannerManage', component: () => import('../views/admin/BannerManage.vue'), meta: { title: '首页轮播' } },
      { path: 'posts', name: 'PostManage', component: () => import('../views/admin/PostManage.vue'), meta: { title: '社区管理' } },
      { path: 'lost', name: 'LostManage', component: () => import('../views/admin/LostManage.vue'), meta: { title: '寻宠管理' } }
    ]
  },
  { path: '*', redirect: '/home' }
]

const router = new VueRouter({
  mode: 'hash',
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { x: 0, y: 0 }
  }
})

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = parseInt(localStorage.getItem('role') || '0', 10)

  if (to.matched.some(r => r.meta.admin)) {
    if (!token) return next({ path: '/login', query: { redirect: to.fullPath } })
    if (role < 1) return next('/home')
    return next()
  }

  if (to.matched.some(r => r.meta.auth) && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router
