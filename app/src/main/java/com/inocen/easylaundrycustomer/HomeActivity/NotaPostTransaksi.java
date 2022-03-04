package com.inocen.easylaundrycustomer.HomeActivity;

import java.io.Serializable;
import java.util.Map;

public class NotaPostTransaksi implements Serializable {
    String token;
    Map<String,String>detailKiloan;
    DetailTransaksi detailSatuan;
    DetailTransaksi detailMeteran;
    String total;
    String pembayaran;
    String kembalian;
    String status;
    String namaOutlet;


    public NotaPostTransaksi(String token, Map<String, String> detailKiloan, DetailTransaksi detailSatuan, DetailTransaksi detailMeteran, String total, String pembayaran, String kembalian, String status, String namaOutlet) {
        this.token = token;
        this.detailKiloan = detailKiloan;
        this.detailSatuan = detailSatuan;
        this.detailMeteran = detailMeteran;
        this.total = total;
        this.pembayaran = pembayaran;
        this.kembalian = kembalian;
        this.status = status;
        this.namaOutlet = namaOutlet;
    }



    public static class DetailTransaksi{
        String jenis;
        Map<String,String>item;
        String biaya;

        @Override
        public String toString() {
            String print="\nJenis:"+jenis+"\nBiaya:"+biaya+"\n"+item.toString();
            return print;
        }

        public DetailTransaksi(String jenis, Map<String, String> item, String biaya) {
            this.jenis = jenis;
            this.item = item;
            this.biaya = biaya;


        }
    }
}
