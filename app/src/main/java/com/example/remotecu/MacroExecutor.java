package com.example.remotecu;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MacroExecutor {
    private final Handler handler;
    private final IrTransmitter irTransmitter;

    public interface IrTransmitter {
        void transmit(String irCode);
    }

    public MacroExecutor(IrTransmitter irTransmitter) {
        this.handler = new Handler(Looper.getMainLooper());
        this.irTransmitter = irTransmitter;
    }

    // Execute a macro from JSON string
    public void executeMacro(String macroDataJson) {
        List<MacroCommand> commands = parseMacroData(macroDataJson);
        if (commands != null && !commands.isEmpty()) {
            executeMacroCommands(commands, 0);
        }
    }

    // Parse macro data from JSON
    private List<MacroCommand> parseMacroData(String macroDataJson) {
        List<MacroCommand> commands = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(macroDataJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                MacroCommand cmd = MacroCommand.fromJson(jsonArray.getJSONObject(i));
                if (cmd != null) {
                    commands.add(cmd);
                }
            }
        } catch (JSONException e) {
            Log.e("MacroExecutor", "Error parsing macro data", e);
            return null;
        }
        return commands;
    }

    // Recursively execute commands with delays
    private void executeMacroCommands(final List<MacroCommand> commands, final int index) {
        if (index >= commands.size()) {
            return; // All commands executed
        }

        MacroCommand command = commands.get(index);

        // Transmit the IR code
        irTransmitter.transmit(command.getIrCode());

        // Schedule next command after delay
        if (index + 1 < commands.size()) {
            handler.postDelayed(() -> executeMacroCommands(commands, index + 1), command.getDelayMs());
        }
    }

    // Convert list of MacroCommands to JSON string
    public static String macroCommandsToJson(List<MacroCommand> commands) {
        JSONArray jsonArray = new JSONArray();
        for (MacroCommand cmd : commands) {
            jsonArray.put(cmd.toJson());
        }
        return jsonArray.toString();
    }
}
