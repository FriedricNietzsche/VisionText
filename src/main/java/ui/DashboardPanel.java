package ui;

import application.OCRUseCase;
import application.HistoryService;
import application.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel implements ThemeAware {
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

    private static final int CONTENT_WIDTH = 1000;
    private static final int CARD_HEIGHT    = 380;

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
        Theme.addListener(this);
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

        // Center: main action cards (centered fixed-width row)
        JPanel center = createActionCards();
        root.add(center, BorderLayout.CENTER);

        // Footer: user actions (centered fixed-width row)
        JPanel footer = createFooter();
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, Theme.Spacing.XXL, 0));

        // Welcome + subtitle (centered)
        String firstName = username.split("@")[0];
        JLabel welcomeLabel = new JLabel("Welcome back, " + firstName + "!");
        welcomeLabel.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 32));
        welcomeLabel.setForeground(Theme.getTextColor());
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("What would you like to do today?");
        subtitleLabel.setFont(Theme.Fonts.BODY);
        subtitleLabel.setForeground(Theme.getSecondaryTextColor());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.add(welcomeLabel);
        textCol.add(Box.createVerticalStrut(Theme.Spacing.SM));
        textCol.add(subtitleLabel);

        ModernButton settingsBtn = new ModernButton("⚙ Settings", ModernButton.Style.GHOST);
        settingsBtn.setPreferredSize(new Dimension(120, 36));
        settingsBtn.addActionListener(e -> new SettingsDialog(mainApp.frame).setVisible(true));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(settingsBtn);

        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        Dimension rightSize = right.getPreferredSize();
        leftSpacer.setPreferredSize(rightSize);

        row.add(leftSpacer, BorderLayout.WEST);
        row.add(textCol, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        header.add(row);
        return header;
    }

    private JPanel createActionCards() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);

        int gap = Theme.Spacing.XL; // space between the two cards
        JPanel row = new JPanel(new GridLayout(1, 2, gap, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(CONTENT_WIDTH, CARD_HEIGHT));

        JPanel extractCard = createActionCard(
                "🔍", "Extract Text from Images",
                "Upload images and get accurate text extraction using OCR technology",
                Theme.getPrimaryColor(),
                e -> cardLayout.show(topPanel, "CreateVisionText")
        );

        JPanel historyCard = createActionCard(
                "📚", "View History",
                "Browse your previous text extractions and download them",
                new Color(99, 102, 241),
                e -> {
                    historyPanel.loadHistory();
                    cardLayout.show(topPanel, "history");
                }
        );

        int cardWidth = (CONTENT_WIDTH - gap) / 2;
        Dimension cardSize = new Dimension(cardWidth, CARD_HEIGHT);
        extractCard.setPreferredSize(cardSize);
        extractCard.setMaximumSize(cardSize);
        historyCard.setPreferredSize(cardSize);
        historyCard.setMaximumSize(cardSize);

        row.add(extractCard);
        row.add(historyCard);
        wrapper.add(row);

        return wrapper;
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

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconPanel.setOpaque(false);
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconPanel.add(iconLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 20));
        titleLabel.setForeground(Theme.getTextColor());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align:center; margin:0 auto;'><p style='margin:0;'>"
                + description + "</p></div></html>");
        descLabel.setFont(Theme.Fonts.BODY);
        descLabel.setForeground(Theme.getSecondaryTextColor());
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

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
        // Centered outer wrapper
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(Theme.Spacing.XXL, 0, 0, 0));

        // Inner row with the same width as the cards row
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.Spacing.MD, 0));
        inner.setOpaque(false);
        inner.setPreferredSize(new Dimension(CONTENT_WIDTH, 40));

        // Theme toggle button
        ModernButton themeBtn = new ModernButton(
                Theme.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode",
                ModernButton.Style.GHOST
        );
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

        inner.add(themeBtn);
        inner.add(logoutBtn);
        outer.add(inner);

        return outer;
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

    @Override
    public void onThemeChanged(Color previousBackground) { refreshTheme(); }

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
