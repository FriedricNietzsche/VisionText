package ui;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
    private String lastView = "login";
    private static final String UI_PREFS = "ui.properties";
    private static final String KEY_W = "win.w";
    private static final String KEY_H = "win.h";
    private static final String KEY_X = "win.x";
    private static final String KEY_Y = "win.y";
    private static final String KEY_VIEW = "last.view";

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

    restoreWindowPrefs();
    addMenu();
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { saveWindowPrefs(); }
        });
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
    lastView = LOG_IN;
    }

    // Existing API retained for logins (no welcome toast)
    public void showDashboard(String username) {
        showDashboard(username, false);
    }

    // New method allowing a welcome toast for newly registered users
    public void showDashboard(String username, boolean showWelcomeToast) {
        String DASHBOARD = "dashboard";
        this.username = username;

        if (dashboardPanel != null) {
            mainPanel.remove(dashboardPanel);
        }
        dashboardPanel = new DashboardPanel(this, ocrUseCase, historyService, loginService, errorHandler, username);
        mainPanel.add(dashboardPanel, DASHBOARD);
        cardLayout.show(mainPanel, DASHBOARD);
        frame.setTitle("VisionText - Welcome, " + username.split("@")[0]);
        lastView = DASHBOARD;
        refreshTheme();

            if (showWelcomeToast && dashboardPanel != null) {
                SwingUtilities.invokeLater(() -> new WelcomeDialog(frame).setVisible(true));
        }
    }

    public void showHistory(String username) {
        if (historyPanel != null) {
            mainPanel.remove(historyPanel);
        }
        historyPanel = new HistoryPanel(this, historyService, loginService, errorHandler, username);
        mainPanel.add(historyPanel, "history");
        cardLayout.show(mainPanel, "history");
        frame.setTitle("VisionText - History");
    lastView = "history";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Load & apply persisted theme before creating UI
            Theme.loadPersistedTheme();
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

    private void addMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu app = new JMenu("App");
        JMenuItem toggleAnim = new JMenuItem(Theme.isFadeTransitions() ? "Disable Animations" : "Enable Animations");
        toggleAnim.addActionListener(e -> {
            Theme.setFadeTransitions(!Theme.isFadeTransitions());
            toggleAnim.setText(Theme.isFadeTransitions() ? "Disable Animations" : "Enable Animations");
        });
        JMenuItem savePrefs = new JMenuItem("Save Layout");
        savePrefs.addActionListener(e -> saveWindowPrefs());
    JMenuItem settings = new JMenuItem("Settings...");
    settings.addActionListener(e -> new SettingsDialog(frame).setVisible(true));
    app.add(settings);
    app.add(toggleAnim);
    app.add(savePrefs);
        bar.add(app);
        frame.setJMenuBar(bar);
    }

    private void restoreWindowPrefs() {
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(UI_PREFS)) {
            p.load(fis);
            int w = Integer.parseInt(p.getProperty(KEY_W, "0"));
            int h = Integer.parseInt(p.getProperty(KEY_H, "0"));
            int x = Integer.parseInt(p.getProperty(KEY_X, "-1"));
            int y = Integer.parseInt(p.getProperty(KEY_Y, "-1"));
            lastView = p.getProperty(KEY_VIEW, "login");
            if (w > 0 && h > 0) frame.setSize(new Dimension(w, h));
            if (x >= 0 && y >= 0) frame.setLocation(x, y);
            // Defer showing last view until after login (simple approach)
        } catch (IOException ignored) { }
    }

    public void saveWindowPrefs() {
        Properties p = new Properties();
        Dimension d = frame.getSize();
        Point loc = frame.getLocation();
        p.setProperty(KEY_W, Integer.toString(d.width));
        p.setProperty(KEY_H, Integer.toString(d.height));
        p.setProperty(KEY_X, Integer.toString(loc.x));
        p.setProperty(KEY_Y, Integer.toString(loc.y));
        p.setProperty(KEY_VIEW, lastView);
        try (FileOutputStream fos = new FileOutputStream(UI_PREFS)) {
            p.store(fos, "VisionText UI preferences");
        } catch (IOException ignored) { }
    }
}
