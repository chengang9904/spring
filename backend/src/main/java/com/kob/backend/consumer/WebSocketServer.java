package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAutentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    private static final ConcurrentHashMap<Integer, WebSocketServer> users =
            new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<User> matchpool =
            new CopyOnWriteArraySet<>();
    private User user;
    private Session session;

    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
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
            matchpool.remove(this.user);
        }
    }

    private void startmatching() {
        System.out.println("startmatching");
        matchpool.add(this.user);

        while (matchpool.size() >= 2) {
            Iterator<User> iterator = matchpool.iterator();
            User a = iterator.next(), b = iterator.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13,14, 20);
            game.createMap();

            JSONObject respA = new JSONObject();
            JSONObject respB = new JSONObject();
            respA.put("event", "start-matching");
            respB.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respA.put("gamemap", game.getG());
            respB.put("gamemap", game.getG());
            users.get(a.getId()).sendMessage(respA.toJSONString());
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopmatching() {
        System.out.println("stopmatching");
        matchpool.remove(this.user);
    }

    @OnMessage
    public void onMessage(String message, Session session) {       //当作路由
        System.out.println("received message");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startmatching();
        } else if ("stop-matching".equals(event)) {
            stopmatching();
        }
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