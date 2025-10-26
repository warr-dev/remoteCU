# RemoteCU - VSCode Development Guide

Complete guide for developing and running the RemoteCU Android app in Visual Studio Code.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Initial Setup](#initial-setup)
3. [Running the App](#running-the-app)
4. [Available VSCode Tasks](#available-vscode-tasks)
5. [Debugging](#debugging)
6. [Common Issues](#common-issues)
7. [Tips & Tricks](#tips--tricks)

---

## Prerequisites

### Required Software
- ✅ **Visual Studio Code** - [Download](https://code.visualstudio.com/)
- ✅ **Android SDK** - Installed via Android Studio or standalone
- ✅ **Java JDK 8 or higher**
- ✅ **ADB (Android Debug Bridge)** - Included with Android SDK

### Recommended VSCode Extensions
1. **Extension Pack for Java** (Microsoft)
   - Press `Ctrl + Shift + X`
   - Search for "Extension Pack for Java"
   - Click Install

2. **Gradle for Java** (Microsoft)
   - Automatically manages Gradle builds
   - Already installed in your project

3. **Android for VS Code** (adelphes) - Optional but recommended
   - Provides Android-specific features
   - Device management
   - APK building UI

---

## Initial Setup

### 1. Open Project in VSCode

```bash
# Open the project folder
cd "c:\Users\Predator Helios 300\projects\remoteCU"
code .
```

Or from VSCode:
- `File → Open Folder`
- Navigate to the remoteCU folder

### 2. Verify Android SDK Path

The Android SDK should be detected automatically. If not:

1. Press `Ctrl + ,` (Settings)
2. Search for "android sdk"
3. Set the path (usually `C:\Users\YourName\AppData\Local\Android\Sdk`)

### 3. Connect Your Android Device

**Enable USB Debugging on your phone:**
1. Go to `Settings → About Phone`
2. Tap "Build Number" 7 times
3. Go to `Settings → Developer Options`
4. Enable "USB Debugging"
5. Connect phone via USB
6. Accept the debugging prompt on your phone

**Verify connection:**
```bash
adb devices
```

You should see your device listed.

---

## Running the App

### Method 1: Quick Run (Keyboard Shortcut)

**Press `Ctrl + Shift + B`**
- Builds the APK
- This is the fastest way to build

Then run the install task separately.

### Method 2: Build, Install & Run (Recommended)

1. **Press `Ctrl + Shift + P`** (Command Palette)
2. Type: **"Tasks: Run Task"**
3. Select: **"Android: Build, Install and Run"**

The app will:
- ✅ Build the APK
- ✅ Install on your device
- ✅ Automatically launch

### Method 3: Using the Terminal

**Build only:**
```bash
./gradlew.bat assembleDebug
```

**Build and Install:**
```bash
./gradlew.bat assembleDebug installDebug
```

**Build, Install, and Run:**
```bash
./gradlew.bat assembleDebug installDebug && adb shell am start -n com.example.remotecu/.MainActivity
```

---

## Available VSCode Tasks

All tasks are accessible via:
- **`Ctrl + Shift + P`** → "Tasks: Run Task"

### Task List

| Task Name | Description | When to Use |
|-----------|-------------|-------------|
| **Android: Build Debug APK** | Just builds the APK file | To check for compilation errors |
| **Android: Install Debug APK** | Installs already-built APK | When APK exists and you just want to reinstall |
| **Android: Build and Install** | Builds + Installs | Most common for development |
| **Android: Build, Install and Run** ⭐ | Builds + Installs + Launches | Best for active development |
| **Android: Launch App** | Just launches installed app | To reopen app without rebuilding |
| **Android: Clean Build** | Deletes build cache and rebuilds | When you have build issues |
| **Android: Uninstall App** | Removes app from device | To start fresh or fix signature issues |
| **Android: List Connected Devices** | Shows connected Android devices | To verify device connection |

### Quick Access

**Default Build Task:**
- **`Ctrl + Shift + B`** → Automatically runs "Build Debug APK"

---

## Debugging

### View Logs (Logcat)

**In Terminal:**
```bash
adb logcat | grep RemoteCu
```

**Filter for errors only:**
```bash
adb logcat *:E
```

**Clear logs:**
```bash
adb logcat -c
```

### Common Debug Commands

**Check if app is running:**
```bash
adb shell pm list packages | grep remotecu
```

**Force stop the app:**
```bash
adb shell am force-stop com.example.remotecu
```

**Clear app data:**
```bash
adb shell pm clear com.example.remotecu
```

**View app info:**
```bash
adb shell dumpsys package com.example.remotecu
```

### Using VSCode Debugger

1. Install "Debugger for Java" extension
2. Press `F5` to start debugging
3. Set breakpoints by clicking left of line numbers

---

## Common Issues

### Issue 1: "Gradle sync failed"

**Solution:**
```bash
# Clean the project
./gradlew.bat clean

# Re-sync
./gradlew.bat build
```

### Issue 2: "INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Cause:** App signatures don't match

**Solution:**
```bash
# Uninstall the old app first
adb uninstall com.example.remotecu

# Then reinstall
./gradlew.bat installDebug
```

Or use the task: **"Android: Uninstall App"** then reinstall.

### Issue 3: "No devices found"

**Check connection:**
```bash
adb devices
```

**If no devices listed:**
```bash
# Kill and restart ADB server
adb kill-server
adb start-server
```

### Issue 4: Build is slow

**Speed up builds:**

1. **Increase Gradle memory:**
   Edit `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx2048m
   org.gradle.daemon=true
   org.gradle.parallel=true
   ```

2. **Use incremental builds:**
   - Don't use "Clean Build" unless necessary
   - Let Gradle cache work for you

### Issue 5: "Cannot find symbol" errors

**Solution:**
```bash
# Clean and rebuild
./gradlew.bat clean build
```

Or in VSCode:
- `Ctrl + Shift + P` → "Java: Clean Java Language Server Workspace"

---

## Tips & Tricks

### 1. Faster Development Workflow

**Edit → Save → Auto Build:**
- VSCode can auto-compile on save
- Press `Ctrl + Shift + P` → "Java: Configure Workspace Settings"

### 2. Multiple Device Testing

**List all connected devices:**
```bash
adb devices
```

**Install on specific device:**
```bash
adb -s <device_id> install app/build/outputs/apk/debug/app-debug.apk
```

### 3. View APK Location

After building, find your APK at:
```
app/build/outputs/apk/debug/app-debug.apk
```

You can share this file to install on other devices.

### 4. Quick Navigation

- **`Ctrl + P`** - Quick file search
- **`Ctrl + Shift + F`** - Search across all files
- **`F12`** - Go to definition
- **`Alt + Left/Right`** - Navigate back/forward

### 5. Integrated Terminal

- **`Ctrl + ~`** - Toggle terminal
- Can run all adb/gradle commands here
- Multiple terminals supported

### 6. Code Snippets

Create custom snippets for common code:
- `File → Preferences → User Snippets → java.json`

Example snippet for Toast:
```json
{
  "Toast": {
    "prefix": "toast",
    "body": [
      "Toast.makeText(${1:this}, \"${2:message}\", Toast.LENGTH_SHORT).show();"
    ]
  }
}
```

### 7. Git Integration

VSCode has built-in Git support:
- **`Ctrl + Shift + G`** - Open Source Control
- See changes, commit, push/pull
- Your project is already a Git repo!

### 8. Format Code

**Format entire file:**
- **`Shift + Alt + F`**

**Format selection:**
- Select code → Right-click → "Format Selection"

---

## Project Structure

```
remoteCU/
├── .vscode/
│   ├── tasks.json          # Your custom tasks (build, run, etc.)
│   └── launch.json         # Debug configurations
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/remotecu/
│   │   │   │   ├── MainActivity.java          # Main activity
│   │   │   │   ├── RemoteButton.java          # Button data model
│   │   │   │   ├── DraggableButton.java       # Draggable button widget
│   │   │   │   ├── MacroCommand.java          # Macro command model
│   │   │   │   ├── MacroExecutor.java         # Executes macros
│   │   │   │   ├── MacroCommandAdapter.java   # RecyclerView adapter
│   │   │   │   ├── SharedPrefManager.java     # Data persistence
│   │   │   │   └── ButtonAdapter.java         # Legacy adapter
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml      # Main UI layout
│   │   │   │   │   ├── dialog_edit_button.xml # Edit button dialog
│   │   │   │   │   ├── dialog_create_macro.xml# Create macro dialog
│   │   │   │   │   └── item_macro_command.xml # Macro command item
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle                        # App dependencies
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties           # Gradle version
├── build.gradle                                # Project build config
├── settings.gradle                             # Project settings
├── gradlew.bat                                 # Gradle wrapper (Windows)
└── VSCODE_GUIDE.md                            # This file!
```

---

## Keyboard Shortcuts Cheat Sheet

| Shortcut | Action |
|----------|--------|
| `Ctrl + Shift + P` | Command Palette (run tasks) |
| `Ctrl + Shift + B` | Build (default task) |
| `Ctrl + ~` | Toggle Terminal |
| `Ctrl + P` | Quick file search |
| `Ctrl + Shift + F` | Search in files |
| `F12` | Go to definition |
| `Shift + F12` | Find references |
| `Ctrl + /` | Toggle comment |
| `Shift + Alt + F` | Format code |
| `Ctrl + Shift + X` | Extensions |
| `Ctrl + ,` | Settings |
| `F5` | Start debugging |

---

## Development Workflow Example

**Typical development session:**

1. **Start VSCode**
   ```bash
   code .
   ```

2. **Connect device and verify**
   ```bash
   adb devices
   ```

3. **Make code changes**
   - Edit Java files
   - Modify layouts
   - VSCode auto-saves

4. **Build and run**
   - `Ctrl + Shift + P` → "Tasks: Run Task" → "Android: Build, Install and Run"
   - Wait ~5-10 seconds
   - App launches on phone

5. **Test feature**
   - Use the app on phone
   - Check logs: `adb logcat`

6. **Repeat steps 3-5**

7. **Commit changes**
   - `Ctrl + Shift + G` (Source Control)
   - Stage changes
   - Write commit message
   - Commit

---

## Troubleshooting Commands

**Reset everything:**
```bash
# Kill ADB
adb kill-server
adb start-server

# Clean project
./gradlew.bat clean

# Rebuild
./gradlew.bat assembleDebug
```

**Check Java installation:**
```bash
java -version
```

**Check Gradle version:**
```bash
./gradlew.bat --version
```

**Verify Android SDK:**
```bash
adb --version
```

---

## Resources

### Official Documentation
- [Android Developer Guide](https://developer.android.com)
- [Gradle Build Tool](https://gradle.org/guides/)
- [VSCode Java](https://code.visualstudio.com/docs/java/java-tutorial)

### Useful Commands Reference
- [ADB Commands Cheat Sheet](https://gist.github.com/Pulimet/5013acf2cd5b28e55036c82c91bd56d8)
- [Gradle Tasks](https://docs.gradle.org/current/userguide/command_line_interface.html)

---

## Next Steps

Now that you're set up:

1. ✅ Try running the app: `Ctrl + Shift + P` → "Android: Build, Install and Run"
2. ✅ Make a small change (e.g., change a button text)
3. ✅ Rebuild and see the change on your phone
4. ✅ Explore the new features (Edit Mode, Drag-Drop, Macros)

**Happy coding! 🚀**

---

*Last updated: 2025-10-26*
*Project: RemoteCU - Dynamic IR Remote Mapping App*
