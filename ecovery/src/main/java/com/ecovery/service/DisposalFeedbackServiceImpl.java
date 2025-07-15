package com.ecovery.service;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.DisposalFeedbackDto;
import com.ecovery.mapper.DisposalFeedbackMapper;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisposalFeedbackServiceImpl implements DisposalFeedbackService {

    private final DisposalFeedbackMapper disposalFeedbackMapper;

    @Override
    public void saveFeedback(DisposalFeedbackVO disposalFeedbackVO) {
        disposalFeedbackMapper.insertFeedback(disposalFeedbackVO);
    }

    @Override
    public DisposalFeedbackVO getFeedback(Long disposalHistoryId) {

        return disposalFeedbackMapper.findFeedbackByDisposalHistoryId(disposalHistoryId);
    }

    @Override
    public List<DisposalFeedbackDto> getAllFeedback() {
        return disposalFeedbackMapper.findAllFeedbackWithImg();
    }

    @Override
    public List<DisposalFeedbackDto> getFeedbackByMemberId(Long memberId) {
        return disposalFeedbackMapper.findMyFeedbackWithImg(memberId);
    }
}
