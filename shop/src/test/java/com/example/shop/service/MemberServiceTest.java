package com.example.shop.service;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //DB에 저장하려면 @Transactional 주석 처리, 기재할 경우 DB 저장 안됨
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("apple@test.com")
                .name("안쪼꼼")
                .address("서울 중랑구")
                .password("1324")
                .build();

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    //회원가입 기능 테스트 - 같은 메일이면 이미 회원이라고 뜸
    @Test
    public void saveMemberTest(){
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(savedMember.getEmail(), member.getEmail());
        assertEquals(savedMember.getName(), member.getName());
        assertEquals(savedMember.getAddress(), member.getAddress());
        assertEquals(savedMember.getPassword(), member.getPassword());
        assertEquals(savedMember.getRole(), member.getRole());
    }

}