package com.example.remotecu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "com.example.serialreader.USB_PERMISSION";
    private UsbManager usbManager;
    private TextView serialOutput;
    private UsbSerialPort usbSerialPort;
    private PendingIntent permissionIntent;

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

        serialOutput = findViewById(R.id.serial_output);
        Button selectDeviceButton = findViewById(R.id.select_device_button);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

        // Register USB device attached receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter);

        // Button to manually show device selection
        selectDeviceButton.setOnClickListener(v -> showDeviceSelectionDialog());
    }

    private void showDeviceSelectionDialog() {
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

        if (availableDrivers.isEmpty()) {
            serialOutput.setText("No USB devices found.");
            return;
        }

        // Create a list of device names
        String[] deviceNames = new String[availableDrivers.size()];
        for (int i = 0; i < availableDrivers.size(); i++) {
            UsbSerialDriver driver = availableDrivers.get(i);
            deviceNames[i] = driver.getDevice().getDeviceName();  // You can customize this
        }

        // Show the selection dialog
        new AlertDialog.Builder(this)
                .setTitle("Select Serial Device")
                .setItems(deviceNames, (dialog, which) -> {
                    UsbSerialDriver selectedDriver = availableDrivers.get(which);
                    UsbDevice device = selectedDriver.getDevice();
                    requestPermission(device, selectedDriver);
                })
                .show();
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