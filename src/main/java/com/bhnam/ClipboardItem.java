package com.bhnam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClipboardItem {

    private final String content;
    private final LocalDateTime time;

    public ClipboardItem(String content) {
        this.content = content;
        this.time = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time.format(
                DateTimeFormatter.ofPattern("HH:mm:ss")
        );
    }
}