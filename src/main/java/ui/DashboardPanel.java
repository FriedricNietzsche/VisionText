package ui;

import application.OCRUseCase;
import application.HistoryService;
import application.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        cardLayout = new CardLayout();
        topPanel = new JPanel(cardLayout);

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
        root.setBorder(new EmptyBorder(40, 20, 40, 20));
        root.setOpaque(false);

        // ---------- CENTER: big buttons stack ----------
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        AnimatedButton createNew = new AnimatedButton("Create New VisionText");
        AnimatedButton historyBtn = new AnimatedButton("History");

        // Bigger buttons for center
        Dimension big = new Dimension(380, 80);
        Font bigFont = new Font("Arial", Font.BOLD, 18);
        for (AnimatedButton b : new AnimatedButton[]{createNew, historyBtn}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setPreferredSize(big);
            b.setMaximumSize(big);
            b.setFont(bigFont);
        }

        center.add(Box.createVerticalGlue());
        center.add(createNew);
        center.add(Box.createVerticalStrut(24));
        center.add(historyBtn);
        center.add(Box.createVerticalGlue());

        // ---------- SOUTH: smaller logout button ----------
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        south.setBorder(new EmptyBorder(24, 0, 0, 0));

        AnimatedButton logoutBtn = new AnimatedButton("Logout",
                new Color(250, 230, 230), new Color(240, 210, 210), // base
                new Color(245, 120, 120), new Color(220, 80, 80));   // hover (red gradient)

        Dimension small = new Dimension(260, 54);
        logoutBtn.setPreferredSize(small);
        logoutBtn.setMaximumSize(small);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 15));

        south.add(Box.createHorizontalGlue());
        south.add(logoutBtn);
        south.add(Box.createHorizontalGlue());

        // ---------- wiring ----------
        createNew.addActionListener(e -> cardLayout.show(topPanel, "CreateVisionText"));

        historyBtn.addActionListener(e -> {
            historyPanel.loadHistory();
            cardLayout.show(topPanel, "history");
        });

        logoutBtn.addActionListener(e -> {
            loginService.logout();
            mainApp.showLoginScreen();
        });

        root.add(center, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
        return root;
    }

    public void showMainMenu() {
        cardLayout.show(topPanel, "mainMenu");
    }

    /**
     * Button: default gradient + hover animation.
     * Now supports custom base & hover colors.
     */
    private static class AnimatedButton extends JButton {
        private float progress = 0f;   // hover progress 0..1
        private float target = 0f;
        private final Timer timer;

        private final Color baseTop;
        private final Color baseBottom;
        private final Color hoverTop;
        private final Color hoverBottom;

        private final Color borderColor = new Color(200, 205, 210);

        public AnimatedButton(String text) {
            this(text,
                    new Color(250, 250, 250), new Color(235, 237, 240),   // default base
                    new Color(185, 215, 245), new Color(150, 200, 240)); // default hover (blue)
        }

        public AnimatedButton(String text, Color baseTop, Color baseBottom, Color hoverTop, Color hoverBottom) {
            super(text);
            this.baseTop = baseTop;
            this.baseBottom = baseBottom;
            this.hoverTop = hoverTop;
            this.hoverBottom = hoverBottom;

            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(12, 20, 12, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            timer = new Timer(16, e -> {
                float speed = 0.12f;
                progress += (target - progress) * speed;
                if (Math.abs(target - progress) < 0.01f) progress = target;
                repaint();
            });
            timer.setCoalesce(true);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { animateTo(1f); }
                @Override public void mouseExited(MouseEvent e)  { animateTo(0f); }
                @Override public void mousePressed(MouseEvent e) { repaint(); }
                @Override public void mouseReleased(MouseEvent e){ repaint(); }
            });

            addChangeListener(e -> repaint());
        }

        private void animateTo(float t) {
            target = t;
            if (!timer.isRunning()) timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            int arc = 20;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color top = lerp(baseTop, hoverTop, progress);
            Color bottom = lerp(baseBottom, hoverBottom, progress);

            int shadow = Math.round(6 * progress) + 2;
            g2.setColor(new Color(0, 0, 0, (int) (30 + 60 * progress)));
            g2.fillRoundRect(4, shadow, w - 8, h - shadow * 2, arc, arc);

            boolean pressed = getModel().isArmed() && getModel().isPressed();
            float pressDarken = pressed ? 0.12f : 0f;

            GradientPaint gp = new GradientPaint(0, 0, darken(top, pressDarken),
                    0, h, darken(bottom, pressDarken));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            g2.setColor(darken(borderColor, pressDarken / 2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            // Text
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(new Color(25, 30, 40));
            g2.drawString(text, tx, ty);

            g2.dispose();
        }

        private static Color lerp(Color a, Color b, float t) {
            t = Math.max(0f, Math.min(1f, t));
            int r = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
            int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
            int bC = (int) (a.getBlue() + (b.getBlue()  - a.getBlue())  * t);
            int aC = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
            return new Color(r, g, bC, aC);
        }

        private static Color darken(Color c, float amount) {
            amount = Math.max(0f, Math.min(1f, amount));
            int r = (int) (c.getRed() * (1 - amount));
            int g = (int) (c.getGreen() * (1 - amount));
            int b = (int) (c.getBlue() * (1 - amount));
            return new Color(r, g, b, c.getAlpha());
        }
    }
}


