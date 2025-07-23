package ui;

import application.LoginService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private MainAppUI mainApp;
    private LoginService loginService;
    private ErrorHandler errorHandler;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(MainAppUI mainApp, LoginService loginService, ErrorHandler errorHandler) {
        this.mainApp = mainApp;
        this.loginService = loginService;
        this.errorHandler = errorHandler;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        add(btnPanel, gbc);

        loginBtn.addActionListener(this::onLogin);
        registerBtn.addActionListener(this::onRegister);
    }

    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorHandler.showError("Please enter both username and password.");
            return;
        }
        boolean success = loginService.login(username, password);
        if (success) {
            mainApp.showDashboard(username);
        } else {
            errorHandler.showError("Login failed. Please check your credentials.");
        }
    }

    private void onRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorHandler.showError("Please enter both username and password.");
            return;
        }
        boolean success = loginService.register(username, password);
        if (success) {
            mainApp.showDashboard(username);
        } else {
            errorHandler.showError("Registration failed. Username may already exist.");
        }
    }
} 