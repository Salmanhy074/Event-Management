package com.example.eventmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.button.MaterialButton;

public class slider_Adapter extends PagerAdapter {
    MaterialButton button;

    Context context;
    LayoutInflater inflater;
    public int[] list_img = {
            R.drawable.todo_image,
            R.drawable.time_line,
            R.drawable.meeting,
    };

    public String[] list_title = {
            "Create Event",
            "Manage Timeline",
            "Manage your Meetings",
    };

    public String[] list_description = {
            "You can create events and manage them.",
            "You can easily manage your timeline by using this app.",
            "You can manage your meetings and set reminder by using this app.",
    };


    public slider_Adapter(Context context){
        this.context = context;


    }


    @Override
    public int getCount() {
        return list_title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout)object);

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        //LinearLayout linearLayout = view.findViewById(R.id.slider_Layout);
        ImageView imageView = view.findViewById(R.id.slide_image);
        TextView heading_text = view.findViewById(R.id.slide_heading);
        TextView description_text = view.findViewById(R.id.slider_des);
        //linearLayout.setBackgroundColor(list_bg_color[position]);
        imageView.setImageResource(list_img[position]);
        heading_text.setText(list_title[position]);
        description_text.setText(list_description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

}
