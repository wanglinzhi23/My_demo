CALL intellif_base.addFieldIfNotExists('intellif_base','t_area', 'user_id', 'bigint(20)','not null default 0');
CALL intellif_base.addFieldIfNotExists('intellif_base','t_other_area', 'user_id', 'bigint(20)','not null default 0');
call intellif_base.updateFieldAttribute('intellif_base','t_user','mobile','varchar(25) default null');
CREATE TABLE IF NOT EXISTS intellif_base.t_alarm_process(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarm_id` bigint(64) NOT NULL,
  `user_id` bigint(20) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `created` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `updated` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `alarmIndex` (`alarm_id`) USING BTREE,
  KEY `typeIndex` (`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

 