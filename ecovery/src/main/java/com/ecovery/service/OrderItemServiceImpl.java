package com.ecovery.service;

import com.ecovery.domain.ItemVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * 에코마켓 주문 상품 ServiceImpl
 * @author : sehui
 * @fileName : OrderItemServiceImpl
 * @since : 250722
 * @history
 *  - 250722 | sehui | 주문 상품 객체 생성
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final ItemService itemService;

    //주문 상품 객체 생성
    @Override
    public OrderItemVO createOrderItem(OrderDto orderDto, ItemVO item) {

        //재고 차감
        itemService.removeStock(item.getItemId(), orderDto.getCount());

        //주문 가격
        int totalPrice = item.getPrice() * orderDto.getCount();

        return OrderItemVO.builder()
                .itemId(item.getItemId())
                .count(orderDto.getCount())
                .orderPrice(totalPrice)
                .build();
    }
}
