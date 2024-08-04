
package com.example.eventmanagement;

import static com.example.eventmanagement.Home_Screen.bottomAppBar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<UserModel> list;
    DatabaseReference databaseReference;
    ChatListAdapter chatListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.rvItems);
        databaseReference = FirebaseDatabase.getInstance().getReference("Admin Data");
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatListAdapter = new ChatListAdapter(getContext(), list, this::openChatFragment);
        recyclerView.setAdapter(chatListAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel admin = dataSnapshot.getValue(UserModel.class);
                    if (admin != null) {
                        list.add(admin);
                    } else {
                        Log.e("Firebase", "UserModel is null");
                    }
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });

        return view;
    }

    private void openChatFragment(UserModel user) {
        bottomAppBar.setVisibility(View.GONE);

        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("userId", user.getUserId());
        args.putString("userName", user.owner_name);
        args.putString("userEmail", user.email);
        chatFragment.setArguments(args);

        FragmentManager manager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, chatFragment);
        transaction.commit();
    }
}


/*
package com.example.eventmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    ImageView imgContact;
    RecyclerView recyclerView;
    ArrayList<UserModel> list;
    DatabaseReference databaseReference;
    ChatListAdapter chatListAdapter;
    //TextView txtReceiverName, txtReceiverEmail;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);




        recyclerView = view.findViewById(R.id.rvItems);
        databaseReference = FirebaseDatabase.getInstance().getReference("Admin Data");
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatListAdapter = new ChatListAdapter(getContext(), list);
        recyclerView.setAdapter(chatListAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserModel admin = dataSnapshot.getValue(UserModel.class);
                    if (admin != null) {
                        list.add(admin);
                    } else {
                        Log.e("Firebase", "SignupModel is null");
                    }
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });


        return view;
    }
}*/
