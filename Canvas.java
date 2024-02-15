import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class Canvas extends JPanel {
    final int width = 1280;
    final int height = 720;
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Wall> walls = new CopyOnWriteArrayList<>();
    // FPS tracking
    private final JLabel fpsLabel; // JLabel to display FPS
    private int framesCounted = 0;
    private long lastFpsUpdateTime = System.nanoTime(); // Time of the last FPS update
    private final BufferedImage offscreenImage;
    private final ForkJoinPool physicsThreadPool = new ForkJoinPool();
    private final ForkJoinPool renderingThreadPool = new ForkJoinPool();
    private final double timeStep = 1.0 / 240.0;
    private final long time = 1000000000 / 60; // where 1000000000 is nanoseconds and 60 is the target FPS

    public Canvas(JLabel fpsLabel) {
        this.fpsLabel = fpsLabel; // Initialize the FPS label
        setPreferredSize(new Dimension(width, height));
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public void startSimulation() {
        new Thread(() -> {
            while (true) {
                long startTime = System.nanoTime();
                updateParticles(this.timeStep); // Update particle physics
                SwingUtilities.invokeLater(this::repaint); // Repaint the canvas

                try {
                    long sleepTime = (this.time - (System.nanoTime() - startTime)) / 1000000;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void updateParticles(double deltaTime) {
        physicsThreadPool.invokeAll(particles.stream().map(particle -> (Callable<Void>)() -> {
            particle.updatePosition(deltaTime);
            particle.handleWallCollision(width, height, walls);
            return null;
        }).collect(Collectors.toList()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = offscreenImage.createGraphics();

        // Set the background color to black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Split the canvas into smaller regions
//        int numThreads = Runtime.getRuntime().availableProcessors(); // Get the number of available processors
        int numThreads = 12; // 12 logical cores ## ADJUST ACCORDING TO NUMBER OF LOGICAL CORES
        int regionHeight = height / numThreads;

        List<Callable<Void>> renderingTasks = new ArrayList<>(); // Use java.util.List here

        // Create rendering tasks for each thread
        for (int i = 0; i < numThreads; i++) {
            final int startY = i * regionHeight;
            final int endY = (i == numThreads - 1) ? height : (i + 1) * regionHeight;
            renderingTasks.add(() -> {
                renderRegion(g2d, startY, endY);
                return null;
            });
        }

        // Invoke all rendering tasks in parallel
        renderingThreadPool.invokeAll(renderingTasks);

        // Dispose of the graphics object
        g2d.dispose();
        g.drawImage(offscreenImage, 0, 0, this);

        // Draw the border for the designated area
        g.setColor(Color.BLACK); // Set border color
        g.drawRect(0, 0, width, height); // Draw border around the 1280x720 area

        updateFPS();
    }

    private void renderRegion(Graphics2D g, int startY, int endY) {
        for (Particle particle : particles) {
            int drawY = height - particle.y - 5; // Adjust for inverted y-coordinate
            if (drawY >= startY && drawY < endY) {
                // Set the particle color to white
                g.setColor(Color.WHITE);
                g.fillOval(particle.x, drawY, 5, 5);
            }
        }
    }

    private void updateFPS() {
        long currentTime = System.nanoTime();
        framesCounted++;
        if ((currentTime - lastFpsUpdateTime) >= 500_000_000L) {
            double elapsedTimeInSeconds = (currentTime - lastFpsUpdateTime) / 1_000_000_000.0;
            double fps = framesCounted / elapsedTimeInSeconds;
            fpsLabel.setText(String.format("              %.2f              ", fps));
            framesCounted = 0;
            lastFpsUpdateTime = currentTime;
        }
    }

    public void addParticlesBetweenPoints(int n, Point start, Point end, double angle, double velocity) {
        if (n <= 0) return; // No particles to add
        if (n == 1) {
            // Add a single particle at the start point
            addParticle(new Particle(start.x, start.y, angle, velocity));
            return;
        }
        for (int i = 0; i < n; i++) {
            double ratio = (double) i / (n - 1);
            int x = start.x + (int) ((end.x - start.x) * ratio);
            int y = start.y + (int) ((end.y - start.y) * ratio);
            addParticle(new Particle(x, y, angle, velocity));
        }
    }

    public void addParticlesVaryingAngles(int n, Point start, double startAngle, double endAngle, double velocity) {
        if (n <= 1) {
            addParticle(new Particle(start.x, start.y, startAngle, velocity));
            return;
        }

        for (int i = 0; i < n; i++) {
            double angleIncrement = (endAngle - startAngle) / (n - 1);
            double angle = startAngle + (angleIncrement * i);
            addParticle(new Particle(start.x, start.y, angle, velocity));
        }
    }

    public void addParticlesVaryingVelocities(int n, Point start, double angle, double startVelocity, double endVelocity) {
        if (n <= 1) {
            addParticle(new Particle(start.x, start.y, angle, startVelocity));
            return;
        }

        for (int i = 0; i < n; i++) {
            double velocityIncrement = (endVelocity - startVelocity) / (n - 1);
            double velocity = startVelocity + (velocityIncrement * i);
            addParticle(new Particle(start.x, start.y, angle, velocity));
        }
    }
}
