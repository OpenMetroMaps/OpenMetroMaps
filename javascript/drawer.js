function Drawer(canvas) {

    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');

    this.draw = function() {
        this.ctx.lineWidth = 3;
        this.rect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.lineWidth = 1;
        this.rect(10, 10, this.canvas.width - 20, this.canvas.height - 20);
    }

    this.rect = function(x, y, width, height) {
        this.ctx.moveTo(x, y);
        this.ctx.lineTo(x + width, y);
        this.ctx.lineTo(x + width, y + height);
        this.ctx.lineTo(x, y + height);
        this.ctx.closePath();
        this.ctx.stroke();
    }

}
