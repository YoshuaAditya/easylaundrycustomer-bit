package com.inocen.easylaundrycustomer.HomeActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inocen.easylaundrycustomer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class TabFragment3Nota extends Fragment {

    String url = "https://easylaundry-2c69e.firebaseapp.com/api/detail-nota/";
    private Activity activity;
    ArrayList<NotaGet> notaGetList = new ArrayList<>();
    NotaGet notaGet;
    TabFragment3NotaAdapter adapter;
    RecyclerView recyclerView;
    int numberOfRequest = 0;
    int totalOfRequest = 0;
    ProgressBar progressBar;
    private final int MAX_RETRY=10;
    private final int TIMEOUT=2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_3_nota, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        activity = getActivity();
        progressBar=activity.findViewById(R.id.loading_circle_bar_tab3);

        getNota();

        final SearchView searchView=activity.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void getNota() {
        recyclerView = activity.findViewById(R.id.my_recycler_view_tab3);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new TabFragment3NotaAdapter(activity, HomeActivity.currentUser.listNotaUser, progressBar);
        recyclerView.setAdapter(adapter);

        Collections.sort(notaGetList);
        adapter.notaIdListFilter.addAll(HomeActivity.currentUser.listNotaUser);
        adapter.notifyDataSetChanged();
//
//        numberOfRequest = HomeActivity.currentUser.listNotaUser.size();
//        if(numberOfRequest==0)
//            activity.findViewById(R.id.loading_circle_bar_tab3).setVisibility(View.INVISIBLE);
//        for (final String idNota : HomeActivity.currentUser.listNotaUser) {
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    String tempUrl = url + idNota;
//                    StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
////                                Log.e("GET",idNota);
////                                Toast.makeText(getContext(), ""+response, Toast.LENGTH_SHORT).show();
//                                        JSONObject obj = new JSONObject(response);
//                                        NotaGet notaGet = new NotaGet(
//                                                obj.getString("nota"),
//                                                obj.getString("namaPelanggan"),
//                                                obj.getString("namaOutlet"),
//                                                obj.getString("jenis"),
//                                                obj.getString("berat"),
//                                                obj.getString("tanggalMasuk"),
//                                                obj.getString("tanggalSelesai"),
//                                                obj.getString("biaya"),
//                                                obj.getString("progress"),
//                                                obj.getString("terakhirUpdate"),
//                                                obj.getString("urlNotaPrint")
//                                        );
//                                        notaGetList.add(notaGet);
//                                        adapter.notifyDataSetChanged();
//                                        totalOfRequest++;
//                                        checkAllRequestDone();
//                                    } catch (JSONException e) {
//                                        Toast.makeText(getContext(), "Terdapat gangguan, coba lagi nanti", Toast.LENGTH_SHORT).show();
////                                e.printStackTrace();
//                                        activity.findViewById(R.id.loading_circle_bar_tab3).setVisibility(View.INVISIBLE);
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // Error handling
//                            System.out.println("Something went wrong!");
//                            error.printStackTrace();
//
//                        }
//                    });
//                    Volley.newRequestQueue(activity).add(stringRequest);
//                }
//            }, 1000);
//        }
    }

    private void checkAllRequestDone() {
        if (totalOfRequest == numberOfRequest) {
            Collections.sort(notaGetList);
            adapter.notaIdListFilter.addAll(HomeActivity.currentUser.listNotaUser);
            adapter.notifyDataSetChanged();
            activity.findViewById(R.id.loading_circle_bar_tab3).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Volley.newRequestQueue(activity).cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                // do I have to cancel this?
                return true; // -> always yes
            }
        });
    }
}
