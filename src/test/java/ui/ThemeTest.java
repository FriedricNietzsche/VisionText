package ui;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ThemeTest {

    @Test
    void colorUtilDarkenLightenTransparent() {
        Color base = new Color(100, 150, 200, 255);
        Color darker = Theme.ColorUtil.darken(base, 0.2f);
        Color lighter = Theme.ColorUtil.lighten(base, 0.2f);
        Color trans = Theme.ColorUtil.transparent(base, 0.5f);
    // 200 + (255-200)*0.2 = 200 + 11 = 211
    assertEquals(211, lighter.getBlue());
    // 150 * (1-0.2) = 120
    assertEquals(120, darker.getGreen());
        assertEquals(127, trans.getAlpha());
    }

    @Test
    void toggleThemeFlipsMode() {
        boolean before = Theme.isDarkMode();
        Theme.apply();
        Theme.toggleTheme();
        assertNotEquals(before, Theme.isDarkMode());
        // flip back for isolation
        Theme.toggleTheme();
    }
}
