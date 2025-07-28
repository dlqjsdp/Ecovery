package com.ecovery.controller;

/*
 * 구매 상품 조회 Controller
 * @author : 방희경
 * @fileName : OrderHistoryController
 * @since : 250722
 */

import com.ecovery.dto.OrderHistoryDto;
import com.ecovery.service.OrderHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/mypage/orders")
@Slf4j
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    // 구매이력 조회
    @GetMapping(value = "/list")
    public String orderHistory(Model model, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/member/login"; //비로그인 상태면 로그인 페이지 이동
        }

        List<OrderHistoryDto> orderList = orderHistoryService.getOrderHistory(memberId);
        model.addAttribute("orderList", orderList);

        return "order/order-detail";
    }
}
