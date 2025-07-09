package com.ecovery.mapper;

import com.ecovery.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MemberMapper
 * - MyBatis 매퍼 인터페이스
 * - MemberMapper.xml과 연동되어 SQL 실행
<<<<<<< HEAD
=======
 * @author : jihye Lee
 * @fileNmae : MemberMapper
 * @since : 20250708
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9
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
     * 전체 회원 목록 조회
     */
    public List<MemberVO> findAllMembers();





}
