package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.dto.*;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 관리자페이지 Controller
 * 관리자페이지를 만들어 분리배출 관리, 나눔 관리, 회원 관리 연결하여 사용 용도
 * 작성자 : 방희경
 */

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
@Slf4j
public class AdminMainController {
    private final DisposalHistoryService disposalHistoryService;
    private final DisposalFeedbackService disposalFeedbackService;
    private final MemberService memberService;
    private final EnvService envService;
    private final NoticeService noticeService;


    @GetMapping("/main")
    public String adminMainPage(Model model, Criteria cri) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long memberId = userDetails.getMemberVO().getMemberId();

            log.info("memberid : {}", memberId);

            model.addAttribute("memberId", memberId); // Thymeleaf로 전달할 때
        } else {
            log.info("비회원 접근입니다.");
        }

        List<DisposalHistoryDto> adminDisposalHistory = disposalHistoryService.getAllHistory(cri);
        List<DisposalFeedbackDto> adminDisposalFeedback = disposalFeedbackService.getAllFeedback(cri);
        List<MemberVO> adminMember = memberService.getAllMembers(cri);
        List<EnvDto> adminEnv = envService.getList(cri);
        List<NoticeDto> adminNotice = noticeService.getList(cri);

        model.addAttribute("recentWasteRecords", adminDisposalHistory);
        model.addAttribute("recentSharingList", adminDisposalFeedback);
        model.addAttribute("recentUsers", adminMember);
        model.addAttribute("recentEnvTalks", adminEnv);
        model.addAttribute("recentNotices", adminNotice);



        /*int total = disposalHistoryService.getTotal(cri);
        model.addAttribute("disposalHistoryPage", new PageDto(cri, total));*/

        return "admin/adminMain";
    }
}
