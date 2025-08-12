package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimatedButton extends JButton {
    private float progress = 0f;   // hover 0..1
    private float target   = 0f;
    private final Timer timer;

    // Idle (matches your screenshot)
    private final Color baseTop    = new Color(250, 250, 250);
    private final Color baseBottom = new Color(235, 237, 240);
    // Hover (cool blue)
    private final Color hoverTop    = new Color(185, 215, 245);
    private final Color hoverBottom = new Color(150, 200, 240);
    // Border
    private final Color borderColor = new Color(200, 205, 210);

    public AnimatedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Arial", Font.BOLD, 14));

        timer = new Timer(16, e -> {
            float speed = 0.12f;
            progress += (target - progress) * speed;
            if (Math.abs(target - progress) < 0.01f) progress = target;
            repaint();
        });
        timer.setCoalesce(true);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                animateTo(1f);
            }
            @Override public void mouseExited (MouseEvent e) {
                animateTo(0f);
            }
            @Override public void mousePressed(MouseEvent e) {
                repaint();
            }
            @Override public void mouseReleased(MouseEvent e){ repaint(); }
        });

        addChangeListener(e -> repaint());
    }

    private void animateTo(float t) {
        target = Math.max(0f, Math.min(1f, t));
        if (!timer.isRunning()) timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight(), arc = 16;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Interpolate between idle and hover colors
        Color top    = lerp(baseTop,    hoverTop,    progress);
        Color bottom = lerp(baseBottom, hoverBottom, progress);

        // Shadow / elevation
        int shadow = Math.round(5 * progress) + 2;
        g2.setColor(new Color(0, 0, 0, (int) (28 + 50 * progress)));
        g2.fillRoundRect(3, shadow, w - 6, h - shadow * 2, arc, arc);

        boolean pressed = getModel().isArmed() && getModel().isPressed();
        float pressDarken = pressed ? 0.12f : 0f;

        // Background gradient
        GradientPaint gp = new GradientPaint(0, 0, darken(top, pressDarken),
                0, h, darken(bottom, pressDarken));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Border
        g2.setColor(darken(borderColor, pressDarken / 2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // Text
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(new Color(25, 30, 40));
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }

    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed()   + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue()  + (b.getBlue() - a.getBlue()) * t);
        int al = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(r, g, bl, al);
    }
    private static Color darken(Color c, float amt) {
        amt = Math.max(0f, Math.min(1f, amt));
        return new Color((int) (c.getRed() * (1 - amt)),
                (int) (c.getGreen() * (1 - amt)),
                (int) (c.getBlue() * (1 - amt)),
                c.getAlpha());
    }
}
