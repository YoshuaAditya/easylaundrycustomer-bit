package com.inocen.easylaundrycustomer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inocen.easylaundrycustomer.HomeActivity.CurrentUser;
import com.inocen.easylaundrycustomer.HomeActivity.HomeActivity;
import com.inocen.easylaundrycustomer.ViewPagerActivity.ViewPagerActivity;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "Register";
    private String tempKota="", tempPosisi="";
    private EditText editTextUsername, editTextEmail, editTextPasswordConfirmation, editTextPassword;
    private Button login;
    String token;
    String email;
    String kode;
    String kodeOutlet;
    String namaOutlet;
    String kota;
    String posisi;
    String username;
    String uid;
    ArrayList<String> listNotaUser=new ArrayList<>();
    ArrayList<String> listKota=new ArrayList<>();
    boolean finished=false;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

        loginFunction();
        buttonClickRegister();

    }

    public void buttonClickRegister() {
        Button create = findViewById(R.id.buttonDaftarBaru);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getKotaOutletInfo();
            }
        });
    }

    private void getKotaOutletInfo() {
        findViewById(R.id.loading_circle_bar_login).setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Kota/");
        databaseReference
                .addValueEventListener
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(final DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.exists()) {
                                     for(DataSnapshot child:dataSnapshot.getChildren()){
                                         listKota.add(dataSnapshot.child(child.getKey()).child("nama").getValue().toString());
                                     }
                                     findViewById(R.id.loading_circle_bar_login).setVisibility(View.INVISIBLE);
                                     Bundle bundle=new Bundle();
                                     bundle.putStringArrayList("list",listKota);
                                     Intent i=new Intent(LoginActivity.this, RegisterActivity.class);
                                     i.putExtra("bundle",bundle);
                                     startActivity(i);
                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                                 findViewById(R.id.loading_circle_bar_login).setVisibility(View.INVISIBLE);
                             }
                         }
                        );
    }

    /**
     * Login using firebase
     * success go to main
     * failed show message
     */
    public void loginFunction() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        login = findViewById(R.id.buttonMasuk);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.fill_data_login),
                            Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                }
                else {
                    findViewById(R.id.loading_circle_bar_login).setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Login..." ,
                            Toast.LENGTH_SHORT).show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        user = mAuth.getCurrentUser();
                                        getUserInfo();
//                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    } else {
                                        // If sign in fails, display wordsArray message to the user.
                                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        login.setEnabled(true);
                                        findViewById(R.id.loading_circle_bar_login).setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void getUserInfo() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        uid=firebaseUser.getUid();
        DatabaseReference databaseReference = database.getReference("users/" + firebaseUser.getUid());
        databaseReference
                .addListenerForSingleValueEvent
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(final DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.exists()) {
                                     firebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                         @Override
                                         public void onSuccess(GetTokenResult result) {
                                             token = result.getToken();
                                             email = dataSnapshot.child("email").getValue().toString();
                                             kode = dataSnapshot.child("kode").getValue().toString();
                                             kodeOutlet = dataSnapshot.child("kodeOutlet").getValue().toString();
                                             namaOutlet = dataSnapshot.child("namaOutlet").getValue().toString();
                                             kota = dataSnapshot.child("kota").getValue().toString();
                                             posisi = dataSnapshot.child("posisi").getValue().toString();
                                             username = dataSnapshot.child("username").getValue().toString();
                                             finished=true;
                                             for(DataSnapshot child:dataSnapshot.child("kodeNota").getChildren()){
                                                 listNotaUser.add(child.getKey());
                                             }
                                             CurrentUser currentUser=new CurrentUser(token,email,kode,kodeOutlet,namaOutlet,kota,posisi,username,user.getUid(),listNotaUser,finished);
                                             HomeActivity.currentUser=currentUser;
                                             checkClaims(user);
                                         }
                                     });
                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                                 System.out.println("The read failed: " + databaseError.getCode());
                             }
                         }
                        );
    }

    private void checkClaims(final FirebaseUser user) {
        user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult result) {
                boolean isUserOutlet=false;
                try {
                   isUserOutlet = (boolean) result.getClaims().get("userOutlet");
                }catch (NullPointerException e){

                }
                if (isUserOutlet) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "???",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
