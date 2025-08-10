package ui;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Theme {
    private Theme() {}

    // Modern color palette - ChatGPT inspired
    public static class Colors {
        // Light theme colors
        public static final Color LIGHT_BG = new Color(248, 250, 252);
        public static final Color LIGHT_SURFACE = new Color(255, 255, 255);
        public static final Color LIGHT_SURFACE_VARIANT = new Color(247, 248, 250);
        public static final Color LIGHT_PRIMARY = new Color(16, 163, 127);
        public static final Color LIGHT_PRIMARY_VARIANT = new Color(13, 148, 115);
        public static final Color LIGHT_SECONDARY = new Color(99, 102, 241);
        public static final Color LIGHT_TEXT_PRIMARY = new Color(17, 24, 39);
        public static final Color LIGHT_TEXT_SECONDARY = new Color(107, 114, 126);
        public static final Color LIGHT_BORDER = new Color(229, 231, 235);
        public static final Color LIGHT_HOVER = new Color(243, 244, 246);

        // Dark theme colors
        public static final Color DARK_BG = new Color(17, 24, 39);
        public static final Color DARK_SURFACE = new Color(31, 41, 55);
        public static final Color DARK_SURFACE_VARIANT = new Color(55, 65, 81);
        public static final Color DARK_PRIMARY = new Color(34, 197, 94);
        public static final Color DARK_PRIMARY_VARIANT = new Color(22, 163, 74);
        public static final Color DARK_SECONDARY = new Color(129, 140, 248);
        public static final Color DARK_TEXT_PRIMARY = new Color(243, 244, 246);
        public static final Color DARK_TEXT_SECONDARY = new Color(156, 163, 175);
        public static final Color DARK_BORDER = new Color(75, 85, 99);
        public static final Color DARK_HOVER = new Color(55, 65, 81);
    }

    // Typography
    public static class Fonts {
        public static final Font DISPLAY = new Font("Inter", Font.BOLD, 24);
        public static final Font HEADING = new Font("Inter", Font.BOLD, 18);
        public static final Font BODY = new Font("Inter", Font.PLAIN, 14);
        public static final Font BODY_MEDIUM = new Font("Inter", Font.BOLD, 14); // Fixed: Font.MEDIUM doesn't exist
        public static final Font CAPTION = new Font("Inter", Font.PLAIN, 12);
        public static final Font CODE = new Font("JetBrains Mono", Font.PLAIN, 13);

        // Fallback to system fonts if Inter is not available
        public static Font getFont(String preferred, int style, int size) {
            Font font = new Font(preferred, style, size);
            if (!font.getFamily().equals(preferred)) {
                return new Font(Font.SANS_SERIF, style, size);
            }
            return font;
        }
    }

    // Spacing constants
    public static class Spacing {
        public static final int XS = 4;
        public static final int SM = 8;
        public static final int MD = 16;
        public static final int LG = 24;
        public static final int XL = 32;
        public static final int XXL = 48;
    }

    // Border radius constants
    public static class Radius {
        public static final int SM = 6;
        public static final int MD = 8;
        public static final int LG = 12;
        public static final int XL = 16;
    }

    private static boolean isDarkMode = false;
    private static final String PREF_FILE = "theme.properties";
    private static final String KEY_DARK = "dark";
    private static final String KEY_ANIM = "animations";
    private static final List<ThemeAware> listeners = new CopyOnWriteArrayList<>();
    private static boolean fadeTransitions = true;

    public static void loadPersistedTheme() {
        try (FileInputStream fis = new FileInputStream(PREF_FILE)) {
            java.util.Properties p = new java.util.Properties();
            p.load(fis);
            isDarkMode = Boolean.parseBoolean(p.getProperty(KEY_DARK, "false"));
            fadeTransitions = Boolean.parseBoolean(p.getProperty(KEY_ANIM, "true"));
        } catch (IOException ignored) { }
    }

    private static void persistTheme() {
        try (FileOutputStream fos = new FileOutputStream(PREF_FILE)) {
            java.util.Properties p = new java.util.Properties();
            p.setProperty(KEY_DARK, Boolean.toString(isDarkMode));
            p.setProperty(KEY_ANIM, Boolean.toString(fadeTransitions));
            p.store(fos, "VisionText theme preference");
        } catch (IOException ignored) { }
    }

    public static void addListener(ThemeAware l) { if (l != null) listeners.add(l); }
    public static void removeListener(ThemeAware l) { listeners.remove(l); }
    public static void setFadeTransitions(boolean enable) { fadeTransitions = enable; persistTheme(); }
    public static boolean isFadeTransitions() { return fadeTransitions; }

    public static void applyModernTheme() {
        try {
            // Set system properties for better rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            // Try to apply FlatLaf theme if available
            try {
                Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                Class<?> flatDarkLafClass = Class.forName("com.formdev.flatlaf.FlatDarkLaf");

                if (isDarkMode) {
                    javax.swing.LookAndFeel darkLaf = (javax.swing.LookAndFeel) flatDarkLafClass.getDeclaredConstructor().newInstance();
                    UIManager.setLookAndFeel(darkLaf);
                    customizeDarkTheme();
                } else {
                    javax.swing.LookAndFeel lightLaf = (javax.swing.LookAndFeel) flatLafClass.getDeclaredConstructor().newInstance();
                    UIManager.setLookAndFeel(lightLaf);
                    customizeLightTheme();
                }
            } catch (ClassNotFoundException e) {
                // FlatLaf not available, use system LaF with custom styling
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            // Global UI customizations
            customizeGlobalUI();

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void customizeLightTheme() {
        // Button styling
        UIManager.put("Button.background", Colors.LIGHT_SURFACE);
        UIManager.put("Button.foreground", Colors.LIGHT_TEXT_PRIMARY);
        UIManager.put("Button.border", BorderFactory.createLineBorder(Colors.LIGHT_BORDER));

        // TextField styling
        UIManager.put("TextField.background", Colors.LIGHT_SURFACE);
        UIManager.put("TextField.foreground", Colors.LIGHT_TEXT_PRIMARY);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(Colors.LIGHT_BORDER));

        // Panel and background
        UIManager.put("Panel.background", Colors.LIGHT_BG);
        UIManager.put("ScrollPane.background", Colors.LIGHT_SURFACE);

        // List styling
        UIManager.put("List.background", Colors.LIGHT_SURFACE);
        UIManager.put("List.selectionBackground", Colors.LIGHT_PRIMARY);
        UIManager.put("List.selectionForeground", Color.WHITE);
    }

    private static void customizeDarkTheme() {
        // Button styling
        UIManager.put("Button.background", Colors.DARK_SURFACE);
        UIManager.put("Button.foreground", Colors.DARK_TEXT_PRIMARY);
        UIManager.put("Button.border", BorderFactory.createLineBorder(Colors.DARK_BORDER));

        // TextField styling
        UIManager.put("TextField.background", Colors.DARK_SURFACE);
        UIManager.put("TextField.foreground", Colors.DARK_TEXT_PRIMARY);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(Colors.DARK_BORDER));

        // Panel and background
        UIManager.put("Panel.background", Colors.DARK_BG);
        UIManager.put("ScrollPane.background", Colors.DARK_SURFACE);

        // List styling
        UIManager.put("List.background", Colors.DARK_SURFACE);
        UIManager.put("List.selectionBackground", Colors.DARK_PRIMARY);
        UIManager.put("List.selectionForeground", Color.WHITE);
    }

    private static void customizeGlobalUI() {
        // Typography
        UIManager.put("Label.font", Fonts.BODY);
        UIManager.put("Button.font", Fonts.BODY_MEDIUM);
        UIManager.put("TextField.font", Fonts.BODY);
        UIManager.put("TextArea.font", Fonts.BODY);
        UIManager.put("List.font", Fonts.BODY);
    }

    public static void toggleTheme() {
        Color previous = getBackgroundColor();
        isDarkMode = !isDarkMode;
        applyModernTheme();
        persistTheme();

        // Update all windows
        for (Window window : Window.getWindows()) {
            if (fadeTransitions && window.isDisplayable()) {
                applyFade(window, previous);
            } else {
                SwingUtilities.updateComponentTreeUI(window);
                window.repaint();
            }
        }
        for (ThemeAware l : listeners) {
            try { l.onThemeChanged(previous); } catch (Exception ignored) { }
        }
    }

    private static void applyFade(Window window, Color previousBg) {
        if (!(window instanceof JFrame)) {
            SwingUtilities.updateComponentTreeUI(window);
            return;
        }
        JFrame frame = (JFrame) window;
        JRootPane root = frame.getRootPane();
        final float[] alpha = {1f};
        final JComponent glass = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                if (alpha[0] <= 0f) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.SrcOver.derive(alpha[0]));
                g2.setColor(previousBg);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        glass.setOpaque(false);
        root.setGlassPane(glass);
        glass.setVisible(true);
        SwingUtilities.updateComponentTreeUI(window);
        new Timer(15, e -> {
            alpha[0] -= 0.07f;
            if (alpha[0] <= 0f) {
                glass.setVisible(false);
                ((Timer) e.getSource()).stop();
            } else {
                glass.repaint();
            }
        }).start();
    }

    // Central color utilities
    public static final class ColorUtil {
        public static Color darken(Color c, float factor) {
            return new Color(
                Math.max(0, (int)(c.getRed() * (1 - factor))),
                Math.max(0, (int)(c.getGreen() * (1 - factor))),
                Math.max(0, (int)(c.getBlue() * (1 - factor))),
                c.getAlpha());
        }
        public static Color lighten(Color c, float factor) {
            return new Color(
                Math.min(255, (int)(c.getRed() + (255 - c.getRed()) * factor)),
                Math.min(255, (int)(c.getGreen() + (255 - c.getGreen()) * factor)),
                Math.min(255, (int)(c.getBlue() + (255 - c.getBlue()) * factor)),
                c.getAlpha());
        }
        public static Color transparent(Color c, float alpha) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255 * alpha));
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    // Utility methods for getting current theme colors
    public static Color getPrimaryColor() {
        return isDarkMode ? Colors.DARK_PRIMARY : Colors.LIGHT_PRIMARY;
    }

    public static Color getBackgroundColor() {
        return isDarkMode ? Colors.DARK_BG : Colors.LIGHT_BG;
    }

    public static Color getSurfaceColor() {
        return isDarkMode ? Colors.DARK_SURFACE : Colors.LIGHT_SURFACE;
    }

    public static Color getTextColor() {
        return isDarkMode ? Colors.DARK_TEXT_PRIMARY : Colors.LIGHT_TEXT_PRIMARY;
    }

    public static Color getSecondaryTextColor() {
        return isDarkMode ? Colors.DARK_TEXT_SECONDARY : Colors.LIGHT_TEXT_SECONDARY;
    }

    public static Color getBorderColor() {
        return isDarkMode ? Colors.DARK_BORDER : Colors.LIGHT_BORDER;
    }

    // Legacy method for backward compatibility
    public static void apply() {
        applyModernTheme();
    }

    // Legacy color constants for backward compatibility
    public static final Color BG = Colors.LIGHT_BG;
    public static final Color CARD_TOP = Colors.LIGHT_SURFACE;
    public static final Color CARD_BOTTOM = Colors.LIGHT_SURFACE_VARIANT;
    public static final Color OUTLINE = Colors.LIGHT_BORDER;
    public static final Color TEXT = Colors.LIGHT_TEXT_PRIMARY;
    public static final Font FONT = Fonts.BODY;
    public static final Font FONT_BOLD = Fonts.BODY_MEDIUM;
}
