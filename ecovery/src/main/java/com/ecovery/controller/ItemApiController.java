package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.CategoryService;
import com.ecovery.service.ItemService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 에코마켓 상품 Controller
 * @author : sehui
 * @fileName : ItemApiController
 * @since : 250711
 * @history
 *  - 250711 | sehui | 상품 상세 페이지 요청 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청에 단일 조건 검색 기능 추가
 *  - 250716 | sehui | 상품 등록 페이지 요청 추가
 *  - 250716 | sehui | 상품 등록 요청 추가
 *  - 250718 | sehui | 상품 수정 페이지 요청 추가
 *  - 250718 | sehui | 상품 수정 요청 추가
 *  - 250718 | sehui | REST API 방식으로 변경
 *  - 250718 | sehui | 상품 삭제 요청 추가
 *  - 250721 | sehui | 상품 등록, 수정, 삭제 요청에 관리자 권한 확인 기능 추가
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
@Slf4j
public class ItemApiController {

    private final ItemService itemService;
    private final MemberService memberService;
    private final CategoryService categoryService;

    //상품 목록 조회 요청
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> itemList(@RequestParam(required = false) String itemNm,
                                        @RequestParam(required = false) String category,
                                        Criteria cri) {
        log.info("Ecomarket List >>>>>>>");

        //전체 상품 조회
        List<ItemListDto> itemList = itemService.getItemList(itemNm, category, cri);

        //전체 상품의 개수
        int totalCount = itemService.getTotalCount(itemNm, category);

        //응답 객체
        Map<String, Object> response = new HashMap<>();
        response.put("itemList", itemList);
        response.put("pageMaker", new PageDto(cri, totalCount));
        response.put("itemNm", itemNm);
        response.put("category", category);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //상품 상세 페이지 요청
    @GetMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> itemDtl(@PathVariable Long itemId, Principal principal) {

        //관리자 전용 버튼을 위해 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        //상품 단건 조회
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

        //응답 객체
        Map<String, Object> response = new HashMap<>();
        response.put("item", itemFormDto);
        response.put("role", role);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //상품 등록 페이지 요청
    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> itemForm(Principal principal) {

        //관리자 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("itemFormDto", new ItemFormDto());         //기본 폼 객체
        response.put("categories", categoryService.findAllCategories());    //카테고리 목록

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //상품 등록 요청
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> saveItem(@Valid @RequestPart("itemFormDto") ItemFormDto itemFormDto,
                                                        BindingResult bindingResult,
                                                        @RequestPart("itemImgFile") List<MultipartFile> itemImgFileList,
                                                        Principal principal) {
        //관리자 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        //유효성 검사 확인
        if(bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //대표 이미지 확인
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getItemId() == null){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        //상품 등록
        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errorMessage", "상품 등록 중 에러가 발생하였습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    //상품 수정 페이지 요청
    @GetMapping("/modify/{itemId}")
    public ResponseEntity<Map<String, Object>> itemModify(@PathVariable Long itemId, Principal principal) {

        //관리자 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, Object> response = new HashMap<>();

        try{
            //상품 단건 조회
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            response.put("item", itemFormDto);
        }catch (Exception e){
            response.put("errorMessage", "존재하지 않는 상품입니다.");
            response.put("itemFormDto", new ItemFormDto());
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    //상품 수정 요청
    @PutMapping("/modify/{itemId}")
    public ResponseEntity<String> itemModify(@PathVariable Long itemId,
                                                          @Valid @RequestPart("itemFormDto") ItemFormDto itemFormDto,
                                                          BindingResult bindingResult,
                                                          @RequestPart("itemImgFile") List<MultipartFile> itemImgFileList,
                                                          Principal principal) {

        //관리자 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        //유효성 검사
        if(bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //대표 이미지 확인
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getItemId() == null){
            String errorMessage = "첫번째 상품 이미지는 필수 입력 값입니다.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        //상품 수정
        try{
            boolean isUpdated = itemService.updateItem(itemFormDto, itemImgFileList);

            if(!isUpdated){
                String errorMessage = "상품 수정에 실패했습니다.";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        }catch (Exception e){
            String errorMessage = "상품 수정 중 에러가 발생하였습니다.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

    //상품 삭제
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> itemRemove(@PathVariable Long itemId, Principal principal) {

        //관리자 권한 확인
        String email = principal.getName();
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try{
            boolean isDeleted = itemService.deleteItem(itemId);

            if(!isDeleted){
                String errorMessage = "상품 삭제에 실패했습니다.";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        }catch (Exception e){
            String errorMessage = "상품 삭제 중 에러가 발생하였습니다.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
