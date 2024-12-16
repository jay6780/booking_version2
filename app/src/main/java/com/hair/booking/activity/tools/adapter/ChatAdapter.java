package com.hair.booking.activity.tools.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hair.booking.activity.tools.Model.ChatMessage;
import com.hair.booking.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;
    private String currentUserId;

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUserId) {
        this.chatMessages = chatMessages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.messageTextView.setText(message.getMessage());
        if (message.getSenderId().equals(currentUserId)) {
            holder.messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            holder.messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
