package com.example.shop.controller;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.entity.Item;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;

//관리자용 상품 등록 페이지 컨트롤러

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    //상품 등록 화면
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) { //itemForm를 model에 담아 뷰로 전달

        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }
    //상품 등록
    @PostMapping(value = "/admin/item/new")
    public String itemnew(@Valid ItemFormDto itemFormDto, //사용자 입력값을 DTO에 담고 유효성 검사 실행
                          BindingResult bindingResult, //유효성 검사 결과를 저장하는 객체
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, //file 타입으로 들어온 이미지 파일들
                          Model model){ //View 데이터로 전달(에러 메세지 등)

        //ItemFormDto 유효성 검사가 실패 했을 때 등록 화면으로 돌아가는 if(bindingResult 검사 결과 저장 객체)
        if(bindingResult.hasErrors()){
            return "/item/itemForm";
        }

        log.info("itemFormDto ===> {}", itemFormDto);

        //itemFormDto.getId() == null : ID는 DB 저장 시 자동으로 생성되어 어차피 null 값인데 안전하게 기재한듯
        //사용자가 대표이미지(첫번째 이미지)를 등록하지 않은경우를 체크하는 if문
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){ //상품 ID가 없는 경우 중에 대표 이미지가 없을 경우(수정은 제외)
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다."); //model에 담아 출력
            return "/item/itemForm"; //등록 페이지 이동
        }

        //이미지가 있을 경우 실행 - 상품 등록 에러 발생 시 사용자에게 안내 메세지를 주고 등록 폼으로 이동하는 try문
        try { //문제가 없으면 그대로 상품 등록
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){ //에러 발생 시 catch 블록 실행
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/"; //등록 후 상세페이지
    }

    //상품 수정 화면
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto); //성공하면 화면에 담아서 출력
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
        }
        return "item/itemForm";
    }
    //상품 수정
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){

        if(bindingResult.hasErrors()){
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "/item/itemForm";
        }
        
        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생되었습니다.");
            return "/item/itemForm";
        }
        return "redirect:/";
    }

}
