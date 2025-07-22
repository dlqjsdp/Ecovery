package com.ecovery.mapper;

import com.ecovery.domain.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 구매이력 조회 Mapper
 * 구매이력 조회 및 상세 내용 확인을 위한 인터페이스
 * @author : 방희경
 * @fileName : OrderMapper
 * @since : 250722
 */

@Mapper
public interface OrderMapper {

    // 로그인한 사용자의 주문 내역 조회(주문 + 주문상세 + 상품)
    public List<OrderVO> findOrdersItems(Long memberId);
}
