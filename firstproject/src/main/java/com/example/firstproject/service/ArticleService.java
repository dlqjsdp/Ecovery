package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> index() {
        return articleRepository.findAll();
    }

    public Article show(Long id) {

        articleRepository.findById(id).ifPresent(article -> {});

        Article article = articleRepository.findById(id).orElse(null);

        return article;
    }

    public Article create(ArticleForm dto) {
        log.info("1차 조회 시도");
        Article article = dto.toEntity();
        if(article.getId() != null) {
            return null;
        }
        log.info("2차 조회 시도");
        return articleRepository.save(article);
    }

    public Article update(Long id, ArticleForm dto) {
        log.info("3차 조회 시도");
        //1. DTO를 엔티티로 변환
        Article article = dto.toEntity();
        log.info("되는 거야?" + article.toString(), id);
        //2. 타깃 조회
        Article target = articleRepository.findById(id).orElse(null);
        log.info("5차 조회 시도");
        //3. 잘못된 요청 처리
        if(target == null) {  //!id.equals(article.getId()) => 값 비교. ==는 주소 비교
            log.info("잘못됨" + article.getId() + " 이건왜" + id);
            return null;
        }
        //4. 업데이트 및 정상 응답(200)
        target.patch(article);
        return articleRepository.save(target);
    }

    public Article delete(Long id) {
        //1. 대상 찾기
        Article target = articleRepository.findById(id).orElse(null);
        //2. 잘못된 요청 처리
        if(target == null) {
            return null;
        }
        //3. 대상 삭제
        articleRepository.delete(target);
        return target;
    }
}
