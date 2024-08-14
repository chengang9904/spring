package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<Player>();    // 有读写冲突
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;

    private static final String startGameUrl = "http://localhost:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) { MatchingPool.restTemplate = restTemplate; }

    public void addPlayer(Integer userId, Integer rating) {
        lock.lock();
        try {
            players.add(new Player(
                    userId,
                    rating,
                    0
            ));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            players.removeIf(player -> player.getUserId().equals(userId));
        } finally {
            lock.unlock();
        }
    }

    private void increasingWaitingtime() {     //将所有当前玩家的等待时间加1
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatch(Player a, Player b) {      // 判断两名玩家是否能匹配上
        int ratinga = a.getRating();
        int ratingb = b.getRating();
        int ratingdelta = Math.abs(ratinga - ratingb);
        int waitingtimea = a.getWaitingTime();
        int waitingtimeb = b.getWaitingTime();
        return Math.min(waitingtimea, waitingtimeb) * 10 >= ratingdelta;
    }

    private void sendResult(Player a, Player b) {     // 将两个玩家匹配的消息发给backend
        MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
        data.add("userIdA", String.valueOf(a.getUserId()));
        data.add("userIdB", String.valueOf(b.getUserId()));
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() {      //尝试匹配所有玩家
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (used[i]) continue;
            for (int j = i + 1; j < players.size(); j++) {
                if (used[j]) continue;
                Player a = players.get(i);
                Player b = players.get(j);
                if (checkMatch(a, b)) {
                    used[i] = true;
                    used[j] = true;
                    System.out.println("Player " + a.getUserId() + " matched " + b.getUserId());
                    sendResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (!used[i]) newPlayers.add(players.get(i));
        }

        players = newPlayers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increasingWaitingtime();
                    matchPlayers();
                    System.out.println(players);
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
