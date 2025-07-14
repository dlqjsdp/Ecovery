package com.ecovery.mapper;

import com.ecovery.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 회원가입, 로그인를 위한 MemberMapper
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * MyBatis 매퍼 인터페이스 / MemberMapper.xml과 연동되어 SQL 실행
 * 작성자 : 방희경
 */
@Mapper
public interface MemberMapper {

    /**
     * 회원 등록
     * - 입력한 MemberVO 정보를 DB에 저장
     */
    public void insertMember(MemberVO member);

    /**
     * 회원 정보 수정
     * - 닉네임, 비밀번호 등 변경
     */
    public void updateMember(MemberVO member);

    /**
     * 회원 번호(PK)로 회원 조회
     */
    public MemberVO findByMemberId(Long memberId);

    /**
     * 이메일로 회원 조회
     * - 로그인, 이메일 중복 체크 등에 사용
     */
    public MemberVO findByEmail(String email);

    /**
     * 닉네임으로 회원 조회
     * - 로그인, 이메일 중복 체크 등에 사용
     */
    public MemberVO findByNickname(String nickname);

    /**
     * 전체 회원 목록 조회
     */
    public List<MemberVO> findAllMembers();





}
