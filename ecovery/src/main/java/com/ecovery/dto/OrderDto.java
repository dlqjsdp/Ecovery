package com.ecovery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/*
 * 에코마켓 사용자의 전체 주문 정보 + 사용자 입력 정보 Dto
 * @author : sehui
 * @fileName : OrderDto
 * @since : 250723
 * @history
 *  - 250723 | sehui | OrderDto 생성
 *  - 250724 | sehui | 주문 고유번호 UUID 변수 추가
 */

@Getter
@Setter
@ToString
public class OrderDto {

    private Long orderId;           //주문 id

    private String orderUuid;       //주문 고유번호 UUID

    private Long memberId;          //주문한 사용자 id

    private List<OrderItemDto> orderItems;

    //사용자 입력 정보
    @NotBlank
    private String name;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String roadAddress;

    @NotBlank
    private String detailAddress;

    @NotBlank
    private String phoneNumber;

    //계산 정보
    private int totalPrice;

}
