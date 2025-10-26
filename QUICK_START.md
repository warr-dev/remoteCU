# RemoteCU - Quick Start Guide

Get up and running with RemoteCU in 5 minutes!

---

## 🚀 Running the App in VSCode

### Option 1: Using Tasks (Easiest)
1. Press **`Ctrl + Shift + P`**
2. Type: **"Tasks: Run Task"**
3. Select: **"Android: Build, Install and Run"**
4. Wait ~10 seconds
5. App opens on your phone! ✅

### Option 2: Keyboard Shortcut
1. Press **`Ctrl + Shift + B`** to build
2. Press **`Ctrl + Shift + P`**
3. Select: **"Android: Install Debug APK"**
4. Manually open app on phone

### Option 3: Terminal
```bash
# Full build, install, and launch
cmd /c "gradlew.bat assembleDebug installDebug && adb shell am start -n com.example.remotecu/.MainActivity"
```

---

## 📱 Using the App

### Adding a Button
1. Enter button name (e.g., "TV Power")
2. Enter IR code in hex (e.g., "FF00FF")
3. Tap **"Add New Button"**
4. Button appears on screen

### Sending IR Signal
- Just **tap the button**
- Point phone at device
- IR signal sent!

### Learning IR Codes (BLE)
1. Tap **"Scan BLE"**
2. Select your Arduino/ESP32
3. Point remote at IR receiver
4. Press remote button
5. Hex code appears automatically

---

## ✨ New Features

### Edit Mode (Drag & Drop + Edit)
1. Tap **"Edit Mode"** (turns red)
2. **Drag** buttons to reposition
3. **Tap** button to edit name/code
4. **Long-press** to delete
5. Tap **"Done"** when finished

### Creating Macros
1. Tap **"Macro"** button
2. Enter macro name
3. Add commands:
   - IR code: `FF00FF`
   - Delay: `1000` (ms)
   - Tap **"+"**
4. Repeat for more commands
5. Tap **"Save Macro"**

---

## 🔧 Quick Troubleshooting

### Build fails?
```bash
./gradlew.bat clean build
```

### App won't install?
```bash
adb uninstall com.example.remotecu
./gradlew.bat installDebug
```

### Device not detected?
```bash
adb devices
# If empty:
adb kill-server
adb start-server
```

---

## 📚 Full Documentation

- **[VSCODE_GUIDE.md](VSCODE_GUIDE.md)** - Complete VSCode setup and workflow
- **[FEATURES.md](FEATURES.md)** - All app features explained
- **[README.md](README.md)** - Project overview

---

## ⌨️ Essential Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + Shift + P` | Run tasks |
| `Ctrl + Shift + B` | Build app |
| `Ctrl + ~` | Terminal |
| `Ctrl + P` | Quick file search |

---

**That's it! You're ready to go! 🎉**
