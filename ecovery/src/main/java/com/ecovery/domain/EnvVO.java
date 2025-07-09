package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 환경톡톡 게시글 테이블(env)
 * @author : 노유경
 * @fileName : EnvVO
 * @since : 250708
 */

@Getter @Setter
@ToString
public class EnvVO {
    private Long envId; // 게시글 ID
    private Long memberId; // 작성자 ID
    private String title; // 제목
    private String content; // 내용
    private int viewCount; // 조회수
    private LocalDateTime createAt; // 작성일자
    private LocalDateTime updateAt; // 수정일자

}
