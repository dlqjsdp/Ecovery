package com.example.shop.repository;

import com.example.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

//Member 엔티티를 데이터 베이스에 저장할 수 있도록 만든 레파지토리

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email); //회워 가입 시 중복된 회원을 이메일로 검사할 수 있도록 작성한 쿼리 메소드
}
