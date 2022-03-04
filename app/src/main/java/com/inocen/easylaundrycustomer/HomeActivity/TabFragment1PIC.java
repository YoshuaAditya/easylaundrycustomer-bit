package com.inocen.easylaundrycustomer.HomeActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.inocen.easylaundrycustomer.LoginActivity;
import com.inocen.easylaundrycustomer.R;
import com.inocen.easylaundrycustomer.RegisterActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TabFragment1PIC extends Fragment {
    Bundle bundleHargaSatuan = null;
    private Activity activity;
    private ConstraintLayout buttonLogout, buttonRaporHarian, buttonRaporBulanan;
    private final int MAX_LENGTH = 30;
    DateTimeFormatter tglHarian = DateTimeFormat.forPattern("yyyy-MMM-dd");
    DateTimeFormatter tglBulanan = DateTimeFormat.forPattern("yyyy-MMM");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1_pic, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        activity = getActivity();

//        Toast.makeText(activity,""+bundleHargaSatuan.getInt("Blazer"),Toast.LENGTH_SHORT).show();
        setDataOutlet();
        setButtonClick();
    }

    private void setDataOutlet() {
        TextView textViewNama,textViewKode,textViewNamaOutlet,textViewKodeOutlet;
        textViewNama=activity.findViewById(R.id.textViewNamaPIC);
        textViewKode=activity.findViewById(R.id.textViewKodePegawaiPIC);
        textViewNamaOutlet=activity.findViewById(R.id.textViewNamaOutletPIC);
        textViewKodeOutlet=activity.findViewById(R.id.textViewKodeOutletPIC);
        textViewNama.setText(HomeActivity.currentUser.username);
        textViewKode.setText(HomeActivity.currentUser.kode);
        textViewNamaOutlet.setText(HomeActivity.currentUser.namaOutlet);
        textViewKodeOutlet.setText(HomeActivity.currentUser.kodeOutlet);
    }

    private void setButtonClick() {
        buttonRaporHarian = activity.findViewById(R.id.buttonRaporHarian);
        buttonRaporHarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupRapor(1);
            }
        });
        buttonRaporBulanan = activity.findViewById(R.id.buttonRaporBulanan);
        buttonRaporBulanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupRapor(2);
            }
        });
        buttonLogout = activity.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            }
        });
    }

    private void showPopupRapor(int i) {
        activity.findViewById(R.id.loading_circle_bar_tab1).setVisibility(View.VISIBLE);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DateTime dateTime = new DateTime();
        String waktu="",by="",rapor="",jumlah="";
        switch (i){
            case 1://harian
                waktu = dateTime.toString(tglHarian);
                by="byDay";
                rapor="Rapor Harian";
                jumlah="Jumlah order hari ini: ";
                break;
            case 2://bulanan
                waktu = dateTime.toString(tglBulanan);
                by="byMonth";
                rapor="Rapor Bulanan";
                jumlah="Jumlah order bulan ini: ";
                break;
        }

        final Query query = database.getReference("users/" + firebaseUser.getUid() + "/kodeNota")
                .orderByChild(by).equalTo(waktu);
        final String finalRapor = rapor;
        final String finalJumlah = jumlah;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("test ", dataSnapshot.getChildrenCount() + "");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(finalRapor);

                // Set up the input
                final TextView input = new TextView(activity);
                String text = finalJumlah + dataSnapshot.getChildrenCount();

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
                activity.findViewById(R.id.loading_circle_bar_tab1).setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
