package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * 에코마켓 상품 이미지 VO
 * 에코마켓에서 판매하는 상품의 이미지 정보
 * DB의 item_img 테이블과 매핑됩니다.
 * 작성자 : 오세희
 */

@Getter
@Setter
public class ItemImgVO {

    private Long itemImgId;
    private Long itemId;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgUrl;
    private LocalDateTime createdAt;

}
