package com.example.eventmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class user_admin extends AppCompatActivity {
    MaterialButton buttonAdminSignUp, buttonUserSignUp;
    TextView textViewLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_admin);


        buttonAdminSignUp = findViewById(R.id.adminSignupBtn);
        buttonUserSignUp = findViewById(R.id.userSignuoBtn);
        textViewLogin = findViewById(R.id.textLogin);


        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bNext;
                bNext = new Intent(user_admin.this, login_screen.class);
                startActivity(bNext);
            }
        });


        buttonAdminSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cNext;
                cNext = new Intent(user_admin.this, signup.class);
                startActivity(cNext);
            }
        });


        buttonUserSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eNext;
                eNext = new Intent(user_admin.this, User_Signup.class);
                startActivity(eNext);
            }
        });



    }
}