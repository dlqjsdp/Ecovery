package com.ecovery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 에코마켓 View Controller
 * @author : sehui
 * @fileName : ItemViewController
 * @since : 250721
 * @history
 *  - 250721 | sehui | View 반환을 위해 컨트롤러 생성
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemViewController {

    //상품 목록 페이지
    @GetMapping("/list")
    public String itemListPage(){

        return "item/list";
    }

    //상품 상세 페이지
    @GetMapping("/{itemId}")
    public String itemDtlPage(){
        return "item/itemDtl";
    }

    //상품 등록 페이지
    @GetMapping("/new")
    public String itemFormPage(){
        return "item/itemForm";
    }

    //상품 수정 페이지
    @GetMapping("/modify/{itemId}")
    public String itemModifyPage(){
        return null;
    }
}
