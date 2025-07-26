package com.ecovery.service;

import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 Service Test
 * @author : sehui
 * @fileName : OrderServiceTest
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문 페이지에 보여줄 주문 정보 세팅 기능 Test 실행
 *  - 250723 | sehui | 실제 주문 저장 기능 Test 실행
 */

@SpringBootTest
@Transactional
@Rollback(false)    //DB 반영
@Slf4j
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문 페이지의 주문 정보")
    public void testPageOrderDto() {

        //given : 주문 요청 정보 설정
        List<OrderItemRequestDto> requestDtoList = new ArrayList<>();

        OrderItemRequestDto requestDto1 = new OrderItemRequestDto();
        requestDto1.setItemId(5L);
        requestDto1.setCount(2);

        OrderItemRequestDto requestDto2 = new OrderItemRequestDto();
        requestDto2.setItemId(9L);
        requestDto2.setCount(3);

        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        Long memberId = 2L;

        //when : 주문 정보 OrderDto 세팅
        OrderDto orderDto = orderService.prepareOrderDto(requestDtoList, memberId);

        //then : 결과 검증
        assertNotNull(orderDto);

        log.info("orderDto >> {}", orderDto.toString());

    }

    @Test
    @DisplayName("주문 저장")
    public void testSaveOrder(){

        //given : 주문 정보 설정
        OrderDto orderDto = new OrderDto();
        orderDto.setMemberId(2L);
        orderDto.setName("tester");
        orderDto.setZipcode("12345");
        orderDto.setRoadAddress("서울시 00구");
        orderDto.setDetailAddress("1동 1호");
        orderDto.setPhoneNumber("010-1111-2222");

        List<OrderItemDto> requestDtoList = new ArrayList<>();

        OrderItemDto requestDto1 = OrderItemDto.builder()
                .itemId(10L)
                .itemName("test Item3")
                .price(15000)
                .count(2)
                .orderPrice(15000 * 2)
                .build();

        OrderItemDto requestDto2 = OrderItemDto.builder()
                .itemId(9L)
                .itemName("test 등록")
                .price(5000)
                .count(3)
                .orderPrice(5000 * 3)
                .build();

        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        orderDto.setOrderItems(requestDtoList);

        Long memberId = 2L;

        //when : 주문 저장
        Long savedOrderId = orderService.saveOrder(orderDto, memberId);

        //then : 결과 검증
        assertNotNull(savedOrderId);

        log.info("savedOrderId >> {}", savedOrderId);

    }

}