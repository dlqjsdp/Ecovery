package com.ecovery.mapper;

import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderItemDto;
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
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문한 개별 상품 정보 저장 Test 실행
 *  - 250724 | sehui | 주문 상품 단건 조회 Test 실행
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
        Long orderId = 4L;
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
        OrderItemDto savedOrderItem = orderItemMapper.findByOrderIdAndItemId(orderId, itemId);

        assertNotNull(savedOrderItem);
        assertEquals(11, savedOrderItem.getItemId());
        assertEquals(2, savedOrderItem.getCount());
        assertEquals(10000, savedOrderItem.getOrderPrice());

        log.info("savedOrderOrderItemId >> {}", savedOrderItem.getOrderItemId());

    }

    @Test
    @DisplayName("주문 상품 단건 조회")
    public void testGet(){

        //given : 조회할 주문 상품 id, 상품 id
        Long orderId = 1L;
        Long itemId = 10L;

        //when : 주문 상품 단건 조회
        OrderItemDto orderItem = orderItemMapper.findByOrderIdAndItemId(orderId, itemId);

        //then : 결과 검증
        assertNotNull(orderItem);

        log.info("orderItemDto >> {}", orderItem);
    }

}