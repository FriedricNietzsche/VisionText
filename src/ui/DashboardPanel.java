package app;

import domain.OCRService;
import domain.HistoryManager;
import domain.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Date;

public class DashboardPanel extends JPanel {
    private MainAppUI mainApp;
    private OCRService ocrService;
    private HistoryManager historyManager;
    private UserManager userManager;
    private ErrorHandler errorHandler;
    private String username;

    private JTextArea textArea;
    private JLabel imageLabel;
    private File currentImageFile;

    public DashboardPanel(MainAppUI mainApp, OCRService ocrService, HistoryManager historyManager, UserManager userManager, ErrorHandler errorHandler, String username) {
        this.mainApp = mainApp;
        this.ocrService = ocrService;
        this.historyManager = historyManager;
        this.userManager = userManager;
        this.errorHandler = errorHandler;
        this.username = username;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        JButton uploadBtn = new JButton("Upload Image");
        JButton saveBtn = new JButton("Save as TXT");
        JButton historyBtn = new JButton("View History");
        JButton logoutBtn = new JButton("Logout");
        topPanel.add(uploadBtn);
        topPanel.add(saveBtn);
        topPanel.add(historyBtn);
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);

        textArea = new JTextArea(20, 60);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.SOUTH);

        uploadBtn.addActionListener(this::onUpload);
        saveBtn.addActionListener(this::onSave);
        historyBtn.addActionListener(e -> mainApp.showHistory(username));
        logoutBtn.addActionListener(e -> {
            userManager.logout();
            mainApp.showLoginScreen();
        });
    }

    private void onUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentImageFile = chooser.getSelectedFile();
            imageLabel.setText("Selected: " + currentImageFile.getName());
            try {
                String extracted = ocrService.extractTextFromImage(currentImageFile);
                textArea.setText(extracted);
            } catch (Exception ex) {
                errorHandler.showError("OCR failed: " + ex.getMessage(), ex);
            }
        }
    }

    private void onSave(ActionEvent e) {
        String text = textArea.getText();
        if (text.isEmpty()) {
            errorHandler.showError("No text to save.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("ocr_result.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(text);
            } catch (IOException ex) {
                errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
                return;
            }
        }
        // Save to history
        if (currentImageFile != null) {
            historyManager.saveHistory(username, currentImageFile.getName(), text, new Date().getTime());
        }
    }
} 