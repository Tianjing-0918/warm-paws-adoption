-- =============================================
-- 流浪动物救助领养系统 数据库脚本
-- MySQL 8.0
-- =============================================

CREATE DATABASE IF NOT EXISTS pet_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE pet_adoption;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(MD5)',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `role` INT DEFAULT 0 COMMENT '角色：0普通用户，1管理员，2超级管理员',
  `status` INT DEFAULT 0 COMMENT '状态：0正常，1禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 宠物品种表
-- ----------------------------
DROP TABLE IF EXISTS `pet_category`;
CREATE TABLE `pet_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '品种名称',
  `type` VARCHAR(20) DEFAULT NULL COMMENT '类别：猫/狗/其他',
  `description` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物品种';

-- ----------------------------
-- 宠物表
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '宠物名字',
  `category_id` BIGINT DEFAULT NULL COMMENT '品种ID',
  `age` INT DEFAULT NULL COMMENT '年龄(月)',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '毛色',
  `weight` DOUBLE DEFAULT NULL COMMENT '体重(kg)',
  `health_status` VARCHAR(200) DEFAULT NULL COMMENT '健康状况',
  `description` TEXT COMMENT '描述',
  `images` VARCHAR(500) DEFAULT NULL COMMENT '图片(逗号分隔)',
  `status` INT DEFAULT 0 COMMENT '0可领养，1已领养，2寄养中，3待处理',
  `room_id` BIGINT DEFAULT NULL COMMENT '房间ID',
  `adopt_user_id` BIGINT DEFAULT NULL COMMENT '领养人ID',
  `adopt_time` DATETIME DEFAULT NULL COMMENT '领养完成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息';

-- ----------------------------
-- 宠物房间表
-- ----------------------------
DROP TABLE IF EXISTS `pet_room`;
CREATE TABLE `pet_room` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
  `type` VARCHAR(20) DEFAULT NULL COMMENT '猫舍/狗舍/隔离区',
  `capacity` INT DEFAULT 1 COMMENT '容量',
  `status` INT DEFAULT 0 COMMENT '0空闲，1已满，2维修中',
  `description` VARCHAR(200) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物房间';

-- ----------------------------
-- 寄养申请表
-- ----------------------------
DROP TABLE IF EXISTS `foster`;
CREATE TABLE `foster` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '申请人',
  `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名字',
  `category_id` BIGINT DEFAULT NULL,
  `pet_gender` VARCHAR(10) DEFAULT NULL,
  `pet_age` INT DEFAULT NULL,
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `description` VARCHAR(500) DEFAULT NULL,
  `status` INT DEFAULT 0 COMMENT '0待处理，1已分配，2已完成，3已拒绝',
  `room_id` BIGINT DEFAULT NULL COMMENT '分配房间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养申请';

-- ----------------------------
-- 流浪宠物上报表
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '上报人',
  `location` VARCHAR(200) NOT NULL COMMENT '发现地点',
  `description` TEXT COMMENT '描述',
  `images` VARCHAR(500) DEFAULT NULL COMMENT '图片(逗号分隔)',
  `contact` VARCHAR(50) DEFAULT NULL COMMENT '联系方式',
  `status` INT DEFAULT 0 COMMENT '0待处理，1已处理，2已拒绝',
  `reply` VARCHAR(500) DEFAULT NULL COMMENT '处理回复',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流浪宠物上报';

-- ----------------------------
-- 领养申请表
-- ----------------------------
DROP TABLE IF EXISTS `adopt_apply`;
CREATE TABLE `adopt_apply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `pet_id` BIGINT NOT NULL,
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '领养理由',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '居住地址',
  `experience` VARCHAR(500) DEFAULT NULL COMMENT '养宠经验',
  `status` INT DEFAULT 0 COMMENT '0待审核，1已通过，2已拒绝',
  `reply` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请';

-- ----------------------------
-- 宠物用品表
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(50) DEFAULT NULL COMMENT '食品/玩具/用品/医疗',
  `price` DECIMAL(10,2) DEFAULT 0,
  `stock` INT DEFAULT 0,
  `image` VARCHAR(255) DEFAULT NULL,
  `description` TEXT,
  `status` INT DEFAULT 0 COMMENT '0上架，1下架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物用品';

-- ----------------------------
-- 订单表
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(10,2) DEFAULT 0,
  `status` INT DEFAULT 0 COMMENT '0待付款，1已付款待发货，2已发货，3已完成，4已取消',
  `receiver_name` VARCHAR(50) DEFAULT NULL,
  `receiver_phone` VARCHAR(20) DEFAULT NULL,
  `receiver_address` VARCHAR(200) DEFAULT NULL,
  `pay_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

-- ----------------------------
-- 订单明细表
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `product_name` VARCHAR(100) DEFAULT NULL,
  `product_image` VARCHAR(255) DEFAULT NULL,
  `price` DECIMAL(10,2) DEFAULT 0,
  `quantity` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- ----------------------------
-- 物流公司表
-- ----------------------------
DROP TABLE IF EXISTS `logistics_company`;
CREATE TABLE `logistics_company` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '公司名称',
  `phone` VARCHAR(20) DEFAULT NULL,
  `description` VARCHAR(200) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流公司';

-- ----------------------------
-- 物流信息表
-- ----------------------------
DROP TABLE IF EXISTS `logistics`;
CREATE TABLE `logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `company_id` BIGINT DEFAULT NULL,
  `tracking_no` VARCHAR(50) DEFAULT NULL COMMENT '运单号',
  `sender_address` VARCHAR(200) DEFAULT NULL,
  `receiver_address` VARCHAR(200) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT '已发货',
  `trace_info` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息';

-- ----------------------------
-- 通知公告表
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT,
  `status` INT DEFAULT 0 COMMENT '0发布，1草稿',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告';

-- ----------------------------
-- AI对话记录表
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `content` TEXT COMMENT '用户问题',
  `reply` TEXT COMMENT 'AI回复',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录';

-- =============================================
-- 初始化测试数据
-- =============================================

-- 超级管理员账号：admin / 123456 (MD5加密)
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@pet.com', '超级管理员', 2, 0),
('test', 'e10adc3949ba59abbe56e057f20f883e', 'test@qq.com', '测试用户', 0, 0);

-- 品种
INSERT INTO `pet_category` (`name`, `type`, `description`) VALUES
('英短蓝猫', '猫', '英国短毛猫，性格温顺'),
('橘猫', '猫', '中华田园猫，活泼可爱'),
('金毛寻回犬', '狗', '大型犬，聪明友善'),
('柯基', '狗', '小型犬，短腿呆萌'),
('中华田园犬', '狗', '本土犬种，忠诚护主');

-- 房间
INSERT INTO `pet_room` (`room_number`, `type`, `capacity`, `status`, `description`) VALUES
('A101', '猫舍', 5, 0, '一号猫舍'),
('A102', '猫舍', 5, 0, '二号猫舍'),
('B101', '狗舍', 3, 0, '一号狗舍'),
('B102', '狗舍', 3, 1, '二号狗舍已满'),
('C101', '隔离区', 2, 0, '新到动物隔离区');

-- 宠物示例
INSERT INTO `pet` (`name`, `category_id`, `age`, `gender`, `color`, `weight`, `health_status`, `description`, `images`, `status`, `adopt_time`) VALUES
('咪咪', 1, 6, '母', '灰色', 3.5, '健康，已疫苗', '温顺粘人，已绝育，喜欢被抚摸', '/media/animals/pet-01-mimi.jpg', 0, NULL),
('橘子', 2, 12, '公', '橘色', 4.2, '健康，已疫苗', '活泼好动，喜欢玩球', '/media/animals/pet-02-juzi.jpg', 0, NULL),
('大黄', 3, 24, '公', '金色', 25.0, '健康，已疫苗', '非常友善，适合有孩子的家庭', '/media/animals/pet-03-dahuang.jpg', 0, NULL),
('短腿', 4, 8, '母', '黄白双色', 8.0, '健康，已疫苗', '呆萌可爱，短腿柯基', '/media/animals/pet-04-duantui.jpg', 1, NOW());

-- 物流公司
INSERT INTO `logistics_company` (`name`, `phone`, `description`) VALUES
('顺丰速运', '95338', '顺丰速运，快速安全'),
('中通快递', '95311', '中通快递，经济实惠'),
('圆通速递', '95554', '圆通速递，全国可达');

-- 用品示例
INSERT INTO `product` (`name`, `category`, `price`, `stock`, `description`, `status`) VALUES
('皇家猫粮2kg', '食品', 129.00, 50, '皇家室内成猫粮，营养均衡', 0),
('狗狗玩具球', '玩具', 19.90, 100, '耐咬橡胶球，适合中大型犬', 0),
('猫砂盆', '用品', 59.00, 30, '开放式猫砂盆，易清理', 0),
('宠物沐浴露', '医疗', 39.00, 80, '抑菌除螨，温和不刺激', 0);

-- 公告
INSERT INTO `notice` (`title`, `content`, `status`) VALUES
('欢迎来到宠物之家', '我们是一个致力于流浪动物救助的公益平台，希望每一个毛孩子都能找到温暖的家。领养代替购买，用爱终止流浪！', 0),
('领养须知', '1. 领养人需年满18周岁\n2. 有稳定的居住环境和经济来源\n3. 同意定期回访\n4. 科学喂养，不离不弃', 0),
('救助热线', '如发现流浪动物需要帮助，请拨打救助热线：400-123-4567，或通过网站上报功能联系我们。', 0);
