import java.awt.*;

class Wall {
    int startX; 
    int startY; 
    int endX;   
    int endY;   

    public Wall(int initX, int initY, int finalX, int finalY) {
        this.startX = initX;
        this.startY = initY;
        this.endX = finalX;
        this.endY = finalY;
    }
}
