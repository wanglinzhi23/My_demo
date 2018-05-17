-- @see https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql
-- used in tests that use HSQL
DROP TABLE IF EXISTS oauth_client_details;
create table oauth_client_details (
  client_id VARCHAR(255) PRIMARY KEY,
  resource_ids VARCHAR(255),
  client_secret VARCHAR(255),
  scope VARCHAR(255),
  authorized_grant_types VARCHAR(255),
  web_server_redirect_uri VARCHAR(255),
  authorities VARCHAR(255),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(255)
);
DROP TABLE IF EXISTS oauth_client_token;
create table oauth_client_token (
  token_id VARCHAR(255),
  token LONG VARBINARY,
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name VARCHAR(255),
  client_id VARCHAR(255)
);
DROP TABLE IF EXISTS oauth_access_token;
create table oauth_access_token (
  token_id VARCHAR(255),
  token LONG VARBINARY,
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication LONG VARBINARY,
  refresh_token VARCHAR(255)
);
DROP TABLE IF EXISTS oauth_refresh_token;
create table oauth_refresh_token (
  token_id VARCHAR(255),
  token LONG VARBINARY,
  authentication LONG VARBINARY
);
DROP TABLE IF EXISTS oauth_code;
create table oauth_code (
  code VARCHAR(255), authentication LONG VARBINARY
);
DROP TABLE IF EXISTS oauth_approvals;
create table oauth_approvals (
	userId VARCHAR(255),
	clientId VARCHAR(255),
	scope VARCHAR(255),
	status VARCHAR(10),
	expiresAt TIMESTAMP,
	lastModifiedAt TIMESTAMP
);


-- -- customized oauth_ClientDetails table
-- DROP TABLE IF EXISTS oauth_ClientDetails;
-- create table oauth_ClientDetails (
--   appId VARCHAR(255) PRIMARY KEY,
--   resourceIds VARCHAR(255),
--   appSecret VARCHAR(255),
--   scope VARCHAR(255),
--   grantTypes VARCHAR(255),
--   redirectUrl VARCHAR(255),
--   authorities VARCHAR(255),
--   access_token_validity INTEGER,
--   refresh_token_validity INTEGER,
--   additionalInformation VARCHAR(4096),
--   autoApproveScopes VARCHAR(255)
-- );