package com.example.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

//회원 가입 화면으로부터 넘어오는 가입정보를 담을 dto, 백엔드의 유효성 검사하는 곳

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.") //NotBlank : null 값 체크 + 문자열의 경우 빈 문자열인지 검사(String 전용, 공백만 있어도 불가)
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.") //NotEmpty : null 값 체크 + 문자열의 경우 빈 문자열인지 검사(단순히 비어있지만 않으면 성공 공백만 써도 가능)
    @Email(message = "이메일 형식으로 입력해 주세요.") //Email : 이메일 형식인지 검사
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요.") //Length : 최소, 최대 길이 검사
    private String password;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;
}
