package com.ecovery.domain;

import com.ecovery.constant.OrderStatus;
import lombok.Builder;
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
@Builder
public class OrderVO {

    private Long orderId;
    private Long memberId;
    private OrderStatus orderStatus;
    private Date createdAt;
    private Date updatedAt;
    private String name;
    private String zipcode;
    private String roadAddress;
    private String detailAddress;
    private String phoneNumber;
}
