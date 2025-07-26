package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // 생성자 주입
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository; // articleRepository 선언

    // 전체 조회
    public List<Article> index() {
        return articleRepository.findAll();
    }

    // 단건 조회
    public Article show(Long id) {
        return articleRepository.findById(id).get();
    }

    // create - insert sqp 실행
    public Article create(ArticleForm dto) {
        Article article = dto.toEntity();

        if(article.getId() != null) { // 아이디 값을 입력하면
            return null; // null 값을 띄울것이다
        }

        return articleRepository.save(article); // articleRepository 통해 DB에 저장
    }

    // update - update sql 실행
    public Article update(Long id, ArticleForm dto) {
        // 1. DTO -> entity 변환
        Article article = dto.toEntity(); // 모든 필드가 포함된 새로은 객체 생성
        // 2. 타닛 조회하기
        Article target = articleRepository.findById(id).orElse(null);
        // 3. 잘못된 요청 처리하기
        if (target == null || id != article.getId()) { // 잘못된 요청인지 판별
            return null;
        }
        // 4. update 및 정상 응답하기
        target.patch(article); // patch 사용 시 수정하지 않은 값을 유지하기 위해
        return articleRepository.save(target);
    }

    // delete
    public Article delete(Long id) {
        // 1. 대상 찾기 -> DB에 삭제할 대상 엔티티가 있는지 조회하고 없으면 null 값 반환, 반환받은 값은 target이라는 변수에 저장
        Article target = articleRepository.findById(id).orElse(null);
        // 2. 잘못된 요청 처리하기
        if (target == null) {
            return null;
        }
        // 3. 대상 삭제하기
        articleRepository.delete(target);
        return target;
    }


    // EntityManager : JPA에서 엔티티를 관리해주는 핵심 관리자(중간 관리자) - 엔티티 상태를 관리gksmssha
    private final EntityManager entityManager;

    // 트랜잭션 연습
    @Transactional // 해당 어노테이션을 기재하지 않으면 3개 중 1개가 실패나더라도 DB 정상 등록됨, 3개 중 1개라도 실패나면 전체 실패 나도록 처리
    public List<Article> createArticles(List<ArticleForm> dtos) {
        // 1. dto 묶음을 엔티티 묶음으로 변환하기
        List<Article> articleList = dtos.stream() // dtos를 엔티티의 묶음으로 만들기 위해 스트림 사용
                .map(dto -> dto.toEntity()) // map로 dto가 하나씩 올 때마다 dto.toEntity()를 수행해 매핑
                .collect(Collectors.toList()); // 매핑한걸 리스트로 묶어 최종 결과를 createArticles 저장
        // 2. 엔티티 묶음을 DB에 저장하기 -> articleList의 모든 article을 하나씩 꺼내서, articleRepository.save()를 실행해라
        articleList.stream().forEach(article ->
                {
                    articleRepository.save(article);

                    // DB에 저장 -> flush(); 또는 commit() 될 때까지 JPA는 DB에 인서트 하지 않음 // insert 쿼리를 DB에 날라가게 하기 위해 사용
                    entityManager.flush();
                }
        );


        /*for (Article article : articleList) { 2번과 같은 코드
            articleRepository.save(article);
        }*/
        // 3. 강제 예외 발생시키기 -> 트랜잭션이 실제로 잘 작동되는지 테스트하려고 일부러 예외를 발생 시킴
        articleRepository.findById(-1L) // id가 -1인 데이터 찾기 당연히 없는 데이터로 orElseThrow 메서드 실행
                .orElseThrow(() -> new IllegalArgumentException("결제 실패")); // orElseThrow 메서드 값이 존재하면 그 값을 반환 존재하지 않으면 전달값으로 보낸 예외 발생
        // 4. 결과 값 반환하기
        return articleList;
    }
}
