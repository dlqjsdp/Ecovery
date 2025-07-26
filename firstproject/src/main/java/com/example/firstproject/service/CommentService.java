package com.example.firstproject.service;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository; // 댓글 리파지터리 객체 주입
    private final ArticleRepository articleRepository; // 게시글 리파지터리 객체 주입 -> 댓글을 생성할 때 대상 게시글 존재 여부를 파악하기 위해 등록 필요

    // 댓글 조회
    public List<CommentDto> comments(Long articleId) {
        /*// 1. 댓글 조회
        List<Comment> Comments = commentRepository.findByArticleId(articleId); // 리파지터에서 댓글 찾아서 Comments 변수에 저장(한 게시글에 여러 댓글이 달릴 수 있어 리스트로 받음)
        // 2. 엔티티 -> DTO 반환
        List<CommentDto> dtos = new ArrayList<CommentDto>();

        for(Comment comment : Comments) { // 댓글 리스트(엔티티)를 하나씩 꺼내면서 반복
            CommentDto dto = CommentDto.createCommentDto(comment); // 정적 메서드를 사용해서 Comment -> CommentDto 반환
            dtos.add(dto);
        }
        // 3. 결과 반환
        return dtos; */
        // 스트림문으로 개선하는 문법 -> 가독성을 높이고 코드 양을 줄일 수 있음
        return commentRepository.findByArticleId(articleId) // 댓글 조회
                .stream()  // 반복 : 가져온 닷글 엔티티 목록을 스트림으로 변경
                .map(comment -> CommentDto.createCommentDto(comment)) // 각 엔티티(comment)를 스트림으로부터 입력 받아 메서드를 호출해서 얻은 DTO를 다시 스트림에 매핑
                .collect(Collectors.toList()); // 스트림 데이터를 리스트 자료형으로 변환
    }

    // 댓글 생성
    @Transactional
    public CommentDto create(Long articleId, CommentDto dto) {
        // 1. 게시글 조회 및 예외 발생
        Article article = articleRepository.findById(articleId) // 게시글이 없을 경우 람다형식으로 내용 출력
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패!, " + articleId + "번 게시글은 없습니다!"));
        // 2. 댓글 엔티티 생성
        Comment comment = Comment.create(dto, article);
        // 3. 댓글 엔티티를 DB에 저장
        Comment created = commentRepository.save(comment);
        // 4. DTO로 변환해 반환
        return CommentDto.createCommentDto(created);
    }

    // 댓글 수정
    @Transactional
    public CommentDto update(Long id, CommentDto dto) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패!, 대상 댓글이 없습니다."));
        // 2. 댓글 수정
        target.patch(dto); // patch : 수정된 값만 수정하고 나머지는 유지
        // 3. DB로 갱신
        Comment updated = commentRepository.save(target);
        // 4. 댓글 엔티티를 DTO로 반환
        return CommentDto.createCommentDto(updated);
    }

    // 댓글 삭제
    @Transactional
    public CommentDto delete(Long id) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패!, 대상이 없습니다."));
        // 2. 댓글 삭제
        commentRepository.delete(target);
        // 3. 삭제 댓글을 DTO에 변환 및 반환
        return CommentDto.createCommentDto(target);
    }
}
