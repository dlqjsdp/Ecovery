package com.ecovery.controller;

import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
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
    public String signup(@ModelAttribute("member") MemberVO memberVO, Model model) {
        log.info("### signup POST 메서드 진입! ###");
        boolean hasError = false;

        // 1. 이메일 검사
        if (memberVO.getEmail() == null || memberVO.getEmail().trim().isEmpty()) {
            model.addAttribute("emailError", "이메일은 필수 입력 항목입니다.");
            hasError = true;
        } else if (!memberVO.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            model.addAttribute("emailError", "올바른 이메일 형식이어야 합니다.");
            hasError = true;
        } else if (memberService.getMemberByEmail(memberVO.getEmail()) != null) {
            model.addAttribute("emailError", "이미 사용 중인 이메일입니다.");
            hasError = true;
        }

        // 2. 비밀번호 검사
        String password = memberVO.getPassword();
        if (password == null || password.isEmpty()) {
            model.addAttribute("passwordError", "비밀번호는 필수 입력 항목입니다.");
            hasError = true;
            log.info("비밀번호 에러: 비어있음 - " + model.getAttribute("passwordError"));
        } else if (password.length() < 8 || password.length() > 20) {
            model.addAttribute("passwordError", "비밀번호는 8자 이상 20자 이하여야 합니다.");
            hasError = true;
            log.info("비밀번호 에러: 길이 문제 - " + model.getAttribute("passwordError"));
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,}$")) {
            model.addAttribute("passwordError", "비밀번호는 영문자와 숫자를 포함해야 하며 특수문자(!@#$%^&*)만 허용됩니다.");
            hasError = true;
            log.info("비밀번호 에러: 형식 문제 - " + model.getAttribute("passwordError"));
        } else {
            log.info("비밀번호 유효성 검사 통과: " + password);
        }

        // 3. 닉네임 검사
        if (memberVO.getNickname() == null || memberVO.getNickname().trim().isEmpty()) {
            model.addAttribute("nicknameError", "닉네임은 필수 입력 항목입니다.");
            hasError = true;
        } else if (memberService.getMemberByNickname(memberVO.getNickname()) != null){
            model.addAttribute("nicknameError", "이미 사용 중인 닉네임입니다..");
            hasError = true;
        }

        // 에러가 있다면 다시 회원가입 페이지로
        if (hasError) {
            log.info("회원가입 실패: 유효성 검사 오류 발생");
            return "member/signup";
        }

        // 모든 유효성 검사 통과 시
        log.info("회원가입 성공: 모든 유효성 검사 통과");
        memberService.registerMember(memberVO);
        return "redirect:/member/login";
    }

    // 이메일 중복 확인 (AJAX)
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email) {
        return memberService.getMemberByEmail(email) != null;
    }

    // 닉네임 중복 확인 (AJAX)
    @GetMapping("/check-nickname")
    @ResponseBody
    public boolean checkNickname(@RequestParam("nickname") String nickname) {
        log.info("닉네임 중복검사 요청: [" + nickname + "]");
        MemberVO member = memberService.getMemberByNickname(nickname);
        log.info("DB 조회 결과: {}", member != null ? member.getNickname() : "없음");
        return member != null;
    }

    // 로그인 페이지 이동
    @GetMapping(value = "/login")
    public String loginForm(){
        return "member/login";
    }
}