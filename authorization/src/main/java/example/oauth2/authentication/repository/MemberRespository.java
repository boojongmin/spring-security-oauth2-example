package example.oauth2.authentication.repository;

import example.oauth2.authentication.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRespository extends JpaRepository<Member, Long> {
	Optional<Member> findByUserId(String username);
}
