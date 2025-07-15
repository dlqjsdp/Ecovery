package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.ItemService;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/*
 * 에코마켓 상품 Controller
 * @author : sehui
 * @fileName : ItemController
 * @since : 250711
 * @history
 *  - 250711 | sehui | 상품 상세 페이지 요청 메소드 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청 메소드 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청에 단일 조건 검색 기능 추가
 */

@Controller
    @RequiredArgsConstructor
    @RequestMapping("/item")
    @Slf4j
    public class ItemController {

    private final ItemService itemService;
    private final MemberService memberService;

    //상품 목록 페이지 요청
    @GetMapping("/list")
    public String itemList(@RequestParam(required = false) String itemNm,
                           @RequestParam(required = false) String category,
                           Criteria cri, Model model) {
        log.info("Ecomarket List >>>>>>>");

        //전체 상품 조회
        List<ItemListDto> itemList = itemService.getItemList(itemNm, category, cri);

        //전체 상품의 개수
        int totalCount = itemService.getTotalCount(itemNm, category);

        model.addAttribute("itemList", itemList);
        model.addAttribute("pageMaker", new PageDto(cri, totalCount));
        model.addAttribute("itemNm", itemNm);
        model.addAttribute("category", category);

        return "item/itemList";
    }

    //상품 상세 페이지 요청
    @GetMapping("/{itemId}")
    public String itemDtl(@PathVariable Long itemId, Principal principal, Model model) {

        //관리자 전용 버튼을 위해 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        //상품 단 건 조회
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

        model.addAttribute("item", itemFormDto);
        model.addAttribute("role", role);

        return "item/itemDtl";
    }
}
