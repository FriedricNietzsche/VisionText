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

public class MainAppUI {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private HistoryPanel historyPanel;
    private JFrame frame;
    private final LoginService loginService;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private final ErrorHandler errorHandler;
    private String username ;

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
        String DASHBOARD = "dashboard";
        frame = new JFrame("VisionText - OCR App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //The size is now adjustable
        frame.setPreferredSize(new Dimension(800, 600));
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        // Initialize panels
        loginPanel = new LoginPanel(this,loginService,errorHandler);
        dashboardPanel = new DashboardPanel(this,ocrUseCase,historyService,loginService,errorHandler,username);
        historyPanel = new HistoryPanel(this,historyService,loginService,errorHandler,username);

        //card panels to card layout
        mainPanel.add(loginPanel,LOG_IN);
        mainPanel.add(dashboardPanel,DASHBOARD);
        mainPanel.add(historyPanel,"history");
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void showLoginScreen() {
        String LOG_IN = "login";
        loginPanel = new LoginPanel(this,loginService,errorHandler);
        mainPanel.remove(loginPanel);
        mainPanel.add(loginPanel,LOG_IN);
        cardLayout.show(mainPanel,LOG_IN);
        frame.setTitle("VisionText - OCR App");
        frame.pack();
        frame.repaint();

    }

    public void showDashboard(String username) {
        String DASHBOARD = "dashboard";
        this.username = username;
        dashboardPanel = new DashboardPanel(this,ocrUseCase,historyService,loginService,errorHandler,username);
        mainPanel.remove(dashboardPanel);
        mainPanel.add(dashboardPanel,DASHBOARD);
        cardLayout.show(mainPanel, DASHBOARD);
        frame.setTitle("VisionText  Welcome" +" " + username);
    }

    public void showHistory(String username) {
        historyPanel = new HistoryPanel(this,historyService,loginService,errorHandler,username);
        cardLayout.show(mainPanel, "history");
        frame.setTitle("VisionText - History");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainAppUI::new);
    }

    public String getCurrentUser() {
        return this.username;
    }
}