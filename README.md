
# RemoteCu

**A fully customizable IR remote control app for Android** - Learn, create, and control any IR device from your phone!

[![Version](https://img.shields.io/badge/version-2.0-blue)]()
[![Android](https://img.shields.io/badge/Android-12%2B-green)]()
[![License](https://img.shields.io/badge/license-MIT-orange)]()

---

## 🌟 What is RemoteCu?

RemoteCu solves the problem of **generic or brandless remote controls**. If you have IR-controlled devices without smartphone apps (cheap fans, AC units, LED lights, etc.), this app lets you:

- 📱 Replace all your physical remotes with your phone
- 🎯 Create custom button layouts exactly how you want
- 🔄 Learn IR codes from existing remotes via BLE
- ⚡ Execute multiple commands with macro buttons
- 🎨 Drag and drop buttons anywhere on screen

---

## ✨ Features

### Core Functionality
- ✅ **IR Signal Transmission** - Send IR signals using phone's IR blaster
- ✅ **BLE Learning Mode** - Capture codes from existing remotes via Arduino/ESP32
- ✅ **Custom Buttons** - Add unlimited buttons with custom names
- ✅ **NEC Protocol Support** - Compatible with most IR devices

### v2.0 New Features
- 🎯 **Drag & Drop Layout** - Position buttons anywhere on screen
- ✏️ **Button Editing** - Edit button names and IR codes after creation
- 🗑️ **Easy Deletion** - Long-press to delete with confirmation
- 🎬 **Macro Support** - Create command sequences with custom delays
- 💾 **Smart Persistence** - Positions and settings saved automatically

---

## 🚀 Quick Start

### For Users
1. Install the APK on your Android phone (requires IR blaster)
2. Add buttons manually or learn from existing remotes
3. Point and click to control your devices!

👉 **[See QUICK_START.md](QUICK_START.md) for detailed instructions**

### For Developers
1. Open project in VSCode
2. Connect Android device via USB
3. Press `Ctrl + Shift + P` → "Tasks: Run Task" → "Android: Build, Install and Run"

👉 **[See VSCODE_GUIDE.md](VSCODE_GUIDE.md) for complete development setup**

---

## 📋 Requirements

### For End Users
- Android phone with IR blaster (e.g., Xiaomi phones)
- Android 12 or higher
- Bluetooth (for learning mode)

### For Learning Mode (Optional)
- Arduino board with Bluetooth (ESP32 recommended)
- IR receiver module (TSOP38238 or similar)
- IRremote library

### For Development
- Visual Studio Code
- Android SDK
- Java JDK 8+
- ADB (Android Debug Bridge)

---

## 📖 Documentation

| Document | Description |
|----------|-------------|
| **[QUICK_START.md](QUICK_START.md)** | 5-minute getting started guide |
| **[VSCODE_GUIDE.md](VSCODE_GUIDE.md)** | Complete VSCode development guide |
| **[FEATURES.md](FEATURES.md)** | Detailed feature documentation |

---

## 🎯 Use Cases

### Why I Made This

I had **5 generic IR remote-controlled appliances** at home:
- Cheap ceiling fan
- No-name LED strip
- Generic AC unit
- Unbranded air purifier
- Random IR-controlled socket

Each came with a different remote, and none had smartphone apps. I was constantly looking for the right remote. My phone has an IR blaster, so why not create ONE app to rule them all?

### Who This Is For

- People with **generic/brandless IR devices**
- Users wanting **custom remote layouts**
- Smart home enthusiasts
- People who lose remotes frequently
- Anyone wanting to consolidate multiple remotes

---

## 🏗️ Architecture

### Core Components

```
┌─────────────────────────────────────┐
│         MainActivity                │
│  - UI management                    │
│  - Button creation/editing          │
│  - BLE connection                   │
└─────────────────────────────────────┘
            │
    ┌───────┴───────┐
    ▼               ▼
┌─────────┐   ┌──────────────┐
│ Macro   │   │ Draggable    │
│ Executor│   │ Button       │
└─────────┘   └──────────────┘
    │               │
    ▼               ▼
┌─────────────────────────────────────┐
│      SharedPrefManager              │
│  - Data persistence                 │
│  - Button storage                   │
└─────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────┐
│      ConsumerIrManager              │
│  - IR signal transmission           │
└─────────────────────────────────────┘
```

### Data Flow

```
Physical Remote → IR Receiver → Arduino/ESP32
        ↓
    BLE UART → Android App → Store IR Code
        ↓
Button Tap → IR Code → Phone IR Blaster → Device
```

---

## 🛠️ Development

### Project Structure

```
remoteCU/
├── app/src/main/
│   ├── java/com/example/remotecu/
│   │   ├── MainActivity.java          # Main app logic
│   │   ├── RemoteButton.java          # Button data model
│   │   ├── DraggableButton.java       # Custom draggable widget
│   │   ├── MacroExecutor.java         # Macro execution engine
│   │   └── SharedPrefManager.java     # Data persistence
│   └── res/layout/
│       ├── activity_main.xml          # Main UI
│       ├── dialog_edit_button.xml     # Edit dialog
│       └── dialog_create_macro.xml    # Macro creation
├── .vscode/
│   └── tasks.json                     # VSCode build tasks
└── docs/
    ├── QUICK_START.md
    ├── VSCODE_GUIDE.md
    └── FEATURES.md
```

### Building

**Using VSCode (Recommended):**
```
Ctrl + Shift + P → Tasks: Run Task → Android: Build, Install and Run
```

**Using Terminal:**
```bash
# Windows
gradlew.bat assembleDebug

# Build and install
gradlew.bat installDebug
```

### Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

## 🎬 Example Macros

### "Movie Mode"
1. Turn on TV (1000ms delay)
2. Switch to HDMI 1 (500ms delay)
3. Set volume to 20 (500ms delay)
4. Turn on soundbar (1000ms delay)

### "Bedtime"
1. Turn off TV (500ms delay)
2. Turn off AC (500ms delay)
3. Turn off lights (500ms delay)

### "Gaming Setup"
1. Turn on TV (1000ms delay)
2. Switch to HDMI 2 (500ms delay)
3. Turn on RGB lights (500ms delay)
4. Set AC to cool (500ms delay)

---

## 🔮 Future Enhancements

- [ ] Multiple remote profiles with tabs
- [ ] Button templates and icons
- [ ] Cloud backup/sync
- [ ] Export/import configurations
- [ ] Widget support (home screen shortcuts)
- [ ] IR code database integration
- [ ] Voice control
- [ ] Button colors and themes

---

## 🐛 Known Issues

- Requires phone with IR blaster (hardware limitation)
- BLE connection may take 5-10 seconds
- Some older IR protocols not supported (only NEC currently)

---

## 📄 License

MIT License - Feel free to use, modify, and distribute!

---

## 🙏 Acknowledgments

- **IRremote Library** for Arduino IR handling
- **Android BLE APIs** for Bluetooth communication
- **Material Design** for UI components

---

## 📞 Support

- 📖 Read the [documentation](VSCODE_GUIDE.md)
- 🐛 Report issues on GitHub
- 💡 Request features via issues

---

**Made with ❤️ for people tired of losing remote controls!**

*Last updated: 2025-10-26*
