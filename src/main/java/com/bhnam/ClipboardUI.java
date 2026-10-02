package com.bhnam;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ClipboardUI extends JFrame {

    private final List<ClipboardItem> items =
            new ArrayList<>();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;

    public ClipboardUI() {

        setTitle("Clipboard Manager");
        setSize(950, 600);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        // =====================================================
        // TOP
        // =====================================================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        topPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        searchField = new JTextField();

        searchField.setToolTipText(
                "Search clipboard..."
        );

        JButton clearButton =
                new JButton("Clear All");

        topPanel.add(
                searchField,
                BorderLayout.CENTER
        );

        topPanel.add(
                clearButton,
                BorderLayout.EAST
        );

        // =====================================================
        // TABLE
        // =====================================================

        String[] columns = {
                "Time",
                "Content",
                "Action"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        table =
                new JTable(tableModel);

        table.setRowHeight(38);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader()
                .setReorderingAllowed(false);

        // Time
        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(80);

        table.getColumnModel()
                .getColumn(0)
                .setMaxWidth(100);

        // Content
        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(650);

        // Action
        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(170);

        table.getColumnModel()
                .getColumn(2)
                .setMinWidth(170);

        table.getColumnModel()
                .getColumn(2)
                .setMaxWidth(170);

        // Action renderer
        table.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new ActionRenderer()
                );

        JScrollPane scrollPane =
                new JScrollPane(table);

        // =====================================================
        // STATUS
        // =====================================================

        JLabel status =
                new JLabel(
                        "  Monitoring: ● Active"
                );

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout()
                );

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        5, 10, 5, 10
                )
        );

        bottomPanel.add(
                status,
                BorderLayout.WEST
        );

        // =====================================================
        // MAIN LAYOUT
        // =====================================================

        setLayout(
                new BorderLayout()
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // SEARCH
        // =====================================================

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {

                                refreshTable();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {

                                refreshTable();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {

                                refreshTable();
                            }
                        }
                );

        // =====================================================
        // CLEAR ALL
        // =====================================================

        clearButton.addActionListener(e -> {

            if (items.isEmpty()) {
                return;
            }

            int result =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Delete all clipboard history?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION
                    );

            if (result ==
                    JOptionPane.YES_OPTION) {

                items.clear();

                refreshTable();
            }
        });

        // =====================================================
        // MOUSE CLICK
        // =====================================================

        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        int row =
                                table.rowAtPoint(
                                        e.getPoint()
                                );

                        int column =
                                table.columnAtPoint(
                                        e.getPoint()
                                );

                        if (row < 0) {
                            return;
                        }

                        // -------------------------------------
                        // CONTENT
                        // -------------------------------------

                        if (column == 1
                                && e.getClickCount() == 2) {

                            ClipboardItem item =
                                    getItemFromTableRow(row);

                            if (item != null) {

                                showFullContent(item);
                            }

                            return;
                        }

                        // -------------------------------------
                        // ACTION
                        // -------------------------------------

                        if (column == 2) {

                            handleActionClick(
                                    row,
                                    e.getX()
                            );
                        }
                    }
                }
        );

        refreshTable();
    }

    // =========================================================
    // ADD CONTENT
    // =========================================================

    public void addClipboardContent(
            String content) {

        if (content == null
                || content.isBlank()) {

            return;
        }

        // Không lưu trùng item mới nhất
        if (!items.isEmpty()
                && items.get(0)
                .getContent()
                .equals(content)) {

            return;
        }

        ClipboardItem item =
                new ClipboardItem(content);

        items.add(0, item);

        refreshTable();
    }

    // =========================================================
    // REFRESH TABLE
    // =========================================================

    private void refreshTable() {

        tableModel.setRowCount(0);

        String keyword =
                searchField
                        .getText()
                        .toLowerCase()
                        .trim();

        /*
         * Duyệt items theo thứ tự hiện tại.
         *
         * Action column không chứa object.
         * Nó chỉ hiển thị:
         *
         * [ Copy ] [ Delete ]
         */

        for (int i = 0; i < items.size(); i++) {

            ClipboardItem item =
                    items.get(i);

            String content =
                    item.getContent();

            if (!content
                    .toLowerCase()
                    .contains(keyword)) {

                continue;
            }

            String display =
                    content
                            .replace("\n", " ")
                            .replace("\r", " ");

            if (display.length() > 120) {

                display =
                        display.substring(
                                0,
                                120
                        ) + "...";
            }

            /*
             * Column 2 lưu INDEX của item.
             *
             * Ví dụ:
             *
             * items:
             * 0 -> Hello
             * 1 -> 192.168.1.1
             * 2 -> Google
             *
             * Nếu search 192.168:
             *
             * table row -> index 1
             *
             * Vì vậy không bị nhầm item.
             */

            tableModel.addRow(
                    new Object[]{
                            item.getTime(),
                            display,
                            i
                    }
            );
        }
    }

    // =========================================================
    // GET ITEM
    // =========================================================

    private ClipboardItem getItemFromTableRow(
            int viewRow) {

        if (viewRow < 0) {
            return null;
        }

        int modelRow =
                table.convertRowIndexToModel(
                        viewRow
                );

        if (modelRow < 0
                || modelRow >= tableModel.getRowCount()) {

            return null;
        }

        Object value =
                tableModel.getValueAt(
                        modelRow,
                        2
                );

        if (!(value instanceof Integer)) {
            return null;
        }

        int itemIndex =
                (Integer) value;

        if (itemIndex < 0
                || itemIndex >= items.size()) {

            return null;
        }

        return items.get(itemIndex);
    }

    // =========================================================
    // ACTION
    // =========================================================

    private void handleActionClick(
            int row,
            int x) {

        ClipboardItem item =
                getItemFromTableRow(row);

        if (item == null) {
            return;
        }

        /*
         * Action column rộng 170px.
         *
         * Bên trái:
         * [ Copy ]
         *
         * Bên phải:
         * [ Delete ]
         */

        if (x < 85) {

            // COPY

            copyToClipboard(
                    item.getContent()
            );

            System.out.println(
                    "[Clipboard] Copied"
            );

        } else {

            // DELETE

            int result =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Delete this clipboard item?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION
                    );

            if (result ==
                    JOptionPane.YES_OPTION) {

                items.remove(item);

                refreshTable();
            }
        }
    }

    // =========================================================
    // COPY
    // =========================================================

    private void copyToClipboard(
            String content) {

        StringSelection selection =
                new StringSelection(content);

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        selection,
                        null
                );
    }

    // =========================================================
    // FULL CONTENT
    // =========================================================

    private void showFullContent(
            ClipboardItem item) {

        JTextArea textArea =
                new JTextArea(
                        item.getContent()
                );

        textArea.setLineWrap(true);

        textArea.setWrapStyleWord(true);

        textArea.setEditable(false);

        textArea.setCaretPosition(0);

        JScrollPane scrollPane =
                new JScrollPane(textArea);

        scrollPane.setPreferredSize(
                new Dimension(
                        700,
                        400
                )
        );

        JButton copyButton =
                new JButton("Copy");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(copyButton);

        buttonPanel.add(closeButton);

        JDialog dialog =
                new JDialog(
                        this,
                        "Clipboard Content",
                        true
                );

        dialog.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        dialog.add(
                scrollPane,
                BorderLayout.CENTER
        );

        dialog.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        copyButton.addActionListener(e -> {

            copyToClipboard(
                    item.getContent()
            );

            copyButton.setText(
                    "Copied!"
            );
        });

        closeButton.addActionListener(
                e -> dialog.dispose()
        );

        dialog.setSize(
                750,
                500
        );

        dialog.setLocationRelativeTo(
                this
        );

        dialog.setVisible(true);
    }

    // =========================================================
    // ACTION RENDERER
    // =========================================================

    private static class ActionRenderer
            extends JLabel
            implements TableCellRenderer {

        public ActionRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setVerticalAlignment(
                    SwingConstants.CENTER
            );

            setOpaque(true);

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            setText(
                    "<html>" +
                            "<span style='color:#cc0000'>" +
                            "[ Delete ]" +
                            "</span>" +
                            "</html>"
            );

            if (isSelected) {

                setBackground(
                        table.getSelectionBackground()
                );

            } else {

                setBackground(
                        table.getBackground()
                );
            }

            return this;
        }
    }
}
