import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel {
    private List<Particle> particles;
    private List<Wall> walls;

    public Canvas() {
        particles = new ArrayList<>();
        walls = new ArrayList<>();
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
        // Draw particles
        for (Particle particle : particles) {
            // Draw particles
        }
        // Draw walls
        for (Wall wall : walls) {
            g.drawLine(wall.x1, wall.y1, wall.x2, wall.y2);
        }
    }
}
