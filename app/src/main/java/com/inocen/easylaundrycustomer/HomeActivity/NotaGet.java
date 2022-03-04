package com.inocen.easylaundrycustomer.HomeActivity;

public class NotaGet implements Comparable<NotaGet> {

    String noNota;
    String namaPelanggan;
    String namaOutlet;
    String jenis;
    String berat;
    String tglMasuk;
    String tglSelesai;
    String biaya;
    String progress;
    String terakhirUpdate;
    String urlNotaPrint;

    public NotaGet(String noNota, String namaPelanggan, String namaOutlet, String jenis, String berat, String tglMasuk, String tglSelesai, String biaya, String progress, String terakhirUpdate, String urlNotaPrint) {
        this.noNota = noNota;
        this.namaPelanggan = namaPelanggan;
        this.namaOutlet = namaOutlet;
        this.jenis = jenis;
        this.berat = berat;
        this.tglMasuk = tglMasuk;
        this.tglSelesai = tglSelesai;
        this.biaya = biaya;
        this.progress = progress;
        this.terakhirUpdate = terakhirUpdate;
        this.urlNotaPrint = urlNotaPrint;
    }

    @Override
    public int compareTo(NotaGet o) {
        return this.noNota.compareTo(o.noNota);
    }
}
