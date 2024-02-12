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
    private static final int PARTICLE_SIZE = 5;
    private static final int FPS_UPDATE_INTERVAL = 500; // Interval in milliseconds for updating FPS
    private List<Particle> particles;
    private List<Wall> walls;
    private Timer timer;
    private int frameCount;
    private long lastFpsUpdateTime;

    public Canvas() {
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParticles();
                repaint();
                // Increment frame count
                frameCount++;
            }
        });

        frameCount = 0;
        lastFpsUpdateTime = System.currentTimeMillis();
        startSimulation();
    }

    public void startSimulation() {
        timer.start();
        startFpsCounter();
    }

    public void stopSimulation() {
        timer.stop();
    }

    private void updateParticles() {
        int particleSize = PARTICLE_SIZE; // Assuming PARTICLE_SIZE is defined somewhere in your code

        for (Particle particle : particles) {
            double angleInRadians = Math.toRadians(particle.getAngle());
            double velocityX = particle.getVelocity() * Math.cos(angleInRadians);
            double velocityY = particle.getVelocity() * Math.sin(angleInRadians);
            double newX = particle.getX() + velocityX;
            double newY = particle.getY() + velocityY;

            // Handle collisions with vertical walls
            if (newX - particleSize / 2 < 0 || newX + particleSize / 2 > CANVAS_WIDTH) {
                particle.setAngle(180 - particle.getAngle()); // Bounce off the vertical walls
            } else {
                particle.setX(newX);
            }

            // Handle collisions with horizontal walls
            if (newY - particleSize / 2 < 0 || newY + particleSize / 2 > CANVAS_HEIGHT) {
                particle.setAngle(-particle.getAngle()); // Bounce off the horizontal walls
            } else {
                particle.setY(newY);
            }

            // Handle collisions with walls
            for (Wall wall : walls) {
                if (isParticleCollidingWithWall(particle, wall)) {
                    // Handle collision with wall
                    handleWallCollision(particle, wall);
                }
            }
        }
    }

    private boolean isParticleCollidingWithWall(Particle particle, Wall wall) {
        // Use vector cross product to determine if particle is on one side of the wall or the other
        double crossProduct = (wall.getX2() - wall.getX1()) * (particle.getY() - wall.getY1()) -
                (wall.getY2() - wall.getY1()) * (particle.getX() - wall.getX1());
        return Math.abs(crossProduct) < 1e-6 && // Check if the particle is close enough to the wall
                particle.getX() >= Math.min(wall.getX1(), wall.getX2()) && // Check if particle is within wall bounds
                particle.getX() <= Math.max(wall.getX1(), wall.getX2()) &&
                particle.getY() >= Math.min(wall.getY1(), wall.getY2()) &&
                particle.getY() <= Math.max(wall.getY1(), wall.getY2());
    }

    private void handleWallCollision(Particle particle, Wall wall) {
        // Calculate the angle of reflection using the wall's normal vector
        double wallAngle = Math.atan2(wall.getY2() - wall.getY1(), wall.getX2() - wall.getX1());
        double incidentAngle = Math.atan2(Math.sin(Math.toRadians(particle.getAngle())),
                Math.cos(Math.toRadians(particle.getAngle())));
        double reflectionAngle = 2 * wallAngle - incidentAngle;

        // Set the new angle for the particle after reflection
        particle.setAngle(Math.toDegrees(reflectionAngle));
    }

    private void startFpsCounter() {
        Timer fpsTimer = new Timer(FPS_UPDATE_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double fps = (double) frameCount / (FPS_UPDATE_INTERVAL / 1000.0);
                System.out.println("FPS: " + fps);

                // Reset frame count
                frameCount = 0;
            }
        });
        fpsTimer.start();
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
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Fill background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(1, 1, getWidth() - 2, getHeight() - 2); // Fill within the outline

        // Draw particles
        g.setColor(Color.WHITE); // Set color for particles
        for (Particle particle : particles) {
            int particleSize = 5;
            int particleX = (int) Math.round(particle.getX());
            int particleY = (int) Math.round(particle.getY());
            g.fillOval(particleX - particleSize / 2, particleY - particleSize / 2, particleSize, particleSize);
        }

        // Draw walls
        g.setColor(Color.WHITE); // Set color for walls
        for (Wall wall : walls) {
            g.drawLine(wall.getX1(), wall.getY1(), wall.getX2(), wall.getY2());
        }
    }
}
