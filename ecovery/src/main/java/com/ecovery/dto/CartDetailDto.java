package com.ecovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 장바구니 Dto
 * 게시글 정보를 화면에 전달하기 위한 데이터 전달 객체
 * @author : yeonsu
 * @fileName : FreeDto
 * @since : 250710
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailDto {

    private Long cartItemId; //장바구니 상품 아이디
    private String itemNm; //상품명
    private int price; //상품 금액
    private int count; //수량
    private String imgUrl; //상품 이미지 경로
    private int stockNumber;  // 남은 재고

}
