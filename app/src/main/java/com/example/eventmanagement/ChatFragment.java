package com.example.eventmanagement;

import static com.example.eventmanagement.Home_Screen.bottomAppBar;
import static com.example.eventmanagement.Home_Screen.bottomNavigationView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ChatFragment extends Fragment {

    TextView tvReceiverName;
    ImageView backImage, sendButton;
    RecyclerView rvUserChat;
    ChatAdapter messageAdapter;
    EditText messageInputEditText;
    List<ChatModel> mChat;
    String msg;


    private String userId;
    private String userName;
    private String userEmail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        //open chat on click
        boolean getArguments = false;
        if (false) {
            userId = getArguments().getString("userId");
            userName = getArguments().getString("userName");
            userEmail = getArguments().getString("userEmail");
        }

        TextView chatTitle = view.findViewById(R.id.tvReceiverName);
        chatTitle.setText("Chat with " + userName);
        //open chat on click

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvReceiverName = view.findViewById(R.id.tvReceiverName);
        backImage = view.findViewById(R.id.backImage);
        rvUserChat = view.findViewById(R.id.rvUserChat);
        messageInputEditText = view.findViewById(R.id.messageInputEditText);
        sendButton = view.findViewById(R.id.sendButton);

        rvUserChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvUserChat.setLayoutManager(linearLayoutManager);

        if (Constant.data.getOwnerName() != null) {
            tvReceiverName.setText(Constant.data.getOwnerName());
        } else {
            tvReceiverName.setText("Name");
        }

        String receiverEmail = Constant.data.getOwnerEmail();
        String senderUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchReceiverUUID(receiverEmail, new ReceiverIdCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReceiverId(String receiverUserId) {
                if (receiverUserId != null) {
                    readMessages(getContext(), senderUUID, receiverUserId);

                    // Add notification sending logic here
                    //NotificationHelper.sendNotification(getContext(), "New Message", msg);

                }
            }
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomeFragment();
                bottomAppBar.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.notification);
            }
        });


        setupSendButtonVisibility();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = messageInputEditText.getText().toString().trim();

                if (msg.isEmpty()) {
                    Toast.makeText(getContext(), "Please type a message", Toast.LENGTH_SHORT).show();
                } else {
                    fetchReceiverUUID(receiverEmail, new ReceiverIdCallback() {
                        @Override
                        public void onReceiverId(String receiverUserId) {
                            if (receiverUserId != null) {
                                sendMessage(senderUUID, receiverUserId, msg);
                                readMessages(getContext(), senderUUID, receiverUserId);

                            }
                        }
                    });
                }
            }

        });


       /* sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageInputEditText.getText().toString().trim();

                if (msg.isEmpty()) {
                    Toast.makeText(getContext(), "Please type a message", Toast.LENGTH_SHORT).show();
                } else {
                    fetchReceiverUUID(receiverEmail, new ReceiverIdCallback() {
                        @Override
                        public void onReceiverId(String receiverUserId) {
                            if (receiverUserId != null) {
                                sendMessage(senderUUID, receiverUserId, msg);
                                readMessages(getContext(), senderUUID, receiverUserId);
                            }

                        }
                    });
                }
            }
        });*/

    }

    private void setupSendButtonVisibility() {
        // Define a TextWatcher for the EditText
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    sendButton.setVisibility(View.GONE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                }
            }
        };

        messageInputEditText.addTextChangedListener(textWatcher);

        if (messageInputEditText.getText().toString().trim().isEmpty()) {
            sendButton.setVisibility(View.GONE);
        } else {
            sendButton.setVisibility(View.VISIBLE);
        }
    }


    private void loadHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


   /* private void readMessages(Context context, String senderUUID, String receiverUserId) {
        mChat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(senderUUID) && chat.getSender().equals(receiverUserId) ||
                            chat.getReceiver().equals(receiverUserId) && chat.getSender().equals(senderUUID)) {
                        mChat.add(chat);

                    }

                    rvUserChat.scrollToPosition(mChat.size() - 1);
                    messageAdapter = new ChatAdapter(context, mChat);
                    rvUserChat.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
*/

    private void readMessages(Context context, String senderUUID, String receiverUserId) {
        mChat = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatModel chat = dataSnapshot.getValue(ChatModel.class);

                if (chat != null && (
                        (chat.getReceiver().equals(senderUUID) && chat.getSender().equals(receiverUserId)) ||
                                (chat.getReceiver().equals(receiverUserId) && chat.getSender().equals(senderUUID)))) {
                    mChat.add(chat);
                    // Notify adapter about the new message
                    if (messageAdapter == null) {
                        messageAdapter = new ChatAdapter(context, mChat);
                        rvUserChat.setAdapter(messageAdapter);

                    } else {
                        messageAdapter.notifyDataSetChanged();
                    }
                    // Scroll to the last message
                    rvUserChat.scrollToPosition(mChat.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });



        /*reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(senderUUID) && chat.getSender().equals(receiverUserId) ||
                            chat.getReceiver().equals(receiverUserId) && chat.getSender().equals(senderUUID)) {
                        mChat.add(chat);

                    }

                    rvUserChat.scrollToPosition(mChat.size() - 1);
                    messageAdapter = new ChatAdapter(context, mChat);
                    rvUserChat.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/

    }







    private void sendMessage(String senderUUID, String receiverUserId, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderUUID);
        hashMap.put("receiver", receiverUserId);
        hashMap.put("message", msg);

        reference.child("chat").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    messageInputEditText.setText("");

                }
            }
        });

    }

    private void fetchReceiverUUID(String email, ReceiverIdCallback receiverIdCallback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Admin Data");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String receiverUserId = null;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    if (userEmail != null && userEmail.equals(email)) {
                        receiverUserId = userSnapshot.getKey();
                        break;
                    }
                }
                receiverIdCallback.onReceiverId(receiverUserId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}