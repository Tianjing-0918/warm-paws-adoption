USE pet_adoption;

SET @adopt_col_exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet' AND COLUMN_NAME = 'adopt_time'
);
SET @adopt_col_sql = IF(@adopt_col_exists = 0,
  'ALTER TABLE pet ADD COLUMN adopt_time DATETIME NULL COMMENT ''领养完成时间'' AFTER adopt_user_id',
  'SELECT 1');
PREPARE adopt_col_stmt FROM @adopt_col_sql;
EXECUTE adopt_col_stmt;
DEALLOCATE PREPARE adopt_col_stmt;

UPDATE pet SET images = '/media/animals/pet-01-mimi.jpg'   WHERE id = 1;
UPDATE pet SET images = '/media/animals/pet-02-juzi.jpg'   WHERE id = 2;
UPDATE pet SET images = '/media/animals/pet-03-dahuang.jpg' WHERE id = 3;
UPDATE pet SET images = '/media/animals/pet-04-duantui.jpg' WHERE id = 4;
UPDATE pet SET images = '/media/animals/pet-05-xiaoxian.jpg' WHERE id = 5;
UPDATE pet SET images = '/media/animals/pet-06-tuantuan.jpg' WHERE id = 6;
UPDATE pet SET images = '/media/animals/pet-07-xiaohei.jpg' WHERE id = 7;
UPDATE pet SET images = '/media/animals/pet-08-xueqiu.jpg' WHERE id = 8;
UPDATE pet SET images = '/media/animals/pet-09-huajuan.jpg' WHERE id = 9;
UPDATE pet SET images = '/media/animals/pet-10-doudou.jpg' WHERE id = 10;
UPDATE pet SET images = '/media/animals/pet-11-naitang.jpg' WHERE id = 11;
UPDATE pet SET images = '/media/animals/pet-12-sanhua.jpg' WHERE id = 12;
UPDATE pet SET images = '/media/animals/pet-13-kele.jpg'   WHERE id = 13;
UPDATE pet SET images = '/media/animals/pet-14-buding.jpg' WHERE id = 14;
UPDATE pet SET images = '/media/animals/pet-15-xueli.jpg'  WHERE id = 15;
UPDATE pet SET images = '/media/animals/pet-16-xiaoman.jpg' WHERE id = 16;

UPDATE pet SET adopt_time = update_time WHERE status = 1 AND adopt_time IS NULL;
UPDATE pet SET color = '黑棕白' WHERE id = 10;

UPDATE banner SET title = '大黄', subtitle = '2 岁 · 温柔的大金毛',
  image = '/media/animals/pet-03-dahuang.jpg', link = '/pet/3' WHERE id = 1;
UPDATE banner SET title = '豆豆', subtitle = '1 岁 4 个月 · 爱散步的小暖男',
  image = '/media/animals/pet-10-doudou.jpg', link = '/pet/10' WHERE id = 2;
UPDATE banner SET title = '可乐', subtitle = '2 岁 2 个月 · 慢热但很温柔',
  image = '/media/animals/pet-13-kele.jpg', link = '/pet/13' WHERE id = 3;

UPDATE post SET images = '/media/animals/pet-14-buding.jpg' WHERE id = 1;
UPDATE post SET images = '/media/animals/pet-04-duantui.jpg' WHERE id = 2;
UPDATE post SET images = '/media/animals/pet-13-kele.jpg'   WHERE id = 3;
UPDATE post SET images = '/media/animals/pet-09-huajuan.jpg' WHERE id = 4;
UPDATE post SET images = '/media/animals/pet-16-xiaoman.jpg' WHERE id = 5;
UPDATE post SET images = '/media/animals/pet-12-sanhua.jpg' WHERE id = 6;
UPDATE post SET images = '/media/animals/pet-07-xiaohei.jpg' WHERE id = 8;

UPDATE lost_pet SET images = '/media/animals/lost-01.jpg' WHERE id IN (1, 4);
UPDATE lost_pet SET images = '/media/animals/lost-02.jpg' WHERE id IN (2, 5, 7);
UPDATE lost_pet SET images = '/media/animals/lost-03.jpg' WHERE id IN (3, 6);

UPDATE report SET images = '/media/animals/lost-02.jpg' WHERE id = 1;
UPDATE report SET images = '/media/animals/lost-03.jpg' WHERE id = 2;
UPDATE report SET images = '/media/animals/lost-01.jpg' WHERE id = 3;

UPDATE trial_checkin SET images = '/media/animals/pet-01-mimi.jpg'
  WHERE images LIKE '/upload/%' AND pet_name = '咪咪';
