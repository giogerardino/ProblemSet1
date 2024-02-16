/**
 * STDISCM S11
 * Gabriel Angelo M. Gerardino
 * Jaira Millicent M. Santos
 */

 import java.awt.*;
 import javax.swing.*;
 import java.awt.image.BufferedImage;
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.concurrent.*;
 import java.util.concurrent.atomic.AtomicInteger;
 
 class Canvas extends JPanel {
     // Canvas dimensions
     final int CANVAS_WIDTH = 1280;
     final int CANVAS_HEIGHT = 720;
     
     // Offscreen image for double buffering
     private final BufferedImage offscreenImage;
     
     // FPS-related variables
     private final JLabel fps; // Display FPS
     private int frameCtr = 0;
     private long updatedFpsTime = System.nanoTime(); 
     
     // Time-related constants
     private final double timeStep = 1.0 / 240.0;
     private final long time = 1000000000 / 60; // 60 FPS target
     private static final long FPS_UPDATE_INTERVAL = 500_000_000L; // 500 ms in nanoseconds
 
     // Particle and Wall containers
     private final List<Particle> particles = new ArrayList<>();
     private final CopyOnWriteArrayList<Wall> walls = new CopyOnWriteArrayList<>();
     private final Object particlesLock = new Object();
 
     // Constructor
     public Canvas(JLabel fps) {
         this.fps = fps;
         setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
         offscreenImage = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
     }
 
     // Add particle to the list
     public void addParticle(Particle particle) {
         synchronized (particlesLock) {
             particles.add(particle);
         }
     }
     
     // Add wall to the list
     public void addWall(Wall wall) {
         walls.add(wall);
     }
     
     // Start the simulation
     public void startSimulation() {
         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
         scheduler.scheduleAtFixedRate(this::updateParticles, 0, time, TimeUnit.NANOSECONDS);
         scheduler.scheduleAtFixedRate(this::repaint, 0, time, TimeUnit.NANOSECONDS);
     }
 
     // Update particle positions and handle collisions
     private void updateParticles() {
         int threadCount = Runtime.getRuntime().availableProcessors(); // Use available processors
 
         synchronized (particlesLock) {
             int particlesPerThread = particles.size() / threadCount;
 
             // Shuffle particles for better load balancing
             List<Particle> shuffledParticles = new ArrayList<>(particles);
             Collections.shuffle(shuffledParticles); 
 
             // Partition particles into batches for parallel processing
             List<List<Particle>> particleBatches = new ArrayList<>();
 
             for (int i = 0; i < threadCount; i++) {
                 int startIndex = i * particlesPerThread;
                 int endIndex = (i == threadCount - 1) ? particles.size() : (i + 1) * particlesPerThread;
                 particleBatches.add(shuffledParticles.subList(startIndex, endIndex));
             }
 
             // Use CompletableFuture for asynchronous processing
             List<CompletableFuture<Void>> futures = new ArrayList<>();
 
             for (List<Particle> particleBatch : particleBatches) {
                 futures.add(CompletableFuture.runAsync(() -> {
                     for (Particle particle : particleBatch) {
                         particle.updatePosition(timeStep);
                         particle.handleWallCollision(CANVAS_WIDTH, CANVAS_HEIGHT, walls);
                     }
                 }));
             }
 
             // Wait for all particle updates to complete
             CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
 
             try {
                 allOf.get();
             } catch (InterruptedException | ExecutionException e) {
                 e.printStackTrace();
             }
         }
     }
     
     // Render particles and walls on the canvas
     @Override
     protected void paintComponent(Graphics g) {
         super.paintComponent(g);
 
         Graphics2D g2d = offscreenImage.createGraphics();
         g2d.setColor(Color.BLACK);
         g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
 
         // Load balancing for rendering particles
         synchronized (particlesLock) {
             AtomicInteger counter = new AtomicInteger(0);
 
             particles.parallelStream().forEach(particle -> {
                 int drawY = CANVAS_HEIGHT - particle.y - 5;
                 g2d.setColor(Color.WHITE);
                 g2d.fillOval(particle.x, drawY, 5, 5);
 
                 counter.incrementAndGet();
             });
         }
 
         // Render walls
         for (Wall wall : walls) {
             g2d.setColor(Color.YELLOW);
             g2d.drawLine(wall.x1, CANVAS_HEIGHT - wall.y1, wall.x2, CANVAS_HEIGHT - wall.y2);
         }
 
         g2d.dispose();
         g.drawImage(offscreenImage, 0, 0, this);
 
         // Draw canvas border
         g.setColor(Color.BLACK);
         g.drawRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
 
         // Update and display FPS
         updateFPS();
     }
 
     // Update and display FPS
     private void updateFPS() {
         long currTime = System.nanoTime();
         frameCtr++;
 
         long elapsedTime = currTime - updatedFpsTime;
 
         // Check if the update interval has passed
         if (elapsedTime >= FPS_UPDATE_INTERVAL) {
 
             // Calculate frames per second
             double elapsedTimeInSeconds = elapsedTime / 1_000_000_000.0;
             double currentFPS = frameCtr / elapsedTimeInSeconds;
 
             // Update FPS label with formatted text
             updateFPSLabel(currentFPS);
 
             // Reset counters and update time
             resetCounters(currTime);
         }
     }
 
     // Update FPS label
     private void updateFPSLabel(double fpsValue) {
         fps.setText(String.format("              %.2f              ", fpsValue));
     }
 
     // Reset FPS counters
     private void resetCounters(long currTime) {
         frameCtr = 0;
         updatedFpsTime = currTime;
     }
 
     /***
      * Provide an integer n indicating the number of particles to
      * add. Keep the velocity and angle constant. Provide a start
      * point and end point. Particles are added with a uniform
      * distance between the given start and end points.
      */
     public void particlesCase1(int n, int x1, int y1, int x2, int y2, double angle, double velocity) {
         if (n <= 0) return; // No particles to add
 
         // Add a single particle at the start point if only one particle is requested
         if (n == 1) {
             addParticle(new Particle(x1, y1, angle, velocity));
             return;
         }
 
         // Calculate the increment for x and y coordinates
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
             // Calculate the angle increment
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
         if (n > 1) { // Calculate the velocity increment
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
 