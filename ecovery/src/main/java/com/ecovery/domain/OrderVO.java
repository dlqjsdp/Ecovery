package com.ecovery.domain;

import com.ecovery.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 * 회원의 주문 정보 VO
 * @author : sehui
 * @fileName : OrderVO
 * @since : 250709
 */

@Getter
@Setter
public class OrderVO {

    private Long orderId;
    private Long memberId;
    private OrderStatus orderStatus;
    private Date createdAt;
    private Date updatedAt;
}
