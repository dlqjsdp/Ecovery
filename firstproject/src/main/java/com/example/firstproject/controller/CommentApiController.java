package com.example.firstproject.controller;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService; // 댓글 서비스 객체 주입
    
    // 1. 댓글 조회
    @GetMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long articleId) { // DB에서 조회한 댓글 엔티티 목록은 List<Comment> 이지만 엔티티를 DTO로 반환하여 List<CommentDto>
        // 서비스 위임
        List<CommentDto> dtos = commentService.comments(articleId); // 서비스에 댓글 조회 위임하기 위해 comments(articleId) 메서드 호출, 실행 결과로 반환 받은 값 dtos에 저장
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(dtos); // 댓글이 있으면 목록을 리스트로 보여줌
    }

    // 2. 댓글 생성
    @PostMapping("/api/articles/{articleId}/comments")
                                            // PathVariable : 요청 URL의 articleId 가져오기,RequestBody : JSON 데이터 dto로 받기
    public ResponseEntity<CommentDto> create(@PathVariable Long articleId, @RequestBody CommentDto dto) {
        // 1. 서비스에 위임
        CommentDto createDto = commentService.create(articleId, dto);
        // 2. 결과 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(createDto);
    }
    
    // 3. 댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id, @RequestBody CommentDto dto) {
        // 1. 서비스 위임
        CommentDto updateDto = commentService.update(id, dto); // 수정할 댓글 id와 수정 데이터인 dot를 넘긴다
        // 2. 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updateDto); // 성공 수정 시 반환 받은 결과 클라이언트에 응답
    }
    
    // 4. 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long id) {
        // 1. 서비스에 위임
        CommentDto deletedDto = commentService.delete(id);
        // 2. 결과응답
        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
    }
}
