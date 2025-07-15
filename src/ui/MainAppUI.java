package app;

import domain.UserManager;
import domain.OCRService;
import domain.HistoryManager;
import util.Config;
import javax.swing.*;
import java.awt.*;

public class MainAppUI {
    private JFrame frame;
    private UserManager userManager;
    private OCRService ocrService;
    private HistoryManager historyManager;
    private ErrorHandler errorHandler;

    public MainAppUI() {
        Config.load();
        userManager = new UserManager();
        ocrService = new OCRService();
        historyManager = new HistoryManager();
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
        frame.setContentPane(new LoginPanel(this, userManager, errorHandler));
        frame.revalidate();
    }

    public void showDashboard(String username) {
        frame.setContentPane(new DashboardPanel(this, ocrService, historyManager, userManager, errorHandler, username));
        frame.revalidate();
    }

    public void showHistory(String username) {
        frame.setContentPane(new HistoryPanel(this, historyManager, userManager, errorHandler, username));
        frame.revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainAppUI::new);
    }
} 