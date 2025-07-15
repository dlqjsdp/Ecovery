package com.ecovery.dto;

import com.ecovery.domain.DisposalHistoryImgVO;

import java.util.Date;

public class DisposalFeedbackDto {

    private Long feedbackId;
    private Long disposalHistoryId;
    private Date createdAt;
    private Long memberId;

    private String aiPrediction;
    private String regionGu;
    private String finalItem;

    private String disposalHistoryImgUrl;

    private String nickname;
}
