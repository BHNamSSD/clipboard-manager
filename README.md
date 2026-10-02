# Clipboard Manager

A lightweight Windows clipboard history manager built with **Java 21** and **Swing**.

Clipboard Manager automatically monitors the Windows system clipboard, stores copied text, and provides a simple GUI to search, view, copy, and delete clipboard history.

> **Status:** Work in Progress

---

## Features

* 📋 Automatically monitor Windows clipboard
* 🕒 Store clipboard history with timestamp
* 🔎 Search clipboard history
* 📄 View full clipboard content
* 📑 Copy saved content back to clipboard
* 🗑️ Delete individual clipboard items
* 🧹 Clear all clipboard history
* 🖥️ Lightweight Swing GUI
* ☕ Built with Java 21
* 📦 Can be packaged as a Windows `.exe`

---

## Screenshot

*Add application screenshots here.*

```text
+---------------------------------------------------------------+
| [ Search clipboard...                         ] [ Clear All ] |
+---------------------------------------------------------------+
| Time     | Content                              | Action      |
+---------------------------------------------------------------+
| 16:20:31 | GET /login HTTP/1.1                  | Copy Delete |
| 16:20:12 | suspicious-command                    | Copy Delete |
| 16:19:45 | https://example.com                   | Copy Delete |
+---------------------------------------------------------------+
| Monitoring: ● Active                                         |
+---------------------------------------------------------------+
```

---

## Requirements

* Windows
* Java 21

Check your Java version:

```powershell
java -version
```

Expected:

```text
openjdk version "21.x.x"
```

---

## Project Structure

```text
clipboard-manager/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── bhnam/
│                   ├── Main.java
│                   ├── ClipboardUI.java
│                   ├── ClipboardItem.java
│                   └── ClipboardMonitor.java
│
├── pom.xml
├── ClipboardManager.jar
└── README.md
```

---

## Architecture

The application currently consists of four main classes:

### `Main`

Application entry point.

Responsibilities:

* Start the Swing UI
* Start the clipboard monitoring thread
* Connect clipboard events to the GUI

### `ClipboardMonitor`

Monitors the Windows system clipboard.

```text
Windows Clipboard
       │
       ▼
ClipboardMonitor
       │
       ▼
New clipboard content
       │
       ▼
ClipboardUI
```

### `ClipboardItem`

Represents a clipboard history entry.

Stores:

* Clipboard content
* Timestamp

### `ClipboardUI`

Main Swing graphical interface.

Provides:

* Search
* Clipboard history table
* Copy
* Delete
* Clear All
* Full content viewer

---

## Build from Source

The project can be built directly with the JDK without requiring Maven to be installed globally.

### 1. Compile

Using JDK 21:

```powershell
& "C:\Users\psvin\.jdks\ms-21.0.12.1\bin\javac.exe" `
    -encoding UTF-8 `
    -d .\out `
    .\src\main\java\com\bhnam\*.java
```

### 2. Create JAR

```powershell
& "C:\Users\psvin\.jdks\ms-21.0.12.1\bin\jar.exe" `
    --create `
    --file ClipboardManager.jar `
    --main-class com.bhnam.Main `
    -C out .
```

The result:

```text
ClipboardManager.jar
```

---

## Run

Run the application with Java 21:

```powershell
& "C:\Users\psvin\.jdks\ms-21.0.12.1\bin\java.exe" `
    -jar .\ClipboardManager.jar
```

Or, if Java 21 is configured in your `PATH`:

```powershell
java -jar ClipboardManager.jar
```

---

## Build Windows EXE

The project can also be packaged using Java 21 `jpackage`.

### Portable Application

Create a Windows application image:

```powershell
& "C:\Users\psvin\.jdks\ms-21.0.12.1\bin\jpackage.exe" `
    --type app-image `
    --name ClipboardManager `
    --input . `
    --main-jar ClipboardManager.jar `
    --main-class com.bhnam.Main `
    --dest dist
```

Output:

```text
dist/
└── ClipboardManager/
    ├── ClipboardManager.exe
    ├── app/
    └── runtime/
```

Run:

```powershell
.\dist\ClipboardManager\ClipboardManager.exe
```

The application image contains its own Java runtime, so the target machine does not need a separate Java installation.

---

## Build Windows Installer

To create a Windows `.exe` installer:

```powershell
& "C:\Users\psvin\.jdks\ms-21.0.12.1\bin\jpackage.exe" `
    --type exe `
    --name ClipboardManager `
    --input . `
    --main-jar ClipboardManager.jar `
    --main-class com.bhnam.Main `
    --dest dist
```

The installer will be generated inside:

```text
dist/
```

---

## Current Limitations

The current version stores clipboard history **only in memory**.

This means:

```text
Start application
       ↓
Copy text
       ↓
Clipboard history
       ↓
Close application
       ↓
History is lost
```

Clipboard contents are currently text-only.

---

## Roadmap

### V1 — Clipboard Monitoring

* [x] Clipboard monitoring
* [x] Clipboard history
* [x] Timestamp
* [x] Search
* [x] Copy item
* [x] Delete item
* [x] Clear history
* [x] Full content viewer

### V2 — Persistent Storage

* [ ] SQLite database
* [ ] Persistent clipboard history
* [ ] History survives application restart
* [ ] Maximum history size
* [ ] Automatic cleanup

### V3 — Clipboard Intelligence

* [ ] Detect URLs
* [ ] Detect IP addresses
* [ ] Detect email addresses
* [ ] Detect hashes
* [ ] Detect commands
* [ ] Detect API keys/tokens
* [ ] Content type filtering

### V4 — Security Features

* [ ] Sensitive clipboard detection
* [ ] Auto-delete sensitive content
* [ ] Configurable retention period
* [ ] Exclusion patterns
* [ ] Password/token protection
* [ ] Clipboard history encryption

### V5 — Windows Integration

* [ ] System tray
* [ ] Start with Windows
* [ ] Global hotkey
* [ ] Background mode
* [ ] Windows installer
* [ ] Application icon

---

## Security Considerations

Clipboard data can contain sensitive information such as:

* Passwords
* API keys
* Authentication tokens
* Cookies
* SSH keys
* Personal information
* Internal commands

The current version stores clipboard contents in application memory without encryption.

**Do not use the application to permanently store sensitive clipboard data in the current version.**

Future versions will provide configurable retention and sensitive-data protection.

---

## Technology Stack

| Component    | Technology        |
| ------------ | ----------------- |
| Language     | Java              |
| Java Version | 21                |
| GUI          | Java Swing        |
| Build        | JDK tools / Maven |
| Database     | Planned: SQLite   |
| Packaging    | `jpackage`        |
| Platform     | Windows           |

---

## License

This project is currently provided for personal and educational use.

License information will be added in a future release.

---

## Author

**BHNamSSD**

Security Engineering / SOC / Java Security Tools

GitHub:

`BHNamSSD`
