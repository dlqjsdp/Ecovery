package com.ecovery.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String adminMainPage() {
        return "admin/main";
    }
}
