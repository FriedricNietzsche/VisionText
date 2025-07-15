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
    private JFrame frame;
    private LoginService loginService;
    private OCRUseCase ocrUseCase;
    private HistoryService historyService;
    private ErrorHandler errorHandler;

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
        frame = new JFrame("VisionText - OCR App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        showLoginScreen();
        frame.setVisible(true);
    }

    public void showLoginScreen() {
        frame.setContentPane(new LoginPanel(this, loginService, errorHandler));
        frame.revalidate();
    }

    public void showDashboard(String username) {
        frame.setContentPane(new DashboardPanel(this, ocrUseCase, historyService, loginService, errorHandler, username));
        frame.revalidate();
    }

    public void showHistory(String username) {
        frame.setContentPane(new HistoryPanel(this, historyService, loginService, errorHandler, username));
        frame.revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainAppUI::new);
    }
} 