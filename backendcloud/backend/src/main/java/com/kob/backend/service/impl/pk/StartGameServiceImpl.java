package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer userIdA, Integer userIdB, Integer botIdA, Integer botIdB) {
        System.out.println("startGame" + userIdA + ' ' + userIdB);
        WebSocketServer.startGame(userIdA, userIdB, botIdA, botIdB);
        return "start game success";
    }
}
