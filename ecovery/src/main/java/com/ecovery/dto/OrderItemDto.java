package com.ecovery.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * 에코마켓 주문페이지에 주문 상품 정보 출력용 Dto
 * @author : sehui
 * @fileName : OrderItemDto
 * @since : 250723
 */

@Getter
@Setter
@Builder
@ToString
public class OrderItemDto {

    private Long orderItemId;       //주문 항목 id
    private Long itemId;            //상품 id
    private String itemNm;
    private int price;
    private int count;
    private int totalPrice;

}
