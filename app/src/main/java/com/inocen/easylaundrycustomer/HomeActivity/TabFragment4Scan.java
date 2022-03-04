package com.inocen.easylaundrycustomer.HomeActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.inocen.easylaundrycustomer.LoginActivity;
import com.inocen.easylaundrycustomer.R;
import com.inocen.easylaundrycustomer.RegisterActivity;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TabFragment4Scan extends Fragment {

    private Activity activity;
    String notaId = "";
    String notaIdRak = "";
    String urlRak = "https://easylaundry-2c69e.firebaseapp.com/api/detail-nota/";
    String url = "https://easylaundry-2c69e.firebaseapp.com/api/scan-outlet";
    String urlTerima = "https://easylaundry-2c69e.firebaseapp.com/api/scan-outlet-terima";
    private final int MAX_RETRY = 5;
    private final int MAX_LENGTH = 30;
    RadioButton radioButtonTerima, radioButtonPelunasan, radioButtonCekRak;
    RadioGroup radioGroup;
    EditText editTextNoRak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_4_scan, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        activity = getActivity();
        setButtonClick();
    }

    private void setButtonClick() {
        radioGroup = activity.findViewById(R.id.radioGroup);
        radioButtonTerima = activity.findViewById(R.id.radioTerima);
        radioButtonPelunasan = activity.findViewById(R.id.radioPelunasan);
        radioButtonCekRak = activity.findViewById(R.id.radioCekRak);
        editTextNoRak = activity.findViewById(R.id.editTextNomorRak);
        final LinearLayout linearLayout = activity.findViewById(R.id.linearlayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioButtonTerima.getId() == checkedId) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button buttonActivateCamera, buttonInputManual, buttonCekRak;
        buttonActivateCamera = activity.findViewById(R.id.buttonActivateCamera);
        buttonActivateCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tombolTerima()) return;
                IntentIntegrator.forSupportFragment(TabFragment4Scan.this).setOrientationLocked(false).initiateScan();
            }
        });
        buttonInputManual = activity.findViewById(R.id.buttonInputManual);
        buttonInputManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tombolTerima()) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.dialog_title);

                // Set up the input
                final EditText input = new EditText(activity);
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
                input.setFilters(FilterArray);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notaId = input.getText().toString();
                        scanNotaLunas();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private boolean tombolTerima() {
        if (radioButtonTerima.isChecked()) {
            if (editTextNoRak.getText().toString().isEmpty()) {
                Toast.makeText(activity, "Isi nomor rak terlebih dahulu", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    private void scanNotaTerima() {
        activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.VISIBLE);
        //if everything is fine
        StringRequest stringRequestNota = new StringRequest(Request.Method.PUT, urlTerima,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
                        try {
                            //converting response to json object
                            Log.e("GET", notaId);
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "nota id tidak ditemukan", Toast.LENGTH_SHORT).show();
                            activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                        }
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            ImageView imageViewQrCode = activity.findViewById(R.id.imageViewTab4);
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(notaId, BarcodeFormat.QR_CODE, 250, 250);
                            imageViewQrCode.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                DateTime temp = new DateTime();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                String sekarang = temp.toString(dateTimeFormatter);
                Map<String, String> params = new HashMap<>();
                params.put("token", HomeActivity.currentUser.token);
                params.put("nota", notaId);
                params.put("posisiRak", editTextNoRak.getText().toString());
                params.put("username", HomeActivity.currentUser.username);
                params.put("employee", HomeActivity.currentUser.kode);
                params.put("waktu", sekarang);
                return params;
            }
        };
        Volley.newRequestQueue(activity).add(stringRequestNota)
                .setRetryPolicy(new DefaultRetryPolicy(10000, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void scanNotaLunas() {
        if (radioButtonTerima.isChecked()) {
            scanNotaTerima();
            return;
        }
        if (radioButtonCekRak.isChecked()) {
            getNotaRak();
            return;
        }
        activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.VISIBLE);
        //if everything is fine
        StringRequest stringRequestNota = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                        } catch (JSONException e) {
                            Toast.makeText(activity, "nota id tidak ditemukan", Toast.LENGTH_SHORT).show();
                            activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                        }
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            ImageView imageViewQrCode = activity.findViewById(R.id.imageViewTab4);
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(notaId, BarcodeFormat.QR_CODE, 250, 250);
                            imageViewQrCode.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                DateTime temp = new DateTime();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                String sekarang = temp.toString(dateTimeFormatter);
                Map<String, String> params = new HashMap<>();
                params.put("token", HomeActivity.currentUser.token);
                params.put("transaksi", notaId);
                params.put("waktu", sekarang);
                return params;
            }
        };
        Volley.newRequestQueue(activity).add(stringRequestNota)
                .setRetryPolicy(new DefaultRetryPolicy(10000, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getNotaRak() {
        activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("nota/"+notaId);
        databaseReference
                .addListenerForSingleValueEvent
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(final DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.exists()) {
                                     AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                     builder.setTitle(notaId);

                                     // Set up the input
                                     final TextView input = new TextView(activity);
                                     String nomorRak = dataSnapshot.child("posisi rak").getValue().toString();
                                     String text = "Nomor rak: " + nomorRak;

                                     input.setGravity(Gravity.CENTER);
                                     input.setText(text);
                                     input.setTypeface(ResourcesCompat.getFont(activity, R.font.lato_medium));
                                     input.setTextSize(20);
                                     builder.setView(input);

                                     // Set up the buttons
                                     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                         }
                                     });

                                     builder.show();
                                     activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                                 activity.findViewById(R.id.loading_circle_bar_tab4).setVisibility(View.INVISIBLE);
                             }
                         }
                        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    ImageView imageViewQrCode = activity.findViewById(R.id.imageView);
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(result.getContents(), BarcodeFormat.QR_CODE, 250, 250);
                    imageViewQrCode.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notaId = result.getContents();
                scanNotaLunas();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
