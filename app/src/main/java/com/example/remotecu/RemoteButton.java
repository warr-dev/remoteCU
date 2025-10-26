package com.example.remotecu;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoteButton {
    private String name;
    private String irCode;
    private String icon; // Icon/emoji for button
    private float posX;
    private float posY;
    private int width;
    private int height;
    private boolean isMacro;
    private String macroData; // JSON string containing macro commands

    // Popular IR remote button icons
    public static final String[] AVAILABLE_ICONS = {
        "⚡", // Power
        "🔴", // Record
        "▶️", // Play
        "⏸️", // Pause
        "⏹️", // Stop
        "⏮️", // Previous
        "⏭️", // Next
        "🔊", // Volume Up
        "🔉", // Volume Down
        "🔇", // Mute
        "➕", // Increase/Up
        "➖", // Decrease/Down
        "◀️", // Left
        "▶️", // Right
        "🔼", // Up Arrow
        "🔽", // Down Arrow
        "✅", // OK/Enter
        "↩️", // Return/Back
        "🏠", // Home
        "📱", // Source/Input
        "⚙️", // Settings/Menu
        "ℹ️", // Info
        "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "0️⃣", // Numbers
        "📺", // TV
        "❄️", // AC Cool
        "🔥", // AC Heat
        "💨", // Fan
        "💡", // Light
        "🌡️", // Temperature
        "⏰", // Timer
        "🔄", // Refresh/Repeat
        "🎬", // Netflix/Video
        "▪️"  // Generic/Custom
    };

    public RemoteButton(String name, String irCode) {
        this.name = name;
        this.irCode = irCode;
        this.icon = "▪️"; // Default icon
        this.posX = -1; // -1 means auto-layout
        this.posY = -1;
        this.width = -1;
        this.height = -1;
        this.isMacro = false;
        this.macroData = null;
    }

    public RemoteButton(String name, String irCode, float posX, float posY, int width, int height) {
        this.name = name;
        this.irCode = irCode;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.isMacro = false;
        this.macroData = null;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIrCode() {
        return irCode;
    }

    public void setIrCode(String irCode) {
        this.irCode = irCode;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMacro() {
        return isMacro;
    }

    public void setMacro(boolean macro) {
        isMacro = macro;
    }

    public String getMacroData() {
        return macroData;
    }

    public void setMacroData(String macroData) {
        this.macroData = macroData;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Convert to JSON for storage
    public String toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("irCode", irCode);
            json.put("icon", icon != null ? icon : "▪️");
            json.put("posX", posX);
            json.put("posY", posY);
            json.put("width", width);
            json.put("height", height);
            json.put("isMacro", isMacro);
            json.put("macroData", macroData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    // Create from JSON
    public static RemoteButton fromJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            RemoteButton button = new RemoteButton(
                    json.getString("name"),
                    json.getString("irCode"),
                    (float) json.getDouble("posX"),
                    (float) json.getDouble("posY"),
                    json.getInt("width"),
                    json.getInt("height")
            );
            if (json.has("icon")) {
                button.setIcon(json.getString("icon"));
            }
            button.setMacro(json.getBoolean("isMacro"));
            if (json.has("macroData") && !json.isNull("macroData")) {
                button.setMacroData(json.getString("macroData"));
            }
            return button;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
