import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Simulator {
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

        frame.setSize(1300, 920);
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
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 0, 10, 0);

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
        scrollPane.setPreferredSize(new Dimension(1280, 100));
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

    private static void createParticlesCase1Panel(JPanel panel, Canvas canvas) {
        JPanel case1Panel = new JPanel();
        case1Panel.setLayout(new BoxLayout(case1Panel, BoxLayout.Y_AXIS));

        // Add bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Particles", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD));
        case1Panel.setBorder(titleBorder);

        JLabel caseText = new JLabel("Case 1: Between Points");
        caseText.setHorizontalAlignment(SwingConstants.CENTER);
        case1Panel.add(caseText);

        JTextField nField = createLabeledTextField(case1Panel, "Number of Particles:", "");
        JTextField x1Field = createLabeledTextField(case1Panel, "Start Point (X):", "");
        JTextField y1Field = createLabeledTextField(case1Panel, "Start Point (Y):", "");
        JTextField x2Field = createLabeledTextField(case1Panel, "End Point (X):", "");
        JTextField y2Field = createLabeledTextField(case1Panel, "End Point (Y):", "");
        JTextField velocityField = createLabeledTextField(case1Panel, "Velocity:", "");
        JTextField angleField = createLabeledTextField(case1Panel, "Angle:", "");

        JButton addButton = new JButton("Add Particles");
        addButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nField.getText());
                int x1 = Integer.parseInt(x1Field.getText());
                int y1 = Integer.parseInt(y1Field.getText());
                int x2 = Integer.parseInt(x2Field.getText());
                int y2 = Integer.parseInt(y2Field.getText());
                double angle = Double.parseDouble(angleField.getText());
                double velocity = Double.parseDouble(velocityField.getText());

                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("Number of particles must be at least 1.");

                // Validate x and y ranges
                if (x1 < 0 || x1 > 1280 || x2 < 0 || x2 > 1280 || y1 < 0 || y1 > 720 || y2 < 0 || y2 > 720) {
                    throw new IllegalArgumentException("X must be between 0 and 1280, Y must be between 0 and 720.");
                }

                canvas.particlesCase1(n, x1, y1, x2, y2, angle, velocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please enter valid numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }
        });
        case1Panel.add(addButton);
        case1Panel.setBorder(BorderFactory.createCompoundBorder(case1Panel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(case1Panel);
    }

    private static void createParticlesCase2Panel(JPanel panel, Canvas canvas) {
        JPanel case2Panel = new JPanel();
        case2Panel.setLayout(new BoxLayout(case2Panel, BoxLayout.Y_AXIS));

        // bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Particles", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        case2Panel.setBorder(titleBorder);

        // centered text
        JLabel caseText = new JLabel("Case 2: Different Angles");
        caseText.setHorizontalAlignment(SwingConstants.CENTER);
        case2Panel.add(caseText);

        JTextField nAngleField = createLabeledTextField(case2Panel, "Number of Particles:", "");
        JTextField xField = createLabeledTextField(case2Panel, "Particle Location (X):", "");
        JTextField yField = createLabeledTextField(case2Panel, "Particle Location (Y):", "");
        JTextField startAngleField = createLabeledTextField(case2Panel, "Start Angle:", "");
        JTextField endAngleField = createLabeledTextField(case2Panel, "End Angle:", "");
        JTextField velocityAngleField = createLabeledTextField(case2Panel, "Velocity:", "");

        JButton addAngleButton = new JButton("Add Particles");
        addAngleButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nAngleField.getText());
                double startAngle = Double.parseDouble(startAngleField.getText());
                double endAngle = Double.parseDouble(endAngleField.getText());
                double velocity = Double.parseDouble(velocityAngleField.getText());
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());

                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("Number of particles must be at least 1.");

                // Validate x and y ranges
                if (x < 0 || x > 1280 || y < 0 || y > 720) {
                    throw new IllegalArgumentException("X must be between 0 and 1280, Y must be between 0 and 720.");
                }

                canvas.particlesCase2(n, x, y, startAngle, endAngle, velocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please enter valid numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }
        });
        case2Panel.add(addAngleButton);
        case2Panel.setBorder(BorderFactory.createCompoundBorder(case2Panel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(case2Panel);
    }

    private static void createParticlesCase3Panel(JPanel panel, Canvas canvas) {
        JPanel case3Panel = new JPanel();
        case3Panel.setLayout(new BoxLayout(case3Panel, BoxLayout.Y_AXIS));

        // bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Particles", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        case3Panel.setBorder(titleBorder);

        // centered text
        JLabel caseText = new JLabel("Case 3: Different Velocities");
        caseText.setHorizontalAlignment(SwingConstants.CENTER);
        case3Panel.add(caseText);

        JTextField nVelocityField = createLabeledTextField(case3Panel, "Number of Particles:", "");
        JTextField xVelocityField = createLabeledTextField(case3Panel, "Particle Location (X):", "");
        JTextField yVelocityField = createLabeledTextField(case3Panel, "Particle Location (Y):", "");
        JTextField startVelocityField = createLabeledTextField(case3Panel, "Start Velocity:", "");
        JTextField endVelocityField = createLabeledTextField(case3Panel, "End Velocity:", "");
        JTextField angleVelocityField = createLabeledTextField(case3Panel, "Angle:", "");

        JButton addVelocityButton = new JButton("Add Particles");
        addVelocityButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nVelocityField.getText());
                double startVelocity = Double.parseDouble(startVelocityField.getText());
                double endVelocity = Double.parseDouble(endVelocityField.getText());
                double angle = Double.parseDouble(angleVelocityField.getText());
                int x = Integer.parseInt(xVelocityField.getText());
                int y = Integer.parseInt(yVelocityField.getText());

                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("Number of particles must be at least 1.");

                // Validate x and y ranges
                if (x < 0 || x > 1280 || y < 0 || y > 720) {
                    throw new IllegalArgumentException("X must be between 0 and 1280, Y must be between 0 and 720.");
                }

                canvas.particlesCase3(n, x, y, angle, startVelocity, endVelocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please enter valid numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }
        });
        case3Panel.add(addVelocityButton);
        case3Panel.setBorder(BorderFactory.createCompoundBorder(case3Panel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(case3Panel);
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

        JTextField x1Field = createLabeledTextField(addWallPanel, "X1:", "");
        JTextField y1Field = createLabeledTextField(addWallPanel, "Y1:", "");
        JTextField x2Field = createLabeledTextField(addWallPanel, "X2:", "");
        JTextField y2Field = createLabeledTextField(addWallPanel, "Y2:", "");

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
                    throw new IllegalArgumentException("X must be between 0 and 1280, Y must be between 0 and 720.");
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
