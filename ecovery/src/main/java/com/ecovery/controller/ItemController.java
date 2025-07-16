package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.ItemService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

/*
 * 에코마켓 상품 Controller
 * @author : sehui
 * @fileName : ItemController
 * @since : 250711
 * @history
 *  - 250711 | sehui | 상품 상세 페이지 요청 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청에 단일 조건 검색 기능 추가
 *  - 250716 | sehui | 상품 등록 페이지 요청 추가
 *  - 250716 | sehui | 상품 등록 요청 추가
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
    public String itemDtl(@PathVariable Long itemId,Principal principal, Model model) {

        //관리자 전용 버튼을 위해 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        //상품 단건 조회
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

        model.addAttribute("item", itemFormDto);
        model.addAttribute("role", role);

        return "item/itemDtl";
    }

    //상품 등록 페이지 요청
    @GetMapping("/new")
    public String itemForm(Model model) {

        model.addAttribute("itemFormDto", new ItemFormDto());

        return "item/itemForm";
    }

    //상품 등록 요청
    @PostMapping("/new")
    public String itemNew(@Valid ItemFormDto itemFormDto,
                          BindingResult bindingResult,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          Model model) {

        //유효성 검사 확인
        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        log.info("itemFormDto ===> {}", itemFormDto);

        //대표 이미지 확인
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getItemId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");

            return "item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/item/list";
    }

}
