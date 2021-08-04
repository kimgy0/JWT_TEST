package knut.clubWeb.auth;

import knut.clubWeb.domain.Member;
import knut.clubWeb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //이 메서드가 동작하는 시기는 언제냐?
    //http://localhost:8080/login
    /*
     * 우리는 loginform을 비활성화 disable 해놨기 때문에 login폼이 동작도 안하면서
     * 이메서드도 절대 실행될 일이 없다.
     *
     * 동작을 하게 하려면 필터를 추가해야됨.
     *
     * 그래서 우리는 필터를 하나 추가해줄건데
     * JwtAuthenticationFilter =>>만듬
     * 이클래스임
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByUsername(username);
        log.info("loadUserByUsername"); //실행되는지 확인
        return new PrincipalDetails(findMember);
    }
}
