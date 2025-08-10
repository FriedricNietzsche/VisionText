package ui;

import java.awt.*;

/** Components that want to react to a theme change implement this. */
public interface ThemeAware {
    /**
     * Called after Theme.toggleTheme() finishes applying the new Look & Feel.
     * @param previousBackground the background color before the change (can be used for transition effects)
     */
    void onThemeChanged(Color previousBackground);
}
