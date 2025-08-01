package com.ecovery.controller;

import com.ecovery.dto.OrderItemRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 에코마켓 주문 View Controller
 * @author : sehui
 * @fileName : OrderViewController
 * @since : 250728
 * @history
 *  - 250728 | sehui | View 반환을 위해 컨트롤러 생성
 *  - 250801 | sehui | 주문 페이지 첫 화면 요청 GET -> POST로 변경
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderViewController {

    //주문 페이지 첫 화면
    @PostMapping("/prepare")
    public String preparePage(OrderItemRequestDto orderItemRequest){

        return "orderhistory";
    }

    //결제 실패 시 다시 주문 페이지
    @GetMapping("/retry/{orderId}")
    public String RetryPage(@PathVariable String orderId, Model model){

        model.addAttribute("orderId", orderId);

        return "orderhistory";
    }

    //주문 완료 페이지

}
