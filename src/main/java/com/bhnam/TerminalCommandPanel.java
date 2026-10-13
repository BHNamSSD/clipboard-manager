
package com.bhnam;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

public class TerminalCommandPanel extends JPanel {

    private final JComboBox<String> shellSelector =
            new JComboBox<>(new String[]{"CMD", "PowerShell"});

    private final JTextField commandField = new JTextField();
    private final JTextArea outputArea = new JTextArea();

    private final Path logPath =
            Path.of("logs", "terminal-events.jsonl");

    public TerminalCommandPanel() {
        super(new BorderLayout(8, 8));

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
        JButton executeButton = new JButton("Thực thi");

        inputPanel.add(shellSelector, BorderLayout.WEST);
        inputPanel.add(commandField, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        executeButton.addActionListener(e -> executeCommand());
        commandField.addActionListener(e -> executeCommand());
    }

    private void executeCommand() {
        String command = commandField.getText().trim();

        if (command.isEmpty()) {
            return;
        }

        String shell = (String) shellSelector.getSelectedItem();
        commandField.setText("");

        executeCommandAsync(shell, command);
    }

    private void executeCommandAsync(String shell, String command) {
        outputArea.append("> " + shell + ": " + command + "\n");

        new SwingWorker<Integer, String>() {
            private String result = "";

            @Override
            protected Integer doInBackground() throws Exception {
                ProcessBuilder builder;

                if ("PowerShell".equals(shell)) {
                    builder = new ProcessBuilder(
                            "powershell.exe",
                            "-NoLogo",
                            "-NoProfile",
                            "-Command",
                            command
                    );
                } else {
                    builder = new ProcessBuilder(
                            "cmd.exe",
                            "/d",
                            "/s",
                            "/c",
                            command
                    );
                }

                builder.redirectErrorStream(true);
                Process process = builder.start();

                StringBuilder output = new StringBuilder();

                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(
                                process.getInputStream(),
                                StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        publish(line);
                    }
                }

                int exitCode = process.waitFor();
                result = output.toString();

                return exitCode;
            }

            @Override
            protected void process(java.util.List<String> lines) {
                for (String line : lines) {
                    outputArea.append(line + "\n");
                }
            }

            @Override
            protected void done() {
                try {
                    int exitCode = get();

                    logEvent(shell, command, exitCode, result);
                    outputArea.append(
                            "[Exit code: " + exitCode + "]\n\n"
                    );
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    outputArea.append("[Đã bị gián đoạn]\n");
                } catch (ExecutionException ex) {
                    outputArea.append(
                            "[Lỗi: " + ex.getCause().getMessage() + "]\n"
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    private void logEvent(
            String shell,
            String command,
            int exitCode,
            String output
    ) throws IOException {
        Files.createDirectories(logPath.getParent());

        String json = "{"
                + "\"timestamp\":\"" + escape(Instant.now().toString()) + "\","
                + "\"source\":\"terminal-command-panel\","
                + "\"shell\":\"" + escape(shell) + "\","
                + "\"command\":\"" + escape(command) + "\","
                + "\"exitCode\":" + exitCode + ","
                + "\"output\":\"" + escape(output) + "\""
                + "}";

        Files.writeString(
                logPath,
                json + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
