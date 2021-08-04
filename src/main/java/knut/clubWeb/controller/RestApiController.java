package knut.clubWeb.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @CrossOrigin 인증이 필요한 시큐리티 요청은 다 거부됨. 로그인을 해야되지만 할 수 있는 페이지는 이렇게 안됨. @CrossOrigin 은 인증이 필요없을때
public class RestApiController {

    @GetMapping("/home")
    public String home(){
        return "<h1>home</h1>";
    }

    @PostMapping("/token")
    public String token(){
        return "<h1>token</h1>";
    }
}
