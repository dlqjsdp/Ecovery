package com.example.firstproject.controller;

import com.example.firstproject.dto.MemberForm;
import com.example.firstproject.entity.Member;
import com.example.firstproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller // 컨트롤러임을 나타냄
@RequiredArgsConstructor //생성자 자동 주입
@Slf4j //log.info 사용 가능
@RequestMapping("/members") // 기본 url 설정
public class MemberController {

//    @Autowired -> 이거 대신 @RequiredArgsConstructor 사용
 //   private MemberRepository memberRepository;

    private final MemberRepository memberRepository; // @RequiredArgsConstructor 덕분에 생성자 자동 주입, 해당 코드 Repository를 통해 DB에서 Member 데이터를 CRUD 함

    // 회원 전체 목록 페이지
    @GetMapping("")
    public String index(Model model) {
        // 1. 모든 데이터 가져오기
        List<Member> memberList = memberRepository.findAll();

        log.info("members : ", memberList);

        // 2. 모델에 데이터 등록하기
        model.addAttribute("memberList", memberList);

        // 3. 뷰 페이지 설정하기
        return "members/index";
    }
    
    // 신규 회원 입력 폼 화면
    @GetMapping("/new")
    public String newMember(Model model) {
        model.addAttribute("member", new Member());
        return "members/new";
    }

    // 회원 저장 처리
    @PostMapping("/create")
    public String createMember(MemberForm form, Model model) {
        // 1. MemberForm -> member entity 변환(회원 폼으로부터 전달받은 MemberForm을 Entity로 변환)
        Member member = form.toEntity();

        // 2. 리파지터리 엔티티로 DB에 저장
        Member savedMember = memberRepository.save(member); // article 엔티티를 저장해 saved 객체에 반환, saved : DB에 저장된 정보를 가지고 있음

        log.info("members : ", savedMember);

        // 3. 저장된 회원을 로그로 출력하고 목록페이지로 리다렉트
        return "redirect:/members"; // 경로 + id값을 가져오기 위해 saved 객체 이용
    }

    // 회원 상세 조회
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        log.info("Show article {}", id);

        // 1. 해당 ID의 회원을 DB에서 조회
        Member memberEntity = memberRepository.findById(id).orElse(null);

        log.info("memberEntity : {}", memberEntity);

        // 2. Entity -> DTO 반환
        // 생략

        // 3. View 전달
        model.addAttribute("member", memberEntity);
        return "members/show";
    }
}
