package com.inocen.easylaundrycustomer.HomeActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inocen.easylaundrycustomer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TabFragment3NotaAdapter extends RecyclerView.Adapter<TabFragment3NotaAdapter.CardViewHolder> {
    private Context mCtx;
    private List<String> notaIdList;
    public List<String> notaIdListFilter = new ArrayList<>();
    private SharedPreferences sharedPref;
    private int imageSizeDp = 400;
    ProgressBar progressBar;
    String url = "https://easylaundry-2c69e.firebaseapp.com/api/detail-nota/";

    public TabFragment3NotaAdapter(Context mCtx, List<String> notaIdList, ProgressBar progressBar) {
        this.mCtx = mCtx;
        this.notaIdList = notaIdList;
        this.progressBar = progressBar;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mCtx);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.card_view_nota, null);

        final CardViewHolder holder = new CardViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        final String notaGet = notaIdList.get(i);
        cardViewHolder.textViewNoNota.setText(notaGet);
//        cardViewHolder.textViewNamaPelanggan.setText(notaGet.namaPelanggan);
//        cardViewHolder.textViewTglMasuk.setText(notaGet.tglMasuk);
//        cardViewHolder.textViewTglSelesai.setText(notaGet.tglSelesai);
//        cardViewHolder.textViewStatus.setText(notaGet.progress);
        cardViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String tempUrl = url + notaGet;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
//                                Log.e("GET",idNota);
//                                Toast.makeText(getContext(), ""+response, Toast.LENGTH_SHORT).show();
                                    JSONObject obj = new JSONObject(response);
                                    NotaGet notaGet = new NotaGet(
                                            obj.getString("nota"),
                                            obj.getString("namaPelanggan"),
                                            obj.getString("namaOutlet"),
                                            obj.getString("jenis"),
                                            obj.getString("berat"),
                                            obj.getString("tanggalMasuk"),
                                            obj.getString("tanggalSelesai"),
                                            obj.getString("biaya"),
                                            obj.getString("progress"),
                                            obj.getString("terakhirUpdate"),
                                            obj.getString("urlNotaPrint")
                                    );
                                    TabFragment3DetailNotaActivity.notaGet = notaGet;
                                    mCtx.startActivity(new Intent(mCtx, TabFragment3DetailNotaActivity.class));
                                    progressBar.setVisibility(View.INVISIBLE);
                                } catch (JSONException e) {
                                    Toast.makeText(mCtx, "Terdapat gangguan, coba lagi nanti", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
//                                e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        System.out.println("Something went wrong!");
                        error.printStackTrace();

                    }
                });
                Volley.newRequestQueue(mCtx).add(stringRequest);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notaIdList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageViewCard;
        TextView textViewNoNota;
        TextView textViewNamaPelanggan;
        TextView textViewTglMasuk;
        TextView textViewTglSelesai;
        TextView textViewStatus;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            textViewNoNota = itemView.findViewById(R.id.textViewNoNota);
//            textViewNamaPelanggan = itemView.findViewById(R.id.textViewNamaPelanggan);
//            textViewTglMasuk = itemView.findViewById(R.id.textViewTglMasuk);
//            textViewTglSelesai = itemView.findViewById(R.id.textViewTglSelesai);
//            textViewStatus = itemView.findViewById(R.id.textViewStatus);
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

    public void filter(String text) {
        notaIdList.clear();
        if (text.isEmpty()) {
            notaIdList.addAll(notaIdListFilter);
        } else {
            text = text.toLowerCase();
            for (String item : notaIdListFilter) {
//                Log.e("LIST",item + " "+item.toLowerCase().contains(text));
                if (item.toLowerCase().contains(text)) {
                    notaIdList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
