package com.ecovery.controller;

import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    // 1. 전체 회원 목록 조회

}
