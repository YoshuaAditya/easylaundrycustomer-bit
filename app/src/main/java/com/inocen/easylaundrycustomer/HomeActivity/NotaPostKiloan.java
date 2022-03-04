package com.inocen.easylaundrycustomer.HomeActivity;

import java.util.Map;

public class NotaPostKiloan {
    String token;
    String kodeOutlet;
    String namaOutlet;
    String namaPelanggan;
    String noHp;
    String jenis;
    String layanan;
    String berat;
    String jumlah;
    String biaya;
    String status;
    String currentProgress;
    String tanggalMasuk;
    String tanggalSelesai;


    public NotaPostKiloan(String token, String kodeOutlet, String namaOutlet, String namaPelanggan, String noHp, String jenis, String layanan, String berat, String jumlah, String biaya, String status, String currentProgress, String tanggalMasuk, String tanggalSelesai) {
        this.token = token;
        this.kodeOutlet = kodeOutlet;
        this.namaOutlet = namaOutlet;
        this.namaPelanggan = namaPelanggan;
        this.noHp = noHp;
        this.jenis = jenis;
        this.layanan = layanan;
        this.berat = berat;
        this.jumlah = jumlah;
        this.biaya = biaya;
        this.status = status;
        this.currentProgress = currentProgress;
        this.tanggalMasuk = tanggalMasuk;
        this.tanggalSelesai = tanggalSelesai;
    }


}
