package knut.clubWeb.auth;

import knut.clubWeb.domain.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


// 일반로그인에서!!!! 시큐리티가 login주소를 날리면 낚아채서 로그인을 진행시킴
// 로그인을 진행하기 완료ㅕ가 되면 시큐리티 세션을 만들어주고 security contextholder 에 저장이 됩니다.
@Data
public class PrincipalDetails implements UserDetails {

    private Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    @Override
    //해당 멤버의 권한을 리턴하는 곳.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        member.getRoleList().forEach(r->{
            authorities.add(()->r);
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
