package ui;

import javax.swing.JOptionPane;

/**
 * Handles error display and logging for UI components.
 */
public final class ErrorHandler {
    /**
     * Show an error dialog with the given message.
     * @param message the error message
     */
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an error dialog and log the exception.
     * @param message the error message
     * @param exception the exception to log
     */
    public static void showError(String message, Exception exception) {
        showError(message);
        logError(exception);
    }
    /**
     * Log an exception to the standard error stream.
     * @param exception the exception to log
     */

    public static void logError(Exception exception) {
        // TODO: Replace with robust logging if needed
        exception.printStackTrace();
    }
}
