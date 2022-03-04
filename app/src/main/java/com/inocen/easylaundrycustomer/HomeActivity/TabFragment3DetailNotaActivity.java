package com.inocen.easylaundrycustomer.HomeActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.inocen.easylaundrycustomer.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TabFragment3DetailNotaActivity extends AppCompatActivity {

    static NotaGet notaGet;
    private WebView mMainWebview;
    private WebView print_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainWebview = new WebView(TabFragment3DetailNotaActivity.this);
        setContentView(R.layout.tab_fragment_detail_nota);

        ImageView imageViewCard;
        TextView textViewNamaOutlet;
        TextView textViewAlamatOutlet;
        TextView textViewJenis;
        TextView textViewNoNota;
        TextView textViewNamaPelanggan;
        TextView textViewTglMasuk;
        TextView textViewTglSelesai;
        TextView textViewTotalTagihan;
        TextView textViewStatus;
        TextView textViewTipePengerjaan;
        TextView textViewTahap;

        imageViewCard=findViewById(R.id.imageViewBarcode);
        textViewNoNota = findViewById(R.id.textViewNoNota);
        textViewNamaOutlet= findViewById(R.id.textViewNamaOutlet);
        textViewJenis= findViewById(R.id.textViewType);
        textViewNamaPelanggan = findViewById(R.id.textViewNamaPelanggan);
        textViewTglMasuk = findViewById(R.id.textViewTglMasuk);
        textViewTglSelesai = findViewById(R.id.textViewTglSelesai);
        textViewTotalTagihan = findViewById(R.id.textViewTotalTagihan);
        textViewStatus = findViewById(R.id.textViewStatus);

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;
        try {
            bitmap = barcodeEncoder.encodeBitmap(notaGet.noNota, BarcodeFormat.QR_CODE, 110, 110);
            imageViewCard.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        textViewNoNota.setText(notaGet.noNota);
        textViewNamaOutlet.setText(notaGet.namaOutlet);
        textViewJenis.setText(notaGet.jenis);
        textViewNamaPelanggan.setText(notaGet.namaPelanggan);
        textViewTglMasuk.setText(notaGet.tglMasuk);
        textViewTglSelesai.setText(notaGet.tglSelesai);
        textViewTotalTagihan.setText(notaGet.biaya);
//        textViewStatus.setText(notaGet.status);
        textViewStatus.setText(notaGet.progress);
//        textViewTahap.setText(notaGet.tahap);

        Button button=findViewById(R.id.buttonKembali);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
