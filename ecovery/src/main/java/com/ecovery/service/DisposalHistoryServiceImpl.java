package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryVO;
import com.ecovery.mapper.DisposalHistoryMapper;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DisposalHistoryServiceImpl implements DisposalHistoryService {

    private final DisposalHistoryMapper disposalHistoryMapper;

    @Override
    public Long saveHistory(DisposalHistoryVO disposalHistoryVO) {
        disposalHistoryMapper.insertDisposalHistory(disposalHistoryVO);
        return disposalHistoryVO.getDisposalHistoryId();
    }

    @Override
    public DisposalHistoryVO getHistory(Long disposalHistoryId) {
        return disposalHistoryMapper.findByDisposalHistoryId(disposalHistoryId);
    }

    @Override
    public List<DisposalHistoryVO> getHistoryByMemberId(Long memberId) {
        return disposalHistoryMapper.findDisposalHistoryWithImgByMemberId(memberId);
    }

    @Override
    public List<DisposalHistoryVO> getAllHistory() {
        return disposalHistoryMapper.findAllDisposalHistory();
    }
}
