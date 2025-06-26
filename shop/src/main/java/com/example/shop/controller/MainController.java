package com.example.shop.controller;

//메인화면 컨트롤러(회원가입 후 메인 페이지로 갈 수 있도록 작성)

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = "/")
    public String main() {
        return "main";
    }
}
