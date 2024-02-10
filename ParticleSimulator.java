import javax.swing.*;
import java.awt.*;

public class ParticleSimulator extends JFrame {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public ParticleSimulator() {
        setTitle("Particle Simulator");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Canvas canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        add(canvas);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParticleSimulator::new);
    }
}
