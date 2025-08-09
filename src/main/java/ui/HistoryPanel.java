package ui;

import application.HistoryService;
import application.LoginService;
import ui.AnimatedButton;

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
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Top controls
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JLabel title = new JLabel("History");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Theme.TEXT);
        top.add(title, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search...");

        // Sorting options now include date sorts
        sortBox = new JComboBox<>(new String[]{"Newest", "Oldest", "A → Z", "Z → A"});

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

        historyList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && historyList.getSelectedIndex() >= 0) onView(null);
            }
        });

        // Right-click menu (View / Download / Delete)
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miView = new JMenuItem("View");
        JMenuItem miDownload = new JMenuItem("Download");
        JMenuItem miDelete = new JMenuItem("Delete");

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
                BorderFactory.createLineBorder(Theme.OUTLINE),
                new EmptyBorder(6, 6, 6, 6)
        ));
        add(sp, BorderLayout.CENTER);

        // Footer buttons
        AnimatedButton viewBtn = new AnimatedButton("View");
        AnimatedButton downloadBtn = new AnimatedButton("Download");
        AnimatedButton deleteBtn = new AnimatedButton("Delete");
        AnimatedButton backBtn = new AnimatedButton("Back to Dashboard");

        Dimension btnSize = new Dimension(160, 40);
        for (AnimatedButton b : new AnimatedButton[]{viewBtn, downloadBtn, deleteBtn, backBtn}) {
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

    private void onView(ActionEvent e) {
        String display = historyList.getSelectedValue();
        if (display == null || !historyList.isEnabled()) {
            errorHandler.showError("Select a history item to view.");
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
            JScrollPane sc = new JScrollPane(area);
            sc.setPreferredSize(new Dimension(700, 450));
            JOptionPane.showMessageDialog(this, sc, "History Item", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            errorHandler.showError("Failed to load item.", ex);
        }
    }

    private void onDownload(ActionEvent e) {
        String display = historyList.getSelectedValue();
        if (display == null || !historyList.isEnabled()) {
            errorHandler.showError("Select a history item to download.");
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
                Toast.show(this, "Saved");
            }
        } catch (Exception ex) {
            errorHandler.showError("Failed to save file: " + ex.getMessage(), ex);
        }
    }

    private void onDelete(ActionEvent e) {
        String display = historyList.getSelectedValue();
        if (display == null || !historyList.isEnabled()) {
            errorHandler.showError("Select a history item to delete.");
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
            // Make sure your HistoryService has this method.
            // If your method is named deleteHistoryItem, just change this call.
            historyService.deleteHistory(username, original);
            Toast.show(this, "Deleted");
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
        private static final Color EVEN = new Color(252, 253, 255);
        private static final Color ODD  = new Color(244, 247, 252);
        private static final Color SELECT_BG = new Color(210, 230, 255);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setBorder(new EmptyBorder(8, 12, 8, 12));
            c.setForeground(Theme.TEXT);
            c.setBackground(isSelected ? SELECT_BG : (index % 2 == 0 ? EVEN : ODD));
            return c;
        }
    }
}
