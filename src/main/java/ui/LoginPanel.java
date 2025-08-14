package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import application.LoginService;

/**
 * Login panel for user authentication.
 */
public class LoginPanel extends JPanel implements ThemeAware {
    public static final String JCOMPONENT_OUTLINE = "JComponent.outline";
    private final MainAppUI mainApp;
    private final LoginService loginService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton themeToggleBtn;

    public LoginPanel(MainAppUI mainApp, LoginService loginService) {
        this.mainApp = mainApp;
        this.loginService = loginService;
        initUI();
        Theme.addListener(this);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.getBackgroundColor());

        // Main container with proper spacing
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.LG, Theme.Spacing.XXL, Theme.Spacing.LG));

        // Login card
        JPanel loginCard = createLoginCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        container.add(loginCard, gbc);

        add(container, BorderLayout.CENTER);

        // Theme toggle button in top-right corner
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(themeToggleBtn);

        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.getSurfaceColor());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.getBorderColor(), 1, true),
            new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.XXL, Theme.Spacing.XXL, Theme.Spacing.XXL)
        ));

        // Set rounded corners (this will be handled by FlatLaf)
        card.putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.LG);

        Dimension cardSize = new Dimension(400, 480);
        card.setPreferredSize(cardSize);
        card.setMaximumSize(cardSize);

        // Header
        card.add(createHeader());
        card.add(Box.createVerticalStrut(Theme.Spacing.XXL));

        // Form
        card.add(createForm());
        card.add(Box.createVerticalStrut(Theme.Spacing.LG));

        // Buttons
        card.add(createButtons());

        return card;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App icon
        JLabel iconLabel = new JLabel("👁️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(iconLabel);

        header.add(Box.createVerticalStrut(Theme.Spacing.MD));

        // Title
        JLabel title = new JLabel("VisionText");
        title.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 28));
        title.setForeground(Theme.getTextColor());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);

        header.add(Box.createVerticalStrut(Theme.Spacing.SM));

        // Subtitle
        JLabel subtitle = new JLabel("Sign in to extract text from images");
        subtitle.setFont(Theme.Fonts.BODY);
        subtitle.setForeground(Theme.getSecondaryTextColor());
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(subtitle);

        return header;
    }

    private JPanel createForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.Fonts.BODY_MEDIUM);
        emailLabel.setForeground(Theme.getTextColor());
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = createModernTextField();
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(emailLabel);
        form.add(Box.createVerticalStrut(Theme.Spacing.SM));
        form.add(emailField);
        form.add(Box.createVerticalStrut(Theme.Spacing.MD));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(Theme.Fonts.BODY_MEDIUM);
        passwordLabel.setForeground(Theme.getTextColor());
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = createModernPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(Theme.Spacing.SM));
        form.add(passwordField);

        return form;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", "Enter your email");
        field.setFont(Theme.Fonts.BODY);
        field.setPreferredSize(new Dimension(320, 44));
        field.setMaximumSize(new Dimension(320, 44));

        // Add focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.putClientProperty(JCOMPONENT_OUTLINE, "focus");
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.putClientProperty(JCOMPONENT_OUTLINE, null);
            }
        });

        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.putClientProperty("JTextField.placeholderText", "Enter your password");
        field.setFont(Theme.Fonts.BODY);
        field.setPreferredSize(new Dimension(320, 44));
        field.setMaximumSize(new Dimension(320, 44));

        // Add focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.putClientProperty(JCOMPONENT_OUTLINE, "focus");
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.putClientProperty(JCOMPONENT_OUTLINE, null);
            }
        });

        return field;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button (primary)
        loginBtn = new ModernButton("Sign In", ModernButton.Style.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(320, 44));
        loginBtn.setMaximumSize(new Dimension(320, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(this::onLogin);

        buttons.add(loginBtn);
        buttons.add(Box.createVerticalStrut(Theme.Spacing.MD));

        // Register button (secondary)
        registerBtn = new ModernButton("Create Account", ModernButton.Style.SECONDARY);
        registerBtn.setPreferredSize(new Dimension(320, 44));
        registerBtn.setMaximumSize(new Dimension(320, 44));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(this::onRegister);

        buttons.add(registerBtn);

        // Setup keyboard shortcuts
        setupKeyboardShortcuts();

        return buttons;
    }

    private void setupKeyboardShortcuts() {
        // Enter key triggers login
        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin(e);
            }
        };

        emailField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);

        // Focus management
        emailField.addActionListener(e -> passwordField.requestFocus());
    }

    private void toggleTheme() {
        Theme.toggleTheme();
        themeToggleBtn.setText(Theme.isDarkMode() ? "☀️" : "🌙");

        // Update all components
        SwingUtilities.updateComponentTreeUI(this);
        mainApp.frame.repaint();
    }

    private void onLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            ErrorHandler.showError("Please enter both email and password.");
            return;
        }

        // Disable buttons during login
        setButtonsEnabled(false);

        // Simulate async login (you can make this actually async if needed)
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return loginService.login(email, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        mainApp.showDashboard(email);
                    }
                    else {
                        ErrorHandler.showError("Login failed. Please check your credentials.");
                    }
                }
                catch (Exception ex) {
                    ErrorHandler.showError("Login error: " + ex.getMessage());
                }
                finally {
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void onRegister(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            ErrorHandler.showError("Please enter both email and password.");
            return;
        }

        // Disable buttons during registration
        setButtonsEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return loginService.register(email, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Successful registration -> show dashboard with welcome dialog
                        mainApp.showDashboard(email, true);
                    } else {
                        ErrorHandler.showError("Registration failed. Email may already be in use or password is too weak.");
                    }
                }
                catch (Exception ex) {
                    ErrorHandler.showError("Registration error: " + ex.getMessage());
                }
                finally {
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        loginBtn.setEnabled(enabled);
        registerBtn.setEnabled(enabled);
        loginBtn.setText(enabled ? "Sign In" : "Signing in...");
        registerBtn.setText(enabled ? "Create Account" : "Creating...");
    }

    /**
     * Refresh the theme colors and update all components.
     */
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
            c.setForeground(Theme.getTextColor());
        }
        if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            if (p.isOpaque()) {
                p.setBackground(Theme.getBackgroundColor());
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                updateColors(child);
            }
        }
    }
}
