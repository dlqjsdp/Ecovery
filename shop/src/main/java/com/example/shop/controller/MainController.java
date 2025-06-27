package com.example.shop.controller;

//메인화면 컨트롤러(회원가입 후 메인 페이지로 갈 수 있도록 작성)

import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6); //첫번째 페이지, 6개 항목 씩 노출(page.get() : 1 일경우 화면성 2번째 페이지 노출)

    //    log.info("MainController: pageable: {}", pageable.getOffset());
    //    log.info("MainController: pageable: {}", pageable.getPageSize());

        Page<MainItemDto> items =
                itemService.getMainItemPage(itemSearchDto, pageable);

    //    log.info("MainController: items numbers: {}", items.getNumber());

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "main";
    }
}
