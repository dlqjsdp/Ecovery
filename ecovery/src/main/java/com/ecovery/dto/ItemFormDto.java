package com.ecovery.dto;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemVO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
 * 에코마켓 상품 정보 + 이미지 상세 페이지 및 상품 등록 DTO
 * @author : sehui
 * @fileName : ItemFormDto
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상세 페이지 화면 출력용 변수, 객체 변환 생성자 추가
 */

@Getter
@Setter
@ToString
public class ItemFormDto {

    private Long itemId;                   //상품 코드

    private String itemNm;             //상품명

    private Integer price;             //상품 가격

    private int stockNumber;           //재고 수량

    private String category;           //카테고리

    private String itemDetail;         //상품 상세 설명

    private ItemSellStatus itemSellStatus;      //상품 판매 상태

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //상품 이미지 list

    private List<Long> itemImgId = new ArrayList<>();   //ItemImgVO 개별 이미지 수정 용도

    //ItemVO -> ItemFormDto 변환 생성자
    public static ItemFormDto of(ItemVO item){
        ItemFormDto itemFormDto = new ItemFormDto();

        itemFormDto.setItemId(item.getItemId());
        itemFormDto.setItemNm(item.getItemName());
        itemFormDto.setPrice(item.getPrice());
        itemFormDto.setStockNumber(item.getStockNumber());
        itemFormDto.setCategory(item.getCategory());
        itemFormDto.setItemDetail(item.getItemDetail());
        itemFormDto.setItemSellStatus(item.getItemSellStatus());

        return itemFormDto;
    }
}
