package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/new")
    public String newArticleForm() {
        log.info("New article form");
        return "articles/new";
    }

    @PostMapping("/create")
    public String createArticle(ArticleForm form) {
        log.info("Create article");
        log.info("articleForm : {}", form);

        //1. DTO를 엔티티로 변환
        Article article = form.toEntity();

        //2. 리파지터리로 엔티티를 DB에 저장
        Article saved = articleRepository.save(article);

        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        log.info("Show article {}", id);
        
        //1. {id}값을 DB에서 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);
        log.info("articleEntity : {}", articleEntity);
        
        //2. Entity -> DTO 변환
        //생략

        //3. view 전달
        model.addAttribute("article", articleEntity);

        return "articles/show";
    }

    @GetMapping
    public String index(Model model) {

        //1.모든 데이터 가져오기
        List<Article> articleEntityList = articleRepository.findAll();
        articleEntityList.forEach(List -> {log.info("List: {}", List);});
        //2. 모델에 데이터 등록하기
        model.addAttribute("articleList", articleEntityList);
        //3. 뷰 페이지 설정하기
        return "articles/index";
    }

    //articles/{{articles.id}}/edit
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {

        //1.수정할 테이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);

        //2.모델에 데이터 등록하기
        model.addAttribute("article", articleEntity);

        //3.뷰 페이지 설정하기
        return "articles/edit";
    }
}
