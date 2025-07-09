package com.ecovery.domain;

<<<<<<< HEAD
=======
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
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9
public class DisposalHistoryVO {

    private Long disposalHistoryId;
    private Long memberId;
    private String aiPrediction;
    private String regionGu;
    private String createAt;
}
