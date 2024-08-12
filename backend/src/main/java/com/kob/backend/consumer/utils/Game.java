package com.kob.backend.consumer.utils;

import java.util.Random;

public class Game {

    private Integer rows;
    private Integer columns;
    final private Integer inner_walls_count;
    final private int[][] g;
    private int[] dx = {-1, 0, 1, 0};
    private int[] dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer columns, Integer inner_walls_count) {
        this.rows = rows;
        this.columns = columns;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][columns];
    }

    public int[][] getG() {
        return g;
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
}
