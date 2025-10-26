# RemoteCU - Changelog

All notable changes to this project will be documented in this file.

---

## [2.0.0] - 2025-10-26

### ✨ Major New Features

#### Option B: Enhanced UI with Drag-Drop Button Positioning
- **Added** `DraggableButton.java` - Custom draggable button widget
- **Added** Free-form button positioning using FrameLayout
- **Added** Edit Mode toggle button
- **Added** Automatic position saving
- **Changed** Button container from GridLayout to FrameLayout
- **Feature** Buttons can be dragged anywhere on screen in Edit Mode
- **Feature** Position persistence across app restarts

#### Option C: Button Editing
- **Added** Edit button dialog with Material Design
- **Added** `dialog_edit_button.xml` layout
- **Feature** Tap buttons in Edit Mode to edit name/IR code
- **Feature** Long-press in Edit Mode to delete with confirmation
- **Feature** Button name changes without losing position
- **Feature** Real-time button updates after editing

#### Option D: Macro Support
- **Added** `MacroCommand.java` - Macro command data model
- **Added** `MacroExecutor.java` - Macro execution engine
- **Added** `MacroCommandAdapter.java` - RecyclerView adapter for macro commands
- **Added** `dialog_create_macro.xml` - Macro creation dialog
- **Added** `item_macro_command.xml` - Macro command list item
- **Feature** Create sequences of IR commands
- **Feature** Custom delays between commands (in milliseconds)
- **Feature** Visual macro command builder
- **Feature** Remove individual commands from macros
- **Feature** Macro buttons execute sequentially with delays

### 🔄 Improvements

#### Data Management
- **Added** `RemoteButton.java` - Enhanced button data model
- **Updated** `SharedPrefManager.java` to support new RemoteButton format
- **Added** Backward compatibility for v1.0 button data
- **Added** Automatic migration from v1.0 to v2.0 format
- **Feature** Button positions, sizes, and macro data now persisted

#### UI Enhancements
- **Updated** `activity_main.xml` with new button layout
- **Added** Three-button toolbar (Scan BLE, Edit Mode, Macro)
- **Added** Visual feedback for Edit Mode (button turns red)
- **Improved** Button creation flow
- **Added** Material Design dialogs

#### Code Structure
- **Refactored** `createDynamicButton()` to use RemoteButton model
- **Added** `createDynamicButtonFromRemoteButton()` for better modularity
- **Added** `updateButtonsEditMode()` helper method
- **Added** `showEditButtonDialog()` for button editing
- **Added** `showDeleteConfirmDialog()` for safe deletion
- **Added** `showCreateMacroDialog()` for macro creation
- **Improved** Code organization and separation of concerns

### 🛠️ Developer Experience

#### VSCode Integration
- **Added** `.vscode/tasks.json` with 8 custom tasks
- **Added** `.vscode/launch.json` for debugging
- **Fixed** Windows PowerShell compatibility for tasks
- **Added** Auto-launch after install task
- **Feature** One-click build, install, and run

#### Documentation
- **Added** `VSCODE_GUIDE.md` - Complete VSCode development guide
- **Added** `FEATURES.md` - Detailed feature documentation
- **Added** `QUICK_START.md` - 5-minute quick start guide
- **Added** `CHANGELOG.md` - This file
- **Updated** `README.md` - Professional project overview with badges
- **Added** Comprehensive troubleshooting sections
- **Added** Architecture diagrams
- **Added** Example use cases and macros

### 🐛 Bug Fixes
- **Fixed** Gradle version compatibility (downgraded from 9.0 to 8.0)
- **Fixed** Button container reference from GridLayout to FrameLayout
- **Fixed** MacroCommandAdapter variable initialization issue
- **Fixed** Task command compatibility with Windows cmd

### 📱 UI/UX Changes
- **Changed** Button layout from fixed grid to free-form
- **Added** Edit Mode toggle for safer operation
- **Added** Visual distinction between normal and edit modes
- **Improved** Button interaction feedback
- **Added** Confirmation dialogs for destructive actions

---

## [1.0.0] - Initial Release

### Features
- ✅ Basic IR signal transmission using NEC protocol
- ✅ BLE connection to Arduino/ESP32 for learning IR codes
- ✅ Add custom buttons with name and IR code
- ✅ GridLayout button organization
- ✅ Long-press to delete buttons
- ✅ SharedPreferences for data persistence
- ✅ Bluetooth LE scanning and device selection
- ✅ Real-time IR code reception from BLE device
- ✅ USB serial support (for wired connection)

### Components
- `MainActivity.java` - Main activity with all logic
- `SharedPrefManager.java` - Data persistence
- `ButtonAdapter.java` - RecyclerView adapter
- `activity_main.xml` - Main UI layout
- `button_item.xml` - Button item layout

---

## Upgrade Guide

### From v1.0 to v2.0

**Data Migration:**
- All existing buttons automatically migrated to new format
- Buttons initially positioned in top-left corner
- Use Edit Mode to rearrange as desired

**New Permissions:**
- No new permissions required
- All existing permissions still used

**Breaking Changes:**
- None - fully backward compatible

**New Features Available:**
1. Enable Edit Mode to start dragging buttons
2. Tap "Macro" to create your first command sequence
3. Long-press buttons in Edit Mode to delete

---

## Development Stats

### Lines of Code Added (v2.0)
- Java: ~800 lines
- XML: ~200 lines
- Documentation: ~2000 lines

### Files Added (v2.0)
- Java classes: 4 new files
- Layout files: 3 new files
- Documentation: 4 new files
- Configuration: 2 new files

### Dependencies
- No new dependencies added
- Uses existing Android SDK components
- Material Design components (already included)

---

## Known Issues

### v2.0
- First-time button positioning may overlap (use Edit Mode to fix)
- Very long macro sequences (>10 commands) may have timing issues
- Edit Mode button color only changes on tap (no animation)

### v1.0 (Carried Forward)
- Requires phone with IR blaster (hardware limitation)
- BLE connection may take 5-10 seconds
- Only NEC protocol supported
- No visual feedback during IR transmission

---

## Planned for Future Releases

### v2.1 (Upcoming)
- [ ] Button visual indicators for macros
- [ ] Undo/redo for button edits
- [ ] Button size adjustment in Edit Mode
- [ ] Grid snapping option
- [ ] Button alignment helpers

### v3.0 (Future)
- [ ] Multiple remote profiles with tabs
- [ ] Button templates and icons
- [ ] Cloud backup/sync
- [ ] Export/import configurations
- [ ] Widget support
- [ ] IR code database integration

---

## Contributors

- **Primary Developer** - Initial release and v2.0 features
- **AI Assistant (Claude)** - Architecture guidance and documentation

---

## License

MIT License - See LICENSE file for details

---

*For detailed feature usage, see [FEATURES.md](FEATURES.md)*
*For development setup, see [VSCODE_GUIDE.md](VSCODE_GUIDE.md)*
