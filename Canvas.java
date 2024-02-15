import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Canvas extends JPanel {
    final int width = 1280;
    final int height = 720;
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Wall> walls = new CopyOnWriteArrayList<>();
    private final JLabel fpsLabel;
    private int framesCounted = 0;
    private long lastFpsUpdateTime = System.nanoTime();
    private final BufferedImage offscreenImage;
    private final ForkJoinPool physicsThreadPool = new ForkJoinPool();
    private final ExecutorService renderingExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final double timeStep = 1.0 / 240.0;
    private final long time = 1000000000 / 60;

    public Canvas(JLabel fpsLabel) {
        this.fpsLabel = fpsLabel;
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
                updateParticles(this.timeStep);
                SwingUtilities.invokeLater(this::repaint);

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
        int batchSize = 10; // Experiment with different batch sizes
        List<List<Particle>> particleChunks = partition(particles, batchSize);

        physicsThreadPool.submit(() -> particleChunks.parallelStream().forEach(chunk ->
                chunk.forEach(particle -> {
                    particle.updatePosition(deltaTime);
                    particle.handleWallCollision(width, height, walls);
                })
        )).join();
    }

    private static <T> List<List<T>> partition(List<T> list, int size) {
        return IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.groupingBy(index -> index / size))
                .values()
                .stream()
                .map(indices -> indices.stream().map(list::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = offscreenImage.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        int batchSize = (renderingExecutor instanceof ThreadPoolExecutor) ?
                ((ThreadPoolExecutor) renderingExecutor).getMaximumPoolSize() : 1;

        List<Future<Void>> renderingTasks = new CopyOnWriteArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            int startIndex = i * (particles.size() / batchSize);
            int endIndex = (i == batchSize - 1) ? particles.size() : (i + 1) * (particles.size() / batchSize);

            List<Particle> particleChunk = particles.subList(startIndex, endIndex);

            renderingTasks.add(renderingExecutor.submit(() -> {
                particleChunk.parallelStream().forEachOrdered(particle -> {
                    int drawY = height - particle.y - 5;
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(particle.x, drawY, 5, 5);
                });

                return null;
            }));
        }

        renderingTasks.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        for (Wall wall : walls) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(wall.startX, height - wall.startY, wall.endX, height - wall.endY);
        }

        g2d.dispose();
        g.drawImage(offscreenImage, 0, 0, this);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width, height);

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
