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

    @Test
    void colorGettersReturnColors() {
        // Exercise both modes
        boolean before = Theme.isDarkMode();
        if (!before) Theme.toggleTheme();
        assertNotNull(Theme.getPrimaryColor());
        assertNotNull(Theme.getBackgroundColor());
        assertNotNull(Theme.getSurfaceColor());
        assertNotNull(Theme.getTextColor());
        assertNotNull(Theme.getSecondaryTextColor());
        assertNotNull(Theme.getBorderColor());
        if (before != Theme.isDarkMode()) Theme.toggleTheme();
    }

    @Test
    void listenerIsNotifiedOnToggle() {
    class L implements ThemeAware { boolean called=false; public void onThemeChanged(java.awt.Color c){ called=true; } }
    L l = new L();
    Theme.addListener(l);
    Theme.toggleTheme();
    assertTrue(l.called);
    // cleanup
    Theme.toggleTheme();
    Theme.removeListener(l);
    }

    @Test
    void customPrimaryOverrides() {
        java.awt.Color prev = Theme.getPrimaryColor();
        java.awt.Color custom = new java.awt.Color(10,20,30);
        Theme.setCustomPrimary(custom);
        assertEquals(custom, Theme.getPrimaryColor());
        Theme.clearCustomPrimary();
        assertNotEquals(custom, Theme.getPrimaryColor());
        // sanity: back to some color
        assertNotNull(prev);
    }
}
