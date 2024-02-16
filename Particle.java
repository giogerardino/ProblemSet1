import java.util.concurrent.CopyOnWriteArrayList;

class Particle {
    // Coordinates
    int x; // x-coordinate
    int y; // y-coordinate

    // Angle (degrees) and velocity (pixels / s)
    double currentAngle; 
    double currentVelocity; 

    // Cumulative shifts
    double cumulativeShiftX = 0.0; 
    double cumulativeShiftY = 0.0; 

    public Particle(int initialX, int initialY, double initialAngle, double initialVelocity) {
        x = initialX;
        y = initialY;
        currentAngle = initialAngle;
        currentVelocity = initialVelocity;
    }

    // updates the particle's position based on its velocity and angle
    public void updatePosition(double deltaTime) {
        double radians = Math.toRadians(currentAngle);

        double deltaMoveX = calculateDeltaX(deltaTime, radians);
        double deltaMoveY = calculateDeltaY(deltaTime, radians);

        accumulateMovement(deltaMoveX, deltaMoveY);
        updatePositionIfExceededOnePixel();
    }

    private double calculateDeltaX(double deltaTime, double radians) {
        return currentVelocity * Math.cos(radians) * deltaTime;
    }

    private double calculateDeltaY(double deltaTime, double radians) {
        return currentVelocity * Math.sin(radians) * deltaTime;
    }

    private void accumulateMovement(double deltaMoveX, double deltaMoveY) {
        cumulativeShiftX += deltaMoveX;
        cumulativeShiftY += deltaMoveY;
    }

    private void updatePositionIfExceededOnePixel() {
        if (movementExceededOnePixel()) {
            x += roundAndResetcumulativeShiftX();
            y += roundAndResetcumulativeShiftY();
        }
    }

    private boolean movementExceededOnePixel() {
        return Math.abs(cumulativeShiftX) >= 1.0 || Math.abs(cumulativeShiftY) >= 1.0;
    }

    private int roundAndResetcumulativeShiftX() {
        int roundedX = (int) Math.round(cumulativeShiftX);
        cumulativeShiftX -= roundedX;
        return roundedX;
    }

    private int roundAndResetcumulativeShiftY() {
        int roundedY = (int) Math.round(cumulativeShiftY);
        cumulativeShiftY -= roundedY;
        return roundedY;
    }

    // handles wall collision and update particle's position and angle
    public void handleWallCollision(int canvasWidth, int canvasHeight, CopyOnWriteArrayList<Wall> walls) {
        int particleDiameter = 5;
        int buffer = 1;

        handleCanvasCollision(canvasWidth, canvasHeight, particleDiameter, buffer);
        handleWallCollisions(walls);

        normalizeAngle();
    }

    private void handleCanvasCollision(int canvasWidth, int canvasHeight, int diameter, int buffer) {
        if (x - cumulativeShiftX <= 0 || x + diameter + cumulativeShiftX >= canvasWidth) {
            reflectOffVerticalWall();
            moveInsideCanvas(canvasWidth, diameter, buffer);
        }

        if (y + diameter + cumulativeShiftY >= canvasHeight || y - cumulativeShiftY <= 0) {
            reflectOffHorizontalWall();
            moveInsideCanvas(canvasHeight, diameter, buffer);
        }
    }
    private void moveInsideCanvas(int canvasHeight, int diameter, int buffer) {
        int canvasWidth = 1280;
        if (x <= 0) {
            x = buffer;
        } else if (x + diameter >= canvasWidth) {
            x = canvasWidth - diameter - buffer;
        }

        if (y <= 0) {
            y = buffer;
        } else if (y + diameter >= canvasHeight) {
            y = 720 - diameter - buffer;
        }
    }

    private void reflectOffVerticalWall() {
        currentAngle = 180 - currentAngle;
    }

    private void reflectOffHorizontalWall() {
        currentAngle = -currentAngle;
    }

    private void handleWallCollisions(CopyOnWriteArrayList<Wall> walls) {
        for (Wall wall : walls) {
            if (checkCollisionWithWall(wall)) {
                reflectOffWall(wall);
            }
        }
    }

    private void normalizeAngle() {
        if (currentAngle < 0) currentAngle += 360;
        else if (currentAngle >= 360) currentAngle -= 360;
    }

    private boolean checkCollisionWithWall(Wall wall) {
        double x1 = x;
        double y1 = y;

        double x2 = x1 + currentVelocity * Math.cos(Math.toRadians(currentAngle)) * (1 / 60.0);
        double y2 = y1 + currentVelocity * Math.sin(Math.toRadians(currentAngle)) * (1 / 60.0);

        double x3 = wall.x1;
        double y3 = wall.y1;
        double x4 = wall.x2;
        double y4 = wall.y2;

        double denominator = calculateDenominator(x1, y1, x2, y2, x3, y3, x4, y4);
        if (denominator == 0) {
            return false;
        }

        double t = calculateParameterT(x1, y1, x3, y3, x4, y4, denominator);
        double u = calculateParameterU(x1, y1, x2, y2, x3, y3, denominator);

        return isIntersectionWithinSegments(t, u);
    }

    private double calculateDenominator(double x1, double y1, double x2, double y2,
                                        double x3, double y3, double x4, double y4) {
        return (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    }

    private double calculateParameterT(double x1, double y1, double x3, double y3, double x4, double y4, double denominator) {
        return ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
    }

    private double calculateParameterU(double x1, double y1, double x2, double y2,
                                       double x3, double y3, double denominator) {
        return ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;
    }

    private boolean isIntersectionWithinSegments(double t, double u) {
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    private void reflectOffWall(Wall wall) {
        double incomingVectorX = Math.cos(Math.toRadians(currentAngle));
        double incomingVectorY = Math.sin(Math.toRadians(currentAngle));

        double[] normalVector = calculateWallNormalVector(wall);
        double dotProduct = calculateDotProduct(incomingVectorX, incomingVectorY, normalVector[0], normalVector[1]);
        double[] reflectedVector = calculateReflectedVector(incomingVectorX, incomingVectorY, normalVector, dotProduct);
        currentAngle = calculateAngleFromVector(reflectedVector[0], reflectedVector[1]);

        normalizeAngle();
    }

    private double[] calculateWallNormalVector(Wall wall) {
        double wallDx = wall.x2 - wall.x1;
        double wallDy = wall.y2 - wall.y1;
        double normalX = wallDy;
        double normalY = -wallDx;

        return normalizeVector(normalX, normalY);
    }

    private double calculateDotProduct(double incomingVectorX, double incomingVectorY, double normalX, double normalY) {
        return incomingVectorX * normalX + incomingVectorY * normalY;
    }

    private double[] calculateReflectedVector(double incomingVectorX, double incomingVectorY, double[] normalVector, double dotProduct) {
        return new double[]{
                incomingVectorX - 2 * dotProduct * normalVector[0],
                incomingVectorY - 2 * dotProduct * normalVector[1]
        };
    }

    private double[] normalizeVector(double x, double y) {
        double length = Math.sqrt(x * x + y * y);
        return new double[]{x / length, y / length};
    }

    private double calculateAngleFromVector(double x, double y) {
        return Math.toDegrees(Math.atan2(y, x));
    }
}
