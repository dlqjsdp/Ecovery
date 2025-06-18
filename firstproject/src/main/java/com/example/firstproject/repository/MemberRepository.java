package com.example.firstproject.repository;

import com.example.firstproject.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository<Member, Long> : JPA가 기본적인 CRUD 기능 제공
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAll(); // Repository 인터페이스에서 선언하는 메서드, 모든 엔티티 조회
}
