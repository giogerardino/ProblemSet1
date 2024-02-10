import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel {
    private static final int CANVAS_WIDTH = 1280;
    private static final int CANVAS_HEIGHT = 720;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private List<Particle> particles;
    private List<Wall> walls;

    public Canvas() {
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    }

    public void addParticle(double x, double y, double angle, double velocity) {
        particles.add(new Particle(x, y, angle, velocity));
        repaint();
    }

    public void addWall(int x1, int y1, int x2, int y2) {
        walls.add(new Wall(x1, y1, x2, y2));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fill background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw particles
        for (Particle particle : particles) {
            // Draw particles
        }
        // Draw walls
        for (Wall wall : walls) {
            g.drawLine(wall.getX1(), wall.getY1(), wall.getX2(), wall.getY2());
        }
    }
}
