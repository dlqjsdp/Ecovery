package com.ecovery.controller;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.DisposalFeedbackDto;
import com.ecovery.service.DisposalFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/feedback")
public class DisposalFeedbackController {

    private final DisposalFeedbackService disposalFeedbackService;

    //오류신고 버튼 클릭시 disposalfeedback db에 저장 후 이미지 업로드 페이지로 전환
    @PostMapping("/report")
    @ResponseBody
    public String feedbackReport(DisposalFeedbackVO feedbackVO) {
        if (!disposalFeedbackService.isAlreadyReported(feedbackVO.getDisposalHistoryId())) {
            disposalFeedbackService.saveFeedback(feedbackVO);
        }
        log.info("DisposalFeedbackVO : {}", feedbackVO);
        log.info("feedbackVo : {}", feedbackVO.getMemberId());
        return "ok"; // 단순 문자열 응답
    }


    @GetMapping("/history")
    public String feedbackHistory(Model model) {
        List<DisposalFeedbackDto> adminFeedbackHistory = disposalFeedbackService.getAllFeedback();

        model.addAttribute("adminFeedbackHistory", adminFeedbackHistory);

        return "feedback/feedbackHistory";
    }

    @GetMapping("/history/{memberId}")
    public String feedbackHistoryOfMember(@PathVariable("memberId") Long memberId, Model model) {
        List<DisposalFeedbackDto> myFeedbackHistory = disposalFeedbackService.getFeedbackByMemberId(memberId);
        model.addAttribute("myFeedbackHistory", myFeedbackHistory);
        return "feedback/memberFeedbackHistory";
    }

    //리스트에서 목록 클릭시 오류 신고 상세 조회 페이지로 이동하는 코드
    @GetMapping("/detail/{disposalHistoryId}")
    public String feedbackDetail(@PathVariable("disposalHistoryId") Long disposalHistoryId, Model model) {
        DisposalFeedbackDto feedback = disposalFeedbackService.getFeedbackDetail(disposalHistoryId);
        model.addAttribute("feedback", feedback);
        return "feedback/detail"; // 예: detail.jsp
    }
}
