package ui;

import application.HistoryService;
import application.OCRUseCase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class CreateVisionTextPanel extends JPanel implements ThemeAware {

    private final MainAppUI mainApp;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private final ErrorHandler errorHandler;
    private final String username;

    private JLabel statusLabel;
    private ModernImagePreview previewPanel;
    private JTextArea outputArea;
    private File currentImage;
    private JPanel loadingOverlay;
    private ModernButton uploadBtn, pasteBtn, copyBtn, clearBtn, saveBtn, backBtn;

    public CreateVisionTextPanel(MainAppUI mainApp,
                                 OCRUseCase ocrUseCase,
                                 HistoryService historyService,
                                 ErrorHandler errorHandler,
                                 String username) {
        this.mainApp = mainApp;
        this.ocrUseCase = ocrUseCase;
        this.historyService = historyService;
        this.errorHandler = errorHandler;
        this.username = username;
        initUI();
    Theme.addListener(this);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.getBackgroundColor());
        setBorder(new EmptyBorder(Theme.Spacing.LG, Theme.Spacing.LG, Theme.Spacing.LG, Theme.Spacing.LG));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        setupKeyboardShortcuts();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, Theme.Spacing.LG, 0));

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("🔍 Text Extraction");
        title.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 24));
        title.setForeground(Theme.getTextColor());

        statusLabel = new JLabel("Upload an image or paste from clipboard to extract text");
        statusLabel.setFont(Theme.Fonts.BODY);
        statusLabel.setForeground(Theme.getSecondaryTextColor());

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(Theme.Spacing.XS));
        titlePanel.add(statusLabel);

        header.add(titlePanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout(Theme.Spacing.LG, 0));
        mainContent.setOpaque(false);

        // Left side: Image preview (40%)
        JPanel leftPanel = createImagePreviewPanel();

        // Right side: Text output (60%)
        JPanel rightPanel = createTextOutputPanel();

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(rightPanel, BorderLayout.CENTER);

        // Loading overlay
        loadingOverlay = createLoadingOverlay();

        // Use OverlayLayout to stack loading over content
        JPanel container = new JPanel();
        container.setLayout(new OverlayLayout(container));
        container.add(loadingOverlay);
        container.add(mainContent);

        return container;
    }

    private JPanel createImagePreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(400, 0));

        // Preview area
        previewPanel = new ModernImagePreview();
        previewPanel.setPreferredSize(new Dimension(400, 300));
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
            new EmptyBorder(Theme.Spacing.MD, Theme.Spacing.MD, Theme.Spacing.MD, Theme.Spacing.MD)
        ));
        previewPanel.putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.MD);

        // Setup drag and drop
        new DropTarget(previewPanel, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                handleDrop(dtde);
            }
        });

        panel.add(previewPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTextOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Header for output area
        JLabel outputLabel = new JLabel("📝 Extracted Text");
        outputLabel.setFont(Theme.Fonts.getFont("Inter", Font.BOLD, 16));
        outputLabel.setForeground(Theme.getTextColor());
        outputLabel.setBorder(new EmptyBorder(0, 0, Theme.Spacing.SM, 0));

        // Text area
        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(Theme.Fonts.getFont("JetBrains Mono", Font.PLAIN, 13));
        outputArea.setMargin(new Insets(Theme.Spacing.MD, Theme.Spacing.MD, Theme.Spacing.MD, Theme.Spacing.MD));
        outputArea.setBackground(Theme.getSurfaceColor());
        outputArea.setForeground(Theme.getTextColor());
        outputArea.setBorder(null);

        // Custom placeholder text
        outputArea.putClientProperty("JTextArea.placeholderText", "Extracted text will appear here...");

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.MD);

        panel.add(outputLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLoadingOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(Theme.getSurfaceColor().getRed(),
                                      Theme.getSurfaceColor().getGreen(),
                                      Theme.getSurfaceColor().getBlue(), 200));
        overlay.setVisible(false);

        JPanel loadingCard = new JPanel();
        loadingCard.setLayout(new BoxLayout(loadingCard, BoxLayout.Y_AXIS));
        loadingCard.setBackground(Theme.getSurfaceColor());
        loadingCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
            new EmptyBorder(Theme.Spacing.LG, Theme.Spacing.LG, Theme.Spacing.LG, Theme.Spacing.LG)
        ));
        loadingCard.putClientProperty("FlatLaf.style", "arc: " + Theme.Radius.LG);

        JLabel loadingIcon = new JLabel("⏳");
        loadingIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        loadingIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loadingText = new JLabel("Extracting text from image...");
        loadingText.setFont(Theme.Fonts.BODY_MEDIUM);
        loadingText.setForeground(Theme.getTextColor());
        loadingText.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadingCard.add(loadingIcon);
        loadingCard.add(Box.createVerticalStrut(Theme.Spacing.SM));
        loadingCard.add(loadingText);

        overlay.add(loadingCard);
        return overlay;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BorderLayout());
        footer.setBorder(new EmptyBorder(Theme.Spacing.LG, 0, 0, 0));

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.Spacing.SM, 0));
        buttonPanel.setOpaque(false);

        uploadBtn = new ModernButton("📁 Upload Image", ModernButton.Style.PRIMARY);
        pasteBtn = new ModernButton("📋 Paste", ModernButton.Style.SECONDARY);
        copyBtn = new ModernButton("📄 Copy Text", ModernButton.Style.SECONDARY);
        clearBtn = new ModernButton("🗑️ Clear", ModernButton.Style.GHOST);
        saveBtn = new ModernButton("💾 Save as TXT", ModernButton.Style.SECONDARY);

        Dimension btnSize = new Dimension(140, 40);
        for (ModernButton btn : new ModernButton[]{uploadBtn, pasteBtn, copyBtn, clearBtn, saveBtn}) {
            btn.setPreferredSize(btnSize);
        }

        // Wire up actions
        uploadBtn.addActionListener(this::onUpload);
        pasteBtn.addActionListener(e -> pasteFromClipboard());
        copyBtn.addActionListener(e -> copyToClipboard());
        clearBtn.addActionListener(e -> clearOutput());
        saveBtn.addActionListener(this::onSave);

        buttonPanel.add(uploadBtn);
        buttonPanel.add(pasteBtn);
        buttonPanel.add(copyBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(saveBtn);

        // Back button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setOpaque(false);

        backBtn = new ModernButton("← Back to Dashboard", ModernButton.Style.GHOST);
        backBtn.addActionListener(e -> mainApp.showDashboard(username));
        backPanel.add(backBtn);

        footer.add(backPanel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.CENTER);

        return footer;
    }

    private void setupKeyboardShortcuts() {
        // Ctrl+V for paste
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control V"), "paste");
        getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteFromClipboard();
            }
        });

        // Ctrl+C for copy
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control C"), "copy");
        getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
    }

    private void handleDrop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) dtde.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);
            if (!files.isEmpty()) {
                File file = files.get(0);
                loadImage(file);
            }
        } catch (Exception ex) {
            errorHandler.showError("Failed to drop file: " + ex.getMessage(), ex);
        }
    }

    private void onUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "bmp", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadImage(chooser.getSelectedFile());
        }
    }

    private void loadImage(File imageFile) {
        currentImage = imageFile;
        statusLabel.setText("Processing: " + imageFile.getName());
        previewPanel.setImage(imageFile);
        runOCR(imageFile);
    }

    private void pasteFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = clipboard.getContents(null);

            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                File tempFile = createTempImageFile(image);
                loadImage(tempFile);
            } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                if (outputArea.getText().isEmpty()) {
                    outputArea.setText(text);
                } else {
                    outputArea.replaceSelection(text);
                }
                Toast.show(this, "Text pasted");
            } else {
                Toast.show(this, "No image or text found in clipboard");
            }
        } catch (Exception ex) {
            errorHandler.showError("Paste failed: " + ex.getMessage(), ex);
        }
    }

    private File createTempImageFile(Image image) throws Exception {
        BufferedImage bufferedImage = new BufferedImage(
            image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        File tempFile = File.createTempFile("visiontext_", ".png");
        javax.imageio.ImageIO.write(bufferedImage, "png", tempFile);
        tempFile.deleteOnExit();
        return tempFile;
    }

    private void copyToClipboard() {
        String text = outputArea.getText();
        if (text == null || text.trim().isEmpty()) {
            Toast.show(this, "No text to copy");
            return;
        }

        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        Toast.show(this, "Text copied to clipboard");
    }

    private void clearOutput() {
        outputArea.setText("");
        previewPanel.setImage((File) null);
        currentImage = null;
        statusLabel.setText("Upload an image or paste from clipboard to extract text");
        Toast.show(this, "Cleared");
    }

    private void runOCR(File imageFile) {
        setProcessing(true);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return ocrUseCase.runOCR(imageFile);
            }

            @Override
            protected void done() {
                setProcessing(false);
                try {
                    String text = get();
                    outputArea.setText(text == null ? "" : text);

                    if (text != null && !text.isBlank()) {
                        historyService.addHistory(username, imageFile.getName(), text);
                        statusLabel.setText("✅ Text extracted successfully from " + imageFile.getName());
                        Toast.show(CreateVisionTextPanel.this, "Text extracted and saved to history");
                    } else {
                        statusLabel.setText("⚠️ No text found in " + imageFile.getName());
                    }
                } catch (Exception ex) {
                    statusLabel.setText("❌ Failed to process " + imageFile.getName());
                    errorHandler.showError("Failed to process image: " + ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    private void setProcessing(boolean processing) {
        loadingOverlay.setVisible(processing);
        uploadBtn.setEnabled(!processing);
        pasteBtn.setEnabled(!processing);

        if (processing) {
            uploadBtn.setText("🔄 Processing...");
        } else {
            uploadBtn.setText("📁 Upload Image");
        }
    }

    private void onSave(ActionEvent e) {
        String text = outputArea.getText();
        if (text.trim().isEmpty()) {
            errorHandler.showError("Nothing to save—text area is empty.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("extracted_text.txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
                writer.write(text);
                Toast.show(this, "File saved successfully");
            } catch (Exception ex) {
                errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
            }
        }
    }

    public void refreshTheme() {
        setBackground(Theme.getBackgroundColor());
        if (outputArea != null) {
            outputArea.setBackground(Theme.getSurfaceColor());
            outputArea.setForeground(Theme.getTextColor());
        }
        if (loadingOverlay != null) {
            loadingOverlay.setBackground(new Color(Theme.getSurfaceColor().getRed(),
                    Theme.getSurfaceColor().getGreen(),
                    Theme.getSurfaceColor().getBlue(), 200));
        }
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged(Color previousBackground) { refreshTheme(); }

    // Custom image preview component
    private static class ModernImagePreview extends JPanel {
        private Image image;

        public void setImage(File imageFile) {
            if (imageFile != null) {
                try {
                    this.image = new ImageIcon(imageFile.getAbsolutePath()).getImage();
                } catch (Exception ex) {
                    this.image = null;
                }
            } else {
                this.image = null;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (image == null) {
                // Draw drop zone
                g2.setColor(Theme.getSecondaryTextColor());
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 8}, 0));
                g2.drawRoundRect(10, 10, width - 20, height - 20, Theme.Radius.MD, Theme.Radius.MD);

                // Drop zone text
                String[] lines = {"📷", "Drop image here", "or click Upload"};
                g2.setColor(Theme.getSecondaryTextColor());

                for (int i = 0; i < lines.length; i++) {
                    Font font = i == 0 ? new Font("Segoe UI Emoji", Font.PLAIN, 32) : Theme.Fonts.BODY;
                    g2.setFont(font);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (width - fm.stringWidth(lines[i])) / 2;
                    int y = height / 2 - 20 + (i * 25);
                    g2.drawString(lines[i], x, y);
                }
            } else {
                // Draw image scaled to fit
                int imgWidth = image.getWidth(null);
                int imgHeight = image.getHeight(null);

                if (imgWidth > 0 && imgHeight > 0) {
                    double scale = Math.min((double) (width - 20) / imgWidth, (double) (height - 20) / imgHeight);
                    int scaledWidth = (int) (imgWidth * scale);
                    int scaledHeight = (int) (imgHeight * scale);
                    int x = (width - scaledWidth) / 2;
                    int y = (height - scaledHeight) / 2;

                    g2.drawImage(image, x, y, scaledWidth, scaledHeight, null);
                }
            }

            g2.dispose();
        }
    }
}
