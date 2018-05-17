delete from intellif_base.t_business_api;
delete from intellif_base.t_api_resource;




REPLACE INTO intellif_base.t_api_resource VALUES ('1000029001', '/intellif/server', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029002', '/intellif/server', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029003', '/intellif/server/time', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029004', '/intellif/server/{id}', 'DELETE');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029005', '/intellif/server/{id}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029006', '/intellif/server/{id}', 'PUT');



REPLACE INTO intellif_base.t_api_resource VALUES ('1000029007', '/intellif/user/{id}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029008', '/intellif/user', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029009', '/intellif/user/{id}', 'PUT');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029010', '/intellif/user/{id}', 'DELETE');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029011', '/intellif/user/query', 'POST');

REPLACE INTO intellif_base.t_api_resource VALUES ('1000029012', '/intellif/area/{id}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029013', '/intellif/area', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029014', '/intellif/area/{id}', 'PUT');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029015', '/intellif/area/{id}', 'DELETE');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029016', '/intellif/area/query', 'POST');

REPLACE INTO intellif_base.t_api_resource VALUES ('1000029017', '/intellif/camera/{id}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029018', '/intellif/camera', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029019', '/intellif/camera/{id}', 'PUT');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029020', '/intellif/camera/{id}', 'DELETE');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029021', '/intellif/camera/query', 'POST');

REPLACE INTO intellif_base.t_api_resource VALUES ('1000029022', '/intellif/image/upload/{face}', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029023', '/intellif/person/detail', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029024', '/intellif/person/detail/{id}', 'PUT');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029025', '/intellif/person/detail/{id}', 'DELETE');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029026', '/intellif/person/detail/{id}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029027', '/intellif/person/detail/query', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029028', '/intellif/alarm/station/query', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029029', '/intellif/alarm/{id}', 'PUT');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029030', '/intellif/server/logoff', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029031', '/intellif/user/right/{name}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029032', '/intellif/alarm/person/query', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029033', '/intellif/alarm/{id}', 'DELETE');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029034', '/intellif/black/detail/person/{id}', 'GET');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029035', '/intellif/alarm/process', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029036', '/intellif/alarm/process/query', 'POST');
REPLACE INTO intellif_base.t_api_resource VALUES ('1000029037', '/intellif/alarm/process/{id}', 'DELETE');

REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029001);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029002);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029003);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029004);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029005);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029006);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029007);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029008);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029009);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029010);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029011);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029012);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029013);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029014);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029015);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029016);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029017);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029018);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029019);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029020);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029021);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029022);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029023);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029024);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029026);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029027);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029028);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029029);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029030);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029031);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029032);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029033);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029025);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029034);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029035);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029036);
REPLACE INTO intellif_base.t_business_api (resource_id, api_resource_id) values (800,1000029037);