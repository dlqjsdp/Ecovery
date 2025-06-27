package com.example.shop.service;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.dto.ItemImgDto;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.entity.Item;
import com.example.shop.entity.ItemImg;
import com.example.shop.repository.ItemImgRepository;
import com.example.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//상품 등록 + 이미지 저장까지 담당하는 클래스 - 사용자가 상품 등록 폼을 통해 입력한 정보와 파일을 받아 상품, 이미지 객체로 각각 저장하고 DB와 서버 디스크에 모두 저장

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository; //상품 데이터 저장
    private final ItemImgRepository itemImgRepository; //이미지 파일 저장
    private final ItemImgService itemImgService; //드라이브 저장

    //itemFormDto : 상품명, 가격, 재고 등 사용자 입력값이 담긴 DTO, itemImgFileList : 업로드 된 이미지 파일 리스트(여러장을 올리니까 리스트로 담음)
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 등록
        Item item = itemFormDto.createItem(); //createItem : DTO를 엔티티로 변환하여
        itemRepository.save(item); //변환한 엔티티 저장(상품명, 가격, 재고, 상세설명이 저장)

        //이미지 등록
        for(MultipartFile multipartFile : itemImgFileList) {
            ItemImg itemImg = new ItemImg(); //빈 이미지 객체 생성
            itemImg.setItem(item); //상품관 연결(연관 관계 설정)

            if(itemImgFileList.get(0).equals(multipartFile)){
                itemImg.setRepimgYn("Y"); //첫 번째 파일은 대표 이미지로 지정(대표이미지는 Y로 지정)
            }else {
                itemImg.setRepimgYn("N"); //나머지는 일반 이미지
            }
            itemImgService.saveItemImg(itemImg, multipartFile); //이미지 저장 실행
        }

        return item.getId(); //DB에 저장되면 자동 생성된 상품 id 반환(컨트롤러가 이 ID를 이용해서 상세페이지로 이동)
    }
    
    //상품 수정
    /*
        Form 화면에 ItemFormDto에 있는 내용을 화면상 보여주고, 그 내용을 수정하기 위한 것이다.
        ItemFromDto에는 item에 있는 상품 내용과 item_id해당하는
        ItemImg 이미지를 조회해서 ItemFormDto 담아서 itemForm.html에 전달해야 한다.
    */
    /*
        JPA가 더티채킹(변경감지)를 수행하지 않는다.
        즉, itemId 조회해서 조회한 결과를 영속계층에 저장하고
        그 조회한 데이터를 변경해도 update(save) 실제 DB에 반영하지 않겠다.
    */
    @Transactional(readOnly = true) //JPA가 더티체킹(변경감지)을 수행하지 않는다(읽기전용) - 기존 상품 정보를 화면에 보여주기 위한 메서드
    public ItemFormDto getItemDtl(Long itemId) throws EntityNotFoundException {

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId); //해당 상품에 연결된 상품 이미지들을 ID 오름차순으로 조회
        List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //ItemImgDto에 담아(화면에 보여주기 위해 여기에 담는다) //화면에 넘길 DTO 리스트, 엔티티 그대로 넘기지 않고 화면에서 쓸 수 있도록 ItemImgDto 반환

        //ItemImg -> ItemImgDto 변환해서 List 저장
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);

            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId) //상품 엔티티를 조회해서
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터가 존재하지 않습니다.")); //없으면 예외 발생

        ItemFormDto itemFormDto = ItemFormDto.of(item); //상품 엔티티를 ItemFormDto로 변환
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto; //컨트롤러로 itemFormDto 반환(화면에서 폼에 기존 값 채워줄 수 있음)
    }

    //상품 수정 후 업데이트
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId()) //수정할 상품 DB에서 조회
                .orElseThrow(() -> new EntityNotFoundException()); //없으면 예외 발생
        //상품 데이터 수정
        item.updateItem(itemFormDto); //기존 상품 엔티티의 필드를 ItemFormDto 값으로 업데이트 // updateItem : 해당 메서드를 통해 한 번에 업데이트하는 구조
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); //수정할 이미지들의 ID 리스트 (대표 이미지 포함)

        for(int i=0; i<itemImgIds.size(); i++){ //이미지 개수 만큼 해당 작업 반복
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId(); //수정 완료된 상품 ID 리턴 → 컨트롤러에서 리다이렉트 등에 사용 가능
    }

    //상품 조회 조건과 페이지 정보를 파라미터로 받아 상품을 조회
    @Transactional(readOnly = true) //데이터 수정이 일어나지 않아 최적화를 위해 해당 어노테이션 설정(수정하지 말것)
    //검색조건, 페이지 정보(몇 번째 페이지, 한 페이지 당 몇개, 정렬 방식 등을 담은 정보)를 페이지 형태로 넘겨줌
    public Page<Item> getAdminItemPage(ItemSearchDto searchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(searchDto, pageable); //실제 데이터 조회는 레파지토리에서 진행함으로 서비스 계층에서 메서드를 호출하여 결과를 받아 리턴함
    }

    //상품 데이터 조회
    @Transactional(readOnly = true) //데이터 수정이 일어나지 않아 최적화를 위해 해당 어노테이션 설정(수정하지 말것)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) { //검색조건과 페이징 정보를 받아서
        return itemRepository.getMainItemPage(itemSearchDto, pageable); //실제 상품 데이터를 가져오는 메서드를 호출하고 그 결과를 그대로 반환
    }

}
