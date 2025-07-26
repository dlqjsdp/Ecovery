package com.ecovery.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/*
 * 에코마켓 사용자의 상품 주문 요청 Dto
 * @author : sehui
 * @fileName : OrderItemRequestDto
 * @since : 250709
 */

@Getter
@Setter
public class OrderItemRequestDto {

    @NotNull
    private Long itemId;

    //최대 수량 조정 필요
    @Min(value = 1)
    @Max(value = 99)
    private int count;
}
