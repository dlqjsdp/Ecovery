package com.ecovery.security;

import com.ecovery.domain.MemberVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security에서 사용할 사용자 정보 객체
 * MemberVO를 감싸서 UserDetails로 변환
 * 작성자 : 방희경
 */

@Getter @Setter
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails {

    private final MemberVO memberVO;

    // 사용자 권한 반환(USER / ADMIN)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + memberVO.getRole().name()));

    }


    // 사용자 비밀번호 반환(암호화 된 값)
    @Override
    public String getPassword() {
        return memberVO.getPassword();
    }

    // 사용자 ID로 이메일 사용
    @Override
    public String getUsername() {
        return memberVO.getEmail();
    }

    // 계정 만료 여부 -> true : 만료되지 않음(UserDetails 인터페이스에서 반드시 구현해야하는 필수 값으로 기재 진행)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정 잠김 여부 -> true : 만료되지 않음(UserDetails 인터페이스에서 반드시 구현해야하는 필수 값으로 기재 진행)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    //비밀번호 만료 여부 -> true : 만료되지 않음(UserDetails 인터페이스에서 반드시 구현해야하는 필수 값으로 기재 진행)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 계정 활성화 여부 -> true : 만료되지 않음(UserDetails 인터페이스에서 반드시 구현해야하는 필수 값으로 기재 진행)
    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
