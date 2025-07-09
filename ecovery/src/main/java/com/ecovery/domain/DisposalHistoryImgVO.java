package com.ecovery.domain;

<<<<<<< HEAD
=======
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대형폐기물 이미지 테이블(disposal_history_img)
 * @author : jihye Lee
 * @fileNmae : DisposalHistoryImgVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
>>>>>>> 8ad52e142275893a8fd8a093f7ceb9d06d077ed9
public class DisposalHistoryImgVO {

    private Long disposalImgId;
    private Long disposalHistoryid;
    private String disposalImgName;
    private String oriDisposalImgName;
    private String disposalImgUrl;
}
