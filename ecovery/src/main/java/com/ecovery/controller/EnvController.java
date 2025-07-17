package com.ecovery.controller;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.PageDto;
import com.ecovery.service.EnvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/*
 * 환경톡톡 게시글 컨트롤러
 * 게시글 등록, 조회, 수정, 삭제, 목록 조회(페이징) 기능의 컨트롤러 역할을 수행
 * 클라이언트의 요청을 받아 서비스 계층(EnvService)과 상호작용하여 처리하고 View에 데이터를 전달
 * @author : yukyeong
 * @fileName : EnvController.java
 * @since : 250716
 * @history
     - 250716 | yukyeong | EnvController 클래스 최초 작성 (목록 조회)
 */

@Controller
@RequestMapping("/env")
@RequiredArgsConstructor
@Slf4j
public class EnvController {

    private final EnvService envService;

    // 목록 조회
    @GetMapping("/list")
    public void list(Criteria cri, Model model) {
        log.info("게시글 목록 조회 : {}", cri); // cri에 담긴 조건(페이지 번호, 검색어 등) 로그 출력

        List<EnvVO> list = envService.getList(cri); // 조건에 맞는 게시글 목록 조회
        int total = envService.getTotal(cri); // 조건에 맞는 게시글 전체 개수 조회

        model.addAttribute("list", list); // 조회된 게시글 목록을 모델에 담아 View로 전달
        model.addAttribute("pageMaker", new PageDto(cri, total)); // 페이지 정보(PageDto)를 모델에 담아 View로 전달
    }

    // 게시글 등록 폼 이동

    // 게시글 등록 처리

    // 게시글 조회 및 수정 화면

    // 게시글 삭제 처리

    // 게시글 수정 처리
}
