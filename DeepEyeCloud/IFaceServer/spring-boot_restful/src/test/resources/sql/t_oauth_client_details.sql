#t_oauth_client_details
TRUNCATE TABLE oauth_client_details;
TRUNCATE TABLE t_oauth_client_details;
INSERT INTO t_oauth_client_details(id,client_id,resource_ids,client_secret,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,autoapprove) VALUES  (1,"clientapp","1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18","123456","read,write","password,refresh_token","","USER",-1,-1,"{}","");