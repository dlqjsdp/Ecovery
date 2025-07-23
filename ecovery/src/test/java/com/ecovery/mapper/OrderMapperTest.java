package com.ecovery.mapper;

import com.ecovery.constant.OrderStatus;
import com.ecovery.domain.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 Mapper Test
 * @author : sehui
 * @fileName : OrderMapperTest
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문 등록 저장 Test 추가
 */

@SpringBootTest
@Transactional
@Rollback(false)        //DB 확인
@Slf4j
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    @DisplayName("주문 저장")
    public void testInsert() {

        //given : OrderVO 객체 설정
        OrderVO order = OrderVO.builder()
                .memberId(2L)
                .orderStatus(OrderStatus.ORDER)
                .name("tester")
                .zipcode("12345")
                .roadAddress("서울시 강남구 테헤란로 123")
                .detailAddress("101동")
                .phoneNumber("010-1234-5678")
                .build();

        //when : 주문 저장
        int result = orderMapper.insertOrder(order);

        //then : 결과 검증
        assertEquals(1, result);

        log.info("저장된 주문 >> {}", order);

    }

}