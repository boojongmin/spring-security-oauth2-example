package example.oauth2.authentication;

import example.oauth2.authentication.entity.Member;
import example.oauth2.authentication.repository.MemberRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

@SpringBootApplication
public class AuthorizationApplication {
	@Autowired
	MemberRespository memberRespository;

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
