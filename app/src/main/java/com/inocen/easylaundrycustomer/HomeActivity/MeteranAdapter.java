package com.inocen.easylaundrycustomer.HomeActivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inocen.easylaundrycustomer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MeteranAdapter extends RecyclerView.Adapter<MeteranAdapter.CardViewHolder> {
    private Context mCtx;
    private List<MeteranModel> meteranModelList;
    private TextView textViewBiaya;
    ArrayAdapter<String> adapter ;
    ArrayList<String> strings=new ArrayList<>();

    public MeteranAdapter(Context mCtx, List<MeteranModel> meteranModelList, TextView textViewBiaya) {
//        strings=  new ArrayList<>(Arrays.asList(mCtx.getResources().getStringArray(R.array.jenis_meteran_array)));
        adapter=new ArrayAdapter<String>(
                mCtx,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(strings));
        this.mCtx = mCtx;
        this.meteranModelList = meteranModelList;
        this.textViewBiaya = textViewBiaya;
    }
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.recycler_item_meteran, null);

        final CardViewHolder holder = new CardViewHolder(view);

        //set onClickListener tiap holder
        holder.textViewNumber.setText(String.valueOf(i+1));
        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah= Integer.decode(holder.editTextJumlahPcs.getText().toString());
                jumlah--;
                if(jumlah<0)jumlah=0;
                meteranModelList.get(holder.getAdapterPosition()).jumlah=jumlah;
                holder.editTextJumlahPcs.setText(String.valueOf(jumlah));
                kalkulasiBiaya();
            }
        });
        holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah= Integer.decode(holder.editTextJumlahPcs.getText().toString());
                jumlah++;
                meteranModelList.get(holder.getAdapterPosition()).jumlah=jumlah;
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
                meteranModelList.get(holder.getAdapterPosition()).harga=
                        Meteran.bundle.getInt(holder.editTextJenis.getText().toString());
                meteranModelList.get(holder.getAdapterPosition()).namaMeteran=holder.editTextJenis.getText().toString();
                kalkulasiBiaya();
            }
        });
        holder.editTextPanjang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                meteranModelList.get(holder.getAdapterPosition()).panjang=
                       Integer.decode(holder.editTextPanjang.getText().toString());
                kalkulasiBiaya();
            }
        });
        holder.editTextLebar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                meteranModelList.get(holder.getAdapterPosition()).lebar=
                        Integer.decode(holder.editTextLebar.getText().toString());
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
                meteranModelList.get(holder.getAdapterPosition()).jumlah=Integer.decode(holder.editTextJumlahPcs.getText().toString());
                kalkulasiBiaya();
            }
        });

        return holder;
    }

    private void kalkulasiBiaya() {
        int total=0;
//        Log.e("biaya","harga: "+meteranModelList.get(0).harga+
//                " jumlah: "+meteranModelList.get(0).jumlah+
//                " panjang: "+meteranModelList.get(0).panjang+
//                " lebar: "+meteranModelList.get(0).lebar);
        for(int i=0;i<meteranModelList.size();i++){
            total+=meteranModelList.get(i).harga*meteranModelList.get(i).jumlah
                    *meteranModelList.get(i).panjang*meteranModelList.get(i).lebar;
        }
        textViewBiaya.setText(String.valueOf(total));
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        //buat note kedepan, method ini dipanggil pas notifyDataSetChanged
        kalkulasiBiaya();
    }

    @Override
    public int getItemCount() {
        return meteranModelList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber;
        AutoCompleteTextView editTextJenis;
        EditText editTextJumlahPcs;
        EditText editTextPanjang;
        EditText editTextLebar;
        ImageView imageViewPlus;
        ImageView imageViewMinus;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNomor);
            editTextJenis = itemView.findViewById(R.id.editTextJenisMeteran);
            editTextJumlahPcs = itemView.findViewById(R.id.editTextJumlahPcsMeteran);
            editTextPanjang= itemView.findViewById(R.id.editTextPanjangMeteran);
            editTextLebar= itemView.findViewById(R.id.editTextLebarMeteran);
            imageViewMinus = itemView.findViewById(R.id.imageViewMinusMeteran);
            imageViewPlus = itemView.findViewById(R.id.imageViewPlusMeteran);
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
