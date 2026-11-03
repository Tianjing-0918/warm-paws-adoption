# 暖爪之家 · 基于 Web 的流浪动物救助领养系统

> 以领养代替购买。让每一次相遇，都有归处。

这是一个面向流浪动物救助站的完整前后端项目，包含用户端、管理后台、领养审核、试养打卡、寄养、流浪上报、社区、寻宠寻主、公益商城、订单物流与爱心捐赠等模块。项目同时提供演示数据，后端未启动时前端也能以本地演示数据完整浏览。

![首页](docs/screenshots/home.png)

## 技术栈

- 后端：Java 8+ / Spring Boot 2.7 / MyBatis-Plus / MySQL 8.0
- 前端：Vue 2.7 / Vue Router / Vuex / Element UI / Axios / ECharts
- 鉴权：JWT（用户、普通管理员、超级管理员三种角色）
- 架构：B/S 前后端分离，RESTful API

## 本次改版重点

- 全新视觉系统：暖米色 + 陶土橙 + 深绿，统一字体、卡片、按钮、状态标签与图表风格
- 首页重做：宠物照片拼贴主视觉 + 自动轮播、待领养筛选、救助数据、领养流程、真实故事、公告与捐赠入口
- 领养中心：品种 / 性别 / 年龄筛选、搜索、分页、动物详情、健康档案、领养申请与流程指导
- 用户中心：资料与头像、领养进度、试养打卡、寄养记录、上报记录、订单、社区分享、密码安全
- 社区与寻宠：Markdown 发帖与预览、点赞评论、分类浏览、LBS 定位、走失 / 捡到启事
- 公益商城：商品浏览、购物袋、下单、模拟支付宝沙箱支付、订单状态与物流轨迹
- 爱心捐赠：金额档位、留言墙、资金去向、捐赠记录
- AI 救助助手：后端接入大模型，未配置密钥时自动使用本地救助知识库回复
- 管理后台：数据看板、领养审核、试养打卡、上报处理、房间分配、发货物流、用户角色、公告、轮播、社区、捐赠与商城管理
- 数据库与安全：补充演示数据脚本，配置项改为环境变量优先，避免把数据库密码、JWT 密钥、支付密钥写死在代码中

## 页面预览

| 领养中心 | 宠物社区 |
| --- | --- |
| ![领养中心](docs/screenshots/pets.png) | ![宠物社区](docs/screenshots/community.png) |

| 数据看板 | 动物档案 |
| --- | --- |
| ![数据看板](docs/screenshots/admin-dashboard.png) | ![动物档案](docs/screenshots/admin-pets.png) |

首页轮播与运营数据也可以在后台维护：

![首页轮播管理](docs/screenshots/admin-banners.png)

## 快速运行

### 1. 数据库

在 MySQL 8.0 中依次执行：

```sql
source sql/pet_adoption.sql;   -- 基础表结构与初始数据
source sql/upgrade_v2.sql;    -- v2 新增：捐赠、试养打卡
source sql/demo_data.sql;     -- 可选：更完整的演示数据
```

默认数据库名：`pet_adoption`。如果使用命令行且路径包含中文，建议在项目根目录执行：

```bash
mysql -uroot -p --default-character-set=utf8mb4 < sql/pet_adoption.sql
mysql -uroot -p --default-character-set=utf8mb4 < sql/upgrade_v2.sql
mysql -uroot -p --default-character-set=utf8mb4 < sql/demo_data.sql
```

### 2. 后端

```bash
cd backend
mvn spring-boot:run
```

默认地址：`http://localhost:8080/api`

数据库账号、邮箱、JWT、支付宝与 AI 配置位于 `backend/src/main/resources/application.yml`。推荐通过环境变量覆盖：

| 环境变量 | 说明 |
| --- | --- |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL 账号密码 |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | 发件邮箱与授权码 |
| `JWT_SECRET` | JWT 签名密钥（生产环境务必替换） |
| `AI_API_KEY` | 智谱 GLM API Key；未配置时使用本地兜底回复 |
| `ALIPAY_APP_ID` / `ALIPAY_PRIVATE_KEY` / `ALIPAY_PUBLIC_KEY` | 支付宝沙箱配置 |
| `UPLOAD_PATH` | 图片上传目录，默认 `D:/pet-upload/` |

> 邮箱验证与支付宝沙箱需要在配置好对应账号后才能真实发送 / 支付。毕业设计演示环境下，登录页提供“演示体验”入口，支付会模拟成功，不会产生真实扣款。

### 3. 前端

```bash
cd frontend
npm install
npm run serve
```

默认地址：`http://localhost:3000`

生产构建：

```bash
npm run build
```

## 测试账号

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 超级管理员 | `admin` | `123456` |
| 普通用户 | `test` | `123456` |
| 普通管理员 | `linxiao` | `123456` |
| 演示用户 | `summer` / `zhou` / `momo` 等 | `123456` |

登录页还提供“普通用户 / 超级管理员”一键演示入口，使用浏览器本地演示数据，适合没有启动后端时做页面展示。

## 目录结构

```text
stray-animal-adoption/
├─ backend/                 Spring Boot 后端
│  └─ src/main/java/com/pet/
│     ├─ controller/        用户、动物、领养、寄养、商城、捐赠、打卡等接口
│     ├─ entity/            实体类
│     ├─ mapper/            MyBatis-Plus Mapper
│     └─ config/            JWT 拦截器、跨域、MyBatis-Plus 配置
├─ frontend/                Vue 2 前端
│  ├─ public/images/        本地宠物与商品图片
│  └─ src/
│     ├─ layouts/           用户端整体布局
│     ├─ components/        导航、页脚、宠物卡片等
│     ├─ views/user/        用户端页面
│     ├─ views/admin/       管理后台页面
│     ├─ utils/             请求封装、演示数据、购物袋
│     └─ assets/            设计系统与后台样式
├─ sql/                     基础 SQL、v2 升级脚本、演示数据
├─ upload/                  项目自带图片素材
└─ docs/screenshots/        页面截图
```

## 说明

- 图片默认存放在 `D:/pet-upload/`，与 `application.yml` 的 `UPLOAD_PATH` 对应；项目根目录 `upload/` 也保留了一份素材。
- 前端在接口不可用时会自动回退到本地演示数据，便于答辩演示；启动后端后则以数据库数据为准。
- 新增的捐赠、试养打卡、角色分配、管理员重置密码接口需要重启后端后生效。
