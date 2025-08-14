package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Settings dialog for theme and animation preferences.
 */
public class SettingsDialog extends JDialog implements ThemeAware {
    private final JButton accentBtn;
    private final JCheckBox animationsBox;

    public SettingsDialog(JFrame owner) {
        super(owner, "Settings", true);
        setLayout(new BorderLayout(16,16));
        Theme.addListener(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        animationsBox = new JCheckBox("Enable animations (fade on theme toggle)", Theme.isFadeTransitions());
        animationsBox.setOpaque(false);
        animationsBox.addActionListener(e -> Theme.setFadeTransitions(animationsBox.isSelected()));

        accentBtn = new JButton("Change Accent Color");
        accentBtn.addActionListener(e -> chooseAccent());

        JButton resetAccentBtn = new JButton("Reset Accent Color");
        resetAccentBtn.addActionListener(e -> { Theme.clearCustomPrimary(); refreshAccentButton();});

        content.add(animationsBox);
        content.add(Box.createVerticalStrut(12));
        content.add(accentBtn);
        content.add(Box.createVerticalStrut(6));
        content.add(resetAccentBtn);

        add(content, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(close);
        add(south, BorderLayout.SOUTH);

        getRootPane().setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        pack();
        setSize(new Dimension(400, 230));
        setLocationRelativeTo(owner);
        refreshAccentButton();
    }

    private void chooseAccent() {
        Color initial = Theme.getCustomPrimary() == null ? Theme.getPrimaryColor() : Theme.getCustomPrimary();
        Color chosen = JColorChooser.showDialog(this, "Select Accent Color", initial);
        if (chosen != null) {
            Theme.setCustomPrimary(chosen);
            Theme.applyModernTheme();
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
                w.repaint();
            }
            refreshAccentButton();
        }
    }

    private void refreshAccentButton() {
        Color c = Theme.getCustomPrimary();
        accentBtn.setText(c == null ? "Change Accent Color (current: default)" :
                String.format("Change Accent Color (current: #%02X%02X%02X)", c.getRed(), c.getGreen(), c.getBlue()));
    }

    @Override
    public void onThemeChanged(Color previousBackground) {
        animationsBox.setSelected(Theme.isFadeTransitions());
        refreshAccentButton();
        repaint();
    }
}
