package com.kob.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {

    @RequestMapping("getbotinfo/")
    public Map<String, String> gitBotInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "tiger");
        map.put("version", "1.0.0");
        return map;
    }
}
