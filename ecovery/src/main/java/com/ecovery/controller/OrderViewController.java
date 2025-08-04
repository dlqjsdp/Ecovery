package com.ecovery.controller;

import com.ecovery.dto.OrderItemRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * 에코마켓 주문 View Controller
 * @author : sehui
 * @fileName : OrderViewController
 * @since : 250728
 * @history
 *  - 250728 | sehui | View 반환을 위해 컨트롤러 생성
 *  - 250801 | sehui | 주문 페이지 첫 화면 요청 GET -> POST로 변경
 *  - 250801 | sehui | 반환하는 View 변경
 *  - 250801 | sehui | 주문 페이지 첫 화면에서 model로 전달하는 값 추가
 *  - 250802 | sehui | 주문 페이지 첫 화면에 orderItemRequestDto를 JSON 문자열로 반환 로직 추가
 *  - 250804 | 방희경 | 여러개 상품 주문을 위한 로직 변경
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderViewController {

    //주문 페이지 첫 화면
    @PostMapping("/prepare")
    public String prepareOrderPage(@RequestBody List<OrderItemRequestDto> orderItemRequest, Model model) {

        try{
            //OrderItemRequestDto를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(List.of(orderItemRequest));

            model.addAttribute("orderItemRequests", json);
        }catch (JsonProcessingException e){
            throw new RuntimeException("JSON 변환 실패", e);
        }

        return "order/order-payment";
    }

    //결제 실패 시 다시 주문 페이지
    @GetMapping("/retry/{orderId}")
    public String RetryPage(@PathVariable String orderId, Model model){

        model.addAttribute("orderId", orderId);

        return "order/order-payment";
    }

    //주문 완료 페이지

}
