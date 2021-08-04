package knut.clubWeb.config;

import ch.qos.logback.classic.servlet.LogbackServletContainerInitializer;
import knut.clubWeb.Filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.FilterRegistration;

@Configuration
public class FilterConfig {
    //그런데 굳이 securityConfig 클래스에서
    //http.addFilterBefore(new LoginFilter(), BasicAuthenticationFilter.class);
    //이런식으로 걸어주지말고
    //filterconfig에서 걸어주려고 클래스를 하나 따로 만듬.
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter(){
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>(new LoginFilter());
        bean.addUrlPatterns("/**"); //모든 url에 대해서 필터를 허용하라
        bean.setOrder(0); //숫자가 낮을 수록 우선순위가 높아서 먼저 실행됨
        return bean;
    }


    //필터를 하나 더 등록하고 싶으면 로그인 필터처럼 클래스를 하나 복붙해서 setOrder(1) 로 만들어준다.
    //    @Bean
    //    public FilterRegistrationBean<LoginFilter> loginFilter(){
    //        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>(new LoginFilter());
    //        bean.addUrlPatterns("/**"); //모든 url에 대해서 필터를 허용하라
    //        bean.setOrder(1); //숫자가 낮을 수록 우선순위가 높아서 먼저 실행됨
    //        return bean;
    //    }

}
