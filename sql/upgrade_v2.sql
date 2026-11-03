-- ============================================================
-- 暖爪之家 v2 升级脚本
-- 新增：爱心捐赠、试养打卡
-- 可重复执行
-- ============================================================
USE pet_adoption;

CREATE TABLE IF NOT EXISTS `donation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `donor_name` VARCHAR(50) DEFAULT NULL,
  `amount` DECIMAL(10,2) NOT NULL DEFAULT 0,
  `message` VARCHAR(200) DEFAULT NULL,
  `anonymous` INT DEFAULT 0 COMMENT '1匿名',
  `monthly` INT DEFAULT 0 COMMENT '1每月捐赠',
  `status` INT DEFAULT 1 COMMENT '1已支付',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_donation_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爱心捐赠';

CREATE TABLE IF NOT EXISTS `trial_checkin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `adoption_id` BIGINT DEFAULT NULL,
  `pet_id` BIGINT DEFAULT NULL,
  `pet_name` VARCHAR(50) DEFAULT NULL,
  `content` VARCHAR(500) DEFAULT NULL,
  `images` VARCHAR(500) DEFAULT NULL,
  `checkin_date` DATE DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_checkin_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试养打卡';

CREATE TABLE IF NOT EXISTS `banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `subtitle` VARCHAR(200) DEFAULT NULL,
  `image` VARCHAR(255) NOT NULL,
  `link` VARCHAR(255) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  `status` INT DEFAULT 0 COMMENT '0启用，1禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图';

INSERT INTO banner (`title`, `subtitle`, `image`, `link`, `sort_order`, `status`)
SELECT '三只小暹罗', '5 个月 · 等待一个家', '/upload/pet5.jpg', '/pet/5', 1, 0
WHERE NOT EXISTS (SELECT 1 FROM banner WHERE title = '三只小暹罗' AND deleted = 0);

INSERT INTO banner (`title`, `subtitle`, `image`, `link`, `sort_order`, `status`)
SELECT '短腿', '1 岁 8 个月 · 亲人爱散步', '/upload/pet4.jpg', '/pet/4', 2, 0
WHERE NOT EXISTS (SELECT 1 FROM banner WHERE title = '短腿' AND deleted = 0);

INSERT INTO banner (`title`, `subtitle`, `image`, `link`, `sort_order`, `status`)
SELECT '雪球', '1 岁 6 个月 · 温柔的长毛', '/upload/pet6.jpg', '/pet/6', 3, 0
WHERE NOT EXISTS (SELECT 1 FROM banner WHERE title = '雪球' AND deleted = 0);

INSERT INTO donation (`user_id`, `donor_name`, `amount`, `message`, `anonymous`, `monthly`, `status`, `create_time`)
SELECT 2, '橘子汽水', 50.00, '给咪咪加个罐头', 0, 0, 1, NOW() - INTERVAL 1 DAY
WHERE NOT EXISTS (SELECT 1 FROM donation WHERE donor_name = '橘子汽水' AND amount = 50.00 AND deleted = 0);

INSERT INTO donation (`user_id`, `donor_name`, `amount`, `message`, `anonymous`, `monthly`, `status`, `create_time`)
SELECT NULL, '爱心人士', 200.00, '愿每个小生命都被温柔以待', 1, 0, 1, NOW() - INTERVAL 3 HOUR
WHERE NOT EXISTS (SELECT 1 FROM donation WHERE donor_name = '爱心人士' AND amount = 200.00 AND deleted = 0);

INSERT INTO donation (`user_id`, `donor_name`, `amount`, `message`, `anonymous`, `monthly`, `status`, `create_time`)
SELECT 4, '夏天的风', 100.00, '谢谢你们一直在坚持', 0, 1, 1, NOW() - INTERVAL 2 DAY
WHERE NOT EXISTS (SELECT 1 FROM donation WHERE donor_name = '夏天的风' AND amount = 100.00 AND deleted = 0);

INSERT INTO trial_checkin (`user_id`, `pet_id`, `pet_name`, `content`, `images`, `checkin_date`, `create_time`)
SELECT 2, 1, '咪咪', '今天第一次主动跳上沙发，胃口很好，便便正常。', '/upload/pet1.jpg', CURDATE() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY
WHERE NOT EXISTS (SELECT 1 FROM trial_checkin WHERE user_id = 2 AND pet_name = '咪咪' AND checkin_date = CURDATE() - INTERVAL 2 DAY AND deleted = 0);

INSERT INTO trial_checkin (`user_id`, `pet_id`, `pet_name`, `content`, `images`, `checkin_date`, `create_time`)
SELECT 2, 1, '咪咪', '晚上会自己回窝睡觉了，和我之间的距离近了很多。', '', CURDATE() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY
WHERE NOT EXISTS (SELECT 1 FROM trial_checkin WHERE user_id = 2 AND pet_name = '咪咪' AND checkin_date = CURDATE() - INTERVAL 4 DAY AND deleted = 0);
