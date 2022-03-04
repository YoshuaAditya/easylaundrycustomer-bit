package com.inocen.easylaundrycustomer.HomeActivity;

import android.os.Bundle;

public class Meteran {
    static Bundle bundle = new Bundle();

    public static void initBundle() {
        bundle.putInt("Karpet tipis", 5000);
        bundle.putInt("Karpet medium", 8000);
        bundle.putInt("Karpet tebal", 10000);
        bundle.putInt("Karpet super", 15000);
        bundle.putInt("Karpet sutra", 25000);
        bundle.putInt("Kasur palembang", 8000);
        bundle.putInt("Tikar plastik", 5000);
    }
}
