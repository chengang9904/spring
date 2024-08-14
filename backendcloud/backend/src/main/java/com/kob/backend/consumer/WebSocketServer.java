package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAutentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static final ConcurrentHashMap<Integer, WebSocketServer> users =
            new ConcurrentHashMap<>();
    private User user;
    private Session session;

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;
    private Game game = null;

    private static final String addPlayerUrl = "http://localhost:3001/player/add/";
    private static final String removePlayerUrl = "http://localhost:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("connected");
        Integer userId = JwtAutentication.getUserId(token);
        User user = userMapper.selectById(userId);
        this.user = user;

        if (this.user != null) {
            users.put(userId, this);
            System.out.println("users.put");
        } else {
            System.out.println("null");
            this.session.close();
        }
        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        System.out.println("disconnected");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer a_id, Integer b_id) {
        User a = userMapper.selectById(a_id);
        User b = userMapper.selectById(b_id);

        Game game = new Game(13,14, 20, a.getId(), b.getId());
        game.createMap();

        if (users.get(a.getId()) != null) {
            users.get(a.getId()).game = game;
        }
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }
        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        JSONObject respB = new JSONObject();
        respA.put("event", "start-matching");
        respB.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respA.put("game", respGame);
        respB.put("game", respGame);
        if(users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }
        if(users.get(b.getId()) != null) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void startMatching() {
        System.out.println("start matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    private void stopMatching() {
        System.out.println("stop matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        if (Objects.equals(game.getPlayerA().getId(), user.getId())) {
            game.setNextStepA(direction);
        } else if (Objects.equals(game.getPlayerB().getId(), user.getId())) {   // 为了防止写出bug做双重判断
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {       //当作路由
        System.out.println("received message");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            System.out.println("received move");
            move(data.getInteger("direction"));
        };
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}