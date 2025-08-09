package ui;

import javax.swing.*;
import java.awt.*;

public final class Theme {
    private Theme(){}

    // Colors
    public static final Color BG          = new Color(245, 246, 248);
    public static final Color CARD_TOP    = new Color(252, 252, 252);
    public static final Color CARD_BOTTOM = new Color(242, 244, 248);
    public static final Color OUTLINE     = new Color(220, 224, 230);
    public static final Color TEXT        = new Color(25, 30, 40);
    public static final Font  FONT        = new Font("Arial", Font.PLAIN, 14);
    public static final Font  FONT_BOLD   = new Font("Arial", Font.BOLD, 16);

    public static void apply() {
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("List.font", FONT);
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(OUTLINE));
    }
}
