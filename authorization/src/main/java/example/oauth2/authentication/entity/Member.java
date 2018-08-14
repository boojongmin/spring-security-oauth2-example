package example.oauth2.authentication.entity;

import lombok.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Member {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	private String userId;
	private String password;
	@ElementCollection
	@CollectionTable( name = "role", joinColumns = @JoinColumn(name = "member_id"))
	private List<String> roles = new ArrayList<>();

	public UserDetails getUserDetails() {
		String[] roleArr = new String[this.roles.size()];
		roles.toArray(roleArr);
		return User.withUsername(this.userId).password(password).roles(roleArr).build();
	}
}
