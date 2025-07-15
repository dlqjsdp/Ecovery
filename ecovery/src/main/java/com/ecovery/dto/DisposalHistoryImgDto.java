package com.ecovery.dto;

import com.ecovery.domain.DisposalHistoryImgVO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisposalHistoryImgDto {

    private Long disposalImgId;
    private Long disposalHistoryId;
    private String disposalImgName;
    private String oriDisposalImgName;
    private String disposalImgUrl;

    public DisposalHistoryImgVO toDisposalHistoryImgVO() {
        return DisposalHistoryImgVO.builder()
                .disposalHistoryId(disposalHistoryId)
                .disposalImgName(disposalImgName)
                .oriDisposalImgName(oriDisposalImgName)
                .disposalImgUrl(disposalImgUrl)
                .build();
    }
}
