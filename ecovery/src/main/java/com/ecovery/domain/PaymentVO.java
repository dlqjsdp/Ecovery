package com.ecovery.domain;

/*
 * 회원의 결제 정보 VO
 * 에코마켓 이용하는 회원의 결제 정보
 * DB의 payment 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

import com.ecovery.constant.PayStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentVO {

    private Long paymentId;
    private Long orderId;
    private Long memberId;
    private String payMethod;
    private int payAmount;
    private PayStatus payStatus;
    private LocalDateTime paidAt;
    private Boolean test_mode;
}
