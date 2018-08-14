package example.oauth2.authentication.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
	@RequestMapping("/api/v1/userinfo")
	public Principal user(Principal principal) {
		return principal;
	}
}
