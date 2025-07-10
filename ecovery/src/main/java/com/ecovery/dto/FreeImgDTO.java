package com.ecovery.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 무료나눔 이미지 DTO
 * 게시글 이미지 정보를 화면에 전달하기 위한 데이터 전달 객체
 * @author : yeonsu
 * @fileName : FreeImgDTO
 * @since : 250710
 */

@Getter
@Setter
public class FreeImgDTO {

    private Long freeImgId;
    private Long freeId;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
}
