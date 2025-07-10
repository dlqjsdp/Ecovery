package com.ecovery.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 무료나눔 댓글 DTO
 * 댓글 내용을 화면에 전달하기 위한 데이터 전달 객체
 * @author : yeonsu
 * @fileName : FreeReplyDTO
 * @since : 250710
 */

@Getter
@Setter
public class FreeReplyDTO {

    private Long replyId;
    private String content;
    private Long nickName;
    private String createdAt;

}
