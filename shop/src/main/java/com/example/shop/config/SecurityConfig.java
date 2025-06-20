package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 컨트롤러, 서비스에 매번 객체를 만들지 않고 메모리에 딱 1개만 생성하여 관리하는 클래스

@Configuration //스프링 설정 클래스임 선언, @Bean들을 등록하기 위한 설정 파일이란 뜻
@EnableWebSecurity //스프링 시큐리티를 웹 보안 설정에 적용하겠다
@Slf4j
public class SecurityConfig {

    // @Bean으로 등록해두면 @Autowired나 생성자 주입을 통해 필요한 곳에서 바로 사용 가능
    @Bean //스프링 컨테이너(메모리)에 SecurityFilterChain 객체를 등록하겠다
    // HttpSecurity : 보안 설정을 담당하는 클래스, 로그인/로그아웃, 권한 설정 등을 구성할 수 있음 // 제약 조건 기재 필요
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------Security Filter Chain----------");

        return http.build(); //http.build(); : 설정한 내용을 바탕으로 SecurityFilterChain 객체를 만들어 리턴
    }

    @Bean //스프링 컨테이너(메모리)에 PasswordEncoder 객체를 등록하겠다
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //BCryptPasswordEncoder : 비밀번호 암호화
    }
}
