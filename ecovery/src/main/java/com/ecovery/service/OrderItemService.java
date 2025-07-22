package com.ecovery.service;


import com.ecovery.domain.ItemVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderDto;

/*
 * 에코마켓 주문 상품 Service
 * @author : sehui
 * @fileName : OrderItemService
 * @since : 250722
 * @history
 *  - 250722 | sehui | 주문 상품 객체 생성
 */

public interface OrderItemService {

    //주문 상품 객체 생성
    public OrderItemVO createOrderItem(OrderDto orderDto, ItemVO item);
}
