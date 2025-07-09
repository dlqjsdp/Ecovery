package com.ecovery.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/*
 * 에코마켓 상세 페이지에서 사용자의 주문 정보
 * 사용자가 전달한 주문 정보를 서버에 전달하기 위한 DTO입니다.
 * 최대 주문 수량은 논의 후 수정이 필요합니다.
 * 작성자 : 오세희
 */

@Getter
@Setter
public class OrderDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;
}
