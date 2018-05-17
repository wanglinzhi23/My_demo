/*
Navicat MySQL Data Transfer

Source Server         : 192.168.2.25
Source Server Version : 50616
Source Host           : 192.168.2.25:3306
Source Database       : intellif_wangxin

Target Server Type    : MYSQL
Target Server Version : 50616
File Encoding         : 65001

Date: 2016-07-11 19:56:27
*/

SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE IF NOT EXISTS `intellif_face`;
CREATE DATABASE IF NOT EXISTS `intellif_static`;

CREATE TABLE IF NOT EXISTS intellif_base.oauth_access_token(
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` mediumblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.oauth_approvals(
  `userId` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastModifiedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.oauth_client_details(
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS  intellif_base.oauth_client_token(
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.oauth_code(
  `code` varchar(255) DEFAULT NULL,
  `authentication` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.oauth_refresh_token(
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_alarm_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `black_id` bigint(20) NOT NULL,
  `confidence` double NOT NULL,
  `face_id` bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `time` datetime DEFAULT NULL,
  `face_url` varchar(100) DEFAULT NULL,
  `image_url` varchar(100) DEFAULT NULL,
   `send` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `t_alarm_info_black_id` (`black_id`),
  KEY `t_alarm_info_task_id` (`task_id`),
  KEY `t_alarm_info_black_id_time` (`black_id`,`time`),
  KEY `t_alarm_info_time` (`time`)
) ENGINE=InnoDB AUTO_INCREMENT=3892 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_allow_ips(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_ip` varchar(20) NOT NULL,
  `start_ip_number` bigint(20) NOT NULL,
  `end_ip` varchar(20) NOT NULL,
  `end_ip_number` bigint(20) NOT NULL,
  `ip_rang_name` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  KEY `t_allow_ips_id` (`id`),
  KEY `allow_ip_range` (`start_ip_number`,`end_ip_number`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_api_resource(
  `id` bigint(20) NOT NULL DEFAULT '0',
  `uri` varchar(100) NOT NULL,
  `http_method` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_area(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `geo_string` varchar(255) DEFAULT NULL,
  `geometry` geometry DEFAULT NULL,
  `area_name` varchar(255) DEFAULT NULL,
  `area_no` varchar(255) DEFAULT NULL,
  `person_threshold` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_audit_log(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `object` varchar(255) DEFAULT NULL,
  `object_id` bigint(20) NOT NULL,
  `object_status` bigint(20) NOT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_audit_log_object_object_id` (`object`,`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2587 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_black_bank(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bank_description` varchar(255) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `bank_no` varchar(255) DEFAULT NULL,
  `station_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_black_detail(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `bank_id` bigint(20) NOT NULL,
  `black_description` varchar(255) DEFAULT NULL,
  `face_feature` longblob,
  `from_image_id` bigint(20) NOT NULL,
  `from_person_id` bigint(20) NOT NULL,
  `image_data` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `t_black_detail_from_person_id` (`from_person_id`),
  KEY `t_black_detail_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1075 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_business_api(
  `resource_id` bigint(20) NOT NULL DEFAULT '0',
  `api_resource_id` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_camera_blackdetail(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `blackdetail_id` bigint(20) NOT NULL,
  `camera_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `t_camera_blackdetail_blackdetail_id` (`blackdetail_id`),
  KEY `t_camera_blackdetail_camera_id` (`camera_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_camera_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `geo_string` varchar(255) DEFAULT NULL,
  `geometry` geometry DEFAULT NULL,
  `addr` varchar(255) DEFAULT NULL,
  `capability` int(11) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `county` varchar(255) DEFAULT NULL,
  `cover` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `port` int(11) NOT NULL,
  `rtspuri` varchar(255) DEFAULT NULL,
  `station_id` bigint(20) NOT NULL,
  `status` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `in_station` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `t_camera_info_id` (`id`),
  KEY `t_camera_info_station_id` (`station_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_crime_fri_type(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_crime_sec_type(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fri_id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_crime_sec_type_fri_id` (`fri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_oauth_client_details(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_token_validity` int(11) NOT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `refresh_token_validity` int(11) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_person_detail(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `arrest` int(11) NOT NULL,
  `birthday` datetime DEFAULT NULL,
  `cid` varchar(255) DEFAULT NULL,
  `crime_address` varchar(255) DEFAULT NULL,
  `crime_type` int(11) NOT NULL,
  `deleted` char(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `endtime` datetime DEFAULT NULL,
  `identity` bigint(20) NOT NULL,
  `important` int(11) NOT NULL,
  `nation` varchar(255) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `photo_data` varchar(255) DEFAULT NULL,
  `real_gender` int(11) NOT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `rule_id` bigint(20) NOT NULL,
  `starttime` datetime DEFAULT NULL,
  `status` int(11) NOT NULL,
  `history` int(11) NOT NULL,
  `in_station` int(11) NOT NULL,
  `similar_suspect` int(11) NOT NULL,
  `bank_id` bigint(20) NOT NULL DEFAULT '1',
  `owner_station` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_person_detail_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=601 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_person_red(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS intellif_base.t_police_station(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `geo_string` varchar(255) DEFAULT NULL,
  `geometry` geometry DEFAULT NULL,
  `station_name` varchar(255) DEFAULT NULL,
  `station_no` varchar(255) DEFAULT NULL,
  `person_threshold` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_police_station_authority(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bank_id` bigint(20) NOT NULL,
  `station_id` bigint(20) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=223 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_red_detail(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `face_feature` longblob,
  `from_image_id` bigint(20) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `face_url` varchar(255) DEFAULT NULL,
  `from_person_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_resource(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(255) DEFAULT NULL,
  `scopes` varchar(255) NOT NULL DEFAULT 'read,write',
  `uri` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_role(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `cn_name` varchar(255) DEFAULT NULL,
  `modules` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `res_ids` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_rule_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_description` varchar(255) DEFAULT NULL,
  `rule_name` varchar(255) DEFAULT NULL,
  `thresholds` varchar(255) DEFAULT NULL,
  `types` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_search_record(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `face_url` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2824 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_server_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `geo_string` varchar(255) DEFAULT NULL,
  `geometry` geometry DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `peak` int(11) NOT NULL,
  `port` int(11) NOT NULL,
  `server_name` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_solr_config_info(
  `id` bigint(20) NOT NULL DEFAULT '0',
  `server_url` varchar(255) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_task_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `bank_id` bigint(20) NOT NULL,
  `cron_status` int(11) NOT NULL,
  `cron_tabs` varchar(255) DEFAULT NULL,
  `decode_type` int(11) NOT NULL,
  `rule_id` bigint(20) NOT NULL,
  `server_id` bigint(20) NOT NULL,
  `source_id` bigint(20) NOT NULL,
  `source_type` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `task_name` varchar(255) DEFAULT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `t_task_info_source_id` (`source_id`),
  KEY `t_task_info_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_upload_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `geo_string` varchar(255) DEFAULT NULL,
  `geometry` geometry DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `image_id` bigint(20) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_user(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `age` int(3) NOT NULL DEFAULT '0',
  `camera_rights` varchar(255) NOT NULL DEFAULT '',
  `face_id` bigint(20) NOT NULL DEFAULT '-1',
  `gender` int(1) NOT NULL DEFAULT '0',
  `login` varchar(255) NOT NULL,
  `mobile` varchar(12) NOT NULL DEFAULT '000',
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `police_station_id` int(25) NOT NULL DEFAULT '-1',
  `post` varchar(255) NOT NULL DEFAULT '?',
  `role_ids` mediumtext NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_user_api_limit(
  `id` bigint(20) NOT NULL DEFAULT '0',
  `user_id` bigint(20) NOT NULL,
  `api_id` bigint(20) NOT NULL,
  `limit_method` varchar(30) NOT NULL DEFAULT 'TIME_INTERVAL',
  `call_count` bigint(20) NOT NULL DEFAULT '1',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `deny_count` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_user_attention(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `person_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=814 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_user_role(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;



CREATE TABLE  IF NOT EXISTS intellif_base.t_crime_alarm_info(
        id bigint NOT NULL AUTO_INCREMENT,
        crime_face_id bigint NOT NULL,
        crime_person_id bigint NOT NULL,
        confidence DOUBLE NOT NULL,
        face_id bigint NOT NULL,
        status INT NOT NULL default 0,
        camera_id bigint NOT NULL,
        time DATETIME,
        face_url VARCHAR(200),
        PRIMARY KEY (id),
        INDEX t_alarm_info_crime_person_id (crime_person_id),
        INDEX t_alarm_info_camera_id (camera_id),
        INDEX t_alarm_info_time (TIME)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
 -- ----------------------------
-- Table structure for t_audit_log_type
-- ----------------------------
CREATE TABLE  IF NOT EXISTS intellif_base.t_audit_log_type(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_id` int(2) DEFAULT NULL,
  `type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  IF NOT EXISTS intellif_base.t_face_camera_count(
        id bigint NOT NULL AUTO_INCREMENT,
        source_id bigint NOT NULL ,
        `time` DATETIME NOT NULL,
        `count` bigint NOT NULL ,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;    
    
CREATE TABLE IF NOT EXISTS intellif_face.t_tables (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `table_name` varchar(255) NOT NULL,
  `table_code` bigint(20) NOT NULL,
  `short_name` varchar(255) NOT NULL,
  `total_num` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `time_index` (`start_time`,`end_time`) USING BTREE,
  KEY `code_index` (`table_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS  intellif_base.t_engine_imgserver(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`imgserverip` VARCHAR(128) NOT NULL,
`server_id` BIGINT(20) NOT NULL,
`imgftp_path` varchar(128),
`other` varchar(1024),
 PRIMARY KEY (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS  intellif_base.t_engine_imgserver(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`imgserverip` VARCHAR(128) NOT NULL,
`server_id` BIGINT(20) NOT NULL,
`imgftp_path` varchar(128),
`other` varchar(1024),
 PRIMARY KEY (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
-- ----------------------------
-- Table structure for t_police_man_authority
-- ----------------------------
CREATE TABLE IF NOT EXISTS  intellif_base.t_police_man_authority(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `police_no` varchar(255) DEFAULT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_police_man_authority_type
-- ----------------------------
CREATE TABLE IF NOT EXISTS  intellif_base.t_police_man_authority_type(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_id` int(11) NOT NULL,
  `type_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
    
CREATE TABLE IF NOT EXISTS intellif_base.t_police_cloud_audit_log(
  `id` bigint(20) NOT NULL auto_increment,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `message` varchar(255) default NULL,
  `object` varchar(255) default NULL,
  `object_status` bigint(20) NOT NULL,
  `operation` varchar(255) default NULL,
  `police_id` bigint(20) NOT NULL,
  `title` varchar(255) default NULL,
  `hash_code` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `t_police_cloud_audit_log` USING BTREE (`hash_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
-- ----------------------------
-- Table structure for t_police_man_authority_type
-- ----------------------------
CREATE TABLE IF NOT EXISTS intellif_base.t_user_business_api(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `info` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
   
CREATE TABLE IF NOT EXISTS intellif_base.t_red_switch (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `red_switch` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_excel_record(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `excel_name` varchar(255) DEFAULT NULL,
  `record` int(11) NOT NULL,
  `u_key` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_trans_proxy_info (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `capability` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `station_id` bigint(20) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `trans_type` int(11) NOT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `ext1` varchar(255) DEFAULT NULL,
  `ext2` varchar(255) DEFAULT NULL,
  `ext3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_trans_proxy_topology (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_id` bigint(20) NOT NULL,
  `source_type` int(11) NOT NULL,
  `source_channel` int(11) NOT NULL,
  `proxy_id` bigint(20) NOT NULL,
  `ext1` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS  intellif_base.t_search_reason (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`r_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '事由名称',
	`created` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
	`updated` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '更新时间',
	PRIMARY KEY (`id`)
)
COMMENT='搜索事由'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS intellif_base.t_urgent_alarm_info (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `black_id` bigint(20) NOT NULL,
  `confidence` double NOT NULL,
  `face_id` bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `task_id` bigint(20) NOT NULL,
  `time` datetime DEFAULT NULL,
  `face_url` varchar(100) DEFAULT NULL,
  `image_url` varchar(100) DEFAULT NULL,
  `send` int(11) NOT NULL DEFAULT '0',
  `newtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `t_alarm_info_time` (`time`),
  KEY `t_alarm_info_blackid` (`black_id`),
  KEY `t_alarm_info_taskid` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13913883 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_system_switch(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `switch_type` varchar(50) NOT NULL DEFAULT 'AREA_AUTHORIZE' COMMENT '开关类型',
  `opened` bit(1) NOT NULL COMMENT '是否打开',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统开关（全局开关）';


CREATE TABLE IF NOT EXISTS intellif_base.t_user_area(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `area_id` bigint(20) NOT NULL COMMENT '区域ID',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与区域映射关系表';


CREATE TABLE IF NOT EXISTS intellif_base.t_user_camera(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `camera_id` bigint(20) NOT NULL COMMENT '摄像头ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` bigint(20) NOT NULL COMMENT '授权人用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与摄像头授权关系表';


CREATE TABLE IF NOT EXISTS intellif_base.t_user_switch(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `opened` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否打开',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户授权开关表';

CREATE TABLE IF NOT EXISTS intellif_base.t_district (
	`id`              bigint(64)   NOT NULL   AUTO_INCREMENT,
    `district_name`   varchar(255) NOT NULL        COMMENT '行政区名称',
    `district_no`     varchar(255) DEFAULT NULL    COMMENT '行政区编号',
    `created`         datetime     NOT NULL,
    `updated`         datetime     NOT NULL,
    `geo_string`      varchar(255) DEFAULT NULL    COMMENT '位置坐标',
    `geometry`        geometry     DEFAULT NULL,
    `local`           int(10)      NOT NULL DEFAULT '0' COMMENT '是否属于本机所在区域 0 否，1是',
     PRIMARY KEY (`id`)
)
COMMENT='行政区表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS intellif_base.t_other_area (
	`id`               BIGINT(64)   NOT NULL   AUTO_INCREMENT,
	`created`          DATETIME     NOT NULL,
	`updated`          DATETIME     NOT NULL,
	`geo_string`       VARCHAR(255) NULL DEFAULT NULL,
	`geometry`         GEOMETRY     NULL DEFAULT NULL,
	`area_name`        VARCHAR(255) NULL DEFAULT NULL,
	`area_no`          VARCHAR(255) NULL DEFAULT NULL,
	`person_threshold` BIGINT(20)   NOT NULL,
	`district_id`      BIGINT(64)   NOT NULL    COMMENT '行政区ID, 与t_district表ID对应',
	 PRIMARY KEY (`id`)
)
COMMENT='其他区域表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS intellif_base.t_other_camera (
	`id`              BIGINT(64)    NOT NULL       AUTO_INCREMENT,
	`created`         DATETIME      NOT NULL,
	`updated`         DATETIME      NOT NULL,
	`geo_string`      VARCHAR(255)  NULL DEFAULT NULL,
	`geometry`        GEOMETRY      NULL DEFAULT NULL,
	`addr`            VARCHAR(255)  NULL DEFAULT NULL,
	`capability`      INT(11)       NOT NULL,
	`city`            VARCHAR(255)  NULL DEFAULT NULL,
	`county`          VARCHAR(255)  NULL DEFAULT NULL,
	`cover`           VARCHAR(255)  NULL DEFAULT NULL,
	`name`            VARCHAR(255)  NULL DEFAULT NULL,
	`password`        VARCHAR(255)  NULL DEFAULT NULL,
	`port`            INT(11)       NOT NULL,
	`rtspuri`         VARCHAR(255)  NULL DEFAULT NULL,
	`station_id`      BIGINT(20)    NOT NULL,
	`status`          INT(11)       NOT NULL,
	`type`            INT(11)       NOT NULL,
	`uri`             VARCHAR(255)  NULL DEFAULT NULL,
	`username`        VARCHAR(255)  NULL DEFAULT NULL,
	`short_name`      VARCHAR(255)  NULL DEFAULT NULL,
	`in_station`      BIGINT(20)    NOT NULL,
	`liveurl`         VARCHAR(255)  NULL DEFAULT NULL,
	 PRIMARY KEY (`id`),
	 INDEX `t_other_camera_id` (`id`),
	 INDEX `t_other_camera_station_id` (`station_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS intellif_base.t_proxy_server_info(
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `district_id` bigint(20) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `ext1` varchar(255) DEFAULT NULL,
  `ext2` varchar(255) DEFAULT NULL,
  `ext3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
)   
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS intellif_base.t_face_quality_camera_count(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_id` bigint(20) NOT NULL,
  `time` datetime NOT NULL,
  `high_total` bigint(20) NOT NULL default 0,
  `low_total` bigint(20) NOT NULL default 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_search_log(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `time_delay` bigint(20) DEFAULT NULL,
  `result_code` int(2) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- search result code   0 equals success others equal diffrent faults---
-- ----------------------------
CREATE TABLE IF NOT EXISTS intellif_base.t_search_result_code(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `result_code_id` int(2) DEFAULT NULL,
  `result_code_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_user_district (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
	`district_id` BIGINT(20) NOT NULL COMMENT '局点ID',
	`created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_other_node (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `server_id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `district_id` bigint(20) DEFAULT NULL,
  `ext1` varchar(255) DEFAULT NULL,
  `ext2` varchar(255) DEFAULT NULL,
  `ext3` varchar(255) DEFAULT NULL,
  `ext4` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_face.t_share_sync_info(
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `server_id` bigint(64) NOT NULL,
  `node_id` bigint(64) NOT NULL,
  `begin_time` varchar(255) NOT NULL,
  `end_time` varchar(255) NOT NULL,
  `begin_sequence` bigint(64) NOT NULL,
  `end_sequence` bigint(64) NOT NULL,
  `status` int(11) NOT NULL,
  `ext1` varchar(255) DEFAULT NULL,
  `ext2` varchar(255) DEFAULT NULL,
  `ext3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_table_version (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`db_name` VARCHAR(50) NOT NULL COMMENT '数据库名',
	`table_name` VARCHAR(50) NOT NULL COMMENT '表名',
	`update_version` BIGINT(20) NOT NULL COMMENT '更新版本号',
	PRIMARY KEY (`id`)
) COMMENT='表版本信息' ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if not exists intellif_base.t_red_force_log(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message` varchar(255) NOT NULL DEFAULT '',
  `opera_person` varchar(255) NOT NULL DEFAULT '',
  `cmp_person` varchar(255) NOT NULL DEFAULT '',
  `reason` varchar(255) DEFAULT NULL,
  `station` varchar(255) NOT NULL DEFAULT '',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `s_id` bigint(20) NOT NULL DEFAULT 0,
  `r_id` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE if not exists intellif_base.t_red_check_result(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message` varchar(255) NOT NULL DEFAULT '',
  `apply_person` varchar(255) NOT NULL DEFAULT '',
  `check_person` varchar(255) DEFAULT NULL,
  `cmp_person` varchar(255) NOT NULL DEFAULT '',
  `result` varchar(255) NOT NULL DEFAULT '',
  `station` varchar(255) NOT NULL DEFAULT '',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `s_id` bigint(20) NOT NULL DEFAULT 0,
  `r_id` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_role_resource (
    id              bigint(64)   NOT NULL        AUTO_INCREMENT,
    role_name       varchar(255) NOT NULL        COMMENT '角色名称',
    resource_id     bigint(20)   DEFAULT NULL    COMMENT '行政区编号',
    must            boolean      NOT NULL        COMMENT '是否必选',
    display         boolean      NOT NULL        COMMENT '是否显示给前端',
    PRIMARY KEY (id)
)
COMMENT='角色对应的功能权限表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS intellif_base.t_chd_configure_time(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starttime` varchar(20) NOT NULL,
  `endtime` varchar(20) NOT NULL,
  `period` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_zip_path(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `starttime` datetime DEFAULT NULL,
  `endtime` datetime DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4156 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_contrast_face_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cj_name` varchar(255) DEFAULT NULL,
  `bz_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `identity` varchar(255) DEFAULT NULL,
  `score` float NOT NULL,
  `face_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_contrast_face_info(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cj_image` varchar(255) DEFAULT NULL,
  `bz_image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `identity` varchar(255) DEFAULT NULL,
  `score` float NOT NULL,
  `face_time` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS intellif_base.t_last_capture_time (
	id              BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	camera_id       BIGINT(20) NOT NULL COMMENT '摄像头id',
	last_time       DATETIME   NOT NULL COMMENT '最后采集数据时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS intellif_base.t_alg_param(
        id bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
        version VARCHAR(10) NOT NULL COMMENT '算法版本',
        base_points VARCHAR(255) NOT NULL COMMENT '标准点集',
        new_points VARCHAR(255) NOT NULL COMMENT '新点集',
        PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Solr算法参数配置表';


CREATE TABLE  if not exists intellif_base.t_fk_person_attr(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `from_person_id` bigint(20) NOT NULL,
  `fk_status` varchar(255) DEFAULT NULL,
  `distric_id` bigint (20) DEFAULT NULL,
  `area_id` bigint (20) DEFAULT NULL,
  `used_name` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `register_address` varchar(255) DEFAULT NULL,
  `register_address_division` varchar(255) DEFAULT NULL,
  `address_division` varchar(255) DEFAULT NULL,
  `register_police_station` varchar(255) DEFAULT NULL,
  `profession` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `ic_card` varchar(255) DEFAULT NULL,
  `mac_address` varchar(255) DEFAULT NULL,
  `update_identification` varchar(255) DEFAULT NULL,
  `photo_type` int(11) NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE  if not exists intellif_base.t_fk_bank_dictionary(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `short_name` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `bankno` bigint(11) NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE if not exists intellif_face.t_face_delete(
  `id` bigint(64) DEFAULT NULL,
  `accessories` int(11) NOT NULL COMMENT '穿戴',
  `race` int(11) NOT NULL COMMENT '种族',
  `age` int(11) NOT NULL COMMENT '年龄',
  `face_feature` longblob,
  `from_image_id` bigint(64) DEFAULT NULL,
  `from_person_id` bigint(20) DEFAULT NULL,
  `from_video_id` bigint(20) DEFAULT NULL,
  `gender` int(11) NOT NULL COMMENT '性别',
  `image_data` varchar(255) DEFAULT NULL,
  `indexed` int(11) NOT NULL,
  `source_id` bigint(20) NOT NULL,
  `source_type` int(11) NOT NULL,
  `time` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `json` varchar(255) DEFAULT NULL,
  `sequence` bigint(64) NOT NULL AUTO_INCREMENT,
  `quality` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sequence`),
  KEY `t_face_id` (`id`),
  KEY `t_face_source_id_time` (`source_id`,`time`),
  KEY `t_face_time` (`time`),
  KEY `t_face_race` (`race`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS  intellif_base.t_black_feature(
id bigint(20) primary key auto_increment, 
created datetime DEFAULT CURRENT_TIMESTAMP, 
updated datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
face_feature longblob DEFAULT NULL,
from_black_id bigint(20) DEFAULT 0,
version  int(11) DEFAULT 0
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS   intellif_base.t_red_feature(
id bigint(20) primary key auto_increment NOT NULL, 
created datetime DEFAULT CURRENT_TIMESTAMP, 
updated datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
face_feature longblob DEFAULT NULL,
from_red_id bigint(20) DEFAULT 0,
version  int(11)  DEFAULT 0
)ENGINE=InnoDB DEFAULT CHARSET=utf8;   

create table if not exists intellif_base.t_mobile_collect_sync_log (
    id                bigint(64)        not null    auto_increment,
    file_date         date              not null,
    file_name         varchar(255)      not null,
    sync_status       tinyint           not null,
	created           datetime,
	updated           datetime,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists intellif_base.t_mobile_collect_station_map (
    id                bigint(64)        not null    auto_increment,
    sync_station_id   bigint(64)        not null,
    mapped_station_id bigint(64)        not null,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists intellif_face.t_face_filtered (
    id                bigint(64),
    accessories       int(11)            NOT NULL COMMENT '穿戴',
    race              int(11)            NOT NULL COMMENT '种族',
    age               int(11)            NOT NULL COMMENT '年龄',
    face_feature      longblob           DEFAULT NULL,
    from_image_id     bigint(64)         DEFAULT NULL,
    from_person_id    bigint(20)         DEFAULT NULL,
    from_video_id     bigint(20)         DEFAULT NULL,
    gender            int(11)            NOT NULL COMMENT '性别',
    image_data        varchar(255)       DEFAULT NULL,
    indexed           int(11)            NOT NULL,
    source_id         bigint(20)         NOT NULL,
    source_type       int(11)            NOT NULL,
    time              datetime           DEFAULT NULL,
    version           int(11)            NOT NULL,
    json              varchar(255)       DEFAULT NULL,
    sequence          bigint(64)         NOT NULL AUTO_INCREMENT,
    quality           int(11)            NOT NULL DEFAULT '0',
	filter_type       bigint(64)         NOT NULL COMMENT '过滤类型',
    PRIMARY KEY (sequence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists intellif_face.t_face_filter_type (
    id                bigint(64)         NOT NULL AUTO_INCREMENT,
	name              varchar(255)       NOT NULL COMMENT '过滤类型名字',
	created           datetime           NOT NULL COMMENT '创建时间',
	updated           datetime           NOT NULL COMMENT '最后更新时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  if not exists intellif_base.t_fk_place(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `geo_string` varchar(255) DEFAULT NULL,
  `geometry` geometry DEFAULT NULL,
  `place_name` varchar(255) DEFAULT NULL,
  `place_no` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE  if not exists intellif_base.t_fk_place_camera(
  `place_id` bigint(20) NOT  NULL,
  `camera_ids` varchar(5000) NOT  NULL,
   PRIMARY KEY (`place_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  if not exists intellif_base.t_fk_alarm_push_log(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `notes` varchar(1024) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE  if not exists intellif_base.t_fk_institution_code (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `JGDM` varchar(255) DEFAULT NULL,
  `JGMC` varchar(255) DEFAULT NULL,
  `LSJG` varchar(255) DEFAULT NULL,
  `JB` varchar(255) DEFAULT NULL,
  `SFYXJDW` varchar(255) DEFAULT NULL,
  `IS_TEMP` varchar(255) DEFAULT NULL,
  `SFKY` varchar(255) DEFAULT NULL,
  `JGJC` varchar(255) DEFAULT NULL,
  `JGPY` varchar(255) DEFAULT NULL,
  `JGPX` varchar(255) DEFAULT NULL,
  `BRANCHKEY` varchar(255) DEFAULT NULL,
  `STATIONKEY` varchar(255) DEFAULT NULL,
  `MS` varchar(255) DEFAULT NULL,
  `CJR` varchar(255) DEFAULT NULL,
  `CJRDW` varchar(255) DEFAULT NULL,
  `CJSJ` varchar(255) DEFAULT NULL,
  `LY` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


create table if not exists intellif_base.t_venue (
   id         bigint(64)      NOT NULL       AUTO_INCREMENT,
   created    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
   updated    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后更新时间',
   name       varchar(255)    NOT NULL       COMMENT '场馆名',
   PRIMARY KEY (`id`)
) COMMENT='场馆表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_base.t_zone (
   id         bigint(64)      NOT NULL       AUTO_INCREMENT,
   created    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
   updated    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后更新时间',
   name       varchar(255)    NOT NULL       COMMENT '分区表',
   venue_id   bigint(64)      NOT NULL       COMMENT '场馆id',
   PRIMARY KEY (`id`)
) COMMENT='摄像头组'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_base.t_zone_camera (
   id         bigint(64)      NOT NULL       AUTO_INCREMENT,
   created    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
   updated    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后更新时间',
   zone_id    bigint(64)      NOT NULL       COMMENT '区域id',
   camera_id  bigint(64)      NOT NULL       COMMENT '摄像头id',
   PRIMARY KEY (`id`)
) COMMENT='分区与摄像头映射表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_face.t_face_stream (
   id         bigint(64)      NOT NULL       AUTO_INCREMENT,
   created    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
   updated    datetime        NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后更新时间',
   face_id    bigint(64)      NOT NULL       COMMENT '区域id',
   src_id     bigint(64)      NOT NULL       COMMENT '摄像头id',
   venue_id   bigint(64)      NOT NULL       COMMENT '场馆id',
   feature    blob            NOT NULL       COMMENT '特征值',
   version    int(11)         NOT NULL       COMMENT '版本',
   time       datetime        NOT NULL       COMMENT '抓拍时间',
   PRIMARY KEY (`id`)
) COMMENT='人员流动记录表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_base.t_qiangdan_record(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `red_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '红名单ID',
  `face_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '抓拍图片ID',
  `send` int(11) NOT NULL DEFAULT '0',
  `time` datetime DEFAULT NULL,
  `source_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if not exists intellif_base.t_area_blackdetail(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `blackdetail_id` bigint(20) NOT NULL,
  `area_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `areaId` (`area_id`) USING BTREE,
  KEY `blackId` (`blackdetail_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
REPLACE INTO intellif_base.t_api_resource (id, uri, http_method) values (1440029001, '/intellif/alarm/station/query', 'POST');
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800, 1440029001);


create table if not exists intellif_base.t_uploaded_status (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`created`  datetime NULL COMMENT '创建时间' ,
`updated`  datetime NULL COMMENT '最后更新时间' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户id' ,
`resumable_chunk_size`  bigint(20) NOT NULL COMMENT '块的平均大小' ,
`resumable_total_size`  bigint(20) NOT NULL COMMENT '文件总大小' ,
`resumable_identifier`  varchar(255) NOT NULL COMMENT '文件标识' ,
`resumable_file_name`  varchar(255) NOT NULL COMMENT '文件名' ,
`resumable_relative_path`  varchar(255) NOT NULL COMMENT '文件相对路径' ,
`resumable_file_path`  varchar(255) NOT NULL COMMENT '文件在文件系统中的存储路径' ,
`uploaded_chunks`  longtext  NULL COMMENT '已上传的块',
`file_type`  tinyint NOT NULL COMMENT '文件类型(0:视频，1：压缩包,2图片集）' ,
`file_id`  bigint(20) NULL COMMENT '已上传文件id',
`is_finished`  tinyint NOT NULL DEFAULT 0 COMMENT '0：上传中 1：已上传完成',
`progress`  tinyint NULL COMMENT '任务进度(0-100)',
`upload_identifier`  varchar(255) NULL COMMENT '上传标识',
PRIMARY KEY (`id`)
)COMMENT='文件上传状态表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_base.t_uploaded_file (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`created`  datetime NULL COMMENT '创建时间' ,
`updated`  datetime NULL COMMENT '最后更新时间' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户id' ,
`file_name`  varchar(255) NOT NULL COMMENT '上传的原文件名' ,
`file_url`  varchar(255) NOT NULL COMMENT '文件路径' ,
`file_type`  tinyint NOT NULL COMMENT '文件类型(0:视频，1：压缩包，2图片集）' ,
`pics_count`  int NULL COMMENT '图片集的张数',
`file_size`  bigint(20) NULL COMMENT '文件大小',
`progress`  tinyint NULL COMMENT '任务进度(0-100)',
`status`  tinyint NULL COMMENT '0:写入阶段 1:重命名阶段 2:图片打包阶段 3:打包成功原图删除阶段4上传完成5任务创建完成 ',
PRIMARY KEY (`id`)
)COMMENT='已上传文件表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_base.t_face_extract_task (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`created`  datetime NULL COMMENT '创建时间' ,
`updated`  datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间' ,
`server_id`  bigint(20) NULL COMMENT '引擎id' ,
`file_id`  bigint(20) NOT NULL COMMENT '数据源id，对应t_uploaded_file表id' ,
`file_type`  tinyint NOT NULL COMMENT '文件类型(0:视频，1：压缩包）' ,
`status`  int(11)  NULL COMMENT '任务状态（-1：处理任务失败 0：任务尚未处理   1：任务正在被处理   2：任务处理完成）' ,
`task_name`  varchar(255) NOT NULL COMMENT '任务名',
`archive_url`  varchar(255) NULL COMMENT '文档路径',
`total`  bigint(20) NULL COMMENT '文件总大小/图片总张数',
`current`  bigint(20) NULL COMMENT '已处理的文件大小/图片张数',
`error_reason`  text NULL COMMENT '错误原因',
`deleted`  boolean NOT NULL DEFAULT false COMMENT '是否已删除',
PRIMARY KEY (`id`)
)COMMENT='提取人脸任务表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

create table if not exists intellif_base.t_face_collision_task (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`created`  datetime NULL COMMENT '创建时间' ,
`updated`  datetime NULL COMMENT '最后更新时间' ,
`task_name`  varchar(255) NOT NULL COMMENT '任务名',
`complete_time`  datetime NULL COMMENT '任务完成时间' ,
`deleted`  boolean NOT NULL default false COMMENT '是否已删除',
`user_id`  bigint(20) NULL COMMENT '用户id',
`task_param`  varchar(2000) NULL COMMENT '任务参数(json)',
`task_result`  varchar(2000) NULL COMMENT '任务结果(json)',
`progress`  decimal(5,2) NULL COMMENT '碰撞进度',
`status`    int(11) NOT NULL DEFAULT 0 COMMENT '任务状态(0:人脸解析，1：人脸碰撞, 2: 已完成)',
PRIMARY KEY (`id`)
)COMMENT='数据碰撞任务表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE if not exists intellif_base.t_qiangdan_camera_police(
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	camera_id BIGINT(20) NOT NULL,
	police_no varchar(255) NOT NULL,
	create_time DATETIME NOT NULL,
	endtime DATETIME NOT NULL,
	PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 








    