import { AcGameObjects } from "./AcGameObjects"
import { Wall } from "./Wall";

export class GameMap extends AcGameObjects {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;

        this.rows = 13;
        this.cols = 13;

        this.walls = [];
        this.inner_walls_count = 20;
    }

    check_conectivity(g, sx, sy, ex, ey) {
        if (sx == ex && sy == ey)
            return true;

        g[sx][sy] = true;

        let dx = [1, 0, -1, 0];
        let dy = [0, 1, 0, -1];

        for (let i = 0; i < 4; i++) {
            let x = dx[i] + sx, y = dy[i] + sy;
            if (!g[x][y] && this.check_conectivity(g, x, y, ex, ey)) {
                return true;
            }
        }

        return false;
    }

    create_walls() {
        let g = [];
        for (let r = 0; r < this.rows; r++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c++) {
                g[r][c] = false;
            }
        }

        for (let r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = true;
        }

        for (let c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = true;
        }

        for (let i = 0; i < this.inner_walls_count / 2; i++) {
            for (let j = 0; j < 1000; j++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c]) {
                    continue;
                }
                if (r == 1 && c == this.cols - 2 || r == this.rows - 2 && c == 1) {
                    continue;
                }
                g[r][c] = g[c][r] = true;
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));             //拷贝
        if (!this.check_conectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2))
            return false;

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        return true;
    }

    start() {
        for (let i = 0; i < 100; i++) {
            if (this.create_walls())
                break;
        }
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size();
        this.render();
    }

    render() {
        let color_even = "#AAD781", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((c + r) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }

                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}

