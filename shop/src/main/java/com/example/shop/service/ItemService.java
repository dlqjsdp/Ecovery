package com.example.shop.service;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.dto.ItemImgDto;
import com.example.shop.entity.Item;
import com.example.shop.entity.ItemImg;
import com.example.shop.repository.ItemImgRepository;
import com.example.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //ItemImgDto에 담아(화면에 보여주기 위해 여기에 담는다)

        //ItemImg -> ItemImgDto 변환해서 List 저장
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);

            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터가 존재하지 않습니다."));

        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    //상품 수정 후 업데이트
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException());
        //상품 데이터 수정
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i=0; i<itemImgIds.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();

    }
}
