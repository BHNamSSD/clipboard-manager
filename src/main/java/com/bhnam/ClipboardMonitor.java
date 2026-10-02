package com.bhnam;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.function.Consumer;

public class ClipboardMonitor implements Runnable {

    private final Clipboard clipboard;
    private final Consumer<String> onNewContent;

    private String lastContent = "";
    private volatile boolean running = true;

    public ClipboardMonitor(Consumer<String> onNewContent) {
        this.clipboard =
                Toolkit.getDefaultToolkit().getSystemClipboard();

        this.onNewContent = onNewContent;
    }

    @Override
    public void run() {

        while (running) {

            try {

                if (clipboard.isDataFlavorAvailable(
                        DataFlavor.stringFlavor)) {

                    String content =
                            (String) clipboard.getData(
                                    DataFlavor.stringFlavor
                            );

                    if (content != null
                            && !content.equals(lastContent)) {

                        lastContent = content;

                        onNewContent.accept(content);
                    }
                }

                Thread.sleep(300);

            } catch (Exception ignored) {
            }
        }
    }

    public void stop() {
        running = false;
    }
}