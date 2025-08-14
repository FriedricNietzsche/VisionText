package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal welcome dialog displayed immediately after a successful new user registration.
 */
public class WelcomeDialog extends JDialog implements ThemeAware {

    public WelcomeDialog(JFrame owner) {
        super(owner, "Welcome to Vision Text", true);
        initUI();
        Theme.addListener(this);
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setOpaque(true);
        content.setBackground(Theme.getSurfaceColor());
        content.setBorder(new EmptyBorder(32, 40, 32, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Thank you for joining Vision Text!");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 24));
        title.setForeground(Theme.getTextColor());

        content.add(title);
        content.add(Box.createVerticalStrut(16));

        JLabel subtitle = new JLabel("Extract your first image by clicking the 'Get Started' button!");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(Theme.Fonts.BODY);
        subtitle.setForeground(Theme.getSecondaryTextColor());
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setMaximumSize(new Dimension(460, 60));
        content.add(subtitle);

        content.add(Box.createVerticalStrut(24));

        ModernButton closeBtn = new ModernButton("Let's Go", ModernButton.Style.PRIMARY);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setPreferredSize(new Dimension(160, 44));
        closeBtn.addActionListener(e -> dispose());
        content.add(closeBtn);

        add(content, BorderLayout.CENTER);
        getRootPane().setDefaultButton(closeBtn);

        pack();
        setMinimumSize(new Dimension(520, getHeight()));
        setLocationRelativeTo(getOwner());
    }

    @Override
    public void onThemeChanged(Color previousBackground) { refreshTheme(); }

    public void refreshTheme() {
        getContentPane().setBackground(Theme.getSurfaceColor());
        SwingUtilities.updateComponentTreeUI(this);
    }
}
