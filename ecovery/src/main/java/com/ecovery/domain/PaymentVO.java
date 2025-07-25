package com.ecovery.domain;

import com.ecovery.constant.PayStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * 회원의 결제 정보 VO
 * @author : sehui
 * @fileName : PaymentVO
 * @since : 250709
 */

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
