package example.oauth2.authentication.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Role {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	@ManyToOne
	private Member member;
	@NonNull
	private String name;

	public GrantedAuthority getGrantedAuthority() {
		return new SimpleGrantedAuthority("ROLE_" + name);
	}
}
