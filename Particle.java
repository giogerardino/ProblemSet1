import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

class Particle {
    // Variables
    int x; // Current x-coordinate
    int y; // Current y-coordinate
    double currentDirection; // Current direction in degrees
    double currentSpeed; // Current speed in pixels per second
    double accumulatedXMovement = 0.0; // Accumulated movement in the x-axis
    double accumulatedYMovement = 0.0; // Accumulated movement in the y-axis

    // Constructor
    public Particle(int initialX, int initialY, double initialAngle, double initialVelocity) {
        this.x = initialX;
        this.y = initialY;
        this.currentDirection = initialAngle;
        this.currentSpeed = initialVelocity;
    }

    // Update particle's position based on its velocity and angle
    public void updatePosition(double deltaTime) {
        // Convert angle to radians for trigonometric calculations
        double radians = toRadians(this.currentDirection);

        // Calculate movement based on velocity and angle
        double deltaX = calculateDeltaX(deltaTime, radians);
        double deltaY = calculateDeltaY(deltaTime, radians);

        // Accumulate sub-pixel movements
        accumulateSubPixelMovements(deltaX, deltaY);

        // Update position when accumulated movement exceeds 1 pixel
        updatePositionIfExceedsOnePixel();
    }

    // Handle collisions with walls and canvas boundaries
    public void handleWallCollision(int canvasWidth, int canvasHeight, CopyOnWriteArrayList<Wall> walls) {
        // Handle canvas boundaries
        handleCanvasBoundaries(canvasWidth, canvasHeight);

        // Handle wall collisions
        handleWallCollisions(walls);

        // Normalize the angle
        normalizeAngle();
    }

    // Check collision between particle and a wall
    private boolean checkCollisionWithWall(Wall wall) {
        // Current position
        double particleX1 = this.x;
        double particleY1 = this.y;

        // Predicted next position based on current velocity and angle
        double particleX2 = predictNextPositionX(1 / 60.0);
        double particleY2 = predictNextPositionY(1 / 60.0);

        // Wall start and end points
        double wallX1 = wall.startX;
        double wallY1 = wall.startY;
        double wallX2 = wall.endX;
        double wallY2 = wall.endY;

        // Calculate denominators for the line intersection formula
        double denominator = calculateDenominator(particleX1, particleX2, particleY1, particleY2, wallX1, wallY1, wallX2, wallY2);

        if (denominator == 0) {
            return false; // Lines are parallel, no intersection
        }

        // Calculate the intersection point (u and t are the line scalar values)
        double t = calculateScalarT(particleX1, wallX1, wallX2, particleY1, wallY1, wallY2, denominator);
        double u = calculateScalarU(particleX1, particleX2, particleY1, particleY2, wallY1, denominator);

        // Check if there is an intersection within the line segments
        return isIntersectionWithinSegments(t, u);
    }

    // Reflect off the wall's surface
    private void reflectOffWall(Wall wall) {
        // Calculate the incident vector components
        double[] incidentVector = calculateIncidentVector();

        // Calculate wall's normal vector
        double[] normalVector = calculateWallNormalVector(wall);

        // Calculate the dot product between the incident vector and the wall's normal vector
        double dotProduct = calculateDotProduct(incidentVector, normalVector);

        // Reflect the incident vector off the wall's normal vector
        double[] reflectedVector = calculateReflectedVector(incidentVector, normalVector, dotProduct);

        // Update direction from the reflected vector
        updateDirectionFromReflectedVector(reflectedVector);

        // Normalize the angle
        normalizeAngle();
    }

    // Reflect on collision with canvas boundaries
    private void reflectOnCollisionWithCanvasBorder(double targetAngle) {
        this.currentDirection = 2 * targetAngle - this.currentDirection;
    }

    // Normalize the angle to the range [0, 360)
    private void normalizeAngle() {
        if (this.currentDirection < 0) this.currentDirection += 360;
        else if (this.currentDirection >= 360) this.currentDirection -= 360;
    }

    // Additional subfunctions

    // Convert degrees to radians
    private double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    // Calculate the change in X based on velocity, angle, and deltaTime
    private double calculateDeltaX(double deltaTime, double radians) {
        return this.currentSpeed * Math.cos(radians) * deltaTime;
    }

    // Calculate the change in Y based on velocity, angle, and deltaTime
    private double calculateDeltaY(double deltaTime, double radians) {
        return this.currentSpeed * Math.sin(radians) * deltaTime;
    }

    // Accumulate sub-pixel movements
    private void accumulateSubPixelMovements(double deltaX, double deltaY) {
        this.accumulatedXMovement += deltaX;
        this.accumulatedYMovement += deltaY;
    }

    // Update position when accumulated movement exceeds 1 pixel
    private void updatePositionIfExceedsOnePixel() {
        if (Math.abs(this.accumulatedXMovement) >= 1.0 || Math.abs(this.accumulatedYMovement) >= 1.0) {
            this.x += (int) Math.round(this.accumulatedXMovement);
            this.y += (int) Math.round(this.accumulatedYMovement);

            // Reset accumulated movement after applying position change
            this.accumulatedXMovement -= (int) Math.round(this.accumulatedXMovement);
            this.accumulatedYMovement -= (int) Math.round(this.accumulatedYMovement);
        }
    }

    // Handle canvas boundaries
    private void handleCanvasBoundaries(int canvasWidth, int canvasHeight) {
        int diameter = 5;
        int buffer = 1;

        // Handle canvas boundaries
        if (this.x <= 0) {
            reflectOnCollisionWithCanvasBorder(180);
            this.x = buffer; // Move particle slightly inside to prevent sticking
        } else if (this.x + diameter >= canvasWidth) {
            reflectOnCollisionWithCanvasBorder(180);
            this.x = canvasWidth - diameter - buffer;
        }

        if (this.y + diameter >= canvasHeight) {
            reflectOnCollisionWithCanvasBorder(0);
            this.y = canvasHeight - diameter - buffer;
        } else if (this.y <= 0) {
            reflectOnCollisionWithCanvasBorder(0);
            this.y = buffer;
        }
    }

    // Handle wall collisions
    private void handleWallCollisions(CopyOnWriteArrayList<Wall> walls) {
        for (Wall wall : walls) {
            if (checkCollisionWithWall(wall)) {
                reflectOffWall(wall);
            }
        }
    }

    // Calculate denominator for line intersection formula
    private double calculateDenominator(double particleX1, double particleX2, double particleY1, double particleY2, 
                                        double wallX1, double wallY1, double wallX2, double wallY2) {
        return (particleX1 - particleX2) * (wallY1 - wallY2) - (particleY1 - particleY2) * (wallX1 - wallX2);
    }

    // Predict next X position based on current velocity and angle
    private double predictNextPositionX(double deltaTime) {
        return this.x + this.currentSpeed * Math.cos(Math.toRadians(this.currentDirection)) * deltaTime;
    }

    // Predict next Y position based on current velocity and angle
    private double predictNextPositionY(double deltaTime) {
        return this.y + this.currentSpeed * Math.sin(Math.toRadians(this.currentDirection)) * deltaTime;
    }

    // Calculate scalar 't' for line intersection formula
    private double calculateScalarT(double particleX1, double wallX1, double wallX2, double particleY1, double wallY1, double wallY2, double denominator) {
        return ((particleX1 - wallX1) * (wallY1 - wallY2) - (particleY1 - wallY1) * (wallX1 - wallX2)) / denominator;
    }

    // Calculate scalar 'u' for line intersection formula
    private double calculateScalarU(double particleX1, double particleX2, double particleY1, double particleY2, double wallY1, double denominator) {
        return ((particleX2 - particleX1) * (particleY1 - wallY1) - (particleY2 - particleY1) * (particleX1 - wallX1)) / denominator;
    }

    // Check if intersection is within line segments
    private boolean isIntersectionWithinSegments(double t, double u) {
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    // Calculate incident vector components
    private double[] calculateIncidentVector() {
        return new double[]{Math.cos(Math.toRadians(this.currentDirection)), Math.sin(Math.toRadians(this.currentDirection))};
    }

    // Calculate wall's normal vector
    private double[] calculateWallNormalVector(Wall wall) {
        double wallDx = wall.endX - wall.startX;
        double wallDy = wall.endY - wall.startY;
        double normalX = wallDy;
        double normalY = -wallDx;
        double length = Math.sqrt(normalX * normalX + normalY * normalY);
        normalX /= length;
        normalY /= length;
        return new double[]{normalX, normalY};
    }

    // Calculate dot product between two vectors
    private double calculateDotProduct(double[] vector1, double[] vector2) {
        return vector1[0] * vector2[0] + vector1[1] * vector2[1];
    }

    // Calculate reflected vector using the reflection formula
    private double[] calculateReflectedVector(double[] incidentVector, double[] normalVector, double dotProduct) {
        double reflectX = incidentVector[0] - 2 * dotProduct * normalVector[0];
        double reflectY = incidentVector[1] - 2 * dotProduct * normalVector[1];
        return new double[]{reflectX, reflectY};
    }

    // Update direction from the reflected vector
    private void updateDirectionFromReflectedVector(double[] reflectedVector) {
        this.currentDirection = Math.toDegrees(Math.atan2(reflectedVector[1], reflectedVector[0]));
    }
}
