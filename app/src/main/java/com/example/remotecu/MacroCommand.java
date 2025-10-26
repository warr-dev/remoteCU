package com.example.remotecu;

import org.json.JSONException;
import org.json.JSONObject;

public class MacroCommand {
    private String irCode;
    private int delayMs; // Delay after this command in milliseconds

    public MacroCommand(String irCode, int delayMs) {
        this.irCode = irCode;
        this.delayMs = delayMs;
    }

    public String getIrCode() {
        return irCode;
    }

    public void setIrCode(String irCode) {
        this.irCode = irCode;
    }

    public int getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    // Convert to JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("irCode", irCode);
            json.put("delayMs", delayMs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Create from JSON
    public static MacroCommand fromJson(JSONObject json) {
        try {
            return new MacroCommand(
                    json.getString("irCode"),
                    json.getInt("delayMs")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
