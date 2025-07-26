package com.ecovery.service;

import com.ecovery.constant.OrderStatus;
import com.ecovery.domain.ItemVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.domain.OrderVO;
import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.mapper.MemberMapper;
import com.ecovery.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * 에코마켓 주문 ServiceImpl
 * @author : sehui
 * @fileName : OrderServiceImpl
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 페이지에 보여줄 주문 정보 세팅 기능 추가
 *  - 250723 | sehui | 실제 주문 저장 기능 추가
 *  - 250724 | sehui | 주문 고유번호 UUID 생성하는 로직 추가
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;

    //주문 페이지 출력용 OrderDto 세팅
    @Override
    public OrderDto prepareOrderDto(List<OrderItemRequestDto> requestDtoList, Long memberId) {

        List<OrderItemDto> orderItemList = new ArrayList<>();

        int totalPrice = 0;

        //주문 상품 orderItem
        for(OrderItemRequestDto requestDto : requestDtoList) {
            //주문 상품 객체 생성 + 재고 차감
            OrderItemDto orderItem = orderItemService.buildOrderItem(requestDto);

            orderItemList.add(orderItem);

            totalPrice += orderItem.getOrderPrice();
        }

        //주문 UUID 생성
        String orderUuid = UUID.randomUUID().toString();

        //주문 OrderDto 객체 생성, 주문 상품 List, 총 금액 값 설정
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderUuid(orderUuid);
        orderDto.setMemberId(memberId);
        orderDto.setOrderItems(orderItemList);
        orderDto.setTotalPrice(totalPrice);

        return orderDto;
    }

    //실제 주문 저장
    @Override
    public Long saveOrder(OrderDto orderDto, Long memberId) {

        //주문 저장하기 위해 OrderVO 생성 및 값 설정
        OrderVO order = OrderVO.builder()
                .memberId(memberId)
                .orderStatus(OrderStatus.ORDER)
                .name(orderDto.getName())
                .zipcode(orderDto.getZipcode())
                .roadAddress(orderDto.getRoadAddress())
                .detailAddress(orderDto.getDetailAddress())
                .phoneNumber(orderDto.getPhoneNumber())
                .build();

        //주문 DB 저장
        int result = orderMapper.insertOrder(order);

        if(result != 1) {
            throw new RuntimeException("주문 저장에 실패하였습니다.");
        }

        //주문 id 확인
        Long orderId = order.getOrderId();

        //주문 상품 저장
        for(OrderItemDto orderItemDto : orderDto.getOrderItems()) {
            orderItemService.saveOrderItem(orderItemDto, orderId);
        }

        return orderId;
    }
}
