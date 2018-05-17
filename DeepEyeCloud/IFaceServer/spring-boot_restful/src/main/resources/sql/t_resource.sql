--t_resource
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (1, "alarm","报警","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (2, "black/bank","黑名单库","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (3, "black/detail","黑名单详情","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (4, "camera","摄像头","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (5, "face","人脸","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (6, "greeting","测试","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (7, "upload","上传","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (8, "image","图像","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (9, "person/deteail","人员详情","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (10, "person/info","人员信息","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (11, "police/station","警察厅","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (12, "role","角色","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (13, "rule","规则","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (14, "search","搜索","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (15, "server","服务器","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (16, "suspect","嫌疑人","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (17, "task","任务","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (18, "user","用户","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (19, "alarm/statistic","报警统计","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (20, "face/search","人脸检索","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (21, "upload/image","图片上传","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (22, "upload/zip","重点人员批量导入","read,write");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (23, "upload/excel","任务批量导入","read,write");
-- 20160222
-- 	犯罪类型编辑
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (101, "crime/fritype","读_犯罪类型-","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (102, "crime/fritype","创建_犯罪类型-","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (103, "crime/fritype","更新_犯罪类型-","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (104, "crime/fritype","删除_犯罪类型-","D");

INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (105, "crime/sectype","读_犯罪类型二","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (106, "crime/sectype","创建_犯罪类型二","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (107, "crime/sectype","更新_犯罪类型二","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (108, "crime/sectype","删除_犯罪类型二","D");
-- 	新增入库
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (109, "person/detail","读_入库信息","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (110, "person/detail","创建_入库信息","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (111, "person/detail","更新_入库信息","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (112, "person/detail","删除_入库信息","D");
-- 布控嫌疑人编辑（取消布控）
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (113, "person/detail/{id}","读_标记重点/抓捕","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (114, "person/detail/{id}","创建_标记重点/抓捕","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (115, "person/detail/{id}","更新_标记重点/抓捕","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (116, "person/detail/{id}","删除_标记重点/抓捕","D");
-- 相关布控嫌疑人编辑（取消布控）
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (117, "camera/person/{id}/add/{stationId}","读_标记重点/抓捕_警局_增","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (118, "camera/person/{id}/add/{stationId}","读写_标记重点/抓捕_警局_增","C,R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (119, "camera/person/{id}/del/{stationId}","读_标记重点/抓捕_警局_删","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (120, "camera/person/{id}/del/{stationId}","读写_标记重点/抓捕_警局_删","C,D");

INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (121, "black/detail","读_标记重点/抓捕_黑名单","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (122, "black/detail","创建_标记重点/抓捕_黑名单","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (123, "black/detail","更新_标记重点/抓捕_黑名单","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (124, "black/detail","删除_标记重点/抓捕_黑名单","D");
-- 删除布控告警记录
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (125, "alarm/{id}","读_数据检索","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (126, "alarm/{id}","创建_数据检索","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (127, "alarm/{id}","更新_数据检索","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (128, "alarm/{id}","删除_数据检索","D");
-- 摄像头管理
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (129, "camera","读_摄像头","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (130, "camera","创建_摄像头","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (131, "camera","更新_摄像头","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (132, "camera","删除_摄像头","D");
-- 用户管理
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (133, "user","读_用户","R");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (134, "user","创建_用户","C");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (135, "user","更改_用户","U");
INSERT INTO t_resource (id, uri,cn_name,scopes) VALUES (136, "user","删除_用户","D");
-- 用户密码管理