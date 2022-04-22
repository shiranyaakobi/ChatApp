package com.example.chatscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {


    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        holder.messageContentTv.setText(message.getMessageContent());
        holder.messageDateTv.setText(DateUtils.getDateStringFromTimeMillis(message.getDate()));

        // load image to message item
        Picasso.get().load(message.getSenderImage()).into(holder.senderImageView);


        // set user name for message item
        holder.pNameTv.setText(message.getSenderName());

        // change direction for foreign users
        if (message.getUid().equals(FirebaseAuth.getInstance().getUid())) {
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void reloadMessages(List<ChatMessage> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    class ChatViewHolder extends RecyclerView.ViewHolder {


        ImageView senderImageView;
        TextView messageContentTv, messageDateTv, pNameTv;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImageView = itemView.findViewById(R.id.message_image);
            messageContentTv = itemView.findViewById(R.id.message_content);
            messageDateTv = itemView.findViewById(R.id.message_date);
            pNameTv = itemView.findViewById(R.id.pNameTv);
        }

    }
}
