package com.example.eventmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_screen extends AppCompatActivity {


    EditText textEmail, setPasswordLayout;
    ImageView imageViewBack;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        Button loginBtn;
        TextView txtforgot, txtsignup;

        setContentView(R.layout.activity_login_screen);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        textEmail = (findViewById(R.id.Textemail));
        setPasswordLayout = (findViewById(R.id.pswd));
        loginBtn = findViewById(R.id.loginBtn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textEmail.getText().toString();
                String password = setPasswordLayout.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    textEmail.setError("Enter email");
                    textEmail.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    setPasswordLayout.setError("Enter password");
                    setPasswordLayout.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String currentUserUid = user.getUid();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Admin Data").child(currentUserUid);
                                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.hasChild("owner_cnic")) {
                                                    Toast.makeText(login_screen.this, "Admin login Successful.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Home_Screen.class);
                                                    progressBar.setVisibility(View.GONE);
                                                    startActivity(intent);

                                                } else {
                                                    Toast.makeText(login_screen.this, "User login Successful.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Home_Screen.class);
                                                    startActivity(intent);
                                                }
                                                finish();

                                            } else {
                                                Toast.makeText(login_screen.this, "User not found", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(login_screen.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    Toast.makeText(login_screen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }


                        });

            }
        });


        txtforgot =

                findViewById(R.id.forgotpassword);

        Intent aNext;
        aNext = new

                Intent(login_screen.this, Reset_Password.class);
        txtforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(aNext);
                //finish();

            }
        });

        txtsignup = findViewById(R.id.textViewsignup);


        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bNext;
                bNext = new

                        Intent(login_screen.this, user_admin.class);
                startActivity(bNext);
                finish();

            }
        });


        imageViewBack =

                findViewById(R.id.arrowLeft);

        Intent cNext;
        cNext = new

                Intent(login_screen.this, user_admin.class);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(cNext);
                finish();

            }
        });


    }
    /*
    public void emailValidator(EditText textEmail) {

        String emailToText = textEmail.getText().toString();

        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            Toast.makeText(this, "Email Verified !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Enter valid Email address !", Toast.LENGTH_SHORT).show();
        }

    }

     */
}