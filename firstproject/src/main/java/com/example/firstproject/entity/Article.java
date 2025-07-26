package com.example.firstproject.entity;

// DB 반영 용도로 사용, 테이블 작성

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity // 엔티티 선언, JPA에서 자공하는 어노테이션(테이블로 생성 // 무조건 기본키가 적용되어 있어야 함(기본키 없으면 클래스 오류 발생))
// @Table(name = article) //테이블 이름(설정하지 않을 경우 클래스 이름 Article이 기본적으로 테이블 이름이 됨)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Article {
    
    @Id // 기본키 설정(필수)
    @GeneratedValue(strategy = GenerationType.AUTO) // 대푯값 자동 생성, 자동생성 기능 추가(숫자가 자동 증가)ㄴ
    private Long id; //기본키 적용

    @Column() // 타이틀 필드 선언, DB 테이블의 테이블 열과 연결됨, nullable = false : not null -> 테이블에서 null값을 허용하지 않겠다
    private String title;

    @Column
    private String content;
    
    // patch() 메서드 직접 만들기
    // patch는 일부만 수정할 경우 수정하지 않는 값은 null로 변경이 된다 -> 수정 하지 않는 값을 유지하기 위해 기재
    public void patch(Article article) {

        if (article.title != null) {
            title = article.title;
        }
        if (article.content != null) {
            content = article.content;
        }
    }
}
