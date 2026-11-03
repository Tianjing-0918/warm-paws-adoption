-- ============================================================
-- 暖爪之家 · 演示数据（可重复执行，不删除已有数据）
-- 使用前请确认已导入 pet_adoption.sql
-- ============================================================
USE pet_adoption;

-- 补全品种分类
UPDATE pet_category SET type = '猫' WHERE name IN ('暹罗猫', '布偶猫');
UPDATE pet_category SET type = '狗' WHERE name IN ('拉布拉多', '博美');

-- 更多注册用户（密码均为 123456）
INSERT IGNORE INTO `user`
(`username`, `password`, `email`, `nickname`, `phone`, `role`, `status`, `create_time`) VALUES
('summer',  'e10adc3949ba59abbe56e057f20f883e', 'summer@example.com',  '夏天的风',   '139****8891', 0, 0, NOW() - INTERVAL 66 DAY),
('linxiao', 'e10adc3949ba59abbe56e057f20f883e', 'lin@example.com',     '志愿者小林', '137****1023', 1, 0, NOW() - INTERVAL 210 DAY),
('zhou',    'e10adc3949ba59abbe56e057f20f883e', 'zhou@example.com',    '周先生',     '136****7712', 0, 0, NOW() - INTERVAL 95 DAY),
('doubao',  'e10adc3949ba59abbe56e057f20f883e', 'doubao@example.com',  '豆包',       '135****6620', 0, 0, NOW() - INTERVAL 52 DAY),
('orange',  'e10adc3949ba59abbe56e057f20f883e', 'orange@example.com',  '橘子汽水',   '',            0, 1, NOW() - INTERVAL 38 DAY),
('xiaoyu',  'e10adc3949ba59abbe56e057f20f883e', 'xiaoyu@example.com',  '小雨',       '138****3302', 0, 0, NOW() - INTERVAL 31 DAY),
('momo',    'e10adc3949ba59abbe56e057f20f883e', 'momo@example.com',    '默默',       '',            0, 0, NOW() - INTERVAL 24 DAY),
('aqi',     'e10adc3949ba59abbe56e057f20f883e', 'aqi@example.com',     '阿七',       '139****7741', 0, 0, NOW() - INTERVAL 18 DAY),
('tangtang','e10adc3949ba59abbe56e057f20f883e', 'tangtang@example.com','糖糖',       '',            0, 0, NOW() - INTERVAL 12 DAY),
('nuan',    'e10adc3949ba59abbe56e057f20f883e', 'nuan@example.com',    '暖爪志愿者', '137****9910', 1, 0, NOW() - INTERVAL 9 DAY),
('xiaoman', 'e10adc3949ba59abbe56e057f20f883e', 'xiaoman@example.com', '小满',       '136****2288', 0, 0, NOW() - INTERVAL 6 DAY),
('ajie',    'e10adc3949ba59abbe56e057f20f883e', 'ajie@example.com',    '阿哲',       '',            0, 0, NOW() - INTERVAL 3 DAY);

-- 更多待领养动物
INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '花卷', 2, 9, '母', '三花', 3.2, '健康，已疫苗，已绝育',
       '花卷在雨天躲进了便利店，被店员收留后交给我们。她有点慢热，但只要你愿意等她，她会用呼噜声回应你。',
       '/upload/lost1.jpg', 0, NOW() - INTERVAL 7 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '花卷' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '豆豆', 5, 16, '公', '灰棕色', 9.6, '健康，已驱虫',
       '豆豆是在公园门口被发现的，刚来时毛发打结。剪完毛之后才发现，他是一个眼睛特别亮的小伙子，喜欢散步，也很忠诚。',
       '/upload/lost2.jpg', 0, NOW() - INTERVAL 10 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '豆豆' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '奶糖', 5, 14, '母', '奶黄色', 6.4, '健康，已疫苗',
       '奶糖是一只被人遗弃在小区门口的小型犬，非常亲人，会握手，也能和其他狗狗友好相处。',
       '/upload/story2.jpg', 0, NOW() - INTERVAL 5 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '奶糖' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '三花', 2, 11, '母', '三花', 3.5, '健康，已绝育',
       '三花曾经在菜市场附近流浪，性格独立但很爱撒娇。她喜欢晒太阳，也喜欢被轻轻挠下巴。',
       '/upload/story3.jpg', 0, NOW() - INTERVAL 4 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '三花' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '可乐', 5, 26, '公', '巧克力色', 7.8, '健康，已驱虫',
       '可乐走失过一段时间，被志愿者在公园找到。他性格胆小但非常温顺，需要一位有耐心的家人。',
       '/upload/lost2.jpg', 0, NOW() - INTERVAL 12 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '可乐' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '布丁', 2, 7, '公', '橘色虎斑', 2.8, '健康，已完成第一针',
       '布丁是同一窝里最贪吃的小橘猫，听到罐头声音会第一个跑过来。喜欢被摸下巴，也能和其他猫和平相处。',
       '/upload/pet2.jpg', 0, NOW() - INTERVAL 3 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '布丁' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '雪梨', 7, 18, '母', '海豹双色', 4.6, '健康，已驱虫',
       '雪梨有一双很蓝的眼睛，性格像一团软棉花。需要定期梳毛，希望你愿意为这份漂亮的长毛多花一点时间。',
       '/upload/pet6.jpg', 2, NOW() - INTERVAL 20 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '雪梨' AND deleted = 0);

INSERT INTO pet (`name`,`category_id`,`age`,`gender`,`color`,`weight`,`health_status`,`description`,`images`,`status`,`create_time`)
SELECT '小满', 3, 10, '公', '浅金色', 12.5, '健康，疫苗齐全',
       '小满是救助站里最热情的小朋友，见人就摇尾巴。正在学习坐下和等待，适合有时间陪伴、愿意带他运动的家庭。',
       '/upload/pet3.jpg', 1, NOW() - INTERVAL 16 DAY
WHERE NOT EXISTS (SELECT 1 FROM pet WHERE name = '小满' AND deleted = 0);

-- 社区帖子
INSERT INTO post (`title`,`content`,`images`,`user_id`,`username`,`like_count`,`status`,`create_time`)
SELECT '第一次带救助猫回家，需要准备什么？',
       '**新手求助！** 昨天刚从救助站接回一只 8 个月大的狸花猫，目前一直躲在床底。\n\n目前已准备：\n- 猫砂盆、猫砂\n- 幼猫粮和饮水机\n- 一个安静的次卧\n\n请问大家，前几天需要强行把它抱出来吗？',
       '/upload/pet1.jpg', 2, '测试用户', 42, 0, NOW() - INTERVAL 5 HOUR
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '第一次带救助猫回家，需要准备什么？' AND deleted = 0);

INSERT INTO post (`title`,`content`,`images`,`user_id`,`username`,`like_count`,`status`,`create_time`)
SELECT '周末志愿日记：给 12 只狗狗洗澡',
       '今天和 5 位志愿者一起给园区里的狗狗洗澡、剪指甲。最怕水的大黄全程把脑袋埋在我怀里，洗完却第一个在草地上打滚。\n\n累是真的累，但看到它们干干净净地晒太阳，觉得一切都值得。',
       '/upload/pet3.jpg', 3, '志愿者小林', 96, 0, NOW() - INTERVAL 1 DAY
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '周末志愿日记：给 12 只狗狗洗澡' AND deleted = 0);

INSERT INTO post (`title`,`content`,`images`,`user_id`,`username`,`like_count`,`status`,`create_time`)
SELECT '领养后第 30 天，他终于会在我脚边睡觉了',
       '刚到家时他连饭都要等我走开才吃。现在每天我坐在书桌前，他就会蜷在脚边。**信任真的需要时间，但值得。**',
       '/upload/pet5.jpg', 4, '夏天的风', 134, 0, NOW() - INTERVAL 2 DAY
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '领养后第 30 天，他终于会在我脚边睡觉了' AND deleted = 0);

INSERT INTO post (`title`,`content`,`images`,`user_id`,`username`,`like_count`,`status`,`create_time`)
SELECT '自制猫饭真的比猫粮更好吗？',
       '最近看了很多自制猫饭的教程，有点心动。想请教大家，长期自制需要注意哪些营养配比？有没有靠谱的参考？',
       '', 5, '豆包', 27, 0, NOW() - INTERVAL 3 DAY
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '自制猫饭真的比猫粮更好吗？' AND deleted = 0);

INSERT INTO post (`title`,`content`,`images`,`user_id`,`username`,`like_count`,`status`,`create_time`)
SELECT '给老年犬一个安稳的晚年',
       '很多人领养时会更偏向幼犬，但我们想给一只大龄狗狗一个家。阿墨走路慢慢的，吃饭也慢慢的，可他每天都会在门口等我们下班。',
       '/upload/pet7.jpg', 6, '周先生', 321, 0, NOW() - INTERVAL 4 DAY
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '给老年犬一个安稳的晚年' AND deleted = 0);

INSERT INTO post (`title`,`content`,`images`,`user_id`,`username`,`like_count`,`status`,`create_time`)
SELECT '猫咪一直躲着不出来，是讨厌我吗？',
       '到家第三天了，猫还是只在晚上出来吃饭。白天我一靠近就躲。有点挫败……\n\n有没有过来人分享一下经验？',
       '', 7, '默默', 18, 0, NOW() - INTERVAL 8 HOUR
WHERE NOT EXISTS (SELECT 1 FROM post WHERE title = '猫咪一直躲着不出来，是讨厌我吗？' AND deleted = 0);

-- 公告
INSERT INTO notice (`title`,`content`,`status`,`create_time`)
SELECT '「暖冬计划」流浪动物过冬物资募集开始啦',
       '天气渐凉，救助站正在为园区里的毛孩子们准备过冬物资，包括保暖垫、猫砂、犬粮和驱虫药。欢迎通过爱心捐赠页面参与，所有物资明细会在每月 1 日公示。',
       0, NOW() - INTERVAL 1 DAY
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE title = '「暖冬计划」流浪动物过冬物资募集开始啦' AND deleted = 0);

INSERT INTO notice (`title`,`content`,`status`,`create_time`)
SELECT '10 月领养家庭回访安排',
       '本月回访将在 10 月 26 日进行，志愿者会提前与领养家庭电话联系。回访内容主要包括宠物健康、饮食和适应情况，感谢大家的配合。',
       0, NOW() - INTERVAL 3 DAY
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE title = '10 月领养家庭回访安排' AND deleted = 0);

INSERT INTO notice (`title`,`content`,`status`,`create_time`)
SELECT '领养须知：请给彼此一个适应期',
       '刚到新家的宠物可能会出现躲藏、拒食或夜间叫唤的情况，这是正常的应激反应。建议准备一个安静的小空间，不要强行抱抱，给它 3–7 天时间熟悉气味和环境。',
       0, NOW() - INTERVAL 5 DAY
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE title = '领养须知：请给彼此一个适应期' AND deleted = 0);

INSERT INTO notice (`title`,`content`,`status`,`create_time`)
SELECT '本周六 · 线下领养日志愿者招募',
       '本周六 13:00–17:00 将在徐汇区滨江绿地举办线下领养日活动，需要 8 名志愿者协助布置场地、照顾动物和引导来访家庭。报名请联系客服微信。',
       0, NOW() - INTERVAL 8 DAY
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE title = '本周六 · 线下领养日志愿者招募' AND deleted = 0);

-- 寻宠启事
INSERT INTO lost_pet (`title`,`type`,`pet_type`,`color`,`location`,`contact`,`description`,`images`,`status`,`user_id`,`create_time`)
SELECT '徐汇区捡到一只蓝眼奶牛猫','捡到','猫','黑白','上海市徐汇区 · 田林路','微信：warmpaw_01',
       '很亲人，戴着红色项圈，会使用猫砂。暂时安置在家中，希望主人尽快联系。','/upload/lost1.jpg',0,2,NOW() - INTERVAL 12 HOUR
WHERE NOT EXISTS (SELECT 1 FROM lost_pet WHERE title = '徐汇区捡到一只蓝眼奶牛猫' AND deleted = 0);

INSERT INTO lost_pet (`title`,`type`,`pet_type`,`color`,`location`,`contact`,`description`,`images`,`status`,`user_id`,`create_time`)
SELECT '寻找走失的巧克力色泰迪','走失','狗','巧克力色','上海市静安区 · 大宁公园附近','138****6621',
       '名字叫可乐，3 岁，公犬，左耳有一小块浅色毛。走失时没有穿衣服，性格胆小，看到人会躲。','/upload/lost2.jpg',0,4,NOW() - INTERVAL 1 DAY
WHERE NOT EXISTS (SELECT 1 FROM lost_pet WHERE title = '寻找走失的巧克力色泰迪' AND deleted = 0);

INSERT INTO lost_pet (`title`,`type`,`pet_type`,`color`,`location`,`contact`,`description`,`images`,`status`,`user_id`,`create_time`)
SELECT '地铁站附近捡到橘猫，正在找主人','捡到','猫','橘色','上海市浦东新区 · 世纪大道','微信：orange_cat_2026',
       '在地铁 2 号口附近徘徊了两天，很干净，应该是家养猫。已带去宠物医院检查，没有芯片。','/upload/lost3.jpg',0,5,NOW() - INTERVAL 2 DAY
WHERE NOT EXISTS (SELECT 1 FROM lost_pet WHERE title = '地铁站附近捡到橘猫，正在找主人' AND deleted = 0);

INSERT INTO lost_pet (`title`,`type`,`pet_type`,`color`,`location`,`contact`,`description`,`images`,`status`,`user_id`,`create_time`)
SELECT '寻主：小区门口捡到一只博美','捡到','狗','奶白色','上海市普陀区 · 真如','021-6******8',
       '非常亲人，会握手，疑似刚走失不久。请主人描述项圈颜色和特征。','/upload/pet8.jpg',0,6,NOW() - INTERVAL 3 DAY
WHERE NOT EXISTS (SELECT 1 FROM lost_pet WHERE title = '寻主：小区门口捡到一只博美' AND deleted = 0);

-- 领养申请
INSERT INTO adopt_apply (`user_id`,`pet_id`,`reason`,`address`,`experience`,`status`,`reply`,`create_time`)
SELECT 3, 5, '家里有稳定住所，已养猫五年，希望给小暹罗一个安静的家。','上海市徐汇区','养过两只猫，了解常见疾病护理。',0,'',NOW() - INTERVAL 10 HOUR
WHERE NOT EXISTS (SELECT 1 FROM adopt_apply WHERE user_id = 3 AND pet_id = 5 AND deleted = 0);

INSERT INTO adopt_apply (`user_id`,`pet_id`,`reason`,`address`,`experience`,`status`,`reply`,`create_time`)
SELECT 4, 7, '家里有院子，希望给大型犬一个安稳的家。','上海市松江区','养过两只拉布拉多。',0,'',NOW() - INTERVAL 1 DAY
WHERE NOT EXISTS (SELECT 1 FROM adopt_apply WHERE user_id = 4 AND pet_id = 7 AND deleted = 0);

INSERT INTO adopt_apply (`user_id`,`pet_id`,`reason`,`address`,`experience`,`status`,`reply`,`create_time`)
SELECT 6, 8, '喜欢布偶猫，家中无其他宠物，有三年养猫经验。','上海市杨浦区','有三年养猫经验。',1,'已通过审核，待安排接猫。',NOW() - INTERVAL 4 DAY
WHERE NOT EXISTS (SELECT 1 FROM adopt_apply WHERE user_id = 6 AND pet_id = 8 AND deleted = 0);

INSERT INTO adopt_apply (`user_id`,`pet_id`,`reason`,`address`,`experience`,`status`,`reply`,`create_time`)
SELECT 5, 3, '希望领养一只金毛陪伴孩子。','上海市浦东新区','无养犬经验，家中孩子 4 岁。',2,'建议先积累养犬经验，欢迎参加线下领养活动。',NOW() - INTERVAL 5 DAY
WHERE NOT EXISTS (SELECT 1 FROM adopt_apply WHERE user_id = 5 AND pet_id = 3 AND deleted = 0);

-- 上报记录
INSERT INTO report (`user_id`,`location`,`description`,`images`,`contact`,`status`,`reply`,`create_time`)
SELECT 2,'上海市长宁区 · 中山公园 3 号门',
       '一只黄色小型犬在门口徘徊，脖子上有蓝色项圈，看起来有些脱水。已留下饮用水。',
       '/upload/lost2.jpg','138****2210',0,'',NOW() - INTERVAL 8 HOUR
WHERE NOT EXISTS (SELECT 1 FROM report WHERE location = '上海市长宁区 · 中山公园 3 号门' AND deleted = 0);

INSERT INTO report (`user_id`,`location`,`description`,`images`,`contact`,`status`,`reply`,`create_time`)
SELECT 3,'上海市杨浦区 · 五角场地铁站',
       '地铁口纸箱里有三只小猫，大约一个月大，天气转凉，希望尽快有人接手。',
       '/upload/pet5.jpg','微信 cat_rescue',0,'',NOW() - INTERVAL 20 HOUR
WHERE NOT EXISTS (SELECT 1 FROM report WHERE location = '上海市杨浦区 · 五角场地铁站' AND deleted = 0);

INSERT INTO report (`user_id`,`location`,`description`,`images`,`contact`,`status`,`reply`,`create_time`)
SELECT 4,'上海市徐汇区 · 田林路',
       '一只受伤的橘猫，后腿有擦伤，躲在车底，已联系附近志愿者。',
       '/upload/lost3.jpg','139****0091',1,'已送医处理伤口，目前在隔离区观察。',NOW() - INTERVAL 3 DAY
WHERE NOT EXISTS (SELECT 1 FROM report WHERE location = '上海市徐汇区 · 田林路' AND deleted = 0);

-- 寄养申请
INSERT INTO foster (`user_id`,`pet_name`,`category_id`,`pet_gender`,`pet_age`,`start_date`,`end_date`,`description`,`status`,`room_id`,`create_time`)
SELECT 2,'奶糖',1,'母',8,'2026-10-24','2026-10-31','国庆出行，希望帮忙照顾一周。',0,NULL,NOW() - INTERVAL 4 DAY
WHERE NOT EXISTS (SELECT 1 FROM foster WHERE user_id = 2 AND pet_name = '奶糖' AND deleted = 0);

INSERT INTO foster (`user_id`,`pet_name`,`category_id`,`pet_gender`,`pet_age`,`start_date`,`end_date`,`description`,`status`,`room_id`,`create_time`)
SELECT 5,'可乐',5,'公',26,'2026-10-22','2026-10-28','第一次寄养，狗狗比较胆小，需要安静房间。',1,2,NOW() - INTERVAL 6 DAY
WHERE NOT EXISTS (SELECT 1 FROM foster WHERE user_id = 5 AND pet_name = '可乐' AND deleted = 0);

-- 订单与物流
INSERT IGNORE INTO orders (`order_no`,`user_id`,`total_amount`,`status`,`receiver_name`,`receiver_phone`,`receiver_address`,`pay_time`,`create_time`)
VALUES ('PET202610180001',2,158.90,1,'田同学','138****2210','上海市徐汇区某路 88 号',NOW() - INTERVAL 2 DAY,NOW() - INTERVAL 2 DAY);

INSERT IGNORE INTO orders (`order_no`,`user_id`,`total_amount`,`status`,`receiver_name`,`receiver_phone`,`receiver_address`,`pay_time`,`create_time`)
VALUES ('PET202610120002',2,99.80,2,'田同学','138****2210','上海市徐汇区某路 88 号',NOW() - INTERVAL 8 DAY,NOW() - INTERVAL 8 DAY);

INSERT INTO order_item (`order_id`,`product_id`,`product_name`,`product_image`,`price`,`quantity`)
SELECT o.id,1,'皇家室内成猫粮 2kg','/upload/prod1.jpg',129.00,1 FROM orders o
WHERE o.order_no='PET202610180001'
  AND NOT EXISTS (SELECT 1 FROM order_item oi WHERE oi.order_id=o.id AND oi.product_id=1 AND oi.deleted=0);

INSERT INTO order_item (`order_id`,`product_id`,`product_name`,`product_image`,`price`,`quantity`)
SELECT o.id,5,'天然豆腐猫砂 6L','/upload/prod5.jpg',29.90,2 FROM orders o
WHERE o.order_no='PET202610120002'
  AND NOT EXISTS (SELECT 1 FROM order_item oi WHERE oi.order_id=o.id AND oi.product_id=5 AND oi.deleted=0);

INSERT INTO order_item (`order_id`,`product_id`,`product_name`,`product_image`,`price`,`quantity`)
SELECT o.id,4,'温和抑菌宠物沐浴露','/upload/prod4.jpg',39.00,1 FROM orders o
WHERE o.order_no='PET202610120002'
  AND NOT EXISTS (SELECT 1 FROM order_item oi WHERE oi.order_id=o.id AND oi.product_id=4 AND oi.deleted=0);

INSERT INTO logistics (`order_id`,`company_id`,`tracking_no`,`sender_address`,`receiver_address`,`status`,`trace_info`,`create_time`)
SELECT o.id,1,'SF1234567890','上海市徐汇区暖爪公益仓','上海市徐汇区某路 88 号','运输中','快件已到达上海徐汇中转场',NOW() - INTERVAL 7 DAY
FROM orders o
WHERE o.order_no='PET202610120002'
  AND NOT EXISTS (SELECT 1 FROM logistics l WHERE l.order_id=o.id AND l.deleted=0);
