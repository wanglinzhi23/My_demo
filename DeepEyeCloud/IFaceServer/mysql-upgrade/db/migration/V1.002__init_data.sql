REPLACE INTO intellif_base.oauth_client_details VALUES ('clientapp', '', '123456', 'read,write', 'password,refresh_token', '', 'USER', null, null, '{}', '');


REPLACE INTO intellif_base.t_allow_ips(`id`, `start_ip`, `start_ip_number`, `end_ip`, `end_ip_number`, `ip_rang_name`, `user`) VALUES ('1', '0.0.0.0',  inet_aton('0.0.0.0'), '255.255.255.255',  inet_aton('255.255.255.255'), null, null);



REPLACE INTO intellif_base.t_role VALUES ('1', '2016-02-24 15:32:07', '2016-02-24 15:32:07', '超级管理员', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23', 'SUPER_ADMIN', '101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160');
REPLACE INTO intellif_base.t_role VALUES ('2', '2015-11-17 15:32:07', '2015-11-17 15:32:07', '管理账户', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23', 'ADMIN', '101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,133,134,135,136,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160');
REPLACE INTO intellif_base.t_role VALUES ('3', '2015-11-17 15:32:07', '2015-11-17 15:32:07', '操作账号', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23', 'USER', '101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,133,135,139,140,141,158,159,160');
REPLACE INTO intellif_base.t_role VALUES ('4', '2015-11-17 15:32:07', '2015-11-17 15:32:07', '查询账号', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23', 'GUEST', '101,105,109,113,117,121,125,129,133,135,158,159,160');
REPLACE INTO intellif_base.t_role VALUES ('5', '2016-02-24 15:32:07', '2016-02-24 15:32:07', '匿名用户', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23', 'ANONYMOUS', '-1');
REPLACE INTO intellif_base.t_role VALUES ('6', '2016-04-05 14:40:52', '2016-04-05 14:40:52', '信义科技', '', 'GUEST', '137');
REPLACE INTO intellif_base.t_role VALUES ('7', '2016-04-05 14:40:52', '2016-04-05 14:40:52', '机器人识别功能', '', 'GUEST', '101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159 ');
REPLACE INTO intellif_base.t_role VALUES ('8', '2016-02-24 15:32:07', '2016-02-24 15:32:07', '测试管理员', '', 'SUPER_ADMIN', '101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159 ');


DELIMITER $$

DROP PROCEDURE IF EXISTS intellif_base.addFieldIfNotExists 
$$

DROP FUNCTION IF EXISTS intellif_base.isFieldExisting 
$$

CREATE FUNCTION intellif_base.isFieldExisting (table_name_IN VARCHAR(100), field_name_IN VARCHAR(100)) 
RETURNS INT
RETURN (
    SELECT COUNT(COLUMN_NAME) 
    FROM INFORMATION_SCHEMA.columns 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = table_name_IN 
    AND COLUMN_NAME = field_name_IN
)
$$

CREATE PROCEDURE intellif_base.addFieldIfNotExists (
    IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
    , IN field_definition_IN VARCHAR(100)
)
BEGIN

  

    SET @isFieldThere = intellif_base.isFieldExisting(table_name_IN, field_name_IN);
    IF (@isFieldThere = 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'ADD COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_definition_IN);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END;
$$
DELIMITER ;

CALL intellif_base.addFieldIfNotExists ('t_allow_ips', 'ip_rang_name', 'VARCHAR(255)');
CALL intellif_base.addFieldIfNotExists ('t_allow_ips', 'user', 'VARCHAR(255)');


update intellif_base.t_role set res_ids =  CONCAT(res_ids, ',161') where id = 1; 

INSERT INTO t_police_station
SELECT 1, '2016-07-21 09:37:08', '2016-07-21 10:22:18', 'POLYGON((-107 39, -102 38, -102 41, -107 41, -107 39))' , null ,'全国总部', 112356 ,8
FROM dual
WHERE not exists (select * from t_police_station
where t_police_station.id = 1);

INSERT INTO intellif_base.t_user
SELECT 1, '2016-05-11 09:37:08', '2016-05-28 10:22:18', '0', '', '0', '0', 'superuser', '', 'superuser', 'e10adc3949ba59abbe56e057f20f883e', '1', '一级警员', '1'
FROM dual
WHERE not exists (select * from intellif_base.t_user
where t_user.id = 1 or t_user.login='superuser');

INSERT INTO intellif_base.t_rule_info
SELECT '1', 'test1', '测试规则#1', '0.85,0.9201,0.9400', '0,1,2'
FROM dual
WHERE not exists (select * from  intellif_base.t_rule_info
where t_rule_info.id = 1);




CALL intellif_base.addFieldIfNotExists ('t_police_station_authority', 'created', 'datetime');
CALL intellif_base.addFieldIfNotExists ('t_police_station_authority', 'updated', 'datetime');



CALL intellif_base.addFieldIfNotExists ('t_black_bank', 'created', 'datetime');
CALL intellif_base.addFieldIfNotExists ('t_black_bank', 'updated', 'datetime');


DELIMITER $$

DROP PROCEDURE IF EXISTS intellif_base.addFieldIfNotExists 
$$

DROP FUNCTION IF EXISTS intellif_base.isFieldExisting 
$$

CREATE FUNCTION intellif_base.isFieldExisting (table_name_IN VARCHAR(100), field_name_IN VARCHAR(100)) 
RETURNS INT
RETURN (
    SELECT COUNT(COLUMN_NAME) 
    FROM INFORMATION_SCHEMA.columns 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = table_name_IN 
    AND COLUMN_NAME = field_name_IN
)
$$

CREATE PROCEDURE intellif_base.addFieldIfNotExists (
    IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
    , IN field_definition_IN VARCHAR(100)
)
BEGIN

    SET @isFieldThere = isFieldExisting(table_name_IN, field_name_IN);
    IF (@isFieldThere = 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'ADD COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_definition_IN);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END;
$$
DELIMITER ;


DELIMITER $$

DROP PROCEDURE IF EXISTS intellif_base.rename_table $$
CREATE PROCEDURE intellif_base.rename_table(IN talbe_name varchar(100), IN new_name varchar(100))
BEGIN

	
	IF NOT EXISTS( (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE()
	        AND TABLE_NAME= talbe_name) ) THEN
	        
	        SET @ddl = CONCAT('RENAME TABLE ', talbe_name);
	        SET @ddl = CONCAT(@ddl, ' ', 'TO') ;
	        SET @ddl = CONCAT(@ddl, ' ',  new_name) ;
	       
	     	PREPARE stmt FROM @ddl;
	        EXECUTE stmt;
	        DEALLOCATE PREPARE stmt;
	END IF;


END $$

DELIMITER ;


CALL intellif_base.addFieldIfNotExists ('t_rule_info', 'created', 'datetime');
CALL intellif_base.addFieldIfNotExists ('t_rule_info', 'updated', 'datetime');

REPLACE INTO intellif_base.t_audit_log_type VALUES ('1', '11', '登录/注销');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('2', '12', '用户信息');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('3', '13', '单位信息');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('4', '14', '库信息');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('5', '15', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('6', '1', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('7', '2', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('8', '3', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('9', '4', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('10', '5', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('11', '6', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('12', '7', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('13', '8', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('14', '9', '黑名单');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('15', '10', '黑名单');

ALTER TABLE intellif_base.t_alarm_info MODIFY  send int(11) NOT NULL;


DELIMITER $$

DROP PROCEDURE IF EXISTS intellif_base.addFieldIfNotExists 
$$

DROP FUNCTION IF EXISTS intellif_base.isFieldExisting 
$$

CREATE FUNCTION intellif_base.isFieldExisting (table_SCHEMA_IN VARCHAR(100),table_name_IN VARCHAR(100), field_name_IN VARCHAR(100)) 
RETURNS INT
RETURN (
    SELECT COUNT(COLUMN_NAME) 
    FROM INFORMATION_SCHEMA.columns 
    WHERE TABLE_SCHEMA = table_SCHEMA_IN 
    AND TABLE_NAME = table_name_IN 
    AND COLUMN_NAME = field_name_IN
)
$$

CREATE PROCEDURE intellif_base.addFieldIfNotExists (
    IN database_name_IN VARCHAR(100)
    ,IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
    , IN field_definition_IN VARCHAR(100)
)
BEGIN

  

    SET @isFieldThere = isFieldExisting(database_name_IN,table_name_IN, field_name_IN);
    IF (@isFieldThere = 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'ADD COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_definition_IN);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END;
$$
DELIMITER ;

ALTER TABLE intellif_base.t_alarm_info MODIFY  send int(11) NOT NULL;


DELIMITER $$

DROP PROCEDURE IF EXISTS  intellif_base.addFieldIfNotExists $$

DROP FUNCTION IF EXISTS  intellif_base.isFieldExisting $$

CREATE FUNCTION  intellif_base.isFieldExisting (table_SCHEMA_IN VARCHAR(100),table_name_IN VARCHAR(100),field_name_IN VARCHAR(100)) 
RETURNS INT
RETURN (
    SELECT COUNT(COLUMN_NAME) 
    FROM INFORMATION_SCHEMA.columns 
    WHERE TABLE_SCHEMA = table_SCHEMA_IN 
    AND TABLE_NAME = table_name_IN 
    AND COLUMN_NAME = field_name_IN
)$$

CREATE PROCEDURE  intellif_base.addFieldIfNotExists (
   IN database_name_IN VARCHAR(100)
    ,IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
    , IN field_definition_IN VARCHAR(100)
)
BEGIN

    SET @isFieldThere =  intellif_base.isFieldExisting(database_name_IN,table_name_IN, field_name_IN);
    IF (@isFieldThere = 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', database_name_IN);
		SET @ddl = CONCAT(@ddl,'.',table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'ADD COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_definition_IN);


        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END $$
DELIMITER ;


CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'liveurl', 'VARCHAR(255)');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_task_info', 'beforesrv_id', 'BIGINT');

ALTER table intellif_base.t_task_info drop beforesrv_id;
ALTER table intellif_base.t_task_info add column beforesrv_id BIGINT not null DEFAULT 0;

INSERT INTO intellif_base.t_police_man_authority_type
SELECT '1', '1', '动态检索、布控'
FROM dual
WHERE not exists (select * from  intellif_base.t_police_man_authority_type
where t_police_man_authority_type.id = 1);

REPLACE INTO intellif_base.t_audit_log_type VALUES ('16', '16','身份查询');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('17', '17','人脸检索');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('18', '18','布控人员');
alter table intellif_base.t_police_cloud_audit_log modify police_id varchar(255);
UPDATE intellif_base.t_alarm_info SET status = 0 WHERE status IS NULL;
alter table intellif_base.t_alarm_info modify status int(11) not null  DEFAULT '0';
UPDATE intellif_base.t_alarm_info SET send = 0 WHERE send IS NULL;
alter table intellif_base.t_alarm_info modify send int(11) not null  DEFAULT '0';

CALL intellif_base.addFieldIfNotExists('intellif_base','t_black_bank', 'create_user', 'VARCHAR(255)');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_red', 'sex', 'VARCHAR(255)');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_red', 'remarks', 'VARCHAR(255)');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_red', 'face_url', 'VARCHAR(255)');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_red_detail', 'created', 'datetime');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_red_detail', 'updated', 'datetime');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_detail', 'type', 'int(11)');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_black_bank', 'list_type', 'int(11)');
UPDATE intellif_base.t_person_detail SET type = 0 WHERE type IS NULL;
alter table intellif_base.t_person_detail modify type int(11) not null  DEFAULT '0';
UPDATE intellif_base.t_black_bank SET list_type = 0 WHERE list_type IS NULL;
alter table intellif_base.t_black_bank modify list_type int(11) not null  DEFAULT '0';
alter table intellif_base.t_person_red default character set utf8;

INSERT INTO intellif_base.t_red_switch
SELECT 1,0 ,'2016-11-18 00:00:00','2016-11-18 00:00:00'
FROM dual
WHERE not exists (select * from intellif_base.t_red_switch
where intellif_base.t_red_switch.id = 1);


DELIMITER $$
DROP PROCEDURE IF EXISTS intellif_base.deleteFieldIfExists 
$$
CREATE PROCEDURE intellif_base.deleteFieldIfExists (
    IN database_name_IN VARCHAR(100)
    ,IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
)
BEGIN

   
    SET @isFieldThere = intellif_base.isFieldExisting(database_name_IN,table_name_IN, field_name_IN);
    IF (@isFieldThere != 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'DROP COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END;
$$
DELIMITER ;

CALL intellif_base.deleteFieldIfExists('intellif_base','t_red_detail', 'real_name');
CALL intellif_base.deleteFieldIfExists('intellif_base','t_red_detail', 'image_url');

DROP PROCEDURE IF EXISTS intellif_base.add_index; 
DELIMITER $$
CREATE PROCEDURE intellif_base.add_index(IN dbname varchar(100), IN tablename varchar(100), IN indexname varchar(100), IN columnname varchar(100))
BEGIN
	
	set @exist := (SELECT count(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = tablename AND INDEX_NAME = indexname AND TABLE_SCHEMA = database());
	set @sqlstmt := if(@exist > 0, 'SELECT ''INFO: Index already exists.''', CONCAT('ALTER TABLE ', dbname, '.', tablename, ' ADD INDEX ', indexname, ' (', columnname, ')'));
	prepare stmt from @sqlstmt;
	execute stmt;
	
END$$ 
DELIMITER ;

CALL intellif_base.add_index('intellif_base', 't_crime_alarm_info', 't_crime_alarm_info_confidence', 'confidence');
CALL intellif_base.add_index('intellif_base', 't_person_detail', 't_person_detail_bank_id', 'bank_id');


CREATE TABLE IF NOT EXISTS intellif_base.t_iface_com_conf(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `con_key` varchar(255) NOT NULL,
  `con_value` int(11) NOT NULL,
  `brief` varchar(255) DEFAULT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO intellif_base.t_iface_com_conf
SELECT 1,'red_switch',0,'红名单开关','2016-12-03 00:00:00','2016-12-03 00:00:00'
FROM dual
WHERE not exists (select * from intellif_base.t_iface_com_conf
where intellif_base.t_iface_com_conf.id = 1);

DROP TABLE IF EXISTS intellif_base.t_red_switch;
 
 

REPLACE INTO intellif_base.t_audit_log_type VALUES ('19', '1000', '白名单移库');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('20', '1001', '白名单创建');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('21', '1002', '白名单过期');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('22', '1003', '白名单布控');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('23', '1004', '白名单删除');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('24', '1005', '白名单更新');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('25', '2001', '红名单创建');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('26', '2002', '红名单更新');
REPLACE INTO intellif_base.t_audit_log_type VALUES ('27', '2003', '红名单删除');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_black_detail', 'indexed', 'int(11)');
UPDATE intellif_base.t_black_detail SET indexed = 0 WHERE indexed IS NULL;
alter table intellif_base.t_black_detail modify indexed int(11) not null  DEFAULT '0';
CALL intellif_base.add_index('intellif_base', 't_black_detail', 't_black_detail_indexed', 'indexed');


CALL intellif_base.add_index('intellif_base', 't_camera_blackdetail', 't_camera_blackdetail_blackdetail_id', 'blackdetail_id');
CALL intellif_base.add_index('intellif_base', 't_alarm_info', 't_alarm_info_time', 'time');

DROP PROCEDURE IF EXISTS intellif_base.delete_index; 
DELIMITER $$
CREATE PROCEDURE intellif_base.delete_index(IN dbname varchar(100), IN tablename varchar(100), IN indexname varchar(100))
BEGIN
	
	set @exist := (SELECT count(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = tablename AND INDEX_NAME = indexname);
	set @sqlstmt := if(@exist = 0, 'SELECT ''INFO: Index not exists.''', CONCAT('ALTER TABLE ', dbname, '.', tablename, ' DROP INDEX ', indexname));
	prepare stmt from @sqlstmt;
	execute stmt;
	
END$$ 
DELIMITER ;

DROP PROCEDURE IF EXISTS intellif_base.add_index; 
DELIMITER $$
CREATE PROCEDURE intellif_base.add_index(IN dbname varchar(100), IN tablename varchar(100), IN indexname varchar(100), IN columnname varchar(100))
BEGIN
	
	set @exist := (SELECT count(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = tablename AND INDEX_NAME = indexname);
	set @sqlstmt := if(@exist > 0, 'SELECT ''INFO: Index already exists.''', CONCAT('ALTER TABLE ', dbname, '.', tablename, ' ADD INDEX ', indexname, ' (', columnname, ')'));
	prepare stmt from @sqlstmt;
	execute stmt;
	
END$$ 
DELIMITER ;

CALL intellif_base.delete_index('intellif_base', 't_alarm_info', 't_alarm_info_black_id');
CALL intellif_base.add_index('intellif_base', 't_alarm_info', 't_alarm_info_blackid', 'black_id');
CALL intellif_base.delete_index('intellif_base', 't_alarm_info', 't_alarm_info_task_id');
CALL intellif_base.add_index('intellif_base', 't_alarm_info', 't_alarm_info_taskid', 'task_id');
CALL intellif_base.delete_index('intellif_base', 't_alarm_info', 't_alarm_info_black_id_time');
CALL intellif_base.delete_index('intellif_base', 't_alarm_info', 't_alarm_info_confidence');
CALL intellif_base.delete_index('intellif_base', 't_black_detail', 't_black_detail_id');
CALL intellif_base.delete_index('intellif_base', 't_black_detail', 't_black_detail_from_person_id');
CALL intellif_base.add_index('intellif_base', 't_black_detail', 't_black_detail_frompersonid', 'from_person_id');
CALL intellif_base.delete_index('intellif_base', 't_camera_blackdetail', 't_camera_blackdetail_camera_id');
CALL intellif_base.delete_index('intellif_base', 't_crime_sec_type', 't_crime_sec_type_fri_id');
CALL intellif_base.delete_index('intellif_base', 't_person_detail', 't_person_detail_id');
CALL intellif_base.delete_index('intellif_base', 't_crime_alarm_info', 't_crime_alarm_info_time_crime_person_id');
CALL intellif_base.delete_index('intellif_base', 't_crime_alarm_info', 't_crime_alarm_info_crime_person_id');
CALL intellif_base.add_index('intellif_base', 't_crime_alarm_info', 't_alarm_info_crime_person_id', 'crime_person_id');
CALL intellif_base.add_index('intellif_base', 't_crime_alarm_info', 't_alarm_info_camera_id', 'camera_id');
CALL intellif_base.delete_index('intellif_base', 't_crime_alarm_info', 't_crime_alarm_info_time');
CALL intellif_base.add_index('intellif_base', 't_crime_alarm_info', 't_alarm_info_time', 'time');
CALL intellif_base.delete_index('intellif_base', 't_task_info', 't_task_info_source_id');
CALL intellif_base.add_index('intellif_base', 't_task_info', 't_task_info', 'source_id');
CALL intellif_base.delete_index('intellif_base', 't_task_info', 't_task_info_id');
CALL intellif_base.delete_index('intellif_base', 't_audit_log', 't_audit_log_object_object_id');
CALL intellif_base.add_index('intellif_base', 't_audit_log', 't_audit_log_object_objectid', 'object, object_id');


DELIMITER $$
DROP PROCEDURE IF EXISTS intellif_base.addFieldIfNotExists $$
CREATE PROCEDURE intellif_base.addFieldIfNotExists (
   IN database_name_IN VARCHAR(100)
    ,IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
    , IN field_definition_IN VARCHAR(100)
    , IN field_Default_IN VARCHAR(100)
)
BEGIN

    SET @isFieldThere = intellif_base.isFieldExisting(database_name_IN,table_name_IN, field_name_IN);
    IF (@isFieldThere = 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', database_name_IN);
		SET @ddl = CONCAT(@ddl,'.',table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'ADD COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_definition_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_Default_IN);


        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END $$
DELIMITER ;


DROP PROCEDURE IF EXISTS intellif_base.updateFieldValues; 
DELIMITER $$
CREATE PROCEDURE intellif_base.updateFieldValues(IN dbname varchar(100), IN tablename varchar(100), IN fieldname varchar(100),IN fieldvalue varchar(100),IN whereparam varchar(100))
BEGIN
	
	 SET @isFieldThere = isFieldExisting(dbname,tablename, fieldname);
    IF (@isFieldThere != 0) THEN

        SET @ddl = CONCAT('UPDATE ', dbname);
		    SET @ddl = CONCAT(@ddl,'.',tablename);
        SET @ddl = CONCAT(@ddl, ' ', 'SET') ;
        SET @ddl = CONCAT(@ddl, ' ', fieldname);
        SET @ddl = CONCAT(@ddl, ' ', '= ');
        SET @ddl = CONCAT(@ddl, ' ', fieldvalue);
        SET @ddl = CONCAT(@ddl, ' ', whereparam);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;
	
END$$ 
DELIMITER ;

DROP PROCEDURE IF EXISTS intellif_base.updateFieldAttribute; 
DELIMITER $$
CREATE PROCEDURE intellif_base.updateFieldAttribute(IN dbname varchar(100), IN tablename varchar(100), IN fieldname varchar(100),IN attparam varchar(100))
BEGIN
	
	 SET @isFieldThere = isFieldExisting(dbname,tablename, fieldname);
    IF (@isFieldThere != 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', dbname);
		    SET @ddl = CONCAT(@ddl,'.',tablename);
        SET @ddl = CONCAT(@ddl, ' ', 'MODIFY') ;
        SET @ddl = CONCAT(@ddl, ' ', fieldname);
        SET @ddl = CONCAT(@ddl, ' ', attparam);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;
	
END$$ 
DELIMITER ;


update intellif_base.t_role set res_ids =  CONCAT(res_ids, ',162') where id = 1; 


CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'starttime', 'datetime','not null default "1970-01-01 00:00:00"');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'endtime', 'datetime','not null default "2050-01-01 00:00:00"');



CALL intellif_base.addFieldIfNotExists('intellif_base','t_audit_log', 'fri_detail', 'varchar(255)','not null default ""');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_audit_log', 'sec_detail', 'varchar(255)','not null default ""');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_police_cloud_audit_log', 'fri_detail', 'varchar(255)','not null default ""');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_police_cloud_audit_log', 'sec_detail', 'varchar(255)','not null default ""');

call intellif_base.updateFieldAttribute('intellif_base','t_role','res_ids','varchar(1024)');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_black_bank', 'url', 'varchar(255)','not null default 0');
CALL intellif_base.add_index('intellif_base', 't_black_detail', 't_bank_id', 'bank_id');


CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_detail', 'is_urgent', 'int(20)','not null default 0');

ALTER TABLE intellif_face.t_tables ADD unique(`table_name`);

CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'parameter', 'varchar(1024)','not null default 0');
call intellif_base.updateFieldAttribute('intellif_base','t_trans_proxy_topology','source_channel','int(20)');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'code', 'varchar(256)','not null default 0');

delimiter $$
drop procedure if EXISTS intellif_base.divide_table$$
create procedure intellif_base.divide_table(in start_time datetime,in end_time datetime,in dayNum int)
begin
        declare mid_start_time datetime;
        declare mid_end_time datetime;
        set mid_start_time=start_time;
        set mid_end_time=date_add(start_time, interval dayNum day);
        set @num := 1;
        lab: while mid_end_time < end_time do
    
    set @face_table = concat('intellif_face.t_face_',@num); 
    set @image_table = concat('intellif_face.t_image_',@num); 
       
    set @csql = concat("create table if not exists ",@face_table ,
   "(
  `id` bigint(64) NOT NULL DEFAULT 0,
  `accessories` int(11) NOT NULL COMMENT '穿戴',
  `race` int(11) NOT NULL COMMENT '种族',
  `age` int(11) NOT NULL COMMENT '年龄',
  `face_feature` longblob,
  `from_image_id` bigint(64) DEFAULT NULL,
  `from_person_id` bigint(20) DEFAULT NULL,
  `from_video_id` bigint(20) DEFAULT NULL,
  `gender` int(11) NOT NULL COMMENT '性别',
  `image_data` varchar(255) DEFAULT NULL,
  `indexed` int(11) NOT NULL DEFAULT 0,
  `source_id` bigint(20) NOT NULL,
  `source_type` int(11) NOT NULL,
  `time` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `json` varchar(255) DEFAULT NULL,
  `sequence` bigint(64) NOT NULL AUTO_INCREMENT,
  `quality` int(11) NOT NULL default 0,
  PRIMARY KEY (`sequence`),
  KEY `t_face_id` (`id`),
  KEY `t_face_source_id_time` (`source_id`,`time`),
  KEY `t_face_time_source_id` (`time`,`source_id`),
  KEY `t_face_time` (`time`),
  KEY `t_face_race` (`race`,`time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

set @csql1 = concat("create table if not exists ",@image_table , "(
  `id` bigint(64) NOT NULL default 0,
  `face_uri` varchar(255) DEFAULT NULL,
  `faces` int(11) NOT NULL DEFAULT 0,
  `time` datetime DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `sequence` bigint(64) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`sequence`),
  KEY `t_image_info_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;");

PREPARE create_stmt from @csql;  
  EXECUTE create_stmt;  
PREPARE create_stmt1 from @csql1;  
  EXECUTE create_stmt1;  
INSERT INTO intellif_face.t_tables VALUES (null, mid_start_time, mid_end_time, concat('t_face_',@num), @num, 't_face', '0');
INSERT INTO intellif_face.t_tables VALUES (null, mid_start_time, mid_end_time, concat('t_image_',@num), @num, 't_image', '0');

set @num = @num+1;
set mid_start_time=mid_end_time;
set mid_end_time=date_add(mid_end_time, interval dayNum day);
 end while lab;
end $$
delimiter ;


DROP PROCEDURE IF EXISTS intellif_base.addSequence;
delimiter $$
create procedure intellif_base.addSequence(In tableName VARCHAR(255))
begin
     
    set @count = (select count(1) from intellif_face.t_tables);
      set @start = 1;
         lab: while @start <= @count/2 do
        set @faceName = concat(tableName,@start); 
         set @face_table = concat('intellif_face.',@faceName);
       SET @isFieldThere = isFieldExisting('intellif_face',@faceName, 'sequence');
       IF (@isFieldThere = 0) THEN    
        
        #判断不存在sequence才添加

    set @csql = concat("alter table ",@face_table,
   " change id id BIGINT(64);");
   set @csql1 = concat("alter table ",@face_table,
   " drop primary key;");
   set @csql2 = concat("alter table ",@face_table,
    " AUTO_INCREMENT=1;");
   set @csql3 = concat("alter table ",@face_table,
   " add column sequence BIGINT(64) not null auto_increment primary key;");



PREPARE create_stmt from @csql;  
  EXECUTE create_stmt;  
DEALLOCATE PREPARE create_stmt;
PREPARE create_stmt1 from @csql1;  
  EXECUTE create_stmt1;  
DEALLOCATE PREPARE create_stmt1;
  PREPARE create_stmt2 from @csql2;  
  EXECUTE create_stmt2;  
DEALLOCATE PREPARE create_stmt2;
 PREPARE create_stmt3 from @csql3;  
  EXECUTE create_stmt3;  
DEALLOCATE PREPARE create_stmt3;
        END IF;
 set @start = @start +1;
          end while lab;
     
 
end$$
delimiter ;



call intellif_base.divide_table(date_add(curdate(), interval -60 day),date_add(curdate(), interval 60 day),10);


CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'district_id', 'BIGINT(20)', 'not null default 0');

call intellif_base.updateFieldAttribute('intellif_base','t_area','id','int(64)');
call intellif_base.updateFieldAttribute('intellif_base','t_camera_info','id','int(64)');

call intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'parameter', 'VARCHAR(1024)', 'not null default 0');
call intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'code', 'VARCHAR(256)', 'not null default 0');

 INSERT INTO intellif_base.t_system_switch (switch_type, opened, created)
  SELECT 'AREA_AUTHORIZE', false, now() 
  FROM dual
  WHERE not exists (select * from intellif_base.t_system_switch 
  where switch_type = 'AREA_AUTHORIZE');

CALL intellif_base.add_index('intellif_base', 't_alarm_info', 't_alarm_info_task_id_confidence_black_id_time', 'task_id, confidence, black_id, time');


DROP PROCEDURE IF EXISTS intellif_base.addSequence;
delimiter $$
create procedure intellif_base.addSequence(In tableName VARCHAR(255))
begin
     
    set @count = (select count(1) from intellif_face.t_tables);
      set @start = 1;
         lab: while @start <= @count/2 do
        set @faceName = concat(tableName,@start); 
         set @face_table = concat('intellif_face.',@faceName);
       SET @isFieldThere = isFieldExisting('intellif_face',@faceName, 'sequence');
       IF (@isFieldThere = 0) THEN    
        
        #判断不存在sequence才添加

    set @csql = concat("alter table ",@face_table,
   " change id id BIGINT(64);");
   set @csql1 = concat("alter table ",@face_table,
   " drop primary key;");
   set @csql2 = concat("alter table ",@face_table,
    " AUTO_INCREMENT=1;");
   set @csql3 = concat("alter table ",@face_table,
   " add column sequence BIGINT(64) not null auto_increment primary key;");



PREPARE create_stmt from @csql;  
  EXECUTE create_stmt;  
DEALLOCATE PREPARE create_stmt;
PREPARE create_stmt1 from @csql1;  
  EXECUTE create_stmt1;  
DEALLOCATE PREPARE create_stmt1;
  PREPARE create_stmt2 from @csql2;  
  EXECUTE create_stmt2;  
DEALLOCATE PREPARE create_stmt2;
 PREPARE create_stmt3 from @csql3;  
  EXECUTE create_stmt3;  
DEALLOCATE PREPARE create_stmt3;
        END IF;
 set @start = @start +1;
          end while lab;
     
 
end$$
delimiter ;



DROP PROCEDURE IF EXISTS intellif_base.dropDivideTmpTables;
delimiter $$
create procedure intellif_base.dropDivideTmpTables(In tableName VARCHAR(255))
begin
    #获取face或image表字段集合
    set @count = (select count(1) from intellif_face.t_tables);
    set @start = 1;
       lab: while @start <= @count/2 do
    set @faceName = concat(tableName,@start); 
    set @face_table = concat('intellif_face.',@faceName);      
     set @dropCsql = concat('DROP TABLE IF EXISTS ',@face_table,'tmp'); 
  PREPARE drop_stmt from @dropCsql;  
  EXECUTE drop_stmt;   
  DEALLOCATE PREPARE drop_stmt;

 set @start = @start +1;
          end while lab;

end$$
delimiter ;


update intellif_base.t_camera_info set code = '' where code = '0';
call intellif_base.updateFieldAttribute('intellif_base','t_camera_info','code',' VARCHAR(256) NOT NULL DEFAULT \'\'');




DROP PROCEDURE IF EXISTS intellif_base.dividedFaceImagesAddField;
delimiter $$
create procedure intellif_base.dividedFaceImagesAddField(IN tCount int,IN tableName VARCHAR(255), IN field_name_IN VARCHAR(100),IN field_definition_IN VARCHAR(100),IN field_Default_IN VARCHAR(100))
begin
     

  DECLARE  field  varchar(255); 
  DECLARE done INT DEFAULT FALSE;
  DECLARE cur_account CURSOR FOR select COLUMN_NAME from information_schema.COLUMNS where TABLE_SCHEMA = 'intellif_face' and table_name = concat(tableName,tCount);
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
 
    set @fieldStr = '';
    OPEN  cur_account;     
    read_loop: LOOP
            FETCH  NEXT from cur_account INTO field;       
            IF done THEN
                LEAVE read_loop;
             END IF;
     SET @fieldStr = CONCAT(@fieldStr,field,',');
    END LOOP;
    SET @fieldStr = left(@fieldStr, LENGTH(@fieldStr)-1);
    CLOSE cur_account;
  
    #set @count = (select count(1) from intellif_face.t_tables);
    set @start = 1;
       lab: while @start <= tCount do
    set @faceName = concat(tableName,@start); 
    set @face_table = concat('intellif_face.',@faceName);
    set @faceNameTmp = concat(@faceName,'_tmp');
    set @face_tableTmp = concat('intellif_face.',@faceNameTmp);
    SET @isFieldThere = isFieldExisting('intellif_face',@faceName, field_name_IN);

    IF (@isFieldThere = 0) THEN          
     set @createFaceCsql = concat("CREATE TABLE IF NOT EXISTS ",@face_tableTmp," like ",@face_table);
        
     set @insertCsql = concat("insert into ",@face_tableTmp,"(");
     set @insertCsql = concat(@insertCsql,@fieldStr);
     set @insertCsql = concat(@insertCsql,") select ");         
     set @insertCsql = concat(@insertCsql,@fieldStr);
     set @insertCsql = concat(@insertCsql," from ");   
     set @insertCsql = concat(@insertCsql,@face_table);  
        
     set @dropCsql = concat('DROP TABLE IF EXISTS ',@face_table,'tmp');
     set @rename1 = concat("alter table ",@face_table," rename to ",@face_table,"tmp");  
     set @rename2 = concat("alter table ",@face_tableTmp," rename to ",@face_table);  
 
  
  PREPARE create_stmt from @createFaceCsql;  
  EXECUTE create_stmt;  
  DEALLOCATE PREPARE create_stmt;

 
  call intellif_base.addFieldIfNotExists('intellif_face',@faceNameTmp, field_name_IN,field_definition_IN,field_Default_IN);
  
 
  PREPARE insert_stmt from @insertCsql;  
  EXECUTE insert_stmt;  
  DEALLOCATE PREPARE insert_stmt;

  PREPARE drop_stmt from @dropCsql;  
  EXECUTE drop_stmt;   
  DEALLOCATE PREPARE drop_stmt;
  
  PREPARE rename_stmt1 from @rename1;  
  EXECUTE rename_stmt1;  
  DEALLOCATE PREPARE rename_stmt1;
  PREPARE rename_stmt2 from @rename2;  
  EXECUTE rename_stmt2;  
  DEALLOCATE PREPARE rename_stmt2;
        END IF;
 set @start = @start +1;
          end while lab;
     
 
end$$
delimiter ;


DELIMITER $$
DROP PROCEDURE IF EXISTS intellif_base.dividedTablesAddTables
$$

DROP FUNCTION IF EXISTS intellif_base.countDivideTables
$$

CREATE FUNCTION intellif_base.countDivideTables() 
RETURNS INT
RETURN (
   select count(1) from intellif_face.t_tables
)
$$

CREATE PROCEDURE intellif_base.dividedTablesAddTables(IN tableName VARCHAR(255), IN field_name_IN VARCHAR(100),IN field_definition_IN VARCHAR(100),IN field_Default_IN VARCHAR(100))
BEGIN


     SET @count = intellif_base.countDivideTables();
     call intellif_base.dividedFaceImagesAddField(@count/2,tableName,field_name_IN,field_definition_IN,field_Default_IN);

END;
$$
DELIMITER ;

call intellif_base.dividedTablesAddTables('t_face_','quality','int(11)','not null default 0');

REPLACE INTO intellif_base.t_police_man_authority_type VALUES ('1', '1', '人脸检索权限');
REPLACE INTO intellif_base.t_police_man_authority_type VALUES ('2', '2', '布控人员权限');
REPLACE INTO intellif_base.t_police_man_authority_type VALUES ('3', '3', '身份查询权限');

call intellif_base.updateFieldAttribute('intellif_base','t_camera_info','id','bigint(64) NOT NULL AUTO_INCREMENT');
call intellif_base.updateFieldAttribute('intellif_base','t_area','id','bigint(64) NOT NULL AUTO_INCREMENT');


REPLACE INTO intellif_base.t_search_result_code VALUES ('1', '0', '成功');
REPLACE INTO intellif_base.t_search_result_code VALUES ('2', '1', '图片为空或格式错误');
REPLACE INTO intellif_base.t_search_result_code VALUES ('3', '2', '图片无脸人');


INSERT INTO intellif_base.t_table_version (`db_name`, table_name, update_version)
  SELECT 'intellif_base', 't_district', 0  
  FROM dual
  WHERE not exists (select * from intellif_base.t_table_version where `db_name` = 'intellif_base' and table_name = 't_district');

INSERT INTO intellif_base.t_table_version (`db_name`, table_name, update_version)
  SELECT 'intellif_base', 't_area', 0 
  FROM dual
  WHERE not exists (select * from intellif_base.t_table_version where `db_name` = 'intellif_base' and table_name = 't_area');

INSERT INTO intellif_base.t_table_version (`db_name`, table_name, update_version)
  SELECT 'intellif_base', 't_camera_info', 0 
  FROM dual
  WHERE not exists (select * from intellif_base.t_table_version where `db_name` = 'intellif_base' and table_name = 't_camera_info');


CALL intellif_base.deleteFieldIfExists('intellif_base','t_user_camera', 'creator');


CALL intellif_base.addFieldIfNotExists('intellif_base','t_district', 'parent_id', 'bigint(64)', 'NOT NULL DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'parent_id', 'bigint(64)', 'NOT NULL DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'node_id', 'bigint(64)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'area_code', 'varchar(1024)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'parent_id', 'bigint(64)', 'NOT NULL DEFAULT 0');


CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'camera_code', 'varchar(1024)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'district_id', 'bigint(64)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'node_id', 'bigint(64)', 'DEFAULT NULL');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'user_name', 'varchar(255)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'password', 'varchar(255)', 'DEFAULT NULL');


delimiter $$ 

DROP TRIGGER IF EXISTS intellif_base.district_insert $$
CREATE TRIGGER intellif_base.district_insert  
AFTER INSERT ON intellif_base.t_district 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_district';
END $$

DROP TRIGGER IF EXISTS intellif_base.district_update $$
CREATE TRIGGER intellif_base.district_update  
AFTER UPDATE ON intellif_base.t_district 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_district';
END $$

DROP TRIGGER IF EXISTS intellif_base.district_delete $$
CREATE TRIGGER intellif_base.district_delete  
AFTER DELETE ON intellif_base.t_district 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_district';
END $$


DROP TRIGGER IF EXISTS intellif_base.area_insert $$
CREATE TRIGGER intellif_base.area_insert  
AFTER INSERT ON intellif_base.t_area 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_area';
END $$

DROP TRIGGER IF EXISTS intellif_base.area_update $$
CREATE TRIGGER intellif_base.area_update  
AFTER UPDATE ON intellif_base.t_area 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_area';
END $$

DROP TRIGGER IF EXISTS intellif_base.area_delete $$
CREATE TRIGGER intellif_base.area_delete  
AFTER DELETE ON intellif_base.t_area 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_area';
END $$


DROP TRIGGER IF EXISTS intellif_base.other_area_insert $$
CREATE TRIGGER intellif_base.other_area_insert  
AFTER INSERT ON intellif_base.t_other_area 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_area';
END $$

DROP TRIGGER IF EXISTS intellif_base.other_area_update $$
CREATE TRIGGER intellif_base.other_area_update  
AFTER UPDATE ON intellif_base.t_other_area 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_area';
END $$

DROP TRIGGER IF EXISTS intellif_base.other_area_delete $$
CREATE TRIGGER intellif_base.other_area_delete  
AFTER DELETE ON intellif_base.t_other_area 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_area';
END $$

DROP TRIGGER IF EXISTS intellif_base.camera_info_insert $$
CREATE TRIGGER intellif_base.camera_info_insert  
AFTER INSERT ON intellif_base.t_camera_info 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_camera_info';
END $$

DROP TRIGGER IF EXISTS intellif_base.camera_info_update $$
CREATE TRIGGER intellif_base.camera_info_update  
AFTER UPDATE ON intellif_base.t_camera_info 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_camera_info';
END $$

DROP TRIGGER IF EXISTS intellif_base.camera_info_delete $$
CREATE TRIGGER intellif_base.camera_info_delete  
AFTER DELETE ON intellif_base.t_camera_info 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_camera_info';
END $$

DROP TRIGGER IF EXISTS intellif_base.other_camera_insert $$
CREATE TRIGGER intellif_base.other_camera_insert  
AFTER INSERT ON intellif_base.t_other_camera 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_camera_info';
END $$

DROP TRIGGER IF EXISTS intellif_base.other_camera_update $$
CREATE TRIGGER intellif_base.other_camera_update  
AFTER UPDATE ON intellif_base.t_other_camera 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_camera_info';
END $$

DROP TRIGGER IF EXISTS intellif_base.other_camera_delete $$
CREATE TRIGGER intellif_base.other_camera_delete  
AFTER DELETE ON intellif_base.t_other_camera 
FOR EACH ROW 
BEGIN
     UPDATE intellif_base.t_table_version SET update_version = (update_version + 1) WHERE `db_name` = 'intellif_base' and table_name = 't_camera_info';
END $$

delimiter ;

DELIMITER $$
drop function if EXISTS intellif_base.calcStationUserCount$$
CREATE FUNCTION intellif_base.calcStationUserCount(stationId bigint(20))
RETURNS int
BEGIN
    set group_concat_max_len = 10240000;
    set @usercount =
    (
        select count(*) from t_user u where police_station_id in
        (
            SELECT id
            FROM intellif_base.t_police_station
            WHERE FIND_IN_SET(`ID`, (
                SELECT GROUP_CONCAT(Level SEPARATOR ',') FROM (
                    SELECT @Ids := (
                        SELECT GROUP_CONCAT(`ID` SEPARATOR ',')
                    FROM intellif_base.t_police_station
                    WHERE FIND_IN_SET(`parent_id`, @Ids)
                    ) Level
                FROM intellif_base.t_police_station
                JOIN (SELECT @Ids := stationId) temp1
                WHERE FIND_IN_SET(`parent_id`, @Ids)
            ) temp2
            ))
        ) or police_station_id = stationId
    );

    return @usercount;
END $$
DELIMITER ;

CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'type', 'int(11)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'user_name', 'varchar(255)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'password', 'varchar(255)','default null');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'camera_code', 'varchar(1024)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'district_id', 'bigint(20)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'node_id', 'bigint(20)','not null default 0');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'district_id', 'bigint(20)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'node_id', 'bigint(20)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'area_code', 'varchar(1024)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'parent_id', 'bigint(20)','not null default 0');


DELETE FROM intellif_base.t_resource;

REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (1, '数据共享', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (100, '布控功能', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (101, '布控功能(仅查看)', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (200, '高级管理', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (201, '高级管理(分局)', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (202, '高级管理(派出所)', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (300, '数据挖掘', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (400, '数据统计', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (500, '报警导出', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (600, '离线布控', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (700, '人像1:1比对', '', '');
REPLACE INTO intellif_base.t_resource (id, cn_name, scopes, uri) values (800, '通用', '', '');

DELETE FROM intellif_base.t_role_resource;
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (1, 'SUPER_ADMIN', 1, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (2, 'SUPER_ADMIN', 100, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (4, 'SUPER_ADMIN', 200, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (7, 'SUPER_ADMIN', 300, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (8, 'SUPER_ADMIN', 400, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (9, 'SUPER_ADMIN', 500, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (10, 'SUPER_ADMIN', 600, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (11, 'SUPER_ADMIN', 700, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (12, 'SUPER_ADMIN', 800, true, false);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (13, 'MIDDLE_ADMIN', 1, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (14, 'MIDDLE_ADMIN', 100, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (16, 'MIDDLE_ADMIN', 201, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (18, 'MIDDLE_ADMIN', 300, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (19, 'MIDDLE_ADMIN', 400, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (20, 'MIDDLE_ADMIN', 500, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (21, 'MIDDLE_ADMIN', 600, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (22, 'MIDDLE_ADMIN', 700, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (23, 'MIDDLE_ADMIN', 800, false, false);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (24, 'ADMIN', 1, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (25, 'ADMIN', 100, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (27, 'ADMIN', 202, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (28, 'ADMIN', 300, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (29, 'ADMIN', 400, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (30, 'ADMIN', 500, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (31, 'ADMIN', 600, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (32, 'ADMIN', 700, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (33, 'ADMIN', 800, false, false);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (34, 'USER', 1, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (35, 'USER', 100, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (37, 'USER', 300, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (38, 'USER', 400, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (39, 'USER', 500, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (40, 'USER', 600, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (41, 'USER', 700, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (42, 'USER', 800, false, false);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (43, 'GUEST', 1, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (44, 'GUEST', 101, true, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (45, 'GUEST', 300, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (46, 'GUEST', 400, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (47, 'GUEST', 500, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (48, 'GUEST', 600, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (49, 'GUEST', 700, false, true);
REPLACE INTO intellif_base.t_role_resource (id, role_name, resource_id, must, display) values (50, 'GUEST', 800, false, false);

CALL intellif_base.addFieldIfNotExists('intellif_base','t_police_station', 'parent_id', 'bigint(20)', 'NOT NULL DEFAULT 0 COMMENT \'父id\'');


update intellif_base.t_role set res_ids = '1,100,200,300,400,500,600,700,800' where id = 1;
update intellif_base.t_role set res_ids = '1,100,202,300,400,500,600,700,800' where id = 2;
update intellif_base.t_role set res_ids = '1,100,300,400,500,600,700,800' where id = 3;
update intellif_base.t_role set res_ids = '1,101,300,400,500,600,700,800' where id = 4;


CALL intellif_base.addFieldIfNotExists('intellif_base','t_police_station', 'special_total_num', 'int(11)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_police_station', 'special_use_num', 'int(11)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_police_station', 'user_count', 'int(11)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'special_sign', 'int(11)','not null default 0');


CALL intellif_base.addFieldIfNotExists('intellif_base','t_district', 'sort', 'int(11)','not null default 0');


CALL intellif_base.addFieldIfNotExists('intellif_base','t_red_detail', 'version', 'bigint(20)','NOT NULL default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_red_detail', 'json', 'varchar(255)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_black_detail', 'json', 'varchar(255)','default null ');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'def_alg', 'bigint(20)','NOT NULL default 0');

call intellif_base.updateFieldAttribute('intellif_base','t_audit_log','message','varchar(1024)');
 
update intellif_base.t_police_station s set s.user_count = calcStationUserCount(s.id);

REPLACE INTO intellif_base.t_chd_configure_time VALUES ('1', '00:00:00', '23:59:59', 'DAY');


CREATE TABLE IF NOT EXISTS intellif_base.t_filter_face(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `feature` longblob NOT NULL COMMENT '?????',
  `kind` enum('DOG','CHILD','PROFILE','BLUR','TYRE','OTHER','DARK') NOT NULL DEFAULT 'OTHER' COMMENT '??',
  `threshold` float NOT NULL DEFAULT '0.92' COMMENT '????',
  `num` int(11) NOT NULL DEFAULT '1' COMMENT '??',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='?????';


REPLACE INTO intellif_base.t_filter_face VALUES ('1', 0xDFEF65004069A6BCDA205FBB8CBA0FBC188EA33CA12AF6BC6034B4BDFF49AEBDC75792BDCE2EE1BD07D389BBB66F293DC5E7983CC3DC4DBD76E365BC93114B3DF35405BE2B39B03CCD01373CF6F4D6BD1A0053BD35C19DBCD86F52BDAAE8AE3C50939FBD16BBA0BC7E1DAABC2678D4BDFD29B8BD9AE62ABD77104ABB6BE5E7BD5C3232BDB8D30EBEDBD0813B68F3C53DB4F9423C0D23213DADD712BC77AAC4BDC6FE8EBD1B8B403DBDEFBCBB188191BDC4E1613EE4C442BD242F623A829963BD660836BD63CDB43D0BE333BDA0B3063DF8802BBCC79842BD7AF4CDBD99FE803B364F99BCDB4557BD2F36CABD06AA3ABD58F197BB16E83ABDF4B6003C824B07BB7B6FE43DDA49713DB03287BB0E3EE83C8AEB92BD50423B3E13631F3DCB6BC4BD3DE6FEBD1D62EE3D68D6C03D373A3B3DC9A5B03CA7A16DBD875FA9BC4021D9BC3CD58DBD76CC413D66086D3D33398B3CB9BB0A3E6AF14ABDA37DCE3D54FA9FBDCD25913D858CCCBD03C8503DC0CECABD70BE97BD2F4018BEADCA2FBC1CAFE23B96D958BDDB57023E41B3D0BD003EB23D0438243DA22F683DB530DEBDA4DBE4BCCA201BBDC6C6DF3C9FDB71BDAAA0C6BB493586BC5E543D3DE25B6CBD8D8B09BD2257BCBDC5DF89BCD3C1F83DB1A7BFBD511157BC17B0033E625B583D83F6ECBB531FDCBD8E5AE73CECD61F3D5942113D432EB5BDD8E2B9BC16CC5BBDF6815D3D2937853D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '958');
REPLACE INTO intellif_base.t_filter_face VALUES ('2', 0xB2EF65004D05D5BD1A286ABD04F328BD83B12DBCF8BC0DBEA700593DDDE2193D0E1283BD0F88443C6CB64DBDD2E13EBC32991CBEC8766DBD4B64C3BD9DBB853D54802B3DAC17663DF20AD33CC0BE76BD4AE5FEBD68459C3DF0D585BCD377973DA7426FBD0D25A4BD15CC72BD979EA6BDE10D0F3DA340BBBB9976C6BD44A1CBBDDBB99B3DE41CD2BD57A2C4BC7DF9453C4AF0BDBD3FD4483C30F980BD4B2E703C1418F0BBFC02083EA1F3E13D3F05E3BCD7721F3D358B36BDD2C0293D9CE8ACBD1D27BF3CE19DF03DBF5B5B3CC100BDBCF60F16BD5D5178BD064913BD1626193C4D12303CD73FA33D049440BEB27BB7BDA2C3ADBC4981823D0441B93DED87D43D3D6FA73D2EDDB2BD9312BD3D4AF8B43D61799EBDAE3E92BCEE5822BDE05E9BBB1C6CDF3C9D014ABDCBBB953DC9C911BD80B478BDB5A0A43D1EAD29BC0E7CEA3C8B13C2BCCD3382BDFD0C6A3D2D07063E48D7833DB5A135BE93EEA43DD5C6B83CB00C09BEE1F1BCBC898B8A3BE9EA1EBE8632AA3B4E5968BCF7B9ECBD8ADA10BC35E886BDB17E323E245C11BEDAF9933DADBE4E3E7BFB67BD21CE2DBECB1745BD78DA64BD329331BD605308BC41580DBDE9A369BB6CA6AA3D8BE7DCBD66D5BCBCDA45A1BCB5509FBDE4BE94BCCBE43DBD2429873D5AA10EBC1AFD423D5C65773D4451DD3B0E7D5DBDB23DA53D9DE1743D33848EBC759258BDE200AD3CE33114BD0493993B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'TYRE', '0.9', '204');
REPLACE INTO intellif_base.t_filter_face VALUES ('3', 0xE5EF65000C4529BD367780BDDB042F3AA624683BB2C527BD6A19803DC74A493DAC0FF2BDAF59983C1CA484BDAFE1BC3DC2298ABD809F3CBD860033BD101CCA3D66B81B3D7AE58A3D27607E3D14F6013CA6D195BD0CB7BE3BDBA6253C095A863DB1A04FBC1E25AABDD49B57BC1820DFB9EB719EBBC6008BBC2F830DBE81A937BD3F8190BB3A24BFBD8D2001BD34D4793C24A9C0BDD5D25D3D5BC27FBDB6E7303E881101BDEDEF903D397AF53C5E10DFBD9E290E3E4AF63CBBFEB38A3CC5C292BC265153BDFC439D3D20854CBD7F2B093D7182683C03ACFF3C7A78C4BD5FEEEDBCD8BF703CF9C9583D366999BD4678CDBD997D8BBCEBA7153D6D53C6BCD2F86ABCB23DBE3D153A28BD20E4A03D582BA63D29D601BEFDA01E3D31EEB5BD21A51139FE4BA2BD153601BD50F206BD379EFDBC1C4D05BEE4335D3D8C38573DDCCB8D3D037E3CBE14419FBC086F1C3D8F03F13A9ED568BB61B1253D3D2F2F3DF60665BC4145A8BDCDB975BB1960093D4D9325BE4FC98D3DF48103BD377B9ABDDE9BE7BBE76E4C3D05B7C43DB20B0ABE0B41C1BCC7A38E3D6BC48EBD24E188BDA24A823DE1CA443D65F1AD3C0EE84C3B32DD9EBC1BDD1DBD899E5B3AA8A50ABEA519423DD8D769BDE8D5A0BD94FB15BCF41CD3BBB205F73CE9A405BD0FD2A7BB63828CBCF4A94A3D76992E3D7B17BD3C7590A7BC97B8783D559D833DB481E83C33ADBB3D203E2B3A0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '1169');
REPLACE INTO intellif_base.t_filter_face VALUES ('4', 0xD0EF6500EFAC7DBC17C9C2BD19CC4CBC3E78553CED587ABDD046A13DDCAC9A3C77DC33BE104C333DAC68983D8E1B6B3D6CE0963BF2ED623D0E65E6BD4C19C43D298BCB3D0EA632BC8B24633DBB44E2BBA6A530BEB5E4033E53E223BD9987553C505D603CE1F1503B7435CA3C867DE5BD7794753D2C8FE63CDF58F8BD483B703C1E7905BDAF70E2BDFADB893DB7CE73BDE6721DBE77D36B3D3CAC94BDA7B508BDF56559BDA57153BDCE97E83C51C453BDBEB7FB3D6D9E563D9EC83CBD3789623D5E8E82BD6AC3243C978B60BDDE14CE3DECF1FF3CD9D0823D83F4FA3C77A28EBB0071C8BC516E893D486D53BE3023BCBCA3C125BDDC52813D53E48E3BE4C52DBB2D14C33D8330E4BD8CE34A3D5570093EA1327EBD22833CBC130EEFBC6C9DCF3DF2526FBD856112BD775C8ABC06557EBD7F4B8FBDCA4B0D3EA54545BCE44D713DA136CBBD119056BD0FE53B3D9EE95FBCD4C227BD5B88AE3C4652833D5ED7E4BBAFE53CBD8FFE2E3DE2DC983CEF5D97BDE918A4BC0C2E5A3C8F4333BDEAAC5CBDE91C5D3CEF416D3DAD73FCBC6D39493DE5DA95BD07AD323E41A7BABD5E3511BCAAF1763C7572FBBB176FF6BC1E4BB4BCA5D0DD3B576D273DD45137BE9417A23DD899AA39667C21BE3B59BF3D01BDAF3D1D779DBD225C223D145EA33DADF26FBD10710A3E01C7183D8EBC10BEA4B7F43C621882BB653B96BD372F603D6F6DA53D8C07953D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'DARK', '0.9', '656');
REPLACE INTO intellif_base.t_filter_face VALUES ('5', 0xBFEF6500FEF309BA5705FCBCDBB296BC698EF2BB8136F4BC542D50BC0C1285BDAF3897BBA65FF2BD5972493B357CD83CDA497EBD9225AFBCF9A2BCBD88F79E3B2FCD72BD60CDEABCFE121D3DC04C30BD3BF86EBD9B2FBA3CB39D073D6F543EBD31DD42BC212FBCBDCFEC6BBC8B1D5E3D3A7A2D3D8D5F12BD7F67A5BDDB1CDFBDAE71CB3CF29090BDDA701E3C2577B43BC41BD0BD5C2CA73DCB1ADABC0CFC5CBC945DFFBD2E1FE73D88EE953D04BDD6BD8539E73D1A9522BD8DC7E93D52D4B8BD4DFADD3CA5EB123D6B9BF13C6E97F8BB3256CCBD2DDF1FBEFBF44EBDE098D7BC2254DEBB81AF03BECDFFE0BDFC4EF7BCAAF7A33D36D31DBDBE95793D5477593C973C263D8A802C3D2DBB31BDB74F023E0CDE39BC9E11E8BCF1B2A53DF2E75BBD34404DBCDB87813D6D4DD13C01F6543DD23331BAA26BD8BB848F13BDADB731BD44F3D6BCFCD25D3CC8A9163CC42C493DFAB9083D734388BDCE441B3E628528BDB06E7EBC77166BBD6FD2593D1ED848BE8710943C181AA8BDEBAA293D10A737BD42D9F9BDF658E73DF7C959BDF398F93CBB9E9BBDA97F0C3E0E0F98BD46C385BCD0FBACBC5EEB923D9D18B9BDE37DBEBB172C2CBD18400C3D1EBAE9BD7840543D173BD7BD81D3E83BE55F833D79929CBD06D7E03C5ACAB63C521D10BD8E1C2C3D4664D3BCD583903D32165E3C945DEB3D4AEB49BD329F9BBD108F73BD9903593D5DED003B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '16');
REPLACE INTO intellif_base.t_filter_face VALUES ('6', 0xDAEF65001E92C4BDBDA099BD24C848BDB905FABC44DBA0BD10F3A53DB167203D0C4FD4BDD937EB3C18413EBD98D8BC3B6EC5D5BDE10731BD0AADBABDD1719C3DD9EC903D2727663D951A253D80152CBD53B708BE4911823D9B8D03BDAF88143D0FF154BD17D494BD209A86BD661A99BD811EC23C1BE107BDE59210BE32DA7FBD76053A3D3108F9BD595C66BCACA1A2BBE9E3E0BD4E1FE43C2FC14FBD9A5D893C7743DC3C185BCA3DB9B1CA3DBAE418BDD87F643DA7F767BD592D163D6F9079BD54E1963C19A7C43DE8FA273C67D1F73CACAA8CBC119D48BD059302BD339E12BC90A734BD2E5D893D1D0F5EBE992CF8BD40E0D4BB652BDF3CCFB2023DD5F7A33D2660D93D4789EEBD4C83C73D2096C93D795EA7BD797AABBC7FD868BD4B93D63CC136E4BA42BB18BD64A3253DEF34C9BC04A9B8BD19B6C83D4486643BBC5AE23CE85FF2BC899F70BDE24D1E3D0A8FA03D6467343DF4AF01BE12CEB93D9CD58F3CF3F1FBBDDB11AA3B04D0C4BC87491BBE652A3BBB0CC989BC6091D8BD0E8D793B5420C1BCD9CA0E3EBE2ED1BD0D4C983D7936103EE527803AA555F9BD089F6ABDD91817BDA9FC8DBC97089FBC163E69BDA8D65EBBED69973DDC3FF4BD7542A03BADBA57BDA435F4BDA886EC3C09C8F1BCE7E06C3DBD5D1FBC2B44413D563B2E3D2B9A393D652B42BD1E91623D06486D3DDB3DB73CA9222FBDCD3AC3BC5D4A833B84F02B3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'OTHER', '0.9', '815');
REPLACE INTO intellif_base.t_filter_face VALUES ('7', 0xBFEF6500443D123D28FE993C9DF2283DBEB21A3DFCD5CCBD1B24333BFDA73A3DB0B4C0BD8A09E2BC1E0725BCDB95EBBB561091BD6115963BCFD6C9BDDD54123D935878BD7ECF1E3DAE67943D16AA12BDBD5F17BE9E1F1B3CC7AEB1BB21A78E3B64D116BCD98812BEEAD090BA260A26BCB2E11ABDDF1BB53D19B2D6BD8CF4C2BBD14B433DCB08AABD4B92623D0168DABC23B207BEEC3C74BD1F1D67BD2EEB94BD877AA1BD3597033B3039B1BC917E25BEF16FE73D3C79983DD5303ABD291A403D2E24D83B2C19DC3DC5B3353D2E11CD3D5088373A509950BC64D491BDA11C08BB24E6673D8D1EE53CAB09EBBD933126BD50FF08BD4D4F653D4F802A3D0A22963D0486F83D1867113DD4542F3D9C3188BC9D6309BEFB34BD3B65761DBD00CF90BC346379BD003D29BDDC11BE3D31C9BCBC917BDCBC9DCFB73D2C260DBD7020E33DF869ACBDEB3FD7BC10BD2A3C66644B3D23E4DA3CACE39CBDEE4C123DA7027C3D340253BC701081BD460623BC66C32ABE281CD43CD25F5A3D1C116CBD389B86BD36ABB2BD2466413DD0DDECBD0A03C43D805ABFBCB8FE473D5D4011BE87D1103CE08B20BD1B6D583D90B7EDBCE6D9463D9307B6BCEFE5B3BCE04431BEB5FF263DC5A379BDC192C9BDE100E83D48B4CABCCA0D9ABB3B3DBB3D52B1A53B5021923D930EF2BB7B670B3D8E727ABDAC1D4DBD596C76BD43C009BD5E3C8E3D4CF5D83C7C8281BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '16');
REPLACE INTO intellif_base.t_filter_face VALUES ('8', 0xBFEF6500446A96BD3F63BB3DF3810B3DAAC30E3DBCA8D63D52E88BBD9CB911BEE144503DD686FEBDAE95713DB66C7D3D636E81BD6744053B1BC837BDAA1A07BDD99C98BDCF80303DE6D8503DBF5B15BE8F12AF3CFA3305BD641CA83CB451AEBC71ED87BCE4A4D6BD90402BBD456C22BD6C2475BAF6A5823CCBFB1DBD4B7ADBBD75C220BDE665823CFC280D3D66A7353DD2D3FABB8C84AF3DB4A2933B4765463CB9C80EBD6FCE6BBC9706D73BA459C2BD2412123E0B6BA6BD47099F3C00AA8BBDA082613C070F45BD18F710BE5D1F9BBD2E2B86BC398509BEC7C33EBDA1D27ABC4157FE3AB6ED89BDD748E0BD861E713CEF4FB0BCF1DE25BD2CF43D3DA2AB67BD46D08DBBD78E073D04F98F3D8291AA3D7B86563D9B5E9B3D3FFAE83CE7244ABD1FF31EBC176C783DC282103D10C5B93D39B040BC754E24BD90572A3C440FBEBDD077BCBD3A422EBD7C17463D38D11A3D9E0F503D81FCE03C0B20BB3D69AA77BD17497F3DA74737BDE627BBBCA29A5ABD413992BCA15186BD46BA973B81AECFBCE1537DBD3192313DB5F7B0BD410C043D2EAAD93B2EA19A3DAE2C92BBA7D6C03D49B3E4BDD6F3E63CAFFCF2BCBBCFA13DCBD59CBD0BF214BD3A63F2BD5F5521BC1A4536BDFE2CA73B85A1303E6CA98ABDBBADD93C27F590BDEBD3873D4E05713DDC3BD3BDC3340A3D6B3D30BDD441B83D723969BD06D7DEBC9B15A4BC2E186E3D4FA9BF3B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'OTHER', '0.9', '26');
REPLACE INTO intellif_base.t_filter_face VALUES ('9', 0xBFEF650055B9B53C53DD513A8325623C14D43DBDD625923C84C92DBE0E7689BD1706B0BC2D1AECBC55F0CA3CB36391BCD03149BDB3ACE43CE52CD4BDE5CA3C3D343810BE83142B3D6E30043ED4FBC43B24305DBB1476263D50E2BD3D2B0E1EBCBDC285BD584EDABC0EF080BDDC52B13DF4FD61BDA6AE013CC9F21B3D35083BBDB57CA6BDBC6C8EBDD797DE3D13E4393BC173D53C67FF1F3D2843133C5F53B6BD4079DCBD5BEEB33C08CA6C3D7877ABBDF8E54A3E01B7AFBC48F668BB5A985EBD9FA0BBBDDA0BC1BB521FB7BC88522D3C789C0CBE7144EDBD28BEFDBD6687773CECD263BDA6429CBDD0CC10BE13D8E23BA9B44DBC25D424BD97859CBC22F3A3BD174A473DBB11963CC448A2BD8F49433C37286F3DE85C0B3CE84AAC3D12A2E1BD584604BEDE6CD2BBCF7ECE3CE8DA313CB5B4ADBAC1F6EFBD3F794B3C5A49E4BA53D651BB3C998038BC49C83D3B38CE3CFEB69A3D483C9F3C4986A13D70AB5EBD182B1DBDF01049BDF211A83D802BF6BD18BCA7BD00AABFBD35AC943DE216DA3D24433ABD4173033D60F62EBDFFB294BCAA35E7BD6DB6F13D13D789BDFC507E3C9B9302BA18499B3DB45ECB3D4F72143D87E065BDB93C8E3CAC7D413B455A703C8879B6BD07F1063D5E939D3DB8A3A5BDCE68C63BA0448F3D7322CA3DA2B8D33C8C6E38BD3E75743DB45C023D5FD08A3D2CECB1BD4BDBE8BDB15677BB23ED303DADDA65BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '17');
REPLACE INTO intellif_base.t_filter_face VALUES ('10', 0xBFEF65004FEA05BE2EE467BD5CB4B73D2A5FA5BA6F8095BC4276C9BDA99FF6BD9481F43C0A129CBDCF260BBD007DFF3C41C2E4BD0207A13B82E4C83B4ECC043D0F0EADBD0F00E23D0770E83CEE2ED4BDD93E87BD599CD03C9FB1A9BDACB001BDE6A4E63B64585EBC7DD87ABC6D810CBD32F0A93CC08E99BD233BEABCF88998BD7D45433C9978A7BD9DEBB43C2A88B23D1E3B243DB50D8E3DC13AB7BDF485BD3C8C4176BDC816543D21A3AE3D1DC21ABDF24BC33DB272AA3DA0EA5C3D0D95E5BD0C77123D7C5694BC2CC0D4BC04F950BD7C664ABD2EE42CBD5815AABDD790D3BD35C8C738AE1B6EBDC79120BE4AB40C3D595B43BDCDA647BD87BB933DEBB0C43D142A243C2153763DB775D83D91C2C93D2FFA793D17C3643DDA2066BCD769EFBC127D65BB29670E3B21C19C3CE74F24BD946936BD60B11C3BF13B813DAF030ABD9F468ABD52CBB7BDDA827E3CCE5E7FBC72C1C33C4001C5BDEE82893DFDD0CEBD7C51E8BC2DC22CBCCB7C9F3D9799BCBDF8F7723C39112ABEDCD86D3D6D1F86BD1ED10ABD013AB23D7F823CBDE3F72C3DC6B2A43D6099A83C611126BD4F07043EC4320BBEC97F183DA00CE0BBE732C73C123F0BBD9965403DD1932EBE12A460BDE6E98BBD460B063D8DF9B73DB8F87DBD7ED8183A3D1AC6BC44C5E33C2E5B673DD82E98BD06AC733CF891293D291D88BC5F3EBBBC9CA407BD6A7E553D24117B3DFADA2B3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '29');
REPLACE INTO intellif_base.t_filter_face VALUES ('11', 0xC7EF6500F18D99BDA71BAABDD8AC5B3DED54BF3DEDBB7BBC8CD75F3D56F1D4BB4B7A59BDEF4DBD3D6DC4553D83E3933DDC0FE4BDC3D5573CC19467BEE2522F3D1FEB303CEA86B3BDAE61813D9B4391BD24F16DBDF407163EF836A43C75064C3DA99672BC8629CB3CC759803DBE5062BD7320ED3D7E801E3DFF5AC0BD521105BDADB714BCE566E9BD2E4D043D8382A53CCB6102BEAE68FD3B2B90C4BDC659D33DA9E402BED666EC3C4392333D54F00FBE554E57BC50EC553C8C1DF63BD75550BD66E6843D18B1033D6D0F113C7CECC03C17F103BE1AECC93CC899AFBCC75E8E3D6C9198BD4329BF3D725E08BEFEABF73AC14114BDF79A893DE976E1BBE5AA39BD37A5883D6F1BDCBDA48C233D0224003E1B8660BD4136A83C667C14BED945CF3C979D40BD0A2B3F3D28F5BCBDF9D10E3CC49FBCBD528CA53DB102A03C24B0C9BC20D8773BF33A51BD0ED0A63CA72283BC3592CDBC620D5EBCD203433D11F984BDE20CFB3B4FED8DBD51D85DBC65B3B2BD169B573D1D49EFBC1F2860BD4771583D445F0DBC6556F83DFDC005BED1BBB3BB3E10D43DDD0186BA8B748ABD4045853CD15E7F3D6E8DA23C4DBCBA3D9258FCBC9B321CBD989253BDA8E118BE337974BDA39CA83C79F125BE84FF4BBCC0051ABD7D9B3BBD2BB79A3D0B7FBE3DADA793BD9D1CDB3C16D4663CD28884BDD07A07BD850F12BCA70A8B3D4B088D3DFBF7973C70308ABD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'DOG', '0.9', '540');
REPLACE INTO intellif_base.t_filter_face VALUES ('12', 0xBFEF6500DE750B3D2A33CBBD24FB8CBC4BB352BDB98281BD0B430F3D6DEC353D8C131DBD0BA4E03C7DE7A33BFB5D0F3E1359B4BB2D3EBABC36E6FCBD7FC4CF3D83A88CBDA4898A3C9060DA3C00C8B43DEDBB0BBDB0633FBCCEEA083D5081B93D49278EBDE4374EBD4487843D79280A3D05DDA43D6112DE3C2B58A8BD486003BE42CAD83D317D9BBC3429B03C7C572CBD8ABB7FBD4E05ADBC2A685A3CB6662EBD5F5DE5BD14B4DB3C19533DBCE2CEBCBD1644683DC813943D55EE2B3D9E4A2D3DA6CFA7BDF762E33D209FD6BCDD47223C2497503DB19C9BBCEE2247BDF2DE0F3D0929AF3D14D504BDAAD13FBECBF048BD077170BCDA8E8DBD3084B43DE5B0803D4632E33D12C73D3D2CC0793CA1B9503DAF9BBBBD141438BD42E4263DF3D36C3DAB265A3D181B4ABC9C3D2DBBB9FE0A3D921AE1BCDF8FC53D810D9FBCD33A0F3D17921ABD01CB513D203567BDBDC4AD3D2EBBE63CF8AC84BD1185993C3DA29CBCF1A199BD65A9B5BDABF7A93D5524E6BD1E70813DDE9787BD344CBB3C22889EBB36B904BECA30623CBFAF20BDF440A13D948B5DBC10871A3BB60960BD2D27A8BC4F2798BD5EF2F53C6CFE74BBC9E2ADBD101146BDBB9F9BBDCF4B84BD301242BC3C7285BD6822EABD62242F3E0280CFBC0C48B73C5968D8BD86FDE4BCAD57B73C0F33823D595FDCBCD4161D3C73763B3C322BF83C75D9783DDFE507BC34EA1FBD776771BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '16');
REPLACE INTO intellif_base.t_filter_face VALUES ('13', 0xBFEF6500864FF5BC817294BCC574AC3C23C8AB3A09FEBEBD31B7A33D49740EBD0AA0BF3CDC06A0BD3A3EFDBDECA068BC5FEAD9BD67091CBD4C6D6D3D095DA0BC8D73BCBD74DF903DAFFFC3BDF0EDF9BD3A81EDBDEC1AAEBC962F00BD185D85BDEC3FD039F22B203DE706D3BC681F81BDD4D18D3BCC669EBDFD34F2BD8CAB06BE6DA731BC005397BCBADA86BCBA5CF83DA9394CBD0F7A40BD10AD6BBD37C976BC047265BDC8F4A93CB4F9E4BC40D91F3D3CB7873D977F343DE127F8BC80AAD7BC6057D7BD1D9C653D5D7413BD56654F3DDC9877BCD9A054BCA24F91BD999BFDBC19A3C13D3920DB3DF160E2BD066E7BBD54E79D3C0A7377BD0911713DA12EEF3DE4DA8E3D6BBC21BCB09EB23D91715B3DA0525FBD40E96F3D014EF03C38D300BDB4EA8A3BF4E8EEBC608273BCE4CFEA3D454EA1BDE8B1333C31FCB4BD6D2C42BC5A4B07BEBDA549BDDF01513DF4F0C2BC9C408D3D1F5DA3BD51E7E33DCCFC8ABD4DFCEBBC493257BD4F1870BBB26A01BE8F444E3D74F003BB94BD52BDDBC602BE148A633C5F617C3D20D8CFBDCC58E43C44A7373D632B6F3CBC41CBBD8DC4863D8DBAE4BDA0CB373D73ADDCBDC56082BD7A6A573CAE83313C570A0EBE7E6034BD1F33AFBD6FCD0EBD567DC23D89E1763B33C0813DB469AEBD1199EF3C494CB63C7D9B39BD375C48BD98A61ABDDC64A1BC776BA1BD3C9BE7BCF60F0BBCCAE87CBC33F2A5BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '11');
REPLACE INTO intellif_base.t_filter_face VALUES ('14', 0xBFEF650017380ABE0DA20D3DC093013D0D73793DB872CE3BCEF34F3C2377FD3BFCA6BB3BAD8A94BD3BF9753D1B6B65BB38AC5EBD88777C3D7C80EDBCAD8F833C80C896BD60E05C3D8015463C24139CBDE7C3CABCF15812BD14ED193D56E780B86DA0A9BDAF60C5BCBA18883D73E9C0BCD4AAA03D96ECD3BD334418BD963605BEF317ED3B012881BDB55CE9BCD84A7B3D8D78C5BD0A28C1BD4CD0DABDC1A8533CC8C4E1BC40966B3D1788A33C3F87C3BCCA969F3DC3AA4C3DA02D2E3CE9101A3DE667E83C38F4863DFC5C39BCBF7404BD104361BD6EE8B8BD55F8A1BD1A0EA73DB36F633C98E4CDBB203A87BDF543A0BDB2377A3DDFC8A23DA73F303D621BCE3D68CB5F3C4684563C78D90C3E3107243DAD1D07BCA28AC8BC8C1A383D97A4E53D37CCC6BD1658763D7CF9993DD2318CBC0393223D21F8E53DCC27B83D0A0172BD7695DDBBE3CD6F3D5EA709BB102A9C3DFBC1573DA7A39ABC6219423D9CC484BCD1132C3AE672893CDE4E70BD867660BE337EFC3BEDFBEC3C1F58BA3CDA9D80BD021EB7BCF1BECD3DE90C3FBE53B7B33DE1AC4C3D4986023D95F25BBC954294BBC1E917BD24BD9A3D4D5EE9BD94135FBDF5F4B1BD38E239BAD04AA3BD7E5C0E3DB9B859BDE6C829BC572BE73D211835BD42DBE63D9D3418BDDB5BDB3D23FAE73C86360EBCAF186ABD0064BE3DB01A913CA646DDBD6A3085BD515F453C2B4BCABDE037DCBC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '16');
REPLACE INTO intellif_base.t_filter_face VALUES ('15', 0xBFEF65002A26243E8A4533BD62B1FEBDB094E5BB4A2985BC0640A53DBE3BF53D6E0DF4BDAA1ADABC9ADD123CAD2A9C3DCB2AA83D326022BCC8B276BD8ED6E13D7CA0443DFD13503C253B903DD8246ABD72B3E8BDC5A999BB3031C33C0CD3A33DBC460C3D50AF50BD3F44043CDFB8A7BCC2F9A03C5854E73C848988BC234B663DEA408A3D387500BEAC5A8E3D26888CBB9AE722BEA0814E3D30D6B93C76AD9CBDFBFA05BD0ED36DBCBB58A0BD23C633BE8CBA123EABE464BCB6D15D3D0078FA3C4D9652BDEA9AD13D21F7CABCEADE8C3DBA822D3DAE2146BC5515023CF5D979BCBEB4943D1664803C4ECDCABD0A7A42BC2341B1BC0A53B1BC9D07F03CAD34E2BC4B5ECE3DB06DDFBCC97705BC4BA6823D444633BDC67F62BDB02F703D986D163D5EF397BC5EAB13BD4EAC313C9BB5FA3B4EE0E3BC861D923DA8A6F6BD6CF8C43D69CB1BBE9226AC3D18FBAEBC8F88C0BB269DDE3C4DB7EF3C87F3033D9BB1ABBDCDD8F33C1BAAD2BC6328803D3BD0EEBDBE9C3ABD7D8922BDEEE48A3DBA0B813DF22FB2BDF2F498BD07B791BC584F5E3D087CBCBDB218523D959BCCBDE8AA6FBDC2FF0FBBEAF0A13DF485A73CAE878FBBEE4E883D4FD0433D7B68983C9D94373D40C02EBDD28988BD2355E53D3AFA413D8B9A95BCC297993D7AA947BD665EBC3DF3C08E3DD13A99BC55B7DABD82CF06BDD641E83AA20F4FBC5E0850BD0B6F643DF4D60D3B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '20');
REPLACE INTO intellif_base.t_filter_face VALUES ('16', 0xBFEF6500549C8CBCF541E1BDD2381DBB243E3CBAEE4C863C095E92BD7C3913BE487F39BC2208BABD8FE8A23DD865B3BC025774BDE5EB53BDE551E3BD00E688B92C515DBDEA03E2BCD354D53D8BC6CBBDAAA603BD87A8293D00EC86BB1DD0943C65F0813C870492BDDB09D3BD63A7A43D6554AE3DAC10AB3CE34D0FBEFB7714BE5FDE09BD05F92FBEF9C97B3D1D9AB9BB0BDC7EBD821BE93DA2E80FBB76C9E1BD367DB2BCD553FB3D5907F93C1C3C2EBD9D50B53DB755A8BC366FA63DF2F050BD64912CBDDC41ACBC1C38913BB50FE4BBA87786BD74834DBDE05EA2B95C06643A2CCB02BDC7B3D6BD2C5533BE94A2ADBD7B62FB3C4838383BFF0F253D13A7953CD536143DF22551BC14D4A33C37FB9B3D240F953C5E3721BC01E00B3D39409EBD1AF949BD6CFE323C9EDA27BB2164AA3CF22C68BAC42B92BC0E95F63CC13C3CBDC975BDBC7977483C65CC233EF5FC683D21F5B23D4C1278BDEE427E3D17ABCCBC31E05A3CC42BEEBD295B06BD17FA27BE17B97FBD96D6B6BC29673D3CD72C04BD755F86BD420AC43D33EDA7BDD760253DAB921BBC94A2C73DF6ECACBD837E123D9C5093BD85AEA83CA3FAC2BC52D705BD593E02BEA923D0BAAEE1C9BD39BF643D6C9591BD1FDEB7BD255EF43D745DB7BDFBD21D3B529E703D90157F3D6CE0FF3D6D1FC4BD3905253DD1893A3C6E37923D123EAD3AD02488BDEE08E13C22BB813D4805023D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '18');
REPLACE INTO intellif_base.t_filter_face VALUES ('17', 0xBFEF6500D33C0A3B5C1B2DBDF7D034BD7BA30BBC354B40BD835CFF3C115ECC3D4F4EC6BCE1D8BCBC861CF93CC17DC03CC9F745BD2A48973D483304BD68E6B33D23C6CBBDA9550A3E34DFAF3D13B6DFBDE66A0BBD277E00BDF59587BD5283613B4335F3BD938CFCBD73CF943DE0AC193C09E6143D89D6C3BD7EBDDB3CFEABD6BCE0F6683B132B19BE2B3A2E3D9084843D3E56C0BD05B3C7BDAD1B6EBC1B0E81BB5ED5F9BC161B833DF8970D3C3A560DBE7E61CA3D05A2BF3CE9E2AA3B58B60C3D6A8588BB4D9DB33D00EA0ABDB53AC23CFD94CEBB5F6280BD03C3033BA1E12D3DBD42F53C8AD629BD8565B2BD6949B1BDC03DC43DE67F02BD857BE83D88FDD33D1811963D93A0F43CA0E7973DCB3AAA3D88A9B43CB30CA5BBE68D283C41BDA23D53CC89BCAD6F02BC2BC9B53DFDFF15BCFD13D23CDAAA173E74CAA03D5E87563CD17D9EBDA0E5693DBD567CBD0C93443DF5D2ABBB6F089A3B30FB6D3DFDCBB1BC2659BDBC3D238ABB9BA15B3CC84F5DBE21143E3C2D4A8BBD90529A3CDA39C1BC93ACE2BCBB570B3D828A91BD4AF8CF3DD6EAB03C4E398BBCF2B886BD5D38043B7406AABD1975CA3D186322BDCBF75A3D307C37BDC0D0693C8EB38EBD962FDEBB24F786BDB66981BCAA68F73D096F113D6F081C3E2238F9BA02C7B83ACA5AA63D281EC2BB4ED51BBD72D3D63DDB8BF7BB31E580BD450680BD4B5DDB3BBD8F6BBDFA5A6ABA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '20');
REPLACE INTO intellif_base.t_filter_face VALUES ('18', 0xBFEF65005ED24FBD0E7BA3BC92FF06BD865C24BD0F4E6CBD00299DBA9512363D900C823CC8586ABD894C3B3C8287C2BCCC0126BC38D1ED3D50B174BA3348353D6E7B91BD6B32D73DE66EAD3DCBCCD1BD1886F9BD6529B3BDBFE6823CF85C88BCB60594BDC92C38BDBEBB3F3DEE6E9BBD65E9523DE0863ABE0243FC3C1F75E0BD8D9BB8BC61E710BE7E508BBDC3F34B3D8856C0BDD32D4DBD362615BCE82C943D88C7B1BD29F7993DDBF1283C7889B4BDCA13233D3A3BB73DC178093DC87238BC50592EBD6CF70E3EDC450C3BA59DC23DDAA0E73AB1C98EBD96A095BDF6A6A73C2945533D1073C0BC1E0CEBBD7DB3E6BDB635693D784DB9BC585B763D30A4143ED96F143DE6AF48BC1C1A803D70BAB43DC260B63C74A4E2392A71BCBC133F6B3DE3856EBB51D51C3D88ED8C3D06E2373C59CBD1BC6B7E573D4E7BD53D5F8D64BD305631BD96D4B93C98DEB3BD2E8823BBF274A93CA524853D21449E3C5EF5AA3CEA4E13BD2816163D83ED723B04A031BE58877F39425910BD0CA531BD87D839BC48EB943A3027853DE322FFBD9453493C8B21AFBCF6C19DBD876147BD6E71AABB9801B63AD6164E3D8C0F7ABD878E993C73289DBCE493C8BCA7EDD9BD0A77B0BC1D70FBBDF851F43BEB7CFF3DFA7092BD89CE023E183B71BDB097A53C2EBEAA3D89D009BB69DCB8BCCEF70F3E6B745A3C19D6D1BDB18C61BDB51F003D5DB882BD617107BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '16');
REPLACE INTO intellif_base.t_filter_face VALUES ('20', 0xBEEF650062E27E3D4DE1CFBD162E68BB03174B3DD972BFBDE481013D9508BC3D781BDFBCC218CDBCF85497BC5B34D13CF37919BE11FB04BDD2A184BCCD5AFC3BDEFA94BD3EAF1A3ED777B1BD28D05FBD10A6BDBD00204A3B436427BCBE88A33CB62DEBBB988DF2BD2FFB10BD5381C1BD39F20C3C15CD55BC8E1262BD12A7253C904D873DEA865FBDE622D4BBEB40593D53B3FDBD1E43013D2265D3BC1A1E993B22D03FBD55A9333DB39EDA3D088F34BCA575F93DC44F19BD526A15BD11469BBDD6B3553CBE4E15BB350F103D405B92BDB0C71ABD4EBC19BD961347BB95A3F0BD6BC0F13CA27FCE3CEA5490BD826BAABD3B50143CC9DA843C066F243D575286BCDBD7C1BB564630BDAB3BCD3C0C5C213ED25D82BDF56617BC1705B73DA8AAC3BD76F4403D42B4873B0AE988BCEEEBBFBC42952D3C5E19BBBC33220FBD9A65FF3DB64E58BEA65C07BDF91BA13DA41EBB3D1BA00C3E9062FDBAC678343E741425BDEDC4F5BD48659CBC4866DA3C683C0CBEB6FC6A3D424ABFBD882B1F3D82C1033D62DC643DE218883DEE820F3DDC1E9F3DA3D1163C2570EDBBB59989BDFF0785BC838EC43C75B3E73B78DCC5BD6B2DA43C5531093D9BF3B83D626BA2BD9087E43C348B8BBDAAA359BD7B225ABD8977A93DBD21D13C12747CBD478083BD54FE963C80D517BC96FE7CBD6E675F3D1AE1603A87E7143D8F2F82BDB92E42BCE2433D3DFB3F4E3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '10');
REPLACE INTO intellif_base.t_filter_face VALUES ('21', 0xBEEF65009CCF0DBE81FAACBCE2A4A73D34965A3CAC9C58BDBD86323CB951FF3C58D63D3D396D6C3C71A6DA3C9BF19A3CD49ADBBDE2575A3D8DF4CF3CF5DE8D3DE7E580BD8571C13C85F70B3CA50BD1BDC32B36BE3DBE213DB0B59FBDE0092CBD003A8BBC928964BC26D3D73B9C159A3CB25B973C20C9DDBD30B418BD5CFC09BE980D043C2039C0BD193009BD4B4A473CA5A752BCC34B05BDB2E9F3BDCA6EC8BCFCC3EEBCB0F3BC3DF5FB4E3CA0EA74BC3ABAD33D3A348E3D5D2F873D74EC19BDAC46CEBB9552A03D8CFD063C7B3202BC82AB78BD0712D9BD19FDD8BD5CEB88BD078BE33D5771E83CB5E665BDA06173BD6031773A0E0463BD04BDF03D3A3CDD3D6FB5873DFCF195BCDFA75B3D996DCA3D34864CBD68D608BD7704AC3DAC40B83CB5E116BDC17B32BD0802953D4E3B593D3B13B5BD2B8F7A3B5934733DB7AAF9BCAADF51BC7DBF84BD8C8E2B3CE7411ABD39EB7B3C131FD1BD775B643DC4AEB3BC40B1DCBB5B4DF23DEE9898BC44A30ABE9C4B1A3D3C9A5DBD54AA83BD131F84BD0072D33B3575CA3D2B08BDBD24F6A53D8C936C3D25960A3E5EC8D8BD6AB0473D0ED6A6BD66D2993D315148BDA7F57BBDDAFE03BD578A2D3D7B0DF4BD2CD3C63C35457BBD11EE8B3B4540ED3DC9275CBD7CDCE43DE39715BDEEA8A23D97A3E43B399083BDC7DB32BDB639573DAF6D47BDFC8A4DBEC0A9EF3C1A3896BD32ED0FBDEEED9B3B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '9');
REPLACE INTO intellif_base.t_filter_face VALUES ('22', 0xBFEF6500348EF3BCB54D2DBDC9471C3D4155D2BC492D80BD59BCEBB925C69B3C5FBA193D3FBFC33D07040DBDD45414BDF0135CBD642614BDDF1D2A3C7FA4E33D4AAF3B3D211A313E1C4EAF3CA61FDF3C44AC9C3C8AF80A3E80E1DF3DDB2C33BD65C98FBDBE978DBDBC7DAFBD97DF49BEB5613ABDA5D073BCE482843DAABA38BCF7558F3D8F8AA9BC6DE589BD3161FAB9004CECBD677F90BB6809943D3146083D401CFE3B262C1C3CA58B023DE0BFC2BC5E4FC83D115222BC35AE5F3B22A60A3C928501BE32F0703D39AB3DBD1A98BBBCDD3A12BD89EE04BD2E2C08BE3C0449BD27CE7B3CF164E1BD0A91BABD6E4019BE359C683D070221BDB472E2BB25015D3D2131C63DCE38F8BC44C366BBAFF4913DFB75D5BDD22F94BD8C863D3CF36A8BBDA2622E3D5B073D3D55AF053D5AA906BCB460EB3C73EF863DFC7C9D3DA662123EB01D61BDA5AF523DAA47113C1E6E3F3BE09A603B6C37783DA43EE03D4A4F203BD6D2F03AA6AEC6BC2EA1BD3D769A99BDB95B413D3F48F1BD5987A3BD822211BB4506043D5AA9D73C1F83BABC0049943B65A4883D8FC108BD662FCCBDC72023BD1AC2A63DBB73BBBC19F70C3BFFA3B03DA603A3BD4E17E9BDFC572FBD0688043D96C8A6BD54A30ABD8175FA3D69724BBD97C908BDF283613D7C45B3BCEA72F03BF1CB5ABBFAA424BC22AFDABC45A1CB3DE738873DF737CC3DEF61993BC493233DDC3A8D3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.901', '13');
REPLACE INTO intellif_base.t_filter_face VALUES ('23', 0xBEEF650041A85B3D9497F5BDD11CC23C3231CFBB89ED13BE8E6EF13B9E3481BD455E303D2AE691BDB83514BE312D2DBDBB3CC5BD774B09BDB0C699389676233D965686BDEA0D703E223A173C2175CCBC3349DCBDEC859C3D6B7283BD71F3B73CEB3184BCDE9B96BD931027BD58C24DBDACDF83BD5035703A0F43653D9259393DBA66E3BC0205C3BDB1E3BFBDDB9DA03D49A575BD4BFAD93DB3372B3D281D933D933C55BDE637243D8247D0BB5A9AC6BDB25F033EE09948BD1A3399BC942A0BBD89FF90BC2904333C540C553DAC23813D4077A6BD0E63D2BC3ED141BD46AEAABD118173BDFABBEBBC55040ABE2D33D3BD0FD9B8BC698688BDCAD3E33D9705D53D615FC83DEC5F6B3D5001C0BCE154353DB44FBBBA740E993B937C213CBAE837BD34FD693B8E488ABCCD188A3C4F5B4EBB3A564F3DF436E03D69BE843C9E65A63C0E294BBEE035543DA487603CB8931FBDC468AE3DD1AFEBBD88844B3C893D003D932655BDE1B39CBC38F7B13D4C78BABD8082A3BC15261DBE6C53F03B6CE492BD707659BDF260653D0AEBB3BCD81F3A3D276CB4BB221D45BDB42508BE3174913DCB2FDEBDB9AB8FBDB754E1BC6285823D3676C8BDD017E03B024DDABD5E658EBCA49AF1BD0034CCBC4450863D5BCDCBBDE23D97BDC987FABC0E2041BDFC22A53D2BFDEABC642C7F3CCA8E863D335D943DD34E88BCA5563ABD0205243C49C8303C429C8C3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '8');
REPLACE INTO intellif_base.t_filter_face VALUES ('24', 0xBFEF6500CAEDD53CCE640EBD927143BDC9B5123D0D1BA5BD006FDB3C3DC315BDE5D0B63C0B168FBDE323A1BD76B6043D242D04BE7B2686BDA69213BDE638013D21B839BD3842C63DC94EB7BCC0387DBD88B781BD4A63663DE81B26BD007F843BD3661C3DBC45083C562ED93CAD5DF43C910E0CBDAE6DA83CAFB04F3C04D810BD75DFDD3D91D184BDEE38303D164F513D9F7D9BBC7BB31E3C8A45AABC95D6A2BDF37F98BD32F8933D85668D3BAE6EAABD268EE93DFCCDEA3C01FCA23DBAC940BDBF79883CBB865C3D46FB123B7F08D23C473561BDBF7D45BDCD892ABD67B79EBDEB7C2A3D6577073B927A90BD822D0B3D3240BA3C412CE1BD82B1F23DCAF43B3CC6C1A43DEF1E873DAC5B383DA1064D3D55A91DBC5B73383C1915143CEF5BE3BCD1B8E33C192456BA2528633C4219143DF1E88BBCCB32A7BDFADBCEBD4699153D6740A8BDA15182BD44ABC63C4284DDBBFFF2853DACF428BEA628BC3D6867BBBD4D6A053D6FF4F7BD263BF33C16BDCFBDD19A543C2F45BCBD6F5A4F3D394484BD48771DBEB3CEA53D51F4BCBC76D88D3D5933743D7551033E01CF02BE27F10D3D95BCDCBDCAD2C43D2516DCBB21C9313A7CA30CBCA50B8E3D6D8378BD8D8C2D3CAEA9A4BD3E8FE03CB5F8D13D570394BDDEA237BD9FD49A3C7EF1E13A7189B43D2049C8BDCCDE38BDD1CFCABD559A523A196AE7BD32CD8A3C5FFB0EBCA3B9C83C391C363D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '31');
REPLACE INTO intellif_base.t_filter_face VALUES ('25', 0xBEEF65001A6BBCBD4E2106BE6A2E73BC9A8AAF3C83D4C5BD5163373D2B880ABDBA9356BD8BF4B5BC267261BDD8548F3C60E021BE3842933CDA422FBDC50AF2BB0ECB63BD2EF10B3E9285E33D4747E2BD601402BE8DD91B3EAF7A293DD18813BDB6FD33BE602E0F3A1A89953D6818B9BD0E763B3DEA1A1EBD279DB5BD4066BE3D345F603D631DC0BD2468A83DDA0758BD83A733BD3663013D222ED63C8E7EFDBD5D2312BEF434583DBA8FAD3DC445133DB661D63C78EC303EEC49133C7F9F883DFAA8D3BC2DFC853D90D382BDCF4BB83D02203D3DD7739BBD6E0C73BC54CDC8BDCC03F73DC6ECA03D988C99BD4B3C21BE9FB2A8BDFE171E3D70A0F13C0A18A8BC3282DF3C0BCC783D5C87023E97170C3E3626F0BC689041BD1BB0DBBD81E66C3D3BB199BD9E7CC1BD5E8A353D543EDCBCA73164BD9B9ED23D07EC6EBDA3AFDC3CB1EADDBD8C08BEBC17A20E3C98D01C3DF225D13BEABBC2BD8A73963C18842DBD746DD2BBB44065BC6E58553D92D49DBDA65A0ABDC310D1BC9BCA48BD0749CEBDBE11773D1055163ECC2DB4BC2EEFB03C1FC0233D1EB2083EDC6FC2BDEEB5C13D06A4083DBE61AC3D45CAA6BC4E94BFBCE40FB2BC1206A53C8ABC3FBC781C65BC4D1D523DA341C73CB2DEFB3D28E88ABD72922B3CB378D73DD44B0F3D4244363DFB6F4A3D11B8B1BD03D7033D34F9483CFDC105BDFF6170BD1E570ABD8DD0AFBC0B25E53C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'OTHER', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('26', 0xBFEF6500040AAA3D770AC0BD64C29BBDC494ADBC00D3BDBC1390B03DEE329C3D778A0ABEFF3CB9BCE828753DD55376BD7978743DE5E993BB499C4A3D787B1A3E293E3D3EDC210DBCF0A2C53CB50CA83C1506D2BD80570A3D0D2B743B2FF945BC883ECC3CD9D8A4BDE31B39BD1E778EBD8838DC3CABECDBBC7385D9BD955C3C3C1027C1BCA8D88FBD0D4F0FBDD6EC9EBC9D170CBE7786383DCD875DBBAA69003D4486C33D752A403D955FBB3D51FFC8BD5D07083E542E95BD8DB5193D1C5067BD7C6C283CEF7D5D3D3B1E60BD74BC0D3CF2B6253D7B301BBD4B1D9D3DFC8D14BC557ED1BB0519BEBAC58801BEC5B2FD3CD010163D6D64AABDE8CC803DE5A9433D54BF7B3D27F60BBE5CFB9EBD9706DA3D0A3623BDBDACA2BDDBF5903D1D3FD33D97691F3DCB49DDBC2BC9AEBD2534533C18C589BD9560AC3D58D696BDA75E173D93DF63BDE384E93B84AB6A3DD8515A3DC9BB1E3DBF7CCD3D99EB313DFFB15C3BA15482BDFFD2233E00D7373D7DD7D1BD9F8DF4BC0127B13C72DC9ABC08A39D3DC8AB533C80B24FBCEB1AFB3C6B6970BC957F27BD09C0AA3DF945AFBC051F96BDC6CCA63D139793BD99E2383C40EDEABBDD4C9E3D25ABED3D8B2F1CBD9F03803D75E178BDD970B33B3864173C213CB23D4DEECB3C9942213D8F2E393D7509493B4D92193E016AF4BB781669BD001F063C33D5913D6C9395BDF871183A55CDE73D2DD1DF3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'DARK', '0.9', '24');
REPLACE INTO intellif_base.t_filter_face VALUES ('27', 0xC0EF6500678835BC893BF53C89A9013DF180BD3B7B3610BD77B79ABD072CA2BD9433503DFE3BF7BD5C369ABB37D50C3D6990E3BDE4D281BDCD499EBD27208CBC95DBC8BD938A0C3D5B49DF3B695E0BBD8AF0C1BD394166BD267E803C7902A23B7942103C98E113BE943985BD74763ABD20C1E9BC9B2C443DE521653D53A800BEE94C9DBC9C3B59BD1D441ABDD57A413D8DA8ACBC5A4FB83D9E58A93DA2B55D3D073406BEA94A203EEF128A3D473A8EBD979AF23D04B0933CCA49413D49BC46BD9430C83DB714D93DF92E3FBA2E19823D64EA00BE557F24BD02E96ABDDB9542BCBF79C2BD36DAB1BCF03EFEBD959EA1BD6FCFCF3D1701D8BB1C82253EB8289B3D3B4180BD0E0AF1BC167B8F3DF05B7B3DE57DE93C5F56143DAC9E513D429D83BCFC96073DB7034C3D5C68AEBB67597C3C8E74CC3C0BF297BCA65636BCDC60D4BD71825EBDB42A84BDD02D743CD4FA26BDA727ED3C87B73E39CF759A3DCAC90C3D90995F3B466E27BDDA6359BD0BAF9FBC7802AB3DF7AA38BDC5D6753AFBDA9CBC58F91FBD40CFA93D17DDAEBB149B943D04974EBEC010EDBB47F75B3CA42C0D3C2DA908BE1400D4BC75D5D6BC77E1AE3C8077F1BDA2D6DEBD54BB03BE0D950EBEDDDB83BDF4BC063DD391B43DAD0DA7BD67E0E43CD4E70E3D073138BDFCC7103E6BB9DFBDCCC90C3E7FD68EBCB88E083E1B3F693B515320BD14684A3C8D6793BB18D98E3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '36');
REPLACE INTO intellif_base.t_filter_face VALUES ('28', 0xBEEF65005CFE23BDF3DEF6BCABA328BEC884FABD550C9ABD5DC73A3C74058A3D40B9633D35D0263CC1230FBD58270C3D25CCC2BD2F8F42BD7A6F9D3D98CA9D3D0093403C277DB7BC38BCD33D65B7C13C8BBA24BE28D0003D9FAE553C359B2EBD287FADBD430A87BD93348C3C6927813D1543F1BC5CBB8CBDAB560E393D0DF0BDD18FD53C0C65A0BD619B4EBDCD6DD7BDD756DCBC40492F3DB7F4993DB363BA3DD798E7BD6C7E0A3ECB16FEBB7F50A23DA5CBF93DF0D955BD6F2B343E3C1110BC3F5D88BD21BD433D0185013DB4309D3D4DB667BD7715EABCC8BAD5BD93C40CBD9867BF3C731A203CC57F6BBDB8501EBE6B5C6EBB7EFC81BDDCF73D3D23ABD3BC8D1ED4BCDF6492BDBD8CA43C2B77A13D217D10BDE43780BD90181FBD88B0D33DF4AEAE3D1C61783D4CAA6C3DF8541E3D87E813BE93ACA03C4789ECBDA869D9BBE3B6AEBCE3F1A5BDDC5C9ABDA04F273C90A231BD953FB0BC277E863DEDDC623DD3B484BDF5AD2A3D491E933D3BB6ACBD3BB6E83D39DC86BDA595BABD4804EDBD13B5FDBB43838A3D884149BDBD8B9E3D006B5C3D59E169BC3D2698BDF5FC04BBD14D81BC6BADE3BC3D7D2BBEF060AC3C03460FBE27A093BD2708D9BD73AB793BFDB212BE6D1595BCDDC0223EF37C65BD855C103ECD55DBBB0D7E37BDCBD2B83DFE570E3E57D1F1BC2826633D748ED23D337645BC15EB223DC76B86BD54F7A4BD195C423D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('29', 0xBFEF65003DEC44BD6762E9BDD66CBD3DBC262D3C4345B6BDD6BA2BBD38F82D3DE8B0F4BCB148293CC79C4EBBAFA0893C18B7113BE8AA56BA6D5E91BDB924783D62DD15BDC345FE3DBD95613DEB2F40BDD3036ABD7E2C8F3CF42EEBBB0DC6733C3DC4B6BDC8F702BE363CEB3BD8449BBDF7B1B03DC39A8BBCF812A0BB5ED4153CD9FF44BD386B02BE735FA23C030A103DF68DF5BD2D3018BDAC19103D132B0EBD0EFC1B3C4C3F7B3B9BB3C53D9E262BBE882B093D9C78A83D13D41CBD0D95A03D7399F93A0A7FE83D6B0B36BD90A2493D1115E93DCD7BF5BD91BEAE3D590DDFBDB4C8603D59CFF8BC7B80ABBD4BD788BDAE5022BD2B8CAABC14C85F3BF8D57F3DFE8FB23D87F252B43004B93DDE56D43D859A67BD983A733C58B452BDEDF9003D9EED7ABC7E87843C3DFB133E2CDAC4BDAC84193B1C9BD13D0AB3D73D31C7DB3DBDF503BE12820C3CD83BDDBD14086EBC5FD2C5BC46270BBD37B74E3D2CFDB03C964E5ABD71E1BBBC1D2CE93C98BEE3BD9F64503D34459EBD4FF6413C380918BC48F10EBD45EA09BC5BFE6FBD4369823D78877DBD1AC30A3C9BEFA4BD5844DABC7929F93C2F9016BDACD1F53C14BBE23C75F9A1BBE88E853BC83AB8BDEF131C3B42A18FBD5B1A14BDD16B023E8A8984BC03438CBC8ADA883D3DFB97BD0C3D133D636D0C3C13DE73BD2395763D81459A3CD16AE13D06E589BD8D332D3CAE9989BC93C2903C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '19');
REPLACE INTO intellif_base.t_filter_face VALUES ('30', 0xBEEF65007194553C094D5F3DF2AA70BD9AB5E2BD8E4326BDE750383D92429E3B50A743BD313ABEBBA1C1EBBC0057B33D28929A3AA4580EBE917637BD3C94483D36C0BABDC22B10BDF963143DAB37E63B0726DABD19D713BD1B69813CDA9D243DC3399BBD250AF13C7C74223CF8CE2B3DE026063D53F3043DA88E32BD1265AABDF792B53C93D91A3CB2611B3E1272EBBC93F308BD25F982BB449883BD250608BE7CAA6BBDABB022BD4A7F3EBDB25515BDB9F8243D63B6843D302AF43B656B193D34A616BD200CB03D9A7F003D1AEC7DBCD425AC3B90C6CCBDDC4183BDC3FA8DBDEEB8B73D4EDF663D8DA103BEA40D8ABC6A52DEBDD2F8A8BDE6F9B0BC526991BDD2BA3BBDBC65073E927E863D78A20D3DA62713BD5A144C3DF85651BDEA3E983D462EDBBCEEDFB83D2CBA8F3D3B94663D5087DDBCCAD2A5BB246E1BBE83C29ABC8875D13CC1DF51BC54B00ABE16299B3D9552C03C6888C4BD776CC83DC053D739DF989DBB7DE82CBE1E4E4A3C590D08BEA3356BBD2B61F9BD23BF84BC610E61BD90D704BE7714DF3DC75551BDC9009D3D1C44943D4BCF113EBA42A4BDA77C943C67529BBD2A2A433D12199DBD7641E1BDA47EFEBD8C611B3D7243EEBDBE79B7BC27AAD4BBCC56ADBC4B9D8A3D2AED1BBD14E2F63A8BBC4A3DF8C8CABD74933EBD253A263DC699DCBDE5FB3ABD562FEA3DDC34D13B1A5774BCDC0606BD7441C1BDFBBDBE3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('31', 0xBFEF6500927C35BD0854553C79A195BDBE05A63C6E6AACBC5296C43D0617A03D0A6007BE7CB1853CC81CC0BD24981C3D6811CABD6ECF4CBD682DABBCFE1201BD441805BC5855F13D358F6FBCF3EC073D0CC780BDFAFAA23D10CB733C3EA77FBC6A26A73BAB9981BCA05FC3BC2055693DB97E3D3D683DA3BD5AC7B2BD2A6299BCB2A8AB3DDD46B6BD3DF07ABDC2EC6A3C4A736FBD9A6C473D3763473DA560F23DDD4A853DB5DC923DC3945CBD703DE1BBC607853D932FEABDA81ED13D7DA22CBD0D294E3DBF7E373D1A1994BDC3C088BDC642623D8BE5E1BDE8466CBD7BD1FDBCCB0E3C3CF22AD63D5BF7A5BDFA2DA5BDC4573C3D8DAF67BD094916BD961EB33CBA42AE3C6EA9C1BD669A37BC70A6D73DE6E023BD1E9914BD80A2F53ACC3E003C3EEECBBC45F3C2BDAE5B65BD867E3C3DAE960ABE770DA5BC7D2183BDA302D93C5F6424BE42A1A5BD346D9E3C251C58BC9BEC323D370511BC967B123EF57CDFBD2BD9DEBDD7BBBA3DF08E9A3BEAC9C2BD9656813D524FA9BD8E80F2BDCE2F183DCBAFCE3CD0C7973D3A97D0BDAE3C0E3D5B7A8D3D3A69C0BC32CAB7BD3CFF9B3CBD00D93C4334C13CEEDE113D3D3E95BD1F9997BDF5AB6DBD0D54E63B37DE313DA2B7C0BDE2246ABD12898FBDAA3CE83CFEA7BB3D8EB901BD20429DBBF3FEDDBC2AF73BBCA68A6F3C6E07B63DD339743D339FA4BBDC192F3DE68D51BC0525F23D447D9C3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '20');
REPLACE INTO intellif_base.t_filter_face VALUES ('32', 0xBFEF65008438F2BC9D972CBC17DBD1BC1F99643D1ABCC8B9F2D9623DEA2C543DA973BABCA1E4903C7ACE113D8D4A703C25FCACBD8DD1BFBDCBA214BD2C6E043D8D85B3BDFC53E23CE5C9A73DC4C616BE372E4DBDCC3E983CEAD98D3D93E7413DCFCC05BEE4ACF9BC8A0E2E3DC5B112BC2CC2CF3DE19F203BC5F770BC2E3953BD1C6893BC9D9F96BD0ECA483DB706443C7F466EBD3F140DBDD7A92A3D0E2AD9BDB340093D9DDCB93C8CA0E63B8570D5BD07B28D3D25969A3DDD9ACA3DDA8D6E3D7CD1263B1AA9053E9F3C52BD167DA13DA166803C3CB7C0BDD46CDE3C46259CBD497BC23DA8BE933D64DA5CBD676722BEE40602BD784EF4BB660AE5BBFF8F10BDE74D0D3DF1CD763D4358B53DE7280A3D4DCE50BD41FF0B3DD163013D9F535D3DDC87B9BD9A7FCABC525E953D00BA953C45A319BDDA89223D59B49C3BADA154BC222B08BEFD3C373D1362B3BD9B0F833D4057933D640EC2BD94851A3D7402DEBD537F263D89F251BD3096183C714A20BE63EFAB3CB8306FBD4000A4BDCD3E4A3D2F84D43C26475A3D24DB5EBDAAB7733DEF4EF83CACDCFF3CF3D3E3BD970F643D4D70913D200EA63D2AA731BDACD554BDC9BB6F3CFF6E253D643AA7BB11A501BD6A882F3D2469433D0D49153EA10C56BD3281B53DCC38313D497F113CB7FECCBC7659703D65E6D0BD7FA13CBBFCC7283D450A133DAF2795BC316A24BDCCB83EBDA4A73B3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.904', '25');
REPLACE INTO intellif_base.t_filter_face VALUES ('33', 0xBEEF6500889A493D88301B3C0667203C7E7A2CBD7EAB3E3B3BF605BD8A7AFABC2B0842BC597141BD7B87E03D8628023EF482A5BDB5AE51BCC82A99BD15E44DBDA0B119BE0A134BBCEACBB5BCC6AB96BD3E2AA2BD0DED83BD172C16BC0360663D6A83D9BC2205FABD6B59893CE1518A3D5ECA1DBD1E4ACF3DD66113BB94399CBD7CDB8A3D5077EDBD6E08183D4BCD653D1AA18ABD59C784BCCDC1BFB9220A95BD45E9DEBDDD273E3DC5F1D7BDE61DD4BDEE86BA3D42D772BD359383BDA2C3123DA62AA83D38DFC33D86B61ABDD1F04B3C92B785BDBD86F9BDF559CA3C2B3C3C3D06A84D3DB36B693CF3180CBDA9EDA23C82F3083DEBDD983D768D73BCF3EA843D0678373BBB43873D6B8B6E3D9456043EC619B4BC56A3963DC73A493D93B5FD3C109322BDB2DDD43DE839F03AE6B1983DC85ED1BDD5D3FB3B6ADC71BDA4534A3DDA979DBD3B55E5BC0D1126BD1642AE3C78A66A3D15F17CBDD83B163EBB89D1BDBB679F3D77E68BBD5AFDCB3C0E37E2BDB6F4513DC3AE00BE76A9E63DBB9D2FBD4A2E46BD36D3283D1A64C5BD199FA73D4BF31E3C8D3CA23DCC98A2BC55D22E3B78C10EBED973A33DA889EDBDCA917DBD48FC75BDA5B025BD94AEA1BD44BD2BBC3203FFBD203C52BC3F3BCC3C2D8D64BD125F093DFD31CF3CFBCB4EBA7E04413D6814B2BD2AE38A3BD88B7BBD2321FC3DEBDD08BEA3FB4FBDC0C880BDE566C83B7B916ABD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '10');
REPLACE INTO intellif_base.t_filter_face VALUES ('34', 0xBFEF65002B70523D94DE2C3DB0B290BD441495BC737972BDC1A039BDDD60E6BDFD3B333CC1F8DEBD5CEE933C30F1B63DB6FE9BBD81542A3DC49F3FBC8160ED3AD5B2FCBD5885D93A3CFD9C3D8E8604BEA55CC93B57D5A9BC5F46C43D5FF9EFBC651AD93B49446CBD957BB93CD9A6F93AB54AC53C58D24A3D9B2ACE3BFC54C9BD3B6BB0BCF183B33D2953C43DA449BD3C1076ACBCE727B53D8D76FA3C131218BBC3DCF6BD841B313D9DEDBBBC8531AABD6C82363E39A89FBD7012E93C899F02BEFEBA29BB7D9BB03D61C6A9BC4460893B406CDBBD796028BD8F869EBD426513BD4B7EBABD1B1DEF3CC7F91EBE57A4A53CB4C04B3DBD6745BD24212F3D073F93BC108024BB84BB763D2D57913CFF90043DB8569C3DE097013EF0E7733D85A668BDA17404BD1BC04C3CD840ACBD152E203D9930A5BB15F9A6BD059DBDBDBF1EC1BD0D9A78BDF37724BD5F9FF43C50BD403C19676A3DBC8BABBD5B019E3D73D681BD601F3F3C017FDFBD355C6D3D2E2590BD74FA2E3D08BC99BD47AF483D2FD18D3C584A41BDD170E13DAFD1BFBC197B853DE49B71BD4B0C863D17AB91BD23834E3CE43038BDECA3FC3C149B5BBDCD4EB63DE5C535BD691A93BD1C06B6BD8CF878BDDBC372BD5104293D6B24FB3D60A3C6BD0FE5D2BCC42187BC50F6CEBC301FF63A11CF7DBDF07DD83D50420DBE63EECF3DA82DE13CE5ED973CDF95713D62EFA03CD0B8FD3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '24');
REPLACE INTO intellif_base.t_filter_face VALUES ('35', 0xBEEF65001DFA323D575CCBBD38ACB5BCFB5F7D3AB79D89BDE04A6FBBC846123E195722BE8405ACBDA83242BD19848C3D7312713C0B7C573D77210DBE2810603DE8E9D9BD975F0C3DB863E33D4D1D69BD7522D9BC1B1A97BD89842F3DC3C0613C10FFEFBC276790BD056812BD5D2BCC3D36619ABC78F656BD869A0EBDA8F8F7BC916D123D3BF7E9BD50B3393C3B30B83CEE961DBEABC1F1BAB82FD33DAB4B85BC193A97BCBF30093DCC1673BD91F018BE8F41A93B6D1AEF3CDB95243D75F48EBAC396F3BB1B7DD43D20EE84BD5D40FF3C47062FBD095D8BBDD867C8BD326F913DB3A24A3DAD3B6F3DD34A31BE135F1DBD75BE6ABDACAF2DBDD940923D084F9F3D84FFBE3D977395BC546EA23D6BC0CFBBDD1DE33CBD6F9CBC458D443DCB8045BDB06A68BD0F5E8D3C0738EF3CEDFFDEBD93CAAB3C6C53793D1AF70DBC29E513BD4816CBBD5F14043DF8E4B13C2CDCE2BD2935923D28513B3D994E603D6070A03CE00988BDB5913D3C34CBAA3DCF8A47BE0A1F22BDE8000CBC48EC3D3DA36E163E83C80CBD6425E5BBF5D014BEEDDF263D91DF90BC0944D1BB8DA936BD531546BDE071ADBDD06B443E5395223DE4A2F73CA9FBE4BD09A521BD66E9A0BCD046A1BDE1115EBE8F8694BD0BF9DB3DD94E0F3D330FB63D04A40ABD55C4F8BCF0182B3D71C3F3BB1114023D4B95033D7082623DD708FCBDB3D24EBD4B6298BCA811073C9F0AA9BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '6');
REPLACE INTO intellif_base.t_filter_face VALUES ('36', 0xBEEF6500E845AF3CDD1694BCBBD8E6BC8AB1633DE09FBCBDF275263D45328F3C3FB1A4BDBA01CEBDF9D9D1BD0B08093D670C883B3EB8D3BDD03BCC3B2AD7A43DA54C60BD9216BB3DF76A8DBB76CA703CEEA59EBD961A43BD8DC2F3BC5E623BBD80B418BD850109BE9B421CBD6AC4B8BDC9CFA6BCF168CCBDED7F72BD4F4BE83C7A86D93BFA753BBE3C1F3C3D6AD1113E335E51BDCC636D3DFC9F2FBC0A90853D68FF7BBD1E914D3D1C676A3C38B7CCBDD3919A3DA6709ABD76CDA2BD397CE43CF675CFBD10DD323DBAB5FBBCFFB0B03DEF0963BCBEC24B3C6CCE32BD6283C03B3A7798BDF44AE53B4E2C2DBEDF2ADEBDFA8615BDA9F84BBD8BA0953D624FFD3C5EC6833DC0B8A7BD97E2BC3DCDF3D33D71BA85BC70F58B3CB52E0E3D2C4B08BD9527363DEE1DA9BD841720BC7703F23BE02391BD5C72653D50AFE53C85D11D3DF15661BE8844A73BC107163CC0D752BBF51718BEC1F0593DD89AE63D62704BBC4E16D83C3F902CBDD63456BD4C3B24BEB56D0EBDEA9B87BDD67C3FBD328D31BD7F7FB93C616CAC3DD072D2BD016BEE3DCB9A83BD9C10523D673226BD37ABE6BC253331BD034B9D3D8DE64E3DE6E3AA3DC91EA4BDDD5BD93CDABBCABDEC7BEE3CBC7010BE075A3BBE0696123DA4BAA63DD3328EBD0E7370BC82C454BD3595F73DB088E63C951C8E3D7290B23CEDF1B7BC4B518FBC443B4EBD175353BCB47DA43D915FF53C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('37', 0xBEEF6500D85B033D78AFB53C709A75BD1D5228BCD2663ABE505A633D437B133D438589BB46D7D8BC499477BDDF6E15BDF07519BE90AB3C3D2671B9BDA4C3CA3D209CB6BDEDC1813D38FB453D7ECB4EBCCE2A7A3CD4A7B33B52EEA9BB6492C73C84BF28BDAC97663C30E3CE3BBA08633C142B333CC5B91D3D3225E1BC18FBAEBD6CDD943D19F0DABD71F7C6BC5C6E073C5812903BEC2C9F3DABA4443DFB545BBCEECFFCBDD9D829BDA0D914BEA7D1BA3CA197A33CB430FF3DFAC53B3CD10B8F3C199A2FBE8574503DE121B8BC6290E33CBC5361BDF0A48B3D982E0BBE24B9033D94D4063D1867FF3ABCD896BDE21740BDA272A03B6CC9A6BC9E41073E016F703D1EB94B3D4974C83DCD27823D884BB53C6328A1BD9873DB3C2E82C3BD90A0D5BBD032E2B930BFE6BBD88E0BBBA733D53CFBCC0EBE3D22AE3DC40304BD4FB50F3E187EA5BD9A53213C46CBEFBB1DC2753D80B1C2B874D195BD141EAC3D306D96BD81A56DBD284AF7BD7839FD3D1F283DBEC74F923D7C6687BD69C62A3D82639DBDBF95A5BD1F7D023D44CCCFBD2FAD133E2633B13D06B41E3D943EEFBDDA3806BCF8A29CBD3855923DC1F519BDCB5B813DE0ADBEBD835251BDD8BB04BE87879FBD3801D1BCD92396BDDD2EAF3D8AD38E3CECBAF43D08D5B53CFAEC233D58AA0C3E2E149ABCC3CDE5BDC2A7823CC6A291BD66CD44BC94226EBB82E7A83D9BFA16BD5F1B58BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('38', 0xBFEF65004FDFF1BDC847CDBC4D7C54BB731E6E3D9A7E0ABDC542723D7E2325BE139E0DBD63B1D03C7BBCC7BD27C58BBD571395BD167CA1BD5997C53C93D25F3D1F08E83C5F55C73D2DAE313D0786F2BD4CA2273C09B7B13C33D0B13D0B8A833DE52BA53CD8EACE3C1D1D38BDD05E77BD88B58D3D5DD8ECBDDB95B0BCB18670BDE6D785BC855910BE88B7A7BDC8A726BC18D0013C49F68E3C8790943D7E32123D95CFA2BDAF644C3D7508FABC5C6319BD6DF2B73D582FAABD61318A3D59090FBD7B22AABDC152B03B6032CABBA4E7E8BC9F37F2BCC1A714BD9C0DACBD472EC73CEC92953CA97F87BD25DBECBD6F6436BE1D7BBF3D168991BCF5E0F93B69E7F13D1AB50F3D64A03E3C749F673D83AA9D3D28641ABD6CDEDBBBBF4528BDADF2A43AA242123D63C33F3D305E033D3268953D901EEEBDB702813DB7EAD13D6DA30A3D501917BE6171A73D7F85B6BC7534FF3CC997A7BD4CF0C3BC926F853D742B8CBD9719A3BB3D5914BCAB3D9C3D17B786BDF73A73BC6066D8BDA9326EBD6C7798BDC383903D1D549D3DD47ECFBD89AE8DBB8D819DBDFD077BBBDDE60FBD2BAFB23C8117963C8DC8353C8F38E13B4974813C08F07FBD7F8177BD470A09BE7C38013E486F9BBD54016CBDE381BA3DC1478ABD5FB2313D47F32DBDE0FBDABB0C5C8F3D7B46A2BAE39841BD0F65F53CF418DB3D35CD863BAD40973BA31DC03BC956AE3D8B2E05BB0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '12');
REPLACE INTO intellif_base.t_filter_face VALUES ('39', 0xBFEF6500667753BD287CCDBC690484BBCF67E23CC9A99BBB2674E13D8A13BABDCB722EBD564E4ABD29F135BDB76F10BCD7919CBD0A12A4BD57248EBAAEEF2EBC5140C0BB3593D43D21038D3D58D536BEC5515BBD629D103DB2C1C53C8AEF72BDF0E044BC7FEA70BD4F72103D58A0FC3B664A9F3D667A84BD6246B7BDDDD226BDA15500BC70A53CBD362991BD628C30BD0333EEBD6A2000BD29C1B83DDEE79C3DC797703BF6EE403D3AA2E7BC69F748BD8D835F3D701C0CBE65171E3DDE5374BB9530A83CD252C23C5FE1CCBCC71ECBBD8172DB3C21E73ABEEF9E4ABD561182BDEAA43BBC971960BD408833BDE14DF1BDF93D113EDA708ABDA280613CABE6783D0841933DF35C53BDCE03143D1762DB3DCA7517BEDBE85D3D756E283D35D7F03ACE968E3D96B403BBFD59943DA505213E0B5ACDBD4ADF9B3D690F7F3D4D86563D581B03BE6BAA0DBD02490DBD81797A3CB601F3BCB96D46BD155B033D46B901BDEFE1E6BD4223373D830A09BC4EE44ABD29588D3D4960BABDC2180FBE0F3929BD1A48923CEA28213E93987BBD293A24BD695DBDBD55D7FD3B95C85CBD40C4933CF78C5DBDABC17DBC3D47C23CAF40283DF5C6C5BDA63066BCB105EABD0EC5E43DAF8318BE8EC96DBB9345963D2B75C4BC26B1233E8BA8A5BDABEDC9BDF6AE933D52E2BABD172A77BD4EC2E63C62A9EE3DE9F0F53B317E7ABC33CAC9BCD698903D1B6FD63C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '14');
REPLACE INTO intellif_base.t_filter_face VALUES ('40', 0xBFEF650009E5C2BCBE3A8ABDE0A19E3D5C7E713D1A1F91BD212385BD1DB5D23BB659D4BD244770BD01BAA93DCF3172BD6DB19CBC5C979D3D9F7D27BE03238FBD198105BE308BA93DD12ABCB92066EFBC9C4405BD00FAE8BA14643A3C2C5528BDA6DA82BC014FC7BD01D030BDFF79343C613288BC6CE5553DF4991BBA54F4D6BC595849BCDD10E0BD6048093BF73BA73CCFA3B2BDAF46AC3D5711503DEF1CB53B57F0A8BDA6A04A3D5C79EA3C8CE3CDBDF4077E3D368B61BD66EB3DBD106A9EBD140E363DAA46E43B49765ABD25A489BC316BC3BD2CA495BDDAD7423C6988FFBC2CB6973C665971BD692AC6BDA36F473CB98F7FBD7D3977BB8E9CB83CC3741B3D02C8933D4CC6563DEFCA00BD42BB2B3EA3D556BD5440493DE1E00D3DE8F221BE065B9E3C818DB53DBFF9163D3AC4ACBDC313AF3D1433FF3C7BB3963BDA3D1C3DE59421BDA60FE83B9A05DD3CAD0868BDBA1B083E7158C4BC9D0C1A3E43C542BC39FD253D086FA7BD6F52F9BB71F614BED9F1FC3C581226BE5920AC3DCEAE033D27DD50BD1AD9743D6F8127BDA2630C3D7AA356BD4D83913DB922A1BD69818A3C07F59EBDAD7087BCC1ADC3BCA9C933BCD54E8CBD31354CBC851CB1BDE60A3CBD9404D4BD702E54BD6C3456BD437A72BD2AE6D7BD5104923D6C7780BCFD205CBA5DB0B5BDDF0C143D20EEF03B8C64AD3DB9449C3B0418EDBD860014BD3175C73CE14C30BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '11');
REPLACE INTO intellif_base.t_filter_face VALUES ('41', 0xBEEF6500C52756BC83DFAEBD074BB53DD7025EBD29D225BDD3618BBC01912C3D7088A1BD98C62BBC033E37BCB5DBC53C2098A3BDEBF3B3BC8951A6BD730F24BD3133A0BDC616833EC0BCF3BC43607FBDDB3A92BD3BD0FF3DB1B6E33CF1EDAA3C674710BD1B790FBE7CEB91BDB88AC2BDABAAA1BD2F59523CD8017DBDB0CB20BD35BC193EF7D5AA3BF1EC45BE80936ABD07FC05BD6B037F3D2961F93D14E3983D76FF9A3D6B55E23D73BD9BBBEB5BB4BB1705023EA7751CBED459C03CD9E028BDA096A0BC44891B3D2B2F49BB9C56C6BDC13947BC7CD4DEBDE169D13C1C5CD43C4B21BD3D438C4D3CFB96AFBD514DDCBD411F633D3002C8BC25153C3D3D59BE3D73F3EF3D76001FBE85E701BD5CF5023E83AAE1BD6CAA25BD0B6EB63DB716D0BD83311C3D004B8E3D7B152D3D44DCAE3BD82B4DBD13EF933C1C29ADBC80FDF73BB44FA9BDAF33EDBC5D4C963DE86532BDDCCB603D9FDCA6BCBDC5AB3D9760CFBC437368BD9771D73D44BA443D5D1FFEBC3B54973D7F48DFBD633B9ABD1DC60C3CD42D313D0838D43DDC4E13BE896AC43D0B962A3D8BD4963BABDBA6BD25F418BC6BF688BD31B5D0BD65C781BD552A33BA483BAABD8784C8BDD512FABCE1C01E3DB70811BE79C614BE8BE8843DACA63ABD2DA6853DB3B4D9BD93353EBDDBCBB8BC23F78DBD0F52203D799A6E3DDE919A3D0F4C1EBDC7AB4A3DD7F885BDB5D2F33D6BAB66BB0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('42', 0xBEEF6500A0261BBD1640F8BB811DDF3D3E8C833D8297CABD5E4BA3BD86FE3CBD12AB14BD55CB06BEC5FB80BB696784BAA5ED873D5AB69B3C5E9340BD09A9F1BCA2CCEBBDA84CA33DF0435FBBB18FC4BDA6513CBD0B7BC3BDB923C6BD57C09BBB954042BD8B822EBD708046BD1844423D9371173D9746A2BD5DCBCDBD6FD849BD0F18D1BCDDB41DBED300D23D5A0BFF3D6B5CBDBCF880443C92A4CBBA49A77AB9226A7DBD1176A43D40306CBD21C038BD17031F3B03A79B3DBD4A2EBD8BC5993C6A5A183DC1B1EB3C356A3D3D67AC1BBEAE7F68BC054400BB49BD3B3D70C7AA3CAEB42D3DF527073D13AB32BEFF493FBD387389BDA34A993D62456A3D9077AF3D7251CDBCCBE9943D8BD1113E4BEA3E3EEA07303D48F68B3DC0FE453DA87E40BD2E6B48BCDE7C4B3D5303A13C506707BEF77A163C5E9BB73C7228A33D7B48BC3C12C75E3903D0563C8E9FD43DEA58FABD00EC51BCD5D497BDC81DAF3DA0C2F33CC002A13C3372A03CA18D95BD412CB2BD93C37D3D19C33DBD20BEA03D0C5F86BD7927163CED7CD33D66E007BEC6CC113EB07A1ABC294D6C3C9F0B043D9AEEA0BDB17008BE023CE2BD5DD6C53CD32777BCB84AF9BD7F273DBDBBEB1BBE00B2373D9F41A3BDF748C3BDE56205BCA9D643BBDB2243BD9032653D37D266BBB701D53C6BD6ACBD2E130F3CF99DC73C9F7DDA3CB5AF773D62E0FCBA9E3807BD70542DBD7937503D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '7');
REPLACE INTO intellif_base.t_filter_face VALUES ('43', 0xBEEF65001F163F3D0A49FBBC43ACDFBC63DD903CED76CDBCFEAA863DEA653E3D7D0859BDF72B1ABC5DA1D5BDA477AD3D8602A9BD129C57BD0FF422BD9DEFE03C823431BE9A1C6FBB5697BC3D567670BDE494B4BDB2C8A3BDE3DB67BD080BD4BB7AB009BD75C9C03C0B7DBD3C881EE53D7A77A3BD807997BC5CA802BE9A7C2D39F584613DCB19C3BDB9FFBF3D2BB08CBD7FD037BDC3007DBC197A1DBCE3F6633D20C265BD588B573CB038A8BDB0141E3BD29E1B3DE3C11DBDE6CA993D407F913DC1F9B5BCA864943D18237BBDC88F0F3DA649BCBDB22098BC2EBC17BEB73ABA3CD3459D3DB2E9A73DD258AEBDF03B0DBE5A79D43C9ADC6DBDD6A6CF3D8DA784BC3B20B93C36C873BD9E1F7D3DCA47DF3B2DE3CFBD868D04BD3A60EDBBCB12FE3C0E3D84BC132A94BD8291A43CBD39FD3CA6422BBD7FB5BD3D42570FBE603052BD429D75BDC8BB05BD664DF23CD0C45C3C8EAF293DBDFEADBC233FD23D59B5483D40EE20BD960B62BBE900173DCB8B19BE02F890BC4D8E2C3CCB80BBBDDCBF1BBC85AB49BDBE9A063E53B5AABD4DDFEA3D5E413D3CB8CE7B3D1AD042BDC4A6B1BC241118BE00BD563DD345D0BDA495043DD6C83FBE837F2CBD8B3846BE4715CABCB5AA4CBD3E5EC7BCEB784F3CEEECB6BC35D10A3EFA7BCF3C70FBBCBD93338F3D5BCE6CBC987B07BC8162C73C2231173D30AE0EBE121C993C8086EFBCC56CB0BD3024F63C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '5');
REPLACE INTO intellif_base.t_filter_face VALUES ('44', 0xBEEF650083ED4CBD137A48BD4BB69CBB581A5B3D1B4335BC23923A3D155ACA3A276B11BE70A9C03CDC0ECABC2B08DC3CD5351EBE8E2BA8BD6F1AE9BDF7BEC63D55E75EBD9B76D83CB3ACD83BF8323BBD6D3F15BE585A9D3CC03E7CBB195D5C3D980CFDBC6B1A663CE1889D3C201B06BC5927503DD520B83CA4CDE9BD9B5B46BD6C7F4D3D504FC2BD6FB7853D33C6D4BDF598CABB82AA923DB5F929BBED5827BDAB0D12BC4084123DA83A5E3CE83B7A3C7C00633DC006193ECDA4223E252229BCF7F8B8BD0001AC3D400956BDBF361F3E0FB19D3CA8C8FB3C0BEDACBD5F71CCBD7B73A93D29DE2C3EE377C3BDEC1086BD1889C1BDABAB953C84B98BBC396C94BDAB47343D00C045BCD1D2C03D9C62513DC7D0B6BD4D638B3D1D3EB2BD2F8F133D24455FBC62C680BD3FDA3A3DC6C991BC279AA8BDD81D2DBC1F059BBDDC094FBD95FF06BE2DD80C3CEC1BCE3D0EC11E3DFBC6B23D0DE2BCBDA528C03A17BE88BD03C780BD87018CBDFDF012BD10D55ABC285F873DF95E80BD93CDC2BD752425BDB1DD1DBD4808393DC897B5BDD435053ED89DC03C2320533D81E018BEEFE44C3E5FD093BC487A363D123C063D058A9BBD55E59136D50E593D4922C8BD0FC921BE4828AF3DE119CFBDB0063A3E49CEF53C8025F53DD96431BDA50C843B19174D3DF7261C3C775588BD25F9B4BB81CF1D3DA364193C25C88C3BCC92D9BDC9CA3F3DEB97AABD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('45', 0xBEEF6500364361BC5A91CCBDAAE6383D031801BD00FA50BDD5CF2B3C13283ABD36C695BD75EBD4BD43A4453CD074CEBC58AB70BD6CC60E3CABD704BE7C74B0BD8BBF05BE36C2B63D9F948BBDA926CABD434D35BD6DD7583D42F3083D7784B1BC919A983CC04A54BDD023E3BCA4BC3A3D2A6904BDD64B103D53A9EABD8D9751BC6A32603D9EF58BBCA2A98C3D52B4653C5AE42EBD5E72663D0D3FEE3C2C2430BD68AF78BD72312B3DC007D6BD150DA6BD2B89973D22A1FCBD124D4BBCC57180BCC92AB5BC6E9E5FBC19F390BD82805FBD226B4ABD5DCA32BE1157293DA8386A3CF85D4A3D0F5005BB8662B4BC75FBB8BBA0B20A3D238F0F3C98C6D2BCE7A92E3D22DC323CF0E8433DB98C0E3D30CA533D33A0E1BB12F6923D02ACDE3CFB138DBD24A63BBD852F7B3D31C017BDAB02993D45373BBDE341C43A930F92BD051A103D9667E3BDE8788E3DBE05CC3CBC65AF3CDE39313DC27693BDA683EC3D7A99C1BD32A9053D2E2CA9BD07E44A3DAA9C18BEE29524BDF398C6BDD38AEA3D42FEB2BDB3C5793A88D6F1BBEA5305BE4A6FEF3DE2F00D3DB638C83DFF3AC1BDB2696A3DBB0660BD1B63D03D8AA03ABCABB167BD2E9484BDC4F10CBCB869EFBD29F8C6BC15D3E3BD07D9A0BDA215523AD28AF9BC563A523D03E784BC52E70E3D7F3EA93D765148BD4271183D4B71CC3CFDA2DC3DD55B23BD1626D5BD7D84473C5451A63D5532D7BA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '10');
REPLACE INTO intellif_base.t_filter_face VALUES ('46', 0xBEEF6500FAA90DBA956E06BD02221D3DE8F605BD28135BBD2586123C23AC03BE70E7D43DC273C8BDD60E91BD155C133EE89A0DBE3E0A70BD0A5BE3BC58258B3CC0D58EBDBAE048BD25949E3C3328743B1673583DE515383D3BD32CBD8AC8DABCD098DDBC2346BABC553591BC6AA1893D86E5BDBB3499B8BC75A411BC7C4B89BDD6D17E3D128FB63BA2CD723C3AB9483D03EF45BDF409B53C128AB43D369681BD56D616BE3599BD3D4255163E668990BD8ACD873D4DF4DD3C46C21C3EF241AABDD875E2BDEDC07ABB90D486BD32A831BDF70C1EBEB34DBABC059D52BCAD01113D46D4EABDC63900BEC3498CBD67180B3D665016BDA5BDA5BD43D1113E738FB53A5DF4D73CBA351F3D48FAE23DF62E513D730F373CF610C13DD0BF00BDA288A53DFB5B413C4EBC1B3EDEDC923DD84F3E3E5E4E8D3D8B8CCE3AEB7322BBFFD331BD74C200BE7A6762BD457CA83D5562D03DACBCBC3C334DAEBD6A7A253E7BA097BD0059C43DC209B6BD17F6153E578E00BDBB465B3DBE5803BE75E8303DC6A14FBD4EA6B6BBE69462BC8B9C0F3BDAA2A63DEA41BD3B3800B53C82C31E3D8D8DD0BC33A2513C72FA1E3D8DA586BA8BD25FBCE2A3D6BD1ABBD9BDFDAD86BDC653E6BD08D2F0BDBB4A113D05EC433E82B254BCAE5C893D834419BDCDE32B3D8B83A13D5AE28BBDCA49913D926E063DF669643DE341F5BC4CBE9E3B9A0BC73D682FC33C4395153B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.903', '5');
REPLACE INTO intellif_base.t_filter_face VALUES ('47', 0xBEEF65008E6B193C8CA782BD113941BD7B34CFBCA099F4BC7E96633DD866CD3DF34E3BBC363675BC2E9EBEBDB351753DA04266BDE2B9923BB0801E3CFA03FE3C980217BDEB2CD53D60CE34BB50EAE3BB76BD9FBD0002D8B99BE4B6BD06BB3ABD374392BD4D124DBD357C1E3C6B4C5ABDDD3A173D7A1DA6BC90226FBD17BB843C982BEB3D52B389BDB1B99A3CBBFFE8BC9E78A0BD3D3C8ABB95608FBCA886B03DE6DC8DBC9B28C23D7AE58D3D1680D73C863FFC3D639B36BC926F8D3D35D0CDBD7657B0BB574DA1BCDB54FB3C5741B0BD91DA8CBDBE1A79BC60DBF9BD370006BE6E60833C2141853DDD7735BE1012BFBD95596E3D6AF125BDD0596B3D004CCE3CA2E63B3D3AEDA4BDA858223CAF26BE3D43938FBD96DD06BDDA5FD33BC81EB4BC401E3F3D1C37A43B47E517BD40AFF6BC432D6E3C992B0E3DEEA9BEBB46597B3DF0D84CBE0DAF51BA8C32AC3D058A583D200AA43D7A43DCBC7B88D23D4E1B00BD4A440CBED064543D8CB2B63D52E91ABE45DF6D3DC6C1A3BDE0D341BD4322173B48939E3C9B71863D8205543DD905B33DF3EAFD3C6A330FBCC7A38BBD7E08A53C33E35DBBAB1A9C3C86B8C5BD2677B03C7228D9BDEDEF743D7EF4EDBD67EE953B263EFCBC0541203DF6393EBCD7C3853DCADC113E128767BD91580A3C48138C3D2DF0D33D8A5FCE3CF0316E3D427605BD63AA4CBD1BE820BD2C73283D804356BC96A7603D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '10');
REPLACE INTO intellif_base.t_filter_face VALUES ('48', 0xBFEF6500C170FBBD4B54C8BDB5C7DA3D998C853DDB5079BC1DC919BD7BD84B3DE145C93C175E3CBDFD16933D3F8A603DEBFD3D3B19CBC73C7F6E5A3C93D22ABD145518BEEC1B823C8C374B3D9A450DBEF7E925BDF07CB33CEC7AB43D553824BD156115BEABAF59BC177203BCE03BCABC8F871A3EF3D8E8BDA5BC49BD11B8D5BDADADF5BCEDF79FBDB9BDBF3C6F40263D4766A3BDF24000BE8DEC9DBCA0D876BDF31322BD921D9E3D13EA533D7DD316BD7963B0BC5767C83D8A61163D9526D03D25322A3D9DB79B3D2579FEBD1C92573CB05E623BC7DCC7BD0529293DF9B225BAB094873D6806593DED0F83BD09C78CBDA82626BDF7949EBC1C24CE3C5326083D1332AC3C4731053D183CBF3DCD48B33D304BABBB8C3BB43C9860A9BC8F742E3DDEC01FBD3B1DAD3A7827F23D57D17ABC58A51C395F09B93D51D9F03D3F7CB0BD6C8ED7BC447DCC3B81B6C5BD8DAF4F3C7567703D45CB303CB399A83D3FA838BD78255BBD2545D2BC101149BD32791CBE1D1334BDD14B09BD8915F6BCA9E223BDE49AD73C71189A3DF8CD92BD18E73B3D0E35823DA7E811BBAB6D56BD857D5E3D936159BCFFE1673DAC4A4EBD3C96993CD7335FBD81118A3A45908BBD752786BD25CA91BA63B0713DC3ABBE3D05B528BD5FC1203DBDBC953C10B1C43D77A0E4BC0D00D63C293BEDBDF39FF93DA837043B61862B3D9DF6893C654B1F3D79D56ABD8012AC3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.91', '24');
REPLACE INTO intellif_base.t_filter_face VALUES ('49', 0xBFEF6500EC8B053D93AF3FBD197BC9398B1615BA3309333DC30DDDBD44A817BD47F3B23CEF9495BCA3DAAD3DD87E103D56B3CBBD971A11BD4CA7BFBD0444483DF21942BD84EAEF3CBA71473D4EB183BDAC90693DA0B547BC689385BDC4DA863D6075C1BCF3153FBE8DE780BD9508EF3D40F3DA3C4B34643D37F24EBC60B3713D7CC4F5BCC34F29BECCD1DE3D94D6553D01EF8FBCFE23413D4430C9BD3AE08ABD0CD1C2BDC99A2E3C22E4A9BC052A89BD2BD1FA3DAE5B68BDCB486ABBDB45C83BEBBDFB3D5264E1BC1AEF97BD195B863C981D08BE21D7B1BD2ED6983C8904843DE07B41BD9A0C17BEE062A7BB43C0DC3C726610BA0BF55A3C537A83BD4BD52EBD956ED7B900DAC93DAEA37B3CF49A5F3DBCD3133C60FB61BC77F2FB3D2C3B1B3BD7CC79BDE385B93D8D8AAB3C846C153D57A24ABDE73CF9BBBC65463C3622933C273405BE93302ABC7453FE3BD95ED93D4551873DF78458BDD4D3B33D25310FBE9CABE4BC03BD92BDBBF2423C380E19BEE0C726BC97D7D6BDA4460F3E0EC5113CA9B6EDBD028AAD3BDD175BBCFE8A613DA26622BCA575EB3D0E9378BD39F001BD402156BDEAC15A3D3EAACABD892B393CD1E88DBD0E13EABC812DDCBDDF24A63DB4DAF4BCAF3D1C3D324EEC3C072425BBB0BBA7BD9EA1803D748F9C3DC59A323DFB8F14BD95BB59BDE9F24ABAE499133EBECAF9BD7C55D5BD18F006BD2C1146BC0FEA55BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '18');
REPLACE INTO intellif_base.t_filter_face VALUES ('50', 0xBEEF6500D23623BDAC3C0DBCF1EF5F3DAEC2F23D0EF08FBC595F3ABD8CDDBD3CEA6A3EBD639BF4BBC8B281BD0FC4ADBDBDC5B3BDE3D0843D7F9835BD759CF6BCD078BFBD509A003D5BFCD6BCE98804BED42825BC2ABFF93C7A1B33BDBF549BBD4E0EE1BD1456DEBDCCDD0BBD952CCA3D47D6683D5CA6CCBD05C4E7BC387DAABD9715B8BD23D5D4BD91ECA03DF6F1533C08C815BED68BF6BC67ABB4BD39B87DBDD027843BCBCF3C3E5A69A03C7A11A5BD2809673D078FD4BD553D2F3D0C21223D7F25A63D704A173DF5B885BCA86674BD3F0ACDBDBB9289BDBB50B73CDE1EF53D9862CABBD97F813C41CCA3BD20F4C6BD700FDA3DAD5FE73C58446BBC9892523CBAEC44BD1FE5843DDC759D3D8890973D80F4AEBCCE0A343D0A130B3EA0765ABB1007AA3C84859CBD56A22D3DB05846BABAC0DD3C6C76303DAE3B993DFC0994BDCC61563DACE41A3DF1A6193D505FC13C9E32CABBE972F6BD5285023EFA857C3D472010BD512ABD3CBE3D3CBDBA1F28BEDAFD88BC28A5F0BDECE67CBCB54DB2BDF29052BDB09CF93D6E3A5BBC049B1F3E6E32953DD78BE93D48A333BD6631323CC0305CBD6E1CCD3DA41E27BC6807A13CCA4118BE12652B3D6E46FABD588B893D3837523B35E873BD8FBCF1BC98131CBEE028EB3DE6AF433C4E6D813D08AF6B3DE64D8DBDF857463DC0A5013E44CF213EBE5ACF3C7FD7C3BD18C0E1BCBF562CBD13A8963D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('51', 0xBEEF6500F0A7EBBC616927BDE73F233CC819F4BCDBF5B53DDD6D8ABD5583BDBDD0B404BBBF32AEBD9D001D3CB3EACD3CD30BBEBDA73FA0BCF8D1E7BD6FB19F3D31BB00BE29A783BDBDD2A93DADA3D8BDFF059F3C2822333C40BCA13DE0AFB23DACEEEB3B01B5B7BD35E6ABBDBBBD0A3D61E412BDDCCDBD3DDCD3A6BDA1ABD1BD579199BD33822A3D41D28DBCA3C9DDBD533CB43D00E73C3EAD5DAB3D7CB51EBDB3C53D3D1D0EFB3CA8C42D3E2BE560BCEBA6DC3D8D698ABD453752BDCF6DF3BD911CCBBD0DAA483D70C2B6BD680EC63CD541683B3519FCBD484C03BDB9C27D3D974A33BE6B49B13C61D31CBE21FF223D00B7ADB9248A96BD1E7B05BD15E12C3D35E3243C3B26AFBD4BCB91BCA2299C3C639998BD2DAC8CBD7754883DBC4915BEB79736BD130A823DADF05FBDDC9D01BDE392EABD387DC3BD1DE96E3DA07FBDBDB88130BD4097E63BFBCF023EFFA9FD3C08010C3CEF04C03C2B4B6F3DA30C1FBD705140BCF39D6CBDA0A5483D57D223BD00B525396B2B193AC34CB83D417D983C49A6103EB882193E7764D2BD508C7C3D258206BEB196C73D0325403D50492D3C45551DBDB4A45ABCCB1C4F3D190232BDB848A9BD47FF7EBDDA9C183D83F0B9BDFBA2C2BD579C81BDC26E2A3E2DB9DBBDABB7AC3D631D4A3DC2EF8D3CE9F713BB97088DBDC530DF3DA3A3B4BD7047513CC11862BCD0E721BD6022BA3C6555953DB10E323D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9027', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('52', 0xBEEF650087BBB33C3A4C183D9045503D52CD8C3C5E1A493DB02C993C86369ABC908F9D3D2AB1CFBDA05343BD5B48363C186FDCBD8071A1BD4E3CBE3CC32A4DBD53EA383D0A79E13DB35B7DBD32C313BEC2ABA8BDD3EF013D601C99BDA07AD93C836881BD95DE07BEAB055EBD9E5F43BCE358543DF2B9BDBD9AD0DBBCE3CFE0BC13B5E5BB0BB6653BF51928BD269DB43BCD332FBC5E1FC13C6380D0BD16208E3DCAFF3FBD1E78633DEE3F85BD3ADAEBBCB02BEA3DD9D710BE9258D3BD53FEFEBC8C379D3D047188BD766724BD33FF66B86008E9BD5F52B3BCCFFD983DB6F0293C86BF86BDDE9578BDBE885ABD3AFC38BD8314633D26A98DBDF25355BDC3C9153D62996B3D9A2A883D53FE8D3D53053A3E5D8040BB72E9A63DB2F72F3D13FC8B3CB84604BD1382FB3D06E6C93C79C8073E1A1B253B167B0A3C2B0DA13D1226CB3C01CDBFBDBD2BBE3B4E5F223D9DE6823D3BC7B73CC592C2BDB854653D3EAECC3C9AB84E3A609984BDA6AA283B9EAC34BEDD39773DC5CE69BDB281BEBDDD8507BE084C16BDAE6FD93D968CB3BD4CB8B33DFE70863D7EAD823DA82DF3BC3A83543D50A774BD8AEA673DC03ED9BDBDCD02BDCE81F8BD4DBEE8BC45762DBE4C2B063E3E2FB8BDBE493EBD4233933D4E6B2BBDBE12853D7D99BBBDDCA88F3DFDC7243D38BFDBBB38D7873DF67E913D76B2A83DA805EFBD02981DBE15FC47BC8AF0C7BA700C683C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '5');
REPLACE INTO intellif_base.t_filter_face VALUES ('53', 0xBEEF6500DF7B21BE3AD2C1BC507304BD2A0F6B3D932441BDD65E833DAEEB9EBC642BDFBD54D2AF3BBA117ABC06C6633D68952DBEC3A19BBD8CEA38BD208C203EAE7FBE3CE61EDDBDFCC8553D89AB50BD98FFC1BD2F658A3DD84420BD903C263CC0EFC1B80CF63DBDB8185A3D5FD18EBDFA58EA3C39CA523D6ACB26BE3ED5063DD6624DBDDD7BDDBDE1880E3EE63805BD36BE36BC083B2A3E334F91BD69CD34BDFA00FABD908A29BBFA15CE3DBCB8C33D5ADBA93D5837A53D45D6B3BC3202E0BD24D302BD5D51F7BCCACD19BDDE31C93DF915AC3CBC340C3D2C7B46BDA2F900BD5D7464BDD08E6CBCF54687BCF407A1BD3FEDA7BC22BA843DAEF78EBC47FEA3BD8FC47C3CA905723D8C95A03DBBBA973D0E8E76BD2181673D0DF98EBD567FE13D142A833D0C74473C405FAC3D185F4C3D54BA06BDF4A2FE3D2E588EBDBB1171BCFB22D5BCC4B5273C86D29F3DB19E0F3E0527603C0C9DD0BDD65D3B3B0341353DEB4828BCF7B8E7BC0E6AD03CF25173BDEAB1A33D6A47BBBC5116AABD732C96BD4F87943DE7F6173E2EE89D3C5866F53DB55E1CBCC77A1A3E509595BD9A55E73BFCE8973DEA1D02BDCF843EBDE8EB9CBD2AE785BCFF721F3D30CDF7BDD369C63BC68D6D3D941393BD7F1AF23D244A7A3C70B4263D86A6AE3D0F6BE0BC61DC9C3C6729A63DA3FC3DBD2846B6BD059E1C3EE63183BD04D104BD40D58CBD982624BDB96ADEBC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'OTHER', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('54', 0xBEEF65001E1043BD8C7C87BCB6AFF4BCA882203D4CF2BBBD7D61953D835E633D00EE76BD074E51BDA1F456BD4040613DE25320BEE40522BDEDAB9E3C13E81D3DA729E2BD54CD3E3D4CD53D3D404833BD58A9673BF04D393A8AC1DBBDC3144EBD837A713D59907DBDFF33823D7705803D1476653C90BD86BD042FC7BD12E70DBD162CDA3D2339AABDD90E33BDBA921D3D414F5FBDB2BC183EECD555BD3C28FE3C661024BDF2FDC43D582D80BD8B3CADBD14EE093EE0B8D4BCDE23AC3D79E4193C337AEBBD4DC7153D7E87A1BD55E623BDF3A51E3D18FF44BCCEC938BE03C966BD5275AEBCA605CBBCD13D9ABC408122BDA01E00BD5C933E3D48D39B3B4CD2813D849BCF3D5A641E3D48A35B3E58B5213D64EBCEBDB422663CCD8A813D72D24ABD56E5033C902E95BD486D27BB1869523D6E7611BE7C54B1BC09495CBD0D52A83CF9D739BE48C49FBD8565ED3C901385BC94DB7ABDEEA707BEDC0B3A3C185D17BE0EC2E8BC4A751ABC88F2D23D8287ECBDCA31B73D60C89ABDD89B4CBA12FF21BC17F0B13C8692093EFC239BBDBC18A93D03CF653D4E5BE8BB866F61BDCFB2943D2044A6BD76BA9C3D95A3A3BD587FD13D9EE2A8BC7EE0653D680853BE0694813DA23C09BE64FAFA3CFE17553DB8F4233B9B760D3DFD669E3C3064FDBD1A1388BD3601BA3B3E9DCEBDF8A3273BA8179DBC31869FBD1B02EF3C667710BC991E103ED5D9863D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('55', 0xBEEF6500705604BDAD53A9BC61A00ABD50E0C93CEA174DBD4E2FA5BC4B0636BD1AC5803D82F290BDE0FAC0BD110D863DA988A5BDC2A5C1BD50601C3D3D67FE3C858221BDF5D5B6BCD385A6BDC5D4353DD1F910BE0408A2BD7ADEFFBD33D4E7BDB53694BD4E22153D56AE47BCB4F1B23B2AEF19BDB75580BD69F9ADBDD16216BE45676B3DC32DED3C36A8833DE9560F3D78F838BBA2A57BBC17DA11BE42B6DF3C1FDAE9BD014EB93D467C713D6C91C83C334E1C3D20D8C33ABB27183D405B81BD48A59A3C5EAF073E18C83F3DE1068E3C9138BBBDDE8FE7BD00C125BEA3A7EDBBEA8CD7BCC8375B3C5C7FA9BC1BBE25BD2509D13D2BF456BD288D8A3DEC690F3DEB7823BD025E7E3D48061F3DF783DC3B81C574BDB86324BC38B64A3D2E85883BE27DDFBCA077603DFAA5F13D00851039D4F283BC7960973D66FABEBDE0F1D6BC3AE3703D2B56FCBD561B7B3DF3A5983D1E5D3E3C92B40ABEE4FFE73C6B9AFB3C68625CBD00CE30B9FB8FAFBC985618BEBEC9193D78BB673C19B62A3D36AF02BEB0E581BDB312B73DC95C86BCEAE0273DF08442BB1C17EC3D4132AABCB81A853A73C391BD8C4A343D7C5B4FBCEE625ABD273ACBBD1ADBB23D9CE084BE1FC72FBD908AF1BD9E6A5C3D7E15C73C082D55BDB252823C58BE053D31498DBC224A5F3D547BFCBC43F5853BB0F0013EE34D843DCA6AE3BD7E13D6BD120EACBD8373E2BDB3D9B63D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('56', 0xBEEF650083D0283D690232BD6CDDC6BD79382E3DDB2C92BD49B96E3DF7E11B3C18ECC7BD45C77BBD3F3F17BDEFFDC13C312EBCBDC16B77BDE0D03BBDFDC38A3D35B62EBDEB4D1B3E8BF69BBB60C9CFBC40BBDCBD35B2DE3CD1F80FBD7509AFBD8F9DB83C37BA16BE10011F3DC844C2BD73ECB03DA1DD94BD117504BDAB1E0B3ECD60A23C945557BEC302CCBC4B33DA3DE1492BBE18B6EE3D1CD34DBDB48B4E3D6BB2EDBC57E78E3C2592303C85F7A1BDBBE14ABC6B9A02BDFD12CA3D85234ABD717A1E3DFFD7C83D1972633C33609D3D92D39ABD73AE2BBC4BEBDCBD358B0D3BC07440BEE5B760BD1B34DEBD15CFEABD01E189BCE34348BD1B74ED3C88A5933D97B8EA3D91988CBD64B90EBD8899CE3DB48B8DBD838B2F3CFBE20ABDC4B71B3D13806F3C4BCA98BDE2E31FBC452D5ABB0411CDBD7C887F3C6DA7353DAB103A3D61D759BE1076C2BD6B183A3D0069A7BB146772BD1D3CE03C5292A13D5C0D2CBD535689BD75263CBDAF209EBD2CFA5CBD709616BD08C062BDCD5A6DBD1D791A3D8D3CF0BC7E0AAA3DDBEC9C3B1DC1BD3D100D6EBD3DD65B3DEB325CBD899B8BBD084892BCFBABCC3D23F5C63D13312B3D3089BEBD5F6FAABD773995BD604CE5BB1F791DBEF11824BEAE78983D4FD15C3CDF472ABD5D13373D11A2AFBD6C74103EEFBF68BD317FF9BC0DBAC43CE03A943C7DF7873DE9E5BCBC3992343DB2790C3EC0FEFCBA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('57', 0xBEEF6500F50074BDC67DB5BBD23D733DFFF4CE3C8B578EBD90D2153DACA7503D408BAEBD4CE55ABDF3CF20BE8C8703BBCACC453CD432BCBD05BA10BD6E653E3D40FF8FBD98C7973D11E8DE3D447746BCFA1720BD004EEBB754794FBD85FF813C0272EDBC1C9F293C0CAE91BD8EBC91BC60ACD93B291F50BD6B5FCCBD0048C8BC2AC8A1BDDC0A3DBE24B5B53C0004B53DD0D396BD6163FA3C4266033DF587CD3DADE7743D5E78D5BD6B08B93D741FB7BDE0B978BBFA6CC6BDAF69F3BD7A1C2A3D365626BEC3A6893DAA3D1FBD498AFD3D1EB7563D1E26993DFE2A253DCC2B6DBCA6AEBB3CC005B63C0D7706BEF9E708BEDE120CBC6C8223BDB47BDC3D7EB0B83DC6A9BA3D3F6DAABC9DBB073E925B083D7BB590BDC0FB26BA4E18093DA16B5B3D0E98ABBC631151BD7DC2BB3BC82865BDFC67A3BD34D84C3EE068C23D9AD3FE3C20BB21BE2A8A073CDADC17BD428D913CF8BCDFBDD38F933DF6CFF33DB77B96BC3819F8BBD4A765BCDCC01FBD8AFE05BED62D643D039770BD563536BD488212BD80A6F23A794BE63C868B2FBE1943843DC5EE83BDBA699B3C3783263DB6A2A93CE2A3ACBDEE6AD23CEC2ECA3C0525C53CCDDAF6BC35980D3D734BB8BD4428A53D309F15BE805B30BE5A64453D5FD1EE3CF9510EBD60DF73BDB373DEBC7F92293EBA3ED33C545ACB3D0C6500BDB5A5A4BDDC0EAB3BF09612BDB1BFC2BDDC784EBAA0F3693C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('58', 0xBFEF6500517BA9BDBBBF93BD53112C3DEDF3033D7C2A33BD0DFB94BC89F6CDBD75B9ACBC414C0B3DEF0D8CBDDC4011BDD21367BC8E1B56BD85CE153D0F865E3D76D291BC8413B93D66C95F3AE70FA1BDF2955ABCF9BD1FBD4B3A423B518AB33D0D671DBDA074D93CCA9AF8BBC0F7A4BD15EE0B3DB6D7D4BDC3CE3A3CD497E8BD99FA76BD23DEA3BD77BDF6BD25042C3DB5F468BD6C0D9FBD56433C3D77B7C03B89B7AC3A9544CB3D0353E1BCAF1FBEBD32F9DE3D1AF8C1BA0B48E43C451DC93D24B7DEBD57A6993D060218BC3B86913C6609613DECEF83BD3F4CE1BDC73CF7BC77E2143EC9C93A3D052CAEBDB029FABDF2EC4A3D9BAD8BBC23E2CE3B91A5BF3D075EAD3D2CBCEF3C5B02A93D277B7B3C11C1B1BD1CB5443DEA950ABD9FBE673CCC79D7BD27A6AA3CC6BABDBA15A2C23D492581BD11CCB8BA7380C23D47348E3CA2A94BBE9A56013E1192A43CA68CD5BC6C094C3C1E3B12BD7B644A3DA60AEFBC3C3431BCFA92143B31FE653DA3DD05BE8DFFDE3B9475D5BDD3FD95BDDC0257BD7C3C8C3D47B75F3D05CD1EBE5A10E63B0B338ABC4E468C3BCC3279BD6330C63D6D77243D052D9F3D25A9AFBDB9580F3C047D2FBBF675043CFB0C0BBE86D0AC3D2EC8C5BDEA45F4BBDECAD53DDC6C93BDBBAF703D73263CBCA2AD09BD898A06BDEE4A33BD6CE91D3DFF0F033D2B79933CB68DC4BC52176E3D3DE383BD12B69E3D34C4AB3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9134', '15');
REPLACE INTO intellif_base.t_filter_face VALUES ('59', 0xBEEF6500A3E5CE3B15342ABD3A2E1A3C26A0133C5ED2993C30ACE1BC8E3D06BEED54DBB9F375FBBCE65F893D5A048C3B923708BEF0083FBD4E3AE7BD125F26BD73CA83BDC87B193D347FBE3D9FB083BDE67B383AEBE3B63C7B9FA8BCE6D755BD60C630BDDE32ECBDF65A77BD28B808BCCEA7A03C809E193D5BE406BEC3905CBC49698FBD36724CBC06FE093EF21B80BD6DC3C6BDE5F2733C1B897ABD6E0CA5BDABDC07BD7D9215BC5F5C903DBB898ABC980E4C3DCE1F17BDF2840F3DE631DDBC6A99C73CB638B9BB826BEEBDB0069DBD2029D4BD4AA8B3BDB2BD0BBD8E59963BCE84223DE98189BBA055CEBD4348B03A75E2573D0A4D0FBCA4C738BD7683B43CD8B372BD72CD973DA581EFBC158CC53C3AE57FBD0741AF3C0D37DA3DEDDFA03B63E0D1BC3EFC18BC4AF6C4BC75D6563DD91514BCB34270BD9368333D3DAFFEBD88EC10BE3EB4D1BC7228103E930F143E9AC21F3E80BE203C6D0F083E156D37BDD651B2394B6069BDA0AEC13D7376B9BD130A193B94039BBCE589AA3D2983823C2DC2D43BCB1B983DBEA194BDB2A9B6BCFCB61B3D220C253D6322EABDBAC3833DAEE22C3CCF28343DFC7A06BDA260383B93C08DBDB0A31F3CF52623BE3E41C73C354430BD3BD64D3D8BD7D9BC26D74D3CE8D6E73CDD9ED63B82DFA33DC664CEBC230AD1BDE8A927BB8EE4E63CB0A4183E583ABDBDF49819BE155BA5BC0283303CF6046F3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '10');
REPLACE INTO intellif_base.t_filter_face VALUES ('60', 0xBEEF6500851122BEDAAB5DBCD0DAD43D035F06BD45C9B93DDEEDE8BC571E0DBEF6A94D3D88D127BE3C90BE3DE85415BD0020513D41EAD6BB72D269BDE435633D00E309BE923BF03C6711A9BCA1B417BEEC2CDCBC241C7ABDE6F4C83D6CDCD93C8303B6BD31F30DBC4C2392BC2A7F2E3D305613BA78A4053EA4211CBD63792EBE56FE99BDC91DE2BC3AB2CC3C6146873D3EC2853D46D4003E9C7B983DB96E05BD8C6E5CBD5073A53D4E1B313B80B3893D17018A3CDE2B27BEB555E1BD0BD10EBC80B994BCA366B53C4889F3BD8657E5BC462BB2BD72B0843C5525D4BDBC5DB43D7C3A6ABD62D4663D0732B2BDB6FE15BE8A2B8DBB9D679ABD760F683DA4AA59BB887C313D3F5B0BBD8A43613C9085E43DCE6D1C3DB28E133EACF67B3CC65899BDEF9EF1BDC44D713DB920C43C56B0F9BC38EA2D3D6DD0A93DD9549DBD8884BB393CBAC23B95F9013E72D1123EC4F2F93CD049E8BAA97EADBC91D7AC3D2A7F8D3D5EC9EC3C64FFF9BD0DA0DABD6E7FB2BDEA8786BCC21C963DFF002ABDC0A6AE3B0C7387BC2286833DF14112BE8094C1BC33D59ABC603E703D451A813D9F98CCBC18003CBDE6ED163DD524FBBCD83B6D3AB7E814BE648B5F3D64C7163D3FC2313DAC9095BD2C001E3DF64C6D3D06DDC4BD9B92953D4466053D20FC303E7AC4A83D5DBF4BBE8F0BE43DAF9D0F3DABD0963D5C3187BC4AB0D13C8201A0BBC0D78C3DF224E93D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('61', 0xBEEF65004147B9BD0837F7BC59FA833D8A13143D7BF5C1BD71370E3D9CDAEABCF5178D3D48AEEBBD5F7D96BD3BC28B3D073E23BE3DFAA7BC086A9ABDF452913D6841A1BD759BC63D0D92DF3B840C7BBD5D0886BD486C33BCA7338FBDB01554BD81A2AFBD713C143DC0C6FEBC4CFC693DFDE4AB3DFCD8613DB089C7BDEF83C4BD471A10BDFB3444BD073C933DE985AA3C23EEC33CB041DFBBADA5D8BDF0ABBF3BCFF49CBDD04FC9BBECFC0D3DE838373DBCBC90BD74F7703DA8FFAF3DC4FB993C9636903C7FF2C03DD4CBE3BCD3DF2F3DA1DC61BD41DBB9BC650FC0BCEB994F3D241EAEBD437AA93AF0B6EBBCD306EB3CC0F86C3BEFA70ABED3B1873D04A8743D1D7B50BDBB8AA33C34A6113EE436B03C845FBC3C78D3133DC074743B6D84DE3DE0D8AF3B70B11EBC06C5983DB14B7EBD30DD59BDB458DE3D7D940ABD9B13F7BCA8B880BDABBD1EBE05347F3B804E553A8BFC1CBD63374DBE0CBD9F3DAB799D3C75A45C3CABFAD0B82B31D03A5C9948BED8B8F6BBE016523C0BB836BD18F81FBEC73D05BDD41C163E40F188BDF4A9DC3D38F2D83D3DC0593E508B51BDABA24D390037ABBD1BCCC3BCF41EFCBD4C5609BCC3D743BDF55FEB3AFDF37EBED1FD00BDC1AC25BD37B804BD4C47C43D2983C5BDD3B7D23CDD8BF0BC817B2F3D11710F3DD86F34BC6CEE51BDECC5073D9969D0BB8C61CE3CDFDFA6BD112A1C3E3CAE4DBD2D4BFA3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('62', 0xBFEF65000E0C9C3C132CFD3DA2A1A0BD918E22BDEE13243CADDBC63D079D88BB0602AEBC152026BEEDCB7D3D5E2628BCCE7D51BDB5927E3D64DE1FBE2B39CBBC3B23503B651F863DF3F745BD81112BBE0E581BBC3807E2BC2257523DFFEA6DBBD8679FBCD3ED9E3C8462133D5CB1A63C85806A3D358E223DADE212BB1C43A4BB9B5DBE3DF2B8AE3C685EDF3DCB91B23CEA5A73BD748E353D703D523DEDDE213D5EC17EBDA52C4CBD6782E9BDD06D84BDEAB47F39CE28113DB55704BC5F6B3FBDBED4A83DF0135F3DB0D0823C325F49BBA4C2C7BD9BA132BE1986FA3CAFB71E3E995D453D217EB83CD9D8FBBDCC9DA73D8A597D3D51D15D3C8723F53CDB2AC2BBE8DB07BD78EB733D9E0B8CBD246D093ECDFCA13D5C74EA3B111DE83A91FA31BD900A87BDF65C80BCEF73A3BD68EADD3D688207BEFE39C83CAF19C23C4EEEEDBC3B7FBDBD74B683BD178A42BDA450713D8D24C13C21613E3C8A8F113DAAD370BDF5F1803C68CB68BB8BD3283D45FE0DBD6FFA1DBB41F095BD87FAEC3D1992DABCB6B9B1BD898E953C76C4CCBDF34F35BDEAFFDA3AEB38CC3C86D1B7BCDFF12BBD1207D6BD4573893C102A5EBD2D0D013D98106E3C7C3007BEC573A5BDBC45203CA175C33CAEC55BBDAAF0583DD9E56BBDBB68D43D1D7236BED1C74A3D70939ABCA540CDBD100DAB3C59971DBE25BA3A3E2A5C31BE5BC94B3DF461E1BC8EFDA5BDC13989BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '53');
REPLACE INTO intellif_base.t_filter_face VALUES ('63', 0xBFEF6500BE8AEB3CFABC2B3D8C8B023D1F9B483D5BF2433D81BCA8BD35FCDDBD155D2E3DDC7684BD980E843DA774243DEE49F1BD37846FBD580C9ABD47249D3C60E378BDA2C6B53C0A9BE2BC3778D3BDAFD479BD12D9A73C6205053D7E106B3D44950F3D30E13DBD21479BBC7E3EC83B990F343DCE65103DFB34BA3D192F41BD45DA6F3D9B9189BC4713BDBC572A803D0C13FABBCCD2883DA7B2853DA25821BC4EF98CBD94E54ABBF6E7A33CDEC0A6BDD19F233E4D5D0DBDBC7A4E3DA6A219BE1787E33C0407953C4ADBEBBDD6FE903C2677A6BDFBDAC1BD9A0D6ABD37C33BBDC23C013D5739ABBD11BC84BDE726663CA73C9C3C76D348BDCC79BD3D27DB8BBD5E77413D023C5A3D5929D4BCD758873D3B1B503D2B57213D19DE0A3CE6473CBDF67F23BD8F3A2D3D2F6C17BC4AF5F53DEAC0E9BBA51ACEBD7B856BBD764BE4B93F0A0BBE2B534BBDF52D113C0E09AA3D241FDC3D6B85C33B7A35253EBE709DBDD5ECDD3C6E6B73BDEF6625BB977570BD5E6BA13CDC0993BDC723CE3CFF6F893D9AF385BDEFCDDFBC6A1E53BC8B8396BC699541BD9A707F3DC5650ABCBC27EB3B99F8A6BD19A7D03D3BC14BBD8E2E423D6E0D39BD8F9622BDA02040BD3B6783BD1767F9BCB6AAE33D909C113EA13688BD8E49763CF54E903CEBABB73CBC709E3D6F68B4BD6F54C43C6EDB50BDBF3AAA3C6E994CBDE6E22D3C765ED9BCF0D3073E99B175BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '26');
REPLACE INTO intellif_base.t_filter_face VALUES ('64', 0xBEEF6500B60444BBF1142F3DC2AA8FBB1F647DBDE7F4043DE1BE14BE647421BE8622163EB444CBBD3619AABDEEA9953DD74300BE227A2DBDAF8B1ABCA0DD92BC9E1A7CBDE4F79B3D2A8A1D3C9EF6C3BCCF4464BDF7E5263D8AAF58BD6CFA8CBB2CA131BD000F5CBD59A3A93C7E76FE3D4181D9BDACF2E73BCC8920BD59C27BBD6D2C3B3DAB86BE3D2566DC3C825FA13C8B8DD23D3624463EE2ABA0BCCED67D3D3E3304BE3A88753D58FF4BBDEC6B15BEBF7F4A3D5E5EFE3C420BED3D3E2C59BD947ABF3B0A2217BDE9C5FBBCE66A7E3D4E054CBD7AB8EFBD243832BD762E4DBDC1BE633D020A8DBCE60ED2BDD3770BBD2A3630BD4334B0BD042E0E3D643166BC528EB3BC1EBDAD3D372465BCA6719C3C3CC6553D88013CBD930F7FBCC475C9BBC8B081BD70DCE4BC2AA1C3BC856269BCB025263D0E6A61BDA24635BE0175BCBD57C937BD244CBEBC1CACF7BC72B6913DADD89F3CAADE7EBD419C1C3EF46A01BE63A7B73D2F41753CF7B1963DF95CA7BD0C99A0BBBE1F5DBD44762F3DF0A0D43BB8C8B6BDD282923CC8C41CBE6CE5C03DB1F4313DDC23AB3DC111A8BDEAA593BCE246AD3B9D5FE33D8C693A3DD68728BC9A1C25BE64ACEABCE0063ABEA04A30BD063F3ABD31C4293D0B03103E4C5B4FBE20208BBDF5986F3D52E45ABC64D3A23DAC65233CF0617B3D085B153D5C79BF3C645ACABDBE042BBD2E158B3CAA08C43DE69AA93B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('65', 0xBEEF6500B8708D3D9EFDCCBD37BBC4BCAE14DC3BEE9831BDCCCCBBBC8AB423BD265AAFBD620BBEBD79D9673DC69E073CE4355CBC9786C2BCFFF6893DB08EE5B9D79EDA3C293C853DD69B69BD6A6B56BD046307BEA3A5873D06E760BC8B89C8BCED1D253D438982BDA8192B3D1BF695BDE879E13D74508FBCE2E476BD88F5873D30D242BB5383F5BD06A1B1BD16FB813B2296DDBD05FF1DBD7792933B040492BB99E40EBD429EBE3D529815BDB4F6F0BD3A7DB03D9BB2CBBD42DE22BD2A3FACBDF945FF3C01DAECBC5CDCCB3CDC84A7BC2C79983C4F8953BCB48D903C0177C4BD6B1FE4BD24A8C3BB54286EBC1807343B2AB35F3D88688FBC0982073D87F9223E926B0C3E4B1453BDBADECB3C0A35153E6A8997BD7A222DBD3BBE993D66F8BABD1C0D8D3DD25080BD66AEBBBD5E72B63D4828B63B0F97D13D17EC16BDA252B33DE2AD58BE991EEDBDEF0ECE3CE018393B686F4E3D25D1043D1162063E1063FDBD9A9916BD11ADD93DD82594BA8A1F6FBD88E1E23B6628FFBC9F39983D697192BDC44DEBBD9E400E3D0B44C93DDC207F3C353489BD53D60CBBFAE6EB3D0193093EA408FDBB00358B3C547D9B3B9CB43B3EB65F893D7938B23D53BDFCBDA15DD83DAC9A1BBE8F654A3C934D953C2E4A253CCF6014BE1D9A0BBDC1BD16BDE440993C94BD0FBC189F043B2D02CE3CFCB402BC5231AF3D3189ABBD8E1E0FBCA57C6C3EB477D43D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('66', 0xBEEF6500E2BBCF3D09FD20BE7A7B0EBE1C44983DC03B9ABDE2D92D3DC791B53DDCC692BD2AA9B2BC8097BFBC9397AE3D448AB6BC57C0FE3CD27F8C3DF4B65F3D733E953D85A5A13DE685EABCF069173B2851ECBDA46B683D625EE8BC88A610BD4ECA4A3D3A0A44BDC6978FBDA3394A3D7A70D63CC891473C3EA186BC811EE6BBBAAFF53D18E5E6BDC09DEDB9384EFDBD4AF2CFBD26850F3D1A084BBD749ECFBB1861B7BD2A5F383E902E6DBDA1D4D2BDC577213E6F9EA6BD9ED8433DDC199DBB905FA13CB853963B4A15243DB0A21A3D9625BEBCC2A6B3BC709AA8BB4E0FB6BC6C60C9BCA60E293D9825ECBC7A9B77BCF08D5C3DD7B28CBD0A52C13CC1F6EF3B5ED4033EAEC484BD2812D33BA0F6963DACC86FBBB07A91BD931CE73C22868CBD09A4C03C4AC8ADBDA733353CF53C86BDB9245DBD958BA93D494BEBBD9EEEB73DCDF265BEF941A1BD2013DF3DF08B173D8504893C880BE3BDDA51B33D89A62EBDEA498ABD9F59523D17FDB33D001F14BEA479FABD696027BD5B3DBB3D6C2D54BD9A6DE7BC10B86BBB1CA4933D485FB23D008FBFBD0F901D3E828262BD82B210BD4279153DEE970C3E7AC90ABD05C2A33D6A85923CFE27603CE0DA88BC460CF03DFF949EBDD607693D32D26E3D8693113D180871BDC012E53DFE32AC3DA497F33D3CD6643DD077443DDABFC3BC067B81BDE0762A3AACC534BD29A48C3D5726003E0873E83D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'BLUR', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('67', 0xBEEF650006CFB6BDFAFB8ABDCBE8FC3CDC0E0A3D4F38B6BC7F4D8CBDE8D7D6BDBACADE3C1F9216BDD02CCEBC031CD7BDA65CFFBD3D28C5BC5E41953D8EE80E3EF0D5F5BB6C79F13A2DEB323EC177FBBD7271493DC82D1F3C7098B33D70F293BDB66506BDBF2CAE3DAD721D3D16FB58BD324D3A3EB0B2EEBD596019BE77DE673D4AB4873D583AAABDCAF4883D187A8ABBA03FC93D75A0073E1C9A373DEAF2ABBBF6C5C53CC075B3BA97CFA03C7E83B83D9EBAE23C507EBC3DC4CB6E3D8EC061BD4ECDB4BD3CBC523D93C9E0BCC3D2193EBAD5AEBDE879423D69968EBD1C6DCFBD7BC8B5BC5BCF193D0445ACBDC0F00FBEC8AAFF3C22A3813C586FB0BCC4B7203E8F2F96BCE7C4EFBCCA0BFE3D34C7D53D16FBA33D7DEF813DE53D0CBE11C1C63C14E1B1BDF5A0CCBD52B3803DB8E58A3D6A49D2BC12E9413E469963BD70CA61BDB56C8DBD25A1A1BDAC6517BD7D8F8C3CE39168BC918C13BE74F4C03C33613ABE4C3574BCF5FB233DAAD6383DE5181CBD9545703D0E52B2BDCFE5D03CE7EF91BD2CDF573DF87B003E60E2D9BDC7EC963D3E6B81BDE863543D17241C3DCD73F83D60649F3C5DBE95BDFCDC11BD0EE16D3D888F67BD690BBE3CB03268BE3E4570BD780C583DAC63C7BCB2DF133E28DFF5BBC46A7F3DCBE9493C616A21BD00BB253C1823D2BB1F03DBBDEF43193D711CA13D2C3E223D2E0D64BDE78DA1BB18EB84BCCEBC61BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('68', 0xBEEF6500122A883C6200A5BC16E49CBC2026B4BDAADF5BBD97F705BC1B3C7FBCE847F8BC522E8FBD349A7BBDB898CDBCF6F104BED97659BD218F4BBCE9C6893DC8C9D0BC78E0E13C254C4EBCCD4FA8BD00D3D9BD729FC6BDDD90863B816F643DA6B1A2BD02770BBE2DC12EBD2251C6BCC4594B3C4695D1BDFD0FD1BDEEB7A1BDF49C4ABDAF7DC4BDE069863AA43B993D025F59BC691FB23D82ECC8BD5B18883D6873F0BDA772C33DAAB4463B1D3EEEBD9C262F3DC4F5CF3C343C0CBC3619B8BD9BBE06BDBEDA863CD646FA3C92C80A3E977BADBD6BEF9EBDDE11C93BBFE8A83D164C9DBD7663EDBCEA65343D3E90773BA78636BD98E479BB5059A83CFA76B9BA84BD5ABBDED7D73D14C6D83DF301053CCA53123CDAAAB43C4EE33A3DB274CF3C809D84B9A5DB163EC8B21A3D52410B3E545A993C86DFE43D3ED46BBD1B9BA0BDBE9B26BE1A79783CBAAD303DAEBA0C3E1E40A7BC91B8803C83C924BD6A0BB4BD289741BD557BEDBDB8B58C3D3A9F19BE3E0859BCC45123BD8CA9193C662C85BC6C7E97BDF39DB43D7E3011BE7A2F8A3D6460FBBDCA53C63DCB0CF2BB2EEA08BDFE86233D41959CBDF21F93BD20969EBCDF19E73C6C9286BA703CF3BDEA6CCC3B6E2F1DBED7D3223DB5EADA3D20C8483C84461F3B3575BC3CF2EC193D1B1A883D71D099BC7260CB3C1842583D3345E03D806086BDF86121BEE0D0B4BD24FCC1BD5C4FC1BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('69', 0xBEEF65007681E5BC029DECBD58E522BD33F37EBD80594EBCF05D8F3CF8BE583C53BBF13B9FC403BDE11182BD1747873C27E3BBBDA693413C6AC53DBDE09B613DB66B94BC06BA8EBA02952BBD23F2A63D7755A1BD82D7653D908FAFBD83817DBD60C497BC3A77B0BDAE2E5CBDB6F5443D6B36E2BB443CC6BD832E24BE08C0EEBDDA3B2ABB21D60DBD0E5B0F3DEB15FDBC3DE519BC7AD6023E1850ECBDDB3FB5BC93B94DBC82EBEB3DEBFDC13C63CE8EBB4B6EEB3C105BD7BC8EA3813D282ECEBC42D60ABE43B83DBC7311E2BB63B6913B3DE5833CED1DFCBD97BD31BD15D3183DC61C81BC71DABDBD55240D3C1A9C10BD85A123BD4A6581BDC3845B3DE294B8BCD38BE53CFDE3503DA50C943D2296873D27E280BD4384983C62272B3DADDE8EBDCADD1DBB72282DBD9AD4D63D2534DB3D9A06563D9A22913B001179BCC12CC9BD5D84DDBD02D8193DA3DF143ECB0C843DB0B3FB3B4FBDC4BD67ECA63DA3BE80BBD10BA73C2264FBBDC60A613DE6FDF9BD2060DBBDB3D1F3BD1654C03D4A0E19BE02EE90BD805F2E3D5AE4AABCB04F263DE5A1EB3C36E4733D1BB566BCCDDEAABA4A94D83BF39B53BCD4209ABD12EB1ABE15F975BD67A9AF3D7BF200BE4ABF9DBC678EACBD54F3C0BD85F1A7BCFD23FABC5A6838BB653AD1BC9871F33CCA05BD3DCD0B4A3D3A1BF13B252E1B3E1E82133DB0DB64BC020910BEE30E2CBE8C2A28BC2EDB993D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '5');
REPLACE INTO intellif_base.t_filter_face VALUES ('70', 0xBEEF650047013EBE2C9A11BEEB73DA3D5CD5D3BC25B7B13B29D38BBDC56C80BD872978BBCCD913BE442B9E3CA8AB693DF47C273D504B723D1BC6ADBD8BF834BDE0C829BECD8E8E3DB0D924BCF57BA3BD3B6BE63B77B15A3DBB29833CBB1E69BB93330DBDF23204BDE9DE153D8D007FBD4D23DB3D83C22C3D386D85BD232ACEBD8BE93C3DBB97A8BBBB2ED43CEB99393EBD9272BC7C04E93D886E313B94E22BBE35BBA83C681E363D243098BBA850D4BD992BEB3D5FC06ABD46021EBC034EF03C6F5413BDE5429B3CA039F5BD78D5533CC99A093EF1FE19BE8588B33CBDD17FBDB3ADC6BC880B69BDD8EFC2BDA0432FBDA4E3A03C805233BD85F0173D6D3E813DBF2D0C3E94B482BCD105FF3CF9C3053C6B3A9DBD7B80DC3DD1F19FBD387AF4BDF4A50BBE8F960A3D502D353DD3EEF03CEA730F3DD5DA463C31409FBD7024EB3BC5490DBEFB73AD3D9B029C3B552C313D0B08D33D7C822DBDED40A23D9BE9A7BD4062ACBD4BDDB5BD804B76BD7EF685BDD9B38DBD89676FBDE4A5B4BB7CF0C53C35D24CBDD50EA23AFBAAB0BD85EABB3C698B16BD45D9CD3BF3B09CBC0373CB3BCBE687BD4B3A7D3D0D0E553D674983BB7970ACBD1D86EF3CE1EA0CBDAF9498BD9928D1BD13B330BC057ECE3D5C3FE8BD77BB8E3D9082913BCBDE2D3DFDDDB83DF011FCBDECFFE63C0D02213D7DE0CE3C77B5503D9547FDBA1524693D5199033E47518F3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('71', 0xBEEF65008EAF60BD6A32E1BC74EFCF3D38F0413D404A8BBBFC335CBC8614EBBC96617DBC6F8950BE0383563D7400693A50B10C3D78C26EBC749A1FBEA6297DBCB28211BD95FF0C3E982B9EBDA11105BE720F8BBCA1392BBD12C135BD29F1703D6C4C70BCC7D408BE5A5242BD466CE23D7BCAAB3D30CE083DF24AA33C92F175BD4A885B3D35450FBE38BBB4BDC23A033EFC96C1BCFA56BB3DD6ADC83CC30DA8BD784DF0BD9253123E42380CBEBCF4BABC44613C3CB2E87E3DC80F943D889FDA3B290F1E3E224BD8BD1DFF60BDDE9013BD905710BEE657C93D81EE533D583BA73DB902F13DD2F9043D091BC6BD9018833C03BBE4BC140C513DE4C5833D34AE3D3D98C6F2BC243CA03DFBB2483C7AE1A93D1E991A3D860AD43C67F961BDBAAC98BC5A9F82BC8C21113E24148BBD0C54E3BDCA7B3F3DC4875B3C829F43BD28D4B23DE2FA1EBEFA69593D780D8D3DBCE652BC9E394C3D4739DBBC31CD3F3D43BE86BDE8ABD9BCF35F35BC0EB79EBD138066BDD325653D44619ABDD59B843D8A5C7C3C96C526BE42D6A7BD2FD93FBEF04F52BA55FD55BDE327E13CD80C1DBD5C5FCA3B63E5CEBDD77F483D9037C6BDDEE14D3DC61854BE21192ABD996B0EBE32458D3D445F6B3D6230A0BD1E85E63C6053A3BB9283E73CA401B33C7022D03D103DE13D7F4964BD4C97583CD8EC063D9C90233DA84435BC1FF383BD2A49D7BB1429A13D3E8567BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('72', 0xBEEF6500E8B6B03DF8A5E03D8F987B3C3C91AABCB47F65BD40773F3D57BF3DBD6E2B003DDDF5B4BDA562E9BCF5ED0CBA9CEFDBBD31F030BCB557883CCCCB51BCF50700BE45B0AF3D97DA26BC3D660CBEFCFE13BDC0BE9D3BF379E9BC5C212E3C0CC69A3D3D7265BDFF1042BD6AA5253C57FEC7BD035162BCF2E310BD8AA99BBDC8AD343D94F54CBD3A21003EB0926D3D7F9216BCDB0C27BCEF4AC4BD7133A7BC951DE0BD6584CB3D0F5C6FBD6C009ABD55A1723EB57899BD509747BD7D9002BDDB95153D60BD353DA9ED8E3D4D25363CBB9C0DBE08B2313DA654183D6FDF813CC714C1BD0D4CA2BC803A0DBAD48FEC3CDB2CDA3D5DF7923D2F4A6B3D2CB6E3BDBB05D2BD6C99403D99A2FD3D77BD093C98BAC73DD378233E6B81BD3DE8BB613C5DFC5F3D4103863BFA45923DF989AA3D5587FCBC735936BD29CD99BDB53008BACFB2EDBC9A1887BCA0D53C3D7C7A813D4D60D0BD02301DBEF41B603C3FC946BD808F4CBADDE5BEBDB7687DBD2C43BDBD49882E3D82EF88BD0F1D7D3C9F7A83BD6B1B72BD07CDED3DE9EAD73B1190D33DF3BF84BCF5C4DF3D27FE3B3BF07FF13CB3FAA5BDEDCCCE3CD0F529BC7CB83D3CADFD8CBB3FF661BCAB8B1BBEBDE893BD5FBF91BD24C46FBD37CBE5BBD046793CF0FB993D072D24BD3671173D25ABEA3DA0C96CBDB19D143D843087BD003BC23DA570A5BDD2D389BDE8E7593D7DB53F3D2303B1BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '6');
REPLACE INTO intellif_base.t_filter_face VALUES ('73', 0xBEEF65003866583C52CF5A3CC895613C829875BD196F1F3DFEEAC13C2B74B9BDB626F3BCEAEAEBBCF297133E02723FBD8DBEAEBD1D4359BD02C9B4BD0BCB633D3CB34BBCCE02B2BD2AC9513CE21DB1BD6D9D293D09A06CBDB78C1B3D1C758D3DE4BD813DFED28EBCE7E2B4BD2AD8873C0A0EF73B56F7313C56CD833D19A897BD053200BE2CDE9CBDE6CCA03DA13E1CBD82537ABD7E3AD33D57524D3CDCF0C9BD999E9CBC1DF119BD41280B3D9CA63DBD65B62D3EF92E323DCEE12D3CDB256BBC087B03BE9A6DD2BD75652ABE6EDC973DE5D6DABC9214BABD480A15BDDE66703D1D8C04BC7E2CE3BC18FC7CBC95A7083EF8460DBB6858D6BD1A340CBDD1F6A3BD66F55DBD0FE9F1BD5F6F403CA06FCE3CBEA7053E57BB0ABD78592DBE0C3E483D196A96BD4AC8F7BC3A7C08BDB6DA613E49B06FBD015D0BBD8982E9BC0C8509BECE8E17BE369710BCB4BE573DBE678A3D43B9C83DF62C3A3D0908243C2CF37ABD6BA1553DCC9209BEF882283EBE2D01BE72D659BDC047C93A0616D53DCE447DBC3AA90BBDC09B36BDB51617BE462608BD38253FBC1B76153E73BDF0BDF048F73D986B81BD3D75223E9A4D3ABDCE9BBE3DFA8F433CA2E741BDA281C5BD5FF4F1BDCE99FE3D1023B3BCBD03183E26C9D73DBE5F543E651CF23B541C8F3D3E6891BC5E9EE5BC34A1463B9DE4E53BE9C4643DE8C65DBE10ADE4BD26DA3A3D6C590DBB396CAFBD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('74', 0xBEEF6500811A7B3D30AD863D2181CEBC6E6EA03918DB25BCCFC302BD56371D3DECB30B3D927E25BE8AAB50BCF814D8BB3680ECBAAF908FBC4B5C0BBEF385043E0AB3E0BD5FFC313DAB6BC83D13907DBDA7222D3D78D3463CC3E4523CCA3E83BD308DB53D2926C3BDE9CA8E3AC4590F3D80086EBA868DEA3CEA0781BD45740CBE31ED70BD52FD4EBDA99B0C3D3E8AB8BC5E55AB3C722F7B3BD1FCDFBD92C62B3A311EE33D524CDB3BEEA759BD4B3BA9BDDF2D0F3ED6D944BDCA5A98BD5D62AD3DAF4BA33D47EFFE3C6814C7BDB13FA83DABB4F8BD574726BBB931433D01E2B03DEE414D3D8752AF3CEE119BBD41AD71BD450AC5BD20F11A3D06903D3D455939BD9A8C52BC0B53693C56D2033DC2ED693D0E8C553C3BC20A3E2EAE023E67FAAC3DD1A4D1BC8E21933D77FBBC3D4260663DA61C473C2BA647BC0B2D4E3CD79A04BE2AFC89BD3E45A43C62CF703C39EDB63D7AF95C3DDE5981BD99D1713D995379BD1EC4953DABBDE6BD8BF7823C2CCA0CBEF95CE0BC6E1C49BD15B7B53DC574473CAE5545BDC2ABEF3D32A7E3BD87FCFB3DC66810BC36260C3E20286A3CAED0A1BDC1D1353DEF3128BD77BFCDBDAEDFD93CA71AAABD0148ACBDB86FB4BD52D93B3D658ACCBA7B64DABCA513253C3F53CDBD89A0C83DD719373D0F57723E13A1143DEA9182BD1EDFDC3D8FC39EBCD91A4B3EB0993FBEC30A6ABDA13D013DA2C0B6BDB08A0ABC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '7');
REPLACE INTO intellif_base.t_filter_face VALUES ('75', 0xBEEF65001EC290BDC0AA193D4803B0BDE0998C3C7A0E92BCD87C36BC7C28B8BDEAFEED3D3A00B0BC47E1C0BDA0755A3DB3DB18BEF2B033BDE5AD833D4AC8EB3C0BA0A4BBDB5AFA3D84E9813CA4C07BBBD004BA3DE80CAF3D79897BBD581BEF3A4CFCD13C2E9CF2BC58B488BD29FC073D0D9381BDC8BF3F3DF875A43BE0FB203D90AF773DE6FC663D7281E63D4F6DEBBC96F2E93C9A917C3DE150F2BDDE7D2C3D6AE767BDA8CF9B3D50214FBBAC8D303DA8CECC3D624F23BD3259133C180273BCD422C83D0CB50ABD72A8F1BC3C5D66BD7DC95EBE24B55DBCC29A84BD7E55D53C941DEEBDF2D345BD2EE1D7BD430D39BC74A71B3C8692CCBD6E868D3D86050B3DB6BECB3C4954E13D9E58E3BBB40A8F3D6FEDFA3CA0BA5ABD42FD56BD985F53BDE597F63C40448B3D5AF079BD2F8E22BD069AD8BBEA5E943CEACE70BD2FD987BDD4C54BBD04CF09BCBA570D3E4663113CC87F1A3D28D569BD76DB0E3EAA3F87BD1E8A92BB62E7AEBDDC76783DB0D691BD9B877FBD18280EBE7247473D1B8752BDF9A3AEBDB5E8673D659E6F3DCD81CD3D96B8DF3C462CB93DAD6899BC97D29C3D1DB2E3BD6B2C923D2805D7BBD864D23D146F10BE6E778FBD909849BE2F07683CC8DEB1BC0C2BCC3DCEC4223DE134B83CD283A3BDC864673D10B54A3DD423573D9E12A6BD5071F43C6E68D2BD973ED73DC7E4ECBD5AA0D5BDE2A80C3D73308D3B5BAA573D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '4');
REPLACE INTO intellif_base.t_filter_face VALUES ('76', 0xBEEF6500BA0CB4BC2D46EC3D566017BE2F2D26BDE3632CBD867338BD7BCE8ABDCE77403B3AE33DBD21846F3D1B1E7A3D784A25BC8562AF3C3F77E6BDE81D88BDB5A006BEED170A3D7158943D0A0F7EBC101D033B1FB4003CCF5F113DA79F36BD135F53BDB93229BDE7E9C3BB6194B03C7AB60EBD86246E3D468D27BEA886903D93EBA93BEAF1ADBDD6C90A3EE7F4743C7EAF1E3DCE77863DB170173D30EE1BBD40ADA8BD8FD0A73C5A91A43C4DE23D3CED7CD63D2F55EDBC4EF76C3DCB927C3C4365863CE5A5123D9BB90EBE90104EBE0D11BABDEBCDAABDDAFE6CBDB22FB43D4262D5BCA59CBABD7A9A3BBD1F5620BE15637BBDD20587BC4223133EAB3CF1BDA53E3CBDA853053EEAA5B03CFF978E3CE25A723D0FE2CF3DFE8C2CBD28FC9BBC05559BBDA1306CBD6E33E13A0296933D7FEA0CBC17CF2CBE85F5AB3D789D073C424B26BEF96C303D3E75FD3DFE9752BD1B73B7BC4B084F3D8CD6013EB76E1CBDA6B6093EA9663BBEEDA0903DADA55BBC983DBEBDCEC400BCF14C263E25FD57BD1A01E2BD0B8F733D029CCC3C7AA5D23D412E373D2EC2223E203A96BDEDEB7A3D7932E2BCD06D943DE70362BD51A3D5BC271314BD523F21BD019A67BD95AC863D477900BEFAC9923D5549993D97527A3C114553BDF1AE4CBDDD8A4C3DCB6D1FBDAB81E8BD3E791B3D0EE63EBD06F8B83DB09BB3BD76E3ADBC5EC2BDBCAD00E3BC4D6EE73D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.902', '7');
REPLACE INTO intellif_base.t_filter_face VALUES ('77', 0xBEEF65009CE8AABD9107923CA799413CD36D42BD98CCA03C77DA51BD309041BDCD606D3D6FF6A5BC915501BC1D757DBCACE402BE09FD08BD7BA0ADBDD84B13BD0882ACBD6B2A213D380F57BDCB1E123A077DA1BC6F5A13BDFC2B16BE5FC6943C93978ABDC40325BD8006FB3A59AF033D759D9EBD878347BD1DC2F03B3BF0C8BD71829ABD880533BD6058A73CA8058B3DF197333D4001983DEBC701BB5FF6033DDB6A36BD80FAA93D585FF4BC2D0428BE649F043E4D87F4BC08DE1CBCFFF618BDB0F1B63C7CF27F3DE55C2E3D04CEF13DA5EC6BBA866300BE28A5D93C91F194BCFB00983C2B3A213BE3CDA4BDBF4864BCAFCA043E050322BDB745943D610E863D4471913CAB358F3D8F231A3DE42BA93C43B55B3DB774623DE0E72F3DFC8472BDA39837BCE99A92BD9D73943BE0004D3DD5CC3B3A9586FCBA8538A8BDE4EDE9BD96FA0E3DCDF5A1BD98A82CBB1015663D5185D7BBA97130BE7F450B3E9FA55EBD3DB87C3D00A9703C21C83DBC43B14BBEBDEFDF3D27C698BD0F6AB13DFB34C23CFBD68DBDCCAC8B3DF732F5BD3550F63D5D2B8ABDBF73F73D9F32BB3D6FA30FBD8F64F9BD111FED3DF0971BBD81EA8BBD3FA889BCEF3922BD7BE9D5BD8F6C0ABE04C0E0BDD3967CBD6BAB7FBB74281DBE855CEEBB2BB85B3D8BE8E939ADEB453DA363B23B6F35A43DE7F8E6BCF8C2CA3DD006BABDF44196BDCF1D21BE2577A5BC20DEE0BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.905', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('78', 0xBEEF650068072F3CA566C8BD4FA73DBDF1E7E7BD1D52B83C45B073BD70E357BD2F492BBDC7FD19BE013EADBB0D8B8CBC468F0D3D80180DBD3D8D54BC69A5B03DB1CDC2BD38B4E63D8F4A09BDE353333DD3F0AABDF77B063D0B79A9BCF099C5BD53E4F3BCA7BF783C20CCED3C4DC51F3D77870CBD18D648BDA8CFB0BDAD9A81BDC9D5633C0B8836BD115B0A3EB7579BBD8840053D33E2C63C8B1559BD3D464BBC3BB708BE9CBD853DB1DC38BD7140773CA986B83DB675853C9540653D5D4A2FBDB7F108BEDD97C9BC740D95BDC559013E25D817BE65CC01BEC09BB2BD83BD7ABCCC9A7EBD46118BBD4FC73CBD8B9A4BBDAD985ABD6CEFB0BD671D2A3DEF4E9D3D60C406BD4D08AA3C1C93873D3BB567BD2FDF7EBD85F69BBC1C5C2E3D9000B0BAC4D94F3DB84D07BD88CAA53D843E883DC0E5723DD8612D3D7FC27ABD994BD3BDA51781BD375C183BA8482F3C73861B3DFCF416BDDF9155BD8F76F73D2B5731BDAE569B3DB53AE4BD05F6D0BCDBBF20BE31740CBEC83914BE5886D13DA304E13BB88D8FBD7D1F963C3DEDD8BD48986E3D487789BDEBF4F73DDFC5BBBC912D483D37D277BD7B5731BDA0ABA73CB353E2BD00FF5DBE852199BC30CF4FBE537136BE40E9D1BD6119CFBD23A1363D7B5C59BDD07EC2BD1508303DC4A218BD91E9A83D6859BDBD109FD8BD2AF0243D8B83D63C008DE83CB5A15DBEB4F798BBBC9981BD5EA499BC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '6');
REPLACE INTO intellif_base.t_filter_face VALUES ('79', 0xBEEF65000ADC31BD92EE3C3DCE33FEBDB54EB4BCE5AE16BD19B5BF3D2804913DFC38F9BDDBF917BE0302D53C8AB9EEBCACFD8B3C07C5BC3BE3A696BD0374493D6B9320BD38775EBB05DFDEBC03B2D2BDC384C6BDD09616BD20247CBA5ADD93BC6C6EE23C110DD03BBA1E383C9014A33CB2B0643D4C7226BD36879EBDE536A3BDBE4C213E62B09DBCDCCEAA3D2E2F533C67B615BE216DFC3C7F3C17BD5B59A5BDB76BA7BCB660E4BC21800ABED46F8ABDDA00903D9A4DE53C52BD873DF8D0FABD9CA8E83B067BAD3CC001183DCE7555BD429103BE73E2D2BD18C877BD1FCEB13D4BBD073E5437763D86146FBDEC83663CAAB026BDF5E5A83D7E5E923DBD24B33D7C7F14BB678ACF3D9C4FF63C0D71EC3C78AE613ABD9BF2BC203982BDABC4A4BD80F5B5BD259FDE3D1BB96BBC02E7DCBCE50D96BC2D3E46BC5A9512BED43BF73CEC8E5EBDB8F576BBB17C9C3D374104BC05B2A73D949B47BD3D76813D9BFF6EBD981B6B3D6B127DBD3A5016BC9B963DBE4E3741BB02DD0FBD1F0B5B3DF6212EBC7A206ABD1A81AB3CCE968FBEC71FC93DCF9C0E3DA641453DB87097BD81D508BCCF9BAFBDAD1691BCF0F450BD067EC6BD4AF4ACBD1D6A0DBEF9445CBD7752FD3C3612EDBD653B95BDF934BB3DA0A87ABD6DE6FA3C6168FF3C822085BC2D7A1DBD9679A4BDBD02C8BB0B244FBE394BA63D083CADBD5FEF963DF4E011BD70CC77BD61FB25BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9069', '8');
REPLACE INTO intellif_base.t_filter_face VALUES ('80', 0xBEEF6500AB49B5BD7B5F6ABD882CB53DA5C5343D0036C439E9A09EBD857B853DAA4A8C3DF3D5FDBD893FB83C9BF5E03CDC60A03D49E05C3CD6D6A2BD156DC53C881763BE5DFF45BCDFC0573DE21701BE0FF2973D575A333D7F74863DE8F8E8BA6DAF62BD0FDA43BE108C333C2BB3C5BCBA44273C944E7A3D1471FBBDBE681DBD643AB7BCBF108BBDC8E0D63DC0608BBBC89DADBD6413143DC37793BD852A69BDC713F23CDF8BA43DFD6A8B3DCBAD44BD5005F53C2471C3BD1706A0BD235EC53CE9CE393D38324ABD8B1441BD3B201DBEC892BEBC203807BEB693243DDC12533D53A715BDAB1EABBD251F62BB45CA02BEC018F2BC607F30BDA102863D703E3D3DB106C33DF9F64C3D670533BD7C54BF3DB4739EBD8F84883D1C8AEC3D00BF26BE804D573CC037E83D89CC033EF3E689BD693B823DA5F3B83D4055F63D23B6D03C511B6ABDA8F02D3E9F20C33DD30BF33C9095093D9C068ABDB3E20F3EA8150A3D757A893DD76B92BD33344B3DBBD034BE5029C9BD9458033C285D353D15799C39BFE74DBDD84DC43D85D6E3BCEB614A3EDF1B963D4BA4CB3D45D9FDBCCB37FFBC4D837B3DAB481ABA64E2D6BB80115BBDC8A50BBE50553C3D3447D6BCC2D49C3D630E0BBEDBB2B5BC070ED33D10A4ABBBA31564BD50DC663DFBF56D3DF5F71E3DD9EBD7BD1D47993DFDB883BDD36B933DBFE596BD550EA93A0CBF513D6975CB3B6DBDA73D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '3');
REPLACE INTO intellif_base.t_filter_face VALUES ('81', 0xBEEF65002BDCFD3B2D65B13B98E615BD36C51D3D0FB730BC4EBF69BC78E4C13D2C5107BE7D43FCBD086622BDDF2684BDCCEBE53C3E88133ECC33B23B1AB9753CC70C99BD72EDCF3DA6A0203B37F1AEBDC5152EBD6E2DF73D6D1FA93D4E6EFBBDF6D94E3D54F7C23C03AA58BDDA24CD3CBFD9723C4D9C2FBE46BDBCBC307C6BBCCF30EA3D0CDAD2BDACDBD13D70F18C3D1A7AD6BD196015BC36E84BBD4A92F7BB28D9B7BD6FAD143D8AA5DABD4E58D2BDDEB8F33DB9D04ABD9BAD80BBD2594DBD4183AEBD440DF73DC3308FBCD1E2873DCC3582BD4CED25BE8BE9373D3F0F223EDA6A943D86F5F4BD4B7D48BD1CD89F3D83BF383C0E3E43BBF437C73D0B4F8ABDC6F6443D8267643DA2C7213D8863B13D449018BB69DC913D15BF453DB08398BD71D8A9BD4E078B3C7DBBF23D4C61253D6AD902BB1EA931BDA64A8ABD713C933C389D43BDBE3ACB3D00B6613D349B84BDC2FB32BD8B17C53C28AA873D0B5ADCBDD453653D7038AFBC1F24173D420A1EBE901804BEB939EDBD60D2893D3674F93CBB263CBDB295BFBCEE388DBD11B3553D1FAAC0BC4D29AD3DC2F36BBDC9E380BDE8FDF13B6BF1203D30F7383CDB9AE2BD8F9890BDAA2ED4BDA8DC0FBCD2A9F8BC48ECE2BD9623A1BDFA14373D84BE40BD081075BD2CD9C8BC5AB903BD29D42F3ED31CB23C91E944BDFB5AC6BD4197823C281F16BE17BA04BC0A7DE2BC680A6B3D6B24E2BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.9', '8');
REPLACE INTO intellif_base.t_filter_face VALUES ('82', 0xBFEF6500DA41E7BA70570A3DD5DB70BDF5A03B3C78921B3CA0D3F4BD8D03DEBD897C003B2F7DA5BD91030C3D33FC0E3DBC6405BA0D57CC3C68AFC8BD6B8B9D3C5F4B87BD0F530ABDA0F04A3DCD43E6BD6AC3D2BBBA1366BC0CEE503D88BD4E3CF978533C9B35D3BCD14BDDBAC038123D1184DA3DD41A123D130FBA3C4BA2A3BC6657913DA828C8BCC68D943D85C5A83B0C126DBC456C493DEDE0C33D1A7A06BB7E4779BD7D343BBD2C3F88BB9475AEBDF960F03D985985BD95DBCA3B016E88BD32A0703D850DEDBB77018BBD6D8A983C5BDC8EBD216901BEF851F5BCD9DA103C98498FBD43E5B7BDC47314BE0688E7BC98996EBB265C34BD70280D3D3521173DDE076D3C8990AB3CFE72613C8842973D9A32123C4086393DBCAB5CBC69C4C5BD5AFE39BD9817293DBDB9B2BC2F47A83D717EFF3B75518EBC4D097BBB2BD904BB30A6E3BD59766C3B93061CBDD2526B3D60C99C3D2F70513B239C253E42F7DBBD2A0D023D916292BD2022053DF93CCABDA8BD90BDB01AD8BDDC35A63D9FF9193D48AEF0BC18440E3DA248F4BC7FCD823DA8A52EBDB7E4BE3D1EAEC4BC172B9A3CE2D688BD1170E33DE87A73BCC4A6B23D58C4ADBD68CBCDBD694CADBD9986CFBCD81AAEBD709E093DA05D1E3E510E97BDD551483C22FE1D3C0A53273DADC0D73C584D0FBEA8EDCE3DD4D292BDAD642C3D84F465BD9C4708BC50DAAE3D87E4853DEA7D2A3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '17');
REPLACE INTO intellif_base.t_filter_face VALUES ('83', 0xBEEF6500D539FDBD50F8E5BC5F74A73D1F8270BDD7DA49BD3891E03C2025D5BDE98384BA81BD59BC427D02BCB01CBBBC943D40BD1B864E3C7DB068BD0923E0BCBDC2CCBDEFE59C3D3C57383B3CDC02BE4F8F6C3BDCEA7E3D55C3253ED11BFCBB65EE11BE40FF35BB7F52C3BCC51C07BD98377DBCBDBEBCBD862E253DED133A3D9531A1BBAB6DE8BC097E1E3DE927C13D60F6F1BDBBC0013E81B2643C55D8243DC41A06BEA06BF33C9C70E03DEB7550BD254A203D136B67BD23AB583D8B78B33ACD37DF3D3D98D93C47C649BEA89589BBF078F0BDB9A04DBEC3FA093E80EFF33DA8C62FBDBDC4ADBDF9667FBD2C234B3DD8471EBE984477BDEF14FDBC3D8D98BBBB5F89BD09E7673D7BF7123DF52A2B3E9157A93DA5A65C3D799C87BB758AB03C2F06003C0D97843D7907C9BCB2B3963D57141CBC1DE2023ED8104FBD338D74BDF1A29DBDB59074BB697B8BBD5DA5113DE97E3ABDA7EF2D3C7BD784BCFF7DC3BDA092473A741DE0BDF9AF053E573E883DEB2479BD487133BE03825E3D1C24423C740246BD6C4B783DB1E213BE78AD44BDB6CC8A3D9614193ED0DFBEBB655C723D507DB4BC98CC303D3025FD3CC7C8B3BCFCCCB7BDA97F69BD44CF30BC28FD8CBD20414D3CC966E23CA17D023E5CF629BD60FD613DD797A2BDD4149D3DE0CBAABDDF5619BE4820E33C1D72523C10FD433E017888BD2158B8BD38DDCA3D9723A3BD155E6B3C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '6');
REPLACE INTO intellif_base.t_filter_face VALUES ('84', 0xBEEF6500F8FEB7BD0E6084BC6651BA3D8C0BA43DDF2D55BD14D6CE3C9EE1B0BCF58DCD3C09308A3CF700943DB5EB353C9BEA58BE932A9B3B821A183D658AC23C754CB1BDFE85423D455EE1BD8C384A3C909A27BEF521AFBB84E0FCBD20B0A3BDF31EA63C23C986BCB68507BDA47F193C0C4F3EBD9C44B6BDE55076BDEC39E7BDE9031A3D7A08A6BDC77E7DBC51EACA3DB5346ABC642B21BD0833D4BD7526C4BC5E4787BD6902773C4B29BDBCDB144CBCFB00DF3DC7F3AC3DD25EC5BC1729A7BD293490BD07AD683D941755BD53D730BD2E05753D094761BDE497E8BD70F2F5BDD39E8D3DF5DB2EBC797C343A7277983A5E6CECBBB624A03CA42C553DA49C2E3DD2D8683CA50BC13D3C2BF43D84DC373D247955BDC770443D79A0823D84E95ABD1E18553BA12D0FBEA5698A3C40B2ED3C9019D3BD31B650BD0EF5DABC4C93FD3D20DA2ABE29C615BE51749C3DAE5AE9BA92B05A3DE7CBD5BD98D7883D247FB8BD903CA7BC47B8903D644D7EBC306C5BBD23D7553E61C330BD241A043DD6559ABD0043A03D6C07BF3C208BFBBD92D1333D3FB0113C30FC82BC047CB9BC2025BE3D71A1DABDE6970D3DEE8B02BD815801BD3210D93DD4C5233CF565ABBDE3CAB6BDF204E1BDB081113D1920B43D6636CF3C4472243DFCBCADBD71D2D6BCC2FE8EBC2BA15F3ABC6191BD5346E13CEEA35DBDAE8967BD09B18D3D3901C7BD59E6BE3D478E5FBC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.91', '9');
REPLACE INTO intellif_base.t_filter_face VALUES ('85', 0xBFEF65003046C5BD43FE8F3C6E25213E769AC7BC31590C3DF020193C77B5B3BDCB1093BDDB014ABD8E47603DCA14A6BC5976A4BD475CE63B57B593BD205C873B9E3A30BEC12D003E16A5333DC15039BD993E19BCD000863D3BBA553D955079BDA18188BD4A3973BD251E11BE4C739CBDD4371E3D5B18CFBCA2A6123D071B13BEDB7CB0BCE29E27BD734C83BDD219BC3DA47496BC36D70C3AD29A52BB28C49BBCDFCED73B0B21F53CAA7C7B3B76A74B3DF1CD6F3D2E028EBC8AA73ABEC488B0BCFEB824BDEEC44E3D122B16BE3A6F483D7073B6BDFAE6EABCD47F973D5249923C5770733DF7CF243D5B81CFBD39E69A3D25DEC83B0637973D2319933D7CD9953D62C2083D0A1D6D3CF25D253EA727203E248CCC3CCAB1FD3CB48AAC3DBE29373D268542BDA03DB73DB1B8AABDF5E69A3C4E6C9D3CA9E69D3C2F12EEBB146A9BBD0D2390BBDBF02DBD167F273DDC6F83BD27ACD9BB57C8F13B967B293D25B8F0BD8F78963DA7D2ABBD047EA23D482304BEBFD42A3EDFAE5ABDC62D833D4C36CBBC072A83BD0A92823A3E06F0BDC626153CAEE0AD3C0C4F8D3DFB61213D8B7A9F3DBC80E5BD86EB98BD864A1DBE699612BC278EBDBDD78616BD2C5328BD95DC80BC11BA9B3B5019673DD247303D8C624CBD803A3D3D671CA5BD76DD9F3D97F3AB3D744301BD9444283DFBEFB23C51A0923D4922DFBD860DFBBDE4608FBDFBE82CBC645BCFBC0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '13');
REPLACE INTO intellif_base.t_filter_face VALUES ('86', 0xBFEF65007229DBBCA3B92EBCCDB885BCB294C5BC9733DEBCB73D923DB8F20FBE236A803C6557B9BDAA5697BC8DC5F0BC493F133C8EE9DEBB7517FEBD0AD9683D93CDF1BD0BBF593E53C1583B1E1BFBBDED563ABD297EB53D62D2DCBD18D36DBC983099BC1FA621BD01A021BD1BEB2BBDB842ECBC5465ADBDB8162EBA485501BC50F89F3DA8CE533D9533583C50E1973D3126B03CAB04F33D0D41CE3C7FC9153DF1E125BDDDFA183BB3E4EBBB0D3A1D3CCC75383EB1AEEA3D19181A3DFA2259BD35C352BDA09FBBBC4858E4BDA592C5BD716B56BDE495EFBDDFF294BDF044E9BD3C1B1C3DB37A43BDC8C76FBDC68DE43C375539BD3F006F3C28C1513EC400143CC02A91BDDB1C963CEB8B89BCDC16DC3D60EB8A3D3EDD9C3D440F553BB29D38BD17A8ECBC1CC807BD799B9DBCB4239F3C2E0B1A3EC93E77BD916BA9BDD2A430BB38EE78BDC0C391BD78EBB739F0DE87BD34405CBDC267B6BD98D6013EB7CB07BEF77D213E930AD4BC58D62F3D0D485EBDED5C663BD85CC0BD770DBB3DA37D8B3DCE3280BDA23CE33CF1D6A8BDBACBC93CDAD7A53C201C043C8EC016BED874293D9AA4C8BD316DC9BD48130ABBC4D828BDE63A3FBDF08B8ABD5F92B0BDE4561EBEE36389BD151C4A3D1BAE463D5DFA933CF797F73C40711E3C98750E3D1AA5AFBDA37C06BE460DE83C2091293DA816B73C3D95B3BCA55D5EBDB66B1B3DCC5DA13DBA0C16BD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.91', '17');
REPLACE INTO intellif_base.t_filter_face VALUES ('87', 0xBEEF6500E90BA6BC7057BDBC49A88CBDB5C8B93DA110233DC62F1FBD066124BE1387D73D696A5EBD5BD73C3DFB588E3D01F5A0BC7124BC3CC10BA5BC5F628E3CB3535ABDDD659F3CBFAB113D58A93BBEE3F5043D8117D83C7828F63B5B23DFBCF8876A3DE3F63BBD23A6CEBA56B887BDC9CAB23DA5BCAE3DE51FA1BC7605AFBDB6D1563D2EB850BC45C29A3CF34B16BD9740763C60AE7C3D1F84123D912A9F3D218AADBB0FB535BDABFA273C33D5A2BC2169F13DADBFCCBD599679BD2DF8C5BDD2CB123EA73DEE3C989E91BD2E77CCBD02E87CBDBE7416BEAF23FCBD0D2606BED2E252BDB9F8583AA50157BE92E786BC7EC75E3CEFEC23BDE5E42CBC7B14483D5210E8BABAE2803C7537843C7926DD3D347F0D3D39D39ABA09B26F3D1FCAB7BD1D54463CEF82E03D8258BEBD9DC5B4BC14F007BD70341BBD91F543BC9B6980BD52094BBD3722A5BD0300BC3CE6362FBDA977143D030593BDDD30BF3D33E029BE86D8EA3CE2DE75BD4D66BB3C2A4F99BCC9AA6FBCDD12D5BD2DB2033ECB9ACFBCA65D7BBD9F67033E09C7833CB7DE11BA1121143D951BFC3C01C463BDBB93BF3D51F4FDBD90B6563B2E66F3BDD7B83D3D7092E5BD0016BEBD7D6F7CBD8938F3BB823DBABDDEE4933DF6E3413ED93160BD3091B8BD4E025E3C0323A93C5A7C9BBD6B1400BE660402BCD3F7A8BD9FA66C3D003E9FBC205FC73C208E343BD0B58E3DF3AA1B3D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'CHILD', '0.92', '7');
REPLACE INTO intellif_base.t_filter_face VALUES ('88', 0xBFEF6500DD1399BDCF07583C3973F93D937DDC3CA8194ABD0C292E3DBC61B3BC2A1D9C3D930BBABDC2405E3DCFF7713C84D202BE288C8D3B7B62233D8FB1ACBCA7C4D8BDF37FC93C8A30E8BC207EA7BD4DBA07BE477ECEBDC0FD74BDD13D9ABCB3F63A3DBC763F3D6A4C513D6A10D4BD3D95DB3C5D8C79BDF9F05DBD81673BBECAE0D13B686182BD98589B3CA513233D790B5B3D51A9BFBD6DCE94BD48930B3CA83E8BBD51DF823C7FDFD13BF621F4BC36E4B53D4C7BE43DBA00213CF2EDCEBD97DC31BC6DBBAF3D4D5136BD36080CBA287B5EBC3F8C8CBC2A14A0BD63E64BBE201CE73DB9912B3D735FF8BD2B49113D6A5CFA3C76376CBC99E8CC3DDC6FC43DE36BD5BC0D41F63D5DB3C53DB59F163C740A08BDFA57103EDBF1913D23ED4ABD382F9A3DD2A9A3BD4824DABC9E07FF3DE60AB1BD9FCB93BD5E637E3D632A973CDAE9AABD08952DBE561FAFBCF6A8403D5179973A9654DABD026028BC3BFEF7BD3F3F07BB6CB80EB873BAEABC902AA2BC154A273EFD37EA3BC53CCFBB1670F9BCD48D09BDC28CF33D2B8E80BD1483D2BC2DD31C3C08F245BD17A8BEBC69C5053EAA0DABBD59BA633CD4DCE4BD1B5841BBA44BA63D93C8463D3F7319BED379B0BD537321BD1DC0E13B01A9A13D1F8F18BC7145C73DB4EC3ABE18A7123C5D98A93BE20392BDFFA19DBD426BACBCB59D81BD18D87EBD6C4BAA3DF8FBEFBA609B803CAC04D7BB0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 'PROFILE', '0.92', '19');


CALL intellif_base.addFieldIfNotExists('intellif_base','t_chd_configure_time', 'threshold', 'float','not null default 0.92');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_contrast_face_info', 'created', 'datetime','not null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_contrast_face_info', 'updated', 'datetime','not null');

delete from intellif_base.t_last_capture_time;
replace into intellif_base.t_last_capture_time (id, camera_id, last_time) values (1, -1, '2012-01-01 00:00:00');

CALL intellif_base.add_index('intellif_base', 't_last_capture_time', 'index_camera_id_last_time', 'camera_id, last_time');

CREATE TABLE IF NOT EXISTS intellif_base.t_wifi_access_info (
	`id` BIGINT(64) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`unumber` VARCHAR(255),
	`mac` VARCHAR(255)COMMENT 'mac地址',
	`miltime` BIGINT(64),	
	PRIMARY KEY (`id`)
)
COMMENT='大运场馆wifi信息表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;



CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_detail', 'fk_type', 'int(11)','not null default 0');



REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (1, 'need_visit_p_has_certificate', '查询库-616需走访人员_有证人员','11');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (2, 'need_visit_p_without_certificate_child', '查询库-616需走访人员_无证儿童','12');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (3, 'track_collection_p_has_certificate', '查询库-616轨迹采集人员_有证人员','13');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (4, 'track_collection_p_without_certificate_child', '查询库-616轨迹采集人员_无证儿童','14');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (5, 'escaped_criminals', '布控库-在逃人员','21');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (6, 'imp_foreigners', '布控库-重点外国人','22');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (7, 'be_nowhere_in_sight', '布控库-来深不知去向','23');
REPLACE INTO intellif_base.t_fk_bank_dictionary VALUES (8, 'priority_foreign_country', '布控库-17个重点国家外国人','24');



CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'c_type', 'int(11)','not null default 1');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'c_type', 'int(11)','not null default 1');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'c_type_ids', 'varchar(255)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'special_sign', 'int(11)','not null default 0');



alter table intellif_base.t_user modify police_station_id int(25) null default -1;
CALL intellif_base.add_index('intellif_base', 't_alarm_info', 't_alarm_faceid', 'face_id');



CALL intellif_base.add_index('intellif_base', 't_black_feature', 'blackId_index', 'from_black_id');
CALL intellif_base.add_index('intellif_base', 't_red_feature', 'redId_index', 'from_red_id');



call intellif_base.updateFieldAttribute('intellif_base','t_camera_info','type','BIGINT(20)');
call intellif_base.updateFieldAttribute('intellif_base','t_other_camera','type','BIGINT(20)');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'camera_code', 'VARCHAR(1024)','DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'district_id', 'BIGINT(64)','DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'node_id', 'BIGINT(64)','DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'camera_code', 'VARCHAR(1024)','DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'district_id', 'BIGINT(64)','DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'node_id', 'BIGINT(64)','DEFAULT 0');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'def_alg', 'BIGINT(20)','DEFAULT -1');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'def_alg', 'BIGINT(20)','DEFAULT -1');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_district', 'parent_id', 'bigint(64)', 'NOT NULL DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'parent_id', 'bigint(64)', 'NOT NULL DEFAULT 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'node_id', 'bigint(64)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'area_code', 'varchar(1024)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'node_id', 'bigint(64)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'area_code', 'varchar(1024)', 'DEFAULT NULL');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'parent_id', 'bigint(64)', 'NOT NULL DEFAULT 0');




replace into intellif_face.t_face_filter_type(id, name, created, updated) values(1, '车轮', now(), now());
replace into intellif_face.t_face_filter_type(id, name, created, updated) values(2, '动物脸', now(), now());
replace into intellif_face.t_face_filter_type(id, name, created, updated) values(3, '海报图案', now(), now());
replace into intellif_face.t_face_filter_type(id, name, created, updated) values(4, '服装图案', now(), now());
replace into intellif_face.t_face_filter_type(id, name, created, updated) values(4, '其他', now(), now());


replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (1, 100000, 7);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (2, 100001, 19);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (3, 100002, 1);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (4, 100003, 44);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (5, 100004, 21);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (6, 100005, 6);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (7, 100006, 20);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (8, 100007, 8);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (9, 100008, 40);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (10, 100009, 3);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (11, 100010, 53);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (12, 100011, 4);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (13, 100012, 15);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (14, 100013, 43);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (15, 100014, 38);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (16, 100015, 2);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (17, 100016, 39);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (18, 100017, 58);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (19, 100018, 23);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (20, 100019, 17);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (21, 100020, 16);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (22, 100021, 41);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (23, 100022, 46);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (24, 100023, 42);
replace into intellif_base.t_mobile_collect_station_map (id, sync_station_id, mapped_station_id) values (25, 100024, 18);

CALL intellif_base.add_index('intellif_base', 't_user_switch', 'index_user_id', 'user_id');

call intellif_base.updateFieldAttribute('intellif_base','t_user','c_type_ids',"varchar(255) default '1,2,3,4'");
call intellif_base.updateFieldValues('intellif_base','t_user','c_type_ids','"1,2,3,4"','where c_type_ids is null');
CALL intellif_base.add_index('intellif_base', 't_camera_blackdetail', 't_camera_id', 'camera_id');
REPLACE INTO intellif_base.t_api_resource VALUES (231, '/intellif/allowips/page/{page}/pagesize/{pagesize}','POST');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_red_check_result', 'search_data_type', 'int(25)','not null default 1');

CALL intellif_base.add_index('intellif_base', 't_alarm_info', 'confidence', 'confidence');

call intellif_base.updateFieldAttribute('intellif_base','t_audit_log','message',"varchar(20000)");



CALL intellif_base.addFieldIfNotExists('intellif_base','t_fk_person_attr', 'fk_sub_institution_code', 'varchar(255)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_fk_person_attr', 'fk_local_institution_code', 'varchar(255)','default null');



replace into intellif_base.t_venue values (1, now(), now(), '大运体育馆');

CALL intellif_base.add_index('intellif_face', 't_face_stream', 'index_venue_id', 'venue_id');
CALL intellif_base.add_index('intellif_face', 't_face_stream', 'index_face_id', 'face_id');



CALL intellif_base.addFieldIfNotExists('intellif_base','t_server_info', 'json', 'varchar(1024)','default null');






DROP PROCEDURE IF EXISTS intellif_base.dividedFaceImagesAddIndex;
delimiter $$
create procedure intellif_base.dividedFaceImagesAddIndex(IN tCount int,IN tableName VARCHAR(255), IN index_name VARCHAR(100),IN index_field VARCHAR(100))
begin
     

    #获取face或image表字段集合
    #set @count = (select count(1) from intellif_face.t_tables);
    set @start = 1;
       lab: while @start <= tCount do
    set @faceName = concat(tableName,@start); 

  #添加索引
  CALL intellif_base.add_index('intellif_face', @faceName, index_name, index_field);

 set @start = @start +1;
          end while lab;
     
 
end$$
delimiter ;




DELIMITER $$
DROP PROCEDURE IF EXISTS intellif_base.dividedTablesAddIndex
$$

CREATE PROCEDURE intellif_base.dividedTablesAddIndex(IN tableName VARCHAR(255), IN index_name VARCHAR(100),IN index_field VARCHAR(100))
BEGIN


     SET @count = intellif_base.countDivideTables();
     call intellif_base.dividedFaceImagesAddIndex(@count/2,tableName,index_name,index_field);

END;
$$
DELIMITER ;




call intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'online', 'int','default 5 comment "在线状态4:异常,5:在线,6:断线"');
call intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'snap', 'int','default 1 comment "抓拍状态0:异常,1:正常"');
call intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'online', 'int','default 5 comment "在线状态4:异常,5:在线,6:断线"');
call intellif_base.addFieldIfNotExists('intellif_base','t_other_camera', 'snap', 'int','default 1 comment "抓拍状态0:异常,1:正常"');


CALL intellif_base.add_index('intellif_base', 't_uploaded_status', 'index_userId', 'user_id');
CALL intellif_base.add_index('intellif_base', 't_uploaded_status', 'index_fileId', 'file_id');
CALL intellif_base.add_index('intellif_base', 't_uploaded_status', 'index_resumable_identifier', 'resumable_identifier');
CALL intellif_base.add_index('intellif_base', 't_uploaded_status', 'index_upload_identifier', 'upload_identifier');
CALL intellif_base.add_index('intellif_base', 't_uploaded_file', 'index_created', 'created');

CALL intellif_base.add_index('intellif_base', 't_face_extract_task', 'index_status', 'status');


REPLACE INTO intellif_base.t_system_switch (switch_type, opened) values ('FIX_AUTHORITY', true);
REPLACE INTO intellif_base.t_system_switch (switch_type, opened) values ('CAMERA_TRANSFER', true);


CALL intellif_base.add_index('intellif_base', 't_police_station_authority', 'bank_id', 'bank_id');
CALL intellif_base.add_index('intellif_base', 't_police_station_authority', 'bank_id_2', 'bank_id, station_id');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_uploaded_status', 'is_deleted', 'boolean','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_uploaded_file', 'is_deleted', 'boolean','not null default 0');
CALL intellif_base.add_index('intellif_base', 't_uploaded_status', 'index_is_deleted', 'is_deleted');
CALL intellif_base.add_index('intellif_base', 't_uploaded_file', 'index_status', 'status');
CALL intellif_base.add_index('intellif_base', 't_uploaded_file', 'index_is_deleted', 'is_deleted');

call intellif_base.updateFieldValues('intellif_base','t_person_detail','real_gender','real_gender+1','');
call intellif_base.updateFieldValues('intellif_base','t_person_detail','real_gender','0','where real_gender >=3');
call intellif_base.updateFieldValues('intellif_base','t_user','gender','gender+1','');
call intellif_base.updateFieldValues('intellif_base','t_user','gender','0','where gender >=3');

call intellif_base.dividedTablesAddIndex('t_face_', 't_face_time_source_id','time,source_id');

CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_detail', 'push_object', 'varchar(512)','');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_black_bank', 'push_object', 'varchar(512)','');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'online', 'int(11)','default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_camera_info', 'snap', 'int(11)','default 0');


DELIMITER $$
DROP PROCEDURE IF EXISTS addFieldIfNotExists $$
CREATE PROCEDURE addFieldIfNotExists (
   IN database_name_IN VARCHAR(100)
    ,IN table_name_IN VARCHAR(100)
    , IN field_name_IN VARCHAR(100)
    , IN field_definition_IN VARCHAR(100)
    , IN field_Default_IN VARCHAR(100) charset 'utf8'
)
BEGIN

    SET @isFieldThere = isFieldExisting(database_name_IN,table_name_IN, field_name_IN);
    IF (@isFieldThere = 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', database_name_IN);
		SET @ddl = CONCAT(@ddl,'.',table_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', 'ADD COLUMN') ;
        SET @ddl = CONCAT(@ddl, ' ', field_name_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_definition_IN);
        SET @ddl = CONCAT(@ddl, ' ', field_Default_IN);


        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;

END $$
DELIMITER ;

DROP PROCEDURE IF EXISTS intellif_base.updateFieldValues; 
DELIMITER $$
CREATE PROCEDURE intellif_base.updateFieldValues(IN dbname varchar(100), IN tablename varchar(100), IN fieldname varchar(100),IN fieldvalue varchar(100) charset 'utf8',IN whereparam varchar(100) charset 'utf8')
BEGIN
	
	 SET @isFieldThere = isFieldExisting(dbname,tablename, fieldname);
    IF (@isFieldThere != 0) THEN

        SET @ddl = CONCAT('UPDATE ', dbname);
		    SET @ddl = CONCAT(@ddl,'.',tablename);
        SET @ddl = CONCAT(@ddl, ' ', 'SET') ;
        SET @ddl = CONCAT(@ddl, ' ', fieldname);
        SET @ddl = CONCAT(@ddl, ' ', '= ');
        SET @ddl = CONCAT(@ddl, ' ', fieldvalue);
        SET @ddl = CONCAT(@ddl, ' ', whereparam);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;
	
END$$ 
DELIMITER ;


DROP PROCEDURE IF EXISTS intellif_base.updateFieldAttribute; 
DELIMITER $$
CREATE PROCEDURE intellif_base.updateFieldAttribute(IN dbname varchar(100), IN tablename varchar(100), IN fieldname varchar(100),IN attparam varchar(100) charset 'utf8')
BEGIN
	
	 SET @isFieldThere = isFieldExisting(dbname,tablename, fieldname);
    IF (@isFieldThere != 0) THEN

        SET @ddl = CONCAT('ALTER TABLE ', dbname);
		    SET @ddl = CONCAT(@ddl,'.',tablename);
        SET @ddl = CONCAT(@ddl, ' ', 'MODIFY') ;
        SET @ddl = CONCAT(@ddl, ' ', fieldname);
        SET @ddl = CONCAT(@ddl, ' ', attparam);

        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END IF;
	
END$$ 
DELIMITER ;


CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'email', 'varchar(255)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_user', 'remark', 'varchar(255)','default null');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'remark', 'varchar(255)','default null');

INSERT INTO intellif_base.t_district(id,district_name,district_no,created,updated,geo_string,geometry,local,parent_id,sort)
SELECT '1', '全国总店', '1','2017-10-01 00:00:00','2017-10-01 00:00:00',null,null,1,0,0
FROM dual
WHERE not exists (select * from intellif_base.t_district
where intellif_base.t_district.id = 1); 

CALL intellif_base.addFieldIfNotExists('intellif_base','t_person_red', 'police_phone', 'varchar(255)','default null');