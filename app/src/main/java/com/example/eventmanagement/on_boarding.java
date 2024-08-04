package com.example.eventmanagement;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class on_boarding extends AppCompatActivity {
    ViewPager viewPager;
    slider_Adapter adapter;
    MaterialButton button;
    private Handler handler;
    private int currentPage = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_on_boarding);


        viewPager = findViewById(R.id.viewpager);
        adapter = new slider_Adapter(this);
        viewPager.setAdapter(adapter);


        viewPager = findViewById(R.id.viewpager);
        button = findViewById(R.id.nextButton);



        adapter = new slider_Adapter(this);
        viewPager.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("onboardingCompleted", true);
                editor.apply();

                int nextIndex = currentPage + 1;
                if (nextIndex < adapter.getCount()) {
                    viewPager.setCurrentItem(nextIndex, true);
                }
                Intent bNext;
                bNext = new Intent(on_boarding.this, user_admin.class);
                startActivity(bNext);
                finish();
            }
        });

        handler.postDelayed(autoScrollRunnable, 5000);

    }


    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            // Auto-scroll to the next page after 2 seconds
            int nextPage = (currentPage + 1) % adapter.getCount();
            viewPager.setCurrentItem(nextPage, true);
            handler.postDelayed(this, 5000);
        }
    };

}