package ui;

import application.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginPanel extends JPanel implements ThemeAware {
    private final MainAppUI mainApp;
    private final LoginService loginService;
    private final ErrorHandler errorHandler;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton themeToggleBtn;

    public LoginPanel(MainAppUI mainApp, LoginService loginService, ErrorHandler errorHandler) {
        this.mainApp = mainApp;
        this.loginService = loginService;
        this.errorHandler = errorHandler;
        initUI();
        Theme.addListener(this);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.getBackgroundColor());

        // Main container
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.LG, Theme.Spacing.XXL, Theme.Spacing.LG));

        JPanel loginCard = createLoginCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        container.add(loginCard, gbc);

        add(container, BorderLayout.CENTER);
        add(createTopBar(), BorderLayout.NORTH);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(Theme.Spacing.MD, Theme.Spacing.MD, 0, Theme.Spacing.MD));

        themeToggleBtn = new ModernButton("🌙", ModernButton.Style.GHOST);
        themeToggleBtn.setToolTipText("Toggle dark/light theme");
        themeToggleBtn.setPreferredSize(new Dimension(40, 40));
        themeToggleBtn.addActionListener(e -> toggleTheme());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(themeToggleBtn);

        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createLoginCard() {
        final int CARD_WIDTH = 420;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.getSurfaceColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorderColor(), 1, true),
                new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.XXL, Theme.Spacing.XXL, Theme.Spacing.XXL)
        ));
        card.putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.LG);

        // Let height be computed from content. Constrain width only.
        card.setMaximumSize(new Dimension(CARD_WIDTH, Integer.MAX_VALUE));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(createHeader());
        card.add(Box.createVerticalStrut(Theme.Spacing.XXL));
        card.add(createForm());
        card.add(Box.createVerticalStrut(Theme.Spacing.LG));
        card.add(createButtons());
        card.add(Box.createVerticalStrut(Theme.Spacing.SM)); // small bottom padding

        return card;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel("👁️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(iconLabel);

        header.add(Box.createVerticalStrut(Theme.Spacing.MD));

        JLabel title = new JLabel("VisionText");
        title.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 28));
        title.setForeground(Theme.getTextColor());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);

        header.add(Box.createVerticalStrut(Theme.Spacing.SM));

        JLabel subtitle = new JLabel("Sign in to extract text from images");
        subtitle.setFont(Theme.Fonts.BODY);
        subtitle.setForeground(Theme.getSecondaryTextColor());
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(subtitle);

        return header;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridwidth = 1;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.Fonts.BODY_MEDIUM);
        emailLabel.setForeground(Theme.getTextColor());

        g.gridy = 0;
        g.insets = new Insets(0, 0, Theme.Spacing.SM, 0);
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.WEST;
        form.add(emailLabel, g);

        emailField = createModernTextField("Enter your email");
        g.gridy = 1;
        g.insets = new Insets(0, 0, Theme.Spacing.MD, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.CENTER;
        form.add(emailField, g);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(Theme.Fonts.BODY_MEDIUM);
        passwordLabel.setForeground(Theme.getTextColor());

        g.gridy = 2;
        g.insets = new Insets(0, 0, Theme.Spacing.SM, 0);
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.WEST;
        form.add(passwordLabel, g);

        passwordField = createModernPasswordField("Enter your password");
        g.gridy = 3;
        g.insets = new Insets(0, 0, 0, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.CENTER;
        form.add(passwordField, g);

        return form;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setFont(Theme.Fonts.BODY);

        int h = 44;
        field.setPreferredSize(new Dimension(320, h));                 // initial
        field.setMinimumSize(new Dimension(200, h));                    // avoid too narrow
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));      // full width

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.putClientProperty("JComponent.outline", "focus"); }
            @Override public void focusLost (FocusEvent e) { field.putClientProperty("JComponent.outline", null); }
        });
        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setFont(Theme.Fonts.BODY);

        int h = 44;
        field.setPreferredSize(new Dimension(320, h));
        field.setMinimumSize(new Dimension(200, h));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.putClientProperty("JComponent.outline", "focus"); }
            @Override public void focusLost (FocusEvent e) { field.putClientProperty("JComponent.outline", null); }
        });
        return field;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn = new ModernButton("Sign In", ModernButton.Style.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(320, 44));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(this::onLogin);

        buttons.add(loginBtn);
        buttons.add(Box.createVerticalStrut(Theme.Spacing.MD));

        registerBtn = new ModernButton("Create Account", ModernButton.Style.SECONDARY);
        registerBtn.setPreferredSize(new Dimension(320, 44));
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(this::onRegister);

        buttons.add(registerBtn);

        // Make sure we always have both buttons visible—no clipping
        buttons.add(Box.createVerticalStrut(Theme.Spacing.SM));

        setupKeyboardShortcuts();
        return buttons;
    }

    private void setupKeyboardShortcuts() {
        Action loginAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onLogin(e); }
        };
        emailField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        emailField.addActionListener(e -> passwordField.requestFocus());
    }

    private void toggleTheme() {
        Theme.toggleTheme();
        themeToggleBtn.setText(Theme.isDarkMode() ? "☀️" : "🌙");
        SwingUtilities.updateComponentTreeUI(this);
        mainApp.frame.repaint();
    }

    private void onLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorHandler.showError("Please enter both email and password.");
            return;
        }

        setButtonsEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return loginService.login(email, password); }
            @Override protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        mainApp.showDashboard(email);
                    } else {
                        errorHandler.showError("Login failed. Please check your credentials.");
                    }
                } catch (Exception ex) {
                    errorHandler.showError("Login error: " + ex.getMessage());
                } finally {
                    setButtonsEnabled(true);
                }
            }
        }.execute();
    }

    private void onRegister(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorHandler.showError("Please enter both email and password.");
            return;
        }

        setButtonsEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return loginService.register(email, password); }
            @Override protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        mainApp.showDashboard(email);
                    } else {
                        errorHandler.showError("Registration failed. Email may already be in use or password is too weak.");
                    }
                } catch (Exception ex) {
                    errorHandler.showError("Registration error: " + ex.getMessage());
                } finally {
                    setButtonsEnabled(true);
                }
            }
        }.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        loginBtn.setEnabled(enabled);
        registerBtn.setEnabled(enabled);
        loginBtn.setText(enabled ? "Sign In" : "Signing in...");
        registerBtn.setText(enabled ? "Create Account" : "Creating...");
    }

    public void refreshTheme() {
        setBackground(Theme.getBackgroundColor());
        SwingUtilities.invokeLater(() -> {
            updateColors(this);
            repaint();
        });
    }

    @Override
    public void onThemeChanged(Color previousBackground) {
        refreshTheme();
    }

    private void updateColors(Component c) {
        if (c instanceof JLabel) {
            ((JLabel) c).setForeground(Theme.getTextColor());
        }
        if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            if (p.isOpaque()) p.setBackground(Theme.getBackgroundColor());
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) updateColors(child);
        }
    }
}
