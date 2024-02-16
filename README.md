# Particle Simulator

This project involves the development of a particle simulation application on a canvas, integrating threading and load balancing techniques with the help of some Java built-in functionalities.

## Members
- Gabriel Angelo M. Gerardino
- Jaira Millicent M. Santos

**Course**: STDISCM - S11

## Getting Started

To run the simulator, follow these steps:

1. Open a terminal or command prompt.
2. Navigate to the directory containing the `ParticleSimulator.java` file.
3. Compile the Java source code using the following commands:

    ```bash
    javac ParticleSimulator.java
    ```

4. Run the compiled program with:

    ```bash
    java ParticleSimulator
    ```

The main window of the application will appear, displaying the canvas for particle movement and various input panels for particle and wall creation.

## User Interface

The user interface consists of the following components:

- **Canvas**: The main area where particle movement and interactions are visualized.
- **FPS Label**: Displays the Frames Per Second (FPS) of the simulation.
- **Particle Input Panels**: Input panels for creating particles with different behaviors.
- **Wall Input Panel**: Input panel for adding walls to the simulation.

## Note

- The default frame size is set to 1300x1000. Adjusting the resolution for smaller screens might be necessary.

## Particle Input Panels

### Case 1: Between Points

- **Number of Particles**: Specify the number of particles to add.
- **Start Point (X, Y)**: Starting coordinates of the line segment.
- **End Point (X, Y)**: Ending coordinates of the line segment.
- **Velocity**: Initial velocity of particles.
- **Angle**: Initial angle of particle movement.

### Case 2: Different Angles

- **Number of Particles**: Specify the number of particles to add.
- **Particle Location (X, Y)**: Starting coordinates of particles.
- **Start Angle**: Initial angle of the first particle.
- **End Angle**: Final angle of the last particle.
- **Velocity**: Initial velocity of particles.

### Case 3: Different Velocities

- **Number of Particles**: Specify the number of particles to add.
- **Particle Location (X, Y)**: Starting coordinates of particles.
- **Start Velocity**: Initial velocity of the first particle.
- **End Velocity**: Final velocity of the last particle.
- **Angle**: Initial angle of particle movement.

## Wall Input Panel

- **Start Point (X, Y)**: Starting coordinates of the wall.
- **End Point (X, Y)**: Ending coordinates of the wall.

## Features

- Real-time simulation of particle movement.
- Ability to add particles with different behaviors.
- Visualization of wall interactions.
