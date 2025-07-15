package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryVO;

import java.util.List;

public interface DisposalHistoryService {

    //이력 저장
    public Long saveHistory(DisposalHistoryVO disposalHistoryVO);
    
    //단건 조회(img, info 포함)
    public DisposalHistoryVO getHistory(Long disposalHistoryId);

    //회원별 이력 조회
    public List<DisposalHistoryVO> getHistoryByMemberId(Long memberId);
    
    //관리자용 전체 이력 조회
    public List<DisposalHistoryVO> getAllHistory();
}
