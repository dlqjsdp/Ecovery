package com.ecovery.service;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.DisposalFeedbackDto;
import com.ecovery.mapper.DisposalFeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public DisposalFeedbackVO getFeedback(Long disposalHistoryId) { // 이 메서드명과 반환 타입이 혼란을 줄 수 있음.
        // 메서드명을 isFeedbackExists 등으로 변경하고 반환 타입을 boolean으로 바꾸는 것을 권장합니다.
        int count = disposalFeedbackMapper.findFeedbackByDisposalHistoryId(disposalHistoryId);
        if (count > 0) {
            return null; // 또는 특정 로직 수행
        }
        return null; // 신고 이력이 없다는 의미
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
