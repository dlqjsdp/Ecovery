package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryImgVO;
import com.ecovery.dto.DisposalHistoryImgDto;

import java.util.List;

public interface DisposalHistoryImgService {

    public void saveDisposalImg(DisposalHistoryImgDto disposalHistoryImgDto);

    public DisposalHistoryImgDto getDisposalImg(Long disposalHistoryId);

}
