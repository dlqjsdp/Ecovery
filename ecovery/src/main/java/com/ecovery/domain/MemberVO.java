package com.ecovery.domain;

import com.ecovery.constant.Role;
<<<<<<< HEAD
=======
import lombok.*;
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9

import java.util.Date;

/**
<<<<<<< HEAD
 * MemberVO
 * - 회원 정보를 담는 도메인 객체 (VO: Value Object)
 * - DB 테이블의 member 컬럼과 매핑됨
 */
=======
 * 로그인/회원가입 테이블(member)
 * @author : jihye Lee
 * @fileNmae : MemberVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9
public class MemberVO {

    private Long memberId;      // 회원 고유 번호 (PK)
    private String email;       // 이메일 (로그인 ID)
    private String password;    // 비밀번호 (암호화 저장 권장)
<<<<<<< HEAD
    private String nickname;    // 닉네임
=======
    private String nickName;    // 닉네임
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9
    private Role role;          // 권한 (USER, ADMIN)
    private Date createAt;      // 가입일시
}
