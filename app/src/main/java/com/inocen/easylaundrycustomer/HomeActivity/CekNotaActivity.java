package com.inocen.easylaundrycustomer.HomeActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inocen.easylaundrycustomer.R;
import com.inocen.easylaundrycustomer.SharedPrefHelper;
import com.inocen.easylaundrycustomer.ViewPagerActivity.ViewPagerActivity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class CekNotaActivity extends AppCompatActivity {

    String url = "https://easylaundry.inocen.com/detail-nota/";
    Button buttonKembali, buttonKiloan, buttonSatuan, buttonMeteran, buttonCustomer;
    boolean printKiloan = false, printSatuan = false, printMeteran = false, printCustomer = false, printWhatsapp=false;
    Printer printer = new Printer();
    boolean notConnected = true;
    static NotaPostTransaksi transaksi;
    String idPertama, idKiloan = "", idSatuan = "", idMeteran = "", hp = "", nama = "";
    boolean sudahPrint = false;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fragment_cek_nota);

        Bundle bundle = getIntent().getExtras().getBundle("bundle");
        idPertama = bundle.getString("id");
        idKiloan = bundle.getString("kiloan");
        idSatuan = bundle.getString("satuan");
        idMeteran = bundle.getString("meteran");
        hp = bundle.getString("hp");
        nama = bundle.getString("nama");

        progressBar = findViewById(R.id.loading_circle_bar_print);
        setButtons();
        checkStatus();
    }

    private void checkStatus() {
        if (transaksi.detailKiloan == null) {
            buttonKiloan.setBackground(getDrawable(R.drawable.custom_button_disabled));
            buttonKiloan.setEnabled(false);
            printKiloan = true;
        }
        if (transaksi.detailSatuan == null) {
            buttonSatuan.setBackground(getDrawable(R.drawable.custom_button_disabled));
            buttonSatuan.setEnabled(false);
            printSatuan = true;
        }
        if (transaksi.detailMeteran == null) {
            buttonMeteran.setBackground(getDrawable(R.drawable.custom_button_disabled));
            buttonMeteran.setEnabled(false);
            printMeteran = true;
        }
    }

    private void setButtons() {
        buttonKiloan = findViewById(R.id.buttonPrintKiloan);
        buttonKiloan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cekKoneksiPrinter()) {
                    printer.masukanData(CekNotaActivity.this, transaksi, idKiloan, hp);
                    if (notConnected) {
                        printer.openBT();
                        notConnected = false;
                    }
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        printer.sendData(1, progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printKiloan = true;
                    enableButtonKembali();
                }
            }
        });
        buttonSatuan = findViewById(R.id.buttonPrintSatuan);
        buttonSatuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cekKoneksiPrinter()) {
                    printer.masukanData(CekNotaActivity.this, transaksi, idSatuan, hp);
                    if (notConnected) {
                        printer.openBT();
                        notConnected = false;
                    }
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        printer.sendData(2, progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printSatuan = true;
                    enableButtonKembali();
                }
            }
        });
        buttonMeteran = findViewById(R.id.buttonPrintMeteran);
        buttonMeteran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cekKoneksiPrinter()) {
                    printer.masukanData(CekNotaActivity.this, transaksi, idMeteran, hp);
                    if (notConnected) {
                        printer.openBT();
                        notConnected = false;
                    }
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        printer.sendData(3, progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printMeteran = true;
                    enableButtonKembali();
                }
            }
        });
        buttonCustomer = findViewById(R.id.buttonPrintCustomer);
        buttonCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cekKoneksiPrinter()) {
                    printer.masukanData(CekNotaActivity.this, transaksi, idPertama, hp, nama);
                    if (notConnected) {
                        printer.openBT();
                        notConnected = false;
                    }
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        printer.sendData(4, progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printCustomer = true;
                    enableButtonKembali();
                    sendWhatsappMessage();
                }
            }
        });

        buttonKembali = findViewById(R.id.buttonKembali);
        buttonKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sudahPrint) {
                    try {
                        printer.closeBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CekNotaActivity.this);
                    builder.setTitle("Nota/Whatsapp belum diprint semua, yakin mau keluar?");

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                printer.closeBT();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }
        });
//        CircleImageView circleImageView = findViewById(R.id.circleImageWhatsapp);
//        circleImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendWhatsappMessage();
//            }
//        });
    }

    private void enableButtonKembali() {
        if (printKiloan && printSatuan && printMeteran && printCustomer) {
//            buttonKembali.setBackground(getDrawable(R.drawable.custom_button));
            sudahPrint = true;
        }
    }

    private boolean cekKoneksiPrinter() {
        if (!printer.findBT()) {
            Toast.makeText(this, "Bluetooth belum nyala, atau tidak ada printer", Toast.LENGTH_SHORT).show();
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
            return false;
        }
        return true;
    }

    //081335678358
    private void sendWhatsappMessage() {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String phone = hp;
            String message = "Nota digital customer: " + idPertama + "\n"+url+idPertama;
            phone = phone.replaceFirst("0", "62");
            String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else
                Toast.makeText(CekNotaActivity.this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            enableButtonKembali();
        } catch (Exception e) {
            Toast.makeText(CekNotaActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

    }

}
