package com.ecovery.controller;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.EnvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
 */

@Controller
@RequestMapping("/env")
@RequiredArgsConstructor
@Slf4j
public class EnvController {

    private final EnvService envService;

    // 목록 조회
    @GetMapping("/list")
    public void list(Criteria cri, Model model) {
        log.info("게시글 목록 조회 : {}", cri); // cri에 담긴 조건(페이지 번호, 검색어 등) 로그 출력

        List<EnvDto> list = envService.getList(cri); // DTO 타입으로 목록 조회
        int total = envService.getTotal(cri); // 총 게시글 수 조회

        model.addAttribute("list", list); // 조회된 게시글 목록을 모델에 담아 View로 전달
        model.addAttribute("pageMaker", new PageDto(cri, total)); // 페이지 정보(PageDto)를 모델에 담아 View로 전달
    }

//    // 게시글 등록 폼 이동
//    @GetMapping("/register")
//    public void register() {
//    }
//
//    // 게시글 등록 처리
//    @PostMapping("/register")
//    public String register(@Valid EnvDto envDto,
//                           BindingResult bindingResult,
//                           RedirectAttributes rttr) {
//        if (bindingResult.hasErrors()) {
//            return "redirect:/env/register";
//        }
//
//        log.info("게시글 등록 처리");
//        envService.register(dtoToVo(envDto));
//        rttr.addFlashAttribute("result", envDto.getEnvId());
//
//        return "redirect:/env/list";
//    }
//
//    // 게시글 단건 조회 및 수정 화면
//    @GetMapping({"/get", "/modify"})
//    public void get(@RequestParam Long envId, Criteria cri, Model model) {
//        log.info("게시글 단건 조회/수정 화면");
//
//        envService.increaseViewCount(envId);
//        model.addAttribute("env", envService.get(envId));
//        model.addAttribute("cri", cri);
//    }
//
//    // 게시글 수정 처리
//    @PostMapping("/modify")
//    public String modify(EnvVO env, @ModelAttribute("cri") Criteria cri,
//                         RedirectAttributes rttr) {
//        log.info("게시글 수정 화면");
//
//        if(envService.modify(env)){
//            rttr.addFlashAttribute("result", "수정 성공했습니다.");
//        }
//
//        rttr.addAttribute("pageNum", cri.getPageNum());
//        rttr.addAttribute("amount", cri.getAmount());
//        rttr.addAttribute("type", cri.getType());
//        rttr.addAttribute("keyword", cri.getKeyword());
//
//        return "redirect:/env/list";
//
//    }
//
//    // 게시글 삭제 처리
//    @PostMapping("/remove")
//    public String remove(@RequestParam Long envId,
//                         @ModelAttribute("cri") Criteria cri,
//                         RedirectAttributes rttr) {
//        log.info("게시글 삭제 : {}", envId);
//
//        if(envService.remove(envId)) {
//            rttr.addFlashAttribute("result", "삭제 성공했습니다.");
//        }
//
//        rttr.addAttribute("pageNum", cri.getPageNum());
//        rttr.addAttribute("amount", cri.getAmount());
//        rttr.addAttribute("type", cri.getType());
//        rttr.addAttribute("keyword", cri.getKeyword());
//
//        return "redirect:/env/list";
//
//    }

}
