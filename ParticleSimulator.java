import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParticleSimulator extends JFrame {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private Canvas canvas;

    public ParticleSimulator() {
        setTitle("Particle Physics Simulator");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JButton addParticleButton = new JButton("Add Particle");
        addParticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show input dialog for particle parameters
                JPanel particlePanel = new JPanel(new GridLayout(4, 2));
                particlePanel.add(new JLabel("X coordinate:"));
                JTextField xField = new JTextField(10);
                particlePanel.add(xField);
                particlePanel.add(new JLabel("Y coordinate:"));
                JTextField yField = new JTextField(10);
                particlePanel.add(yField);
                particlePanel.add(new JLabel("Angle (in degrees):"));
                JTextField angleField = new JTextField(10);
                particlePanel.add(angleField);
                particlePanel.add(new JLabel("Velocity (in pixels/second):"));
                JTextField velocityField = new JTextField(10);
                particlePanel.add(velocityField);

                int result = JOptionPane.showConfirmDialog(null, particlePanel,
                        "Enter Particle Parameters", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    double x = Double.parseDouble(xField.getText());
                    double y = Double.parseDouble(yField.getText());
                    double angle = Double.parseDouble(angleField.getText());
                    double velocity = Double.parseDouble(velocityField.getText());
                    canvas.addParticle(x, y, angle, velocity);
                }
            }
        });

        JButton addWallButton = new JButton("Add Wall");
        addWallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show input dialog for wall parameters
                JPanel wallPanel = new JPanel(new GridLayout(2, 2));
                wallPanel.add(new JLabel("X1 coordinate:"));
                JTextField x1Field = new JTextField(10);
                wallPanel.add(x1Field);
                wallPanel.add(new JLabel("Y1 coordinate:"));
                JTextField y1Field = new JTextField(10);
                wallPanel.add(y1Field);
                wallPanel.add(new JLabel("X2 coordinate:"));
                JTextField x2Field = new JTextField(10);
                wallPanel.add(x2Field);
                wallPanel.add(new JLabel("Y2 coordinate:"));
                JTextField y2Field = new JTextField(10);
                wallPanel.add(y2Field);

                int result = JOptionPane.showConfirmDialog(null, wallPanel,
                        "Enter Wall Parameters", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    int x1 = Integer.parseInt(x1Field.getText());
                    int y1 = Integer.parseInt(y1Field.getText());
                    int x2 = Integer.parseInt(x2Field.getText());
                    int y2 = Integer.parseInt(y2Field.getText());
                    canvas.addWall(x1, y1, x2, y2);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addParticleButton);
        buttonPanel.add(addWallButton);

        add(canvas, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParticleSimulator::new);
    }
}
