package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receiveBotMove " + userId + " " + direction);
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;
            if (game != null) {
                if (Objects.equals(game.getPlayerA().getId(), userId)) {
                        game.setNextStepA(direction);
                } else if (Objects.equals(game.getPlayerB().getId(), userId)) {   // 为了防止写出bug做双重判断
                        game.setNextStepB(direction);
                }
            }
        }
        return "receiveBotMove success";
    }
}
