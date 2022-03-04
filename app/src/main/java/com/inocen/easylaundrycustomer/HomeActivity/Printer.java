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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inocen.easylaundrycustomer.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Printer {
    Activity homeActivity;
    NotaPostTransaksi nota;
    String idNota;
    String hp;
    String nama;
    private DateTimeFormatter dateTimeFormatter;
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

    DateTime temp;
    String layanan, jenis, info, berat, sekarang, outlet, jumlah;

    public void masukanData(Activity homeActivity, NotaPostTransaksi notaPostTransaksi, String idNota, String hp) {
        this.homeActivity = homeActivity;
        this.nota = notaPostTransaksi;
        this.idNota = idNota;
        this.hp = hp;
        dateTimeFormatter = DateTimeFormat.forPattern("E, dd/MM HH:mm");
    }
    public void masukanData(Activity homeActivity, NotaPostTransaksi notaPostTransaksi, String idNota, String hp,String nama) {
        this.homeActivity = homeActivity;
        this.nota = notaPostTransaksi;
        this.idNota = idNota;
        this.hp = hp;
        this.nama=nama;
        dateTimeFormatter = DateTimeFormat.forPattern("E, dd/MM HH:mm");
    }

    // this will find a bluetooth printer device
    boolean findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(homeActivity, "Tidak ada adapter bluetooth", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                homeActivity.startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
//                    Toast.makeText(homeActivity, "Sukses terhubung dengan model "+device.getName(), Toast.LENGTH_SHORT).show();
                    mmDevice = device;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
    void sendData(int i, ProgressBar progressBar) throws IOException {
        try {
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(msg, BarcodeFormat.QR_CODE,250,250);
//            mmOutputStream.write(data);
            print_qr_code(idNota, i, progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print_qr_code(String qrdata, int i, ProgressBar progressBar) {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);

        // QR Code: Select the model
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x41, (byte) 0x32, (byte) 0x00};

        //TODO yang ini bisa ngeprint , n adalah ukuran qr code(3-6?)
        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
        byte[] sizeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x43, (byte) 0x06};


        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x45, (byte) 0x31};


        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, store_pL, store_pH, (byte) 0x31, (byte) 0x50, (byte) 0x30};


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144                              //48 ngeprint text qr code
        byte[] printQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x51, (byte) 0x30};

        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=16
        byte[] reverseFeed = {(byte) 0x1b, (byte) 0x4b, (byte) 0x05};

        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=57
        byte[] setPrintPosition = {(byte) 0x1b, (byte) 0x5c, (byte) 0x70, (byte) 0x00};

        //TODO page mode gk bisa, trus gimana biar bisa samping?
        byte[] initializePrinter = {(byte) 0x1b, (byte) 0x40};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=193#esc_cl
        byte[] pageModePrint = {(byte) 0x1b, (byte) 0x4c};
//        https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=56
        byte[] pageModeArea = {(byte) 0x1b, (byte) 0x57, (byte) 0x50, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x30, (byte) 0x00};
        byte[] pageModeAreaQR = {(byte) 0x1b, (byte) 0x57, (byte) 0x70, (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01};
//        https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=55
        byte[] pagePrintDirection = {(byte) 0x1b, (byte) 0x54, (byte) 0x00};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=10
        byte[] printLineFeed = {(byte) 0x0a};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=14
        byte[] printPageMode = {(byte) 0x1b, (byte) 0x0c};
        //https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=58
        byte[] justificationLeft = {(byte) 0x1b, (byte) 0x61, (byte) 0x00};
        byte[] justificationCenter = {(byte) 0x1b, (byte) 0x61, (byte) 0x01};

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
            mmOutputStream.write(justificationCenter);
            mmOutputStream.write(modelQR);
            mmOutputStream.write(sizeQR);
            mmOutputStream.write(errorQR);
            mmOutputStream.write(storeQR);
            mmOutputStream.write(qrdata.getBytes());
            mmOutputStream.write(printQR);
            qrdata += "\n";
            mmOutputStream.write(qrdata.getBytes());

            String garis = "\n-------------------------------";
            mmOutputStream.write(justificationLeft);
            switch (i) {
                case 1:
                    outlet = String.format("\n%-9s", "Outlet ") + ":" + String.format("%20s", HomeActivity.currentUser.namaOutlet);
                    mmOutputStream.write(outlet.getBytes());
                    temp = new DateTime();
                    layanan = nota.detailKiloan.get("layanan");
                    if (layanan.equals("express")) temp=temp.plusHours(6);
                    else temp=temp.plusDays(1);
                    sekarang = String.format("\n%-9s", "Selesai ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                    mmOutputStream.write(sekarang.getBytes());
                    mmOutputStream.write(garis.getBytes());
                    printKiloan();
                    break;
                case 2:
                    outlet = String.format("\n%-9s", "Outlet ") + ":" + String.format("%20s", HomeActivity.currentUser.namaOutlet);
                    mmOutputStream.write(outlet.getBytes());
                    temp = new DateTime();
                    temp=temp.plusDays(1);
                    sekarang = String.format("\n%-9s", "Selesai ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                    mmOutputStream.write(sekarang.getBytes());
                    mmOutputStream.write(garis.getBytes());
                    printSatuan();
                    break;
                case 3:
                    outlet = String.format("\n%-9s", "Outlet ") + ":" + String.format("%20s", HomeActivity.currentUser.namaOutlet);
                    mmOutputStream.write(outlet.getBytes());
                    temp = new DateTime();
                    temp=temp.plusDays(3);
                    sekarang = String.format("\n%-9s", "Selesai ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                    mmOutputStream.write(sekarang.getBytes());
                    mmOutputStream.write(garis.getBytes());
                    printMeteran();
                    break;
                case 4:
                    String customer = String.format("\n%-9s", "Customer ") + ":" + String.format("%20s", nama);
                    mmOutputStream.write(customer.getBytes());
                    String noHP = String.format("\n%-9s", "Nomor hp") + ":" + String.format("%20s", hp);
                    mmOutputStream.write(noHP.getBytes());
                    outlet = String.format("\n%-9s", "Outlet ") + ":" + String.format("%20s", HomeActivity.currentUser.namaOutlet);
                    mmOutputStream.write(outlet.getBytes());
                    DateTime temp = new DateTime();
                    String sekarang = String.format("\n%-9s", "Terima ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                    mmOutputStream.write(sekarang.getBytes());
                    mmOutputStream.write(garis.getBytes());
                    if (nota.detailKiloan != null) {
                        printKiloan();
                        layanan = nota.detailKiloan.get("layanan");
                        if (layanan.equals("express")) temp=temp.plusHours(6);
                        else temp=temp.plusDays(1);
                        sekarang = String.format("\n%-9s", "Selesai ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                        mmOutputStream.write(sekarang.getBytes());
                        mmOutputStream.write(garis.getBytes());
                    }
                    if (nota.detailSatuan != null) {
                        printSatuan();
                        temp = new DateTime();
                        temp=temp.plusDays(1);
                        sekarang = String.format("\n%-9s", "Selesai ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                        mmOutputStream.write(sekarang.getBytes());
                        mmOutputStream.write(garis.getBytes());
                    }
                    if (nota.detailMeteran!= null) {
                        printMeteran();
                        temp = new DateTime();
                        temp=temp.plusDays(3);
                        sekarang = String.format("\n%-9s", "Selesai ") + ":" + String.format("%20s", temp.toString(dateTimeFormatter));
                        mmOutputStream.write(sekarang.getBytes());
                        mmOutputStream.write(garis.getBytes());
                    }
                    String biaya = String.format("\n%-9s", "Biaya ") + ":" + String.format("%20s", "Rp "+nota.total);
                    mmOutputStream.write(biaya.getBytes());
                    String pembayaran = String.format("\n%-9s", "Bayar ") + ":" + String.format("%20s",  "Rp "+nota.pembayaran);
                    mmOutputStream.write(pembayaran.getBytes());
//                    String kembalian = String.format("\n%-9s", "Kembali ") + ":" + String.format("%20s",  "Rp "+nota.kembalian);
//                    mmOutputStream.write(kembalian.getBytes());
                    break;
            }
            String garisAkhir = "\n-------------------------------\n";
            mmOutputStream.write(garisAkhir.getBytes());
            if(i==4){
                mmOutputStream.write(justificationCenter);
                String peringatan="Jangan sampai hilang!!\n Nota ini diperlukan\nuntuk mengambil laundry\n";
                mmOutputStream.write(peringatan.getBytes());
            }
            mmOutputStream.write(printLineFeed);
            mmOutputStream.write(printLineFeed);
            progressBar.setVisibility(View.INVISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printKiloan() {
        layanan = nota.detailKiloan.get("layanan");
        jenis = nota.detailKiloan.get("jenis");
        berat = nota.detailKiloan.get("berat");
        jumlah = nota.detailKiloan.get("jumlah");
        info = "\n" + jenis + " " + layanan + " " + berat + " " + jumlah + " pcs";
        try {
            mmOutputStream.write(info.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printSatuan() {
        info = "\n" + nota.detailSatuan.jenis+"\n";
        try {
            mmOutputStream.write(info.getBytes());
            String itemSatuan = nota.detailSatuan.item.toString();
            itemSatuan = itemSatuan.replace("{", " ");
            itemSatuan = itemSatuan.replace("}", "");
            itemSatuan = itemSatuan.replaceAll(",", "\n");
            itemSatuan = itemSatuan.replaceAll("=", " : ");
            mmOutputStream.write(itemSatuan.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMeteran() {
        info = "\n" + nota.detailMeteran.jenis+"\n";
        try {
            mmOutputStream.write(info.getBytes());
            String itemMeteran = nota.detailMeteran.item.toString();
            itemMeteran = itemMeteran.replace("{", " ");
            itemMeteran = itemMeteran.replace("}", "");
            itemMeteran = itemMeteran.replaceAll(",", "\n");
            itemMeteran = itemMeteran.replaceAll("=", ": ");
            mmOutputStream.write(itemMeteran.getBytes());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
