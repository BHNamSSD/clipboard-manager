
package com.bhnam;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class KeyboardLabPanel extends JPanel implements AutoCloseable {

    private final JTextArea inputArea = new JTextArea(5, 40);
    private final JTextArea eventArea = new JTextArea();
    private final JsonEventLogger logger;



    public KeyboardLabPanel() throws IOException {
        super(new BorderLayout(8, 8));

        logger = new JsonEventLogger();

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        eventArea.setEditable(false);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Vùng nhập thử nghiệm"
                )
        );
        inputPanel.add(new JScrollPane(inputArea));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Sự kiện bàn phím"
                )
        );
        logPanel.add(new JScrollPane(eventArea));

        JButton clearButton = new JButton("Xóa hiển thị");
        clearButton.addActionListener(e -> eventArea.setText(""));

        add(inputPanel, BorderLayout.NORTH);
        add(logPanel, BorderLayout.CENTER);
        add(clearButton, BorderLayout.SOUTH);

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                record("KEY_PRESSED", e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                record("KEY_RELEASED", e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                record("KEY_TYPED", e);
            }



        });
    }



    private void record(String eventType, KeyEvent e) {
        String keyName = KeyEvent.getKeyText(e.getKeyCode());

        try {
            logger.log(eventType, e.getKeyCode(), keyName);

            eventArea.append(
                    eventType
                            + " | code=" + e.getKeyCode()
                            + " | key=" + keyName + "\n"
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không ghi được log: " + ex.getMessage(),
                    "Lỗi ghi log",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void close() throws IOException {
        logger.close();
    }
}
