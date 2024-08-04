package com.example.eventmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<ChatModel> mChat;
    Context context;
    FirebaseUser fUser;

    public ChatAdapter(Context context, List<ChatModel> mChat) {
        this.context = context;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public ChatAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);
            return new ChatAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);
            return new ChatAdapter.ItemViewHolder(view);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ItemViewHolder holder, int position) {
        ChatModel chatModel = mChat.get(position);
        holder.show_message.setText(chatModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView show_message;

        public ItemViewHolder(View view) {
            super(view);
            show_message = itemView.findViewById(R.id.txtMessage);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}