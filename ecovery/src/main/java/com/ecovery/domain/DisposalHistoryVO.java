package com.ecovery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대형폐기물 분류 이력 테이블(disposal_history)
 * @author : jihye Lee
 * @fileNmae : DisposalHistoryVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisposalHistoryVO {

    private Long disposalHistoryId;
    private Long memberId;
    private String aiPrediction;
    private String regionGu;
    private String createdAt;
    private String finalItem;

    private DisposalHistoryImgVO disposalImg;
    private DisposalInfoVO disposalInfo;
}
