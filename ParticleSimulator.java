/**
 * STDISCM S11
 * Gabriel Angelo M. Gerardino
 * Jaira Millicent M. Santos
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ParticleSimulator {
    public static void main(String[] args) {
        JFrame frame = createAndConfigureFrame();

        JLabel fpsLabel = createFPSLabel();
        Canvas canvas = new Canvas(fpsLabel);
        JPanel inputPanel = createInputPanel(canvas);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1280, 720); // Set the preferred size of the main panel
            }
        };

        mainPanel.add(canvas, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(fpsLabel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setSize(1300, 1000);
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.startSimulation();
    }

    private static JFrame createAndConfigureFrame() {
        JFrame frame = new JFrame("Particle Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        return frame;
    }

    private static JLabel createFPSLabel() {
        JLabel fpsLabel = new JLabel("              0.00              ");

        Border roundedBorder = BorderFactory.createLineBorder(Color.BLACK, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, " FPS ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getSize() + 4));

        // Center text horizontally within the label
        fpsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // empty border with custom insets
        Border emptyBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);

        CompoundBorder compoundBorder = new CompoundBorder(emptyBorder, titleBorder);

        fpsLabel.setBorder(compoundBorder);

        return fpsLabel;
    }

    private static JPanel createInputPanel(Canvas canvas) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = createParticleInputPanels(canvas);
        JScrollPane scrollPane = createScrollPane(bottomPanel);
        inputPanel.add(scrollPane, BorderLayout.SOUTH);
        return inputPanel;
    }

    private static JScrollPane createScrollPane(JPanel inputPanel) {
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(1280, 205));
        return scrollPane;
    }

    private static JPanel createParticleInputPanels(Canvas canvas) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        createParticlesCase1Panel(panel, canvas);
        createParticlesCase2Panel(panel, canvas);
        createParticlesCase3Panel(panel, canvas);
        createWallPanel(panel, canvas);
        return panel;
    }

    private static JTextField createLabeledTextField(JPanel panel, String labelText, String textFieldText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField(9);
        textField.setText(textFieldText);

        fieldPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        fieldPanel.add(label);
        fieldPanel.add(textField);
        panel.add(fieldPanel);
        return textField;
    }

    private static void createParticlesPanel(JPanel panel, Canvas canvas, String title, String[] labels, ParticleCaseHandler handler) {
        JPanel casePanel = new JPanel();
        casePanel.setLayout(new BoxLayout(casePanel, BoxLayout.Y_AXIS));
    
        // bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Particles", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        casePanel.setBorder(titleBorder);
    
        // centered text
        JLabel caseText = new JLabel(title);
        caseText.setHorizontalAlignment(SwingConstants.CENTER);
        casePanel.add(caseText);
    
        JTextField[] textFields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            textFields[i] = createLabeledTextField(casePanel, labels[i], "");
        }
    
        JButton addButton = new JButton("Add Particles");
        addButton.addActionListener(e -> handler.handle(canvas, casePanel, textFields));
        casePanel.add(addButton);
        casePanel.setBorder(BorderFactory.createCompoundBorder(casePanel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(casePanel);
    }
    
    private interface ParticleCaseHandler {
        void handle(Canvas canvas, JPanel panel, JTextField[] textFields);
    }
    
    private static void createParticlesCase1Panel(JPanel panel, Canvas canvas) {
        String title = "Case 1: Between Points";
        String[] labels = {"Number of Particles:", "Start Point (X):", "Start Point (Y):", "End Point (X):", "End Point (Y):", "Velocity:", "Angle:"};
    
        createParticlesPanel(panel, canvas, title, labels, (c, p, t) -> {
            try {
                int n = Integer.parseInt(t[0].getText());
                int x1 = Integer.parseInt(t[1].getText());
                int y1 = Integer.parseInt(t[2].getText());
                int x2 = Integer.parseInt(t[3].getText());
                int y2 = Integer.parseInt(t[4].getText());
                double angle = Double.parseDouble(t[6].getText());
                double velocity = Double.parseDouble(t[5].getText());
   
                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("There must be at least 1 particle.");
    
                // Validate x and y ranges
                if (x1 < 0 || x1 > 1280 || x2 < 0 || x2 > 1280 || y1 < 0 || y1 > 720 || y2 < 0 || y2 > 720) {
                    throw new IllegalArgumentException("Ensure that X falls within the range of 0 to 1280, and Y falls within the range of 0 to 720.");
                }
    
                c.particlesCase1(n, x1, y1, x2, y2, angle, velocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Input is invalid. Please provide valid numerical values.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(p, ex.getMessage());
            }
        });
    }
    
    private static void createParticlesCase2Panel(JPanel panel, Canvas canvas) {
        String title = "Case 2: Different Angles";
        String[] labels = {"Number of Particles:", "Particle Location (X):", "Particle Location (Y):", "Start Angle:", "End Angle:", "Velocity:"};
    
        createParticlesPanel(panel, canvas, title, labels, (c, p, t) -> {
            try {
                int n = Integer.parseInt(t[0].getText());
                int x = Integer.parseInt(t[1].getText());
                int y = Integer.parseInt(t[2].getText());
                double startAngle = Double.parseDouble(t[3].getText());
                double endAngle = Double.parseDouble(t[4].getText());
                double velocity = Double.parseDouble(t[5].getText());
    
                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("There must be at least 1 particle.");
    
                // Validate x and y ranges
                if (x < 0 || x > 1280 || y < 0 || y > 720) {
                    throw new IllegalArgumentException("Ensure that X falls within the range of 0 to 1280, and Y falls within the range of 0 to 720.");
                }
    
                c.particlesCase2(n, x, y, startAngle, endAngle, velocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Input is invalid. Please provide valid numerical values.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(p, ex.getMessage());
            }
        });
    }
    
    private static void createParticlesCase3Panel(JPanel panel, Canvas canvas) {
        String title = "Case 3: Different Velocities";
        String[] labels = {"Number of Particles:", "Particle Location (X):", "Particle Location (Y):", "Start Velocity:", "End Velocity:", "Angle:"};
    
        createParticlesPanel(panel, canvas, title, labels, (c, p, t) -> {
            try {
                int n = Integer.parseInt(t[0].getText());
                int x = Integer.parseInt(t[1].getText());
                int y = Integer.parseInt(t[2].getText());
                double startVelocity = Double.parseDouble(t[3].getText());
                double endVelocity = Double.parseDouble(t[4].getText());
                double angle = Double.parseDouble(t[5].getText());
    
                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("There must be at least 1 particle.");
    
                // Validate x and y ranges
                if (x < 0 || x > 1280 || y < 0 || y > 720) {
                    throw new IllegalArgumentException("Ensure that X falls within the range of 0 to 1280, and Y falls within the range of 0 to 720.");
                }
    
                c.particlesCase3(n, x, y, angle, startVelocity, endVelocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Input is invalid. Please provide valid numerical values.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(p, ex.getMessage());
            }
        });
    }
    
        private static void createWallPanel(JPanel panel, Canvas canvas) {
        JPanel addWallPanel = new JPanel();
        addWallPanel.setLayout(new BoxLayout(addWallPanel, BoxLayout.Y_AXIS));

        // bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Wall", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        addWallPanel.setBorder(titleBorder);

        JTextField x1Field = createLabeledTextField(addWallPanel, "Start Point (X):", "");
        JTextField y1Field = createLabeledTextField(addWallPanel, "Start Point (Y):", "");
        JTextField x2Field = createLabeledTextField(addWallPanel, "End Point (X):", "");
        JTextField y2Field = createLabeledTextField(addWallPanel, "End Point (Y):", "");

        // center the button
        Box centerBox = Box.createHorizontalBox();
        centerBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addWallButton = new JButton("Add Wall");
        addWallButton.addActionListener(e -> {
            try {
                int x1 = Integer.parseInt(x1Field.getText());
                int y1 = Integer.parseInt(y1Field.getText());
                int x2 = Integer.parseInt(x2Field.getText());
                int y2 = Integer.parseInt(y2Field.getText());

                // Validate x and y ranges for walls
                if (x1 < 0 || x1 > 1280 || x2 < 0 || x2 > 1280 || y1 < 0 || y1 > 720 || y2 < 0 || y2 > 720) {
                    throw new IllegalArgumentException("Ensure that X falls within the range of 0 to 1280, and Y falls within the range of 0 to 720.");
                }

                canvas.addWall(new Wall(x1, y1, x2, y2));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input for wall coordinates. Please enter valid integers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }
        });

        centerBox.add(addWallButton);
        addWallPanel.add(centerBox);
        addWallPanel.setBorder(BorderFactory.createCompoundBorder(addWallPanel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(addWallPanel);
    }

}
