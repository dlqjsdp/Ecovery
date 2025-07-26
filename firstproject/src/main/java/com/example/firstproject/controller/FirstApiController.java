package com.example.firstproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // REST API 사용 컨트롤러
public class FirstApiController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello World";
    }
}
