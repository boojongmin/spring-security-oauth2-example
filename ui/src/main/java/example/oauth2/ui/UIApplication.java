package example.oauth2.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
public class UIApplication {
	/*
	아래의 Bean이 없으면 http://localhost:8080/login => ERR_TOO_MANY_REDIRECTS 발생
	 */
	@Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    // 참조 https://github.com/eugenp/tutorials/tree/master/spring-security-sso
	public static void main(String[] args) {
		SpringApplication.run(UIApplication.class, args);
	}
}
