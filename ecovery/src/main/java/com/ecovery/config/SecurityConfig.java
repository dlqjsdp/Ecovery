package com.ecovery.config;

import com.ecovery.security.CustomUserDetailsService;
import com.ecovery.service.OAuth2MemberService;
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
- 250723 | yukyeong | 공지사항 게시판 누구나 접근 가능하게 변경
- 250725 | yukyeong | OAuth2 소셜 로그인 설정 추가 (카카오 로그인 연동)
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private OAuth2MemberService oAuth2MemberService; // 소셜 로그인 사용자 처리 서비스


    //보안 정책 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------Security Filter Chain----------");

        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/*", "/css/**", "/js/**", "/img/**", "/fonts/**", "/main").permitAll() //정적 리소스는 누구나 접근 가능
                        .requestMatchers("/", "/member/signup", "/member/login", "/member/check-email", "/member/check-nickname").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        .requestMatchers("/disposal/*","/disposal/disposalMain/*", "/disposal/history/*", "/api/disposal/*", "/feedback/*", "/error").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        .requestMatchers("/env/**").permitAll() // 환경톡톡 게시판 누구나 접근 가능
                        .requestMatchers("/notice/**").permitAll() // 공지사항 게시판 누구나 접근 가능
                        .requestMatchers("/item/**").permitAll()    //Test용 (추후에 변경)
                        .requestMatchers("/api/**").permitAll()     //AJAX 요청 모두 허용
                        .requestMatchers("/order/**").permitAll()   //주문 Test용으로 모두 허용 (추후에 .authenticated()로 변경)
                        .requestMatchers("/ecovery/**").permitAll()
                        .requestMatchers("/free/register").hasAnyRole("USER", "ADMIN") // 무료나눔 등록 - USER 또는 ADMIN만 가능
                        .requestMatchers("/free/modify/**", "/free/delete/**").hasAnyRole("USER", "ADMIN") // 무료나눔 수정, 삭제 - USER 또는 ADMIN만 가능
                        .requestMatchers("/free/**").permitAll() // 무료나눔 목록, 상세 누구나 접근 가능
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
                )

                // 소셜 로그인 기능을 활성화
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login") // 소셜 로그인 시 보여줄 로그인 페이지 지정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2MemberService) // 사용자 정보 처리용 커스텀 서비스 연결
                        )
                        .defaultSuccessUrl("/main", true) // 로그인 성공 후 이동할 경로 지정
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