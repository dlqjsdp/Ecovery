package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST API 구현을 하려면 URL 설계 필요
// URL 요청을 받아 그 결과를  JSON으로 반환해줄 컨트롤러 생성

@RestController // 이 클래스는 REST 컨트롤러임을 선언
@RequiredArgsConstructor // 생성자 주입
@Slf4j
@RequestMapping("/api")
public class ArticleApiController {

 //   @Autowired -> 이거 대신 @RequiredArgsConstructor 사용이 좋음
    private final ArticleService articleService; // 서비스 객체 주입

    // GET -> 모든 게시글 조회
    @GetMapping("/articles")
    public List<Article> index() { // index() 메서드 정의, Article 묶음으로 반환해야해서 List로 기재됨
        return articleService.index(); // DB에 저장된 모든 데이터를 가져와 반환
    }
    // 단건 게시글 조회
    @GetMapping("/articles/{id}") // 조회하려는 게시글의 id에 따라 url 요청이 변경되어야하여 {id} 기재
    public Article show(@PathVariable Long id) {
        return articleService.show(id);
        // 메서드 반환형 : Article, 메서드 명 : show, @PathVariable : 매개변수로 id를 받아와야 하고 이때 id는 요청 url에서 가지고 와서 기재 진행
    }

    // POST -> 저장
    @PostMapping("/articles")
    public ResponseEntity<Article> create(@RequestBody ArticleForm dto) {
        Article created = articleService.create(dto);

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


    }

    // PATCH -> 수정
    // patch는 수정된 값만 수정되는 것이 맞으나 해당 코드에서 모든 필드가 포함된 새로운 객체를 생성하는 코드가 기재되어 있어 보내지 않는 필드 값은 null 값이 됨
    @PatchMapping("/articles/{id}") // Patch : 일부만 수정할 경우 수정하지 않은 값은 null 값으로 됨 -> 유지가 되도록 수정 필요
    public ResponseEntity<Article> update(@PathVariable Long id, @RequestBody ArticleForm dto) {

    //    Article article = articleRepository.findById(id).get();
        Article updated = articleService.update(id, dto);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // DELETE -> 삭제
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Article> delete(@PathVariable Long id) {
    //    articleRepository.deleteById(id); -> 정상 삭제 여부 확인

        Article deleted = articleService.delete(id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 트랜잭션 연습
    @PostMapping("transaction-test")
    //                                            @RequestBody : REST API 방식으로 POST 요청을 받고 있어 기재 + ArticleForm 데이터를 List로 묶은 dtos 매개변수 선언
    public ResponseEntity<List<Article>> transactionTest(@RequestBody List<ArticleForm> dtos) { // transactionTest 메서드 정의(컨트롤러가 데이터 3개의 요청을 받아 결과를 응답하도록)
        List<Article> createdList = articleService.createArticles(dtos); // 실제 작업은 서비스에서 진행함으로 articleService의 createArticles 메서드 실행 + 게시글 정보 createdList에 저장

        return (createdList != null) ? //createdList에 내용이 있다면
                ResponseEntity.status(HttpStatus.OK).body(createdList) : // createdList를 실어 보낸다.
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 내용이 없다면 빌드만 해서 보낸다

    }
}
