package com.example.remotecu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RemoteButtonListAdapter extends RecyclerView.Adapter<RemoteButtonListAdapter.ButtonViewHolder> {

    private List<RemoteButton> buttons;
    private OnButtonActionListener listener;

    public interface OnButtonActionListener {
        void onButtonClick(RemoteButton button);
    }

    public RemoteButtonListAdapter(List<RemoteButton> buttons, OnButtonActionListener listener) {
        this.buttons = buttons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_remote_button_list, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        RemoteButton button = buttons.get(position);
        holder.bind(button);
    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }

    public void updateButtons(List<RemoteButton> newButtons) {
        this.buttons = newButtons;
        notifyDataSetChanged();
    }

    class ButtonViewHolder extends RecyclerView.ViewHolder {
        TextView iconPreview;
        TextView namePreview;
        TextView buttonName;
        TextView buttonIrCode;

        ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            iconPreview = itemView.findViewById(R.id.buttonIconPreview);
            namePreview = itemView.findViewById(R.id.buttonNamePreview);
            buttonName = itemView.findViewById(R.id.buttonName);
            buttonIrCode = itemView.findViewById(R.id.buttonIrCode);
        }

        void bind(RemoteButton button) {
            // Set icon and name preview (mini button)
            iconPreview.setText(button.getIcon() != null ? button.getIcon() : "▪️");
            namePreview.setText(getTruncatedName(button.getName(), 6));

            // Set full button details
            buttonName.setText(button.getName());
            buttonIrCode.setText("IR: " + (button.getIrCode() != null && !button.getIrCode().isEmpty()
                    ? button.getIrCode()
                    : "Not set"));

            // Set click listener - tapping the item sends IR signal
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onButtonClick(button);
                }
            });
        }

        private String getTruncatedName(String name, int maxLength) {
            if (name == null) return "";
            if (name.length() <= maxLength) return name;
            return name.substring(0, maxLength);
        }
    }
}
