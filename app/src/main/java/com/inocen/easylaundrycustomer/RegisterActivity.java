package com.inocen.easylaundrycustomer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String url = "https://easylaundry-2c69e.firebaseapp.com/api/register-user-outlet";
    private FirebaseAuth mAuth;
    private final String TAG = "Register";
    private String tempKota = "", tempPosisi = "", kode;
    private EditText editTextUsername, editTextEmail, editTextPasswordConfirmation, editTextPassword;
    private TextView textViewGenerateKode;
    private Spinner spinnerNamaOutlet, spinnerKota;
    ArrayList<String> listKota = new ArrayList<>();
    ArrayList<String> listOutlet = new ArrayList<>();
    ArrayList<String> listKodeOutlet = new ArrayList<>();
    private final int MAX_RETRY=10;
    private final int TIMEOUT=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle bundle = getIntent().getExtras().getBundle("bundle");
        listKota = bundle.getStringArrayList("list");
        listOutlet.add("Pilih outlet");
        listKodeOutlet.add("X");

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        registerFunction();
    }

    /**
     * Register using firebase
     * success go to MenuActivity
     * failed show toast
     */
    public void registerFunction() {
        editTextUsername = findViewById(R.id.editTextNama);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = findViewById(R.id.editTextKonfirmasiPassword);
        spinnerKota = findViewById(R.id.spinnerKota);
        ArrayAdapter<String> adapterKota = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listKota);
        adapterKota.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKota.setAdapter(adapterKota);
        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listOutlet.clear();
                listKodeOutlet.clear();
                getOutletInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerNamaOutlet = findViewById(R.id.spinnerNamaOutlet);
        ArrayAdapter<String> adapterOutlet = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listOutlet);
        adapterOutlet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNamaOutlet.setAdapter(adapterOutlet);
        spinnerNamaOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    kode = listKodeOutlet.get(position-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerNamaOutlet.setEnabled(false);

        Button buatAkun = findViewById(R.id.buttonBuatAkun);
        buatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createUserInDatabase();
                createUserInDatabaseAPI();
            }
        });
//        Button buttonLihatAkun = findViewById(R.id.buttonLihatDaftarAkun);
//        buttonLihatAkun.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }

    private void getOutletInfo() {
        findViewById(R.id.loading_circle_bar_register).setVisibility(View.VISIBLE);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("outlet/");
        databaseReference
                .addValueEventListener
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(final DataSnapshot dataSnapshot) {
                                 listOutlet.add("Pilih outlet");
                                 if (dataSnapshot.exists()) {
                                     for (DataSnapshot child : dataSnapshot.getChildren()) {
                                         String kota = child.child("kota").getValue().toString();
                                         if (kota.equals(spinnerKota.getSelectedItem().toString())) {
                                             listKodeOutlet.add(child.getKey());
                                             listOutlet.add(child.child("nama outlet").getValue().toString());
                                         }
                                         spinnerNamaOutlet.setEnabled(true);
                                         spinnerNamaOutlet.setSelection(0);
                                         findViewById(R.id.loading_circle_bar_register).setVisibility(View.INVISIBLE);
                                     }
                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                                 findViewById(R.id.loading_circle_bar_register).setVisibility(View.INVISIBLE);
                                 System.out.println("The read failed: " + databaseError.getCode());
                             }
                         }
                        );
    }

    private void createUserInDatabaseAPI() {
        String username = editTextUsername.getText().toString().trim();
        String kota = spinnerKota.getSelectedItem().toString();
        String posisi = spinnerNamaOutlet.getSelectedItem().toString();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirmation = editTextPasswordConfirmation.getText().toString().trim();

        //creating user in firebase database, checking empty values
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || kota.isEmpty() || posisi.isEmpty())
            Toast.makeText(RegisterActivity.this, getString(R.string.fill_data_login),
                    Toast.LENGTH_SHORT).show();
        else if (password.length() < 6)
            Toast.makeText(RegisterActivity.this, "password minimal 6 huruf/angka",
                    Toast.LENGTH_SHORT).show();
        else if (spinnerNamaOutlet.getSelectedItem().toString().equals("Pilih outlet"))
            Toast.makeText(RegisterActivity.this, "Outlet kota belum dipilih",
                    Toast.LENGTH_SHORT).show();
        else if (password.equals(passwordConfirmation)) {
            findViewById(R.id.loading_circle_bar_register).setVisibility(View.VISIBLE);
            //if everything is fine
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject obj = new JSONObject(response);
                                Toast.makeText(RegisterActivity.this, obj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
//                                findViewById(R.id.loading_circle_bar_register).setVisibility(View.INVISIBLE);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.loading_circle_bar_register).setVisibility(View.INVISIBLE);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", editTextEmail.getText().toString().trim());
                    params.put("kota", spinnerKota.getSelectedItem().toString());
                    params.put("namaOutlet", spinnerNamaOutlet.getSelectedItem().toString());
                    params.put("kodeOutlet", kode);
                    params.put("password", editTextPassword.getText().toString().trim());
                    params.put("posisi", "Outlet");
                    params.put("username", editTextUsername.getText().toString().trim());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(stringRequest)
                    .setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else Toast.makeText(RegisterActivity.this, getString(R.string.password_mismatch),
                Toast.LENGTH_SHORT).show();
    }

}
