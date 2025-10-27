package com.example.remotecu;

public class ButtonTemplate {
    private String name;
    private String icon;

    public ButtonTemplate(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    // Common remote button templates
    public static final ButtonTemplate[] COMMON_TEMPLATES = {
        new ButtonTemplate("Power", "⚡"),
        new ButtonTemplate("Volume Up", "🔊"),
        new ButtonTemplate("Volume Down", "🔉"),
        new ButtonTemplate("Mute", "🔇"),
        new ButtonTemplate("Channel Up", "⬆️"),
        new ButtonTemplate("Channel Down", "⬇️"),
        new ButtonTemplate("Play", "▶️"),
        new ButtonTemplate("Pause", "⏸️"),
        new ButtonTemplate("Stop", "⏹️"),
        new ButtonTemplate("Record", "🔴"),
        new ButtonTemplate("Rewind", "⏪"),
        new ButtonTemplate("Fast Forward", "⏩"),
        new ButtonTemplate("Menu", "☰"),
        new ButtonTemplate("Home", "🏠"),
        new ButtonTemplate("Back", "◀️"),
        new ButtonTemplate("OK", "✓"),
        new ButtonTemplate("Up", "⬆️"),
        new ButtonTemplate("Down", "⬇️"),
        new ButtonTemplate("Left", "⬅️"),
        new ButtonTemplate("Right", "➡️"),
        new ButtonTemplate("0", "0️⃣"),
        new ButtonTemplate("1", "1️⃣"),
        new ButtonTemplate("2", "2️⃣"),
        new ButtonTemplate("3", "3️⃣"),
        new ButtonTemplate("4", "4️⃣"),
        new ButtonTemplate("5", "5️⃣"),
        new ButtonTemplate("6", "6️⃣"),
        new ButtonTemplate("7", "7️⃣"),
        new ButtonTemplate("8", "8️⃣"),
        new ButtonTemplate("9", "9️⃣"),
        new ButtonTemplate("Input", "📺"),
        new ButtonTemplate("Info", "ℹ️"),
        new ButtonTemplate("Guide", "📋"),
        new ButtonTemplate("Exit", "❌"),
        new ButtonTemplate("Settings", "⚙️"),
    };
}
