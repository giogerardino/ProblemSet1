import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ParticleSimulator simulator = new ParticleSimulator();
            simulator.add(new Canvas());
        });
    }
}
