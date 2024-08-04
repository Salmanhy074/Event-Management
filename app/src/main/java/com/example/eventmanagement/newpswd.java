package com.example.eventmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class newpswd extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpswd);


        TextView txtsignup;
        Button newpswdBtn;

        txtsignup = findViewById(R.id.textViewsignup);
        Intent bNext;
        bNext = new Intent(newpswd.this, signup.class);
        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(bNext);

            }
        });


        newpswdBtn = findViewById(R.id.newpasswordbtn);
        Intent cNext;
        cNext = new Intent(newpswd.this, login_screen.class);
        newpswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(cNext);

            }
        });

    }
}