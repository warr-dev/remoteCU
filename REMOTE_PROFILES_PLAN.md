# Remote Profiles Feature - Implementation Plan

## Overview
Adding support for multiple remote control profiles to allow users to organize buttons by device type (TV, AC, Sound System, etc.)

## Features

### 1. Profile Management
- **Create** multiple remote profiles
- **Switch** between profiles
- **Edit** profile name and icon
- **Delete** profiles
- Each profile has its own set of buttons

### 2. Profile Data Structure
```java
RemoteProfile {
    - id: String (unique identifier)
    - name: String (e.g., "TV Remote", "AC", "Sound System")
    - icon: String (emoji like 📺, ❄️, 🔊)
    - buttons: List<RemoteButton>
    - createdAt: long
    - modifiedAt: long
}
```

### 3. UI Components

#### A. Profile Selector
- Accessible from toolbar/appbar
- Shows list of all profiles
- Visual indicator for active profile
- "Create New Profile" button

#### B. Profile Item Display
- Icon (emoji)
- Profile name
- Button count
- Active indicator (checkmark)
- Edit/Delete menu

#### C. Profile Creation Dialog
- Name input field
- Icon picker (common device emojis)
- Save/Cancel buttons

### 4. Storage
- Extend `SharedPrefManager` to store:
  - List of all profiles
  - Active profile ID
  - Profile-specific button data

### 5. User Flow

#### Creating a Profile:
1. Tap profile selector in toolbar
2. Tap "Create New Profile"
3. Enter name and select icon
4. Profile is created and becomes active
5. Add buttons to this profile

#### Switching Profiles:
1. Tap profile selector in toolbar
2. Select profile from list
3. Button container updates to show profile's buttons
4. Active indicator shows current profile

#### Editing a Profile:
1. Open profile selector
2. Tap menu icon on profile item
3. Choose "Edit" or "Delete"
4. Make changes and save

## Implementation Steps

### Phase 1: Core Data & Storage ✅
- [x] Create RemoteProfile class
- [ ] Update SharedPrefManager for profile support
- [ ] Add profile CRUD operations

### Phase 2: UI Components
- [x] Create profile selector dialog layout
- [x] Create profile item layout
- [ ] Create profile creation/edit dialog
- [ ] Create ProfileAdapter for RecyclerView
- [ ] Add profile selector to toolbar

### Phase 3: MainActivity Integration
- [ ] Add profile state management
- [ ] Update button loading to use active profile
- [ ] Update button saving to current profile
- [ ] Add profile switching logic

### Phase 4: Default Profiles
- [ ] Create default profile templates:
  - 📺 TV Remote
  - ❄️ Air Conditioner
  - 🔊 Sound System
  - 💡 Smart Lights
  - 🎮 Gaming Console

## Default Profile Icons
- 📺 TV/Monitor
- ❄️ Air Conditioner
- 🔥 Heater
- 🔊 Audio/Speakers
- 💡 Lights
- 🎮 Gaming
- 📡 Set-top Box
- 🎬 Media Player
- 🚪 Garage Door
- ⚙️ Custom/Other

## Benefits
1. **Organization**: Group buttons by device type
2. **Clarity**: Easier to find the right button
3. **Scalability**: Support for many devices
4. **UX**: Better user experience with logical grouping
5. **Flexibility**: Users can create custom profiles for any device

## Future Enhancements
- Profile import/export
- Profile sharing (QR code)
- Cloud sync across devices
- Profile templates marketplace
- Custom profile colors/themes
