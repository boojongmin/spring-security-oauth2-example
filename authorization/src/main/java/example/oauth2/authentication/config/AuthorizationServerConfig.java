package example.oauth2.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired @Qualifier(BeanIds.USER_DETAILS_SERVICE)
	private UserDetailsService userDetailsService;
	@Autowired
	private JdbcAuthorizationCodeServices jdbcAuthorizationCodeServices;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Bean
	public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory) {
		return new RedisTokenStore(redisConnectionFactory);
	}

	@Bean
	@Primary
	public JdbcClientDetailsService jdbcClientDetailsService(DataSource dataSource) {
		final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
		return jdbcClientDetailsService;
	}

	@Bean
	@Primary
	public JdbcAuthorizationCodeServices jdbcAuthorizationCodeServices(DataSource dataSource) {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security.tokenKeyAccess("permitAll()");
		security.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore);
		endpoints.tokenStore(tokenStore);
		endpoints.userDetailsService(userDetailsService);
		endpoints.setClientDetailsService(clientDetailsService);
		endpoints.authorizationCodeServices(jdbcAuthorizationCodeServices).userApprovalHandler(new DefaultUserApprovalHandler());
	}
}
