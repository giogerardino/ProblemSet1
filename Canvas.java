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
    private final JLabel fps; //display FPS
    private int frameCtr = 0;
    private long updatedFps = System.nanoTime(); // Time of the last FPS update
    private final BufferedImage offscreenImage;
    private final double timeStep = 1.0 / 240.0;
    private final long time = 1000000000 / 60; // where 1000000000 is nanoseconds and 60 is the target FPS
    private final Object particlesLock = new Object();

    public Canvas(JLabel fps) {
        this.fps = fps;
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
        int threadCount = Runtime.getRuntime().availableProcessors(); // use available processors // default

        synchronized (particlesLock) {
            int particlesPerThread = particles.size() / threadCount;

            List<Particle> shuffledParticles = new ArrayList<>(particles);
            Collections.shuffle(shuffledParticles); // shuffle particles for better load balancing

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
                allOf.get(); // wait for all particle updates to complete
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = offscreenImage.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Load balancing for rendering particles
        synchronized (particlesLock) {
            AtomicInteger counter = new AtomicInteger(0);

            particles.parallelStream().forEach(particle -> {
                int drawY = height - particle.y - 5;
                g2d.setColor(Color.WHITE);
                g2d.fillOval(particle.x, drawY, 5, 5);

                counter.incrementAndGet();
            });
        }

        for (Wall wall : walls) {
            g2d.setColor(Color.YELLOW);
            g2d.drawLine(wall.x1, height - wall.y1, wall.x2, height - wall.y2);
        }

        g2d.dispose();
        g.drawImage(offscreenImage, 0, 0, this);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width, height);

        updateFPS();
    }

    private void updateFPS() {
        long currentTime = System.nanoTime();
        frameCtr++;
        if ((currentTime - updatedFps) >= 500_000_000L) {
            double elapsedTimeInSeconds = (currentTime - updatedFps) / 1_000_000_000.0;
            double fps = frameCtr / elapsedTimeInSeconds;
            this.fps.setText(String.format("              %.2f              ", fps));
            frameCtr = 0;
            updatedFps = currentTime;
        }
    }

    /***
     * Provide an integer n indicating the number of particles to
     * add. Keep the velocity and angle constant. Provide a start
     * point and end point. Particles are added with a uniform
     * distance between the given start and end points.
     */
    public void particlesCase1(int n, int x1, int y1, int x2, int y2, double angle, double velocity) {
        if (n <= 0) return; // no particles to add

        // add a single particle at the start point if only one particle is requested
        if (n == 1) {
            addParticle(new Particle(x1, y1, angle, velocity));
            return;
        }

        // calculate the increment for x and y coordinates
        double deltaX = (double) (x2 - x1) / (n - 1);
        double deltaY = (double) (y2 - y1) / (n - 1);

        // Add particles at evenly spaced intervals along the line segment
        for (int i = 0; i < n; i++) {
            int x = (int) (x1 + i * deltaX);
            int y = (int) (y1 + i * deltaY);
            addParticle(new Particle(x, y, angle, velocity));
        }
    }

    /***
     * Provide an integer n indicating the number of particles to
     * add. Keep the start point and velocity constant. Provide a
     * start Θ and end Θ. Particles are added with uniform distance
     * between the given start Θ and end Θ.
     */
    public void particlesCase2(int n, int x, int y, double angle1, double angle2, double velocity) {
        if (n <= 1) {
            addParticle(new Particle(x, y, angle1, velocity));
        } else {
            // calculate the angle increment
            double angleIncrement = (angle2 - angle1) / (n - 1);

            // Add particles with incremented angles
            for (int i = 0; i < n; i++) {
                double angle = angle1 + (angleIncrement * i);
                addParticle(new Particle(x, y, angle, velocity));
            }
        }
    }

    /***
     * Provide an integer n indicating the number of particles to
     * add. Keep the start point and angle constant. Provide a start
     * velocity and end velocity. Particles are added with a uniform
     * difference between the given start and end velocities.
     */
    public void particlesCase3(int n, int x, int y, double angle, double vel1, double vel2) {
        if (n > 1) { // calculate the velocity increment
            double velocityIncrement = (vel2 - vel1) / (n - 1);

            // Add particles with incremented velocities
            for (int i = 0; i < n; i++) {
                double velocity = vel1 + (velocityIncrement * i);
                addParticle(new Particle(x, y, angle, velocity));
            }
        } else {
            addParticle(new Particle(x, y, angle, vel1));
        }
    }
}
