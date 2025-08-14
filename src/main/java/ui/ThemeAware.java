package ui;

import java.awt.Color;

/**
 * Components that want to react to a theme change implement this.
 * @author VisionText
 * @since 1.0
 */
public interface ThemeAware {
    /**
     * Called after Theme.toggleTheme() finishes applying the new Look & Feel.
     * @param previousBackground the background color before the change (can be used for transition effects)
     */
    void onThemeChanged(Color previousBackground);
}
