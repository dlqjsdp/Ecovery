package com.ecovery.controller;

import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.FreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/free")
@RequiredArgsConstructor
@Slf4j
public class FreeController {

    private final FreeService freeService;

    @GetMapping("/register")
    public void register() {
    }

    // 게시글 등록
    @PostMapping("/register")
    public String register(FreeVO vo, RedirectAttributes rttr){
        freeService.register(vo);
        rttr.addFlashAttribute("msg", "게시글 등록!");
        return "redirect:/free/list";
    }

    // 게시글 목록
    @GetMapping("/list")
    public void list(Criteria cri, Model model){
        log.info("list..........");

        List<FreeDto> list = freeService.getAll(cri);
        model.addAttribute("list", list);

        int total = freeService.getTotalCount(cri);

        model.addAttribute("pageMaker", new PageDto(cri, total));

        log.info("전체 게시글 수 : " + total);
    }


}
