package example.oauth2.authentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

//@Configuration
//@EnableResourceServer
////@Order(1)
//public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		http.antMatcher("/user/**").authorizeRequests()
//			.antMatchers("/user/me").authenticated()
//			.anyRequest().permitAll();
//	}
//}
