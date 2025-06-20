package com.example.shop.dto;

import lombok.*;

//회원 가입 화면으로부터 넘어오는 가입정보를 담을 dto

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormDto {

    private String name;
    private String email;
    private String password;
    private String address;
}
