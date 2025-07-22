package com.ecovery.service;

import com.ecovery.domain.ItemVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 상품 Service Test
 * @author : sehui
 * @fileName : OrderItemServiceTest
 * @since : 250722
 * @history
 *  - 250722 | sehui | 주문 상품 객체 생성 Test 추가
 */

@SpringBootTest
@Transactional
@Slf4j
class OrderItemServiceTest {

    @Autowired
    private OrderItemService orderItemService;

    @Test
    @DisplayName("주문 상품 객체 생성")
    public void createOrderItem() {

        //given : 주문 정보 OrderDto, 해당 상품 ItemVO
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(10L);
        orderDto.setCount(2);

        ItemVO item = ItemVO.builder()
                .itemId(10L)
                .price(5000)
                .build();

        //when : 주문 상품 객체 생성
        OrderItemVO orderItem = orderItemService.createOrderItem(orderDto, item);

        //then : 결과 검증
        assertNotNull(orderItem);
        assertEquals(10L, orderItem.getItemId());
        assertEquals(2, orderItem.getCount());
        assertEquals(10000, orderItem.getOrderPrice());
    }
}