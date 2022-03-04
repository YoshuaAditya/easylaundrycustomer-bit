package com.inocen.easylaundrycustomer.HomeActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.inocen.easylaundrycustomer.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PrinterTestActivity extends AppCompatActivity {
    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;


    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        // we are going to have three buttons for specific functions
        Button openButton = findViewById(R.id.open);
        Button sendButton = findViewById(R.id.send);
        Button closeButton = findViewById(R.id.close);

// text label and input box
        myLabel = findViewById(R.id.label);
        myTextbox = findViewById(R.id.entry);

        // open bluetooth connection
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findBT();
                openBT();
            }
        });

        // send data typed by the user to be printed
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // close bluetooth connection
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("F",String.format("%-9s","Nota ")+":"+String.format("%20s","BPN_01_190502000014"));
                Log.e("F",String.format("%-9s","Customer ")+":"+String.format("%20s","BPN_01_190502000014"));
                Log.e("F",String.format("%-9s","Customer ")+":"+String.format("%20s","BPN_01_190502000014"));
                Log.e("F","-------------------------------");
//                try {
//                    closeBT();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
            }
        });
    }

    // this will find a bluetooth printer device
    void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Toast.makeText(PrinterTestActivity.this, "Sukses terhubung dengan model "+device.getName(), Toast.LENGTH_SHORT).show();
                    mmDevice = device;
                    break;
                }
            }

            myLabel.setText("Bluetooth device found.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            sendData();

            myLabel.setText("Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
            // the text typed by the user
            String msg = myTextbox.getText().toString();
            msg += "\n";

//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(msg, BarcodeFormat.QR_CODE,250,250);
//            mmOutputStream.write(data);
            print_qr_code(msg);

            // tell the user data were sent
            myLabel.setText("Data sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print_qr_code(String qrdata)
    {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);


        // QR Code: Select the model
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x03};


        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};


        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144                              //48 ngeprint text qr code
        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};

        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=16
        byte[] reverseFeed = {(byte)0x1b, (byte)0x4b, (byte)0x05};

        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=57
        byte[] setPrintPosition= {(byte)0x1b, (byte)0x5c, (byte)0x70, (byte)0x00};

        byte[] initializePrinter= {(byte)0x1b, (byte)0x40};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=193#esc_cl
        byte[] pageModePrint= {(byte)0x1b, (byte)0x4c};
//        https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=56
        byte[] pageModeArea= {(byte)0x1b, (byte)0x57, (byte)0x50, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x50, (byte)0x00, (byte)0x30, (byte)0x00};
        byte[] pageModeAreaQR= {(byte)0x1b, (byte)0x57, (byte)0x70, (byte)0x00, (byte)0x0, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x01};
//        https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=55
        byte[] pagePrintDirection= {(byte)0x1b, (byte)0x54, (byte)0x00};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=10
        byte[] printLineFeed= {(byte)0x0a};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=14
        byte[] printPageMode= {(byte)0x1b, (byte)0x0c};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=12
        byte[] printPageModeToStandard= {(byte)0x0c};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=58
        byte[] justificationLeft= {(byte)0x1b, (byte)0x61, (byte)0x00};
        byte[] justificationCenter= {(byte)0x1b, (byte)0x61, (byte)0x01};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=68
        byte[] partialCut1= {(byte)0x1b, (byte)0x69};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=69
        byte[] partialCut3= {(byte)0x1b, (byte)0x6d};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=87
        byte[] cut= {(byte)0x1d, (byte)0x56,(byte)0x00};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=66
        byte[] esc_home= {(byte)0x1b, (byte)0x3c};



        // flush() runs the print job and clears out the print buffer
        try {
            mmOutputStream.write(initializePrinter);
//            mmOutputStream.write(pageModePrint);
//            // write() simply appends the data to the
//
//            //Print text sebelah QR
//            mmOutputStream.write(pageModeArea);
//            mmOutputStream.write(pagePrintDirection);
//            mmOutputStream.write(qrdata.getBytes());
//            mmOutputStream.write(printLineFeed);
//            mmOutputStream.write(qrdata.getBytes());
//            mmOutputStream.write(printLineFeed);

            //Print QR
//            mmOutputStream.write(pageModeAreaQR);
//            mmOutputStream.write(justificationCenter);
//            mmOutputStream.write(modelQR);
//            mmOutputStream.write(sizeQR);
//            mmOutputStream.write(errorQR);
//            mmOutputStream.write(storeQR);
            mmOutputStream.write(qrdata.getBytes());
//            mmOutputStream.write(printQR);
//            mmOutputStream.write(qrdata.getBytes());

            mmOutputStream.write(cut);
//            mmOutputStream.write(printPageMode);
//            mmOutputStream.write(reverseFeed);
//            mmOutputStream.write(setPrintPosition);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
