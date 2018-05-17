REPLACE INTO intellif_base.t_api_resource VALUES ('1101394001', '/intellif/person/statistics', 'GET');
REPLACE INTO intellif_base.t_business_api VALUES ('800', '1101394001');
REPLACE INTO intellif_base.t_api_resource VALUES ('1101394002', '/intellif/dataExport/person/statistics', 'GET');
REPLACE INTO intellif_base.t_business_api VALUES ('800', '1101394002');

CREATE TABLE IF NOT EXISTS intellif_face.t_person_statistic (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `count` int(11) DEFAULT NULL COMMENT '数量',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `gender` int(11) DEFAULT NULL COMMENT '性别',
  `time_type` tinyint(4) DEFAULT NULL COMMENT '日期汇总类型10:日,20;月,30:年',
  `person_type` tinyint(4) DEFAULT NULL COMMENT '人员汇总类型10:总榜,20:性别,30:年龄',
  `time` date DEFAULT NULL COMMENT '汇总时间',
  `source_id` bigint(20) NOT NULL COMMENT '区分区域',
  PRIMARY KEY (`id`)
) 
COMMENT='人流汇总表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;




-- ----------------------------
-- Procedure structure for personStatistics
-- ----------------------------
DROP PROCEDURE IF EXISTS intellif_base.personStatistics;

DELIMITER $$

CREATE PROCEDURE intellif_base.personStatistics()
BEGIN
  DECLARE n VARCHAR (20);
  DECLARE sqlString1 VARCHAR (500);
  DECLARE sqlString2 VARCHAR (500);
  DECLARE sqlString3 VARCHAR (500);
  declare done int default false;
  DECLARE cur CURSOR FOR SELECT
	table_name
FROM
	intellif_face.t_tables
WHERE
	short_name = 't_face'
AND total_num > 0;

DECLARE CONTINUE HANDLER FOR NOT found SET done = TRUE;

OPEN cur;

	FETCH cur INTO n;
  SELECT n;
WHILE (not done) do

-- 日
set sqlString1=CONCAT('INSERT INTO intellif_face.t_person_statistic (
	time,
	count,
	age,
	gender,
	person_type,
	time_type
 ,source_id
) SELECT
	DATE_FORMAT(time, "%Y-%m-%d") AS time,
	COUNT(1) AS count,
	0 AS age,
	0 AS gender,
	10 AS person_type,
	10 AS time_type,
  source_id
FROM intellif_face.',
	n,' WHERE
  1=1
GROUP BY
	DATE_FORMAT(time, "%Y-%m-%d"),source_id
  -- ,age
	-- ,gender
ORDER BY
	DATE_FORMAT(time, "%Y-%m-%d");');
select sqlString1;
set @sql1=sqlString1;
prepare stmt1 from @sql1;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;
-- 日按性别
set sqlString2=CONCAT('INSERT INTO intellif_face.t_person_statistic (
	time,
	count,
	age,
	gender,
	person_type,
	time_type
 ,source_id
) SELECT
	DATE_FORMAT(time, "%Y-%m-%d") AS time,
	COUNT(1) AS count,
	0 AS age,
	 gender,
	20 AS person_type,
	10 AS time_type,
  source_id
FROM intellif_face.',
	n,' WHERE
  1=1
GROUP BY
	DATE_FORMAT(time, "%Y-%m-%d"),source_id
  -- ,age
 ,gender
ORDER BY
	DATE_FORMAT(time, "%Y-%m-%d");');
select sqlString2;
set @sql2=sqlString2;
prepare stmt2 from @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;
 -- 日按年龄
set sqlString3=CONCAT('INSERT INTO intellif_face.t_person_statistic (
	time,
	count,
	age,
	gender,
	person_type,
	time_type
 ,source_id
) SELECT
	DATE_FORMAT(time, "%Y-%m-%d") AS time,
	COUNT(1) AS count,
	 age,
	0 AS gender,
	30 AS person_type,
	10 AS time_type,
  source_id
FROM intellif_face.',
	n,' WHERE
  1=1
GROUP BY
	DATE_FORMAT(time, "%Y-%m-%d"),source_id
  ,age
	-- ,gender
ORDER BY
	DATE_FORMAT(time, "%Y-%m-%d");');
select sqlString3;
set @sql3=sqlString3;
prepare stmt3 from @sql3;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

fetch cur into n;

END
WHILE
;-- 关闭游标
CLOSE cur;
-- 月
insert into intellif_face.t_person_statistic (time,count,age,gender,person_type,time_type,source_id)

select
  CONCAT(DATE_FORMAT(time,   '%Y-%m'),'-01') as time
  ,sum(count) as count
	,0 as age
	,0 AS gender
  ,person_type
  ,20 as time_type
  ,source_id
from
  intellif_face.t_person_statistic
WHERE
  1=1
  and person_type=10
GROUP BY
	DATE_FORMAT(time,   '%Y-%m'),source_id
ORDER BY
	DATE_FORMAT(time,   '%Y-%m');
-- 月
insert into intellif_face.t_person_statistic (time,count,age,gender,person_type,time_type,source_id)

select
  CONCAT(DATE_FORMAT(time,   '%Y-%m'),'-01') as time
  ,sum(count) as count
	,0 as age
	, gender
  ,person_type
  ,20 as time_type
  ,source_id
from
  intellif_face.t_person_statistic
WHERE
  1=1
  and person_type=20
GROUP BY
	DATE_FORMAT(time,   '%Y-%m'),gender,source_id
ORDER BY
	DATE_FORMAT(time,   '%Y-%m');
-- 月
insert into intellif_face.t_person_statistic (time,count,age,gender,person_type,time_type,source_id)

select
  CONCAT(DATE_FORMAT(time,   '%Y-%m'),'-01')
  ,sum(count) as count
	,age
	,0 AS gender
  ,person_type
  ,20 as time_type
  ,source_id
from
  intellif_face.t_person_statistic
WHERE
  1=1
  and person_type=30
GROUP BY
	DATE_FORMAT(time,   '%Y-%m'),age,source_id
ORDER BY
	DATE_FORMAT(time,   '%Y-%m');
-- 年
insert into intellif_face.t_person_statistic (time,count,age,gender,person_type,time_type,source_id)
select
  CONCAT(DATE_FORMAT(time,   '%Y'),'-01-01') as time
  ,sum(count) as count
	,0 as age
	,0 AS gender
  ,person_type
  ,30 as time_type
  ,source_id
from
  intellif_face.t_person_statistic
WHERE
  1=1
  and person_type=10
GROUP BY
	DATE_FORMAT(time,   '%Y'),source_id
ORDER BY
	DATE_FORMAT(time,   '%Y');
-- 年
insert into intellif_face.t_person_statistic (time,count,age,gender,person_type,time_type,source_id)

select
  CONCAT(DATE_FORMAT(time,   '%Y'),'-01-01') as time
  ,sum(count) as count
	,0 as age
	, gender
  ,person_type
  ,30 as time_type
  ,source_id
from
  intellif_face.t_person_statistic
WHERE
  1=1
  and person_type=20
GROUP BY
	DATE_FORMAT(time,   '%Y'),gender,source_id
ORDER BY
	DATE_FORMAT(time,   '%Y');
-- 年
insert into intellif_face.t_person_statistic (time,count,age,gender,person_type,time_type,source_id)

select
  CONCAT(DATE_FORMAT(time,   '%Y'),'-01-01') as time
  ,sum(count) as count
	, age
	,0 AS gender
  ,person_type
  ,30 as time_type
  ,source_id
from
  intellif_face.t_person_statistic
WHERE
  1=1
  and person_type=30
GROUP BY
	DATE_FORMAT(time,   '%Y'),age,source_id
ORDER BY
	DATE_FORMAT(time,   '%Y');#Routine body goes here...

END;
$$

DELIMITER ;

call  intellif_base.personStatistics();
DROP PROCEDURE IF EXISTS intellif_base.personStatistics;



