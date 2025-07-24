package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.FreeImgService;
import com.ecovery.service.FreeService;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/*
 * 무료나눔 게시글 View 컨트롤러
 * - 화면 이동(View 렌더링) 전용
 * - 목록, 상세, 등록, 수정 페이지 경로 처리
 *
 * [접근 권한]
 * - 목록/상세: 누구나 접근 가능
 * - 등록/수정: 로그인 사용자만 접근 (보안은 JS나 Security로 처리)
 *
 * [주의]
 * - 데이터 처리(X), 단순 페이지 이동만 담당
 * - CRUD 기능은 FreeRestController에서 처리
 *
 * @author : yeonsu
 * @fileName : FreeController
 * @since : 250722
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/free")
@Slf4j
public class FreeController {

    private final FreeService freeService;
    private final MemberService memberService;

    //무료나눔 목록 페이지
    @GetMapping("/list")
    public String list() {
        return "free/list"; // 템플릿으로 이동
    }
    //무료나눔 상세 페이지
    @GetMapping("/{freeId}")
    public String get(){
        return "free/get";
    }

    //무료나눔 등록 페이지 - 로그인 사용자만 가능
    @GetMapping("/register")
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    public String register(){
        return "free/register";
    }

    //무료나눔 수정 페이지 - 로그인 사용자만 가능
    @GetMapping("/modify/{freeId}")
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    public String modify(){
        return "free/modify";
    }
}
