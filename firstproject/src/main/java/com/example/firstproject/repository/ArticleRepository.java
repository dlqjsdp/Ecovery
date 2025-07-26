package com.example.firstproject.repository;

import com.example.firstproject.entity.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// CRUD 진헹 시 CrudRepository<Article,Long> 필수
// 아무것도 없어도 저장, 조회 가능
public interface ArticleRepository extends CrudRepository<Article,Long> { // Long : 기본키의 자료형 기재

    List<Article> findAll(); // Repository 인터페이스에서 선언하는 메서드

    Long id(Long id);
}
