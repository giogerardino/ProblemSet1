import java.awt.*;

class Wall {
    int startX; // x-coordinate of the starting point
    int startY; // y-coordinate of the starting point
    int endX;   // x-coordinate of the ending point
    int endY;   // y-coordinate of the ending point

    public Wall(int x1, int y1, int x2, int y2) {
        this.startX = x1;
        this.startY = y1;
        this.endX = x2;
        this.endY = y2;
    }
}
