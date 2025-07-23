package com.ecovery.service;

/*
 * 구매이력 조회 서비스 인터페이스
 * 실제 구현은 OrderHistoryServiceImpl에서 처리
 * @author : 방희경
 * @fileName : OrderHistoryService
 * @since : 250723
 */

import com.ecovery.dto.OrderHistoryDto;

import java.util.List;

public interface OrderHistoryService {

    // 로그인한 사용자의 주문 내역 조회
    public List<OrderHistoryDto> getOrderHistory(Long memberId);
}
