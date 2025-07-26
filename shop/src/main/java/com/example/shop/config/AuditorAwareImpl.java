package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

//Spring Security + JPA Auditing을 사용할 때, "누가 작성했는가 / 수정했는가"를 기록하는 작성자 정보 공급자 클래스

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    
    //현재 로그인 한 사용자 정보를 가져온다.
    public Optional<String> getCurrentAuditor() { //현재 로그인한 사용자의 정보를 가져와서 Optional<String>으로 반환하는 메서드
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //SecurityContextHolder에서 현재 로그인한 사용자의 인증 정보를 꺼내오는 코드

        log.info("Current user: {}", authentication.getName());

        String userId = "";
        if (authentication != null) { //인증 객체가 있을 경우 사용자 이름을 꺼냄
            userId = authentication.getName(); //현재 로그인한 사용자의 정보를 조회하여 사용자의 이름을 등록자와 수정자로 지정
        }

        return Optional.empty();
    }
}
