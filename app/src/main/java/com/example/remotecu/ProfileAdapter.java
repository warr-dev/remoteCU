package com.example.remotecu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<RemoteProfile> profiles;
    private OnProfileClickListener listener;
    private String activeProfileId;

    public interface OnProfileClickListener {
        void onProfileClick(RemoteProfile profile);
        void onProfileMenuClick(RemoteProfile profile, View view);
    }

    public ProfileAdapter(List<RemoteProfile> profiles, OnProfileClickListener listener) {
        this.profiles = profiles;
        this.listener = listener;
    }

    public void setActiveProfileId(String activeProfileId) {
        this.activeProfileId = activeProfileId;
        notifyDataSetChanged();
    }

    public void updateProfiles(List<RemoteProfile> newProfiles) {
        this.profiles = newProfiles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        RemoteProfile profile = profiles.get(position);
        holder.bind(profile);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView profileIcon;
        TextView profileName;
        TextView profileButtonCount;
        TextView activeIndicator;
        ImageView profileMenuIcon;
        MaterialCardView cardView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIcon = itemView.findViewById(R.id.profileIcon);
            profileName = itemView.findViewById(R.id.profileName);
            profileButtonCount = itemView.findViewById(R.id.profileButtonCount);
            activeIndicator = itemView.findViewById(R.id.activeIndicator);
            profileMenuIcon = itemView.findViewById(R.id.profileMenuIcon);
            cardView = (MaterialCardView) itemView;
        }

        public void bind(RemoteProfile profile) {
            profileIcon.setText(profile.getIcon());
            profileName.setText(profile.getName());

            int buttonCount = profile.getButtons().size();
            profileButtonCount.setText(buttonCount + " button" + (buttonCount != 1 ? "s" : ""));

            // Show active indicator if this is the active profile
            if (profile.getId().equals(activeProfileId)) {
                activeIndicator.setVisibility(View.VISIBLE);
            } else {
                activeIndicator.setVisibility(View.GONE);
            }

            // Click listener for the whole card - open remote in full screen
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProfileClick(profile);
                }
            });

            // Long-press listener for edit/delete options
            cardView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onProfileMenuClick(profile, v);
                }
                return true;
            });

            // Click listener for menu icon
            profileMenuIcon.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProfileMenuClick(profile, v);
                }
            });
        }
    }
}
