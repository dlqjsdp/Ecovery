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

    private final CommentService commentService;
    
    //1. 댓글 조회
    @GetMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long articleId) {

        List<CommentDto> dtos = commentService.comments(articleId);

        return (dtos != null) ?
                ResponseEntity.status(HttpStatus.OK).body(dtos):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    //2. 댓글 생성
    @PostMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long articleId, @RequestBody CommentDto dto) {
        log.info(articleId.toString());
        // 서비스에 위임
        CommentDto createDto = commentService.create(articleId, dto);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createDto);
    }
    //3. 댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id, @RequestBody CommentDto dto) {
        // 서비스 위임
        CommentDto updatedDto = commentService.update(id, dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }
    //4. 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long id) {
        //서비스 위임
        CommentDto deleteDto = commentService.delete(id);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(deleteDto);
    }

}
