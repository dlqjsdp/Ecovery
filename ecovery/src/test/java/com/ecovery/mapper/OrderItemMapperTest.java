package com.ecovery.mapper;

import com.ecovery.domain.OrderItemVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 상품 Mapper Test
 * @author : sehui
 * @fileName : OrderItemMapperTest
 * @since : 250722
 * @history
 *  - 250722 | sehui | 주문한 개별 상품 정보 저장 Test 추가
 */

@SpringBootTest
@Transactional
@Rollback(false)
@Slf4j
class OrderItemMapperTest {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Test
    @DisplayName("주문한 상품 정보 저장")
    public void testInsert() {

        //given : 주문한 상품 정보 생성
        Long orderId = 1L;
        Long itemId = 11L;

        OrderItemVO orderItemVO = OrderItemVO.builder()
                .orderId(orderId)
                .itemId(itemId)
                .count(2)
                .orderPrice(10000)
                .build();

        //when : 주문한 상품 정보 저장
        orderItemMapper.insertOrderItem(orderItemVO);

        //then : 결과 검증
        OrderItemVO savedOrderItem = orderItemMapper.findByOrderIdAndItemId(orderId, itemId);

        assertNotNull(savedOrderItem);

        log.info("savedOrderItemId >> {}", savedOrderItem.getOrderItemId());

    }

}