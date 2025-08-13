package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A modern styled button for VisionText UI.
 */
public class ModernButton extends JButton {

    public enum Style {
        PRIMARY,    // Filled button with primary color
        SECONDARY,  // Outlined button
        GHOST,      // Text button with no background
        DANGER      // Red colored button for destructive actions
    }

    private final Style style;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        initButton();
    }

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    private void initButton() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Set font and padding
        setFont(Theme.Fonts.BODY_MEDIUM);
        setBorder(new EmptyBorder(Theme.Spacing.SM, Theme.Spacing.MD, Theme.Spacing.SM, Theme.Spacing.MD));

        // Add mouse listeners for hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });

        // Enable smooth animations with FlatLaf
        putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.MD);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Get colors based on style and state
        Color backgroundColor = getBackgroundColor();
        Color textColor = getTextColor();
        Color borderColor = getBorderColor();

        // Draw background
        if (style != Style.GHOST) {
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, width, height, Theme.Radius.MD, Theme.Radius.MD);
        }

        // Draw border for secondary style
        if (style == Style.SECONDARY) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, Theme.Radius.MD, Theme.Radius.MD);
        }

        // Draw text
        g2.setColor(textColor);
        g2.setFont(getFont());

        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + textHeight;

        g2.drawString(text, x, y);

        g2.dispose();
    }

    private Color getBackgroundColor() {
        Color baseColor;

        switch (style) {
            case PRIMARY:
                baseColor = Theme.getPrimaryColor();
                break;
            case SECONDARY:
                baseColor = Theme.getSurfaceColor();
                break;
            case GHOST:
                baseColor = new Color(0, 0, 0, 0); // Transparent
                break;
            case DANGER:
                baseColor = new Color(239, 68, 68); // Red color
                break;
            default:
                baseColor = Theme.getPrimaryColor();
        }

        if (!isEnabled()) {
            return Theme.ColorUtil.transparent(baseColor, 0.5f);
        } else if (isPressed) {
            return Theme.ColorUtil.darken(baseColor, 0.2f);
        } else if (isHovered) {
            return style == Style.GHOST ?
                Theme.ColorUtil.transparent(Theme.getPrimaryColor(), 0.1f) :
                Theme.ColorUtil.lighten(baseColor, 0.1f);
        }

        return baseColor;
    }

    private Color getTextColor() {
        switch (style) {
            case PRIMARY:
                return Color.WHITE;
            case SECONDARY:
                return Theme.getTextColor();
            case GHOST:
                return isHovered ? Theme.getPrimaryColor() : Theme.getTextColor();
            case DANGER:
                return Color.WHITE;
            default:
                return Color.WHITE;
        }
    }

    private Color getBorderColor() {
        if (style == Style.SECONDARY) {
            if (isHovered) {
                return Theme.getPrimaryColor();
            }
            return Theme.getBorderColor();
        }
        return null;
    }

    // Removed local color manipulation methods in favor of Theme.ColorUtil
}
