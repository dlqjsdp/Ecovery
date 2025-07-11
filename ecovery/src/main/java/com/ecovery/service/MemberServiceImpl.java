package com.ecovery.service;

import com.ecovery.domain.MemberVO;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 회원가입, 로그인를 위한 MemberServiceImpl
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * 작성자 : 방희경
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    //회원 가입
    @Override
    public void registerMember(MemberVO memberVO){
        // 1. 평문 비밀번호 꺼냄
        String rawPassword = memberVO.getPassword();

        // 2. 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 3. 재 세팅
        memberVO.setPassword(encodedPassword);

        // 4. DB 저장
        memberMapper.insertMember(memberVO);
    }
    //회원 정보 수정
    @Override
    public void updateMember(MemberVO memberVO){

        memberMapper.updateMember(memberVO);
    }
    //회원 번호(PK)로 회원 조회
    @Override
    public MemberVO getMemberById(Long memberId){

        return memberMapper.findByMemberId(memberId);
    }
    //이메일로 회원 조회(로그인, 이메일 중복 체크 등에 사용)
    @Override
    public MemberVO getMemberByEmail(String email){

        return memberMapper.findByEmail(email);
    }
    //전체 회원 조회
    @Override
    public List<MemberVO> getAllMembers(){

        return memberMapper.findAllMembers();
    }

}
