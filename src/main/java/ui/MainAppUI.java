package ui;

import ui.Theme;
import application.LoginService;
import application.OCRUseCase;
import application.HistoryService;
import infrastructure.FirebaseAuthService;
import infrastructure.OCRSpaceService;
import infrastructure.FirebaseHistoryRepository;
import shared.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainAppUI {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private HistoryPanel historyPanel;
    public JFrame frame;
    private final LoginService loginService;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private final ErrorHandler errorHandler;
    private String username;

    public MainAppUI() {
        Config.load();
        // Compose infrastructure implementations
        var authService = new FirebaseAuthService();
        var ocrProcessor = new OCRSpaceService();
        var historyRepository = new FirebaseHistoryRepository();
        // Compose application services
        loginService = new LoginService(authService);
        ocrUseCase = new OCRUseCase(ocrProcessor);
        historyService = new HistoryService(historyRepository);
        errorHandler = new ErrorHandler();
        initUI();
    }

    private void initUI() {
        String LOG_IN = "login";

        frame = new JFrame("VisionText");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.setMinimumSize(new Dimension(900, 600));

        // Set modern icon and window properties
        frame.setIconImage(createAppIcon());
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Theme.getBackgroundColor());

        // Initialize panels
        loginPanel = new LoginPanel(this, loginService, errorHandler);

        // Add panels to card layout
        mainPanel.add(loginPanel, LOG_IN);
        frame.add(mainPanel);
        cardLayout.show(mainPanel, LOG_IN);

        frame.setVisible(true);
        frame.pack();
    }

    private Image createAppIcon() {
        // Create a simple modern app icon
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background circle
        g2.setColor(Theme.getPrimaryColor());
        g2.fillRoundRect(8, 8, 48, 48, 12, 12);

        // Eye icon (representing vision)
        g2.setColor(Color.WHITE);
        g2.fillOval(20, 24, 24, 16);
        g2.setColor(Theme.getPrimaryColor());
        g2.fillOval(28, 28, 8, 8);

        g2.dispose();
        return icon;
    }

    public void showLoginScreen() {
        String LOG_IN = "login";
        if (loginPanel != null) {
            mainPanel.remove(loginPanel);
        }
        loginPanel = new LoginPanel(this, loginService, errorHandler);
        mainPanel.add(loginPanel, LOG_IN);
        cardLayout.show(mainPanel, LOG_IN);
        frame.setTitle("VisionText");
        frame.repaint();
    }

    public void showDashboard(String username) {
        String DASHBOARD = "dashboard";
        this.username = username;

        if (dashboardPanel != null) {
            mainPanel.remove(dashboardPanel);
        }
        dashboardPanel = new DashboardPanel(this, ocrUseCase, historyService, loginService, errorHandler, username);
        mainPanel.add(dashboardPanel, DASHBOARD);
        cardLayout.show(mainPanel, DASHBOARD);
        frame.setTitle("VisionText - Welcome, " + username.split("@")[0]);
    refreshTheme();
    }

    public void showHistory(String username) {
        if (historyPanel != null) {
            mainPanel.remove(historyPanel);
        }
        historyPanel = new HistoryPanel(this, historyService, loginService, errorHandler, username);
        mainPanel.add(historyPanel, "history");
        cardLayout.show(mainPanel, "history");
        frame.setTitle("VisionText - History");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Apply modern theme before creating UI
            Theme.applyModernTheme();
            new MainAppUI();
        });
    }

    public String getCurrentUser() {
        return this.username;
    }

    // Refresh backgrounds and panel-specific themed components after a theme toggle
    public void refreshTheme() {
        if (mainPanel != null) mainPanel.setBackground(Theme.getBackgroundColor());
    if (loginPanel != null) loginPanel.refreshTheme();
        if (dashboardPanel != null) dashboardPanel.refreshTheme();
        if (historyPanel != null) historyPanel.refreshTheme();
        if (frame != null) {
            frame.getContentPane().setBackground(Theme.getBackgroundColor());
            SwingUtilities.updateComponentTreeUI(frame);
            frame.repaint();
        }
    }
}
