package com.ecovery.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 회원의 결제 완료 정보 Dto
 * @author : sehui
 * @fileName : PaymentConfirmDto
 * @since : 250724
 */

@Getter
@Setter
public class PaymentConfirmDto {

    private String paymentKey;
    private String orderUuid;       //결제 API에서 사용하는 주문 고유번호
    private int amount;
}
