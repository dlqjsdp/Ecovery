package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 공지사항 게시글 테이블(notice)
 * @author : yukyeong
 * @fileName : NoticeVO
 * @since : 250708
 * @history
     - 250708 | yukyeong | VO 기본 필드 작성
 */

@Getter @Setter
@ToString
public class NoticeVO {
    private Long noticeId; // 공지사항 ID
    private Long memberId; // 작성자 ID
    private String title; // 제목
    private String content; // 내용
    private int viewCount; // 조회수
    private LocalDateTime createdAt; // 작성일자
    private LocalDateTime updatedAt; // 수정일자
}
