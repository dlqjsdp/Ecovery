package com.example.shop.controller;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.entity.Member;
import com.example.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    //로그인 시
    @GetMapping(value = "/login")
    public String loginMember() {
        return "member/memberLoginForm";
    }
    //로그인 실패 시
    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }

    //회원가입 페이지(회원가입 폼 제공)
    @GetMapping(value = "/new")
    public String MemberForm(Model model) { //model : 뷰에 데이터를 넘겨주는 객체, 클라이언트에게 보여줄 화면에 보낼 데이터 저장소
        //memberFormDto라는 이름의 빈 dto 객체를 담고 있음(new) - 타임리프 같은 템플릿 엔진에서 입력 폼과 연결 시키기 위해
        model.addAttribute("memberFormDto", new MemberFormDto()); //화면에 넘겨줄 데이터를 모델에 담는다
        return "member/memberForm";
    }

    //회원가입 컨트롤러 작성
    @PostMapping(value = "/new") //회원가입 폼을 제출할 때 호출되는 post 요청
    //@Valid : 유효성 검사를 수행할 대상 객체(DTO) + 검증하려는 객체 앞에 선언, BindingResult : 유효성 검사 결과를 담는 객체 (에러가 있으면 true 반환)
    // 검사 후 결과는 bindingResult에 담는다
    public String MemberForm(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) { //요구사항데로 입력되지 않으면 실행되는 if문, 유효성 검사 에러가 있으면 회원가입 페이지로 이동
            return "member/memberForm";
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder); //dto 데이터를 바탕으로 Member 엔티티 생성 + 비밀번호는 암호화
            memberService.saveMember(member); //회원 생성 및 저장

        } catch (IllegalArgumentException e) { //중복된 데이터가 등록될 경우
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/"; //회원가입 성공 시 메인 페이지로 리다이렉트
    }

}
