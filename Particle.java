public class Particle {
    private double x, y; // coordinates
    private double angle; // in degrees
    private double velocity; // in pixels per second

    public Particle(double x, double y, double angle, double velocity) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
    }
}
