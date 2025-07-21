package com.ecovery.mapper;

import com.ecovery.domain.CartItemVO;
import com.ecovery.dto.CartDetailDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 장바구니 상품 조회 Mapper
 * 로그인한 사용자 장바구니 조회용(조회, 추가, 수량 수정, 삭제 등)
 * @author : 방희경
 * @fileName : CartMapper
 * @since : 20250721
 */

@Mapper
public interface CartMapper {

    // 회원 ID로 장바구니 ID 조회
    public Long findCartIdByMemberId(Long memberId);

    // 장바구니 항목 조회(이미 있으면 수량 증가, 없으면 새로 담음)
    public CartItemVO findCartItem(@Param("cartId") Long cartId, @Param("itemId") Long itemId);

    // 장바구니에 해당 상품이 없으면 호출
    public void insertCartItem(CartItemVO cartItemVO);

    // 기존에 있던 상품이면 수량을 누적
    public void updateCartItem(@Param("cartItemId") Long cartItemId, @Param("addcount") int addcount);

    // 장바구니 전체 목록 출력
    public List<CartDetailDto> getCartItems(Long cartId);

    // 장바구니 상품 수량 수정
    public void updateCartItemCount(@Param("cartItemId") Long cartItemId, @Param("count") int count);

    // 장바구니 상품 삭제
    public void deleteCartItem(Long cartItemId);

}
