package com.ecovery.service;

import com.ecovery.domain.ItemVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.dto.CartDetailDto;
import com.ecovery.mapper.CartMapper;
import com.ecovery.mapper.ItemMapper;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 장바구니 ServiceImpl
 * @author : 방희경
 * @fileName : CartServiceImpl
 * @since : 250721
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;
    private final MemberMapper memberMapper;

    // 장바구니 담기
    @Override
    public String addCart(String nickname, Long itemId, int count){
        MemberVO memberVO = memberMapper.findByNickname(nickname);
        if(memberVO == null)
            return "회원 정보를 찾을 수 없습니다.";

        Long cartId = cartMapper.findCartIdByMemberId(memberVO.getMemberId());
        if(cartId == null)
            return "장바구니 정보가 없습니다.";

        ItemVO item = itemMapper.findByItemId(itemId);
        if(item == null)
            return "상품이 존재하지 않습니다.";
        
        if(count > item.getStockNumber()){
            return "재고 수량을 초과하여 담을 수 없습니다.";
        }

        return nickname;
    }

    // 장바구니 목록 조회
    public List<CartDetailDto> getCartItmes(String nickname){
        return null;
    }

    // 장바구니 수량 수정
    public String updateCartCount(Long cartItemId, int count, Long itemId){
        return null;
    }

    // 장바구니 상품 삭제
    public String deleteCartItem(Long cartItemId){
        return null;
    }

}
