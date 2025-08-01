package ui;

import application.OCRUseCase;
import application.HistoryService;
import application.LoginService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CreateVisionTextPanel extends JPanel {
    private final MainAppUI mainApp;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private final ErrorHandler errorHandler;
    private final String username;

    private JTextArea textArea;
    private JLabel imageLabel;
    private File currentImageFile;

    public CreateVisionTextPanel(MainAppUI mainApp, OCRUseCase ocrUseCase,
                                 HistoryService historyService,
                                 ErrorHandler errorHandler, String username) {
        this.mainApp = mainApp;
        this.ocrUseCase = ocrUseCase;
        this.historyService = historyService;
        this.errorHandler = errorHandler;
        this.username = username;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel for image selection info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imageLabel = new JLabel("No image selected");
        topPanel.add(imageLabel);
        add(topPanel, BorderLayout.NORTH);

        // Text area for OCR results
        textArea = new JTextArea(20, 60);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton uploadBtn = new JButton("Upload Image");
        uploadBtn.addActionListener(this::onUpload);

        JButton saveBtn = new JButton("Save as TXT");
        saveBtn.addActionListener(this::onSave);

        buttonPanel.add(uploadBtn);
        buttonPanel.add(saveBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            String currentUser = mainApp.getCurrentUser();
            mainApp.showDashboard(currentUser);
        });
        bottomPanel.add(backBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        final long MAX_FILE_SIZE = 20 * 1024 * 1024;
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() ||
                        name.endsWith(".jpg") ||
                        name.endsWith(".png") ||
                        name.endsWith(".jpeg") ||
                        f.length() < MAX_FILE_SIZE;
            }
            public String getDescription() {
                return "Image Files (*.jpg, *.png, *.jpeg)";
            }
        });

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentImageFile = chooser.getSelectedFile();
            imageLabel.setText("Selected: " + currentImageFile.getName());

            try {
                String extractedText = ocrUseCase.extractText(currentImageFile);
                textArea.setText(extractedText);
            } catch (Exception ex) {
                errorHandler.showError("OCR failed: " + ex.getMessage(), ex);
                textArea.setText("");
            }
        }
    }

    private void onSave(ActionEvent e) {
        String text = textArea.getText();
        if (text == null || text.trim().isEmpty()) {
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

                // Save to history if an image was processed
                if (currentImageFile != null) {
                    historyService.saveHistory(
                            username,
                            currentImageFile.getName(),
                            text,
                            new Date().getTime()
                    );
                }
            } catch (IOException ex) {
                errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
            }
        }
    }
}
