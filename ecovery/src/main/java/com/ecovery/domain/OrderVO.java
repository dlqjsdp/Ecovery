package com.ecovery.domain;

/*
 * 회원의 주문 VO
 * 에코마켓 이용하는 회원의 주문 정보
 * DB의 orders 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

import com.ecovery.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderVO {

    private Long orderId;
    private Long memberId;
    private OrderStatus orderStatus;
    private Date createdAt;
    private Date updatedAt;
}
