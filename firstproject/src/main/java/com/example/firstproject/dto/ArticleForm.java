package com.example.firstproject.dto;

// 화면에 보여주는 용도로 사용

import com.example.firstproject.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ArticleForm {

    private Long id;
    private String title; //제목을 받을 필드
    private String content; //내용을 받을 필드

    public Article toEntity() { // toEntity 메서드 추가
        return new Article(id, title, content); // 화면 상에 입력 받은 값 객체 생성
    }

 /*   전송받은 제목과 내용을 필드에 저장하는 생성자 추가 -> @AllArgsConstructor 어노테이션 사용 시 미작성 가능
    public ArticleForm(String title, String content) {
        this.title = title;
        this.content = content;
    }*/
}
