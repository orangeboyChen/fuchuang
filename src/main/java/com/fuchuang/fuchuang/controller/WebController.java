package com.fuchuang.fuchuang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class WebController {

    @GetMapping("/")
    public String get(){
        return "forward:/index.html";
    }

}
