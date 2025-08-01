package ui;

import application.OCRUseCase;
import application.HistoryService;
import application.LoginService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardPanel extends JPanel {
    private final MainAppUI mainApp;
    private CardLayout cardLayout;
    private JPanel topPanel;
    private final OCRUseCase ocrUseCase;
    private HistoryService historyService;
    private final LoginService loginService;
    private final ErrorHandler errorHandler;
    private final String username;


    public DashboardPanel(MainAppUI mainApp, OCRUseCase ocrUseCase, HistoryService historyService, LoginService loginService, ErrorHandler errorHandler, String username) {
        this.mainApp = mainApp;
        this.ocrUseCase = ocrUseCase;
        this.historyService = historyService;
        this.loginService = loginService;
        this.errorHandler = errorHandler;
        this.username = username;
        initUI();
    }
    private void initUI() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        topPanel = new JPanel(cardLayout);

        //Create panels
        JPanel mainMenuPanel = createMainMenuPanel();
        CreateVisionTextPanel createVisionTextPanel = new CreateVisionTextPanel(mainApp,ocrUseCase,historyService,errorHandler,username);
        HistoryPanel historyPanel = new HistoryPanel(mainApp, historyService, loginService, errorHandler, username);

        //Add panels to the card layout
        topPanel.add(mainMenuPanel, "mainMenu");
        topPanel.add(createVisionTextPanel, "CreateVisionText");
        topPanel.add(historyPanel, "history");
        add(topPanel, BorderLayout.CENTER);
    }

    private JPanel createMainMenuPanel() {
        JPanel mainMenuPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create New button to upload/save
        JButton createNew = new JButton("Create New VisionText");
        createNew.addActionListener(e -> cardLayout.show(topPanel, "CreateVisionText"));

        // Previous History - view/download/back
        JButton historyBtn = new JButton("History");
        historyBtn.addActionListener(e -> {
            HistoryPanel historyPanel = (HistoryPanel) topPanel.getComponent(2);
            historyPanel.loadHistory();
            cardLayout.show(topPanel, "history");
        });

        //Log out button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            loginService.logout();
            mainApp.showLoginScreen();
        });
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(250, 50);
        for (JButton btn : new JButton[]{createNew, historyBtn, logoutBtn}) {
            btn.setFont(buttonFont);
            btn.setSize(buttonSize);
            // Add hover effects
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(255, 233, 0)); // Light gray
                     }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(UIManager.getColor("Button.background"));
                }
            });
        }
            mainMenuPanel.add(createNew);
            mainMenuPanel.add(historyBtn);
            mainMenuPanel.add(logoutBtn);
        return mainMenuPanel;
    }

    public void showMainMenu() {
    cardLayout.show(topPanel, "mainMenu");}
}