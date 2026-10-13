package com.bhnam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class KeyboardLabFrame extends JFrame {

    private final JTextArea inputArea = new JTextArea(6, 45);
    private final JTextArea eventArea = new JTextArea(12, 45);
    private final JLabel statusLabel = new JLabel("Logger: Active");

    private final JsonEventLogger logger;

    public KeyboardLabFrame() throws IOException {
        super("Keyboard Security Lab");

        logger = new JsonEventLogger();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        eventArea.setEditable(false);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(
                BorderFactory.createTitledBorder("Test Input")
        );
        inputPanel.add(new JScrollPane(inputArea));

        JPanel eventPanel = new JPanel(new BorderLayout(5, 5));
        eventPanel.setBorder(
                BorderFactory.createTitledBorder("Keyboard Events")
        );
        eventPanel.add(new JScrollPane(eventArea));

        JButton clearButton = new JButton("Clear View");
        clearButton.addActionListener(e -> eventArea.setText(""));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(clearButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.NORTH);
        add(eventPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                recordEvent("KEY_PRESSED", e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                recordEvent("KEY_RELEASED", e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Chỉ ghi loại sự kiện, không ghi ký tự đã nhập.
                recordEvent("KEY_TYPED", e);
            }
        });

        setSize(700, 600);
        setLocationRelativeTo(null);

        statusLabel.setText(
                "Log: " + logger.getLogPath().toAbsolutePath()
        );

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                closeLogger();
            }
        });
    }

    private void recordEvent(String type, KeyEvent e) {
        String keyName = KeyEvent.getKeyText(e.getKeyCode());

        try {
            logger.log(type, e.getKeyCode(), keyName);

            eventArea.append(
                    type + " | code=" + e.getKeyCode()
                            + " | key=" + keyName + "\n"
            );

            eventArea.setCaretPosition(
                    eventArea.getDocument().getLength()
            );
        } catch (IOException ex) {
            statusLabel.setText("Log error: " + ex.getMessage());
        }
    }

    private void closeLogger() {
        try {
            logger.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new KeyboardLabFrame().setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Cannot initialize logger: " + e.getMessage(),
                        "Logger Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}