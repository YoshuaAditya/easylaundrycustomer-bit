package com.inocen.easylaundrycustomer.ViewPagerActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.inocen.easylaundrycustomer.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context){
        this.context = context;
    }
    public int[] slide_images={
            R.drawable.ic_view_pager1,
            R.drawable.ic_view_pager2,
            R.drawable.ic_view_pager3,
            R.drawable.ic_view_pager4
    };
    public int[] slide_images_easy_laundry={
            R.drawable.ic_easylaundry,
            R.drawable.ic_easylaundry,
            R.drawable.ic_easylaundry,
            R.drawable.ic_easylaundry
    };
    public int[] slide_headings={
            R.string.tidakribet,
            R.string.proseslaundry,
            R.string.biayaterjangkau,
            R.string.antarsampai
    };
    public int[] slide_keterangan={
            R.string.ketpager1,
            R.string.ketpager2,
            R.string.ketpager3,
            R.string.ketpager4
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView slideImageView = view.findViewById(R.id.imageViewLogo);
        TextView slideHeading = view.findViewById(R.id.textViewTitle);
        TextView slideKeterangan = view.findViewById(R.id.textViewKeterangan);
        ImageView slideImageViewEasyLaundry = view.findViewById(R.id.imageViewEasyLaundry);
        slideImageView.setImageResource(slide_images[position]);
        slideImageViewEasyLaundry.setImageResource(slide_images_easy_laundry[position]);
        slideHeading.setText(context.getText(slide_headings[position]));
        slideKeterangan.setText(context.getText(slide_keterangan[position]));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
