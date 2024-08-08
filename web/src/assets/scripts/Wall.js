import { AcGameObjects } from "./AcGameObjects";

export class Wall extends AcGameObjects {
    constructor(r, c, gamemap) {
        super();

        this.r = r;
        this.c = c;
        this.color = "#b37226";
        this.gamemap = gamemap;
    }

    start() {

    }

    update() {
        this.render()
    }

    render() {
        const L = this.gamemap.L;         //动态取
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c * L, this.r * L, L, L);
    }
}

