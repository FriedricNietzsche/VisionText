package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Toast {

    public static void show(Component parent, String message) {
        show(parent, message, 3000); // 3 seconds default
    }

    public static void show(Component parent, String message, int duration) {
        SwingUtilities.invokeLater(() -> {
            // Find the top-level window
            Window window = SwingUtilities.getWindowAncestor(parent);
            if (window == null) {
                return;
            }

            // Create toast panel
            JPanel toast = createToastPanel(message);

            // Create glass pane overlay
            JPanel glassPane = new JPanel();
            glassPane.setOpaque(false);
            glassPane.setLayout(new BorderLayout());

            // Position toast at bottom center
            JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
            container.setOpaque(false);
            container.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
            container.add(toast);

            glassPane.add(container, BorderLayout.SOUTH);

            // Set as glass pane
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                Component oldGlassPane = frame.getGlassPane();
                frame.setGlassPane(glassPane);
                glassPane.setVisible(true);

                // Auto-hide after duration
                Timer timer = new Timer(duration, e -> {
                    glassPane.setVisible(false);
                    frame.setGlassPane(oldGlassPane);
                });
                timer.setRepeats(false);
                timer.start();

            } else if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                Component oldGlassPane = dialog.getGlassPane();
                dialog.setGlassPane(glassPane);
                glassPane.setVisible(true);

                Timer timer = new Timer(duration, e -> {
                    glassPane.setVisible(false);
                    dialog.setGlassPane(oldGlassPane);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    private static JPanel createToastPanel(String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Apply rounded corners using FlatLaf
        panel.putClientProperty("FlatLaf.style", "arc: 8");

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(Theme.Fonts.BODY);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(300, 50));

        return panel;
    }
}
