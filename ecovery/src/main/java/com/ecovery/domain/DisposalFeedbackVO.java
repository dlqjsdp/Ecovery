package com.ecovery.domain;

<<<<<<< HEAD
import java.util.Date;

=======
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9
public class DisposalFeedbackVO {

    private Long feedbackId;
    private Long disposalHistoryId;
    private Date createdAt;
}
