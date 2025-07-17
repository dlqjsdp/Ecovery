package com.ecovery.dto;

import com.ecovery.domain.DisposalHistoryImgVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
