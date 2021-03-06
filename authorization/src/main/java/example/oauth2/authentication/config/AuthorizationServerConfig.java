package example.oauth2.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
@Order(0)
//@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private AuthorizationCodeServices authorizationCodeServices;
	@Autowired
	private AuthenticationManager authenticationManager;
//	@Autowired @Qualifier(BeanIds.USER_DETAILS_SERVICE)
//	private UserDetailsService userDetailsService;
	@Bean
	public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory) {
		return new RedisTokenStore(redisConnectionFactory);
	}

	@Bean
	@Primary
	// @Primary가 없으면 oauth 기본  ClientDetailsService를 사용. InMemory
	public ClientDetailsService jdbcClientDetailsService(DataSource dataSource) {
		final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
		return jdbcClientDetailsService;
	}

	@Bean
	@Primary
	// @Primary가 없으면 oauth 기본  AuthorizationCodeServices를 사용. InMemory
	public AuthorizationCodeServices jdbcAuthorizationCodeServices(DataSource dataSource) {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security.tokenKeyAccess("permitAll()");
		security.checkTokenAccess("isAuthenticated()");
//		client 인증 http basic auth 대신 form authentication 지원하는 기능인데 동작하지 않아 주석 처리함.
//		security.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager);
		endpoints.tokenStore(tokenStore);
//		userDetailsService authenticationManger를 생성시 이미 들어간 정보이므로 넣지 않음
//		endpoints.userDetailsService(userDetailsService);
		endpoints.setClientDetailsService(clientDetailsService);
		endpoints.authorizationCodeServices(authorizationCodeServices);
	}
}
