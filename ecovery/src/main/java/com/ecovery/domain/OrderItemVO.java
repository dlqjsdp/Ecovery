package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 * 회원의 주문 상품 VO
 * @author : sehui
 * @fileName : OrderItemVO
 * @since : 250709
 */

@Getter
@Setter
public class OrderItemVO {

    private Long orderItemId;
    private Long orderId;
    private Long itemId;
    private int count;
    private int orderPrice;
    private Date createdAt;
}
