// 演示数据：后端未启动或接口异常时，页面仍然可以完整浏览
const now = Date.now()
const daysAgo = n => new Date(now - n * 86400000).toISOString().slice(0, 19).replace('T', ' ')

export const demoCategories = [
  { id: 1, name: '英国短毛猫', type: '猫', description: '圆脸温顺，安静亲人' },
  { id: 2, name: '中华田园猫', type: '猫', description: '体质好，活泼聪明' },
  { id: 3, name: '金毛寻回犬', type: '狗', description: '友善温和，适合家庭' },
  { id: 4, name: '柯基', type: '狗', description: '短腿呆萌，精力充沛' },
  { id: 5, name: '中华田园犬', type: '狗', description: '忠诚聪明，适应力强' },
  { id: 6, name: '暹罗猫', type: '猫', description: '重点色，爱说话' },
  { id: 7, name: '布偶猫', type: '猫', description: '长毛蓝眼，温柔黏人' },
  { id: 8, name: '拉布拉多', type: '狗', description: '稳重亲人，导盲犬常用品种' },
  { id: 9, name: '博美', type: '狗', description: '小巧机灵，毛发蓬松' }
]

export const demoPets = [
  {
    id: 1, name: '咪咪', categoryId: 1, categoryName: '英国短毛猫', age: 8, gender: '母',
    color: '蓝灰色', weight: 3.6, healthStatus: '健康，已驱虫，已绝育',
    description: '咪咪是在小区车库被发现的，刚来时只有巴掌大。她性格安静，喜欢趴在窗边晒太阳，也会在熟悉的人脚边轻轻蹭一蹭。希望能遇到愿意慢慢走进她内心的家人。',
    images: '/images/pet1.jpg', status: 0, location: '上海市 · 徐汇区',
    traits: ['亲人', '安静', '已绝育'], vaccinated: true, sterilized: true, createTime: daysAgo(2)
  },
  {
    id: 2, name: '橘子', categoryId: 2, categoryName: '中华田园猫', age: 14, gender: '公',
    color: '橘色虎斑', weight: 4.4, healthStatus: '健康，已疫苗',
    description: '一只自带治愈感的橘猫。橘子话不多，但特别贪吃，听到开罐头的声音会立刻出现。喜欢被摸下巴，和其他猫也能和平相处。',
    images: '/images/pet2.jpg', status: 0, location: '上海市 · 静安区',
    traits: ['贪吃', '温和', '已疫苗'], vaccinated: true, sterilized: true, createTime: daysAgo(4)
  },
  {
    id: 3, name: '小满', categoryId: 3, categoryName: '金毛寻回犬', age: 10, gender: '公',
    color: '浅金色', weight: 12.5, healthStatus: '健康，疫苗齐全',
    description: '小满是救助站里最热情的小朋友，见人就摇尾巴。正在学习坐下和等待，适合有时间陪伴、愿意带他运动的家庭。',
    images: '/images/pet3.jpg', status: 0, location: '上海市 · 闵行区',
    traits: ['活泼', '亲人', '适合家庭'], vaccinated: true, sterilized: false, createTime: daysAgo(6)
  },
  {
    id: 4, name: '短腿', categoryId: 4, categoryName: '柯基', age: 20, gender: '母',
    color: '黄白双色', weight: 8.2, healthStatus: '健康，已绝育',
    description: '短腿是原主人搬家后留下的孩子。她喜欢散步，也很会看眼色，熟悉的志愿者都说她像一只小狐狸。',
    images: '/images/pet4.jpg', status: 0, location: '上海市 · 长宁区',
    traits: ['聪明', '爱散步', '已绝育'], vaccinated: true, sterilized: true, createTime: daysAgo(8)
  },
  {
    id: 5, name: '三只小暹罗', categoryId: 6, categoryName: '暹罗猫', age: 5, gender: '公/母',
    color: '重点色', weight: 2.1, healthStatus: '健康，已完成第一针',
    description: '同窝的三只小暹罗，从纸箱里被救出来时还没有睁眼。现在个个都是好奇宝宝，可以单独领养，也欢迎成对带走。',
    images: '/images/pet5.jpg', status: 0, location: '上海市 · 浦东新区',
    traits: ['幼猫', '好奇', '同窝'], vaccinated: true, sterilized: false, createTime: daysAgo(3)
  },
  {
    id: 6, name: '雪球', categoryId: 7, categoryName: '布偶猫', age: 18, gender: '母',
    color: '海豹双色', weight: 4.8, healthStatus: '健康，已驱虫',
    description: '雪球有一双很蓝的眼睛，性格像一团软棉花。她需要定期梳毛，希望你愿意为这份漂亮的长毛多花一点时间。',
    images: '/images/pet6.jpg', status: 0, location: '上海市 · 杨浦区',
    traits: ['黏人', '长毛', '安静'], vaccinated: true, sterilized: true, createTime: daysAgo(11)
  },
  {
    id: 7, name: '阿墨', categoryId: 8, categoryName: '拉布拉多', age: 26, gender: '公',
    color: '黑色', weight: 27.0, healthStatus: '健康，已疫苗',
    description: '阿墨曾在工地附近流浪了半年，见到人还是会先观察一下。熟悉之后非常温和，适合有养犬经验、能给他安全感的家庭。',
    images: '/images/pet7.jpg', status: 0, location: '上海市 · 宝山区',
    traits: ['稳重', '需要耐心', '大型犬'], vaccinated: true, sterilized: true, createTime: daysAgo(13)
  },
  {
    id: 8, name: '团子', categoryId: 9, categoryName: '博美', age: 12, gender: '母',
    color: '奶白色', weight: 2.8, healthStatus: '健康，已绝育',
    description: '团子是只很有镜头感的小狗，喜欢被人抱，也喜欢和小朋友一起玩。她已经学会定点如厕，会是个省心的小室友。',
    images: '/images/pet8.jpg', status: 1, location: '上海市 · 普陀区',
    traits: ['小巧', '会定点', '已绝育'], vaccinated: true, sterilized: true, createTime: daysAgo(18)
  },
  {
    id: 9, name: '花卷', categoryId: 2, categoryName: '中华田园猫', age: 9, gender: '母',
    color: '三花', weight: 3.2, healthStatus: '健康，已疫苗',
    description: '花卷在雨天躲进了便利店，被店员收留后交给我们。她有点慢热，但只要你愿意等她，她会用呼噜声回应你。',
    images: '/images/lost1.jpg', status: 0, location: '上海市 · 虹口区',
    traits: ['慢热', '安静', '已疫苗'], vaccinated: true, sterilized: true, createTime: daysAgo(7)
  },
  {
    id: 10, name: '豆豆', categoryId: 5, categoryName: '中华田园犬', age: 16, gender: '公',
    color: '灰棕色', weight: 9.6, healthStatus: '健康，已驱虫',
    description: '豆豆是被志愿者在公园门口发现的，毛打结得很厉害。剪完毛之后才发现，他是一个眼睛特别亮的小伙子。',
    images: '/images/lost2.jpg', status: 0, location: '上海市 · 松江区',
    traits: ['忠诚', '爱散步', '已驱虫'], vaccinated: true, sterilized: false, createTime: daysAgo(10)
  }
]

export const demoNotices = [
  { id: 1, title: '「暖冬计划」流浪动物过冬物资募集开始啦', content: '天气渐凉，救助站正在为园区里的毛孩子们准备过冬物资，包括保暖垫、猫砂、犬粮和驱虫药。欢迎通过爱心捐赠页面参与，所有物资明细会在每月 1 日公示。', createTime: daysAgo(1) },
  { id: 2, title: '10 月领养家庭回访安排', content: '本月回访将在 10 月 26 日进行，志愿者会提前与领养家庭电话联系。回访内容主要包括宠物健康、饮食和适应情况，感谢大家的配合。', createTime: daysAgo(3) },
  { id: 3, title: '领养须知：请给彼此一个适应期', content: '刚到新家的宠物可能会出现躲藏、拒食或夜间叫唤的情况，这是正常的应激反应。建议准备一个安静的小空间，不要强行抱抱，给它 3–7 天时间熟悉气味和环境。', createTime: daysAgo(5) },
  { id: 4, title: '本周六 · 线下领养日志愿者招募', content: '本周六 13:00–17:00 将在徐汇区滨江绿地举办线下领养日活动，需要 8 名志愿者协助布置场地、照顾动物和引导来访家庭。报名请联系客服微信。', createTime: daysAgo(8) },
  { id: 5, title: '关于警惕冒用救助站名义募捐的声明', content: '近期发现有个人账号冒用本站名义进行募捐。本站所有捐赠均通过官网捐赠通道，不会通过私人微信收款，请大家注意甄别。', createTime: daysAgo(12) }
]

export const demoBanners = [
  { id: 1, title: '三只小暹罗', subtitle: '5 个月 · 等待一个家', image: '/images/pet5.jpg', link: '/pet/5', sortOrder: 1, status: 0 },
  { id: 2, title: '短腿', subtitle: '1 岁 8 个月 · 亲人爱散步', image: '/images/pet4.jpg', link: '/pet/4', sortOrder: 2, status: 0 },
  { id: 3, title: '雪球', subtitle: '1 岁 6 个月 · 温柔的长毛', image: '/images/pet6.jpg', link: '/pet/6', sortOrder: 3, status: 0 }
]

export const demoStories = [
  {
    id: 1, title: '从害怕下雨，到拥有自己的小沙发', petName: '豆豆', author: '林女士',
    images: '/images/story2.jpg', category: '领养故事', likes: 286,
    content: '第一次见到豆豆时，他缩在笼子的最里面，听到雷声就会发抖。我们花了三个月，他才愿意主动跳上沙发。现在每到下雨天，他会自己叼着毯子来找我。原来被需要，是这样一件温暖的事。'
  },
  {
    id: 2, title: '一只橘猫，把我们变成了更好的人', petName: '橘子', author: '阿哲',
    images: '/images/story3.jpg', category: '领养故事', likes: 198,
    content: '橘子来家里的第一周，把书架上的东西全推了下来。我们一度怀疑自己是不是做错了决定。后来学会把易碎品收好，也学会在他捣乱时先蹲下来看看他想表达什么。现在他是家里最会撒娇的那一个。'
  },
  {
    id: 3, title: '雪球的第一百个晚安', petName: '雪球', author: '小满',
    images: '/images/pet6.jpg', category: '日常记录', likes: 154,
    content: '领养雪球第 100 天。她终于不再睡在床底，而是每天准时占据枕头的一角。谢谢她愿意相信我们。'
  },
  {
    id: 4, title: '给老年犬一个安稳的晚年', petName: '阿墨', author: '周先生',
    images: '/images/pet7.jpg', category: '领养故事', likes: 321,
    content: '很多人领养时会更偏向幼犬，但我们想给一只大龄狗狗一个家。阿墨走路慢慢的，吃饭也慢慢的，可他每天都会在门口等我们下班。'
  }
]

export const demoPosts = [
  {
    id: 1, title: '第一次带救助猫回家，需要准备什么？',
    content: '**新手求助！** 昨天刚从救助站接回一只 8 个月大的狸花猫，目前一直躲在床底。\n\n目前已准备：\n- 猫砂盆、猫砂\n- 幼猫粮和饮水机\n- 一个安静的次卧\n\n请问大家，前几天需要强行把它抱出来吗？',
    images: '/images/pet1.jpg', username: '橘子汽水', likeCount: 42, category: '新手求助', createTime: daysAgo(0.2),
    comments: [
      { id: 1, username: '有猫的人', content: '不要强行抱，先把食物和水放在附近，人离开房间，让它自己探索。一般 2–3 天就会出来。', createTime: daysAgo(0.1) },
      { id: 2, username: '志愿者小林', content: '可以在床边放一件有你气味的旧衣服，会更有安全感。', createTime: daysAgo(0.05) }
    ]
  },
  {
    id: 2, title: '周末志愿日记：给 12 只狗狗洗澡',
    content: '今天和 5 位志愿者一起给园区里的狗狗洗澡、剪指甲。最怕水的大黄全程把脑袋埋在我怀里，洗完却第一个在草地上打滚。\n\n累是真的累，但看到它们干干净净地晒太阳，觉得一切都值得。',
    images: '/images/pet3.jpg', username: '小林', likeCount: 96, category: '志愿日记', createTime: daysAgo(1),
    comments: [{ id: 3, username: '豆包', content: '辛苦啦！下次活动我也想参加。', createTime: daysAgo(0.8) }]
  },
  {
    id: 3, title: '领养后第 30 天，他终于会在我脚边睡觉了',
    content: '刚到家时他连饭都要等我走开才吃。现在每天我坐在书桌前，他就会蜷在脚边。**信任真的需要时间，但值得。**',
    images: '/images/pet5.jpg', username: '夏天的风', likeCount: 134, category: '领养故事', createTime: daysAgo(2),
    comments: []
  },
  {
    id: 4, title: '自制猫饭真的比猫粮更好吗？',
    content: '最近看了很多自制猫饭的教程，有点心动。想请教大家，长期自制需要注意哪些营养配比？有没有靠谱的参考？',
    images: '', username: '团子爸', likeCount: 27, category: '养宠经验', createTime: daysAgo(3),
    comments: [{ id: 4, username: '兽医阿周', content: '不建议完全照搬网络配方，最好先做体检，并在兽医或宠物营养师指导下补充牛磺酸、钙磷等。', createTime: daysAgo(2.5) }]
  }
]

export const demoLostPets = [
  {
    id: 1, title: '徐汇区捡到一只蓝眼奶牛猫', type: '捡到', petType: '猫', color: '黑白',
    location: '上海市徐汇区 · 田林路', contact: '微信：warmpaw_01', description: '很亲人，戴着红色项圈，会使用猫砂。暂时安置在家中，希望主人尽快联系。',
    images: '/images/lost1.jpg', status: 0, createTime: daysAgo(0.5)
  },
  {
    id: 2, title: '寻找走失的巧克力色泰迪', type: '走失', petType: '狗', color: '巧克力色',
    location: '上海市静安区 · 大宁公园附近', contact: '138****6621', description: '名字叫可乐，3 岁，公犬，左耳有一小块浅色毛。走失时没有穿衣服，性格胆小，看到人会躲。',
    images: '/images/lost2.jpg', status: 0, createTime: daysAgo(1.4)
  },
  {
    id: 3, title: '地铁站附近捡到橘猫，正在找主人', type: '捡到', petType: '猫', color: '橘色',
    location: '上海市浦东新区 · 世纪大道', contact: '微信：orange_cat_2026', description: '在地铁 2 号口附近徘徊了两天，很干净，应该是家养猫。已带去宠物医院检查，没有芯片。',
    images: '/images/lost3.jpg', status: 0, createTime: daysAgo(2.2)
  },
  {
    id: 4, title: '寻主：小区门口捡到一只博美', type: '捡到', petType: '狗', color: '奶白色',
    location: '上海市普陀区 · 真如', contact: '021-6******8', description: '非常亲人，会握手，疑似刚走失不久。请主人描述项圈颜色和特征。',
    images: '/images/pet8.jpg', status: 0, createTime: daysAgo(3.5)
  }
]

export const demoProducts = [
  { id: 1, name: '皇家室内成猫粮 2kg', category: '食品', price: 129, stock: 50, image: '/images/prod1.jpg', description: '适合室内成猫，营养均衡，帮助控制毛球。每售出一袋，将向救助站捐赠 5 元。', sales: 326 },
  { id: 2, name: '耐咬互动玩具球', category: '玩具', price: 19.9, stock: 120, image: '/images/prod2.jpg', description: '天然橡胶材质，耐咬耐玩，适合中大型犬互动。', sales: 518 },
  { id: 3, name: '开放式猫砂盆 · 奶油蓝', category: '用品', price: 59, stock: 32, image: '/images/prod3.jpg', description: '大空间开放式设计，易清理，附赠猫砂铲。', sales: 204 },
  { id: 4, name: '温和抑菌宠物沐浴露', category: '清洁', price: 39, stock: 86, image: '/images/prod4.jpg', description: '低敏配方，温和不刺激，适合犬猫日常清洁。', sales: 167 },
  { id: 5, name: '天然豆腐猫砂 6L', category: '用品', price: 29.9, stock: 200, image: '/images/prod5.jpg', description: '快速结团，可冲厕所，淡淡奶香。', sales: 642 },
  { id: 6, name: '四季可拆洗宠物窝', category: '用品', price: 159, stock: 18, image: '/images/prod6.jpg', description: '加厚软垫，可拆洗，适合中小型犬猫。', sales: 88 },
  { id: 7, name: '透气防挣脱胸背带', category: '出行', price: 69, stock: 44, image: '/images/prod7.jpg', description: '轻量透气网布，反光条设计，夜间出行更安全。', sales: 231 },
  { id: 8, name: '低温烘焙鸡肉粒 200g', category: '食品', price: 32.9, stock: 75, image: '/images/prod8.jpg', description: '单一鸡肉蛋白，低温烘焙，训练奖励好帮手。', sales: 389 }
]

demoProducts.forEach(p => { if (p.status === undefined) p.status = 0 })

export const demoAdoptions = [
  { id: 1, petId: 1, petName: '咪咪', petImage: '/images/pet1.jpg', status: 1, reason: '家里有稳定住所，已养猫五年，希望给咪咪一个安静的家。', address: '上海市徐汇区', experience: '养过两只猫，了解常见疾病护理。', reply: '资料审核通过，已安排本周六家访。', createTime: daysAgo(6) },
  { id: 2, petId: 3, petName: '小满', petImage: '/images/pet3.jpg', status: 0, reason: '有院子，每天有充足时间陪伴和遛狗。', address: '上海市闵行区', experience: '第一次养狗，已学习训犬课程。', reply: '', createTime: daysAgo(1.2) }
]

export const demoFosters = [
  { id: 1, petName: '奶糖', categoryName: '英国短毛猫', petGender: '母', petAge: 8, startDate: '2026-10-24', endDate: '2026-10-31', status: 1, roomNumber: 'A102', description: '国庆出行，希望帮忙照顾一周。', createTime: daysAgo(4) }
]

export const demoReports = [
  { id: 1, location: '上海市长宁区 · 中山公园 3 号门', description: '一只黄色小型犬在门口徘徊，脖子上有蓝色项圈，看起来有些脱水。已留下饮用水。', images: '/images/lost2.jpg', contact: '138****2210', status: 1, reply: '志愿者已到现场，狗狗已带回救助站检查，感谢您的上报。', createTime: daysAgo(2) },
  { id: 2, location: '上海市杨浦区 · 五角场地铁站', description: '地铁口纸箱里有三只小猫，大约一个月大，天气转凉，希望尽快有人接手。', images: '/images/pet5.jpg', contact: '微信：cat_rescue', status: 0, reply: '', createTime: daysAgo(0.6) }
]

export const demoOrders = [
  {
    id: 1, orderNo: 'PET202610180001', totalAmount: 129.00, status: 1, username: '橘子汽水', receiverName: '田同学',
    receiverPhone: '138****2210', receiverAddress: '上海市徐汇区某路 88 号', createTime: daysAgo(1),
    items: [
      { productId: 1, productName: '皇家室内成猫粮 2kg', productImage: '/images/prod1.jpg', price: 129, quantity: 1 }
    ]
  },
  {
    id: 2, orderNo: 'PET202610120002', totalAmount: 98.80, status: 2, username: '橘子汽水', receiverName: '田同学',
    receiverPhone: '138****2210', receiverAddress: '上海市徐汇区某路 88 号', createTime: daysAgo(8),
    items: [
      { productId: 5, productName: '天然豆腐猫砂 6L', productImage: '/images/prod5.jpg', price: 29.9, quantity: 2 },
      { productId: 4, productName: '温和抑菌宠物沐浴露', productImage: '/images/prod4.jpg', price: 39, quantity: 1 }
    ],
    logistics: { companyName: '顺丰速运', trackingNo: 'SF1234567890', status: '运输中', traceInfo: '快件已到达上海徐汇中转场' }
  }
]

export const demoUser = {
  id: 2, username: 'test', nickname: '橘子汽水', email: 'test@qq.com',
  phone: '138****2210', role: 0, status: 0, avatar: '',
  createTime: daysAgo(120)
}

export const demoAdmin = {
  id: 1, username: 'admin', nickname: '超级管理员', email: 'admin@pet.com',
  phone: '', role: 2, status: 0, avatar: '', createTime: daysAgo(365)
}

export const demoStats = {
  petTotal: 128, petAvailable: 46, petAdopted: 72, petFoster: 7, petPending: 3,
  userTotal: 1286, adoptPending: 12, fosterPending: 5, reportPending: 8,
  rescued: 386, donated: 58620, volunteers: 268,
  notices: demoNotices, latestPets: demoPets.filter(p => p.status === 0).slice(0, 8),
  categoryStats: demoCategories.slice(0, 9).map((c, i) => ({ name: c.name, count: [18, 24, 12, 9, 16, 8, 6, 7, 5][i] })),
  trend: [
    { date: '10/14', adopt: 3, report: 5 }, { date: '10/15', adopt: 5, report: 4 },
    { date: '10/16', adopt: 4, report: 7 }, { date: '10/17', adopt: 8, report: 3 },
    { date: '10/18', adopt: 6, report: 6 }, { date: '10/19', adopt: 9, report: 5 },
    { date: '10/20', adopt: 7, report: 4 }
  ]
}

export const demoUsers = [
  demoUser,
  { id: 3, username: 'summer', nickname: '夏天的风', email: 'summer@example.com', phone: '139****8891', role: 0, status: 0, createTime: daysAgo(66) },
  { id: 4, username: 'linxiao', nickname: '志愿者小林', email: 'lin@example.com', phone: '137****1023', role: 1, status: 0, createTime: daysAgo(210) },
  { id: 5, username: 'orange', nickname: '橘子汽水', email: 'orange@example.com', phone: '', role: 0, status: 1, createTime: daysAgo(38) },
  { id: 6, username: 'zhou', nickname: '周先生', email: 'zhou@example.com', phone: '136****7712', role: 0, status: 0, createTime: daysAgo(95) }
]

export const demoAdminAdoptions = [
  { id: 1, username: '夏天的风', petName: '咪咪', petImage: '/images/pet1.jpg', status: 0, reason: '已工作五年，独居有稳定收入，租住房屋允许养宠。', address: '上海市徐汇区', experience: '养过一只猫，了解基础护理。', createTime: daysAgo(0.4) },
  { id: 2, username: '周先生', petName: '阿墨', petImage: '/images/pet7.jpg', status: 0, reason: '家里有院子，希望给大型犬一个安稳的家。', address: '上海市松江区', experience: '养过两只拉布拉多。', createTime: daysAgo(1.1) },
  { id: 3, username: '橘子汽水', petName: '雪球', petImage: '/images/pet6.jpg', status: 1, reason: '喜欢布偶猫，家中无其他宠物。', address: '上海市杨浦区', experience: '有三年养猫经验。', reply: '已通过审核，待安排接猫。', createTime: daysAgo(4) },
  { id: 4, username: '豆包', petName: '小满', petImage: '/images/pet3.jpg', status: 2, reason: '希望领养一只金毛陪伴孩子。', address: '上海市浦东新区', experience: '无养犬经验，家中孩子 4 岁。', reply: '建议先积累养犬经验，欢迎参加线下领养活动。', createTime: daysAgo(5) }
]

export const demoAdminReports = [
  { id: 1, username: '夏天的风', location: '长宁区中山公园 3 号门', description: '黄色小型犬，蓝色项圈，疑似脱水。', images: '/images/lost2.jpg', contact: '138****2210', status: 0, createTime: daysAgo(0.3) },
  { id: 2, username: '橘子汽水', location: '杨浦区五角场地铁站', description: '纸箱内三只幼猫，约一个月大。', images: '/images/pet5.jpg', contact: '微信 cat_rescue', status: 0, createTime: daysAgo(0.8) },
  { id: 3, username: '阿哲', location: '徐汇区田林路', description: '一只受伤的橘猫，后腿有擦伤。', images: '/images/lost3.jpg', contact: '139****0091', status: 1, reply: '已送医处理伤口，目前在隔离区观察。', createTime: daysAgo(3) }
]

export const demoRooms = [
  { id: 1, roomNumber: 'A101', type: '猫舍', capacity: 5, status: 1, description: '一号猫舍 · 朝阳' },
  { id: 2, roomNumber: 'A102', type: '猫舍', capacity: 5, status: 0, description: '二号猫舍 · 安静区' },
  { id: 3, roomNumber: 'B101', type: '狗舍', capacity: 3, status: 0, description: '一号狗舍 · 带活动场' },
  { id: 4, roomNumber: 'B102', type: '狗舍', capacity: 3, status: 0, description: '二号狗舍' },
  { id: 5, roomNumber: 'C101', type: '隔离区', capacity: 2, status: 2, description: '新到动物隔离观察' }
]

export const demoCompanies = [
  { id: 1, name: '顺丰速运', phone: '95338', description: '顺丰速运，快速安全' },
  { id: 2, name: '中通快递', phone: '95311', description: '中通快递，经济实惠' },
  { id: 3, name: '圆通速递', phone: '95554', description: '圆通速递，全国可达' }
]

export const demoAdminOrders = demoOrders.concat([
  {
    id: 3, orderNo: 'PET202610190003', totalAmount: 219, status: 0, username: '夏天的风',
    receiverName: '夏女士', receiverPhone: '139****8891', receiverAddress: '上海市静安区某路 21 号',
    createTime: daysAgo(0.7),
    items: [{ productId: 6, productName: '四季可拆洗宠物窝', productImage: '/images/prod6.jpg', price: 159, quantity: 1 }]
  }
])

export const demoCheckins = [
  { id: 1, date: '2026-10-18', petName: '咪咪', content: '今天第一次主动跳上沙发，胃口很好，便便正常。', images: '/images/pet1.jpg' },
  { id: 2, date: '2026-10-16', petName: '咪咪', content: '晚上会自己回窝睡觉了，和我之间的距离近了很多。', images: '' }
]

export const demoDonations = [
  { id: 1, donor: '爱心人士', amount: 200, message: '愿每个小生命都被温柔以待', createTime: daysAgo(0.2), anonymous: true },
  { id: 2, donor: '林女士', amount: 50, message: '给咪咪加个罐头', createTime: daysAgo(1.1), anonymous: false },
  { id: 3, donor: '夏天的风', amount: 100, message: '谢谢你们一直在坚持', createTime: daysAgo(2.4), anonymous: false },
  { id: 4, donor: '爱心人士', amount: 20, message: '微薄之力', createTime: daysAgo(3.2), anonymous: true }
]

export function getPetType(pet) {
  if (!pet) return '其他'
  if (pet.categoryName && /犬|狗|柯基|金毛|拉布拉多|博美/.test(pet.categoryName)) return '狗'
  if (pet.categoryName && /猫|英短|暹罗|布偶/.test(pet.categoryName)) return '猫'
  return '其他'
}

/** 合并接口字段与演示展示字段，补齐地点、性格标签等非数据库字段 */
export function enrichPet(pet) {
  if (!pet) return pet
  const base = demoPets.find(p => p.id === pet.id) || {}
  const merged = Object.assign({}, base, pet)
  merged.categoryName = pet.categoryName || base.categoryName || ''
  merged.location = pet.location || base.location || '上海市 · 暖爪救助站'
  merged.traits = pet.traits || base.traits || []
  merged.vaccinated = pet.vaccinated !== undefined ? pet.vaccinated : base.vaccinated
  merged.sterilized = pet.sterilized !== undefined ? pet.sterilized : base.sterilized
  if (!merged.images) merged.images = base.images || '/images/pet1.jpg'
  return merged
}

export function ageText(months) {
  if (months === null || months === undefined || months === '') return '年龄未知'
  const m = Number(months)
  if (m < 12) return `${m} 个月`
  const y = Math.floor(m / 12)
  const rest = m % 12
  return rest ? `${y} 岁 ${rest} 个月` : `${y} 岁`
}

export function statusText(status) {
  return ['可领养', '已领养', '寄养中', '待处理'][status] || '待确认'
}

export function money(value) {
  const n = Number(value || 0)
  return `¥${n.toFixed(2)}`
}
