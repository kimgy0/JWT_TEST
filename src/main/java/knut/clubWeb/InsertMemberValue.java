package knut.clubWeb;


import knut.clubWeb.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Transactional
@Component
@RequiredArgsConstructor
public class InsertMemberValue {

    private final InitService initService;


    @PostConstruct
    public void init(){
       initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final BCryptPasswordEncoder bCryptPasswordEncoder;
        private final EntityManager em;

       public void dbInit(){
           Member member = new Member();
           member.setPassword(bCryptPasswordEncoder.encode("1234"));
           member.setUsername("1726092");
           member.setRoles("ROLE_ADMIN");
           member.setMembername("김규영");

           em.persist(member);
       }

    }
}
