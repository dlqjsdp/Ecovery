package com.example.shop.entity;

import com.example.shop.constant.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;

//스프링 시큐리티와 연동해서 회원 정보를 DB에 저장하는 Member 엔티티

@Entity
@Getter
@Setter
@ToString //객체 문자열 추출
@Table(name="member")
@Builder
@AllArgsConstructor //모든 필드 매개변수로 받은 생성자 자동 생성
@NoArgsConstructor //파라미터 없는 기본 생성자 바로 생성
public class Member extends BaseEntity { //최초작성시간, 수정시간, 최초작성자, 수정자 자동으로 들어감(상속 받았으니까)

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB에서 자동 증가되는 숫자를 PK로 사용함
    private Long id;

    private String name;

    @Column(unique = true) //중복 되지 않도록 설정(유일 제약 조건)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING) //열거형 타입을 문자열로 저장, 꼭 STRING 사용 필요
    private Role role;

    //MemberFormDto -> member entity 변환 : DB에 저장하기 위해서
    // createMember 메서드는 회원가입 요청 시 Member 객체를 생성해주는 정적 팩토리 메서드, 외부에서 주입돼야 하므로 정적메서드로 만들어 서비스 계층이나 테스트 코드에서 사용할 수 있게 만듬
    //public static : 클레스 이름으로 바로 호출할 수 있는 메서드, 인스턴스 없이도 회원 객체를 만들 수 있는 정적 팩토리 메서드
    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) { // memberFormDto : 회원가입 폼에서 입력한 값을 담고 있는 dto, passwordEncoder 비밀번호 암호화하기 위해
    // String password = passwordEncoder.encode(memberFormDto.getPassword());
        return Member.builder()
                .name(memberFormDto.getName())
                .email(memberFormDto.getEmail())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .address(memberFormDto.getAddress())
                .role(Role.ADMIN) //권한
                .build();
    }
}
