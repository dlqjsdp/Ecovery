package com.example.firstproject.dto;

import com.example.firstproject.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MemberForm {

    private String email;
    private String password;

    public Member toEntity() {
        return new Member(null, email, password);
    }

    /* @AllArgsConstructor 어노테이션 사용으로 기재 불필요
    public MemberForm(String email, String password) {

        this.email = email;
        this.password = password;
    }*/
}
