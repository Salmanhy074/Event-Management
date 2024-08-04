package com.example.eventmanagement;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    public static ArrayList<UserData> eventsArray = new ArrayList<>();
    public static ArrayList<UserData> privateEventsArray = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";
    private static final String PREF_ONBOARDING_COMPLETED = "onboardingCompleted";


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        fetchPrivateDataFromFirebase(user);
        fetchDataFromFirebase();
        showNoInternetDialog();


    }

    private void navigateToNext() {
        boolean onboardingCompleted = sharedPreferences.getBoolean(PREF_ONBOARDING_COMPLETED, false);

        if (user != null) {
            startActivity(new Intent(MainActivity.this, Home_Screen.class));
            finish();
        } else {
            if (onboardingCompleted) {
                startActivity(new Intent(MainActivity.this, login_screen.class));
                finish();

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, on_boarding.class));
                        finish();
                    }
                }, 2000);
            }

        }
    }


    private void fetchDataFromFirebase() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Events");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uniqueKey = snapshot.getKey();

                        databaseReference.child(uniqueKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot itemsData : snapshot.getChildren()) {
                                    UserData eventsModel = itemsData.getValue(UserData.class);
                                    eventsArray.add(eventsModel);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        navigateToNext();

                    }
                } else {
                    navigateToNext();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("FirebaseData", "Failed to read value.", error.toException());
            }
        });
    }


    private void fetchPrivateDataFromFirebase(FirebaseUser user) {
        if (user != null) {
            String UUID = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin Data").child(UUID);
            databaseReference.child("Event Data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserData userData = snapshot.getValue(UserData.class);
                        privateEventsArray.add(userData);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("FirebaseData", "Failed to read value.", error.toException());
                }
            });
        }
    }


    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private void showNoInternetDialog() {
        if (!isInternetConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.network_dialog, null);
            builder.setView(dialogView);

            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Set background to transparent

            MaterialButton btnRetry = dialogView.findViewById(R.id.retryButton);
            ProgressBar progressBar = dialogView.findViewById(R.id.progress_circular);

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Hide progress bar
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 4000);
                }
            });

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

}