package com.example.firstproject.entity;

import com.example.firstproject.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Comment {

    @Id  //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id")
    private Article article;

    public static Comment createComment(CommentDto dto, Article article) {

        //log.info("DTO ArticleId: {}, Article Entity Id: {}", dto.getArticleId(), article != null ? article.getId() : "null");

        //예외 발생
        if(dto.getId() != null)
            throw new IllegalArgumentException("댓글 생성 실패! 댓글 id를 지우시오.");
        if(dto.getArticleId() != article.getId())
            throw new IllegalArgumentException("댓글 생성 실패! 게시글 id를 확인하시오.");
        //엔티티 생성 및 반환
        return new Comment(dto.getId(), dto.getNickname(), dto.getBody(), article);
    }

    public void patch(CommentDto dto) {
        //예외 발생
        if(this.id != dto.getId())  //this는 service의 target
            throw new IllegalArgumentException("댓글 수정 실패. 댓글 id를 확인하시오.");
        //객체 갱신
        if(dto.getNickname() != null)
            this.nickname = dto.getNickname();
        if(dto.getBody() != null)
            this.body = dto.getBody();
    }
}
