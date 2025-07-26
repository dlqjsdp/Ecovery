package com.example.firstproject.service;

import com.example.firstproject.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // 테스트
@Slf4j
class ArticleServiceTest {

    @Autowired // 생성자 주입, 테스계정에서 @RequiredArgsConstructor 사용 불가
    ArticleService articleService;

    @Test
    void index() {

        // 1. 예상 데이터
        // 2. 실제 데이터
        // 3. 비교 및 검증

    }

    @Test
    void show_성공() {
        // id : 11로 테스트
        // "id": 11,
        //"title": "랑둥이",
        //"content": "귀여워 사랑스러워 이쁘염"

        // 1. 예상 데이터
        Article expected = new Article(11L, "랑둥이", "귀여워 사랑스러워 이쁘염");
        // 2. 실제 데이터
        Article article = articleService.show(11L);
        // 3. 비교 및 검증
        assertEquals(expected.toString(), article.toString());
    //    log.info("article = {}", article);
    }
    @Test
    void show_실패() {
        // 1. 예상 데이터
        Article expected = new Article(11L, "랑둥이", "귀여워 사랑스러워");
        // 2. 실제 데이터
        Article article = articleService.show(11L);
        // 3. 비교 및 검증
        assertEquals(expected.toString(), article.toString());
    }

    @Test
    void create() {
    }
}