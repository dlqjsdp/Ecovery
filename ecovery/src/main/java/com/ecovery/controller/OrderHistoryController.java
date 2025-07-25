package com.ecovery.controller;

/*
 * 에코마켓 View Controller
 * @author : sehui
 * @fileName : ItemViewController
 * @since : 250721
 * @history
 *  - 250721 | sehui | View 반환을 위해 컨트롤러 생성
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
    @GetMapping
    public String orderHistory(Model model, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        
        if (memberId == null) {
            return "redirect:/member/login"; //비로그인 상태면 로그인 페이지 이동
        }

        List<OrderHistoryDto> orderList = orderHistoryService.getOrderHistory(memberId);
        model.addAttribute("orderList", orderList);

        return "mypage/orderHistory";
    }
}
