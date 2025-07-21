package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.domain.PointVO;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 관리자 페이지 > 회원 관리 컨트롤러
 * - 전체 회원 목록 조회
 * - 회원 상세 정보 조회 (옵션)
 * - 회원 정보 수정
 * 작성자: 방희경
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin/member")
@Slf4j
public class AdminController {

    private final MemberService memberService;

    // 전체 회원 목록 조회
    @GetMapping
    public String getAllMembers(Model model) {
        List<MemberVO> memberList = memberService.getAllMembers();
        model.addAttribute("memberList", memberList);
        return "admin/member/memberList";
    }

    // 회원 상세 정보 조회(이메일, 닉네임, 포인트 변동 내역)
    @GetMapping(value = "/{memberId}")
    public String getMemberDetail(@PathVariable("memberId") Long memberId, Model model) {
        MemberVO memberVO = memberService.getMemberById(memberId);
        List<PointVO> pointHistoryList = memberService.getPointHistoryMemberById(memberId);

        model.addAttribute("memberVO", memberVO);
        model.addAttribute("pointHistoryList", pointHistoryList);

        return "admin/member/memberDetail";
    }

}
