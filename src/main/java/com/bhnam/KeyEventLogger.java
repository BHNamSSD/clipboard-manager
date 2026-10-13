package com.bhnam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KeyEventLogger extends JFrame {

    private final JTextArea inputArea = new JTextArea(8, 40);
    private final JTextArea logArea = new JTextArea(12, 40);

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public KeyEventLogger() {
        setTitle("Keyboard Event Logger - Lab");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logArea.setEditable(false);
        inputArea.setLineWrap(true);

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                logEvent("PRESSED", KeyEvent.getKeyText(e.getKeyCode()));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                logEvent("RELEASED", KeyEvent.getKeyText(e.getKeyCode()));
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isISOControl(c)) {
                    logEvent("TYPED", String.valueOf(c));
                }
            }
        });

        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(e -> logArea.setText(""));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Nhập phím vào ô bên dưới:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(new JLabel("Event Log"), BorderLayout.NORTH);
        bottom.add(new JScrollPane(logArea), BorderLayout.CENTER);
        bottom.add(clearButton, BorderLayout.SOUTH);

        setLayout(new GridLayout(2, 1, 8, 8));
        add(panel);
        add(bottom);

        pack();
        setLocationRelativeTo(null);
    }

    private void logEvent(String type, String key) {
        String time = LocalDateTime.now().format(formatter);
        logArea.append(time + " | " + type + " | " + key + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new KeyEventLogger().setVisible(true)
        );
    }
}