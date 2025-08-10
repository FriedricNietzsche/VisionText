package ui;

import application.HistoryService;
import application.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryPanel extends JPanel {
    private final MainAppUI mainApp;
    private final HistoryService historyService;
    private final LoginService loginService;
    private final ErrorHandler errorHandler;
    private final String username;

    private JTextField searchField;
    private JComboBox<String> sortBox;
    private JList<String> historyList;
    private DefaultListModel<String> model;

    private final List<String> rawItems = new ArrayList<>();

    public HistoryPanel(MainAppUI mainApp, HistoryService historyService,
                        LoginService loginService, ErrorHandler errorHandler, String username) {
        this.mainApp = mainApp;
        this.historyService = historyService;
        this.loginService = loginService;
        this.errorHandler = errorHandler;
        this.username = username;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.getBackgroundColor()); // Fixed: use modern theme method
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Top controls
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JLabel title = new JLabel("📚 History");
        title.setFont(Theme.Fonts.HEADING); // Fixed: use modern font
        title.setForeground(Theme.getTextColor()); // Fixed: use modern theme method
        top.add(title, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        searchField.setFont(Theme.Fonts.BODY); // Fixed: use modern font

        // Sorting options now include date sorts
        sortBox = new JComboBox<>(new String[]{"Newest", "Oldest", "A → Z", "Z → A"});
        sortBox.setFont(Theme.Fonts.BODY); // Fixed: use modern font

        right.add(searchField);
        right.add(Box.createHorizontalStrut(8));
        right.add(sortBox);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // List
        model = new DefaultListModel<>();
        historyList = new JList<>(model);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFixedCellHeight(44);
        historyList.setBorder(new EmptyBorder(6, 6, 6, 6));
        historyList.setCellRenderer(new Renderer());
        historyList.setBackground(Theme.getSurfaceColor()); // Fixed: use modern theme method

        historyList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && historyList.getSelectedIndex() >= 0) onView(null);
            }
        });

        // Right-click menu (View / Download / Delete)
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miView = new JMenuItem("👁️ View");
        JMenuItem miDownload = new JMenuItem("💾 Download");
        JMenuItem miDelete = new JMenuItem("🗑️ Delete");

        miView.setFont(Theme.Fonts.BODY);
        miDownload.setFont(Theme.Fonts.BODY);
        miDelete.setFont(Theme.Fonts.BODY);

        miView.addActionListener(this::onView);
        miDownload.addActionListener(this::onDownload);
        miDelete.addActionListener(this::onDelete);

        menu.add(miView);
        menu.add(miDownload);
        menu.addSeparator();
        menu.add(miDelete);

        historyList.setComponentPopupMenu(menu);

        // Delete key support
        historyList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "del");
        historyList.getActionMap().put("del", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onDelete(null); }
        });

        JScrollPane sp = new JScrollPane(historyList);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorderColor()), // Fixed: use modern theme method
                new EmptyBorder(6, 6, 6, 6)
        ));
        sp.setBackground(Theme.getSurfaceColor()); // Fixed: use modern theme method
        add(sp, BorderLayout.CENTER);

        // Footer buttons - Fixed: use ModernButton instead of AnimatedButton
        ModernButton viewBtn = new ModernButton("👁️ View", ModernButton.Style.PRIMARY);
        ModernButton downloadBtn = new ModernButton("💾 Download", ModernButton.Style.SECONDARY);
        ModernButton deleteBtn = new ModernButton("🗑️ Delete", ModernButton.Style.DANGER);
        ModernButton backBtn = new ModernButton("← Back to Dashboard", ModernButton.Style.GHOST);

        Dimension btnSize = new Dimension(160, 40);
        for (ModernButton b : new ModernButton[]{viewBtn, downloadBtn, deleteBtn, backBtn}) {
            b.setPreferredSize(btnSize);
            b.setMaximumSize(btnSize);
        }
        viewBtn.addActionListener(this::onView);
        downloadBtn.addActionListener(this::onDownload);
        deleteBtn.addActionListener(this::onDelete);
        backBtn.addActionListener(e -> mainApp.showDashboard(mainApp.getCurrentUser()));

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBorder(new EmptyBorder(12, 0, 0, 0));
        footer.add(Box.createHorizontalGlue());
        footer.add(viewBtn);     footer.add(Box.createHorizontalStrut(10));
        footer.add(downloadBtn); footer.add(Box.createHorizontalStrut(10));
        footer.add(deleteBtn);   footer.add(Box.createHorizontalStrut(10));
        footer.add(backBtn);
        footer.add(Box.createHorizontalGlue());
        add(footer, BorderLayout.SOUTH);

        // Wire search/sort
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void go() { applyFilterAndSort(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { go(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { go(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { go(); }
        });
        sortBox.addActionListener(e -> applyFilterAndSort());

        loadHistory();
    }

    public void loadHistory() {
        model.clear();
        rawItems.clear();

        List<String> items = historyService.getHistoryList(username);
        if (items == null || items.isEmpty()) {
            model.addElement("(No history yet)");
            historyList.setEnabled(false);
            return;
        }

        historyList.setEnabled(true);
        rawItems.addAll(items);
        applyFilterAndSort();
    }

    private void applyFilterAndSort() {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        // Filter by query against the display text
        List<String> filteredDisplays = new ArrayList<>();
        for (String item : rawItems) {
            String display = extractDisplay(item);
            if (q.isEmpty() || display.toLowerCase().contains(q)) {
                filteredDisplays.add(display);
            }
        }

        // Sort
        int mode = sortBox.getSelectedIndex(); // 0=Newest, 1=Oldest, 2=A→Z, 3=Z→A
        Collections.sort(filteredDisplays, (a, b) -> {
            if (mode <= 1) { // date sorts
                long ta = extractEpoch(findOriginalByDisplayLocal(a));
                long tb = extractEpoch(findOriginalByDisplayLocal(b));
                if (ta == tb) return a.compareToIgnoreCase(b); // stable fallback
                return (mode == 0) ? Long.compare(tb, ta) : Long.compare(ta, tb); // Newest/Oldest
            } else { // alpha sorts
                return (mode == 2) ? a.compareToIgnoreCase(b) : b.compareToIgnoreCase(a);
            }
        });

        model.clear();
        for (String s : filteredDisplays) model.addElement(s);
    }

    public void refreshTheme() {
        setBackground(Theme.getBackgroundColor());
        if (historyList != null) {
            historyList.setBackground(Theme.getSurfaceColor());
            historyList.repaint();
        }
        SwingUtilities.invokeLater(() -> {
            for (Component c : getComponents()) updateComponentColors(c);
            repaint();
        });
    }

    private void updateComponentColors(Component c) {
        if (c instanceof JLabel) {
            c.setForeground(Theme.getTextColor());
        }
        if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            if (p.isOpaque()) p.setBackground(Theme.getBackgroundColor());
        }
        if (c instanceof JScrollPane) {
            c.setBackground(Theme.getSurfaceColor());
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) updateComponentColors(child);
        }
    }

    private void onView(ActionEvent e) {
        String display = historyList.getSelectedValue();
        if (display == null || !historyList.isEnabled()) {
            Toast.show(this, "Please select a history item to view");
            return;
        }
        String original = findOriginalByDisplay(display);
        if (original == null) {
            errorHandler.showError("Could not find the selected history item.");
            return;
        }
        try {
            String text = historyService.getHistoryItem(username, original);
            JTextArea area = new JTextArea(text, 20, 60);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setEditable(false);
            area.setFont(Theme.Fonts.CODE); // Fixed: use modern font
            area.setBackground(Theme.getSurfaceColor()); // Fixed: use modern theme
            area.setForeground(Theme.getTextColor()); // Fixed: use modern theme

            JScrollPane sc = new JScrollPane(area);
            sc.setPreferredSize(new Dimension(700, 450));
            sc.setBorder(BorderFactory.createLineBorder(Theme.getBorderColor())); // Fixed: use modern theme
            JOptionPane.showMessageDialog(this, sc, "📄 History Item", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            errorHandler.showError("Failed to load item.", ex);
        }
    }

    private void onDownload(ActionEvent e) {
        String display = historyList.getSelectedValue();
        if (display == null || !historyList.isEnabled()) {
            Toast.show(this, "Please select a history item to download");
            return;
        }
        String original = findOriginalByDisplay(display);
        if (original == null) {
            errorHandler.showError("Could not find the selected history item.");
            return;
        }
        try {
            String text = historyService.getHistoryItem(username, original);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("history_item.txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (java.io.FileWriter fw = new java.io.FileWriter(chooser.getSelectedFile())) {
                    fw.write(text);
                }
                Toast.show(this, "File saved successfully");
            }
        } catch (Exception ex) {
            errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
        }
    }

    private void onDelete(ActionEvent e) {
        String display = historyList.getSelectedValue();
        if (display == null || !historyList.isEnabled()) {
            Toast.show(this, "Please select a history item to delete");
            return;
        }
        String original = findOriginalByDisplay(display);
        if (original == null) {
            errorHandler.showError("Could not find the selected history item.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this item?\n\n" + display,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            historyService.deleteHistory(username, original);
            Toast.show(this, "Item deleted successfully");
            loadHistory();
        } catch (Exception ex) {
            errorHandler.showError("Failed to delete item: " + ex.getMessage(), ex);
        }
    }

    /* ---------------- utils ---------------- */

    /** Accepts "key|||display" or "key|||epochMillis|||display"; returns display part. */
    private static String extractDisplay(String item) {
        String[] parts = item.split("\\|\\|\\|", 3);
        if (parts.length == 3) return parts[2];     // key|||epoch|||display
        if (parts.length == 2) return parts[1];     // key|||display
        return item;                                 // plain display
    }

    private static long extractEpoch(String item) {
        if (item == null) return 0L;
        String[] parts = item.split("\\|\\|\\|", 3);
        if (parts.length >= 3) {
            try { return Long.parseLong(parts[1]); } catch (NumberFormatException ignored) {}
        }
        return 0L;
    }

    /** Looks up the *original* raw item by its display text, using current rawItems. */
    private String findOriginalByDisplayLocal(String displayText) {
        for (String item : rawItems) {
            if (extractDisplay(item).equals(displayText)) return item;
        }
        return null;
    }

    /** Looks up the original raw item by display text using a fresh read (safer). */
    private String findOriginalByDisplay(String displayText) {
        List<String> all = historyService.getHistoryList(username);
        if (all != null) {
            for (String item : all) {
                if (extractDisplay(item).equals(displayText)) return item;
            }
        }
        return null;
    }

    /** Striped rows + selection coloring. */
    private static class Renderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setBorder(new EmptyBorder(8, 12, 8, 12));
            c.setFont(Theme.Fonts.BODY);

            if (isSelected) {
                c.setForeground(Color.WHITE);
                c.setBackground(Theme.getPrimaryColor());
            } else {
                c.setForeground(Theme.getTextColor());
                c.setBackground(index % 2 == 0 ? Theme.getSurfaceColor() :
                    new Color(Theme.getSurfaceColor().getRed() - 5,
                             Theme.getSurfaceColor().getGreen() - 5,
                             Theme.getSurfaceColor().getBlue() - 5));
            }
            return c;
        }
    }
}
