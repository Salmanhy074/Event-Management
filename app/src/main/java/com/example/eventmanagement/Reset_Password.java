package com.example.eventmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_Password extends AppCompatActivity {
    private EditText emailEditText;
    private FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    public TextView txtsignup, txtLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        progressBar = findViewById(R.id.progress_circular);
        firebaseAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.editEmail);
        txtLogin = findViewById(R.id.textLogin);
        MaterialButton button = findViewById(R.id.resetpasswordbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
                //Intent intent = new Intent(Reset_Password.this, login_screen.class);
                //startActivity(intent);

            }
        });

        txtsignup = findViewById(R.id.textViewsignup);
        Intent bNext;
        bNext = new Intent(Reset_Password.this, signup .class);
        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(bNext);

            }
        });


        Intent dNext;
        dNext = new Intent(Reset_Password.this, login_screen .class);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(dNext);

            }
        });


    }


    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed, but must be implemented
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Hide the error message when the user starts typing in the email EditText
                emailEditText.setError(null);
            }
        });


        if (email.isEmpty()) {

            // Email field is empty, show error message
            emailEditText.setError("Email is required");
            emailEditText.requestFocus(); // Set focus to the email EditText
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar regardless of the outcome
                        if (task.isSuccessful()) {
                            Toast.makeText(Reset_Password.this,
                                    "Password reset email sent. Check your inbox.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Reset_Password.this,
                                    "Failed to send password reset email. Please check your email address.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Reset_Password", "Failed to send password reset email.", task.getException());
                        }
                    }
                });
    }


}