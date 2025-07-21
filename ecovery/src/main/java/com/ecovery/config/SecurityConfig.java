package com.ecovery.config;

import com.ecovery.security.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 로그인, 회원가입, 비밀번호 암호화 등을 처리하기 위한 시큐리티 클래스 파일
 * 작성자 : 방희경
* @history
     - 2507?? | 방희경 | SecurityConfig 클래스 최초 작성
     - 250716 | yukyeong | 환경톡톡 게시판 누구나 접근 가능하게 변경
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //보안 정책 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------Security Filter Chain----------");

        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**", "/main").permitAll() //정적 리소스는 누구나 접근 가능
                        .requestMatchers("/", "/member/signup", "/member/login", "/member/check-email", "/member/check-nickname").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        .requestMatchers("/disposal/*","/disposal/disposalMain/*", "/disposal/history/*", "/api/disposal/*", "/feedback/*", "error").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        .requestMatchers("/env/**").permitAll() // 환경톡톡 게시판 누구나 접근 가능
                        .requestMatchers("/free/**").permitAll() // 무료나눔 게시판 누구나 접근 가능
                        // 대형폐기물 인식 기능 (예: /waste/recognize): 비회원도 가능
                        //.requestMatchers("/waste/**").permitAll()  // url나오면 수정 예정
                        .requestMatchers("/item/**").permitAll()    //Test용
                        .requestMatchers("/admin/**").hasRole("ADMIN") //관리자 페이지는 관리자만 접근 가능
                        .requestMatchers("/mypage/**").authenticated() //로그인한 사용자만 마이페이지 접근 가능
                        .anyRequest().authenticated() //그 외 모든 요청은 로그인 필수
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/member/login") //사용자 정의 로그인 페이지
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/main", true) //성공 시 이동 url
                        .usernameParameter("email") //로그인 폼 name=email
                        .passwordParameter("password") //로그인 폼 name=password
                        .failureUrl("/member/login?error=true") //실패 시 리다이렉트

                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout") //로그아웃 요청 url
                        .logoutSuccessUrl("/") //로그아웃 성공 시
                        .invalidateHttpSession(true) //세션 초기화
                        .deleteCookies("JSESSIONID") //쿠키 삭제
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // 우리가 만든 사용자 정보를 사용!
        authProvider.setPasswordEncoder(passwordEncoder()); // 우리가 만든 비밀번호 암호화 방식을 사용!
        return authProvider;
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
