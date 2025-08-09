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

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class CreateVisionTextPanel extends JPanel {

    private final MainAppUI mainApp;
    private final OCRUseCase ocrUseCase;
    private final HistoryService historyService;
    private final ErrorHandler errorHandler;
    private final String username;

    private JLabel imageStatusLabel;
    private JLabel preview;
    private JTextArea outputArea;
    private File currentImage;
    private final JPanel centerOverlay = new JPanel();

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
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(10, 0, 10, 0)); // No left/right padding

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        bindShortcuts();
    }

    private JComponent createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBorder(new EmptyBorder(0, 10, 0, 10));
        header.setOpaque(false);
        imageStatusLabel = new JLabel("No image selected");
        imageStatusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        header.add(imageStatusLabel);
        return header;
    }

    private JComponent createCenterPanel() {
        JPanel overlayContainer = new JPanel();
        overlayContainer.setLayout(new OverlayLayout(overlayContainer));
        overlayContainer.setOpaque(false);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // ===== LEFT BOX: Preview Panel (25% width) =====
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setOpaque(true);
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setBorder(BorderFactory.createLineBorder(Theme.OUTLINE));

        preview = new JLabel(
                "<html><div style='text-align:center;color:#666;'>Drop an image here</div></html>",
                SwingConstants.CENTER
        );
        preview.setBorder(new EmptyBorder(10, 10, 10, 10));
        previewPanel.add(preview, BorderLayout.CENTER);

        new DropTarget(previewPanel, new DropTargetAdapter() {
            @Override public void drop(DropTargetDropEvent dtde) {
                handleDrop(dtde);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(0, 10, 0, 5);
        contentPanel.add(previewPanel, gbc);

        // ===== RIGHT BOX: OCR Output Area (75% width) =====
        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createLineBorder(Theme.OUTLINE));

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        gbc.insets = new Insets(0, 5, 0, 10);
        contentPanel.add(outputScrollPane, gbc);

        overlayContainer.add(contentPanel);

        // Loading overlay
        centerOverlay.setOpaque(true);
        centerOverlay.setBackground(new Color(255, 255, 255, 180));
        centerOverlay.setLayout(new GridBagLayout());
        JLabel loading = new JLabel("Processing…");
        loading.setFont(Theme.FONT_BOLD);
        centerOverlay.add(loading);
        centerOverlay.setVisible(false);
        overlayContainer.add(centerOverlay);

        return overlayContainer;
    }

    private JComponent createFooterPanel() {
        // --- CHANGE: Reverted to the original centered button layout ---
        AnimatedButton uploadBtn = new AnimatedButton("Upload Image");
        AnimatedButton pasteBtn  = new AnimatedButton("Paste");
        AnimatedButton copyBtn   = new AnimatedButton("Copy");
        AnimatedButton clearBtn  = new AnimatedButton("Clear");
        AnimatedButton saveBtn   = new AnimatedButton("Save as TXT");
        AnimatedButton backBtn   = new AnimatedButton("Back to Dashboard");

        Dimension btnSize = new Dimension(160, 44);
        for (JButton b : new JButton[]{uploadBtn, pasteBtn, copyBtn, clearBtn, saveBtn}) {
            b.setPreferredSize(btnSize);
            b.setMaximumSize(btnSize);
        }
        backBtn.setPreferredSize(new Dimension(220, 44));
        backBtn.setMaximumSize(new Dimension(220, 44));

        uploadBtn.addActionListener(this::onUpload);
        pasteBtn.addActionListener(e -> pasteFromClipboard());
        copyBtn.addActionListener(e -> copyToClipboard());
        clearBtn.addActionListener(e -> {
            outputArea.setText("");
            Toast.show(this, "Cleared");
        });
        saveBtn.addActionListener(this::onSave);
        backBtn.addActionListener(e -> mainApp.showDashboard(mainApp.getCurrentUser()));

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.add(Box.createHorizontalGlue());
        footer.add(uploadBtn); footer.add(Box.createHorizontalStrut(10));
        footer.add(pasteBtn);  footer.add(Box.createHorizontalStrut(10));
        footer.add(copyBtn);   footer.add(Box.createHorizontalStrut(10));
        footer.add(clearBtn);  footer.add(Box.createHorizontalStrut(10));
        footer.add(saveBtn);   footer.add(Box.createHorizontalStrut(12));
        footer.add(backBtn);
        footer.add(Box.createHorizontalGlue());

        return footer;
    }

    // --- Unchanged helper methods below this line ---

    private void bindShortcuts() {
        bindShortcut(this, "control C", "copy", e -> copyToClipboard());
        bindShortcut(this, "control V", "paste", e -> pasteFromClipboard());
    }

    private void handleDrop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) dtde.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);
            if (!files.isEmpty()) {
                currentImage = files.get(0);
                imageStatusLabel.setText("Selected: " + currentImage.getName());
                setPreviewImage(currentImage);
                runOCR(currentImage);
            }
        } catch (Exception ex) {
            errorHandler.showError("Failed to drop file: " + ex.getMessage(), ex);
        }
    }

    private static void bindShortcut(JComponent c, String stroke, String name,
                                     java.awt.event.ActionListener listener) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(stroke), name);
        c.getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        });
    }

    private void copyToClipboard() {
        String t = outputArea.getText();
        if (t == null || t.trim().isEmpty()) {
            Toast.show(this, "Nothing to copy");
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(t), null);
        Toast.show(this, "Copied");
    }

    private void pasteFromClipboard() {
        try {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tr = cb.getContents(null);

            if (tr.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                Image img = (Image) tr.getTransferData(DataFlavor.imageFlavor);
                if (img != null) {
                    File tmp = writeImageToTemp(img);
                    currentImage = tmp;
                    imageStatusLabel.setText("Pasted image");
                    setPreviewImage(tmp);
                    runOCR(tmp);
                    return;
                }
            }

            if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String s = (String) tr.getTransferData(DataFlavor.stringFlavor);
                if (s != null && !s.isEmpty()) {
                    if (outputArea.getText().isEmpty()) outputArea.setText(s);
                    else outputArea.replaceSelection(s);
                    Toast.show(this, "Pasted text");
                    return;
                }
            }

            Toast.show(this, "Nothing to paste");
        } catch (Exception ex) {
            errorHandler.showError("Paste failed: " + ex.getMessage(), ex);
        }
    }

    private File writeImageToTemp(Image image) throws Exception {
        int w = image.getWidth(null), h = image.getHeight(null);
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Cannot process an empty image.");
        }
        BufferedImage bi = new BufferedImage(w, h, TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        File tmp = File.createTempFile("visiontext_clip_", ".png");
        javax.imageio.ImageIO.write(bi, "png", tmp);
        tmp.deleteOnExit();
        return tmp;
    }

    private void setPreviewImage(File f) {
        try {
            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
            Container holder = preview.getParent();

            int padding = 20; // 10px on each side
            int maxW = Math.max(1, holder.getWidth() - padding);
            int maxH = Math.max(1, holder.getHeight() - padding);

            Image img = icon.getImage();
            double ratio = Math.min((double) maxW / img.getWidth(null),
                    (double) maxH / img.getHeight(null));
            int w = Math.max(1, (int) (img.getWidth(null) * ratio));
            int h = Math.max(1, (int) (img.getHeight(null) * ratio));

            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            preview.setIcon(new ImageIcon(scaled));
            preview.setText(null);
        } catch (Exception ex) {
            preview.setIcon(null);
            preview.setText("<html><div style='text-align:center;'>Preview<br>unavailable</div></html>");
            errorHandler.showError("Could not display preview: " + ex.getMessage(), ex);
        }
    }

    private void onUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            currentImage = chooser.getSelectedFile();
            imageStatusLabel.setText("Selected: " + currentImage.getName());
            setPreviewImage(currentImage);
            runOCR(currentImage);
        }
    }

    private void runOCR(File image) {
        setBusy(true);
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return ocrUseCase.runOCR(image);
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    String text = get();
                    outputArea.setText(text == null ? "" : text);
                    if (text != null && !text.isBlank()) {
                        historyService.addHistory(username, image.getName(), text);
                        Toast.show(CreateVisionTextPanel.this, "Added to history");
                    }
                } catch (Exception ex) {
                    errorHandler.showError("Failed to process image: " + ex.getMessage(), ex);
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        centerOverlay.setVisible(busy);
    }

    private void onSave(ActionEvent e) {
        String text = outputArea.getText();
        if (text.trim().isEmpty()) {
            errorHandler.showError("Nothing to save—OCR output is empty.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("visiontext.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.write(text);
                Toast.show(this, "Saved");
            } catch (Exception ex) {
                errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
            }
        }
    }
}

