package ui;

import application.HistoryService;
import application.LoginService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class HistoryPanel extends JPanel {
    private MainAppUI mainApp;
    private HistoryService historyService;
    private LoginService loginService;
    private ErrorHandler errorHandler;
    private String username;
    private JList<String> historyList;
    private DefaultListModel<String> listModel;

    public HistoryPanel(MainAppUI mainApp, HistoryService historyService, LoginService loginService, ErrorHandler errorHandler, String username) {
        this.mainApp = mainApp;
        this.historyService = historyService;
        this.loginService = loginService;
        this.errorHandler = errorHandler;
        this.username = username;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();
        historyList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(historyList);
        add(scrollPane, BorderLayout.CENTER);
        JButton viewBtn = new JButton("View");
        JButton downloadBtn = new JButton("Download");
        JButton backBtn = new JButton("Back to Dashboard");
        JPanel btnPanel = new JPanel();
        btnPanel.add(viewBtn);
        btnPanel.add(downloadBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        viewBtn.addActionListener(this::onView);
        downloadBtn.addActionListener(this::onDownload);
        backBtn.addActionListener(e -> {
            String currentUser = mainApp.getCurrentUser();
            mainApp.showDashboard(currentUser);
        });
        loadHistory();
    }

    public void loadHistory() {
        listModel.clear();
        List<String> items = historyService.getHistoryList(username);
        if (items != null) {
            for (String item : items) {
                // Extract display part from "firebaseKey|||displayString" format
                String displayText = item;
                if (item.contains("|||")) {
                    displayText = item.split("\\|\\|\\|")[1];
                }
                listModel.addElement(displayText);
            }
        }
    }

    private void onView(ActionEvent e) {
        String selectedDisplay = historyList.getSelectedValue();
        if (selectedDisplay == null) {
            errorHandler.showError("Select a history item to view.");
            return;
        }

        // Find the original item (with Firebase key) that matches the selected display text
        String originalItem = findOriginalItemByDisplay(selectedDisplay);
        if (originalItem == null) {
            errorHandler.showError("Could not find the selected history item.");
            return;
        }

        String text = null;
        try {
            text = historyService.getHistoryItem(username, originalItem);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JTextArea area = new JTextArea(text, 20, 60);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, scrollPane, "History Item", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDownload(ActionEvent e)  {
        String selectedDisplay = historyList.getSelectedValue();
        if (selectedDisplay == null) {
            errorHandler.showError("Select a history item to download.");
            return;
        }

        // Find the original item (with Firebase key) that matches the selected display text
        String originalItem = findOriginalItemByDisplay(selectedDisplay);
        if (originalItem == null) {
            errorHandler.showError("Could not find the selected history item.");
            return;
        }

        String text = null;
        try {
            text = historyService.getHistoryItem(username, originalItem);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("history_item.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = chooser.getSelectedFile();
            try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
                fw.write(text);
            } catch (Exception ex) {
                errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
            }
        }
    }

    private String findOriginalItemByDisplay(String displayText) {
        List<String> allItems = historyService.getHistoryList(username);
        if (allItems != null) {
            for (String item : allItems) {
                if (item.contains("|||")) {
                    String itemDisplay = item.split("\\|\\|\\|")[1];
                    if (itemDisplay.equals(displayText)) {
                        return item;
                    }
                } else if (item.equals(displayText)) {
                    return item;
                }
            }
        }
        return null;
    }
}
