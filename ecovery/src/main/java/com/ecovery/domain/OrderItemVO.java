package com.ecovery.domain;

/*
 * 회원의 주문 상품 VO
 * 에코마켓 이용하는 회원의 주문 상품
 * DB의 order_item 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
