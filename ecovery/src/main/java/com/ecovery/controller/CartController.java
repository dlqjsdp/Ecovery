package com.ecovery.controller;

/*
 * 장바구니 컨트롤러
 * 장바구니 상품 추가, 수정, 삭제
 * @author : 방희경
 * @fileName : CartController
 * @since : 250722
 */

import com.ecovery.domain.MemberVO;
import com.ecovery.dto.CartDetailDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.security.CustomUserDetailsService;
import com.ecovery.service.CartItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartItemService cartItemService;

    // 로그인한 사용자 닉네임 가져오는 공통 메서드
    public String getLoginNickname(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUsername();
    }

    // 장바구니 목록 조회
    @GetMapping(value = "/list")
    public String getCartItems(Model model) {
        List<CartDetailDto> cartList = cartItemService.getCartItmes(getLoginNickname());
        model.addAttribute("cartList", cartList);
        return "cart/cart";
    }

    // 장바구니 상품 담기
    @PostMapping("/add")
    public String addCartItem(@RequestParam Long itemId, @RequestParam int count){
        String nickname = getLoginNickname();

        return cartItemService.addCart(nickname, itemId, count);
    }

    // 장바구니 상품 수정
    @PutMapping(value = "/update")
    public String updateCartItem(@RequestParam Long cartItemId, @RequestParam int count, @RequestParam Long itemId){
        return cartItemService.updateCartCount(cartItemId, count, itemId);
    }

    // 장바구니 상품 삭제
    @DeleteMapping(value = "/delete/{cartItemId}")
    public String deleteCartItem(@PathVariable Long cartItemId){
        return cartItemService.deleteCartItem(cartItemId);
    }

}
