package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.dto.MemberForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/signup")
    public String newMemberForm() {
        log.info("New Member form");
        return "members/signup";
    }

    @PostMapping("/signed")
    public String createMember(MemberForm form) {
        log.info("Create member");
        log.info("memberForm : {}", form);

        //1. DTO를 엔티티로 변환
        Member member = form.toEntity();

        //2. 리파지터리로 엔티티를 DB에 저장
        Member saved = memberRepository.save(member);

        return "redirect:/members/" + saved.getId();
    }

    @GetMapping("/members/{id}")
    public String showMember(@PathVariable("id") Long id, Model model) {
        log.info("Show Member {}", id);
        
        //1. {id}값을 DB에서 가져오기
        Member memberEntity = memberRepository.findById(id).orElse(null);
        log.info("memberEntity : {}", memberEntity);
        
        //2. Entity -> DTO 변환
        //생략

        //3. view 전달
        model.addAttribute("member", memberEntity);

        return "members/showMember";
    }

    @GetMapping("/members")
    public String index(Model model) {

        //1.모든 데이터 가져오기
        List<Member> memberEntityList = memberRepository.findAll();
        memberEntityList.forEach(List -> {log.info("List: {}", List);});
        //2. 모델에 데이터 등록하기
        model.addAttribute("memberList", memberEntityList);
        //3. 뷰 페이지 설정하기
        return "members/index";
    }
}
