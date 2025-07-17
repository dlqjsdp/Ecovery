package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryVO;
import com.ecovery.dto.DisposalHistoryDto;

import java.util.List;

public interface DisposalHistoryService {

    //이력 저장
    public Long saveHistory(DisposalHistoryVO disposalHistoryVO);
    
    //단건 조회(img, info 포함)
    public DisposalHistoryDto getHistory(Long disposalHistoryId);

    //회원별 이력 조회
    public List<DisposalHistoryDto> getHistoryByMemberId(Long memberId);
    
    //관리자용 전체 이력 조회
    public List<DisposalHistoryDto> getAllHistory();

    //최종 finalitem을 저장하기 위한 업데이트 코드
    public int updateHistory(DisposalHistoryVO disposalHistoryVO);
}
