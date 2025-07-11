package com.ecovery.controller;

import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ecovery.domain.MemberVO;

/**
 * 회원가입, 로그인를 위한 Controller
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * 작성자 : 방희경
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    //회원가입 페이지 이동
    @GetMapping(value = "/signup")
    public String signupForm(Model model) {
        log.info("signupForm : 회원가입...");
        model.addAttribute("member", new MemberVO());
        return "member/signup";
    }
    //회원가입 처리
    @PostMapping(value = "/signup")
    public String signup(@ModelAttribute MemberVO memberVO, BindingResult bindingResult, Model model) {

        //1. 필드 검증 오류 시 다시 회원가입폼으로 이동
        if (bindingResult.hasErrors()) {
            return "member/signup";
        }
        //2. 이메일 중복 체크 및 정상이라면 로그인 페이지 이동
        if (memberService.getMemberByEmail(memberVO.getEmail()) != null){
            model.addAttribute("errorMessage", "이미 사용 중인 이메일입니다.");
            return "member/signup";
        } else {
            memberService.registerMember(memberVO);
            return "redirect:/member/login";
        }
    }

    // 이메일 중복 확인 (AJAX 요청 처리용)
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email) {

        return memberService.getMemberByEmail(email) != null; // true = 중복
    }
//
//    //로그인
//    @GetMapping(value = "/login")
//    public String loginForm(){
//        return "member/login";
//    }

}
