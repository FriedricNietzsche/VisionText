package ui;

import application.LoginService;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private final MainAppUI mainApp;
    private final LoginService loginService;
    private final ErrorHandler errorHandler;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(MainAppUI mainApp, LoginService loginService, ErrorHandler errorHandler) {
        this.mainApp = mainApp;
        this.loginService = loginService;
        this.errorHandler = errorHandler;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(32, 24, 32, 24));
        add(center, BorderLayout.CENTER);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), arc = 20;
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 10, w - 12, h - 12, arc, arc);
                GradientPaint gp = new GradientPaint(0, 0, Theme.CARD_TOP, 0, h, Theme.CARD_BOTTOM);
                g2.setPaint(gp); g2.fillRoundRect(0, 4, w, h - 8, arc, arc);
                g2.setColor(Theme.OUTLINE); g2.drawRoundRect(0, 4, w - 1, h - 9, arc, arc);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        Dimension cardSize = new Dimension(460, 320);
        card.setMaximumSize(cardSize); card.setPreferredSize(cardSize);

        center.add(Box.createVerticalGlue());
        center.add(card);
        center.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("VisionText Terminal");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Theme.TEXT);
        card.add(title, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(90, 100, 115));
        card.add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setDisplayedMnemonic('E');
        card.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        emailField = makeTextField(24);
        emailLabel.setLabelFor(emailField);
        card.add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setDisplayedMnemonic('P');
        card.add(passLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = makePasswordField(24);
        passLabel.setLabelFor(passwordField);
        card.add(passwordField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new BoxLayout(btnRow, BoxLayout.X_AXIS));

        AnimatedButton loginBtn = new AnimatedButton("Login");
        AnimatedButton registerBtn = new AnimatedButton("Register");
        Dimension btnSize = new Dimension(140, 44);
        loginBtn.setPreferredSize(btnSize); loginBtn.setMaximumSize(btnSize);
        registerBtn.setPreferredSize(btnSize); registerBtn.setMaximumSize(btnSize);
        loginBtn.setToolTipText("Sign in (Enter)");
        registerBtn.setToolTipText("Create a new account");

        btnRow.add(loginBtn); btnRow.add(Box.createHorizontalStrut(12)); btnRow.add(registerBtn);
        card.add(btnRow, gbc);

        loginBtn.addActionListener(this::onLogin);
        registerBtn.addActionListener(this::onRegister);

        // Enter submits login
        Action loginAction = new AbstractAction(){ @Override public void actionPerformed(ActionEvent e){ onLogin(e); }};
        emailField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "login");
        emailField.getActionMap().put("login", loginAction);
        passwordField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "login");
        passwordField.getActionMap().put("login", loginAction);
    }

    private JTextField makeTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(Theme.FONT);
        tf.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        tf.setBackground(Color.WHITE);
        return tf;
    }

    private JPasswordField makePasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(Theme.FONT);
        pf.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        pf.setBackground(Color.WHITE);
        return pf;
    }

    private void onLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            errorHandler.showError("Please enter both email and password.");
            return;
        }
        boolean success = loginService.login(email, password);
        if (success) {
            mainApp.showDashboard(email);
        } else {
            errorHandler.showError("Login failed. Please check your credentials.");
        }
    }

    private void onRegister(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            errorHandler.showError("Please enter both email and password.");
            return;
        }
        boolean success = loginService.register(email, password);
        if (success) {
            mainApp.showDashboard(email);
        } else {
            errorHandler.showError("Registration failed. Email may already be in use.");
        }
    }
}
