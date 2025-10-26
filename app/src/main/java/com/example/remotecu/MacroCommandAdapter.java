package com.example.remotecu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MacroCommandAdapter extends RecyclerView.Adapter<MacroCommandAdapter.ViewHolder> {
    private List<MacroCommand> commands;
    private OnCommandRemovedListener removeListener;

    public interface OnCommandRemovedListener {
        void onCommandRemoved(int position);
    }

    public MacroCommandAdapter(List<MacroCommand> commands, OnCommandRemovedListener listener) {
        this.commands = commands;
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_macro_command, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MacroCommand command = commands.get(position);
        holder.irCodeText.setText("IR Code: " + command.getIrCode());
        holder.delayText.setText("Delay: " + command.getDelayMs() + "ms");

        holder.removeButton.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onCommandRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commands.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView irCodeText;
        TextView delayText;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            irCodeText = itemView.findViewById(R.id.commandIrCodeText);
            delayText = itemView.findViewById(R.id.commandDelayText);
            removeButton = itemView.findViewById(R.id.btnRemoveCommand);
        }
    }
}
