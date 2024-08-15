package com.kob.matchingsystem.controller;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingServiceController {
    @Autowired
    private MatchingService matchingService;

    @PostMapping("/player/add/")
    public String addPlayer(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(params.getFirst("userId"));
        Integer rating = Integer.parseInt(params.getFirst("rating"));
        Integer botId = Integer.parseInt(params.getFirst("botId"));
        return matchingService.addPlayer(userId, rating, botId);
    }

    @PostMapping("/player/remove/")
    public String removePlayer(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(params.getFirst("userId"));
        return matchingService.removePlayer(userId);
    }
}
