-- scope는 페이스북처럼 사용자의 resource별로 scope를 추가하는 방법도 있다. 여기서는 read,write형태로만 간단히 기술
-- 참조 https://brunch.co.kr/@sbcoba/15
insert into oauth_client_details (client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
values ('client1', '{noop}client1', 'client-1', 'read,write', 'authorization_code,password,client_credentials,implicit,refresh_token', null, 'USER', 86400, 2678400, null, null);
insert into oauth_client_details (client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
-- values ('client2', '{noop}client2', 'client-2', 'read,write', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:8081/oauth/code', 'USER,ADMIN', 86400, 2678400, null, true);
values ('client2', '{noop}client2', null, 'read,write', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:8081/login', 'USER,ADMIN', 86400, 2678400, null, 'true');
-- values ('client2', '{noop}client2', 'client-2', 'read,write', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:8082/ui/login,http://localhost:8083/ui2/login,http://localhost:8082/login', 'USER,ADMIN', 86400, 2678400, null, 'true');
