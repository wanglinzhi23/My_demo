
1 新建表
 CREATE TABLE IF NOT EXISTS  intellif_base.t_mall_syn (
	`id` BIGINT(64) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`class_name` VARCHAR(63) NOT NULL COMMENT '类名',
	`data_id` BIGINT(64) NOT NULL COMMENT '数据ID',
	`status` ENUM('WAIT','SUCCESS','FAIL') NOT NULL DEFAULT 'WAIT' COMMENT '状态',
	`operation` ENUM('INSERT','UPDATE','DELETE') NOT NULL DEFAULT 'INSERT' COMMENT '操作',
	`trigger_way` ENUM('SCAN','DEPEND','LISTEN') NOT NULL DEFAULT 'SCAN' COMMENT '触发方式',
	`created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '同步成功时间',
	PRIMARY KEY (`id`)
)
COMMENT='沃尔玛（商超）同步表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

2 删除表
 DROP TABLE if EXISTS intellif_base.t_mall_syn1;
 
3 表添加字段
 CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'area_id', 'int(25)','not null default 0');

4 表删除字段
 CALL intellif_base.deleteFieldIfExists('intellif_base','t_red_detail', 'image_url');

5 添加索引
 CALL intellif_base.add_index('intellif_base', 't_alarm_info', 't_alarm_info_time', 'time');

6 删除索引
 call intellif_base.delete_index('intellif_base','t_mall_syn', 'field'); 
 
7 修改字段值 
 call intellif_base.updateFieldValues('intellif_base','t_lose_pre_c','phone','145623','where pass = 1');//若全表update则最后一个参数为'';

8 修改字段属性
 call intellif_base.updateFieldAttribute('intellif_base','t_lose_pre_c','pass','int(11)');
 
9 表插入数据(若存在则更新，不存在插入)
 REPLACE INTO intellif_base.t_api_resource VALUES ('1004', '/intellif/police/station/display', 'POST'); //当前行若存在数据，则update
 
10 表插入数据(若存在则不更新,不存在插入)
  INSERT INTO t_police_man_authority_type(id,type_id,type_name)
  SELECT '1', '1', '动态检索、布控'
  FROM dual
  WHERE not exists (select * from t_police_man_authority_type
  where t_police_man_authority_type.id = 1); //判断id为1数据是否存在
  
11 创建存储过程
   
  DROP PROCEDURE IF EXISTS intellif_base.add_index; 
  DELIMITER $$
  CREATE PROCEDURE intellif_base.add_index(IN dbname varchar(100), IN tablename varchar(100), IN indexname varchar(100), IN columnname varchar(100))
  BEGIN
	/*内容*/
  END$$ 
  DELIMITER ;
 
