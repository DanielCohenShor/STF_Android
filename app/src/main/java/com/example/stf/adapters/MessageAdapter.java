package com.example.stf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stf.R;
import com.example.stf.entities.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;

        private final TextView tvMessageTime;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }
    }

    static class ContactMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;

        private final TextView tvMessageTime;

        public ContactMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }
    }

    static class DateBubbleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;

        public DateBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    private static final int VIEW_TYPE_1 = 1;
    private static final int VIEW_TYPE_2 = 2;
    private static final int VIEW_TYPE_3 = 3;


    private final LayoutInflater mInflater;

    private Message[] messages;

    private final String currentUserUsername;

    public MessageAdapter(Context context, Message[] messages, String currentUserUsername) {
        this.mInflater = LayoutInflater.from(context);
        this.messages = messages;
        this.currentUserUsername = currentUserUsername;
    }

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on the position or data at that position
        // You can implement your logic here to determine the view type for each item
        // For example:
        if (!Objects.equals(messages[position].getSender().getUsername(), currentUserUsername)) {
            return VIEW_TYPE_2;
        } else {
            return VIEW_TYPE_1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the appropriate layout based on the view type
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_1:
                view = inflater.inflate(R.layout.my_message, parent, false);
                return new MyMessageViewHolder(view);
            case VIEW_TYPE_2:
                view = inflater.inflate(R.layout.contact_message, parent, false);
                return new ContactMessageViewHolder(view);
            case VIEW_TYPE_3:
                view = inflater.inflate(R.layout.date_bubble, parent, false);
                return new DateBubbleViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind data to the appropriate view holder based on the view type
        if (messages != null) {
            final Message currentMessage = messages[position];
            String createdDateString = currentMessage.getCreated();
            SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_1:
                    MyMessageViewHolder myMessageViewHolder = (MyMessageViewHolder) holder;
                    // Bind data for ViewHolder1
                    myMessageViewHolder.tvContent.setText(currentMessage.getContent());

                    try {
                        Date createdTime = inputFormat.parse(createdDateString);
                        String time = timeFormat.format(createdTime);
                        myMessageViewHolder.tvMessageTime.setText(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
                case VIEW_TYPE_2:
                    ContactMessageViewHolder contactMessageViewHolder = (ContactMessageViewHolder) holder;
                    // Bind data for ViewHolder2
                    contactMessageViewHolder.tvContent.setText(currentMessage.getContent());

                    try {
                        Date createdTime = inputFormat.parse(createdDateString);
                        String time = timeFormat.format(createdTime);
                        contactMessageViewHolder.tvMessageTime.setText(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case VIEW_TYPE_3:
                    DateBubbleViewHolder dateBubbleViewHolder = (DateBubbleViewHolder) holder;
                    // Bind data for ViewHolder3
                    dateBubbleViewHolder.tvDate.setText("");
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.length;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public Message[] getMessages() {
        return messages;
    }
}
