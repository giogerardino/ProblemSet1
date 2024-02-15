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
        JPanel inputPanel = createInputPanel(fpsLabel, canvas);

        JScrollPane scrollPane = createScrollPane(inputPanel);
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setSize(1280, 860);
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

        // Center the text horizontally within the label
        fpsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create an empty border with custom insets
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 0, 10, 0);

        // Compound the borders to achieve the desired result
        CompoundBorder compoundBorder = new CompoundBorder(emptyBorder, titleBorder);

        // Set the compound border to the label
        fpsLabel.setBorder(compoundBorder);

        return fpsLabel;
    }

    private static JPanel createInputPanel(JLabel fpsLabel, Canvas canvas) {
        JPanel inputPanel = new JPanel(new BorderLayout());

        // Create a panel for the top area with FPS label
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(fpsLabel);

        // Create the main bottom panel for particles and walls
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 0)); // 1 row, 4 columns, horizontal gap of 10
        createParticleInputPanels(bottomPanel, canvas);

        // Add the top and bottom panels to the main input panel
        inputPanel.add(topPanel, BorderLayout.NORTH);
        inputPanel.add(bottomPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    private static JScrollPane createScrollPane(JPanel inputPanel) {
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(1000, 110));
        scrollPane.setMaximumSize(new Dimension(1280, Integer.MAX_VALUE));
        return scrollPane;
    }

    private static void createParticleInputPanels(JPanel panel, Canvas canvas) {
        createParticlesCase1Panel(panel, canvas);
        createParticlesCase2Panel(panel, canvas);
        createParticlesCase3Panel(panel, canvas);
        createWallPanel(panel, canvas);
    }

    private static JTextField createLabeledTextField(JPanel panel, String labelText, String textFieldText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0)); // Adjust the margins as needed
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField(9);
        textField.setText(textFieldText);

        // Add empty borders to control top and bottom margins
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); // Adjust the margins as needed

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
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        case1Panel.setBorder(titleBorder);

        // Add centered text
        JLabel caseText = new JLabel("Case 1: Between Points");
        caseText.setHorizontalAlignment(SwingConstants.CENTER);
        case1Panel.add(caseText);

        JTextField nField = createLabeledTextField(case1Panel, "Number of Particles:", "");
        JTextField startXField = createLabeledTextField(case1Panel, "Start Point (X):", "");
        JTextField startYField = createLabeledTextField(case1Panel, "Start Point (Y):", "");
        JTextField endXField = createLabeledTextField(case1Panel, "End Point (X):", "");
        JTextField endYField = createLabeledTextField(case1Panel, "End Point (Y):", "");
        JTextField velocityField = createLabeledTextField(case1Panel, "Velocity:", "");
        JTextField angleField = createLabeledTextField(case1Panel, "Angle:", "");

        JButton addButton = new JButton("Add Particles");
        addButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nField.getText());
                int startX = Integer.parseInt(startXField.getText());
                int startY = Integer.parseInt(startYField.getText());
                int endX = Integer.parseInt(endXField.getText());
                int endY = Integer.parseInt(endYField.getText());
                double angle = Double.parseDouble(angleField.getText());
                double velocity = Double.parseDouble(velocityField.getText());

                // Validate number of particles
                if (n < 1) throw new IllegalArgumentException("Number of particles must be at least 1.");

                // Validate x and y ranges
                if (startX < 0 || startX > 1280 || endX < 0 || endX > 1280 || startY < 0 || startY > 720 || endY < 0 || endY > 720) {
                    throw new IllegalArgumentException("X must be between 0 and 1280, Y must be between 0 and 720.");
                }

                canvas.addParticlesCase1(n, startX, startY, endX, endY, angle, velocity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please enter valid numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }
        });
        case1Panel.add(addButton);
        // Add a small margin after the button
        case1Panel.setBorder(BorderFactory.createCompoundBorder(case1Panel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(case1Panel);
    }

    private static void createParticlesCase2Panel(JPanel panel, Canvas canvas) {
        JPanel case2Panel = new JPanel();
        case2Panel.setLayout(new BoxLayout(case2Panel, BoxLayout.Y_AXIS));

        // Add bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Particles", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        case2Panel.setBorder(titleBorder);

        // Add centered text
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

                canvas.addParticlesCase2(n, x, y, startAngle, endAngle, velocity);
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

                canvas.addParticlesCase3(n, x, y, angle, startVelocity, endVelocity);
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

        // Add bold title border
        Border roundedBorder = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        TitledBorder titleBorder = new TitledBorder(roundedBorder, "Add Wall", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        Font titleFont = titleBorder.getTitleFont();
        titleBorder.setTitleFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD)); // Make it bold
        addWallPanel.setBorder(titleBorder);

        JTextField x1Field = createLabeledTextField(addWallPanel, "X1:", "");
        JTextField y1Field = createLabeledTextField(addWallPanel, "Y1:", "");
        JTextField x2Field = createLabeledTextField(addWallPanel, "X2:", "");
        JTextField y2Field = createLabeledTextField(addWallPanel, "Y2:", "");

        // Create a Box to center the button
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

        // Add a small margin after the button
        addWallPanel.setBorder(BorderFactory.createCompoundBorder(addWallPanel.getBorder(), BorderFactory.createEmptyBorder(0, 0, 10, 0)));
        panel.add(addWallPanel);
    }

}
