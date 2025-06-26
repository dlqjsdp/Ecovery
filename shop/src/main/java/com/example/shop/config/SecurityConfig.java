package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

        http
                //요청 URL에 따라 권한을 설정하는 시작점
                .authorizeHttpRequests(config -> config
                        //정적 리소스 css, js...는 누구나 접근 가능하게 허용, + .permitAll() : 로그인 하지 않아도 ok
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                        //홈페이지, 회원 기능, 상품 관련 페이지도 로긍ㄴ 없이 접근 가능
                        .requestMatchers("/", "/members/**", "/item/**").permitAll()
                        // /admin으로 시작하는 경로는 ADMIN 권한 있는 사용자만 접근 가능 // permitAll() : 모든 사람이 다 권한 있음
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        //그외 모든 요청은 로그인된 사용자만 접근 가능
                        .anyRequest().authenticated()
                );
        http
                .csrf(csrf -> csrf.disable())
                //전체 접근을 허용하는 권한을 주겠다 - 람다식으로 표현
                .formLogin(form -> form.loginPage("/members/login") //로그인하면 동작
                        .defaultSuccessUrl("/")
                        //login화면에서 name=username이면 생략가능
                        //username -> email쓰기 때문에 반드시 기입해애함 -> email로 로그인 값 전송하겠소
                        .usernameParameter("email")
                        .failureUrl("/members/login/error")
                )
                .logout(logout -> logout
                        .logoutUrl("/members/logout")
                        .logoutSuccessUrl("/")

                        .invalidateHttpSession(true) //세션 무효화(선택 사항이지만 일반적으로 사용)
                        .deleteCookies("JSESSIONID") //쿠기 삭제(선택 사항이지만 일반적으로 사용)
                );

        return http.build(); //http.build(); : 설정한 내용을 바탕으로 SecurityFilterChain 객체를 만들어 리턴
    }

    @Bean //스프링 컨테이너(메모리)에 PasswordEncoder 객체를 등록하겠다
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //BCryptPasswordEncoder : 비밀번호 암호화
    }
}
