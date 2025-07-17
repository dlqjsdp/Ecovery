package com.ecovery.domain;

import com.ecovery.constant.AgreeType;
import com.ecovery.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * MemberVO
 * - 회원 정보를 담는 도메인 객체 (VO: Value Object)
 * - DB 테이블의 member 컬럼과 매핑됨
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {

    private Long memberId;      // 회원 고유 번호 (PK)
    private String email;       // 이메일 (로그인 ID)
    private String password;    // 비밀번호 (암호화 저장 권장)
    private String nickname;    // 닉네임
    private Role role;          // 권한 (USER, ADMIN)
    private Date createdAt;      // 가입일시
    private AgreeType agreeRequired; //필수 약관
    private AgreeType agreeOptional; //선택 약관

}
