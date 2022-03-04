package com.inocen.easylaundrycustomer.HomeActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inocen.easylaundrycustomer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SatuanAdapter extends RecyclerView.Adapter<SatuanAdapter.CardViewHolder> {
    private Context mCtx;
    private List<SatuanModel> satuanModelList;
    private TextView textViewBiaya;
    ArrayAdapter<String> adapter;
    ArrayList<String> strings=new ArrayList<>();

    public SatuanAdapter(Context mCtx, List<SatuanModel> satuanModelList, TextView textViewBiaya) {
//        strings=  new ArrayList<>(Arrays.asList(mCtx.getResources().getStringArray(R.array.jenis_satuan_array)));
        adapter=new ArrayAdapter<String>(
                mCtx,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(strings));
        this.mCtx = mCtx;
        this.satuanModelList = satuanModelList;
        this.textViewBiaya = textViewBiaya;
    }
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.recycler_item_satuan, null);

        final CardViewHolder holder = new CardViewHolder(view);

        //set onClickListener tiap holder
        holder.textViewNumber.setText(String.valueOf(i+1));
        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah= Integer.decode(holder.editTextJumlahPcs.getText().toString());
                jumlah--;
                if(jumlah<0)jumlah=0;
                satuanModelList.get(holder.getAdapterPosition()).jumlah=jumlah;
                holder.editTextJumlahPcs.setText(String.valueOf(jumlah));
                kalkulasiBiaya();
            }
        });
        holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah= Integer.decode(holder.editTextJumlahPcs.getText().toString());
                jumlah++;
                satuanModelList.get(holder.getAdapterPosition()).jumlah=jumlah;
                holder.editTextJumlahPcs.setText(String.valueOf(jumlah));
                kalkulasiBiaya();
            }
        });
        holder.editTextJenis.setThreshold(1);
        holder.editTextJenis.setAdapter(adapter);
        holder.editTextJenis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                satuanModelList.get(holder.getAdapterPosition()).harga=
                        Satuan.bundle.getInt(holder.editTextJenis.getText().toString());
                satuanModelList.get(holder.getAdapterPosition()).namaSatuan=holder.editTextJenis.getText().toString();
                kalkulasiBiaya();
            }
        });
        holder.editTextJumlahPcs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String check=holder.editTextJumlahPcs.getText().toString();
                if(check.isEmpty()){
                    holder.editTextJumlahPcs.setText("0");
                    return;
                }
                satuanModelList.get(holder.getAdapterPosition()).jumlah=Integer.decode(holder.editTextJumlahPcs.getText().toString());
                kalkulasiBiaya();
            }
        });


        return holder;
    }

    private void kalkulasiBiaya() {
        int total=0;
        for(int i=0;i<satuanModelList.size();i++){
            total+=satuanModelList.get(i).harga*satuanModelList.get(i).jumlah;
        }
        textViewBiaya.setText(String.valueOf(total));
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        kalkulasiBiaya();
    }

    @Override
    public int getItemCount() {
        return satuanModelList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber;
        AutoCompleteTextView editTextJenis;
        EditText editTextJumlahPcs;
        ImageView imageViewPlus;
        ImageView imageViewMinus;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNomor);
            editTextJenis = itemView.findViewById(R.id.editTextJenisSatuan);
            editTextJumlahPcs = itemView.findViewById(R.id.editTextJumlahPcsSatuan);
            imageViewMinus = itemView.findViewById(R.id.imageViewMinusSatuan);
            imageViewPlus = itemView.findViewById(R.id.imageViewPlusSatuan);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
