package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 무료나눔 게시글 테이블(free)
 * @author : yeonsu
 * @fileName : FreeVO
 * @since : 250708
 */


@Getter
@Setter
@ToString

public class FreeVO {

    private Long freeId;        // 게시글 고유번호 (PK)
    private Long memberId;      // 작성자 회원ID (FK)
    private String title;       // 게시글 제목
    private String content;     // 게시글 상세 내용
    private String catefory;    // 품목 카테고리
    private String regionGu;    // 나눔지역 - 구
    private String regionDong;  // 나눔지역 - 동
    private String itemCondition; // 삼품 상태(상/중/하)
    private String dealStatus; // 거래 상태(진행중, 완료)
    private Integer viewCount; //게시글 조회수
    private LocalDateTime createdAt; // 게시글 등록/수정일(시간포함)
}
