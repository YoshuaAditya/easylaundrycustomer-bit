package com.inocen.easylaundrycustomer.HomeActivity;

import java.util.Map;

public class NotaPostSatuan {
    String token;
    String kodeOutlet;
    String namaOutlet;
    String namaPelanggan;
    String noHp;
    String jenis;
    Map<String, String>item;
    String biaya;
    String status;
    String currentProgress;
    String tanggalMasuk;
    String tanggalSelesai;


    public NotaPostSatuan(String token, String kodeOutlet, String namaOutlet, String namaPelanggan, String noHp, String jenis, Map<String, String> item, String biaya, String status, String currentProgress, String tanggalMasuk, String tanggalSelesai) {
        this.token = token;
        this.kodeOutlet = kodeOutlet;
        this.namaOutlet = namaOutlet;
        this.namaPelanggan = namaPelanggan;
        this.noHp = noHp;
        this.jenis = jenis;
        this.item = item;
        this.biaya = biaya;
        this.status = status;
        this.currentProgress = currentProgress;
        this.tanggalMasuk = tanggalMasuk;
        this.tanggalSelesai = tanggalSelesai;
    }


}
