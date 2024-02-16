```java
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

    // Constructor to initialize particle properties
    public Particle(int initialX, int initialY, double initialAngle, double initialVelocity) {
        x = initialX;
        y = initialY;
        currentAngle = initialAngle;
        currentVelocity = initialVelocity;
    }

    // Updates the particle's position based on its velocity and angle
    public void updatePosition(double deltaTime) {
        double radians = Math.toRadians(currentAngle);

        double deltaMoveX = calculateDeltaX(deltaTime, radians);
        double deltaMoveY = calculateDeltaY(deltaTime, radians);

        accumulateMovement(deltaMoveX, deltaMoveY);
        updatePositionIfExceededOnePixel();
    }

    // Calculates the change in x-coordinate based on velocity and angle
    private double calculateDeltaX(double deltaTime, double radians) {
        return currentVelocity * Math.cos(radians) * deltaTime;
    }

    // Calculates the change in y-coordinate based on velocity and angle
    private double calculateDeltaY(double deltaTime, double radians) {
        return currentVelocity * Math.sin(radians) * deltaTime;
    }

    // Accumulates the movement for smoother updates
    private void accumulateMovement(double deltaMoveX, double deltaMoveY) {
        cumulativeShiftX += deltaMoveX;
        cumulativeShiftY += deltaMoveY;
    }

    // Updates the position if movement exceeds one pixel
    private void updatePositionIfExceededOnePixel() {
        if (movementExceededOnePixel()) {
            x += roundAndResetCumulativeShiftX();
            y += roundAndResetCumulativeShiftY();
        }
    }

    // Checks if the movement exceeds one pixel
    private boolean movementExceededOnePixel() {
        return Math.abs(cumulativeShiftX) >= 1.0 || Math.abs(cumulativeShiftY) >= 1.0;
    }

    // Rounds and resets the cumulative shift for x-coordinate
    private int roundAndResetCumulativeShiftX() {
        int roundedX = (int) Math.round(cumulativeShiftX);
        cumulativeShiftX -= roundedX;
        return roundedX;
    }

    // Rounds and resets the cumulative shift for y-coordinate
    private int roundAndResetCumulativeShiftY() {
        int roundedY = (int) Math.round(cumulativeShiftY);
        cumulativeShiftY -= roundedY;
        return roundedY;
    }

    // Handles wall collision and updates particle's position and angle
    public void handleWallCollision(int canvasWidth, int canvasHeight, CopyOnWriteArrayList<Wall> walls) {
        int particleDiameter = 5;
        int buffer = 1;

        handleCanvasCollision(canvasWidth, canvasHeight, particleDiameter, buffer);
        handleWallCollisions(walls);

        normalizeAngle();
    }

    // Handles collision with canvas boundaries
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

    // Moves the particle inside the canvas boundaries
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

    // Reflects off a vertical wall
    private void reflectOffVerticalWall() {
        currentAngle = 180 - currentAngle;
    }

    // Reflects off a horizontal wall
    private void reflectOffHorizontalWall() {
        currentAngle = -currentAngle;
    }

    // Handles collisions with walls
    private void handleWallCollisions(CopyOnWriteArrayList<Wall> walls) {
        for (Wall wall : walls) {
            if (checkCollisionWithWall(wall)) {
                reflectOffWall(wall);
            }
        }
    }

    // Normalizes the angle to be within [0, 360) degrees
    private void normalizeAngle() {
        if (currentAngle < 0) currentAngle += 360;
        else if (currentAngle >= 360) currentAngle -= 360;
    }

    // Checks collision with a wall
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

    // Calculates the denominator for collision detection
    private double calculateDenominator(double x1, double y1, double x2, double y2,
                                        double x3, double y3, double x4, double y4) {
        return (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    }

    // Calculates parameter t for collision detection
    private double calculateParameterT(double x1, double y1, double x3, double y3, double x4, double y4, double denominator) {
        return ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
    }

    // Calculates parameter u for collision detection
    private double calculateParameterU(double x1, double y1, double x2, double y2,
                                       double x3, double y3, double denominator) {
        return ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;
    }

    // Checks if the intersection point is within the line segments
    private boolean isIntersectionWithinSegments(double t, double u) {
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    // Reflects off a wall based on the normal vector
    private void reflectOffWall(Wall wall) {
        double incomingVectorX = Math.cos(Math.toRadians(currentAngle));
        double incomingVectorY = Math.sin(Math.toRadians(currentAngle));

        double[] normalVector = calculateWallNormalVector(wall);
        double dotProduct = calculateDotProduct(incomingVectorX, incomingVectorY, normalVector[0], normalVector[1]);
        double[] reflectedVector = calculateReflectedVector(incomingVectorX, incomingVectorY, normalVector, dotProduct);
        currentAngle = calculateAngleFromVector(reflectedVector[0], reflectedVector[1]);

        normalizeAngle();
    }

    // Calculates the normal vector of a wall
    private double[] calculateWallNormalVector(Wall wall) {
        double wallDx = wall.x2 - wall.x1;
        double wallDy = wall.y2 - wall.y1;
        double normalX = wallDy;
        double normalY = -wallDx;

        return normalizeVector(normalX, normalY);
    }

    // Calculates the dot product of two vectors
    private double calculateDotProduct(double incomingVectorX, double incomingVectorY, double normalX, double normalY) {
        return incomingVectorX * normalX + incomingVectorY * normalY;
    }

    // Calculates the reflected vector based on the incoming vector, normal vector, and dot product
    private double[] calculateReflectedVector(double incomingVectorX, double incomingVectorY, double[] normalVector, double dotProduct) {
        return new double[]{
                incomingVectorX - 2 * dotProduct * normalVector[0],
                incomingVectorY - 2 * dotProduct * normalVector[1]
        };
    }

    // Normalizes a vector to have unit length
    private double[] normalizeVector(double x, double y) {
        double length = Math.sqrt(x * x + y * y);
        return new double[]{x / length, y / length};
    }

    // Calculates the angle (in degrees) from a vector
    private double calculateAngleFromVector(double x, double y) {
        return Math.toDegrees(Math.atan2(y, x));
    }
}
```