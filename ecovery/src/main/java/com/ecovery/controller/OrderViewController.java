package com.ecovery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 에코마켓 주문 View Controller
 * @author : sehui
 * @fileName : OrderViewController
 * @since : 250728
 * @history
 *  - 250728 | sehui | View 반환을 위해 컨트롤러 생성
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderViewController {

    //주문 페이지 첫 화면
    @GetMapping("/prepare")
    public String preparePage(){

        return "order/order-detail";
    }

    //결제 실패 시 다시 주문 페이지
    @GetMapping("/retry/{orderId}")
    public String RetryPage(@PathVariable String orderId, Model model){

        model.addAttribute("orderId", orderId);

        return "order/order-detail";
    }

    //주문 완료 페이지

}
