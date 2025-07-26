package com.example.firstproject.dto;

import com.example.firstproject.entity.Comment;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class CommentDto { // CommentDto : COmment(댓글 엔티티)를 담을 그릇, Comment의 구조와 동일해야함
    
    private Long id; // 댓글 id
    private Long articleId; // 댓글의 부모 id
    
    private String nickname; // 댓글 작성자
    private String body; // 댓글 본문

    public static CommentDto createCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .articleId(comment.getId())
                .nickname(comment.getNickname())
                .body(comment.getBody())
                .build();
    }
}
