package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryImgVO;

import java.util.List;

public interface DisposalHistoryImgService {

    public void saveImg(DisposalHistoryImgVO disposalHistoryImgVO);

    public DisposalHistoryImgVO getHistoryImg(Long disposalHistoryId);

}
