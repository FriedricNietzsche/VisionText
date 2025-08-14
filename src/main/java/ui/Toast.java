package ui;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.Window;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

/**
 * Toast notification utility for showing temporary messages in the UI.
 * @author VisionText
 * @since 1.0
 * @null
 */
public class Toast {
    private static final int DEFAULT_DURATION = 3000;
    private static final int TOAST_MAX_WIDTH = 300;
    private static final int TOAST_MAX_HEIGHT = 50;
    private static final int TOAST_BOTTOM_MARGIN = 50;
    private static final int TOAST_BG_ALPHA = 200;
    private static final int TOAST_BORDER_ALPHA = 50;
    private static final int TOAST_BORDER_RADIUS = 8;
    private static final int TOAST_BORDER_WIDTH = 1;
    private static final int TOAST_PADDING_VERTICAL = 12;
    private static final int TOAST_PADDING_HORIZONTAL = 16;

    /**
     * Show a toast message for the default duration.
     * @param parent the parent component
     * @param message the message to display
     */
    public static void show(Component parent, String message) {
        show(parent, message, DEFAULT_DURATION);
    }

    /**
     * Show a toast message for a custom duration (in milliseconds).
     * @param parent the parent component
     * @param message the message to display
     * @param duration the duration in milliseconds
     */
    public static void show(Component parent, String message, int duration) {
        SwingUtilities.invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(parent);
            if (window == null) {
                return;
            }
            JPanel toast = createToastPanel(message);
            JPanel glassPane = new JPanel();
            glassPane.setOpaque(false);
            glassPane.setLayout(new BorderLayout());
            JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
            container.setOpaque(false);
            container.setBorder(BorderFactory.createEmptyBorder(0, 0, TOAST_BOTTOM_MARGIN, 0));
            container.add(toast);
            glassPane.add(container, BorderLayout.SOUTH);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                Component oldGlassPane = frame.getGlassPane();
                frame.setGlassPane(glassPane);
                glassPane.setVisible(true);
                Timer timer = new Timer(duration, event -> {
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
                Timer timer = new Timer(duration, event -> {
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
        panel.setBackground(new Color(0, 0, 0, TOAST_BG_ALPHA));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, TOAST_BORDER_ALPHA), TOAST_BORDER_WIDTH),
            BorderFactory.createEmptyBorder(TOAST_PADDING_VERTICAL, TOAST_PADDING_HORIZONTAL, TOAST_PADDING_VERTICAL, TOAST_PADDING_HORIZONTAL)
        ));
        panel.putClientProperty("FlatLaf.style", "arc: " + TOAST_BORDER_RADIUS);
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(Theme.Fonts.BODY);
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(TOAST_MAX_WIDTH, TOAST_MAX_HEIGHT));
        return panel;
    }
}
