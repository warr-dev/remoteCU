# RemoteCU - Quick Reference Guide

## 🎯 What Changed?

### Before (Old Flow):
```
Main Screen
├── Scan BLE Button
├── Edit Mode Button
├── Macro Button
├── Add Button Form (always visible)
└── Button Container (all buttons mixed together)
```

### After (New Flow):
```
Main Screen - Remote List
├── Welcome Header
├── Quick Actions (Edit Mode & Macro)
├── Remote Profiles (list)
│   ├── 📺 TV Remote (5 buttons)
│   ├── ❄️ AC Remote (3 buttons)
│   └── 🔊 Sound System (8 buttons)
└── + FAB (create new remote)

Remote Detail Screen (tap any remote)
├── Remote Header (icon, name, count)
├── Button Container (buttons for THIS remote)
└── + FAB (add button to this remote)
```

---

## 🚀 Quick Start

### 1. Create a Remote:
```
1. Tap + button (bottom-right)
2. Type: "TV Remote"
3. Tap: 📺 icon
4. Tap: "Create Profile"
```

### 2. Add Buttons:
```
1. In remote detail screen
2. Tap + button
3. Name: "Power"
4. IR Code: "0xFF00"
5. Tap "Save"
```

### 3. Organize:
```
- Create different remotes for different devices
- Each remote has its own buttons
- Tap remote to open it
- Drag buttons to arrange them
```

---

## 📱 Main Screen Elements

| Element | Action | Result |
|---------|--------|--------|
| Remote Card | Tap | Opens remote detail |
| Menu Icon (⋮) | Tap | Shows Edit/Delete |
| + FAB | Tap | Creates new remote |
| Edit Mode | Tap | Toggle edit mode |
| Macro | Tap | Macro feature (TBD) |
| Scan BLE | Tap | BLE scan (TBD) |

---

## 🎛️ Remote Detail Elements

| Element | Action | Result |
|---------|--------|--------|
| Back Arrow | Tap | Return to list |
| + FAB | Tap | Add new button |
| Button | Tap | Send IR code |
| Button | Drag | Reposition |
| Button | Long Press | Edit/Delete (if edit mode) |

---

## 💾 Data Storage

### Where is data saved?
- **Location**: SharedPreferences
- **File**: `RemoteProfiles`
- **Format**: JSON

### What is saved?
- ✅ All remote profiles
- ✅ Profile icons and names
- ✅ All buttons per remote
- ✅ Button positions
- ✅ Active profile ID

---

## 🎨 Available Icons

| Icon | Device Type |
|------|-------------|
| 📺 | TV/Monitor |
| ❄️ | Air Conditioner |
| 🔥 | Heater |
| 🔊 | Audio/Speakers |
| 💡 | Lights |
| 🎮 | Gaming Console |
| 📡 | Set-top Box |
| 🎬 | Media Player |
| 🚪 | Garage Door |
| ⚙️ | Custom/Other |

---

## 🔧 Troubleshooting

### No remotes showing?
- Tap + FAB to create first remote
- Check empty state message

### Can't add buttons?
- Make sure you're in remote detail screen
- Tap + FAB in detail screen, not main screen

### Buttons not saving?
- Fill both name and IR code fields
- Check toast notification for confirmation

### Want to delete remote?
- Tap menu icon (⋮) on remote card
- Select "Delete"
- Confirm deletion

---

## 📖 Terminology

| Term | Meaning |
|------|---------|
| **Remote/Profile** | A collection of buttons for one device |
| **Button** | Individual IR command |
| **FAB** | Floating Action Button (+) |
| **IR Code** | Infrared signal code (hex) |
| **Active Profile** | Currently selected remote (shows ✓) |

---

## ⚡ Tips & Tricks

1. **Organize by Room**
   - "Living Room TV"
   - "Bedroom AC"
   - "Kitchen Lights"

2. **Use Descriptive Names**
   - "Samsung TV Remote"
   - "Daikin AC"
   - "Sony Sound Bar"

3. **Button Naming**
   - Use clear names: "Power", "Vol+", "Ch-"
   - Not: "Btn1", "Button2"

4. **Test IR Codes**
   - Add one button first
   - Test it works
   - Then add more

---

## 🎯 Common Tasks

### Duplicate a Remote:
```
(Future feature - currently manual)
1. Create new remote with same icon
2. Manually add same buttons
```

### Reorganize Buttons:
```
1. Open remote
2. Enable Edit Mode (if available)
3. Drag buttons to new positions
4. Positions save automatically
```

### Backup Your Data:
```
(Manual for now)
1. Find SharedPreferences file
2. Copy RemoteProfiles file
3. Save externally
```

---

## 📞 Support

For issues or questions:
1. Check [FINAL_IMPLEMENTATION.md](FINAL_IMPLEMENTATION.md)
2. Check [REMOTE_PROFILES_PLAN.md](REMOTE_PROFILES_PLAN.md)
3. Review code documentation

---

**Happy Remote Controlling! 🎮📺🔊**
