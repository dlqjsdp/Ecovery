package com.ecovery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 로그인, 회원가입, 비밀번호 암호화 등을 처리하기 위한 시큐리티 클래스 파일
 * 작성자 : 방희경
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    //보안 정책 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------Security Filter Chain----------");

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll() //정적 리소스는 누구나 접근 가능
                        .requestMatchers("/", "/member/signup", "/member/login", "/member/check-email").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        // 대형폐기물 인식 기능 (예: /waste/recognize): 비회원도 가능
                        //.requestMatchers("/waste/**").permitAll()  // url나오면 수정 예정
                        .requestMatchers("/admin/**").hasRole("ADMIN") //관리자 페이지는 관리자만 접근 가능
                        .anyRequest().authenticated() //그 외 모든 요청은 로그인 필수
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/member/login") //사용자 정의 로그인 페이지
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/") //성공 시 이동 url
                        .usernameParameter("email") //로그인 폼 name=email
                        .passwordParameter("password") //로그인 폼 name=password
                        .failureUrl("/member/login?error=true") //실패 시 리다이렉트
                        .permitAll()

                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout") //로그아웃 요청 url
                        .logoutSuccessUrl("/") //로그아웃 성공 시
                        .invalidateHttpSession(true) //세션 초기화
                        .deleteCookies("JSESSIONID") //쿠키 삭제
                );

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
