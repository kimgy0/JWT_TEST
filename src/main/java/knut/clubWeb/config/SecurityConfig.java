package knut.clubWeb.config;

import knut.clubWeb.Filter.LoginFilter;
import knut.clubWeb.jwt.JwtAuthenticationFilter;
import knut.clubWeb.jwt.JwtAuthorizationFilter;
import knut.clubWeb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final CorsFilter corsFilter;
    private final MemberRepository memberRepository;

    @Bean
    // 패스워드 인코더.
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new LoginFilter(), SecurityContextPersistenceFilter.class);
        //스프링 시큐리티보다 먼저 필터등록하기.


        //http.addFilterBefore(new LoginFilter(), BasicAuthenticationFilter.class);
        //필터의 구조를 알고 BasicAuthenticationFilter.class 전에 로그인필터를 필터걸어줌.
        //필터중에 제일먼저 실행이 됨.
        //시큐리티 필터가 우선순위.
        //http.addFilterBefore
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 세션을 사용하지 않음.
                .and()
                .addFilter(corsFilter)      //corsconfig class를 필터에 등록해줄 때 (인증이 필요할 때만) / @CrossOrigin 은 인증이 필요없을때 / @CrossOrigin 정책을 벗어날거야!
                .formLogin().disable()  // 폼을 쓰지 않음.
                .httpBasic().disable()  // 기본적인 http 방식을 쓰지 않음.
                                        /*
                                         * basic 방식은 헤더에 id랑 pw를 다 들고 서버에 요청을 하는 방식이라서 보안에 취약함
                                         * 대신에 id pw가 담긴 토큰을 암호화해서 로그인 요청을 하려면 https 프로토콜을 사용해야함
                                         * 그럴떄 헤더에 토큰을 올려서 토큰에 시간도 걸어주고 노출되어도 상관x 서버가 토큰을 다시 만들어주기 때문.

                                         * */
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) //이렇게 등록
                                                            //인자가 꼭필요한게 있는데 AuthenticationManager 이거를 통해서 로그인을함.
                                                            //이거는 시큐리티컨피그 (지금 클래스) 상속받는 클래스가 들고있음 메서드로
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),memberRepository))
                                                            //여기는 이미 토큰이 존재하고 토큰을 받았을 때 정상적인 사용자인지 검증하는 곳

                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }
}
