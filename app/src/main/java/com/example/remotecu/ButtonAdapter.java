package com.example.remotecu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {

    private ArrayList<String> buttonValues;
    private Context context;

    public ButtonAdapter(Context context, ArrayList<String> buttonValues) {
        this.context = context;
        this.buttonValues = buttonValues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String irValue = buttonValues.get(position);
        holder.button.setText("IR: " + irValue);

        holder.button.setOnClickListener(v -> {
            // Display the IR signal as a toast for now
            Toast.makeText(context, "Sending IR: " + irValue, Toast.LENGTH_SHORT).show();
            // You will later replace this with actual IR transmission logic
        });
    }

    @Override
    public int getItemCount() {
        return buttonValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.dynamic_button);
        }
    }
}
