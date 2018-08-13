package example.oauth2.authentication.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Member {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	private String userId;
	private String password;
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Role> roles;

	public Member() {}

	public Member(String userId, String password, List<Role> roles) {
		this.userId = userId;
		this.password = password;
		roles.forEach(x -> x.setMember(this));
		this.roles = roles;
	}

	public UserDetails getUserDetails() {
		final List<GrantedAuthority> authorities = roles.stream().map(Role::getGrantedAuthority).collect(toList());
		return User.withUsername(this.userId).password(password).authorities(authorities).build();
	}
}
