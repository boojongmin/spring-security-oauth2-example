## Authorization Server 

### OAUTH2.0 설명
OAUTH 2.0은 [rfc6749](https://tools.ietf.org/html/rfc6749)을 통해 표준화된 것으로, authorization(권한)을 위해 만들어진 규칙이다.

-  문서에서 주요 개념
    - client(웹브라우저, 앱)
    - resource owner(사용자: 사람이 될 수도 있고 봇이 될 수 있다.)
    - authorization server: 권한 서버(토큰 관리(생성, 삭제, 조회) 및 검증)
    - resource server: client가 사용하고자하는 자원(ex: 특정URL)을 소유하고 있는 서버로 클라이언트가 토큰과 함께 자원을 요청하면 (보통)authorization server에 권한 체크를 요청하고 응답 결과에 따라 client에 resource를 제공 유무 판단.
    
- Flow
```
  +--------+                                           +---------------+
  |        |--(A)------- Authorization Grant --------->|               |
  |        |                                           |               |
  |        |<-(B)----------- Access Token -------------|               |
  |        |               & Refresh Token             |               |
  |        |                                           |               |
  |        |                            +----------+   |               |
  |        |--(C)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(D)- Protected Resource --| Resource |   | Authorization |
  | Client |                            |  Server  |   |     Server    |
  |        |--(E)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(F)- Invalid Token Error -|          |   |               |
  |        |                            +----------+   |               |
  |        |                                           |               |
  |        |--(G)----------- Refresh Token ----------->|               |
  |        |                                           |               |
  |        |<-(H)----------- Access Token -------------|               |
  +--------+           & Optional Refresh Token        +---------------+

```    

- oauth2.0은 권한 체크를 위해 토큰을 사용하는데 이 토큰을 얻기위한 Grant Type은 아래와 같다.
    - **password** grant type
        - request: grant_type=password, client_id, client_password, user_id, user_password[, scope]
        - response: access_token, refresh_token, token_type[, expires_in]
        - 일반적으로 app에서 토큰을 받기위해 사용.
        - 참고: client_id/password는 resource 서버에서 보관. client는 모른다.
        ```
             +----------+
             | Resource |
             |  Owner   |
             |          |
             +----------+
                  v
                  |    Resource Owner
                 (A) Password Credentials
                  |
                  v
             +---------+                                  +---------------+
             |         |>--(B)---- Resource Owner ------->|               |
             |         |         Password Credentials     | Authorization |
             | Client  |                                  |     Server    |
             |         |<--(C)---- Access Token ---------<|               |
             |         |    (w/ Optional Refresh Token)   |               |
             +---------+                                  +---------------+
        ```
    - **client credentials** grant type
        - request: grant_type=client, client_id, client_password
        - response: access_token, token_type[, expires_in]
        - 사용자별 접근제어는 필요하지 않지만 client(app/browser) 접근 제어하기 위해 사용.
        - 참고: user_id를 전달하지 않으므로 user 정보 조회를 할 수 없다.
        ```
             +---------+                                  +---------------+
             |         |                                  |               |
             |         |>--(A)- Client Authentication --->| Authorization |
             | Client  |                                  |     Server    |
             |         |<--(B)---- Access Token ---------<|               |
             |         |                                  |               |
             +---------+                                  +---------------+
        ``` 
        
    - **authorization_code** grant type
        - request: client_id, user_id, user_password, response_type=code[, redirect_uri][, scope][, state]
        - response
            1. code
            2. access_token, token_type[, expires_in]
        - 주로 브라우저에서 인증 대행(로그인을 authorization server에서 진행)을하기 위해 사용. 결과적으로 resource server에 session 정보가 생성되기 때문에 헤더에 token을 추가할 필요 없이 cookie값으로 권한 체크가 가능
        ```
             +----------+
             | Resource |
             |   Owner  |
             |          |
             +----------+
                  ^
                  |
                 (B)
             +----|-----+          Client Identifier      +---------------+
             |         -+----(A)-- & Redirection URI ---->|               |
             |  User-   |                                 | Authorization |
             |  Agent  -+----(B)-- User authenticates --->|     Server    |
             |          |                                 |               |
             |         -+----(C)-- Authorization Code ---<|               |
             +-|----|---+                                 +---------------+
               |    |                                         ^      v
              (A)  (C)                                        |      |
               |    |                                         |      |
               ^    v                                         |      |
             +---------+                                      |      |
             |         |>---(D)-- Authorization Code ---------'      |
             |  Client |          & Redirection URI                  |
             |         |                                             |
             |         |<---(E)----- Access Token -------------------'
             +---------+       (w/ Optional Refresh Token)

        ```
    - **implicit** grant type
        - request: response_type=token, client_id[, redirect_uri][, scope][, state]
        - response: access_token, token_type[, expires_in][, scope][, state]
        ```
             +----------+
             | Resource |
             |  Owner   |
             |          |
             +----------+
                  ^
                  |
                 (B)
             +----|-----+          Client Identifier     +---------------+
             |         -+----(A)-- & Redirection URI --->|               |
             |  User-   |                                | Authorization |
             |  Agent  -|----(B)-- User authenticates -->|     Server    |
             |          |                                |               |
             |          |<---(C)--- Redirection URI ----<|               |
             |          |          with Access Token     +---------------+
             |          |            in Fragment
             |          |                                +---------------+
             |          |----(D)--- Redirection URI ---->|   Web-Hosted  |
             |          |          without Fragment      |     Client    |
             |          |                                |    Resource   |
             |     (F)  |<---(E)------- Script ---------<|               |
             |          |                                +---------------+
             +-|--------+
               |    |
              (A)  (G) Access Token
               |    |
               ^    v
             +---------+
             |         |
             |  Client |
             |         |
             +---------+
        ```
    - **refresh** grant type(RFC에는 없지만 spring security oauth에는 있음.)
        - request: fresh token
        - response: access_token, refresh_token, token_type[, expires_in]        
        - refresh 토큰은 갱신 여부는 구현부에 따라 다름(spring security oauth는 갱신하지 않는다.)
                
 #### 스프링 시큐리디 설정
            
        
    


