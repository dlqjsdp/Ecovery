package com.ecovery.service;

/*
 * 에코마켓 주문 Service
 * @author : sehui
 * @fileName : OrderService
 * @since : 250722
 * @history
 *  - 250722 | sehui | 단건 상품 + 장바구니 목록 주문 기능 추가
 */

import com.ecovery.dto.OrderDto;

import java.util.List;

public interface OrderService {

    //주문 (단건 상품 + 장바구니 목록)
    public Long order(List<OrderDto> orderDtoList, String email);
}
