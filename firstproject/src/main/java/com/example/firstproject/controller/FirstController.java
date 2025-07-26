package com.example.firstproject.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FirstController {

    @GetMapping("/hi")
    public String hi(Model model) {
        model.addAttribute("username", "방희경");
        return "greetings";
    }

    @GetMapping("/bye")
    public String bye(Model model) {
        model.addAttribute("nickname", "방희경");
        return "goodbye";
    }
}
