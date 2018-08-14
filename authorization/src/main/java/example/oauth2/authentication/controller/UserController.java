package example.oauth2.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
	@GetMapping("/api/v1/userinfo")
	public Principal user(Principal principal) {
		return principal;
	}

	@GetMapping("/test")
	public String test() {
		return "test";
	}
}
