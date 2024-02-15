import java.awt.*;

class Wall {
    int startPointX; // x-coordinate of the starting point
    int startPointY; // y-coordinate of the starting point
    int endPointX;   // x-coordinate of the ending point
    int endPointY;   // y-coordinate of the ending point

    public Wall(int startX, int startY, int endX, int endY) {
        this.startPointX = startX;
        this.startPointY = startY;
        this.endPointX = endX;
        this.endPointY = endY;
    }
}
