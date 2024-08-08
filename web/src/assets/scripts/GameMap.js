import { AcGameObjects } from "./AcGameObjects";
import { Wall } from "./Wall";
import { Snake } from "./Snake";

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

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ]
    }

    check_connectivity(g, sx, sy, ex, ey) {
        if (sx == ex && sy == ey) return true;

        g[sx][sy] = true;
        const dx = [1, 0, -1, 0];
        const dy = [0, 1, 0, -1];

        for (let i = 0; i < 4; i++) {
            let x = dx[i] + sx, y = dy[i] + sy;
            if (!g[x][y] && this.check_connectivity(g, x, y, ex, ey))
                return true;
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
            for (let j = 0; j < 100; j++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c])
                    continue;
                if ((r == 1 && c == this.cols - 2) || (r == this.rows - 2 && c == 1))
                    continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, 1, this.cols - 2, this.rows - 2, 1))
            return false;

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c])
                    this.walls.push(new Wall(r, c, this));
            }
        }

        return true;
    }

    start() {
        for (let i = 0; i < 100; i++)
            if (this.create_walls())
                break;
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.rows, this.parent.clientHeight / this.cols));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size();
        this.render();
    }

    render() {
        const color_even = "#AAD781", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}