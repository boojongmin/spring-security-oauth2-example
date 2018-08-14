package example.oauth2.authentication;

import example.oauth2.authentication.entity.Member;
import example.oauth2.authentication.entity.Role;
import example.oauth2.authentication.repository.MemberRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

@SpringBootApplication
@EnableResourceServer
public class AuthorizationApplication {
	@Autowired
	MemberRespository memberRespository;

	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<Role> roles = asList(new Role("USER"), new Role("ADMIN"));
			final Member member = new Member("user", "{noop}password", roles);
			memberRespository.save(member);
		};
	}

	@Bean(destroyMethod = "stop")
	public RedisServer redisServer() throws IOException {
		final RedisServer redisServer = new RedisServer(6379);
		redisServer.start();
		return redisServer;
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationApplication.class, args);
	}
}
