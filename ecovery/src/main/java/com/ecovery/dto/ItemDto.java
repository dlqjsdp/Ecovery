package com.ecovery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 에코마켓 상품 상세 정보 DTO
 * @author : sehui
 * @fileName : ItemDto
 * @since : 250709
 */

@Getter
@Setter
public class ItemDto {

    private Long id;
    private String itemName;
    private int price;
    private int stockNumber;
    private String itemDetail;
    private String itemSellStatus;
}
