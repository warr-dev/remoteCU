# RemoteCU - Feature Documentation

Complete guide to all features in the RemoteCU app, including the new enhancements.

---

## Table of Contents
1. [Overview](#overview)
2. [Core Features](#core-features)
3. [New Features (v2.0)](#new-features-v20)
4. [How to Use](#how-to-use)
5. [Feature Deep Dive](#feature-deep-dive)
6. [Tips & Best Practices](#tips--best-practices)

---

## Overview

RemoteCU is a dynamic IR remote control app for Android that allows you to:
- Create custom remote controls for any IR device
- Learn IR codes from existing remotes using BLE
- Organize buttons with drag-and-drop
- Edit buttons after creation
- Create macros (sequences of commands)
- Save and restore your remote layouts

---

## Core Features

### 1. IR Signal Transmission
- Uses your phone's built-in IR blaster
- Supports NEC protocol (most common IR protocol)
- Converts hex codes to IR signals
- Works with TV, AC, fans, and most IR devices

### 2. BLE Learning Mode
- Connect to Arduino/ESP32 IR receiver via Bluetooth LE
- Capture IR codes from existing remotes
- Automatically populate hex codes
- No manual code entry needed

### 3. Button Persistence
- All buttons saved automatically
- Survives app restarts
- Cloud backup ready (via SharedPreferences)

---

## New Features (v2.0)

### ✨ Feature B: Drag-and-Drop Button Positioning

**What it does:**
- Move buttons anywhere on the screen
- Custom remote layouts
- Position saved automatically

**How to use:**
1. Tap **"Edit Mode"** button
2. Drag any button to new position
3. Release to save position
4. Tap **"Done"** when finished

**Use cases:**
- Create TV remote layout (volume left, channels right)
- Group related buttons (power buttons together)
- Mimic physical remote layout
- Personal ergonomic preferences

---

### ✏️ Feature C: Button Editing

**What it does:**
- Edit button name after creation
- Update IR codes
- Delete buttons with confirmation

**How to edit:**
1. Enable **"Edit Mode"**
2. **Tap** any button
3. Edit name or IR code in dialog
4. Click **"Save"**

**How to delete:**
1. Enable **"Edit Mode"**
2. **Long-press** any button
3. Confirm deletion

**Use cases:**
- Fix typos in button names
- Update wrong IR codes
- Rename buttons for clarity
- Remove unused buttons

---

### 🎯 Feature D: Macro Support

**What it does:**
- Create sequences of IR commands
- Add delays between commands
- Execute multiple actions with one tap

**How to create macro:**
1. Tap **"Macro"** button
2. Enter macro name (e.g., "Movie Mode")
3. Add commands:
   - Enter IR code
   - Set delay (milliseconds)
   - Click **"+"** to add
4. Repeat for all commands
5. Click **"Save Macro"**

**Example macros:**

**"Movie Mode"**
- Turn on TV (delay 1000ms)
- Switch to HDMI 1 (delay 500ms)
- Adjust volume to 20 (delay 500ms)
- Turn on surround sound (delay 1000ms)

**"Bedtime"**
- Turn off TV (delay 500ms)
- Turn off AC (delay 500ms)
- Turn off lights (if IR controlled)

**"Channel Surf"**
- Channel up (delay 3000ms)
- Channel up (delay 3000ms)
- Channel up (delay 3000ms)

---

## How to Use

### First Time Setup

1. **Open RemoteCU app**
2. **Connect BLE device** (optional):
   - Tap "Scan BLE"
   - Select your Arduino/ESP32
   - Device will send IR codes when detected

3. **Add your first button**:
   - Enter button name (e.g., "Power")
   - Enter IR code in hex (e.g., "FF00FF")
   - Or point existing remote at BLE receiver
   - Tap "Add New Button"

4. **Test the button**:
   - Point phone at IR device
   - Tap the button
   - Device should respond

### Daily Use

**Normal Mode (default):**
- Tap buttons to send IR signals
- Macro buttons execute sequences
- Quick and simple operation

**Edit Mode:**
- Tap "Edit Mode" to enable
- Rearrange buttons by dragging
- Tap to edit, long-press to delete
- Tap "Done" when finished

---

## Feature Deep Dive

### Button Types

#### Standard Button
- Single IR code
- One tap = one signal
- Example: Power button, Volume up

#### Macro Button
- Multiple IR codes with delays
- One tap = sequence of signals
- Indicated by special icon (optional enhancement)
- Example: "Movie Mode", "Bedtime"

### Edit Mode Details

**Visual Indicators:**
- Edit Mode button turns **red** when active
- Edit Mode button shows **"Done"** text
- Buttons become draggable

**What you can do:**
- **Drag**: Move buttons freely
- **Tap**: Open edit dialog
- **Long-press**: Delete button

**What you cannot do:**
- Send IR signals (prevents accidental triggers)
- Create new buttons (use "Add New Button")

### Data Storage

All data stored in SharedPreferences:

**Old format (v1):**
```
ButtonPrefs/
  "Power" → "FF00FF"
  "Volume Up" → "FF807F"
```

**New format (v2):**
```
ButtonPrefsV2/
  "Power" → {JSON object with position, size, macro data}
  "Movie Mode" → {JSON macro data}
```

**Backward compatible:** Old buttons automatically migrate on first load.

---

## Tips & Best Practices

### Creating Effective Remotes

**1. Group by Function**
- Put power buttons at top
- Volume controls together
- Channel/navigation together

**2. Use Meaningful Names**
- ❌ "Btn1", "Btn2"
- ✅ "TV Power", "AC Cool", "Fan Speed+"

**3. Test Before Saving**
- Learn IR code
- Test immediately
- Confirm device responds
- Then save and name

**4. Start Simple**
- Add essential buttons first
- Test thoroughly
- Add advanced buttons later
- Create macros when comfortable

### Macro Best Practices

**Timing is Critical:**
- Devices need time to process commands
- Use at least 500ms between commands
- TVs often need 1000ms+ to turn on

**Test Individual Commands First:**
- Make sure each IR code works alone
- Then combine into macro
- Easier to debug

**Keep Macros Short:**
- 3-5 commands maximum
- Long macros = more points of failure
- Create multiple short macros instead

**Name Macros Clearly:**
- Use action names: "Start Gaming", "Shutdown All"
- Avoid technical jargon
- Future you will thank you

### Organizing Layouts

**TV Remote Example:**
```
┌─────────────────┐
│   [TV Power]    │ ← Top center
├────────┬────────┤
│[Vol-]  │ [Vol+] │ ← Left side
├────────┼────────┤
│[CH-]   │ [CH+]  │
├────────┴────────┤
│  [1] [2] [3]    │ ← Number pad
│  [4] [5] [6]    │
│  [7] [8] [9]    │
│     [0]         │
└─────────────────┘
```

**AC Remote Example:**
```
┌─────────────────┐
│   [AC Power]    │
├─────────────────┤
│ [Cool] [Heat]   │
│ [Fan]  [Dry]    │
├─────────────────┤
│   [Temp +]      │
│   [Temp -]      │
├─────────────────┤
│ [Fan Low]       │
│ [Fan Med]       │
│ [Fan High]      │
└─────────────────┘
```

### Backup Your Configuration

**Method 1: Android Backup**
- Settings → System → Backup
- Includes app data
- Automatic restore on new device

**Method 2: Manual Export** (future feature)
- Export to JSON file
- Share via email/cloud
- Import on other devices

---

## Troubleshooting

### "IR signal not working"

**Check:**
1. Does your phone have IR blaster?
   - Not all phones have this
   - Required for IR transmission
2. Is device within range? (typically 3-5 meters)
3. Is IR code correct? (test with BLE learning)
4. Is device on/in standby?

### "BLE device not connecting"

**Check:**
1. Bluetooth enabled on phone?
2. Location permission granted?
3. Device powered on?
4. Device in range? (typically 10 meters)
5. Try rescanning

### "Button disappeared"

**Check:**
1. Did you delete it in Edit Mode?
2. Check if it's off-screen (drag Edit Mode to find)
3. App data cleared? (starts fresh)

### "Macro not executing fully"

**Check:**
1. Delays long enough?
2. All IR codes valid?
3. Device responding to individual commands?
4. Try increasing delays

### "Edit Mode stuck"

**Solution:**
- Tap "Done" button
- Restart app if needed

---

## Advanced Usage

### IR Code Format

RemoteCU uses **hex format** for NEC protocol:

**Example codes:**
- `FF00FF` - Common power toggle
- `FF807F` - Common volume up
- `FF40BF` - Common channel up

**Converting:**
- Most IR databases provide hex codes
- Some provide binary (convert to hex)
- Some provide decimal (convert to hex)

### BLE Receiver Setup

**Hardware needed:**
- Arduino/ESP32 board with BLE
- IR receiver module (TSOP38238 or similar)
- Power source (USB or battery)

**Software:**
- IRremote library
- BLE UART service
- Sends hex codes over BLE characteristic

**Connection:**
```
IR Receiver Module → Arduino
    VCC → 5V
    GND → GND
    OUT → Digital Pin 11
```

### Custom UUIDs

If you have custom BLE UUIDs, edit MainActivity.java:

```java
// Line ~395
BluetoothGattService service = bluetoothGatt.getService(
    UUID.fromString("YOUR-SERVICE-UUID"));

BluetoothGattCharacteristic characteristic = service.getCharacteristic(
    UUID.fromString("YOUR-CHARACTERISTIC-UUID"));
```

---

## Future Enhancements

Possible future features:
- [ ] Multiple remote profiles (tabs)
- [ ] Button templates/shapes
- [ ] Button colors and icons
- [ ] Cloud sync
- [ ] Export/import configurations
- [ ] Widget support (home screen shortcuts)
- [ ] Voice control integration
- [ ] IR code database integration

---

## Version History

### v2.0 (Current)
- ✅ Drag-and-drop button positioning
- ✅ Edit button name/IR code
- ✅ Delete buttons with confirmation
- ✅ Macro support with delays
- ✅ Improved UI with Edit Mode
- ✅ Better data persistence

### v1.0 (Original)
- ✅ Basic IR transmission
- ✅ BLE learning mode
- ✅ Add/remove buttons
- ✅ GridLayout button organization
- ✅ SharedPreferences storage

---

## Support

**Issues?**
- Check this documentation first
- Review [VSCODE_GUIDE.md](VSCODE_GUIDE.md) for development help
- Check logs: `adb logcat | grep RemoteCu`

**Feature requests?**
- Open an issue in the repository
- Describe use case clearly
- Include mockups if possible

---

*Made with ❤️ for custom remote control enthusiasts*
*Last updated: 2025-10-26*
