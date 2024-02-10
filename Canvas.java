import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel {
    private static final int CANVAS_WIDTH = 1280;
    private static final int CANVAS_HEIGHT = 720;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final int TIMER_DELAY = 1000 / 60;
    private List<Particle> particles;
    private List<Wall> walls;
    private Timer timer;
    public Canvas() {
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        // Initialize timer for simulation loop
        timer = new Timer(TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParticles();
                repaint();
            }
        });
    }
    public void startSimulation() {
        timer.start();
    }
    public void stopSimulation() {
        timer.stop();
    }
    private void updateParticles() {
        for (Particle particle : particles) {
            double angleInRadians = Math.toRadians(particle.getAngle());
            double velocityX = particle.getVelocity() * Math.cos(angleInRadians);
            double velocityY = particle.getVelocity() * Math.sin(angleInRadians);
            double newX = particle.getX() + velocityX;
            double newY = particle.getY() + velocityY;

            if (newX < 0 || newX > CANVAS_WIDTH) {
                particle.setAngle(180 - particle.getAngle()); // Bounce off the horizontal walls
            } else {
                particle.setX(newX);
            }
            if (newY < 0 || newY > CANVAS_HEIGHT) {
                particle.setAngle(-particle.getAngle()); // Bounce off the vertical walls
            } else {
                particle.setY(newY);
            }
        }
    }

    public void addParticle(double x, double y, double angle, double velocity) {
        particles.add(new Particle(x, y, angle, velocity));
        if (!timer.isRunning()) {
            startSimulation(); // Start simulation if not already running
        }
    }
    public void addWall(int x1, int y1, int x2, int y2) {
        walls.add(new Wall(x1, y1, x2, y2));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Fill background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(1, 1, getWidth() - 2, getHeight() - 2); // Fill within the outline

        // Draw particles
        g.setColor(Color.WHITE); // Set color for particles
        for (Particle particle : particles) {
            int particleSize = 5; // Adjust particle size as needed
            int particleX = (int) Math.round(particle.getX());
            int particleY = (int) Math.round(particle.getY());
            g.fillOval(particleX - particleSize / 2, particleY - particleSize / 2, particleSize, particleSize);
        }

        // Draw walls
        g.setColor(Color.RED); // Set color for walls
        for (Wall wall : walls) {
            g.drawLine(wall.getX1(), wall.getY1(), wall.getX2(), wall.getY2());
        }
    }
}
