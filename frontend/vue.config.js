const path = require('path')
const webpack = require('webpack')

// 图片与文案升级版组件，构建时替换原页面，保持路由与入口不变
const replacements = [
  ['views/user/Home.vue', 'src/views/user/HomeV2.vue'],
  ['views/user/Login.vue', 'src/views/user/LoginV2.vue'],
  ['views/user/Register.vue', 'src/views/user/RegisterV2.vue'],
  ['views/user/Foster.vue', 'src/views/user/FosterV2.vue'],
  ['views/user/AdoptGuide.vue', 'src/views/user/AdoptGuideV2.vue'],
  ['views/user/Report.vue', 'src/views/user/ReportV2.vue'],
  ['views/user/Profile.vue', 'src/views/user/ProfileV2.vue'],
  ['views/user/PetDetail.vue', 'src/views/user/PetDetailV2.vue'],
  ['views/admin/Dashboard.vue', 'src/views/admin/DashboardV2.vue'],
  ['components/PetCard.vue', 'src/components/PetCardV2.vue'],
  ['utils/demo.js', 'src/utils/demoV2.js'],
  ['utils/api.js', 'src/utils/apiV2.js'],
  ['utils/demo', 'src/utils/demoV2.js'],
  ['utils/api', 'src/utils/apiV2.js']
]

module.exports = {
  devServer: {
    port: 3000,
    historyApiFallback: true,
    client: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: process.env.API_TARGET || 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  outputDir: 'dist2',
  configureWebpack: {
    plugins: [
      new webpack.NormalModuleReplacementPlugin(/.*/, resource => {
        const request = String(resource.request || '').replace(/\\/g, '/')
        const hit = replacements.find(([from]) => request === from || request.endsWith('/' + from))
        if (hit) resource.request = path.resolve(__dirname, hit[1])
      })
    ]
  }
}
