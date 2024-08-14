package com.kob.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/pk/")
    public String index() {
        return "pk/index.html";
    }
}
