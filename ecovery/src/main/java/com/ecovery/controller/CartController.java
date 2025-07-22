package com.ecovery.controller;

/*
 * 장바구니 컨트롤러
 * 장바구니 상품 추가, 수정, 삭제
 * @author : 방희경
 * @fileName : CartController
 * @since : 250722
 */

import com.ecovery.dto.CartDetailDto;
import com.ecovery.service.CartItemService;
import com.ecovery.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

    // 장바구니 목록 조회
    @GetMapping(value = "/list")
    public List<CartDetailDto> getCartItems(HttpSession session) {
        String nickname = (String) session.getAttribute("loginNickname");
        return cartItemService.getCartItmes(nickname);
    }

    // 장바구니 상품 담기
    @PostMapping("/add")
    public String addCartItem(HttpSession session, @RequestParam Long itemId, @RequestParam int count){
        String nickname = (String) session.getAttribute("loginNickname");
        return cartItemService.addCart(nickname, itemId, count);
    }

    // 장바구니 상품 수정
    @PutMapping(value = "/update")
    public String updateCartItem(@RequestParam Long cartItemId, @RequestParam int count, @RequestParam Long itemId){
        return cartItemService.updateCartCount(cartItemId, count, itemId);
    }

    // 장바구니 상품 삭제
    @DeleteMapping(value = "/delete")
    public String deleteCartItem(@PathVariable Long cartItemId){
        return cartItemService.deleteCartItem(cartItemId);
    }

}
