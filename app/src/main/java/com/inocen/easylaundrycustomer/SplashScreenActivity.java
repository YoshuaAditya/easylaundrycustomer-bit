package com.inocen.easylaundrycustomer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashSet;

public class SplashScreenActivity extends AppCompatActivity {

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
    boolean finished=false;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.loading_circle_bar_splash).setVisibility(View.VISIBLE);
                    }
                }
            , 1000);
        startApp();
    }

    public void startApp() {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(SplashScreenActivity.this);
        final boolean notFirstTime = sharedPrefHelper.getSharedPref(getPackageName(), false);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (notFirstTime)
                    loginCheck();
                else{
                    startActivity(new Intent(SplashScreenActivity.this, ViewPagerActivity.class));
                    SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(SplashScreenActivity.this);
                    sharedPrefHelper.putSharedPref(getPackageName() + sharedPrefHelper.set, new HashSet<String>());
                    finish();
                }
            }
        }, secondsDelayed * 1000);
    }

    private void loginCheck() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            getUserInfo();
        }
        else {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        }
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
                                             startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                                             CurrentUser currentUser=new CurrentUser(token,email,kode,kodeOutlet,namaOutlet,kota,posisi,username,user.getUid(),listNotaUser,finished);
                                             HomeActivity.currentUser=currentUser;
                                             finish();
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
}
