package com.ecovery.dto;

/*
 * 에코마켓 상품 이미지 화면 출력용
 * ItemImgVO에서 필요한 내용을 저장하여 보여줍니다.
 * 작성자 : 오세희
 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemImgDto {

    private Long id;
    private String imgName;         //이미지 파일명
    private String oriImgName;      //원본 이미지명
    private String repImgName;      //이미지 조회 경로
}
