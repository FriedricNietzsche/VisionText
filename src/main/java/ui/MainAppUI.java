package ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import application.HistoryService;
import application.LoginService;
import application.OCRUseCase;
import infrastructure.FirebaseAuthService;
import infrastructure.FirebaseHistoryRepository;
import infrastructure.OCRSpaceService;
import shared.Config;

/**
 * Main application UI controller.
 */
public class MainAppUI {
    private static final String UI_PREFS = "ui.properties";
    private static final String KEY_W = "win.w";
    private static final String KEY_H = "win.h";
    private static final String KEY_X = "win.x";
    private static final String KEY_Y = "win.y";
    private static final String KEY_VIEW = "last.view";
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 800;
    private static final int FRAME_MIN_WIDTH = 900;
    private static final int FRAME_MIN_HEIGHT = 600;
    private static final int ICON_SIZE = 64;
    private static final int ICON_CIRCLE_SIZE = 48;
    private static final int ICON_CIRCLE_ARC = 12;
    private static final int ICON_EYE_X = 20;
    private static final int ICON_EYE_Y = 24;
    private static final int ICON_EYE_WIDTH = 24;
    private static final int ICON_EYE_HEIGHT = 16;
    public static final String HISTORY = "history";
    public static final String LOGIN = "login";

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private HistoryPanel historyPanel;
    private final LoginService loginService;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private String username;
    private String lastView = LOGIN;

    public JFrame frame;

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
        initUI();
    }

    private void initUI() {
        final String LOGIN = MainAppUI.LOGIN;
        frame = new JFrame("VisionText");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));

        // Set modern icon and window properties
        frame.setIconImage(createAppIcon());
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Theme.getBackgroundColor());

        // Initialize panels
        loginPanel = new LoginPanel(this, loginService);

        // Add panels to card layout
        mainPanel.add(loginPanel, LOGIN);
        frame.add(mainPanel);
        cardLayout.show(mainPanel, LOGIN);

        restoreWindowPrefs();
        addMenu();
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                saveWindowPrefs();
            }
        });
        frame.setVisible(true);
        frame.pack();
    }

    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.getPrimaryColor());
        g2.fillRoundRect(8, 8, ICON_CIRCLE_SIZE, ICON_CIRCLE_SIZE, ICON_CIRCLE_ARC, ICON_CIRCLE_ARC);
        g2.setColor(java.awt.Color.WHITE);
        g2.fillOval(ICON_EYE_X, ICON_EYE_Y, ICON_EYE_WIDTH, ICON_EYE_HEIGHT);
        g2.setColor(Theme.getPrimaryColor());
        g2.fillOval(28, 28, 8, 8);
        g2.dispose();
        return icon;
    }

    /**
     * Show the login screen.
     */
    public final void showLoginScreen() {
        String LOG_IN = LOGIN;
        if (loginPanel != null) {
            mainPanel.remove(loginPanel);
        }
        loginPanel = new LoginPanel(this, loginService);
        mainPanel.add(loginPanel, LOG_IN);
        cardLayout.show(mainPanel, LOG_IN);
        frame.setTitle("VisionText");
        frame.repaint();
        lastView = LOG_IN;
    }

    /**
     * Show the dashboard panel for the given username.
     * @param username the username of the logged-in user
     */
    public final void showDashboard(String username) {
        String DASHBOARD = "dashboard";
        this.username = username;

        if (dashboardPanel != null) {
            mainPanel.remove(dashboardPanel);
        }
        dashboardPanel = new DashboardPanel(this, ocrUseCase, historyService, loginService, username);
        mainPanel.add(dashboardPanel, DASHBOARD);
        cardLayout.show(mainPanel, DASHBOARD);
        frame.setTitle("VisionText - Welcome, " + username.split("@")[0]);
        lastView = DASHBOARD;
        refreshTheme();
    }

    /**
     * Show the history panel for the given username.
     * @param username the username of the logged-in user
     */
    public void showHistory(String username) {
        if (historyPanel != null) {
            mainPanel.remove(historyPanel);
        }
        historyPanel = new HistoryPanel(this, historyService, username);
        mainPanel.add(historyPanel, HISTORY);
        cardLayout.show(mainPanel, HISTORY);
        frame.setTitle("VisionText - History");
    lastView = HISTORY;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Load & apply persisted theme before creating UI
            Theme.loadPersistedTheme();
            Theme.applyModernTheme();
            new MainAppUI();
        });
    }

    public final String getCurrentUser() {
        return this.username;
    }

    // Refresh backgrounds and panel-specific themed components after a theme toggle
    public final void refreshTheme() {
        if (mainPanel != null) {
            mainPanel.setBackground(Theme.getBackgroundColor());
        }
        if (loginPanel != null) {
            loginPanel.refreshTheme();
        }
        if (dashboardPanel != null) {
            dashboardPanel.refreshTheme();
        }
        if (historyPanel != null) {
            historyPanel.refreshTheme();
        }
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
            lastView = p.getProperty(KEY_VIEW, LOGIN);
            if (w > 0 && h > 0) {
                frame.setSize(new Dimension(w, h));
            }
            if (x >= 0 && y >= 0) {
                frame.setLocation(x, y);
            }
            // Defer showing last view until after login (simple approach)
        }
        catch (IOException ignored) { }
    }

    /**
     * Save the current window size, position, and last view to preferences.
     */
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
        }
        catch (IOException ignored) { }
    }
}
