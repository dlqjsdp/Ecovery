package com.ecovery.controller;

import com.ecovery.dto.DisposalHistoryDto;
import com.ecovery.service.DisposalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/disposal")
public class DisposalHistoryController {

    private final DisposalHistoryService disposalHistoryService;

    @GetMapping("/disposalMain")
    public String disposalMain() {
        return "/disposal/disposalMain";
    }

    @GetMapping("/{disposalHistoryId}")
    public String disposalHistory(@PathVariable("disposalHistoryId") Long disposalHistoryId, Model model) {
        //DisposalHistoryDto disposalHistory = disposalHistoryService.getHistory(disposalHistoryId);
        //model.addAttribute("disposalHistory", disposalHistory);
        
        //view 확인용 더미 데이터
        DisposalHistoryDto dummy = DisposalHistoryDto.builder()
                .disposalHistoryId(disposalHistoryId)
                .aiPrediction("small_item")
                .regionGu("강남구")
                .createdAt("2025-07-20")
                .finalItem("책꽂이")
                .itemName("2단 책꽂이")
                .minPrice("2000")
                .maxPrice("5000")
                .reportUrl("https://example.com/report")
                .disposalImgUrl("https://via.placeholder.com/400x300.png?text=Sample+Image") // 테스트용 이미지
                .build();

        model.addAttribute("disposalHistory", dummy);
        return "/disposal/disposalResult";
    }

    @GetMapping("/history")
    public String disposalHistoryList(Model model) {
        List<DisposalHistoryDto> adminDisposalHistory = disposalHistoryService.getAllHistory();
        model.addAttribute("adminHistory", adminDisposalHistory);
        return "/disposal/disposalHistory";
    }

    @GetMapping("/history/{memberId}")
    public String disposalHistoryOfMember(@PathVariable("memberId") Long memberId, Model model) {
        List<DisposalHistoryDto> myDisposalHistory = disposalHistoryService.getHistoryByMemberId(memberId);
        model.addAttribute("myDisposalHistory", myDisposalHistory);
        return "/disposal/memberDisposalHistory";
    }
}
