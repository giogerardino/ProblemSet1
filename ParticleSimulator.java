import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParticleSimulator extends JFrame {
    private static final int CANVAS_WIDTH = 1280;
    private static final int CANVAS_HEIGHT = 720;
    private static final int FRAME_WIDTH = CANVAS_WIDTH + 20;
    private static final int FRAME_HEIGHT = CANVAS_HEIGHT + 100;

    private Canvas canvas;
    private JTextField particleXField;
    private JTextField particleYField;
    private JTextField particleAngleField;
    private JTextField particleVelocityField;
    private JTextField particleCountField;
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

        // input fields for adding particles
        JLabel particleLabel = new JLabel("Particle Parameters:");
        particleXField = new JTextField(5);
        particleYField = new JTextField(5);
        particleAngleField = new JTextField(5);
        particleVelocityField = new JTextField(5);
        particleCountField = new JTextField(5);
        JButton addParticleButton = new JButton("Add Particle");
        addParticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double x = Double.parseDouble(particleXField.getText());
                double y = Double.parseDouble(particleYField.getText());
                double angle = Double.parseDouble(particleAngleField.getText());
                double velocity = Double.parseDouble(particleVelocityField.getText());
                int count = Integer.parseInt(particleCountField.getText());
                for (int i = 0; i < count; i++) {
                    canvas.addParticle(x, y, angle, velocity);
                }
            }
        });

        // input fields for adding walls
        JLabel wallLabel = new JLabel("Wall Parameters:");
        wallX1Field = new JTextField(5);
        wallY1Field = new JTextField(5);
        wallX2Field = new JTextField(5);
        wallY2Field = new JTextField(5);
        JButton addWallButton = new JButton("Add Wall");
        addWallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x1 = Integer.parseInt(wallX1Field.getText());
                int y1 = Integer.parseInt(wallY1Field.getText());
                int x2 = Integer.parseInt(wallX2Field.getText());
                int y2 = Integer.parseInt(wallY2Field.getText());
                canvas.addWall(x1, y1, x2, y2);
            }
        });

        // Particle input panel
        JPanel particleInputPanel = new JPanel();
        particleInputPanel.setLayout(new GridLayout(2, 1));
        JPanel particleFieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        particleFieldsPanel.add(new JLabel("X:"));
        particleFieldsPanel.add(particleXField);
        particleFieldsPanel.add(new JLabel("Y:"));
        particleFieldsPanel.add(particleYField);
        particleFieldsPanel.add(new JLabel("Angle:"));
        particleFieldsPanel.add(particleAngleField);
        particleFieldsPanel.add(new JLabel("Velocity:"));
        particleFieldsPanel.add(particleVelocityField);
        particleFieldsPanel.add(new JLabel("Count:"));
        particleFieldsPanel.add(particleCountField);
        particleInputPanel.add(particleFieldsPanel);

        // Wall input panel
        JPanel wallInputPanel = new JPanel();
        wallInputPanel.setLayout(new GridLayout(2, 1));
        JPanel wallFieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wallFieldsPanel.add(new JLabel("X1:"));
        wallFieldsPanel.add(wallX1Field);
        wallFieldsPanel.add(new JLabel("Y1:"));
        wallFieldsPanel.add(wallY1Field);
        wallFieldsPanel.add(new JLabel("X2:"));
        wallFieldsPanel.add(wallX2Field);
        wallFieldsPanel.add(new JLabel("Y2:"));
        wallFieldsPanel.add(wallY2Field);
        wallInputPanel.add(wallFieldsPanel);

        // Particle button panel
        JPanel particleButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        particleButtonPanel.add(addParticleButton);

        // Wall button panel
        JPanel wallButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wallButtonPanel.add(addWallButton);

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2));
        bottomPanel.add(particleInputPanel);
        bottomPanel.add(particleButtonPanel);
        bottomPanel.add(wallInputPanel);
        bottomPanel.add(wallButtonPanel);
        contentPane.add(canvas, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(contentPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParticleSimulator::new);
    }
}
