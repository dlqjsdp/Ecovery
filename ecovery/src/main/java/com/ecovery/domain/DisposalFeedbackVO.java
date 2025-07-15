package com.ecovery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Member;
import java.util.Date;

/**
 * 대형폐기물 분류 피드백 테이블(disposal_feedback)
 * @author : jihye Lee
 * @fileNmae : DisposalFeedbackVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisposalFeedbackVO {

    private Long feedbackId;
    private Long disposalHistoryId;
    private Date createdAt;
    private Long memberId;

    private DisposalHistoryVO disposalHistory;
}
