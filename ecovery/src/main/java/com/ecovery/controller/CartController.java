package com.ecovery.controller;

/*
 * 장바구니 컨트롤러
 * 장바구니 상품 추가, 수정, 삭제
 * @author : 방희경
 * @fileName : CartController
 * @since : 250722
 */

import com.ecovery.dto.CartDetailDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    // 장바구니 목록 조회(HTML 페이지용)
    @GetMapping(value = "/list")
    public String getCartItems(Model model) {
        List<CartDetailDto> cartList = cartItemService.getCartItmes(getLoginNickname());
        model.addAttribute("cartList", cartList);
        return "cart/cart";
    }

    // 장바구니 목록 조회(JSON 페이지용)
    @GetMapping("/api/list")
    @ResponseBody
    public List<CartDetailDto> getCartItemsJson() {
        return cartItemService.getCartItmes(getLoginNickname());
    }

    // 장바구니 상품 담기
    @PostMapping("/add")
    public String addCartItem(@RequestParam Long itemId, @RequestParam int count){

        System.out.println("🚀 서버로 전달된 상품 ID: " + itemId);
        System.out.println("🚀 서버로 전달된 상품 주문 수량: " + count);

        String nickname = getLoginNickname();
        System.out.println("nickname" + nickname);

        return cartItemService.addCart(nickname, itemId, count);
    }

    // 장바구니 상품 수정
    @PutMapping(value = "/update")
    @ResponseBody
    public ResponseEntity<String> updateCartItem(@RequestParam Long cartItemId, @RequestParam int count, @RequestParam Long itemId){
        String result = cartItemService.updateCartCount(cartItemId, count, itemId);

        // 서비스에서 반환된 문자열이 "수량 변경 완료"이면 성공, 아니면 실패로 처리하는 로직
        if ("수량 변경 완료".equals(result)) {
            return ResponseEntity.ok("수량 변경 완료");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 장바구니 상품 삭제
    @DeleteMapping(value = "/delete/{cartItemId}")
    public String deleteCartItem(@PathVariable Long cartItemId){
        return cartItemService.deleteCartItem(cartItemId);
    }

}
