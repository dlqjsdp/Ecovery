package com.simplane.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {

    private Long memberid;
    private String userid;
    private String password;
    private Date birthdate;
    private Long sex;
    private String name;
}
