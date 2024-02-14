import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParticleSimulator extends JFrame {
    private static final int CANVAS_WIDTH = 1280;
    private static final int CANVAS_HEIGHT = 720;
    private static final int FRAME_WIDTH = CANVAS_WIDTH + 200;
    private static final int FRAME_HEIGHT = CANVAS_HEIGHT + 200;

    private Canvas canvas;

    private JTextField numberOfParticlesField;
    private JTextField startPointXField;
    private JTextField startPointYField;
    private JTextField endPointXField;
    private JTextField endPointYField;
    private JTextField velocityField;
    private JTextField angleField;

    private JTextField numberOfParticles2Field;
    private JTextField particleLocationXField;
    private JTextField particleLocationYField;
    private JTextField startingThetaField;
    private JTextField endingThetaField;
    private JTextField velocity2Field;

    private JTextField numberOfParticles3Field;
    private JTextField particleLocation2XField;
    private JTextField particleLocation2YField;
    private JTextField startingVelocityField;
    private JTextField endingVelocityField;
    private JTextField thetaField;

    private JTextField wallX1Field;
    private JTextField wallY1Field;
    private JTextField wallX2Field;
    private JTextField wallY2Field;

    public ParticleSimulator() {
        setTitle("Particle Simulator");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Particle input fields for case 1
        numberOfParticlesField = createTextFieldWithLabel("Number of Particles");
        startPointXField = createTextFieldWithLabel("Start Point X");
        startPointYField = createTextFieldWithLabel("Start Point Y");
        endPointXField = createTextFieldWithLabel("End Point X");
        endPointYField = createTextFieldWithLabel("End Point Y");
        velocityField = createTextFieldWithLabel("Velocity");
        angleField = createTextFieldWithLabel("Angle");

        // Particle input fields for case 2
        numberOfParticles2Field = createTextFieldWithLabel("Number of Particles");
        particleLocationXField = createTextFieldWithLabel("Particle Location X");
        particleLocationYField = createTextFieldWithLabel("Particle Location Y");
        startingThetaField = createTextFieldWithLabel("Starting Theta");
        endingThetaField = createTextFieldWithLabel("Ending Theta");
        velocity2Field = createTextFieldWithLabel("Velocity");

        // Particle input fields for case 3
        numberOfParticles3Field = createTextFieldWithLabel("Number of Particles");
        particleLocation2XField = createTextFieldWithLabel("Particle Location X");
        particleLocation2YField = createTextFieldWithLabel("Particle Location Y");
        startingVelocityField = createTextFieldWithLabel("Starting Velocity");
        endingVelocityField = createTextFieldWithLabel("Ending Velocity");
        thetaField = createTextFieldWithLabel("Theta");

        // Wall input fields
        wallX1Field = createTextFieldWithLabel("X1");
        wallY1Field = createTextFieldWithLabel("Y1");
        wallX2Field = createTextFieldWithLabel("X2");
        wallY2Field = createTextFieldWithLabel("Y2");

        // Particle button for case 1
        JButton addParticleButton = createButton("Add Particles (Case 1)", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = Integer.parseInt(numberOfParticlesField.getText());
                double startX = Double.parseDouble(startPointXField.getText());
                double startY = Double.parseDouble(startPointYField.getText());
                double endX = Double.parseDouble(endPointXField.getText());
                double endY = Double.parseDouble(endPointYField.getText());
                double velocity = Double.parseDouble(velocityField.getText());
                double angle = Double.parseDouble(angleField.getText());

                for (int i = 0; i < count; i++) {
                    canvas.addParticle((endX - startX) * i / (count - 1) + startX,
                            (endY - startY) * i / (count - 1) + startY,
                            angle,
                            velocity);
                }
            }
        });

        // Particle button for case 2
        JButton addParticle2Button = createButton("Add Particles (Case 2)", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = Integer.parseInt(numberOfParticles2Field.getText());
                double locationX = Double.parseDouble(particleLocationXField.getText());
                double locationY = Double.parseDouble(particleLocationYField.getText());
                double startTheta = Double.parseDouble(startingThetaField.getText());
                double endTheta = Double.parseDouble(endingThetaField.getText());
                double velocity = Double.parseDouble(velocity2Field.getText());

                for (int i = 0; i < count; i++) {
                    double theta = (endTheta - startTheta) * i / (count - 1) + startTheta;
                    canvas.addParticle(locationX, locationY, theta, velocity);
                }
            }
        });

        // Particle button for case 3
        JButton addParticle3Button = createButton("Add Particles (Case 3)", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = Integer.parseInt(numberOfParticles3Field.getText());
                double locationX = Double.parseDouble(particleLocation2XField.getText());
                double locationY = Double.parseDouble(particleLocation2YField.getText());
                double startVelocity = Double.parseDouble(startingVelocityField.getText());
                double endVelocity = Double.parseDouble(endingVelocityField.getText());
                double theta = Double.parseDouble(thetaField.getText());

                for (int i = 0; i < count; i++) {
                    double velocity = (endVelocity - startVelocity) * i / (count - 1) + startVelocity;
                    canvas.addParticle(locationX, locationY, theta, velocity);
                }
            }
        });

        // Wall button
        JButton addWallButton = createButton("Add Wall", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x1 = Integer.parseInt(wallX1Field.getText());
                int y1 = Integer.parseInt(wallY1Field.getText());
                int x2 = Integer.parseInt(wallX2Field.getText());
                int y2 = Integer.parseInt(wallY2Field.getText());
                canvas.addWall(x1, y1, x2, y2);
            }
        });

        // Particle input panel for case 1
        JPanel particleInputPanel1 = createParticleInputPanel("Case 1: ",
                new JTextField[]{numberOfParticlesField, startPointXField, startPointYField,
                        endPointXField, endPointYField, velocityField, angleField}, addParticleButton);
        particleInputPanel1.setLayout(new BoxLayout(particleInputPanel1, BoxLayout.Y_AXIS));
        setTextFieldSizes(numberOfParticlesField, startPointXField, startPointYField,
                endPointXField, endPointYField, velocityField, angleField);

        // Particle input panel for case 2
        JPanel particleInputPanel2 = createParticleInputPanel("Case 2: ",
                new JTextField[]{numberOfParticles2Field, particleLocationXField, particleLocationYField,
                        startingThetaField, endingThetaField, velocity2Field}, addParticle2Button);
        particleInputPanel2.setLayout(new BoxLayout(particleInputPanel2, BoxLayout.Y_AXIS));
        setTextFieldSizes(numberOfParticles2Field, particleLocationXField, particleLocationYField,
                startingThetaField, endingThetaField, velocity2Field);

        // Particle input panel for case 3
        JPanel particleInputPanel3 = createParticleInputPanel("Case 3: ",
                new JTextField[]{numberOfParticles3Field, particleLocation2XField, particleLocation2YField,
                        startingVelocityField, endingVelocityField, thetaField}, addParticle3Button);
        particleInputPanel3.setLayout(new BoxLayout(particleInputPanel3, BoxLayout.Y_AXIS));
        setTextFieldSizes(numberOfParticles3Field, particleLocation2XField, particleLocation2YField,
                startingVelocityField, endingVelocityField, thetaField);

        // Wall input panel
        JPanel wallInputPanel = createWallInputPanel(addWallButton);

        // Separator for the divider
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);

        JPanel particleInputPanels = new JPanel();
        particleInputPanels.setLayout(new BoxLayout(particleInputPanels, BoxLayout.X_AXIS));
        particleInputPanels.add(particleInputPanel1);
        particleInputPanels.add(particleInputPanel2);
        particleInputPanels.add(particleInputPanel3);

        // Create the bottom panel with a BorderLayout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(separator, BorderLayout.CENTER);
        bottomPanel.add(particleInputPanels, BorderLayout.CENTER);
        bottomPanel.add(wallInputPanel, BorderLayout.SOUTH);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(canvas, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(contentPane);


        setVisible(true);
    }
    // Method to set preferred and maximum sizes for text fields
    private void setTextFieldSizes(JTextField... fields) {
        for (JTextField field : fields) {
            field.setPreferredSize(new Dimension(100, 25));
            field.setMaximumSize(new Dimension(100, 25));
        }
        this.revalidate();
        this.repaint();
    }

    // Create a JTextField with a label
    private JTextField createTextFieldWithLabel(String labelText) {
        JTextField textField = new JTextField(5);
        textField.setName(labelText); // Set the name to the label text
        return textField;
    }

    // Create a JButton with a label
    private JButton createButton(String labelText, ActionListener actionListener) {
        JButton button = new JButton(labelText);
        button.addActionListener(actionListener);
        return button;
    }

    // Create a particle input panel with labels
    private JPanel createParticleInputPanel(String label, JTextField[] fields, JButton button) {
        JPanel particleInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;  // Adjust this value as needed

        particleInputPanel.add(new JLabel(label), gbc);

        for (JTextField field : fields) {
            JPanel fieldPanel = new JPanel(new BorderLayout());
            fieldPanel.add(new JLabel(field.getName() + ":"), BorderLayout.WEST);
            fieldPanel.add(field, BorderLayout.CENTER);
            particleInputPanel.add(fieldPanel, gbc);
        }

        particleInputPanel.add(button, gbc);
        return particleInputPanel;
    }

    // Create a wall input panel
    private JPanel createWallInputPanel(JButton addWallButton) {
        JPanel wallInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        wallInputPanel.add(new JLabel("X1:"), gbc);
        gbc.gridx = 1;
        wallInputPanel.add(wallX1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        wallInputPanel.add(new JLabel("Y1:"), gbc);
        gbc.gridx = 1;
        wallInputPanel.add(wallY1Field, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        wallInputPanel.add(new JLabel("X2:"), gbc);
        gbc.gridx = 3;
        wallInputPanel.add(wallX2Field, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        wallInputPanel.add(new JLabel("Y2:"), gbc);
        gbc.gridx = 3;
        wallInputPanel.add(wallY2Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        wallInputPanel.add(addWallButton, gbc);  // Now you can add your button!

        return wallInputPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParticleSimulator::new);
    }
}
