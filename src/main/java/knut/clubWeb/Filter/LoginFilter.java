package knut.clubWeb.Filter;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;


        //토큰 : 코스 (있다고 가정하고)
        /*
        if(headrAuth.equals("cos")) {
                chain.doFilter(req,res);
         */
        //토큰 인증이 되면 filter를 타서 진행하고 인증을 계속해서 진행
        //토큰 인증이 되지 않으면 토큰인증이 안되면 컨트롤러 조차 진입도 못하게 할 예정.

        /**
         * 토큰을 cos를 만들어줘야함 id pw정상적으로 들어와서 로그인이 완료되면 토큰을 만들어주고 그거를 응답해준다.
         * 요청할 때 마다 header에 authorization에 value값으로 토큰을 가지고오면
         * 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지 검증만 하면된다.
         *
         * 정상적인 id pw가 들어왓을 때 토큰을 만들어주고 rsa, hs256으로 잠궈주고 토큰을 넘겨줌
         * 그걸 그대로 나한테 요청할때 다시 토큰을 주면 공개키로 열어보고 맞으면 허용 ㄱ
         */
//        req.setCharacterEncoding("UTF-8");
//       if(req.getMethod().equals("POST")){ //전송방식이 post이면
//           log.info("post요청됨");
//            String headrAuth = req.getHeader("Authorization"); //이 헤더값에 저장되는게 토큰인데
//            log.info("{}",headrAuth); //그 토큰의 값을 출력해야한다.
//
//           if(headrAuth.equals("cos")) {
//                chain.doFilter(req,res);
//           }else{
//               PrintWriter out = res.getWriter();
//               out.print("auth x");
//
//           }
//        }
//        log.info("Login Filter");
        chain.doFilter(req, res);
    }
}
