package com.example.shop.repository;

import com.example.shop.constant.Role;
import com.example.shop.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired //회원 정보를 처리하기 위한 MemberRepository 객체를, 스프링이 자동으로 주입
    private MemberRepository memberRepository;

    @Autowired //비밀번호 암호화를 위해 의존성 주입
    private PasswordEncoder passwordEncoder;

    //Spring Boot + JPA 환경에서 회원(Member) 정보를 저장하는 테스트
    @Test
    public void memberInsertTest() {
        Member member = Member.builder()
                .email("test@test2.com")
                .name("푸동이")
                .address("서울 중랑구")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    //Spring Boot + JPA 환경에서 회원(Member) 정보를 이메일 기준으로 조회하는 테스트
    @Test
    public void memberSelectTest() {
        Member member = memberRepository.findByEmail("test@test.com");
        assertNotNull(member);
    }

}