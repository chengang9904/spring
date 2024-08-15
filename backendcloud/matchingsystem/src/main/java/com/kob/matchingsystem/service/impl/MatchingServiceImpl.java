package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public static final MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating, Integer botId) {
        System.out.println("adding player with id " + userId + " and rating " + rating + " and botId " + botId);
        matchingPool.addPlayer(userId, rating, botId);
        return "adding player with id " + userId + " and rating " + rating;
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("removing player with id " + userId);
        matchingPool.removePlayer(userId);
        return "removing player with id " + userId;
    }
}
