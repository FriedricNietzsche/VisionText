package ui;

import application.OCRUseCase;
import application.HistoryService;
import application.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final MainAppUI mainApp;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private final LoginService loginService;
    private final ErrorHandler errorHandler;
    private final String username;

    private CardLayout cardLayout;
    private JPanel topPanel;

    private CreateVisionTextPanel createVisionTextPanel;
    private HistoryPanel historyPanel;

    public DashboardPanel(MainAppUI mainApp,
                          OCRUseCase ocrUseCase,
                          HistoryService historyService,
                          LoginService loginService,
                          ErrorHandler errorHandler,
                          String username) {
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
        setBackground(Theme.getBackgroundColor());

        cardLayout = new CardLayout();
        topPanel = new JPanel(cardLayout);
        topPanel.setOpaque(false);

        JPanel mainMenuPanel = createMainMenuPanel();
        createVisionTextPanel = new CreateVisionTextPanel(mainApp, ocrUseCase, historyService, errorHandler, username);
        historyPanel = new HistoryPanel(mainApp, historyService, loginService, errorHandler, username);

        topPanel.add(mainMenuPanel, "mainMenu");
        topPanel.add(createVisionTextPanel, "CreateVisionText");
        topPanel.add(historyPanel, "history");

        add(topPanel, BorderLayout.CENTER);
    }

    private JPanel createMainMenuPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.LG, Theme.Spacing.XXL, Theme.Spacing.LG));

        // Header with user greeting
        JPanel header = createHeader();
        root.add(header, BorderLayout.NORTH);

        // Center: main action cards
        JPanel center = createActionCards();
        root.add(center, BorderLayout.CENTER);

        // Footer: user actions
        JPanel footer = createFooter();
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, Theme.Spacing.XXL, 0));

        // Welcome message
        String firstName = username.split("@")[0];
        JLabel welcomeLabel = new JLabel("Welcome back, " + firstName + "!");
        welcomeLabel.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 32));
        welcomeLabel.setForeground(Theme.getTextColor());
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("What would you like to do today?");
        subtitleLabel.setFont(Theme.Fonts.BODY);
        subtitleLabel.setForeground(Theme.getSecondaryTextColor());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(welcomeLabel);
        header.add(Box.createVerticalStrut(Theme.Spacing.SM));
        header.add(subtitleLabel);

        return header;
    }

    private JPanel createActionCards() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Theme.Spacing.MD, Theme.Spacing.MD, Theme.Spacing.MD, Theme.Spacing.MD);

        // Extract Text Card
        JPanel extractCard = createActionCard(
            "🔍", "Extract Text from Images",
            "Upload images and get accurate text extraction using OCR technology",
            Theme.getPrimaryColor(),
            e -> cardLayout.show(topPanel, "CreateVisionText")
        );

        // History Card
        JPanel historyCard = createActionCard(
            "📚", "View History",
            "Browse your previous text extractions and download them",
            new Color(99, 102, 241),
            e -> {
                historyPanel.loadHistory();
                cardLayout.show(topPanel, "history");
            }
        );

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(extractCard, gbc);

        gbc.gridx = 1;
        container.add(historyCard, gbc);

        return container;
    }

    private JPanel createActionCard(String emoji, String title, String description, Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, Theme.Spacing.MD));
        card.setBackground(Theme.getSurfaceColor());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
            new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.LG, Theme.Spacing.XXL, Theme.Spacing.LG)
        ));
        card.putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.LG);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon section
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconPanel.add(iconLabel);

        // Content section
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 20));
        titleLabel.setForeground(Theme.getTextColor());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 200px;'>" + description + "</div></html>");
        descLabel.setFont(Theme.Fonts.BODY);
        descLabel.setForeground(Theme.getSecondaryTextColor());
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(Theme.Spacing.SM));
        contentPanel.add(descLabel);

        // Button section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);

        ModernButton actionBtn = new ModernButton("Get Started", ModernButton.Style.PRIMARY);
        actionBtn.setPreferredSize(new Dimension(140, 40));
        actionBtn.addActionListener(action);
        buttonPanel.add(actionBtn);

        card.add(iconPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Add hover effects
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    new EmptyBorder(Theme.Spacing.XXL - 1, Theme.Spacing.LG - 1, Theme.Spacing.XXL - 1, Theme.Spacing.LG - 1)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
                    new EmptyBorder(Theme.Spacing.XXL, Theme.Spacing.LG, Theme.Spacing.XXL, Theme.Spacing.LG)
                ));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.Spacing.MD, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(Theme.Spacing.XXL, 0, 0, 0));

        // Theme toggle button
        ModernButton themeBtn = new ModernButton(Theme.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode", ModernButton.Style.GHOST);
        themeBtn.addActionListener(e -> {
            Theme.toggleTheme();
            themeBtn.setText(Theme.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
            refreshTheme();
            if (createVisionTextPanel != null) createVisionTextPanel.refreshTheme();
            if (historyPanel != null) historyPanel.refreshTheme();
            mainApp.refreshTheme();
        });

        // Logout button
        ModernButton logoutBtn = new ModernButton("👋 Sign Out", ModernButton.Style.SECONDARY);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                loginService.logout();
                mainApp.showLoginScreen();
            }
        });

        footer.add(themeBtn);
        footer.add(logoutBtn);

        return footer;
    }

    public void showMainMenu() {
        cardLayout.show(topPanel, "mainMenu");
    }

    // Update dynamic colors after theme toggle
    public void refreshTheme() {
        setBackground(Theme.getBackgroundColor());
        for (Component c : getComponents()) {
            refreshComponentTree(c);
        }
        revalidate();
        repaint();
    }

    private void refreshComponentTree(Component comp) {
        if (comp instanceof JPanel) {
            JPanel p = (JPanel) comp;
            if (!p.isOpaque()) {
                // keep transparent to let parent show through
            } else {
                p.setBackground(Theme.getBackgroundColor());
            }
        }
        if (comp instanceof JLabel) {
            JLabel l = (JLabel) comp;
            l.setForeground(Theme.getTextColor());
        }
        if (comp instanceof JList) {
            comp.setBackground(Theme.getSurfaceColor());
        }
        if (comp instanceof JScrollPane) {
            comp.setBackground(Theme.getSurfaceColor());
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                refreshComponentTree(child);
            }
        }
    }
}
