package com.example.remotecu;

import java.util.ArrayList;
import java.util.List;

public class RemoteProfile {
    private String id;
    private String name;
    private String icon;
    private List<RemoteButton> buttons;
    private long createdAt;
    private long modifiedAt;

    public RemoteProfile(String name, String icon) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = name;
        this.icon = icon;
        this.buttons = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = System.currentTimeMillis();
    }

    public RemoteProfile() {
        this.buttons = new ArrayList<>();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.modifiedAt = System.currentTimeMillis();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<RemoteButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<RemoteButton> buttons) {
        this.buttons = buttons;
    }

    public void addButton(RemoteButton button) {
        this.buttons.add(button);
        this.modifiedAt = System.currentTimeMillis();
    }

    public void removeButton(RemoteButton button) {
        this.buttons.remove(button);
        this.modifiedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public int getButtonCount() {
        return buttons != null ? buttons.size() : 0;
    }
}
