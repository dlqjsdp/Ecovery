package com.ecovery.security;

import com.ecovery.domain.MemberVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Spring Security에서 사용할 사용자 정보 객체
 * MemberVO를 감싸서 UserDetails로 변환
 * 작성자 : 방희경
 * @history
      - 250724 | yukyeong | OAuth2User 인터페이스 구현 및 소셜 로그인 대응 필드/메서드 추가
      - 250724 | yukyeong | attributes 필드 및 setAttributes(), getAttributes(), getName() 메서드 구현

 */

@Getter @Setter
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final MemberVO memberVO;
    private Map<String, Object> attributes; // 소셜 로그인(OAuth2) 사용자 정보


    // 소셜 로그인 시 attributes 설정 메서드 (생성자 없이 처리하기 위해 별도 메서드 사용)
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    // OAuth2User 필수 메서드: 사용자 식별자 반환
    @Override
    public String getName() {
        return String.valueOf(memberVO.getProviderId()); // 또는 memberVO.getEmail()
    }

    // OAuth2User 필수 메서드: 사용자 속성 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


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
