package com.ecovery.service;

import com.ecovery.constant.OrderStatus;
import com.ecovery.domain.ItemVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.domain.OrderVO;
import com.ecovery.dto.OrderDto;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/*
 * 에코마켓 주문 ServiceImpl
 * @author : sehui
 * @fileName : OrderServiceImpl
 * @since : 250722
 * @history
 *  - 250722 | sehui | 단건 상품 + 장바구니 목록 주문 기능 추가 (수정 필요)
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderItemService orderItemService;
    private final MemberMapper memberMapper;
    private final ItemService itemService;

    //주문
    @Override
    public Long order(List<OrderDto> orderDtoList, String email) {

        //회원 정보 조회
        MemberVO member = memberMapper.findByEmail(email);

        //주문 정보 생성
        OrderVO order = OrderVO.builder()
                .memberId(member.getMemberId())
                .orderStatus(OrderStatus.ORDER)
                .build();

        //주문 상품 리스트 생성
        List<OrderItemVO> orderItemList = new ArrayList<>();

        for(OrderDto orderDto : orderDtoList) {
            //상품 정보 조회
            ItemVO item = itemService.findByItemId(orderDto.getItemId());

            if(item == null) {
                throw new IllegalArgumentException("해당 상품을 찾을 수 없습니다.");
            }

            //재고 차감 + 주문 상품 객체 생성
            OrderItemVO orderItem = orderItemService.createOrderItem(orderDto, item);

            orderItemList.add(orderItem);
        }

        //주문 생성 및 저장
        OrderVO orders = OrderVO.builder()
                .memberId(member.getMemberId())
                .build();

        return 0L;
    }
}
