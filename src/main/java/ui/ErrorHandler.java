package ui;

import javax.swing.*;

public class ErrorHandler {
    public void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void logError(Exception e) {
        e.printStackTrace();
    }

    public void showError(String message, Exception e) {
        showError(message);
        logError(e);
    }
} 