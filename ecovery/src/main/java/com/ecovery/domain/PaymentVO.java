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
 * @history
 *  - 250709 | sehui | 결제 정보 VO 생성
 *  - 250724 | sehui | 필수 변수 추가
 */

@Getter
@Setter
public class PaymentVO {

    private Long paymentId;

    private Long orderId;       //Order 테이블과 연동
    private String orderUuid;   //결제 API에 전달할 주문 고유번호

    private Long memberId;      //회원 정보

    private String paymentKey;  //API에서 전달받은 결제 키
    private String payMethod;   //결제 수단(카드, 계좌 등)
    private int payAmount;      //총 결제 금액
    private PayStatus payStatus;    //결제 처리 상태
    private LocalDateTime paidAt;   //결제 완료 시간

    private Boolean test_mode;      //테스트 환경 여부
}
