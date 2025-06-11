package com.example.firstproject.dto;

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
    private String title;
    private String content;


    public Article toEntity() {
        return new Article(id, title, content);
    }
}
