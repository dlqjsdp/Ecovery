package com.ecovery.domain;

import com.ecovery.constant.Role;
import lombok.*;

import java.util.Date;

/**
 * 로그인/회원가입 테이블(member)
 * @author : jihye Lee
 * @fileNmae : MemberVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {

    private Long memberId;      // 회원 고유 번호 (PK)
    private String email;       // 이메일 (로그인 ID)
    private String password;    // 비밀번호 (암호화 저장 권장)
    private String nickName;    // 닉네임
    private Role role;          // 권한 (USER, ADMIN)
    private Date createAt;      // 가입일시
}
