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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
@Slf4j
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    // 구매이력 간락 목록 조회
    @GetMapping(value = "/list")
    public String orderHistory(Model model, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/member/login"; //비로그인 상태면 로그인 페이지 이동
        }

        // 간략한 주문 목록을 모델에 담기
        List<OrderHistoryDto> orderSummaries = orderHistoryService.getOrderSummaries(memberId);
        model.addAttribute("orderSummaries", orderSummaries);

        return "order/orderhistory";
    }

    // 주문 상세 보기
    @GetMapping(value = "/{orderId}")
    public String orderDetail(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/member/login"; //비로그인 상태면 로그인 페이지 이동
        }
        // 특정 주문의 상세정보 가져오기
        OrderHistoryDto orderDetail = orderHistoryService.getOrderDetail(orderId);

        // 해당 주문이 현재 로그인한 회원의 주문이 맞는지 확인하는 로직
        if (!orderDetail.getMemberId().equals(memberId)) {
            // 다른 회원 주문의 경우 에러 페이지로 리다이렉트
            return "redirect:/error";
        }
        model.addAttribute("orderDetail", orderDetail);
        return "order/order-detail";
    }
}
