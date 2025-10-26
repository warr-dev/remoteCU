package com.example.remotecu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import android.hardware.ConsumerIrManager;
import android.widget.EditText;


import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "com.example.serialreader.USB_PERMISSION";
    private UsbManager usbManager;
    private TextView serialOutput;
    private UsbSerialPort usbSerialPort;
    private PendingIntent permissionIntent;

    private ConsumerIrManager irManager;
    private EditText irValueInput;
    private EditText buttonNameInput;
    private GridLayout buttonContainer;
    private SharedPrefManager sharedPrefManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;

    private final List<BluetoothDevice> deviceList = new ArrayList<>();
    private final Map<String, BluetoothDevice> deviceMap = new HashMap<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long SCAN_PERIOD = 5000; // 10 seconds
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private AlertDialog scanningDialog; // Dialog to show scanning progress

    private final UUID CLIENT_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private Context context;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    showDeviceSelectionDialog();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the UI elements
        buttonContainer = findViewById(R.id.gridLayout);
//        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sharedPrefManager = new SharedPrefManager(this);

        irValueInput = findViewById(R.id.irValueInput);
        buttonNameInput = findViewById(R.id.buttonName);
        irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        Button addButton = findViewById(R.id.addButton);

        // Load existing buttons
        loadSavedButtons();

        // Initialize Bluetooth manager and adapter
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

//        if (!hasPermissions()) {
//            requestPermissions();
//        } else {
////            initBluetooth();  // Initialize Bluetooth functionality
//            startScan();
//        }
        // Initialize the scan button and set its click listener
        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the BLE scan when the button is clicked
                if (!hasPermissions()) {
                    requestPermissions();
                } else {
                    startScan();
                }
            }
        });



//        scanLeDevice(true); // Start scanning

//        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

        // Register USB device attached receiver
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        registerReceiver(usbReceiver, filter);

        // Button to manually show device selection
//        selectDeviceButton.setOnClickListener(v -> showDeviceSelectionDialog());


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String irValue = irValueInput.getText().toString().trim();
                String name = buttonNameInput.getText().toString().trim();
                createDynamicButton(name, irValue, false);
            }
        });

    }

    private void showScanningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scanning...");
        builder.setMessage("Scanning for BLE devices. Please wait.");

        // Add an indeterminate ProgressBar to the dialog
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);

        builder.setView(progressBar);

        builder.setCancelable(false); // Prevent the user from closing it
        scanningDialog = builder.create();
        scanningDialog.show();
    }

    private void dismissScanningDialog() {
        if (scanningDialog != null && scanningDialog.isShowing()) {
            scanningDialog.dismiss();
        }
    }

    // Scan callback to handle results
    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!deviceMap.containsKey(device.getAddress())) {
                deviceList.add(device);
                deviceMap.put(device.getAddress(), device);
                Log.d("BLE", "Found device: " + device.getName() + " - " + device.getAddress());
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                if (!deviceMap.containsKey(device.getAddress())) {
                    deviceList.add(device);
                    deviceMap.put(device.getAddress(), device);
                    Log.d("BLE", "Found device: " + device.getName() + " - " + device.getAddress());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(MainActivity.this, "Scan failed with error: " + errorCode, Toast.LENGTH_SHORT).show();
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_BLUETOOTH_PERMISSIONS);
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Initialize Bluetooth if permissions are granted
    private void initBluetooth() {

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Proceed with Bluetooth operations
            scanLeDevice(true);  // Start scanning for BLE devices
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
//                initBluetooth();
                startScan();
            } else {
                // Permission was denied, show a message or disable BLE features
                Toast.makeText(this, "Permissions denied. Bluetooth cannot function.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
//        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
//        scanner.startScan(leScanCallback);
        startScan();
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

        // Clear the previous device list
        deviceList.clear();
        deviceMap.clear();

        // Show the scanning dialog with a progress bar
        showScanningDialog();
        // Start scanning for devices (use a Handler to manage the duration of the scan)
        bluetoothLeScanner.startScan(scanCallback);

        // Stop scanning after a set period (e.g., 10 seconds)
        handler.postDelayed(() -> {
            bluetoothLeScanner.stopScan(scanCallback);
            dismissScanningDialog();
            showDeviceSelectionDialog();
        }, SCAN_PERIOD); // Scan for 10 seconds
    }


//    private final ScanCallback leScanCallback = new ScanCallback() {
//        @SuppressLint("MissingPermission")
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            BluetoothDevice device = result.getDevice();
//            String deviceName = device.getName();
//            String deviceAddress = device.getAddress();
//
//            // When a BLE device is found, stop scanning and connect to the device
//            bluetoothLeScanner.stopScan(this);
//
//            // Connect to the BLE server (device)
//            connectToDevice(device);
//        }
//    };
    private void showBluetoothDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Bluetooth")
                .setMessage("Bluetooth is required to scan for BLE devices. Would you like to enable it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    public void onClick(DialogInterface dialog, int id) {
                        // Open Bluetooth settings
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "BLE scanning requires Bluetooth.", Toast.LENGTH_SHORT).show();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if Bluetooth has been enabled
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.i("BLE", "Bluetooth enabled");
                // Proceed with BLE scanning after enabling Bluetooth
                // Your scanning code here
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }
    // GATT callback to handle connection/disconnection and service discovery
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BLE", "Connected to GATT server");

                // Attempt to discover services after successful connection
                bluetoothGatt.discoverServices();
                Toast.makeText(MainActivity.this, "Connected to GATT server", Toast.LENGTH_SHORT).show();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BLE", "Disconnected from GATT server");
                Toast.makeText(MainActivity.this, "Disconnected from GATT server", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Handle the characteristic change (notification/indication received)
            byte[] data = characteristic.getValue();
            Log.d("Message", "Characteristic changed: " + characteristic.getUuid());
            Log.d("Message", "Data: " + new String(data));  // Or handle the byte array as needed

            // Update the UI with the received data
            String dataString = new String(data);  // Convert byte[] to String
            handler.post(()->irValueInput.setText(dataString));
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BLE", "Services discovered");
                // After services are discovered, interact with the desired characteristics
                BluetoothGattService service = bluetoothGatt.getService(UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214"));
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("19b10001-e8f2-537e-4f6c-d104768a1214"));
                    if (characteristic != null) {
                        // Read the characteristic or write to it
                        bluetoothGatt.readCharacteristic(characteristic);
                        enableIndications(characteristic);
                        Log.d("Message", "Data: " + new String(characteristic.toString()));  // Or handle the byte array as needed

                    }
                }
            } else {
                Log.w("BLE", "onServicesDiscovered received: " + status);
            }
        }
        // Enable indications on the characteristic
        @SuppressLint("MissingPermission")
        private void enableIndications(BluetoothGattCharacteristic characteristic) {
            bluetoothGatt.setCharacteristicNotification(characteristic, true);

            // Write to the descriptor to enable indications
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CONFIG_DESCRIPTOR_UUID);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);  // Use ENABLE_INDICATION_VALUE for indications
                bluetoothGatt.writeDescriptor(descriptor);
            } else {
                Log.e("Message", "Descriptor not found for characteristic: " + characteristic.getUuid());
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
    };
    private void loadSavedButtons() {
        Map<String, ?> allButtons = sharedPrefManager.getAllButtons();

        for (Map.Entry<String, ?> entry : allButtons.entrySet()) {
            String buttonName = entry.getKey();
            String irCode = (String) entry.getValue();
            createDynamicButton(buttonName, irCode, true    );
        }
    }

    // Method to add a new button dynamically
    private void createDynamicButton(final String buttonName,final String irValue,boolean isloading) {
        if(!isloading){
            if (sharedPrefManager.isButtonNameDuplicate(buttonName)) {
                Toast.makeText(MainActivity.this, "Button with this name already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (irValue.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter a valid IR value", Toast.LENGTH_SHORT).show();
            return;
        }

        //create Button
        Button newButton = new Button(this);
        newButton.setText(buttonName);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendIrSignal(irValue);

                if (!irValue.isEmpty()) {
                    try {
                        sendIrSignal(irValue); // Send the IR signal
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid IR code format", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid IR code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Remove the button from the layout
                ((ViewGroup) v.getParent()).removeView(v);

                // Remove the button from SharedPreferences
                sharedPrefManager.removeButton(buttonName);
                return true;
            }
        });

        // Add button to the layout
        buttonContainer = findViewById(R.id.gridLayout);
        buttonContainer.addView(newButton);

        if(!isloading){
            // Save button and IR code to SharedPreferences
            sharedPrefManager.saveButton(buttonName, irValue);
        }
    }

    // Example: Clear all saved buttons (call this method when needed)
    public void clearAllButtons() {
        sharedPrefManager.clearAll();
    }
// for usb
//    private void showDeviceSelectionDialog() {
//        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
//
//        if (availableDrivers.isEmpty()) {
//            serialOutput.setText("No USB devices found.");
//            return;
//        }
//
//        // Create a list of device names
//        String[] deviceNames = new String[availableDrivers.size()];
//        for (int i = 0; i < availableDrivers.size(); i++) {
//            UsbSerialDriver driver = availableDrivers.get(i);
//            deviceNames[i] = driver.getDevice().getDeviceName();  // You can customize this
//        }
//
//        // Show the selection dialog
//        new AlertDialog.Builder(this)
//                .setTitle("Select Serial Device")
//                .setItems(deviceNames, (dialog, which) -> {
//                    UsbSerialDriver selectedDriver = availableDrivers.get(which);
//                    UsbDevice device = selectedDriver.getDevice();
//                    requestPermission(device, selectedDriver);
//                })
//                .show();
//    }
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

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Select a BLE Device")
            .setItems(deviceNames, (dialog, which) -> {
                BluetoothDevice selectedDevice = deviceList.get(which);
                connectToDevice(selectedDevice);
            })
            .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

    AlertDialog dialog = builder.create();
    dialog.show();
}

    private void requestPermission(UsbDevice device, UsbSerialDriver driver) {
        usbManager.requestPermission(device, permissionIntent);
        setupUsbDevice(driver);  // Setup the device immediately after permission
    }

    private void setupUsbDevice(UsbSerialDriver driver) {
        UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());

        if (connection == null) {
            serialOutput.setText("Error opening device.");
            return;
        }

        usbSerialPort = driver.getPorts().get(0);  // Get the first port
        try {
            usbSerialPort.open(connection);
            usbSerialPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            // Reading Data
            readDataFromSerial();

        } catch (IOException e) {
            serialOutput.setText("Error: " + e.getMessage());
            Log.e("USB", "Error opening device", e);
        }
    }
    // Function to convert hex to binary (LSB first)
    public static String hexToLsbFirst(String hex) {
        // Step 1: Convert hex string to integer
        long hexValue = Long.parseLong(hex, 16);

        // Step 2: Convert integer to binary string
        StringBuilder binaryString = new StringBuilder(Long.toBinaryString(hexValue));

        // Step 3: Pad the binary string to make sure it is 32 bits (for standard IR commands)
        while (binaryString.length() < 32) {
            binaryString.insert(0, "0"); // pad with leading zeros
        }

        // Step 4: Reverse the binary string for LSB first

        return new StringBuilder(binaryString.toString()).reverse().toString();
    }

    private void readDataFromSerial() {
        new Thread(() -> {
            StringBuilder buffer = new StringBuilder(); // Buffer to hold incoming data
            byte[] byteBuffer = new byte[16]; // Buffer for reading bytes
            try {
                while (true) {
                    int numBytesRead = usbSerialPort.read(byteBuffer, 1000);
                    if (numBytesRead > 0) {
                        // Convert bytes to String and append to buffer
                        String receivedData = new String(byteBuffer, 0, numBytesRead);
                        buffer.append(receivedData);

                        // Check for newline character(s)
                        int newLineIndex;
                        while ((newLineIndex = buffer.indexOf("\n")) >= 0) {
                            String line = buffer.substring(0, newLineIndex).trim(); // Extract complete line
                            buffer.delete(0, newLineIndex + 1); // Remove the processed line from buffer
                            serialOutput.setText(line);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("USB", "Error reading from serial port", e);
            }
        }).start();
    }
    public static void logArray(int[] array, String tag) {
        StringBuilder arrayString = new StringBuilder();

        // Convert the array elements to a string format
        for (int value : array) {
            arrayString.append(value).append(", ");
        }

        // Log the array string
        Log.d(tag, "Array: [" + arrayString + "]");
    }
    private void sendIrSignal(String hexCode) {
        if (irManager.hasIrEmitter()) {
            int frequency = 38000; // Standard IR frequency
            int[] pattern = binaryToNecSignal(hexToLsbFirst(hexCode)); // Generate IR pattern from the input code
//
            irManager.transmit(frequency, pattern);
            logArray(binaryToNecSignal(hexToLsbFirst(hexCode)),"test");
            Toast.makeText(this, "IR Signal Sent: " + hexToLsbFirst(hexCode), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No IR emitter found on this device.", Toast.LENGTH_SHORT).show();
        }
    }
    // This method generates the pattern based on the binary representation of the IR command
    public static int[] binaryToNecSignal(String binaryLsbFirst) {
        // NEC protocol: Leading pulse + space
        int[] signal = new int[68]; // 34 bits = 68 elements (pulse + space per bit)
        int index = 0;

        // Start with the leading pulse and space
        signal[index++] = 9000; // Leading pulse
        signal[index++] = 4500; // Leading space

        // Loop through the binary string and convert to NEC signal format
        for (char bit : binaryLsbFirst.toCharArray()) {
            if (bit == '1') {
                // 1: 560 µs pulse + 1690 µs space
                signal[index++] = 560;
                signal[index++] = 1690;
            } else {
                // 0: 560 µs pulse + 560 µs space
                signal[index++] = 560;
                signal[index++] = 560;
            }
        }

        // End with a final pulse of 560 µs
        signal[index] = 560;

        return removeTrailingZeros(signal); // Return the full NEC signal pattern
    }
    public static int[] removeTrailingZeros(int[] array) {
        int lastIndex = -1; // Tracks the last non-zero element's index

        // Loop through the array to find the last non-zero element
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                lastIndex = i;
            }
        }

        // Create a new array without trailing zeros
        int[] trimmedArray = new int[lastIndex + 1];
        System.arraycopy(array, 0, trimmedArray, 0, lastIndex + 1);

        return trimmedArray;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usbSerialPort != null) {
            try {
                usbSerialPort.close();
            } catch (IOException e) {
                Log.e("USB", "Error closing USB port", e);
            }
        }
        unregisterReceiver(usbReceiver);
    }
}