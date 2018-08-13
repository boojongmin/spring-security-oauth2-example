package example.oauth2.authentication.service;

import example.oauth2.authentication.entity.Member;
import example.oauth2.authentication.repository.MemberRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service(BeanIds.USER_DETAILS_SERVICE)
public class Oauth2UserDetailsService implements UserDetailsService {
	@Autowired
	private MemberRespository memberRespository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Member> memberOptional = memberRespository.findByUserId(username);
		return memberOptional.map(Member::getUserDetails).orElse(null);
	}
}
