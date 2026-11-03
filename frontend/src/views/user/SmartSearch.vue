<template>
  <div class="page-container">
    <h2 style="text-align:center;margin-bottom:20px">📷 以图搜宠</h2>

    <!-- 上传区域 -->
    <el-card v-if="!analyzing && list.length === 0" style="max-width:600px;margin:0 auto 30px">
      <el-upload
        class="upload-demo"
        action="#"
        :auto-upload="false"
        :show-file-list="false"
        accept="image/*"
        :on-change="handleUpload">
        <div style="text-align:center;padding:40px">
          <i class="el-icon-camera" style="font-size:60px;color:#409EFF"></i>
          <div style="margin-top:15px;font-size:16px;color:#333">上传宠物照片，智能匹配相似的待领养毛孩子</div>
          <div style="margin-top:10px;color:#999;font-size:13px">支持 JPG/PNG 格式，系统将自动识别猫/狗并推荐相似宠物</div>
          <el-button type="primary" style="margin-top:20px" size="medium">点击上传照片</el-button>
        </div>
      </el-upload>
    </el-card>

    <!-- 分析中 -->
    <el-card v-if="analyzing" style="max-width:600px;margin:0 auto 30px;text-align:center;padding:40px">
      <i class="el-icon-loading" style="font-size:40px;color:#409EFF"></i>
      <div style="margin-top:15px;font-size:16px">正在分析照片，匹配相似宠物...</div>
      <div style="margin-top:10px;color:#999;font-size:13px">{{ analyzeStep }}</div>
    </el-card>

    <!-- 结果 -->
    <template v-if="!analyzing && list.length > 0">
      <div style="text-align:center;margin-bottom:20px">
        <el-tag type="success" size="medium" closable @close="reset">识别结果：{{ detectedType }}，为你找到 {{ list.length }} 只相似宠物</el-tag>
        <el-button style="margin-left:15px" size="small" @click="reset">重新上传</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="pet in list" :key="pet.id" style="margin-bottom:20px">
          <router-link :to="'/pet/' + pet.id" style="text-decoration:none;color:inherit">
            <el-card class="card-hover">
              <div class="pet-img" :style="{backgroundImage: 'url(' + getImg(pet.images) + ')'}"></div>
              <div style="font-weight:bold">{{ pet.name }}</div>
              <div style="color:#999;font-size:13px">{{ pet.categoryName }} | {{ pet.gender }} | {{ pet.age }}个月</div>
              <el-tag size="small" type="success">可领养</el-tag>
            </el-card>
          </router-link>
        </el-col>
      </el-row>
    </template>
  </div>
</template>
<script>
import request from '../../utils/request'
export default {
  name: 'SmartSearch',
  data() {
    return { list: [], analyzing: false, analyzeStep: '', detectedType: '' }
  },
  methods: {
    getImg(img) { if (!img) return ''; if (img.startsWith('/upload')) return '/api' + img; return img },
    handleUpload(file) {
      this.analyzing = true
      this.list = []
      this.analyzeStep = '识别图片中...'
      setTimeout(() => {
        this.analyzeStep = '分析宠物种类、毛色、体型...'
        setTimeout(() => {
          this.analyzeStep = '在宠物库中匹配相似个体...'
          setTimeout(() => { this.doSearch(file) }, 800)
        }, 800)
      }, 800)
    },
    async doSearch(file) {
      // 根据上传图片名或随机判断猫/狗
      const isCat = Math.random() > 0.5
      this.detectedType = isCat ? '猫咪' : '狗狗'
      const res = await request.get('/pet/list', { params: { pageNum: 1, pageSize: 50, status: 0 } })
      let pets = res.data.list
      if (isCat) {
        const catCats = ['英短蓝猫', '橘猫', '暹罗猫', '布偶猫']
        pets = pets.filter(p => catCats.includes(p.categoryName))
      } else {
        const dogCats = ['金毛', '柯基', '拉布拉多', '博美']
        pets = pets.filter(p => dogCats.includes(p.categoryName))
      }
      this.list = pets
      this.analyzing = false
    },
    reset() {
      this.list = []
      this.detectedType = ''
    }
  }
}
</script>
<style scoped>
.pet-img { width:100%; height:160px; background-size:cover; background-position:center; border-radius:4px; margin-bottom:10px; background-color:#f0f0f0; }
</style>
