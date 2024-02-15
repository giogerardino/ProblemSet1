import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


class Canvas extends JPanel {
    final int width = 1280;
    final int height = 720;
    private final List<Particle> particles = new ArrayList<>();
    private final CopyOnWriteArrayList<Wall> walls = new CopyOnWriteArrayList<>();
    private final JLabel fpsLabel; // JLabel to display FPS
    private int framesCounted = 0;
    private long lastFpsUpdateTime = System.nanoTime(); // Time of the last FPS update
    private final BufferedImage offscreenImage;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(8); // Adjust the number of threads as needed
    private final double timeStep = 1.0 / 240.0;
    private final long time = 1000000000 / 60; // where 1000000000 is nanoseconds and 60 is the target FPS
    private final Object particlesLock = new Object();

    public Canvas(JLabel fpsLabel) {
        this.fpsLabel = fpsLabel; // Initialize the FPS label
        setPreferredSize(new Dimension(width, height));
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void addParticle(Particle particle) {
        synchronized (particlesLock) {
            particles.add(particle);
        }
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public void startSimulation() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(this::updateParticles, 0, time, TimeUnit.NANOSECONDS);
        scheduler.scheduleAtFixedRate(this::repaint, 0, time, TimeUnit.NANOSECONDS);
    }

    private void updateParticles() {
        int threadCount = Runtime.getRuntime().availableProcessors(); // Use available processors

        synchronized (particlesLock) {
            int particlesPerThread = particles.size() / threadCount;

            List<Particle> shuffledParticles = new ArrayList<>(particles);
            Collections.shuffle(shuffledParticles); // Shuffle particles for better load balancing

            List<List<Particle>> particleBatches = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                int startIndex = i * particlesPerThread;
                int endIndex = (i == threadCount - 1) ? particles.size() : (i + 1) * particlesPerThread;
                particleBatches.add(shuffledParticles.subList(startIndex, endIndex));
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (List<Particle> particleBatch : particleBatches) {
                futures.add(CompletableFuture.runAsync(() -> {
                    for (Particle particle : particleBatch) {
                        particle.updatePosition(timeStep);
                        particle.handleWallCollision(width, height, walls);
                    }
                }));
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            try {
                allOf.get(); // Wait for all particle updates to complete
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = offscreenImage.createGraphics();

        // Set the background color to black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Load balancing for rendering particles
        synchronized (particlesLock) {
            AtomicInteger counter = new AtomicInteger(0);

            particles.parallelStream().forEach(particle -> {
                int drawY = height - particle.y - 5; // Adjust for inverted y-coordinate

                // Set the particle color to white
                g2d.setColor(Color.WHITE);
                g2d.fillOval(particle.x, drawY, 5, 5);

                counter.incrementAndGet();
            });
        }

        for (Wall wall : walls) {
            g2d.setColor(Color.YELLOW);
            g2d.drawLine(wall.startX, height - wall.startY, wall.endX, height - wall.endY);
        }

        g2d.dispose();
        g.drawImage(offscreenImage, 0, 0, this);

        // Draw the border for the designated area
        g.setColor(Color.BLACK); // Set border color
        g.drawRect(0, 0, width, height); // Draw border around the 1280x720 area

        updateFPS();
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
