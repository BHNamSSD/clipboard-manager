package com.bhnam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class JsonEventLogger implements AutoCloseable {

    private final Path logPath;
    private final BufferedWriter writer;

    public JsonEventLogger() throws IOException {
        Path logDirectory = Path.of("logs");
        Files.createDirectories(logDirectory);

        logPath = logDirectory.resolve("keyboard-events.jsonl");

        writer = Files.newBufferedWriter(
                logPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    public synchronized void log(
            String eventType,
            int keyCode,
            String keyName
    ) throws IOException {

        String json = """
                {"timestamp":"%s","source":"keyboard-lab","event":"%s","keyCode":%d,"keyName":"%s"}
                """.formatted(
                escape(Instant.now().toString()),
                escape(eventType),
                keyCode,
                escape(keyName)
        ).trim();

        writer.write(json);
        writer.newLine();
        writer.flush();
    }

    public Path getLogPath() {
        return logPath;
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public synchronized void close() throws IOException {
        writer.close();
    }
}