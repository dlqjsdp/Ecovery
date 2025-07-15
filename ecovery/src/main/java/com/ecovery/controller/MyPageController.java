package com.ecovery.controller;


import com.ecovery.domain.MemberVO;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 마이페이지 비밀번호 수정 전용 컨트롤러
 * - 이메일, 닉네임은 수정 불가
 * - 비밀번호만 수정
 * 작성자 : 방희경
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/mypage")
@Slf4j
public class MyPageController {

    private final MemberService memberService;

    // 마이페이지 정보 수정
    @GetMapping(value = "/edit")
    public String showEditPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        Long memberId = user.getMemberVO().getMemberId();
        MemberVO member = memberService.getMemberById(memberId);

        model.addAttribute("member", member); //닉네임, 이메일은 읽기 전용으로 보여주기

        return "mypage/edit";
    }

    // 마이페이지 정보 수정 처리
    @PostMapping(value = "/update")
    // 비밀번호만 수정 가능
    public String update(@AuthenticationPrincipal CustomUserDetails user, @RequestParam("password") String password) {

        MemberVO member = new MemberVO();

        member.setMemberId(user.getMemberVO().getMemberId());
        member.setPassword(password); // 비밀번호 전달

        memberService.updateMember(member);
        return "redirect:/mypage";
    }

}
