package com.ecovery.controller;

import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.service.MemberService;
import com.ecovery.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 에코마켓 상품 Api Controller
 * @author : sehui
 * @fileName : OrderApiController
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문 페이지 요청 기능 추가
 *  - 250723 | sehui | 실제 주문 저장 기능 추가
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Slf4j
public class OrderApiController {

    private final OrderService orderService;
    private final MemberService memberService;

    //주문 페이지 요청
    @PostMapping("/prepare")
    public ResponseEntity<OrderDto> prepareOrder(@Valid @RequestBody List<OrderItemRequestDto> orderItemRequests,
                                                 BindingResult bindingResult,
                                                 Principal principal) {
        //유효성 검사
        if(bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //로그인한 사용자 정보 조회
        String email = principal.getName();
        Long memberId  = memberService.getMemberByEmail(email).getMemberId();

        //주문 페이지에 출력할 OrderDto 생성
        OrderDto orderDto = orderService.prepareOrderDto(orderItemRequests, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    //결제 완료 후 주문 저장
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> saveOrder(@Valid @RequestBody OrderDto orderDto,
                                                         BindingResult bindingResult,
                                                         Principal principal) {

        //유효성 검사
        if(bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //로그인한 사용자 정보 조회
        String email = principal.getName();
        Long memberId  = memberService.getMemberByEmail(email).getMemberId();

        Map<String, Object> response = new HashMap<>();

        //주문 저장
        try{
            Long saveOrderId = orderService.saveOrder(orderDto, memberId);
            response.put("orderId", saveOrderId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            response.put("message", "주문 등록 중 에러가 발생하였습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
