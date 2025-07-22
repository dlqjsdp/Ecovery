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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/*
 * 무료나눔 게시글 컨트롤러
 * - 게시글 목록, 상세조회, 등록, 수정, 삭제 등 CRUD 기능 제공
 * - HTML 기반 화면(View) 반환
 * - Thymeleaf 템플릿 엔진과 연동
 *
 * [기능별 접근 제어]
 * - 목록 조회 (/free/list): 누구나 접근 가능 (비회원 포함)
 * - 상세 보기 (/free/get/{id}): 누구나 접근 가능
 * - 등록 (/free/register): 로그인한 사용자만 접근 가능
 * - 수정 (/free/modify/{id}): 해당 게시글 작성자만 접근 가능
 * - 삭제 (/free/remove/{id}): 작성자 본인 또는 관리자만 접근 가능
 *
 * [로그 처리 기준]
 * - 로그인 여부, 권한 체크, 수정/삭제 실패 등 주요 흐름에만 로그 출력
 * - 정상적인 단순 조회 등은 로그 생략
 *
 * @author : yeonsu
 * @fileName : FreeController
 * @since : 2025-07-21
 */


@Controller
@RequestMapping("/free")
@RequiredArgsConstructor
@Slf4j
public class FreeController {

    private final FreeService freeService;
    private final FreeImgService freeImgService;
    private final MemberService memberService;

    // 게시글 목록 조회 (누구나 가능) - 게시글 목록과 페이징 정보를 모델에 담아 리스트 뷰로 전달
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {

        List<FreeDto> list = freeService.getAll(cri);
        int total = freeService.getTotalCount(cri); // 전체 게시글 수 조회 (페이징용

        // 모델에 게시글 목록, 페이징 정보 전달
        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDto(cri, total));

        return "free/list";
    }

    // 상세보기 페이지 (누구나 가능) - 게시글 정보 + 이미지 목록을 조회하여 뷰로 전달
    @GetMapping("/get/{freeId}")
    public String get(@PathVariable Long freeId, Model model) {

        // 게시글 ID로 상세 정보 조회
        FreeDto dto = freeService.get(freeId);

        // 해당 게시글의 이미지 목록 조회
        List<FreeImgDto> imgList = freeImgService.getAll(freeId);

        // 모델에 게시글 정보와 이미지 리스트 전달
        model.addAttribute("free", dto);
        model.addAttribute("imgList", imgList);
        return "free/get";
    }

    // 등록 폼 페이지 (로그인한 회원만 가능) - FreeVO 객체를 생성해 뷰에 전달
    @GetMapping("/register")
    public String registerForm(Principal principal, Model model) {
        // 로그인 안되어 있으면 로그인 페이지로 이동
        if (principal == null) {
            return "redirect:/member/login";
        }
        // 빈 FreeVO 객체 생성 후 모델에 추가
        model.addAttribute("free", new FreeVO());
        return "free/register";
    }

    // 게시글 등록 처리 (로그인한 회원만 가능) - 작성자의 memberId를 설정 후 등록 처리
    @PostMapping("/register")
    public String register(FreeVO vo, Principal principal, RedirectAttributes rttr) {
        // 비로그인 사용자는 로그인 페이지로 이동
        if (principal == null) {
            return "redirect:/member/login";
        }
        // 로그인 사용자 이메일로 memberID 조회
        String email = principal.getName();
        Long memberId = memberService.getMemberByEmail(email).getMemberId();
        // VO에 memberId 세팅
        vo.setMemberId(memberId);
        // 게시글 등록 처리
        freeService.register(vo);

        rttr.addFlashAttribute("msg", "게시글이 등록되었습니다.");
        return "redirect:/free/list";
    }

    // 수정 폼 페이지 (작성자 본인만 가능)
    @GetMapping("/modify/{freeId}")
    public String modifyForm(@PathVariable Long freeId, Principal principal,
                             Model model, RedirectAttributes rttr) {
        // 로그인 확인
        if (principal == null) {
            return "redirect:/member/login";
        }
        // 로그인된 사용자 ID 조회
        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        // 게시글 정보 조회
        FreeDto dto = freeService.get(freeId);

        // 작성자가 아닌 경우 수정 권한 업음
        if (!currentUserId.equals(dto.getMemberId())) {
            rttr.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/free/get/" + freeId;
        }
        // 모델에 게시글 정보 전달 후 수정 폼 뷰로 이동
        model.addAttribute("free", dto);
        return "free/modify";
    }

    // 게시글 수정 처리 (작성자 본인만 가능)
    @PostMapping("/modify/{freeId}")
    public String modify(@PathVariable Long freeId, FreeVO vo,
                         Principal principal, RedirectAttributes rttr) {
        // 로그인 확인
        if (principal == null) {
            return "redirect:/member/login";
        }
        // 로그인한 사용자 ID
        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        // 기존 게시글 정보 조회
        FreeDto dto = freeService.get(freeId);

        // 작성자 본인만 수정 가능
        if (!currentUserId.equals(dto.getMemberId())) {
            rttr.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/free/get/" + freeId;
        }
        // 수정 대상 vo에 필수 정보 세팅
        vo.setFreeId(freeId);
        vo.setMemberId(currentUserId);

        // 수정처리
        boolean result = freeService.modify(vo);
        // 결과에 따라 메시지 처리
        if (result) {
            rttr.addFlashAttribute("msg", "게시글이 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("error", "수정에 실패했습니다.");
        }

        return "redirect:/free/get/" + freeId;
    }

    // 게시글 삭제 처리 (작성자 본인 or 관리자만 가능)
    @PostMapping("/remove/{freeId}")
    public String remove(@PathVariable Long freeId, Principal principal,
                         RedirectAttributes rttr) {
        // 로그인 여부 확인
        if (principal == null) {
            return "redirect:/member/login";
        }
        // 로그인된 사용자 정보 가져오기
        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        Role role = memberService.getMemberByEmail(email).getRole();

        // 삭제할 게시글 정보 조회
        FreeDto dto = freeService.get(freeId);
        Long postOwnerId = dto.getMemberId();

        // 권한 확인: 작성자 또는 관리자
        if (!currentUserId.equals(postOwnerId) && role != Role.ADMIN) {
            rttr.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/free/get/" + freeId;
        }
        // 삭제 요청용 vo 생성
        FreeVO vo = new FreeVO();
        vo.setFreeId(freeId);
        vo.setMemberId(currentUserId);

        // 삭제 처리
        boolean result = freeService.remove(vo);
        // 결과에 따라 메시지 처리
        if (result) {
            rttr.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
            return "redirect:/free/list";
        } else {
            rttr.addFlashAttribute("error", "삭제에 실패했습니다.");
            return "redirect:/free/get/" + freeId;
        }
    }
}
