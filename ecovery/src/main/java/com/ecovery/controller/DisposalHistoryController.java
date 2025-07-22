package com.ecovery.controller;

import com.ecovery.dto.DisposalHistoryDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.DisposalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String disposalMain(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long memberId = userDetails.getMemberVO().getMemberId();

            log.info("memberid : {}", memberId);

            model.addAttribute("memberId", memberId); // Thymeleaf로 전달할 때
        } else {
            log.info("비회원 접근입니다.");
        }

        return "/disposal/disposalMain";
    }

    @GetMapping("/{disposalHistoryId}")
    public String disposalHistory(@PathVariable("disposalHistoryId") Long disposalHistoryId, Model model) {
        DisposalHistoryDto disposalHistory = disposalHistoryService.getHistory(disposalHistoryId);
        log.info("controller : {}", disposalHistory.toString());
        model.addAttribute("disposalHistory", disposalHistory);

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
