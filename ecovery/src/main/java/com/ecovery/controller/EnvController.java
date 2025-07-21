package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.PageDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.EnvService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/*
 * 환경톡톡 게시글 컨트롤러
 * 게시글 등록, 조회, 수정, 삭제, 목록 조회(페이징) 기능의 컨트롤러 역할을 수행
 * 클라이언트의 요청을 받아 서비스 계층(EnvService)과 상호작용하여 처리하고 View에 데이터를 전달
 * @author : yukyeong
 * @fileName : EnvController.java
 * @since : 250716
 * @history
     - 250716 | yukyeong | EnvController 클래스 최초 작성 (목록 조회)
     - 250717 | yukyeong | 게시글 등록 폼 이동, 등록 처리, 단건 조회 및 수정 화면, 수정 처리, 삭제 추가
     - 250718 | yukyeong | 게시글 목록 조회 (페이징 + 검색)
     - 250721 | yukyeong | 게시글 등록 폼 이동, 게시글 등록 처리, 게시글 단건 조회, 수정 버튼 노출 조건 처리, 게시글 수정 처리, 삭제 처리 추가
 */

@Controller
@RequestMapping("/env")
@RequiredArgsConstructor
@Slf4j
public class EnvController {

    private final EnvService envService;
    private final MemberService memberService;

    // 목록 조회
    @GetMapping("/list")
    public void list(Criteria cri, Model model) {
        log.info("게시글 목록 조회 : {}", cri); // cri에 담긴 조건(페이지 번호, 검색어 등) 로그 출력

        List<EnvDto> list = envService.getList(cri); // DTO 타입으로 목록 조회
        int total = envService.getTotal(cri); // 총 게시글 수 조회

        model.addAttribute("list", list); // 조회된 게시글 목록을 모델에 담아 View로 전달
        model.addAttribute("pageMaker", new PageDto(cri, total)); // 페이지 정보(PageDto)를 모델에 담아 View로 전달
    }

    // 게시글 등록 폼 이동
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/register")
    public void register() {
    }

    // 게시글 등록 처리
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/register")
    public String register(@Valid EnvDto envDto,
                           BindingResult bindingResult,
                           Principal principal,
                           RedirectAttributes rttr) {

        // 1. @Valid 유효성 검사 + BindingResult로 검증
        if (bindingResult.hasErrors()) {
            return "env/register"; // 유효성 검사 실패 시 다시 폼으로 이동 (글쓰기 폼 유지)
        }

        // 2. 유효성 검사 통과한 경우에만 로그인 사용자 이메일 → memberId 조회
        String email = principal.getName();
        Long memberId = memberService.getMemberByEmail(email).getMemberId();
        envDto.setMemberId(memberId);

        log.info("게시글 등록 처리 : {}", envDto);

        // 3. 게시글 등록 처리
        envService.register(envDto);

        // 4. 등록 결과 flash로 전달 → 목록 페이지로 리다이렉트
        rttr.addFlashAttribute("result", envDto.getEnvId());
        return "redirect:/env/list";
    }


    // 게시글 단건 조회, 수정 버튼 노출 조건 처리
    @GetMapping("/get")
    public void get(@RequestParam Long envId, Criteria cri, Model model) {
        log.info("게시글 단건 조회, 게시글 수정버튼 화면");

        // 1. 먼저 해당 게시글의 조회수를 1 증가시킴 (DB에 반영)
        envService.increaseViewCount(envId);
        // 2. 증가된 조회수를 포함한 최신 게시글 정보를 조회
        EnvDto envDto = envService.get(envId);

        // 3. 조회된 게시글 정보와 검색/페이징 조건을 View에 전달
        model.addAttribute("env", envDto);
        model.addAttribute("cri", cri);

    }


    // 게시글 수정 처리
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/modify")
    public String modify(@Valid EnvDto envDto,
                         BindingResult bindingResult,
                         @ModelAttribute("cri") Criteria cri,
                         RedirectAttributes rttr) {

        log.info("게시글 수정 요청: {}", envDto);

        // 1. 유효성 검사 실패 시 수정 폼으로 되돌림
        if (bindingResult.hasErrors()) {
            rttr.addFlashAttribute("error", "입력값을 다시 확인해주세요.");
            return "redirect:/env/modify?envId=" + envDto.getEnvId(); // 수정 폼으로 리다이렉트
        }

        // 2. 수정 로직 처리
        if (envService.modify(envDto)) {
            rttr.addFlashAttribute("result", "수정 성공했습니다.");
        }

        // 3. 페이징 및 검색 조건 유지
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("type", cri.getType());
        rttr.addAttribute("keyword", cri.getKeyword());

        return "redirect:/env/list";

    }

    // 게시글 삭제 처리
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/remove")
    public String remove(@RequestParam Long envId, // 삭제 대상 게시글의 ID
                         @ModelAttribute("cri") Criteria cri, // 현재 페이징 및 검색 상태를 유지하기 위한 정보
                         RedirectAttributes rttr) {
        log.info("게시글 삭제 요청: {}", envId);

        // 1. 삭제 시도
        if (envService.remove(envId)) {
            rttr.addFlashAttribute("result", "삭제 성공했습니다.");
        } else {
            rttr.addFlashAttribute("error", "삭제에 실패했습니다.");
        }

        // 2. 페이징/검색 조건 유지
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("type", cri.getType());
        rttr.addAttribute("keyword", cri.getKeyword());

        return "redirect:/env/list";

    }

}