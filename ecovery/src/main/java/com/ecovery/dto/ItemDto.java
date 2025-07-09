package com.ecovery.dto;

/*
 * 에코마켓 상품 상세 정보 화면 출력용
 * ItemVO에서 필요한 내용을 저장하여 보여줍니다.
 * 작성자 : 오세희
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String itemName;
    private int price;
    private int stockNumber;
    private String itemDetail;
    private String itemSellStatus;
}
