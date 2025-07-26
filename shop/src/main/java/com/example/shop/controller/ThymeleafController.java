package com.example.shop.controller;

import com.example.shop.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafController {

    @GetMapping(value = "/ex01")
    public String ThymeleafExample01(Model model) {
        model.addAttribute("data", "타임리프 예제입니다.");

        model.addAttribute("message", "<strong>굵은글씨</strong>");
        return "thymeleaf/thymeleafEx01";
    }

    // th:text를 이용한 상품 데이터 출력용 컨트롤러 - 단건
    @GetMapping(value = "/ex02")
    public String ThymeleafExample02(Model model) {

        ItemDto itemDto = ItemDto.builder() // itemDto 객체를 넘겨줌
                .itemDetail("상품 상세 설명")
                .itemNm("테스트 상품1")
                .price(12000)
                .regTime(LocalDateTime.now())
                .build();
        // itemDto에 넘길 내용을 담아서 thymeleaf/thymeleafEx02에 전달
        model.addAttribute("itemDto", itemDto);

        return "thymeleaf/thymeleafEx02";
    }

    // th:text를 이용한 상품 리스트 출력용 컨트롤러
    @GetMapping(value = "/ex03")
    public String ThymeleafExample03(Model model) {

        // 10건의 정보를 리스트에 담아서
        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i=1; i<=10; i++) {
            ItemDto itemDto = ItemDto.builder()
                    .itemDetail("상품 상세 설명"+i)
                    .itemNm("테스트 상품1"+i)
                    .price(12000*i)
                    .regTime(LocalDateTime.now())
                    .build();

            itemDtoList.add(itemDto); // itemDto : 10번씩 반복하면서 정보를 담고 있음
        }
        model.addAttribute("itemDtoList", itemDtoList);

        return "thymeleaf/thymeleafEx03";
    }

    // th:if, th:nuless를 이용한 조건문 처리용 컨트롤러
    @GetMapping(value = "/ex04")
    public String ThymeleafExample04(Model model) {

        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i=1; i<=10; i++) {
            ItemDto itemDto = ItemDto.builder()
                    .itemDetail("상품 상세 설명"+i)
                    .itemNm("테스트 상품1"+i)
                    .price(12000*i)
                    .regTime(LocalDateTime.now())
                    .build();

            itemDtoList.add(itemDto); // itemDto : 10번씩 반복하면서 정보를 담고 있음
        }
        model.addAttribute("itemDtoList", itemDtoList);

        return "thymeleaf/thymeleafEx04";
    }

    // th:href를 이용한 링크 처리용 컨트롤러
    @GetMapping(value = "/ex05")
    public String ThymeleafExample05() {

        return "thymeleaf/thymeleafEx05";
    }

    // th:href를 이용한 파라미터 데이터 전달용 컨트롤러
    @GetMapping(value = "/ex06")
    public String ThymeleafExample06(@RequestParam("param1") String p1,
                                     @RequestParam("param2") String p2,
                                     Model model) {

        model.addAttribute("param1", p1);
        model.addAttribute("param2", p2);

        return "thymeleaf/thymeleafEx06";

    }

    // thymeleaf 페이지 레이아웃 예제 컨트롤러
    @GetMapping(value = "/ex07")
    public String ThymeleafExample07() {

        return "thymeleaf/thymeleafEx07";
    }

    // thymeleaf 페이지 레이아웃 예제 컨트롤러
    @GetMapping(value = "/ex07_1")
    public String ThymeleafExample07_1() {

        return "thymeleaf/thymeleafEx07_1";
    }
}
