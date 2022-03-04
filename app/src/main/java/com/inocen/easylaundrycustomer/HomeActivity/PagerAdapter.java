package com.inocen.easylaundrycustomer.HomeActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragment1PIC tab1 = new TabFragment1PIC();
                return tab1;
            case 1:
                TabFragment2Transaksi tab2 = new TabFragment2Transaksi();
                return tab2;
            case 2:
                TabFragment3Nota tab3 = new TabFragment3Nota();
                return tab3;
            case 3:
                TabFragment4Scan tab4 = new TabFragment4Scan();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}