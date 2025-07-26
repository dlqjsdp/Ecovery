package com.ecovery.service;

import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;

/*
 * 에코마켓 주문 상품 Service
 * @author : sehui
 * @fileName : OrderItemService
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 페이지 출력용 객체 생성 기능 추가
 *  - 250723 | sehui | 주문 상품 저장용 객체 생성 기능 추가
 */

public interface OrderItemService {

    //주문 페이지 출력용 객체 생성
    public OrderItemDto buildOrderItem(OrderItemRequestDto requestDto);

    //주문 상품 저장용 객체 생성
    public OrderItemVO saveOrderItem(OrderItemDto orderItemDto, Long orderId);
}
