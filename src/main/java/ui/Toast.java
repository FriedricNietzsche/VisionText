package ui;

import javax.swing.*;
import java.awt.*;

/** Tiny toast for lightweight success/failure feedback. */
public final class Toast {
    private Toast() {}

    public static void show(JComponent parent, String msg) {
        Window win = SwingUtilities.getWindowAncestor(parent);
        if (win == null) return;
        JWindow w = new JWindow(win);
        JLabel l = new JLabel(msg);
        l.setOpaque(true);
        l.setForeground(Color.WHITE);
        l.setBackground(new Color(30, 30, 30, 225));
        l.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        w.add(l);
        w.pack();

        Point p = parent.getLocationOnScreen();
        int x = p.x + parent.getWidth() - w.getWidth() - 20;
        int y = p.y + parent.getHeight() - w.getHeight() - 20;
        w.setLocation(x, y);
        w.setVisible(true);

        new Timer(1800, e -> { w.setVisible(false); w.dispose(); }).start();
    }
}
