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
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getAngle() {
        return angle;
    }
    public double getVelocity() {
        return velocity;
    }
}
