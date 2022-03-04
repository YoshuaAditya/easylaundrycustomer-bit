package com.inocen.easylaundrycustomer.HomeActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.inocen.easylaundrycustomer.LoginActivity;
import com.inocen.easylaundrycustomer.R;
import com.inocen.easylaundrycustomer.RegisterActivity;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabFragment2Transaksi extends Fragment {

    //TODO jangan lupa version code di build gradle
    Map<String, String> itemSatuan = new HashMap<>();
    Map<String, String> itemMeteran = new HashMap<>();
    List<SatuanModel> satuanModelList = new ArrayList<>();
    List<MeteranModel> meteranModelList = new ArrayList<>();
    String url = "https://easylaundry-2c69e.firebaseapp.com/api/buatnota";
    String urlTransaksi = "https://easylaundry-2c69e.firebaseapp.com/api/transaksi";
    String urlTambahNotaUser = "https://easylaundry-2c69e.firebaseapp.com/api/add-idnota";
    String idKiloan = "", idSatuan = "", idMeteran = "";
    private EditText editTextJumlahKiloan, editTextBeratKiloan, editTextNamaPelanggan, editTextNoHp, editTextJenisLayanan, editTextPembayaran;
    private TextView textViewBiayaKiloan, textViewBiayaSatuan, textViewBiayaMeteran,
            textViewTglKiloan, textViewTglSatuan, textViewTglMeteran,
            textViewTotalTagihan, textViewKembalian, textViewTglSelesai, textViewStatusOtomatis;
    private Button buttonCetakNota;
    private Spinner spinnerJenisLayananKiloan;
    private Activity activity;
    int setrika = 5000;
    int reguler = 8000;
    int express = 16000;
    int gorden = 20000;
    private int hargaJenisKiloan = 0, biayaKiloan = 0, biayaSatuan = 0, biayaMeteran = 0, biayaTotal = 0, kembalian = 0;
    private double beratKiloan = 0.0;
    private String future;
    private DateTime dateTime;
    DateTimeFormatter dateTimeFormatter;
    List<View> relatedViewsKiloan = new ArrayList<>();
    List<View> relatedViewsSatuan = new ArrayList<>();
    List<View> relatedViewsMeteran = new ArrayList<>();
    boolean kiloanGone = true;
    boolean satuanGone = true;
    boolean meteranGone = true;
    Printer printer = new Printer();
    boolean notConnected = true;
    private final int MAX_RETRY = 20;
    private final int TIMEOUT = 3000;
    JsonObjectRequest jsonObjectRequestKiloan, jsonObjectRequestSatuan, jsonObjectRequestMeteran, jsonObjectRequestTransaksi;
    StringRequest stringRequestNota;
    private RequestQueue requestQueue;
    MeteranAdapter meteranAdapter;
    SatuanAdapter satuanAdapter;
    ArrayAdapter<String> adapterJenisLayanan;
    ArrayList<String> stringsJenisKiloan=new ArrayList<>();
    ArrayList<Integer> pricesJenisKiloan=new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_2_transaksi, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        JodaTimeAndroid.init(getContext());
        dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        Satuan.initBundle();
        Meteran.initBundle();

        super.onActivityCreated(savedState);
        activity = getActivity();
        requestQueue = Volley.newRequestQueue(activity);
        setOnClick();
        updateHarga();
    }

    private void updateHarga() {
        activity.findViewById(R.id.loading_circle_bar).setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("tarif/");
        databaseReference
                .addListenerForSingleValueEvent
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(final DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.exists()) {
                                     //Kiloan
                                     for (DataSnapshot child : dataSnapshot.child("kiloan").getChildren()) {
                                         String jenis=child.child("jenis").getValue().toString();
                                         int harga=Integer.valueOf(child.child("harga").getValue().toString());
                                         stringsJenisKiloan.add(jenis);
                                         pricesJenisKiloan.add(harga);
                                         ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                                                 activity,
                                                 android.R.layout.simple_spinner_item,
                                                 new ArrayList<String>(stringsJenisKiloan));
                                         spinnerJenisLayananKiloan.setAdapter(adapter);
                                     }
                                     //Meteran
                                     for (DataSnapshot child : dataSnapshot.child("meteran").getChildren()) {
                                         String item=child.child("item").getValue().toString();
                                         int harga=Integer.valueOf(child.child("harga").getValue().toString());
                                         Meteran.bundle.putInt(item,harga);
                                         meteranAdapter.strings.remove(item);
                                         meteranAdapter.strings.add(item);
                                         ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                                                 activity,
                                                 android.R.layout.simple_spinner_item,
                                                 new ArrayList<String>(meteranAdapter.strings));
                                         meteranAdapter.adapter=adapter;
                                     }
                                     //Satuan
                                     for (DataSnapshot child : dataSnapshot.child("satuan").getChildren()) {
                                         String item=child.child("item").getValue().toString();
                                         int harga=Integer.valueOf(child.child("harga").getValue().toString());
                                         Satuan.bundle.putInt(item,harga);
                                         satuanAdapter.strings.remove(item);
                                         satuanAdapter.strings.add(item);
                                         ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                                                 activity,
                                                 android.R.layout.simple_spinner_item,
                                                 new ArrayList<String>(satuanAdapter.strings));
                                         satuanAdapter.adapter=adapter;
                                     }
                                     activity.findViewById(R.id.loading_circle_bar).setVisibility(View.INVISIBLE);
                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                                 activity.findViewById(R.id.loading_circle_bar).setVisibility(View.INVISIBLE);
                             }
                         }
                        );
    }

    private void setOnClick() {
        editTextNamaPelanggan = activity.findViewById(R.id.editTextNamaPelanggan);
        editTextNoHp = activity.findViewById(R.id.editTextNomorHP);

        textViewBiayaKiloan = activity.findViewById(R.id.textViewBiayaKiloan);
        textViewBiayaKiloan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                kalkulasiTotalBiaya();
            }
        });
        textViewBiayaSatuan = activity.findViewById(R.id.textViewBiayaSatuan);
        textViewBiayaSatuan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                biayaSatuan = Integer.decode(textViewBiayaSatuan.getText().toString());
                kalkulasiTotalBiaya();
            }
        });
        textViewBiayaMeteran = activity.findViewById(R.id.textViewBiayaMeteran);
        textViewBiayaMeteran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                biayaMeteran = Integer.decode(textViewBiayaMeteran.getText().toString());
                kalkulasiTotalBiaya();
            }
        });
        textViewTglKiloan = activity.findViewById(R.id.textViewTglSelesaiKiloan);
        textViewTglSatuan = activity.findViewById(R.id.textViewTglSelesaiSatuan);
        textViewTglMeteran = activity.findViewById(R.id.textViewTglSelesaiMeteran);
        textViewTotalTagihan = activity.findViewById(R.id.textViewTotalBiayaOtomatis);
//        textViewTotalTagihan = activity.findViewById(R.id.textViewTotalBiayaOtomatis);
        textViewKembalian = activity.findViewById(R.id.textViewTglKembalianOtomatis);
        textViewStatusOtomatis = activity.findViewById(R.id.textViewStatusOtomatis);
        editTextPembayaran = activity.findViewById(R.id.editTextPembayaran);
        editTextPembayaran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                kalkulasiKembalian();
            }
        });
        buttonCetakNota = activity.findViewById(R.id.buttonCekNota);
        buttonCetakNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextNamaPelanggan.getText().toString().isEmpty() || editTextNoHp.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Masukan nama dan nomor ponsel", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (biayaTotal == 0) {
                    Toast.makeText(getContext(), "Masukan minimal satu jenis order", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (textViewStatusOtomatis.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Masukan angka pembayaran", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editTextPembayaran.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Masukan angka pembayaran", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (!isNetworkAvailable()) {
//                    Toast.makeText(getContext(), "Tidak ada internet", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (!HomeActivity.currentUser.finished) {
                    Toast.makeText(getContext(), "Tunggu sebentar, coba lagi beberapa saat", Toast.LENGTH_SHORT).show();
                    return;
                }
                buttonCetakNota.setEnabled(false);
                activity.findViewById(R.id.loading_circle_bar).setVisibility(View.VISIBLE);
                if (biayaKiloan != 0) {
                    DateTime temp = new DateTime();
                    String sekarang = temp.toString(dateTimeFormatter);
                    if (spinnerJenisLayananKiloan.getSelectedItem().toString().equals("express"))
                        temp = temp.plusHours(6);
                    else temp = temp.plusDays(1);
                    String selesai = temp.toString(dateTimeFormatter);
                    //TODO buat status masih bisa diPOST? cek yg lain juga
                    NotaPostKiloan kiloan = new NotaPostKiloan(
                            HomeActivity.currentUser.token,
                            HomeActivity.currentUser.kodeOutlet,
                            HomeActivity.currentUser.namaOutlet,
                            editTextNamaPelanggan.getText().toString(),
                            editTextNoHp.getText().toString(),
                            "Kiloan",
                            spinnerJenisLayananKiloan.getSelectedItem().toString(),
                            editTextBeratKiloan.getText().toString() + " kg",
                            editTextJumlahKiloan.getText().toString(),
                            String.valueOf(biayaKiloan),
                            textViewStatusOtomatis.getText().toString(),
                            "Belum diproses",
                            sekarang,
                            selesai
                    );
                    Gson gson = new Gson();
                    String json = gson.toJson(kiloan);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        jsonObjectRequestKiloan = new JsonObjectRequest(Request.Method.POST,
                                url, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            idKiloan = response.getString("idNota");
                                            Toast.makeText(getContext(), "Nota kiloan terbuat dengan id: " + idKiloan, Toast.LENGTH_SHORT).show();
                                            buatNotaSatuan();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        requestQueue.add(jsonObjectRequestKiloan)
                                                .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("charset", "utf-8");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequestKiloan)
                                .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (biayaSatuan != 0) {
                    buatNotaSatuan();
                } else {
                    buatNotaMeteran();
                }
            }
        });
        //BEGIN kiloan
        addListViews(1, R.id.textViewKiloBerat);
        addListViews(1, R.id.textViewKiloBiaya);
        addListViews(1, R.id.textViewKiloBiayaTanggal);
        addListViews(1, R.id.textViewKiloJenis);
        addListViews(1, R.id.textViewKiloJumlah);
        addListViews(1, R.id.textViewKiloTanggal);
        addListViews(1, R.id.textViewBiayaKiloan);
        addListViews(1, R.id.textViewTglSelesaiKiloan);
        addListViews(1, R.id.constraintLayoutNestedKiloan1);
        addListViews(1, R.id.constraintLayoutNestedKiloan2);
        spinnerJenisLayananKiloan = addReturnListViewsSpinner(1, R.id.spinnerJenisLayananKiloan);
        adapterJenisLayanan =new ArrayAdapter<String>(
                activity,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(stringsJenisKiloan));
        adapterJenisLayanan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenisLayananKiloan.setAdapter(adapterJenisLayanan);
        spinnerJenisLayananKiloan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerJenisLayananKiloan.getSelectedItem().toString().equals("express")) {
                    kalkulasiWaktu(2);
                }
                else kalkulasiWaktu(1);
                hargaJenisKiloan=pricesJenisKiloan.get(spinnerJenisLayananKiloan.getSelectedItemPosition());
                kalkulasiBiaya(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editTextBeratKiloan = addReturnListViews(1, R.id.editTextBeratKiloan);
        editTextBeratKiloan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = editTextBeratKiloan.getText().toString();
                if (!temp.isEmpty()) {
                    beratKiloan = Double.parseDouble(temp);
                    if (beratKiloan > 10000) {
                        beratKiloan = 10000;
                        editTextBeratKiloan.setText(String.valueOf(beratKiloan));
                    }
                    kalkulasiBiaya(1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextJumlahKiloan = addReturnListViews(1, R.id.editTextJumlahPcsKiloan);
        ConstraintLayout constraintLayoutKiloan = activity.findViewById(R.id.constraintLayoutKiloan);
        constraintLayoutKiloan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(1, relatedViewsKiloan);
            }
        });
        //END kiloan

        //BEGIN satuan
        RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_satuan);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        satuanModelList.add(new SatuanModel());
        satuanAdapter = new SatuanAdapter(activity, satuanModelList, textViewBiayaSatuan);
        recyclerView.setAdapter(satuanAdapter);
        addListViews(2, R.id.recycler_view_satuan);
        addListViews(2, R.id.textView27);
        addListViews(2, R.id.textView28);
        addListViews(2, R.id.textViewTanggalSatuan);
        addListViews(2, R.id.imageViewPlusRowSatuan);
        addListViews(2, R.id.imageViewMinusRowSatuan);
        addListViews(2, R.id.constraintLayoutNestedSatuan1);
        addListViews(2, R.id.constraintLayoutNestedSatuan2);
        ImageView imageViewMinusRowSatuan = activity.findViewById(R.id.imageViewMinusRowSatuan);
        imageViewMinusRowSatuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satuanModelList.size() == 1) return;
                satuanModelList.remove(satuanModelList.size() - 1);
                satuanAdapter.notifyDataSetChanged();
            }
        });

        ImageView imageViewPlusRowSatuan = activity.findViewById(R.id.imageViewPlusRowSatuan);
        imageViewPlusRowSatuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                satuanModelList.add(new SatuanModel());
                satuanAdapter.notifyDataSetChanged();
            }
        });

        ConstraintLayout constraintLayoutSatuan = activity.findViewById(R.id.constraintLayoutSatuan);
        constraintLayoutSatuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(2, relatedViewsSatuan);
                kalkulasiWaktu(1, textViewTglSatuan);
            }
        });
        //END satuan

        //BEGIN meteran
        RecyclerView recyclerViewMeteran = activity.findViewById(R.id.recycler_view_meteran);
        recyclerViewMeteran.setLayoutManager(new LinearLayoutManager(activity));
        meteranModelList.add(new MeteranModel());
        meteranAdapter = new MeteranAdapter(activity, meteranModelList, textViewBiayaMeteran);
        recyclerViewMeteran.setAdapter(meteranAdapter);
        addListViews(3, R.id.textViewBiayaTanggalMeteran);
        addListViews(3, R.id.textViewTulisanBiayaMeteran);
        addListViews(3, R.id.textViewTulisanTanggalMeteran);
        addListViews(3, R.id.constraintLayoutBiayaMeteran);
        addListViews(3, R.id.constraintLayoutTanggalSelesaiMeteran);
        addListViews(3, R.id.imageViewPlusRowMeteran);
        addListViews(3, R.id.imageViewMinusRowMeteran);
        addListViews(3, R.id.recycler_view_meteran);
        ImageView imageViewMinusRowMeteran = activity.findViewById(R.id.imageViewMinusRowMeteran);
        imageViewMinusRowMeteran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meteranModelList.size() == 1) return;
                meteranModelList.remove(meteranModelList.size() - 1);
                meteranAdapter.notifyDataSetChanged();
            }
        });

        ImageView imageViewPlusRowMeteran = activity.findViewById(R.id.imageViewPlusRowMeteran);
        imageViewPlusRowMeteran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meteranModelList.add(new MeteranModel());
                meteranAdapter.notifyDataSetChanged();
            }
        });
        ConstraintLayout constraintLayoutMeteran = activity.findViewById(R.id.constraintLayoutMeteran);
        constraintLayoutMeteran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(3, relatedViewsMeteran);
                kalkulasiWaktu(3, textViewTglMeteran);
            }
        });
        //END meteran
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void kalkulasiKembalian() {
        String temp = editTextPembayaran.getText().toString();
        if (!temp.isEmpty()) {
            kembalian = Integer.decode(temp) - biayaTotal;
            textViewKembalian.setText(String.valueOf(kembalian));
            if (kembalian < 0) {
                textViewStatusOtomatis.setText("DP");
            } else textViewStatusOtomatis.setText("lunas");
        }
    }

    private void buatNotaSatuan() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (biayaSatuan != 0) {
                    DateTime temp = new DateTime();
                    String sekarang = temp.toString(dateTimeFormatter);
                    temp = temp.plusDays(1);
                    String selesai = temp.toString(dateTimeFormatter);
                    for (int i = 0; i < satuanModelList.size(); i++) {
                        itemSatuan.put(satuanModelList.get(i).namaSatuan, String.valueOf(satuanModelList.get(i).jumlah));
                    }
                    NotaPostSatuan satuan = new NotaPostSatuan(
                            HomeActivity.currentUser.token,
                            HomeActivity.currentUser.kodeOutlet,
                            HomeActivity.currentUser.namaOutlet,
                            editTextNamaPelanggan.getText().toString(),
                            editTextNoHp.getText().toString(),
                            "Satuan",
                            itemSatuan,
                            String.valueOf(biayaSatuan),
                            textViewStatusOtomatis.getText().toString(),
                            "Belum diproses",
                            sekarang,
                            selesai
                    );
                    Gson gson = new Gson();
                    String json = gson.toJson(satuan);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        jsonObjectRequestSatuan = new JsonObjectRequest(Request.Method.POST,
                                url, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            idSatuan = response.getString("idNota");
                                            Toast.makeText(getContext(), "Nota satuan terbuat dengan id: " + idSatuan, Toast.LENGTH_SHORT).show();
                                            buatNotaMeteran();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        requestQueue.add(jsonObjectRequestSatuan)
                                                .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("charset", "utf-8");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequestSatuan)
                                .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    buatNotaMeteran();
                }
            }
        }, 500);
    }

    private void buatNotaMeteran() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (biayaMeteran != 0) {
                    DateTime temp = new DateTime();
                    String sekarang = temp.toString(dateTimeFormatter);
                    temp = temp.plusDays(3);
                    String selesai = temp.toString(dateTimeFormatter);
                    for (int i = 0; i < meteranModelList.size(); i++) {
                        String luas = String.valueOf(meteranModelList.get(i).panjang) + "x" + String.valueOf(meteranModelList.get(i).lebar);
                        itemMeteran.put(meteranModelList.get(i).namaMeteran, luas + "/" + String.valueOf(meteranModelList.get(i).jumlah) + "pcs");
                    }
                    NotaPostMeteran meteran = new NotaPostMeteran(
                            HomeActivity.currentUser.token,
                            HomeActivity.currentUser.kodeOutlet,
                            HomeActivity.currentUser.namaOutlet,
                            editTextNamaPelanggan.getText().toString(),
                            editTextNoHp.getText().toString(),
                            "Meteran",
                            itemMeteran,
                            String.valueOf(biayaMeteran),
                            textViewStatusOtomatis.getText().toString(),
                            "Belum diproses",
                            sekarang,
                            selesai
                    );
                    Gson gson = new Gson();
                    String json = gson.toJson(meteran);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        jsonObjectRequestMeteran = new JsonObjectRequest(Request.Method.POST,
                                url, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            idMeteran = response.getString("idNota");
                                            Toast.makeText(getContext(), "Nota meteran terbuat dengan id: " + idMeteran, Toast.LENGTH_SHORT).show();
                                            cetakTransaksi();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        requestQueue.add(jsonObjectRequestMeteran)
                                                .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("charset", "utf-8");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequestMeteran)
                                .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    cetakTransaksi();
                }
            }
        }, 500);
    }

    private void cetakTransaksi() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //POST transaksi
                Map<String, String> detailKiloan = new HashMap<>();
                NotaPostTransaksi.DetailTransaksi detailSatuan;
                NotaPostTransaksi.DetailTransaksi detailMeteran;
                if (biayaKiloan == 0) detailKiloan = null;
                else {
                    detailKiloan.put("jenis", "Kiloan");
                    detailKiloan.put("layanan", spinnerJenisLayananKiloan.getSelectedItem().toString());
                    detailKiloan.put("jumlah", editTextJumlahKiloan.getText().toString());
                    detailKiloan.put("berat", editTextBeratKiloan.getText().toString() + " kg");
                    detailKiloan.put("biaya", String.valueOf(biayaKiloan));
                }
                if (biayaSatuan == 0) detailSatuan = null;
                else {
                    detailSatuan = new NotaPostTransaksi.DetailTransaksi("Satuan", itemSatuan, String.valueOf(biayaSatuan));
                }
                if (biayaMeteran == 0) detailMeteran = null;
                else {
                    detailMeteran = new NotaPostTransaksi.DetailTransaksi("Meteran", itemMeteran, String.valueOf(biayaMeteran));
                }
                final NotaPostTransaksi transaksi = new NotaPostTransaksi(
                        HomeActivity.currentUser.token,
                        detailKiloan,
                        detailSatuan,
                        detailMeteran,
                        String.valueOf(biayaTotal),
                        editTextPembayaran.getText().toString(),
                        textViewKembalian.getText().toString(),
                        textViewStatusOtomatis.getText().toString(),
                        HomeActivity.currentUser.namaOutlet
                );
                Gson gson = new Gson();
                String json = gson.toJson(transaksi);
                json = json.replace("detailKiloan", idKiloan);
                json = json.replace("detailSatuan", idSatuan);
                json = json.replace("detailMeteran", idMeteran);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    jsonObjectRequestTransaksi = new JsonObjectRequest(Request.Method.POST,
                            urlTransaksi, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                            Log.e("HMM","Transaksi");
                                    buttonCetakNota.setEnabled(true);
                                    activity.findViewById(R.id.loading_circle_bar).setVisibility(View.INVISIBLE);

                                    //POST add idnota
                                    String idPertama;
                                    if (!idKiloan.isEmpty()) {
                                        addIdNotaAPI(idKiloan);
                                        idPertama = idKiloan;
                                    } else if (!idSatuan.isEmpty()) {
                                        addIdNotaAPI(idSatuan);
                                        idPertama = idSatuan;

                                    } else {
                                        addIdNotaAPI(idMeteran);
                                        idPertama = idMeteran;
                                    }

                                    //add manual soalnya udh diambil, biar gk request akeh
                                    HomeActivity.currentUser.listNotaUser.add(idPertama);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", idPertama);
                                    bundle.putString("kiloan", idKiloan);
                                    bundle.putString("satuan", idSatuan);
                                    bundle.putString("meteran", idMeteran);
                                    bundle.putString("hp", editTextNoHp.getText().toString());
                                    bundle.putString("nama", editTextNamaPelanggan.getText().toString());
                                    CekNotaActivity.transaksi = transaksi;

                                    Intent i = new Intent(activity, CekNotaActivity.class);
                                    i.putExtra("bundle", bundle);
                                    startActivity(i);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    requestQueue.add(jsonObjectRequestTransaksi)
                                            .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("charset", "utf-8");
                            return headers;
                        }
                    };
                    requestQueue.add(jsonObjectRequestTransaksi)
                            .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    private void addIdNotaAPI(final String idNota) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                stringRequestNota = new StringRequest(Request.Method.POST, urlTambahNotaUser,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                        Log.e("HMM","add idnota");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                requestQueue.add(stringRequestNota)
                                        .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("token", HomeActivity.currentUser.token);
                        params.put("idNota", idNota);
                        return params;
                    }
                };
                requestQueue.add(stringRequestNota)
                        .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        }, 500);
    }

    private void kalkulasiTotalBiaya() {
        biayaTotal = biayaKiloan + biayaMeteran + biayaSatuan;
        textViewTotalTagihan.setText(String.valueOf(biayaTotal));
        kalkulasiKembalian();
    }

    private void kalkulasiWaktu(int i) {
        DateTime temp = new DateTime();
        switch (i) {
            case 1://kiloan non-express, satuan
                temp = temp.plusDays(1);
                break;
            case 2://express
                temp = temp.plusHours(6);
                break;
        }
        future = temp.toString(dateTimeFormatter);
        textViewTglKiloan.setText(future);
    }

    private void kalkulasiWaktu(int i, TextView textView) {
        DateTime temp = new DateTime();
        switch (i) {
            case 1://reguler
                temp = temp.plusDays(1);
                break;
            case 2://express
                temp = temp.plusHours(6);
                break;
            case 3://meteran
                temp = temp.plusDays(3);
                break;
        }
        future = temp.toString(dateTimeFormatter);
        textView.setText(future);
    }

    private void kalkulasiBiaya(int tipe) {
        switch (tipe) {
            case 1://kiloan
                biayaKiloan = (int) (hargaJenisKiloan * beratKiloan);
                textViewBiayaKiloan.setText(String.valueOf(biayaKiloan));
                break;
            case 2://satuan
                break;
            case 3://meteran
                break;
        }
    }

    private void addListViews(int listType, int viewID) {
        switch (listType) {
            case 1:
                relatedViewsKiloan.add(activity.findViewById(viewID));
                break;
            case 2:
                relatedViewsSatuan.add(activity.findViewById(viewID));
                break;
            case 3:
                relatedViewsMeteran.add(activity.findViewById(viewID));
                break;
        }
    }

    private EditText addReturnListViews(int listType, int viewID) {
        EditText editText = activity.findViewById(viewID);
        switch (listType) {
            case 1:
                relatedViewsKiloan.add(editText);
                break;
            case 2:
                relatedViewsSatuan.add(editText);
                break;
            case 3:
                relatedViewsMeteran.add(editText);
                break;
        }
        return editText;
    }

    private Spinner addReturnListViewsSpinner(int listType, int viewID) {
        Spinner spinner = activity.findViewById(viewID);
        switch (listType) {
            case 1:
                relatedViewsKiloan.add(spinner);
                break;
            case 2:
                relatedViewsSatuan.add(spinner);
                break;
            case 3:
                relatedViewsMeteran.add(spinner);
                break;
        }
        return spinner;
    }

    private void changeVisibility(int listType, List<View> views) {
        int visibility = View.VISIBLE;
        switch (listType) {
            case 1:
                if (kiloanGone) {
                    visibility = View.VISIBLE;
                } else visibility = View.GONE;
                kiloanGone = !kiloanGone;
                break;
            case 2:
                if (satuanGone) {
                    visibility = View.VISIBLE;
                } else visibility = View.GONE;
                satuanGone = !satuanGone;
                break;
            case 3:
                if (meteranGone) {
                    visibility = View.VISIBLE;
                } else visibility = View.GONE;
                meteranGone = !meteranGone;
                break;
        }
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                // do I have to cancel this?
                return true; // -> always yes
            }
        });
        try {
            printer.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //TODO kepencet back pas proses upload cancel se, tapi wajar karena onStop?
}
