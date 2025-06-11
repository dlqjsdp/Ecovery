package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    private final EntityManager entitymanager;

    @Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
        //1. dto 묶음을 엔티티 묶음으로 변환하기
        List<Article> articleList = dtos.stream()
                .map(dto -> dto.toEntity())
                .collect(Collectors.toList());
        //2. 엔티티 묶음을 DB에 저장하기
        log.info("되는 거니...............?");
        articleList.stream().forEach(article -> articleRepository.save(article));

        entitymanager.flush();

        //3. 강제 예외 발생시키기
        articleRepository.findById(-1L)
                .orElseThrow(()-> new IllegalArgumentException("실패!"));
        //4. 결과 값 반환하기
        return articleList;
    }
}
