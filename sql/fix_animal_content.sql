-- ============================================================
-- 暖爪之家 · 宠物档案与社区配图一致性修复
-- 可重复执行，不会删除用户自行发布的数据
-- ============================================================
USE pet_adoption;

-- 补全与照片相符的品种，避免“三花猫显示橘猫”“比格犬显示田园犬”等情况。
INSERT INTO pet_category (`name`, `type`, `description`)
SELECT '中华田园猫', '猫', '体质好、性格多样，包含狸花、三花等花色'
WHERE NOT EXISTS (
  SELECT 1 FROM pet_category WHERE name = '中华田园猫' AND deleted = 0
);

INSERT INTO pet_category (`name`, `type`, `description`)
SELECT '比格犬', '狗', '垂耳、短毛，性格活泼亲人'
WHERE NOT EXISTS (
  SELECT 1 FROM pet_category WHERE name = '比格犬' AND deleted = 0
);

INSERT INTO pet_category (`name`, `type`, `description`)
SELECT '混血犬', '狗', '性格与体型因个体而异'
WHERE NOT EXISTS (
  SELECT 1 FROM pet_category WHERE name = '混血犬' AND deleted = 0
);

UPDATE pet
SET category_id = (SELECT id FROM pet_category WHERE name = '中华田园猫' AND deleted = 0 LIMIT 1),
    color = '三花'
WHERE id IN (9, 12);

-- “花卷”和“三花”是两只不同的猫，使用独立照片避免重复。
UPDATE pet SET images = '/media/animals/pet-09-huajuan-v2.jpg' WHERE id = 9;

UPDATE pet
SET category_id = (SELECT id FROM pet_category WHERE name = '比格犬' AND deleted = 0 LIMIT 1),
    color = '黄白双色'
WHERE id = 11;

UPDATE pet
SET category_id = (SELECT id FROM pet_category WHERE name = '混血犬' AND deleted = 0 LIMIT 1)
WHERE id IN (13, 16);

UPDATE pet SET color = '棕白色' WHERE id = 16;

-- 领养申请已通过的雪球，应与领养记录保持同一状态。
UPDATE pet
SET status = 1,
    adopt_user_id = 6,
    adopt_time = COALESCE(adopt_time, '2026-10-16 10:37:02')
WHERE id = 8;

-- 首页轮播只展示当前仍可领养的动物，并保证名字、照片、品种一致。
UPDATE banner SET title = '大黄', subtitle = '2 岁 · 温柔的大金毛',
  image = '/media/animals/pet-03-dahuang.jpg', link = '/pet/3' WHERE id = 1;
UPDATE banner SET title = '豆豆', subtitle = '1 岁 4 个月 · 爱散步的小暖男',
  image = '/media/animals/pet-10-doudou.jpg', link = '/pet/10' WHERE id = 2;
UPDATE banner SET title = '可乐', subtitle = '2 岁 2 个月 · 慢热但很温柔',
  image = '/media/animals/pet-13-kele.jpg', link = '/pet/13' WHERE id = 3;

-- 社区帖子改用居家、志愿服务等独立照片，不再把待领养档案照片当作已领养宠物。
UPDATE post
SET username = '小满',
    images = '/media/animals/community-cat-care.jpg'
WHERE id = 1;

UPDATE post
SET username = '糖糖',
    images = '/media/animals/community-dog-training.jpg'
WHERE id = 2;

UPDATE post
SET username = '阿七',
    images = '/media/animals/scene-foster-room.jpg'
WHERE id = 3;

UPDATE post
SET username = '橘子汽水',
    images = '/media/animals/community-rescue-cat.jpg'
WHERE id = 4;

UPDATE post
SET username = '志愿者小林',
    images = '/media/animals/community-volunteer.jpg',
    content = REPLACE(content, '大黄', '阿福')
WHERE id = 5;

UPDATE post
SET username = '夏天的风',
    images = '/media/animals/community-adopted-day30.jpg'
WHERE id = 6;

UPDATE post
SET username = '豆包',
    images = ''
WHERE id = 7;

UPDATE post
SET username = '周先生',
    images = '/media/animals/community-senior-dog.jpg'
WHERE id = 8;

UPDATE post
SET username = '默默',
    images = ''
WHERE id = 9;

-- 七条寻宠启事分别使用七张不同照片，并按照片修正标题与特征。
UPDATE lost_pet
SET title = '寻找走失的虎斑猫小花',
    pet_type = '猫',
    color = '棕黑色虎斑',
    location = '上海市金山区 · 金山公园附近',
    contact = '138****8888',
    description = '小花是一只两岁虎斑猫，10 月 10 日在金山公园附近走失，性格温顺，见到人可能会先躲起来。看到她请先联系我，非常感谢。',
    images = '/media/animals/lost-tabby-cat.jpg'
WHERE id = 1;

UPDATE lost_pet
SET title = '鼓楼东街口捡到一只棕黄色小狗',
    pet_type = '狗',
    color = '棕黄色',
    location = '上海市黄浦区 · 东街口',
    contact = '159****6666',
    description = '10 月 12 日在东街口附近捡到一只棕黄色小狗，个头不大，很亲人，已临时安置。请主人描述项圈颜色或独特特征后联系认领。',
    images = '/media/animals/lost-small-dog.jpg'
WHERE id = 2;

UPDATE lost_pet
SET title = '寻找走失的白色幼猫栗子',
    pet_type = '猫',
    color = '灰白色',
    location = '上海市静安区 · 万象城附近',
    contact = '137****5555',
    description = '栗子约三个月大，灰白花色，10 月 11 日走失。胆子比较小，叫名字时偶尔会有回应。天气渐凉，非常着急，请附近的朋友帮忙留意绿化带和车底。',
    images = '/media/animals/lost-white-kitten.jpg'
WHERE id = 3;

UPDATE lost_pet
SET title = '徐汇区捡到一只蓝眼奶牛猫',
    pet_type = '猫',
    color = '黑白',
    location = '上海市徐汇区 · 田林路',
    contact = '微信：warmpaw_01',
    description = '一只黑白奶牛猫，眼睛很漂亮，戴着红色项圈，会使用猫砂，性格亲人。目前暂时安置在家中，请主人描述项圈和毛色细节后联系。',
    images = '/media/animals/lost-01.jpg'
WHERE id = 4;

UPDATE lost_pet
SET title = '寻找走失的蓝眼法斗豆包',
    pet_type = '狗',
    color = '浅黄褐色',
    location = '上海市静安区 · 大宁公园附近',
    contact = '138****6621',
    description = '豆包，3 岁，公犬，是一只浅黄褐色法斗，左边眼睛偏蓝。性格胆小，看到人会先躲。走失时穿着浅色连帽衫，请发现者先联系，不要追赶。',
    images = '/media/animals/lost-02.jpg'
WHERE id = 5;

UPDATE lost_pet
SET title = '地铁站附近捡到虎斑猫，正在找主人',
    pet_type = '猫',
    color = '棕黑虎斑',
    location = '上海市浦东新区 · 世纪大道',
    contact = '微信：tabby_cat_2026',
    description = '在世纪大道地铁 2 号口附近徘徊了两天，棕黑色虎斑，毛发干净，应该是家养猫。已带去宠物医院检查，没有发现芯片。',
    images = '/media/animals/lost-03.jpg'
WHERE id = 6;

UPDATE lost_pet
SET title = '寻主：小区门口捡到一只白色小型犬',
    pet_type = '狗',
    color = '白色',
    location = '上海市普陀区 · 真如',
    contact = '021-6******8',
    description = '一只白色小型犬，毛发蓬松，对人很友好，会握手，疑似刚走失不久。目前由小区保安暂时照看，请主人描述牵引绳颜色和身体特征。',
    images = '/media/animals/lost-white-spitz.jpg'
WHERE id = 7;

-- 救助上报也换成与描述相符的独立照片。
UPDATE report SET images = '/media/animals/scene-register.jpg' WHERE id = 1;
UPDATE report SET images = '/media/animals/scene-foster-cat.jpg' WHERE id = 2;
UPDATE report SET images = '/media/animals/pet-14-buding.jpg' WHERE id = 3;
