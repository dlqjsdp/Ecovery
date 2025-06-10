package com.example.firstproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Member")  //테이블 이름 만들지 않으면 자동으로 클래스 이름으로 만들어짐
@AllArgsConstructor
@ToString
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  //자동 생성 기능 추가(숫자가 자동으로 매겨짐)
    private Long   id;

    @Column  //컬럼 어노테이션을 쓰지 않아도 필드를 컬럼으로 인식함.
    private String email;

    private String password;
}
