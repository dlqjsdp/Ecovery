package com.ecovery.controller;

import com.ecovery.dto.DisposalHistoryDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.DisposalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    // 새롭게 추가할 JSON API 엔드포인트
    // URL: /disposal/api/{disposalHistoryId}
    @GetMapping("/api/{disposalHistoryId}") // 새로운 API 경로 설정 (기존 HTML 뷰와 충돌 방지)
    @ResponseBody // 이 메서드가 HTTP 응답 본문에 직접 데이터를 직렬화하여 반환함을 명시 (JSON)
    public ResponseEntity<DisposalHistoryDto> getDisposalHistoryApiDetail(@PathVariable("disposalHistoryId") Long disposalHistoryId) {
        log.info("API 요청: ID {}에 대한 분리배출 상세 내역 조회", disposalHistoryId);

        DisposalHistoryDto disposalHistory = disposalHistoryService.getHistory(disposalHistoryId);

        if (disposalHistory != null) {
            log.debug("ID {}의 데이터 조회 성공: {}", disposalHistoryId, disposalHistory);
            return ResponseEntity.ok(disposalHistory); // 200 OK와 함께 JSON 데이터 반환
        } else {
            log.warn("ID {}에 해당하는 분리배출 내역을 찾을 수 없습니다.", disposalHistoryId);
            return ResponseEntity.notFound().build(); // 404 Not Found 반환
        }
    }
}
