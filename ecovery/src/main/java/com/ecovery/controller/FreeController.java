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

@Controller
@RequestMapping("/free")
@RequiredArgsConstructor
@Slf4j
public class FreeController {

    private final FreeService freeService;
    private final FreeImgService freeImgService;
    private final MemberService memberService;

    // 목록 페이지
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        List<FreeDto> list = freeService.getAll(cri);
        int total = freeService.getTotalCount(cri);
        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDto(cri, total));
        return "free/list"; // → templates/free/list.html
    }

    // 상세보기 페이지
    @GetMapping("/get/{freeId}")
    public String get(@PathVariable Long freeId, Model model) {
        FreeDto dto = freeService.get(freeId);
        List<FreeImgDto> imgList = freeImgService.getAll(freeId);
        model.addAttribute("free", dto);
        model.addAttribute("imgList", imgList);
        return "free/get"; // → templates/free/detail.html
    }

    // 등록 폼 페이지
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("free", new FreeVO());
        return "free/register"; // → templates/free/register.html
    }

    // 게시글 등록 처리
    @PostMapping("/register")
    public String register(FreeVO vo, Principal principal, RedirectAttributes rttr) {
        if (principal == null) {
            return "redirect:/member/login"; // 로그인 안 돼있으면 로그인 페이지로
        }

        String email = principal.getName();
        Long memberId = memberService.getMemberByEmail(email).getMemberId();
        vo.setMemberId(memberId);

        freeService.register(vo);
        rttr.addFlashAttribute("msg", "게시글이 등록되었습니다.");
        return "redirect:/free/list";
    }

    // 수정 폼 페이지
    @GetMapping("/modify/{freeId}")
    public String modifyForm(@PathVariable Long freeId, Principal principal, Model model, RedirectAttributes rttr) {
        if (principal == null) {
            return "redirect:/member/login";
        }

        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        FreeDto dto = freeService.get(freeId);

        if (!currentUserId.equals(dto.getMemberId())) {
            rttr.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/free/list";
        }

        model.addAttribute("free", dto);
        return "free/modify"; // → templates/free/modify.html
    }

    // 게시글 수정 처리
    @PostMapping("/modify/{freeId}")
    public String modify(@PathVariable Long freeId, FreeVO vo, Principal principal, RedirectAttributes rttr) {
        String email = principal.getName();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();

        vo.setFreeId(freeId);
        vo.setMemberId(currentUserId);

        boolean result = freeService.modify(vo);
        if (result) {
            rttr.addFlashAttribute("msg", "게시글이 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("error", "수정에 실패했습니다.");
        }

        return "redirect:/free/detail/" + freeId;
    }

    // 게시글 삭제 처리
    @PostMapping("/remove/{freeId}")
    public String remove(@PathVariable Long freeId, Principal principal, RedirectAttributes rttr) {
        if (principal == null) {
            return "redirect:/member/login";
        }

        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();
        Long currentUserId = memberService.getMemberByEmail(email).getMemberId();
        FreeDto dto = freeService.get(freeId);

        if (!currentUserId.equals(dto.getMemberId()) && role != Role.ADMIN) {
            rttr.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/free/get/" + freeId;
        }

        FreeVO vo = new FreeVO();
        vo.setFreeId(dto.getFreeId());
        vo.setMemberId(currentUserId);

        boolean result = freeService.remove(vo);
        if (result) {
            rttr.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
            return "redirect:/free/list";
        } else {
            rttr.addFlashAttribute("error", "삭제에 실패했습니다.");
            return "redirect:/free/get/" + freeId;
        }
    }
}
