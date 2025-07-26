package com.ecovery.mapper;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.DisposalFeedbackDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 대형폐기물 분류 피드백 테이블(disposal_feedback)
 * @author : jihye Lee
 * @fileName : DisposalFeedbackMapper
 * @since : 20250714
 */
@Mapper
public interface DisposalFeedbackMapper {

    //오류 신고 내역 저장
    public void insertFeedback(DisposalFeedbackVO disposalFeedbackVO);

    // 특정 disposal_history_id로 신고 여부 확인(중복 신고 방지)
    // 매퍼 XML의 COUNT(*) 결과값을 int로 받음
    public int countByDisposalHistoryId(Long disposalHistoryId);

    // (2) 상세 조회용 - 단건 조회
    public DisposalFeedbackDto selectDetailByDisposalHistoryId(Long disposalHistoryId);

    //관리자용 전체 신고 내역 조회
    public List<DisposalFeedbackDto> findAllFeedbackWithImg();

    //회원용 신고 내역 조회
    public List<DisposalFeedbackDto> findMyFeedbackWithImg(Long memberId);
}