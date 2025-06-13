package com.example.firstproject.service;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ArticleRepository articleRepository;

    public List<CommentDto> comments(Long articleId) {
        /*
        //1. 댓글 조회
        List<Comment> comments = commentRepository.findByArticleId(articleId);
        //2. 엔티티 -> DTO 변환
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto dto = CommentDto.createCommentDto(comment);
            dtos.add(dto);
        }
        //3. 결과 반환
        return dtos;
        */

        return commentRepository.findByArticleId(articleId).stream()
                .map(comment -> CommentDto.createCommentDto(comment))
                .collect(Collectors.toList());

    }

    public CommentDto create(Long articleId, CommentDto dto) {
        //1. 게시글 조회 및 예외 발생
        Article article = articleRepository.findById(articleId).
                orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! " + 
                        articleId + "번 게시글이 존재하지 않음"));
        //2. 댓글 엔티티 생성
        Comment comment = Comment.createComment(dto, article);
        //3. 댓글 엔티티를 DB에 저장
        Comment created = commentRepository.save(comment);
        //4. DTO로 변환해 반환
        return CommentDto.createCommentDto(created);
    }

    public CommentDto update(Long id, CommentDto dtod) {
        //1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패."));
        //2. 댓글 수정
        target.patch(dtod);
        //3. DB에 갱신
        Comment updated = commentRepository.save(target);
        //4. 댓글 엔티티를 DTO로 변환 및 반환
        return CommentDto.createCommentDto(updated);
    }

    public CommentDto delete(Long id) {
        //1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        //2. 댓글 삭제
        commentRepository.delete(target);
        //3. 삭제 댓글을 DTO로 변환 및 반환
        return CommentDto.createCommentDto(target);
    }
}
