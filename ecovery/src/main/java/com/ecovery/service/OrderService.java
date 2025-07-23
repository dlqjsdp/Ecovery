package com.ecovery.service;

/*
 * 에코마켓 주문 Service
 * @author : sehui
 * @fileName : OrderService
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 페이지에 보여줄 주문 정보 세팅 기능 추가
 *  - 250723 | sehui | 실제 주문 저장 기능 추가
 */

import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemRequestDto;

import java.util.List;

public interface OrderService {

    //주문 페이지용 주문 정보 세팅
    public OrderDto prepareOrderDto(List<OrderItemRequestDto> requestDtoList, Long memberId);

    //실제 주문 저장
    public Long saveOrder(OrderDto orderDto, Long memberId);

    //결제 API 요청?
}
