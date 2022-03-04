package com.inocen.easylaundrycustomer.ViewPagerActivity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inocen.easylaundrycustomer.HomeActivity.HomeActivity;
import com.inocen.easylaundrycustomer.LoginActivity;
import com.inocen.easylaundrycustomer.R;
import com.inocen.easylaundrycustomer.SharedPrefHelper;

public class ViewPagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewBack, textViewNext;
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private int mCurrentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        initialize();
    }
    private void initialize(){
        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.linearDot);
        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);
        textViewBack = findViewById(R.id.textViewBack);
        textViewNext = findViewById(R.id.textViewNext);
        textViewBack.setOnClickListener(this);
        textViewNext.setOnClickListener(this);
    }
    private void addDotsIndicator(int position){
        mDots = new TextView[4];
        mDotLayout.removeAllViews();
        for (int i=0; i<mDots.length; i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorYoungBlue));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;
            if (i==0){
                textViewBack.setEnabled(false);
                textViewNext.setEnabled(true);
                textViewBack.setVisibility(View.INVISIBLE);
                textViewNext.setText(getResources().getText(R.string.next));
                textViewBack.setText("");
            }else if (i==mDots.length-1){
                textViewBack.setEnabled(true);
                textViewNext.setEnabled(true);
                textViewBack.setVisibility(View.VISIBLE);
                textViewNext.setText(getResources().getText(R.string.done));
                textViewBack.setText(getResources().getText(R.string.back));
            }else {
                textViewBack.setEnabled(true);
                textViewNext.setEnabled(true);
                textViewBack.setVisibility(View.VISIBLE);
                textViewNext.setText(getResources().getText(R.string.next));
                textViewBack.setText(getResources().getText(R.string.back));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v==textViewBack){
            mSlideViewPager.setCurrentItem(mCurrentPage - 1);
        }
        if (v==textViewNext){
            if (mCurrentPage==3){
                SharedPrefHelper sharedPrefHelper=new SharedPrefHelper(ViewPagerActivity.this);
                sharedPrefHelper.putSharedPref(getPackageName(),true);
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
            }else{
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        }
    }
}
