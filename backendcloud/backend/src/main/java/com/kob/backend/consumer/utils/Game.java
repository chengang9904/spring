package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {

    private Integer rows;
    private Integer columns;
    final private Integer inner_walls_count;
    final private int[][] g;
    private int[] dx = {-1, 0, 1, 0};
    private int[] dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private ReentrantLock lock = new ReentrantLock();

    private String status = "playing";    // "playing", "finished"
    private String loser = "";   // "all"， "A", "B"

    private static final String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows, Integer columns, Integer inner_walls_count, Integer idA, Integer idB, Bot botA, Bot botB) {
        this.rows = rows;
        this.columns = columns;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][columns];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, rows - 2, 1, new ArrayList<>(), botIdA, botCodeA);
        playerB = new Player(idB, 1, columns - 2, new ArrayList<>(), botIdB, botCodeB);
    }

    public int[][] getG() {
        return g;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA (Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB (Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i];
            int y = sy + dy[i];
            if (x >= 0 && x < rows && y >= 0 && y < columns && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                g[i][j] = 0;
            }
        }

        for (int i = 0; i < rows; i++) {
            g[i][0] = g[i][columns - 1] = 1;
        }

        for (int i = 0; i < columns; i++) {
            g[0][i] = g[rows - 1][i] = 1;
        }

        Random rand = new Random();
        for (int i = 0; i < inner_walls_count / 2; i++) {
            for (int j = 0; j < 100; j ++ ) {
                int r = rand.nextInt(rows);
                int c = rand.nextInt(columns);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.columns - 1 - c] == 1) {
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.columns - 2) {
                    continue;
                }

                g[r][c] = g[this.rows - 1 - r][this.columns - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(1, this.columns - 2, this.rows - 2, 1);

    }

    public void createMap() {
        for(int i = 0; i < 100; i ++ ){
            if (draw())
                break;
        }
    }

    private String getInput(Player player) {      //将当前的局面信息编程字符串
        Player me, you;
        if (Objects.equals(playerA.getId(), player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";

    }


    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return;  // 人亲自出马
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", player.getId().toString());
        data.add("botCode", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);
        // 等待两名玩家的下一步操作
        for (int i = 0; i < 100; i ++ ) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null) {
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        }
        if (WebSocketServer.users.get(playerB.getId()) != null) {
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
        }
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n - 1; i++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for (int i = 0; i < n - 1; i++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }

    private void judge() {       // 两名玩家是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else if (!validB) {
                loser = "B";
            }
        }
    }

    private String getMapString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(g[i][j]);
            }
        }
        return sb.toString();
    }

    private void saveToDatabase() {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() {    //向两个client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private void sendMove() {       // 向两个client发送移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {      // 新线程里的入口函数就是Run函数
        for(int i = 0; i < 1000; i ++ ) {
            if (nextStep()) {
                judge();

                if ("playing".equals(status)) {
                    sendMove();
                } else if ("finished".equals(status)) {
                    sendResult();
                    break;
                }
            } else {
                lock.lock();
                try {
                    status = "finished";
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else if (nextStepB == null) {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }

                sendResult();
                break;
            }
        }
    }
}
