package com.ecovery.mapper;

import com.ecovery.dto.OrderHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 구매이력 조회 Mapper
 * 구매이력 조회 및 상세 내용 확인을 위한 인터페이스
 * @author : 방희경
 * @fileName : OrderMapper
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 저장 기능 추가
 */

@Mapper
public interface OrderMapper {

    // 로그인한 사용자의 주문 내역 조회
    public List<OrderHistoryDto> findOrdersItems(Long memberId);

    //주문 저장
    public int insertOrder(OrderVO orderVO);

}
