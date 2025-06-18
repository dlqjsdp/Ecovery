package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller //컨트롤러 선언
@Slf4j //@Log4j과 동잃한 내용(사용 불가)
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    // ArticleRepository : JPA를 사용하기 위해 직접적으로 선언
    private ArticleRepository articleRepository; // articleRepository 객체 선언
    @Autowired
    private CommentService commentService;

    // 화면 보여주는 페이지 : CRUD에 C
    @GetMapping("/new") //URL 요청 접수
    public String newArticleForm() { //메서드 생성 및 반환값 작성
        log.info("New article form");
        return "articles/new";
    }
    // 글 작성 + 저장 : CRUD에 C
    @PostMapping("/create")
    public String createArticle(ArticleForm form){ // ArticleForm form : form 데이터(제목, 내용)를 DTO로 받기
        log.info("New article created");
        log.info("articleForm : {}", form);

        // 1. DTO를 엔티티로 변환
        Article article = form.toEntity(); // form 객체의 toEntity()라는 메서드를 호출하여 그 반환 값을 Article 타입의 article 엔티티에 저장

        // 2. 리파지터리 엔티티로 DB에 저장
        Article saved = articleRepository.save(article); // article 엔티티를 저장해 saved 객체에 반환, saved : DB에 저장된 정보를 가지고 있음
        log.info("New article : {}", saved);

        return "redirect:/articles/" + saved.getId(); // 경로 + id값을 가져오기 위해 saved 객체 이용
    }

    // 단건조회 : CRUD에 R
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        log.info("Show article {}", id);

        // 1. {id}값을 DB에서 꺼내오기
    //  Optional<Article> article = articleRepository.findById(id); // 아이디 조회(조회 시 데이터가 없을 경우 null 반환, 아이디 있을 경우 엔티티 반환)
        Article articleEntity = articleRepository.findById(id).orElse(null); // 위에 주석처리한 코드와 같은 코드
        List<CommentDto> commentDtos = commentService.comments(id); // 서비스에서 comments(id) 메서드를 호출해 조회한 댓글 목록을 commentDtos 변수에 저장

        log.info("Article : {}", articleEntity);
        
        // 2. Entity -> DTO 반환
        // 생략
        
        // 3. View 전달
        model.addAttribute("article", articleEntity);
        model.addAttribute("commentDtos", commentDtos); // 댓글 목록 모델에 등록
        return "articles/show";
    }
    // 전체 목록 조회
    @GetMapping("")
    public String index(Model model) {

        // 1. 모든 데이터 가져오기
    //  List<Article> articleEntityList = (List<Article>)articleRepository.findAll(); -> 이 방법은 사용하지 않는게 좋음
        List<Article> articleEntityList = articleRepository.findAll(); // + ArticleRepository 인터페이스에 List<Article> findAll(); 메소드 선언

        articleEntityList.forEach(list -> {log.info("Article : {}", list);}); // 잘 조회 되는지 확인하는 log

        // 2. 모델에 데이터 등록하기
        model.addAttribute("articleList", articleEntityList);

        // 3. 뷰 페이지 설정하기
        return "articles/index";
    }

    // /articles/{{article.id}}/edit -> update get 요청
    // localhost:8080/articles/2/edit -> 이렇게 요청해야 edit() 메소드가 응답합
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        
        // 1. 수정할 데이터 가져오기
    //  Optional<Article> articleEntity = articleRepository.findById(id); // 아이디 값이 존재하지 않을 경우
    //  Article article = articleEntity.orElse(null); // null 값으로 반환한다
        Article articleEntity = articleRepository.findById(id).orElse(null); // 위에랑 같은 내용 코드

        // 2. 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity);

        // 3. 뷰 페이지 설정하기
        return "articles/edit";
    }

    // 데이터 수정
    @PostMapping("/update")
    public String updateArticle(ArticleForm form) {

        log.info("Update article {}", form);

        // 1. DTO를 엔티티로 변환하기
        Article articleEntity = form.toEntity();

        // 2. 엔티티를 DB에 저장하기
        // 데이터 저장 = DB에서 데이터 찾기.데이터가 없으면 null 반환
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        // DB에서 기존 데이터를 가져오고 나면 기존 데이터의 값을 업데이트하는 코드
        if (target != null) {
            articleRepository.save(articleEntity); // 업데이트 명령어 따로 없음, 수정된 데이터 저장이니 저장 명령어 같이 사용
        }
        
        // 3. 수정 결과 페이지로 리다이렉트하기
        return  "redirect:/articles/" + target.getId();
    }

    // 데이터 삭제
    // localhost:8080/articles/2/delete -> 이렇게 요청해야 edit() 메소드가 응답합
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        // 1. 삭제할 데이터 가져오기
        Article target = articleRepository.findById(id).orElse(null); // 아이디가 없으면 null값

        // 2. 대상 엔티티 삭제하기
       if (target != null) { // 삭제할 대상이 있는지 확인하고
           articleRepository.delete(target); // delete() 메서드로 대상 삭제
           redirectAttributes.addFlashAttribute("msg", "삭제되었습니다!");
       }

        // 3. 결과 페이지로 리다이렉트하기
        return "redirect:/articles";
    }
}
