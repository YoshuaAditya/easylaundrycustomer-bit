package com.inocen.easylaundrycustomer.HomeActivity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.inocen.easylaundrycustomer.R;

public class HomeActivity extends AppCompatActivity {

    final int[] iconSelected = new int[]{R.drawable.ic_pic, R.drawable.ic_tab, R.drawable.ic_ceknota_white, R.drawable.ic_scan};
    final int[] iconUnselected = new int[]{R.drawable.ic_pic_blur, R.drawable.ic_tab_blur, R.drawable.ic_ceknota_blur, R.drawable.ic_scan_blur};
    public static CurrentUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("PIC").setIcon(iconSelected[0]));
        tabLayout.addTab(tabLayout.newTab().setText("Transaksi").setIcon(iconUnselected[1]));
        tabLayout.addTab(tabLayout.newTab().setText("Cek Nota").setIcon(iconUnselected[2]));
        tabLayout.addTab(tabLayout.newTab().setText("Scan").setIcon(iconUnselected[3]));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.getTabAt(tab.getPosition()).setIcon(iconSelected[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).setIcon(iconUnselected[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
