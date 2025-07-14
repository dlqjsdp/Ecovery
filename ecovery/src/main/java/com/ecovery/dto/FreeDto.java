package com.ecovery.dto;

import com.ecovery.constant.DealStatus;
import lombok.Getter;
import lombok.Setter;

/*
 * 무료나눔 게시글 DTO
 * 게시글 정보를 화면에 전달하기 위한 데이터 전달 객체
 * @author : yeonsu
 * @fileName : FreeDto
 * @since : 250710
 */

@Getter
@Setter
public class FreeDto {
    private Long freeId;
    private String regionGu;
    private String category;
    private DealStatus dealStatus;
    private String title;
    private String nickname;
    private String createdAt;
    private int viewCount;

}
