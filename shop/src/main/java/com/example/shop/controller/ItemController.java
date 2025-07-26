package com.example.shop.controller;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.entity.Item;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

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
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId); //서비스에서 해당 상품 ID에 대한 정보를 DTO로 받아옴
            model.addAttribute("itemFormDto", itemFormDto); //성공하면 화면에 담아서 출력
        } catch (EntityNotFoundException e) { //해당 ID 상품이 DB에 없으면
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다."); //예외 발생
            model.addAttribute("itemFormDto", new ItemFormDto()); //에러 메세지 보여주고 빈 DTO 전달(화면 깨지지 않게)
        }
        return "item/itemForm"; //상품 수정 화면 이동
    }
    //상품 수정
    @PostMapping(value = "/admin/item/{itemId}")
    //@Valid를 통해 DTO에 있는 유효성 검사(@NotBlank 등) 실행, BindingResult는 유효성 검사 결과가 담김
    //itemImgFileList: 업로드된 이미지 파일 리스트, Model: 에러 메시지나 데이터 다시 화면에 넘길 때 사용
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){

        if(bindingResult.hasErrors()){ //유효성 검사 실패 시 다시 폼 화면으로 돌아감
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){ //상품 등록 시 첫 번째 이미지를 반드시 넣어야함
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "/item/itemForm";
        }
        
        try {
            itemService.updateItem(itemFormDto, itemImgFileList); //서비스로 DTO와 이미지 파일을 넘겨서 상품 + 이미지 정보 수정 진행
        } catch (Exception e){ //오류 발생 시 사용자에게 화면을 보여주고
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생되었습니다.");
            return "/item/itemForm"; //폼 화면으로 이동
        }
        return "redirect:/"; //수정 성공하면 홈화면으로 리다이렉트
    }

    //상품 관리 화면 이동 및 조회한 상품 데이터를 화면에 전달
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    //상품 검색 조건과 페이지 번호를 파라미터로 받고 화면으로 데이터를 넘기기 위해 Model로 함께 받는다
    public String itemManage(ItemSearchDto itemSearchDto, //상품명, 상태 + URL 페이지 번호 + 화면으로 넘길 데이터를 담아
                             @PathVariable("page") Optional<Integer> page, Model model) {

        //전달 받은 page 값이 없으면 1페이지에 글 3 개 보여달라
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable); //검색 조건과 페이지 정보를 기반으로 상품 목록 조회해서 Page<Item> 형태로 가져옴
        model.addAttribute("items", items); //조회한 상품 목록 화면에 넘김
        model.addAttribute("itemSearchDto", itemSearchDto); //검색 조건이 유지 되도록
        model.addAttribute("maxPage", 5); //한 번에 보여줄 최대 페이지 수는 5로 지정하여 페이징 버튼이 1~5까지만 보이도록 설정

        return "item/itemMng"; //화면으로 전달
    }

    //상품 상세페이지 이동
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl2(@PathVariable("itemId") Long itemId, Model model) { //경로에 포함된 itemID 값을 받아서 해당 상품 정보를 조회하고 Model에 담아 화면에 전달

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId); //서비스 계층의 getItemDtl(itemId) 메서드를 호출하여 상품 ID에 해당하는 상품 상세 정보 불어오기
 
        model.addAttribute("item", itemFormDto); //상품 정보를 item 이름으로 화면에 전달

        return "item/itemDtl"; //해당 경로로 데이터 넘겨줌
    }


}
