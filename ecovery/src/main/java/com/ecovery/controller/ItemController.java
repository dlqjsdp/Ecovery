package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.service.ItemService;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

/*
 * 에코마켓 상품 Controller
 * @author : sehui
 * @fileName : ItemController
 * @since : 250711
 * @history
 *  - 250711 | sehui | 상품 상세 페이지 요청 메소드 추가
 */

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final MemberService memberService;

    //상품 상세 페이지 요청
    @GetMapping("/item/{itemId}")
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
