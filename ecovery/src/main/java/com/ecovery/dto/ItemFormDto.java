package com.ecovery.dto;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
@Builder
public class ItemFormDto {

    private Long id;                   //상품 코드

    private String itemNm;             //상품 가격

    private Integer price;             //상품 가격

    private int stockNumber;           //재고 수량

    private String itemDetail;         //상품 상세 설명

    private ItemSellStatus itemSellStatus;      //상품 판매 상태

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //상품 이미지 list

    private List<Long> itemImgId = new ArrayList<>();   //ItemImgVO 개별 이미지 수정 용도

    //ItemVO -> ItemFormDto 변환 생성자
    public static ItemFormDto of(ItemVO item){
        return ItemFormDto.builder()
                .id(item.getItemId())
                .itemNm(item.getItemName())
                .price(item.getPrice())
                .stockNumber(item.getStockNumber())
                .itemDetail(item.getItemDetail())
                .itemSellStatus(item.getItemSellStatus())
                .build();
    }
}
