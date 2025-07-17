package com.ecovery.controller;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.DisposalFeedbackDto;
import com.ecovery.service.DisposalFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/feedback")
public class DisposalFeedbackController {

    private final DisposalFeedbackService disposalFeedbackService;

    //오류신고 버튼 클릭시 disposalfeedback db에 저장 후 이미지 업로드 페이지로 전환
    @PostMapping("/report")
    public String feedbackReport(DisposalFeedbackVO disposalFeedbackVO) {

        DisposalFeedbackVO feedback = disposalFeedbackService.getFeedback(disposalFeedbackVO.getDisposalHistoryId());

        if(feedback == null) {
            disposalFeedbackService.saveFeedback(disposalFeedbackVO);
        }

        return "redirect:/disposal";
    }
    
    @GetMapping("/history")
    public void feedbackHistory(Model model) {
        List<DisposalFeedbackDto> disposalFeedbackHistory = disposalFeedbackService.getAllFeedback();

        model.addAttribute("disposalFeedbackHistory", disposalFeedbackHistory);
    }

    @GetMapping("/history/{memberId}")
    public void feedbackHistory(@PathVariable("memberId") Long memberId, Model model) {
        List<DisposalFeedbackDto> myFeedbackHistory = disposalFeedbackService.getFeedbackByMemberId(memberId);
        model.addAttribute("myFeedbackHistory", myFeedbackHistory);
    }
}
