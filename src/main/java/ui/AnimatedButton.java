package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class AnimatedButton extends JButton {

    private static final int ANIMATION_DELAY_MS = 16;
    private static final float ANIMATION_SPEED = 0.12f;
    private static final float ANIMATION_THRESHOLD = 0.01f;
    private static final float ANIMATION_TARGET_HOVER = 1f;
    private static final float ANIMATION_TARGET_IDLE = 0f;
    private static final int BORDER_PADDING_TOP_BOTTOM = 10;
    private static final int BORDER_PADDING_LEFT_RIGHT = 18;
    private static final int FONT_SIZE = 14;
    private static final int ARC_SIZE = 16;
    private static final int SHADOW_MAX_PROGRESS = 5;
    private static final int SHADOW_MIN_SIZE = 2;
    private static final int SHADOW_ALPHA_BASE = 28;
    private static final int SHADOW_ALPHA_PROGRESS = 50;
    private static final float PRESS_DARKEN_AMOUNT = 0.12f;

    // Colors
    private static final Color BASE_TOP_COLOR = new Color(250, 250, 250);
    private static final Color BASE_BOTTOM_COLOR = new Color(235, 237, 240);
    private static final Color HOVER_TOP_COLOR = new Color(185, 215, 245);
    private static final Color HOVER_BOTTOM_COLOR = new Color(150, 200, 240);
    private static final Color BORDER_COLOR = new Color(200, 205, 210);
    private static final Color TEXT_COLOR = new Color(25, 30, 40);
    private static final int X = 3;
    private static final int Y = 6;

    private float progress;
    private float target;
    private final Timer timer;

    public AnimatedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(BORDER_PADDING_TOP_BOTTOM, BORDER_PADDING_LEFT_RIGHT, BORDER_PADDING_TOP_BOTTOM,
                BORDER_PADDING_LEFT_RIGHT));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Arial", Font.BOLD, FONT_SIZE));

        timer = new Timer(ANIMATION_DELAY_MS, event -> {
            progress += (target - progress) * ANIMATION_SPEED;
            if (Math.abs(target - progress) < ANIMATION_THRESHOLD) {
                progress = target;
            }
            repaint();
        });

        timer.setCoalesce(true);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                animateTo(ANIMATION_TARGET_HOVER);
            }

            @Override public void mouseExited(MouseEvent e) {
                animateTo(ANIMATION_TARGET_IDLE);
            }

            @Override public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });

        addChangeListener(event -> repaint());
    }

    private void animateTo(float targdist) {
        target = Math.max(ANIMATION_TARGET_IDLE, Math.min(ANIMATION_TARGET_HOVER, targdist));
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow / elevation
        int shadow = Math.round(SHADOW_MAX_PROGRESS * progress) + SHADOW_MIN_SIZE;
        g2.setColor(new Color(0, 0, 0, (int) (SHADOW_ALPHA_BASE + SHADOW_ALPHA_PROGRESS * progress)));
        g2.fillRoundRect(X, shadow, w - Y, h - shadow * 2, ARC_SIZE, ARC_SIZE);

        boolean pressed = getModel().isArmed() && getModel().isPressed();
        float pressDarken = 0f;
        if (pressed) {
            pressDarken = PRESS_DARKEN_AMOUNT;
        }

        // Interpolate between idle and hover colors
        Color top = lerp(BASE_TOP_COLOR, HOVER_TOP_COLOR, progress);
        Color bottom = lerp(BASE_BOTTOM_COLOR, HOVER_BOTTOM_COLOR, progress);

        // Background gradient
        GradientPaint gp = new GradientPaint(0, 0, darken(top, pressDarken), 0, h, darken(bottom, pressDarken));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, ARC_SIZE, ARC_SIZE);

        // Border
        g2.setColor(darken(BORDER_COLOR, pressDarken / 2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, ARC_SIZE, ARC_SIZE);

        // Text
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(TEXT_COLOR);
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }

    private static Color lerp(Color col1, Color col2, float target) {
        float t = Math.max(0f, Math.min(1f, target));
        int r = (int) (col1.getRed() + (col2.getRed() - col1.getRed()) * t);
        int g = (int) (col1.getGreen() + (col2.getGreen() - col1.getGreen()) * t);
        int bl = (int) (col1.getBlue() + (col2.getBlue() - col1.getBlue()) * t);
        int al = (int) (col1.getAlpha() + (col2.getAlpha() - col1.getAlpha()) * t);
        return new Color(r, g, bl, al);
    }

    private static Color darken(Color color, float amount) {
        float amt = Math.max(0f, Math.min(1f, amount));
        return new Color((int) (color.getRed() * (1 - amt)),
                (int) (color.getGreen() * (1 - amt)),
                (int) (color.getBlue() * (1 - amt)),
                color.getAlpha());
    }
}
