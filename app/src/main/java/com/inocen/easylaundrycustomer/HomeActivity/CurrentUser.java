package com.inocen.easylaundrycustomer.HomeActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CurrentUser {
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

    public CurrentUser(String token, String email, String kode, String kodeOutlet, String namaOutlet, String kota, String posisi, String username, String uid, ArrayList<String> listNotaUser, boolean finished) {
        this.token = token;
        this.email = email;
        this.kode = kode;
        this.kodeOutlet = kodeOutlet;
        this.namaOutlet = namaOutlet;
        this.kota = kota;
        this.posisi = posisi;
        this.username = username;
        this.uid = uid;
        this.listNotaUser = listNotaUser;
        this.finished = finished;
    }

    public CurrentUser() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        uid=firebaseUser.getUid();
        DatabaseReference databaseReference = database.getReference("users/" + firebaseUser.getUid());
        databaseReference
                .addValueEventListener
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.exists()) {
                                     firebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                         @Override
                                         public void onSuccess(GetTokenResult result) {
                                             token = result.getToken();
                                         }
                                     });;
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
