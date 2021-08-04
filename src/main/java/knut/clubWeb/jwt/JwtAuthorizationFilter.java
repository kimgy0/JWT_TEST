package knut.clubWeb.jwt;

/*
 * 로그인 한 사람이 다음 요청때 인증할 수 있는방법 (서명)
 *
 * 마지막이기 때문에 검증 하는방법을 다룸
 * 인증할 수 있는 방법은 밑에 기술해놨음.
 *
 */
//시큐리티가 filter를 가지고 있는데 그 필터 중에서 basicAuthenticationFilter라는게 있음
//권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.

//권한이나 인증이 필요한 주소가 아니라면 이 필터를 타지 않음.

//이거도 시큐리티 컨피그로 가서 필터추가 ㄱㄱ

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import knut.clubWeb.auth.PrincipalDetails;
import knut.clubWeb.domain.Member;
import knut.clubWeb.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 타게됨.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilterInternal(request, response, chain);
        /**
         * 비하인드.
         * 이거 주석처리 해준 이유는 맨밑에 dofilter 로 다른 필터 넘겨주다가 response가 두번 이루어지면
         * 오류가 나기때문
         */


        log.info("인증이나 권한이 필요한 주소 요청이 됨.==========do filter");
        String jwtHeader = request.getHeader("Authorization");
        log.info("jwtHeader : {}" ,jwtHeader);

        //여기서 부터는 jwt토큰을 검증해서 정상적인 사용자인지 확인할 겁니다.'

        //헤더값을 확인했는데 null값이거나 또는 있어도 bearer로 시작하지 않으면 걍 반환환
       if(jwtHeader == null || !jwtHeader.startsWith("Bearer ")){
            chain.doFilter(request, response); //다시 필터를 타게 다음필터로 넘겨버림
            return;
        }
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

        String username = JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString();
        //verify(jwtToken) 서명하는거
        //서명이 정상적으로 완료되면 username 클레임을 가져올거임.




        if(username != null){
            Member findMember = memberRepository.findByUsername(username);
            PrincipalDetails principalDetails = new PrincipalDetails(findMember);

            // jwt토큰이 서명을 통해서 서명이 정상이면 authentication 개체를 만들어준다.
            Authentication authentication
                    = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());


            // 원래는 매니저를 통해서 아이디 비밀번호를 쳐서 로그인 진행했는데
            // 우리는 인증을하니까 인증할 때 username이 null이 아니라는거는 정상적인 접근이기 때문에
            // principalDetails를 넣어주고 비밀번호에는 null(인가된사용자가 확인됨.)
            // 으로 넣어주면 강제로 authentication 객체를 생성해줄 수 있다.

            //이렇게해서 객체를 만들어줬으면
            //세션 공간을 얻어오자!
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //강제로 시큐리티의 세션에 접근해서 객체를 강제로 저장했다.

            //인증 작업으 세션에 올려주면 끝난다
            //그렇게 때문에 마무리로 다음 체인을 타게 한다.

            chain.doFilter(request, response);
        }
    }
}
