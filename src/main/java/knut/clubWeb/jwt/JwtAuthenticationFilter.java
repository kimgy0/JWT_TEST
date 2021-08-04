package knut.clubWeb.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import knut.clubWeb.auth.PrincipalDetails;
import knut.clubWeb.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


//UsernamePasswordAuthenticationFilter
// /login 요청해서 username password 전송하면(post로)
//UsernamePasswordAuthenticationFilter가 동작을 함.

//원래도 동작하기는 하는데 지금 formlogin을 비활성화 시켜서
//강제로 작동하게 해주는 방법을 기술함.
// =>시큐리티 컨피그에 등록하기

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("============================================");
        log.info("JwtAuthenticationFilter 필터 실행중 로그인 시도");
        log.info("============================================");
        /**1. id와  pw 를 받기 **/
        try {
//            BufferedReader br = request.getReader();
//            String input = null;
//            while((input = br.readLine())!=null){
//                log.info(input);
//                //username=0000&password=0000 출력
            ObjectMapper objectMapper = new ObjectMapper();
            Member member = objectMapper.readValue(request.getInputStream(), Member.class);
            log.info("{}", member);

            /** 로그인시도 하기 (토큰을 만드는 과정) **/

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
            //인자로 username 만 떨어져 나가게 되고 password는 스프링이 db에서 알아서 해줌
            //로그인할 때 필요한 정보를 생성해냄.

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            //이게 실행되면 principalDetailsService의 loadUserByUsername 가 실행이 됨.

            //매니저가 대신 로그인을 진행해줌.
            //매니저에 토큰을 넣어서 실행하게 되면면 인증정보를 받음
            //여기 객체에는 로그인 정보가 담김.
            //성공적으로 담겼다는 거는 로그인 이정상적이고 db에 있는 패스워드와 유저네임이 일치한다! 라고 말할 수 있음.

            //그러면 그걸 꺼내줍시다.
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            log.info("============================================");
            log.info("회원가입을 시도한 멤버객체 (로그인 성공) = {}", principal.getMember());
            //이게 출력이 된다는것은 authentication 객체가 로그인을 성공을 했다는 것은
            //authentication 객체가 세션 영역에 잘 저장이 되었다는 뜻.

            return authentication;
            //리턴하는 이유는 시큐리티가 권한관리를 대신해주기 떄문에 편하려고 ?
            //리턴되면 세션에 실리나봄


            /** 이제는 로그인이 성공적으로 db와 맞아 떨어졌으니까 이제 토큰을 만들어서 던져주자 **/
            //굳이 여기서?
            //로그인이 정상적으로 성공하면 밑에 메서드로 가니까 밑으로 가자 ㄱㄱ

        } catch (Exception e) {
            e.printStackTrace();
        }

        //우리는 /login에 요청을 보내면 이 메세지가 동작함
        //그래서 우리는 이제 여기에서 login과 password를 받고
        //정상인지 로그인 시도를 해본다.
        /*
         * 정상인지 로그인을 시도하는 간단한 방법은 authenticationManager
         * 를 통해 로그인을 시도하면 principal details service가 호출이 된다.
         *
         * 그러면 principalDetailsService의 loadUserByUsername 가 실행이 됨.
         *
         * 그다음에는 반환된 principalDetails를 세션에 담아주고
         *     -> 굳이 세션에 담아주는 이유는 권한관리를 위해서 담아줘야함.
         *     -> 권한 관리가 필요없다면 안해도됨.
         *
         * jwt토큰을 만들어서 응답하면 됨.
         */

       return null; //인증실패

    }



    //login 시도할 때 attemptAuthentication (위의 메서드)가 실행후 인증이 정상적으로 되었으면 successfull --- 함수가 실행
    //      --> 인증이 정상적으로 완료 되었을 때 실행하는거임.


    //jwt토큰을 만들어서 request요청한 사용자게에 jwt토큰을 response해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("인증이 끝났으니까 토큰을 만들어보자!");
        /** 이제는 로그인이 성공적으로 db와 맞아 떨어졌으니까 이제 토큰을 만들어서 던져주자 **/

        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        //RSA 방식이 아닌 Hash 암호 방식
        String jwtToken = JWT.create()
                .withSubject("cos")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                .withClaim("id", principal.getMember().getId())
                .withClaim("username", principal.getMember().getUsername())
                .sign(Algorithm.HMAC512("cos")); //기본키 풀어야하는

        //토큰을 jwt를 헤더에 달아줌.
        response.addHeader("Authorization", "Bearer "+jwtToken); //담기는 형식
    }
}
