## Authorization Server 

### OAUTH2.0 설명
OAUTH 2.0은 [rfc6749](https://tools.ietf.org/html/rfc6749)을 통해 표준화된 것으로, authorization(권한)을 위해 만들어진 규칙이다.

-  문서에서 주요 개념
    - client(앱, 웹브라우저 등)
    - resource owner(사용자: 사람이 될 수도 있고 봇이 될 수 있다.)
    - authorization server: 권한 서버(토큰 관리(생성, 삭제, 조회) 및 검증)
    - resource server: client가 사용하고자하는 자원(ex: 특정URL)을 소유하고 있는 서버로 클라이언트가 토큰과 함께 자원을 요청하면 (보통)authorization server에 권한 체크를 요청하고 응답 결과에 따라 client에 resource를 제공 유무 판단.
    
- OAUTH 2.0 흐름
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
        > 
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
        
    - **client credentials** grant type
        - request: grant_type=client, client_id, client_password
        - response: access_token, token_type[, expires_in]
        - 사용자별 접근제어는 필요하지 않지만 client(app/browser) 접근 제어하기 위해 사용.
        - 참고: user_id를 전달하지 않으므로 user 정보 조회를 할 수 없다.
        > 
             +---------+                                  +---------------+
             |         |                                  |               |
             |         |>--(A)- Client Authentication --->| Authorization |
             | Client  |                                  |     Server    |
             |         |<--(B)---- Access Token ---------<|               |
             |         |                                  |               |
             +---------+                                  +---------------+
        
    - **authorization_code** grant type
        - request: client_id, user_id, user_password, response_type=code[, redirect_uri][, scope][, state]
        - response
            1. code
            2. access_token, token_type[, expires_in]
        - 주로 브라우저에서 인증 대행(로그인을 authorization server에서 진행)을하기 위해 사용. 결과적으로 resource server에 session 정보가 생성되기 때문에 헤더에 token을 추가할 필요 없이 cookie값으로 권한 체크가 가능
        >
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
        
    - **implicit** grant type
        - request: response_type=token, client_id[, redirect_uri][, scope][, state]
        - response: access_token, token_type[, expires_in][, scope][, state]
        >
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
        
    - **refresh** grant type(RFC에는 없지만 spring security oauth에는 있음.)
        - request: fresh token
        - response: access_token, refresh_token, token_type[, expires_in]        
        - [참고] refresh 토큰은 갱신 여부는 구현부에 따라 다름(spring security oauth는 갱신하지 않는다.)
                
### 스프링 시큐리디 설정
#### 의존성
[build.gradle](../authorization/build.gradle) - 가능하면 실제 프로젝트에서 쓸 수 있는 최소한의 구성을 취함. 
- security
    - org.springframework.boot:spring-boot-starter-security
    - org.springframework.security.oauth:spring-security-oauth2:2.3.3.RELEASE
    - org.springframework.boot:spring-boot-starter-web
- persistence
    - org.springframework.boot:spring-boot-starter-data-jpa
    - com.h2database:h2
- cache
    - org.springframework.session:spring-session-core:2.0.4.RELEASE
    - org.springframework.boot:spring-boot-starter-data-redis
    - org.springframework.session:spring-session-data-redis
    - com.github.kstyrc:embedded-redis:0.6
    
#### 참고: 의존성 상세(편집)
```
+--- org.springframework.boot:spring-boot-starter-security -> 2.0.4.RELEASE
|    +--- org.springframework.boot:spring-boot-starter:2.0.4.RELEASE
+--- org.springframework.boot:spring-boot-starter-web -> 2.0.4.RELEASE
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 2.0.4.RELEASE
|    +--- org.springframework.boot:spring-boot-starter-jdbc:2.0.4.RELEASE
|    |    +--- com.zaxxer:HikariCP:2.7.9
|    |    \--- org.springframework:spring-jdbc:5.0.8.RELEASE
|    +--- org.hibernate:hibernate-core:5.2.17.Final
|    |    +--- org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.0.Final -> 1.0.2.Final
|    +--- javax.transaction:javax.transaction-api:1.2
|    +--- org.springframework.data:spring-data-jpa:2.0.9.RELEASE
+--- org.springframework.session:spring-session-core:2.0.4.RELEASE
+--- org.springframework.security.oauth:spring-security-oauth2:2.3.3.RELEASE
+--- org.springframework.boot:spring-boot-starter-data-redis -> 2.0.4.RELEASE
|    +--- org.springframework.data:spring-data-redis:2.0.9.RELEASE
|    \--- io.lettuce:lettuce-core:5.0.4.RELEASE
|         +--- io.projectreactor:reactor-core:3.1.6.RELEASE -> 3.1.8.RELEASE
|         +--- io.netty:netty-common:4.1.24.Final -> 4.1.27.Final
+--- org.springframework.session:spring-session-data-redis -> 2.0.5.RELEASE
|    +--- org.springframework.data:spring-data-redis:2.0.9.RELEASE (*)
|    \--- org.springframework.session:spring-session-core:2.0.5.RELEASE -> 2.0.4.RELEASE (*)
\--- com.github.kstyrc:embedded-redis:0.6
```   

#### Authorization Server 설정
1. AuthorizationServerConfigurerAdapter 상속.

    [AuthorizationServerConfig.java](../authorization/src/main/java/example/oauth2/authentication/config/AuthorizationServerConfig.java)
    ```java
    public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
    ```
1. class 레벨로 @EnableAuthorizationServer 추가
1. 의존성 주입
  - [interface) org.springframework.security.oauth2.provider.token.TokenStore
    - (class) org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore을 사용
    - 토큰 조회, 저장, 삭제
  - (interface) org.springframework.security.oauth2.provider.ClientDetailsService
    - (class) org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
    - Oauth2의 client 인증용 서비스(주의 UserDetailsService와 혼돈되기 쉬움)
    - 보통 oauth2의 client는 app이나 웹브라우저라고 생각.
  - org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
    - class: org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices
    - authorization_code 방식에서 code를 저장하고 검증하는 역할을함. JdbcAuthorizationCodeServices로 설정을하지 않으면 기본은 ImMemory로 동작. jvm 인스턴스 한대일 때는 문제가 없지만 다수가 되면 resource server가 올바른 코드를 던져도 invalid code 에러가 발생함.
  - org.springframework.security.authentication.AuthenticationManager 인터페이스
    - spring security의 가장 기본이 되는 인터페이스로 [WebSecurityConfig](../authorization/src/main/java/example/oauth2/authentication/config/WebSecurityConfig.java)에서 AuthenticationManagerBuilder를 통해 만들어진다. WebSecurityConfig에서 authenticationManagerBean()을 호출해서 Bean으로 등록해서 AuthorizationServerConfig에서 의존성 주입을 받는다. 
    - [Oauth2UserDetailsService](../authorization/src/main/java/example/oauth2/authentication/service/Oauth2UserDetailsService.java)를 통해 인증 진행(내부적으로 가지고 있음.)
1. [AuthorizationServerConfig.java](../authorization/src/main/java/example/oauth2/authentication/config/AuthorizationServerConfig.java) 설정
    ```java
            @Override
        public void configure(AuthorizationServerSecurityConfigurer security) {
            security.tokenKeyAccess("permitAll()"); 
            // 토큰 키 검증(/oauth/token_key) url의 권한. 익명 사용자 접근 가능
            // !!! TODO 예제들이 permitAll을 주로 사용해서 참조했는데 동작을 좀 확인해볼 필요가 있다.
            // org.springframework.security.oauth2.provider.endpoint.TokenKeyEndpoint
            security.checkTokenAccess("isAuthenticated()");
            // 토큰 /oauth/check_token) url의 권한. 인증을 거친 사용자만 접근 가능 
            // org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint.java
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore);
            // Oauth2UserDetailsService를 통해 만들어진 AuthenticationMangaer
            endpoints.tokenStore(tokenStore);
            // oauth grant를 통해 만들어진 토큰을 관리하는 토큰 스토어 설정 - redis 토큰 스토어 사용
            endpoints.setClientDetailsService(clientDetailsService);
            // client 관련 인증 담당하는 서비스 설정, http basic authentication으로 client 인증 진행.
            endpoints.authorizationCodeServices(authorizationCodeServices).userApprovalHandler(new DefaultUserApprovalHandler());
            // authorization_code 방식에서 authorization server에서 resource 서버로 전달하는 code 관리 및 검증할 때 사용되는 서비스를 설정
        }
    ```
 
#### Resource Server 설정
    Authorization Server에서 Resource Server(접근제어) 기능이 필요한경우를 위해 설정 
      - 예) 사용자 정보 조회:  /api/v1/userinfo
      
[ResourceServerConfig.java](../authorization/src/main/java/example/oauth2/authentication/config/ResourceServerConfig.java)
1. ResourceServerConfigurerAdapter 상속
    ```java
    public class ResourceServerConfig extends ResourceServerConfigurerAdapter
    ```
1. class 레벨에 @EnableResourceServer 추가
1. Resource Server로 접근제어를하기 위한 url rule 설정 
    ```java
    @Override
    	public void configure(HttpSecurity http) throws Exception {
    		http.antMatcher("/api/**").authorizeRequests()
    			.antMatchers("/api/v1/**").authenticated()
    			.anyRequest().permitAll();
    	}
    ```  
    
#### WebSecurity 설정
    authorization_code 방식을 사용하기 위해 설정이 필요.(implicit는 구현해보지 않아서 확인 필요.)
    
[WebServerConfig.java](../authorization/src/main/java/example/oauth2/authentication/config/WebSecurityConfig.java)
1. WebSecurityConfigurerAdapter 상속
   ```java
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter
    ```
1. class 레벨에 @Configuration 추가
1. (Optional) class 레벨에 @EnableWebSecurity 추가
1. 의존성 주입
  - (interface) org.springframework.security.core.userdetails.UserDetailsService
    - (class) [Oauth2UserDetailsService](../authorization/src/main/java/example/oauth2/authentication/service/Oauth2UserDetailsService.java)
1. AuthenticationManagerBuilder 설정(AuthentiacationManager 생성용)
    ```
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
    ```    
1. AuthenticationManager 빈 등록
    ```java
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    ```
1. HttpSecurity 설정
    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .permitAll();
    }

    ```
    1. 모든 url(/**)을 webSecurity에서 동작하도록 설정(authorization server, resource server 다음에 동작하게 설정됨.)
    1. form login 설정(browser로 권한없는 url 요청시 "/login" 301 redirect)

###  org.springframework.security.web.FilterChainProxy 설정
> security 설정 클래스에 Order 지정 

 - Order(Min): public 자원
    - ant style pattern
        - /static/**
        - /h2-console/**
    - filters
      - 없음
 - Order(0): AuthorizationServerConfig
   - ant style pattern
     - /oauth/token
     - /oauth/token_key
     - /oauth/check_token
     - /oauth/check_token
   - filters
     - WebAsyncManagerIntegrationFitler
     - SecurityContextPersistenceFilter
     - HeaderWriterFilter
     - LogoutFilter
     - **BasicAuthenticationFilter**
     - RequestCacheAwareFilter
     - SecurityContextHolderAwareRequestFilter
     - AnonymouseAuthenicationFilter
     - SessionManagementFilter
     - ExceptionTranslationFilter
     - FilterSecurityInterceptor 
 - Order(10): ResourceServerConfig
   - ant style pattern
     - /api/**
   - filters
     - WebAsyncManagerIntegrationFitler
     - SecurityContextPersistenceFilter
     - HeaderWriterFilter
     - LogoutFilter
     - **OAuth2AuthenticationProcessingFilter**
     - RequestCacheAwareFilter
     - SecurityContextHolderAwareRequestFilter
     - AnonymouseAuthenicationFilter
     - SessionManagementFilter
     - ExceptionTranslationFilter
     - FilterSecurityInterceptor  
 - Order(100): WebSecurityConfig
   - ant style pattern
     - 없음.(모든 url 적용, Order에 의해 위에서 처리되지 않는 url만 처리)
   - filters
     - WebAsyncManagerIntegrationFitler
     - SecurityContextPersistenceFilter
     - HeaderWriterFilter
     - LogoutFilter
     - **UsernamePasswordAuthenticationFilter**
     - **DefaultLoginPageGeneratingFilter**
     - RequestCacheAwareFilter
     - SecurityContextHolderAwareRequestFilter
     - AnonymouseAuthenicationFilter
     - SessionManagementFilter
     - ExceptionTranslationFilter
     - FilterSecurityInterceptor
     
#### security 설정외 더 보아야할 것.
1.src/main/java 하위
- [Member.java](../authorization/src/main/java/example/oauth2/authentication/entity/Member.java)
  - @Entity
  - user 정보 저장 [AuthorizationApplication.java](../authorization/src/main/java/example/oauth2/authentication/AuthorizationApplication.java)
  ```java
  @Bean
  public CommandLineRunner runner() {
      return args -> {
        List<String> roles = asList("USER", "ADMIN");
                final Member member = Member.builder()
                    .userId("user").password("{noop}password").roles(roles)
                    .build();
                memberRespository.save(member);
            };
        }
  ```
- [MemberRepository.java](../authorization/src/main/java/example/oauth2/authentication/repository/MemberRespository.java)
  - Oauth2UserDetailsService에서 Member entity 조회용으로 만듬.
- [Oauth2UserDetailsService.java](../authorization/src/main/java/example/oauth2/authentication/service/Oauth2UserDetailsService.java)
  - database에 있는 사용자 정보를 가져와서 UserDetails 반환하는 loadUserByUsername를 가진 @FunctionalInterface
- [UserController.java](../authorization/src/main/java/example/oauth2/authentication/controller/UserController.java)
  - GET /api/v1/userinfo
    - resource 서버에서 Principle을 가져오기 위해 만듬.
  - GET /test
    - WebSecurity 접근제어 테스트용.
- src/main/resources 하위
  - [application.yml](../authorization/src/main/resources/application.yml)
    - redis, jdbc, jpa, static resource 관련 설정 yml 참조.
  - GET /static/static_file.txt
    - "/static/**" url은 security filter를 안타는지 확인용.

### [oauth2.http](../http/oauth2.http) 파일을 참조하여 동작 확인

     
