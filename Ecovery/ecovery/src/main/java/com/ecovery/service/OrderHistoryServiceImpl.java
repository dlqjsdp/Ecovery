package com.ecovery.service;

/*
 * 구매이력 조회 서비스
 * @author : 방희경
 * @fileName : OrderHistoryService
 * @since : 250723
 */

import com.ecovery.dto.OrderHistoryDto;
import com.ecovery.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService{

    private final OrderMapper orderMapper;

    // 로그인한 사용자의 주문 내역 조회
    public List<OrderHistoryDto> getOrderHistory(Long memberId){
        return orderMapper.findOrdersItems(memberId);
    }
}
