package com.example.remotecu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivityNew extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener {

    private SharedPrefManager sharedPrefManager;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private LinearLayout emptyState;
    private FloatingActionButton fab;

    private List<RemoteProfile> profiles;
    private String selectedIcon = "📺"; // Default icon

    // Bluetooth BLE fields
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private final List<BluetoothDevice> deviceList = new ArrayList<>();
    private final Map<String, BluetoothDevice> deviceMap = new HashMap<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long SCAN_PERIOD = 5000; // 5 seconds
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private AlertDialog scanningDialog;
    private final UUID CLIENT_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new SharedPrefManager(this);

        // Initialize views
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        fab = findViewById(R.id.fab);

        // Setup RecyclerView
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadProfiles();

        // FAB click listener
        fab.setOnClickListener(v -> showCreateProfileDialog());

        // Initialize Bluetooth
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        // Scan button to trigger BLE scan
        findViewById(R.id.scanButton).setOnClickListener(v -> {
            if (!hasPermissions()) {
                requestPermissions();
            } else {
                startScan();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles(); // Refresh list when returning from detail activity
    }

    private void loadProfiles() {
        profiles = sharedPrefManager.getAllProfiles();

        if (profiles.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            profilesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            profilesRecyclerView.setVisibility(View.VISIBLE);

            if (profileAdapter == null) {
                profileAdapter = new ProfileAdapter(profiles, this);
                profileAdapter.setActiveProfileId(sharedPrefManager.getActiveProfileId());
                profilesRecyclerView.setAdapter(profileAdapter);
            } else {
                profileAdapter.updateProfiles(profiles);
                profileAdapter.setActiveProfileId(sharedPrefManager.getActiveProfileId());
            }
        }
    }

    private void showCreateProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_profile, null);
        TextInputEditText profileNameInput = dialogView.findViewById(R.id.profileNameInput);
        TextView selectedIconPreview = dialogView.findViewById(R.id.selectedIconPreview);

        // Reset selected icon
        selectedIcon = "📺";
        selectedIconPreview.setText(selectedIcon);

        // Setup icon click listeners
        int[] iconIds = {
                R.id.icon_tv, R.id.icon_ac, R.id.icon_heater, R.id.icon_speaker,
                R.id.icon_light, R.id.icon_gaming, R.id.icon_antenna,
                R.id.icon_media, R.id.icon_door, R.id.icon_other
        };

        for (int iconId : iconIds) {
            TextView iconView = dialogView.findViewById(iconId);
            iconView.setOnClickListener(v -> {
                selectedIcon = (String) v.getTag();
                selectedIconPreview.setText(selectedIcon);
                // Visual feedback
                v.setAlpha(0.5f);
                v.postDelayed(() -> v.setAlpha(1.0f), 200);
            });
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancelProfile).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String name = profileNameInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a profile name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new profile
            RemoteProfile newProfile = new RemoteProfile(name, selectedIcon);
            sharedPrefManager.saveProfile(newProfile);

            // Set as active if it's the only profile
            if (profiles.isEmpty()) {
                sharedPrefManager.setActiveProfile(newProfile.getId());
            }

            // Refresh list
            loadProfiles();

            dialog.dismiss();
            Toast.makeText(this, "Remote created: " + name, Toast.LENGTH_SHORT).show();

            // Open the new profile immediately
            openProfileDetail(newProfile);
        });

        dialog.show();
    }

    @Override
    public void onProfileClick(RemoteProfile profile) {
        // Set as active profile
        sharedPrefManager.setActiveProfile(profile.getId());

        // Open detail activity
        openProfileDetail(profile);
    }

    @Override
    public void onProfileMenuClick(RemoteProfile profile, View view) {
        // Show context menu for edit/delete
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(profile.getIcon() + " " + profile.getName())
                .setItems(new String[]{"Edit Layout", "Delete"}, (dialogInterface, i) -> {
                    if (i == 0) {
                        // Edit - Open edit screen
                        Intent intent = new Intent(this, RemoteEditActivity.class);
                        intent.putExtra("PROFILE_ID", profile.getId());
                        startActivity(intent);
                    } else {
                        // Delete
                        showDeleteConfirmation(profile);
                    }
                })
                .create();
        dialog.show();
    }

    private void showDeleteConfirmation(RemoteProfile profile) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Remote?")
                .setMessage("Are you sure you want to delete \"" + profile.getName() + "\"? This will remove all its buttons.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    sharedPrefManager.deleteProfile(profile.getId());
                    loadProfiles();
                    Toast.makeText(this, "Remote deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openProfileDetail(RemoteProfile profile) {
        Intent intent = new Intent(this, RemoteDetailActivity.class);
        intent.putExtra(RemoteDetailActivity.EXTRA_PROFILE_ID, profile.getId());
        startActivity(intent);
    }

    // ========== Bluetooth BLE Methods ==========

    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(this, "Permissions denied. Bluetooth cannot function.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startScan() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            showBluetoothDialog();
            return;
        }

        // Clear previous device list
        deviceList.clear();
        deviceMap.clear();

        // Show scanning dialog
        showScanningDialog();

        // Start scanning
        bluetoothLeScanner.startScan(scanCallback);

        // Stop scanning after SCAN_PERIOD
        handler.postDelayed(() -> {
            bluetoothLeScanner.stopScan(scanCallback);
            dismissScanningDialog();
            showDeviceSelectionDialog();
        }, SCAN_PERIOD);
    }

    private void showScanningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scanning...");
        builder.setMessage("Scanning for BLE devices. Please wait.");

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        builder.setView(progressBar);
        builder.setCancelable(false);

        scanningDialog = builder.create();
        scanningDialog.show();
    }

    private void dismissScanningDialog() {
        if (scanningDialog != null && scanningDialog.isShowing()) {
            scanningDialog.dismiss();
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device != null && !deviceMap.containsKey(device.getAddress())) {
                deviceList.add(device);
                deviceMap.put(device.getAddress(), device);
                Log.i("BLE", "Device found: " + (device.getName() != null ? device.getName() : "Unknown") + " - " + device.getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("BLE", "Scan failed with error: " + errorCode);
            Toast.makeText(MainActivityNew.this, "Scan failed", Toast.LENGTH_SHORT).show();
        }
    };

    private void showBluetoothDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Bluetooth")
                .setMessage("Bluetooth is required to scan for BLE devices. Would you like to enable it?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                    Toast.makeText(MainActivityNew.this, "BLE scanning requires Bluetooth.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @SuppressLint("MissingPermission")
    private void showDeviceSelectionDialog() {
        if (deviceList.isEmpty()) {
            Toast.makeText(this, "No BLE devices found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] deviceNames = new String[deviceList.size()];
        for (int i = 0; i < deviceList.size(); i++) {
            deviceNames[i] = deviceList.get(i).getName() != null ? deviceList.get(i).getName() : "Unknown Device";
        }

        new AlertDialog.Builder(this)
                .setTitle("Select a BLE Device")
                .setItems(deviceNames, (dialog, which) -> {
                    BluetoothDevice selectedDevice = deviceList.get(which);
                    connectToDevice(selectedDevice);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .show();
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        Toast.makeText(this, "Connecting to " + (device.getName() != null ? device.getName() : "device") + "...", Toast.LENGTH_SHORT).show();
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BLE", "Connected to GATT server");
                runOnUiThread(() -> Toast.makeText(MainActivityNew.this, "Connected to device", Toast.LENGTH_SHORT).show());
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BLE", "Disconnected from GATT server");
                runOnUiThread(() -> Toast.makeText(MainActivityNew.this, "Disconnected from device", Toast.LENGTH_SHORT).show());
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BLE", "Services discovered");

                // Look for the specific service UUID (from ESP32 remote)
                BluetoothGattService service = gatt.getService(UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214"));
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("19b10001-e8f2-537e-4f6c-d104768a1214"));
                    if (characteristic != null) {
                        enableIndications(characteristic);
                    }
                }
            }
        }

        @SuppressLint("MissingPermission")
        private void enableIndications(BluetoothGattCharacteristic characteristic) {
            bluetoothGatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CONFIG_DESCRIPTOR_UUID);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            } else {
                Log.e("BLE", "Descriptor not found for characteristic: " + characteristic.getUuid());
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                String value = new String(data);
                Log.i("BLE", "Characteristic Read: " + value);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BLE", "Characteristic Write Success");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            String value = new String(data);
            Log.i("BLE", "Characteristic Changed: " + value);
            // Handle received IR codes from ESP32
            runOnUiThread(() -> Toast.makeText(MainActivityNew.this, "Received: " + value, Toast.LENGTH_SHORT).show());
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }
}
