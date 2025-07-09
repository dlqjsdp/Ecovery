package com.ecovery.domain;

import java.time.LocalDateTime;

public class NoticeVo {
    private Long noticeId;
    private Long memberId;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
