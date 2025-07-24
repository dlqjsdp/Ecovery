package com.ecovery.dto;

import lombok.*;

/*
 * 구매이력 조회 DTO
 * 주문 1건에 포함된 상품 정보
 * @author : 방희경
 * @fileName : OrderItemDto
 * @since : 250723
 * @history
 *  - 250724 | sehui | 상품 이미지 URL 추가
 */

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class OrderItemDto {

    // 상품 정보(item)
    private Long itemId;
    private String itemName;

    // 주문 상품 정보(order_item)
    private Long orderItemId;
    private int price;
    private int count;
    private int orderPrice;     //주문 금액

    // 상품 이미지 정보(item_img)
    private Long itemImgId;
    private String imgName;
    private String imgUrl;
}
