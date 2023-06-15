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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;

        private final TextView tvMessageTime;

        private final TextView tvDate;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    static class ContactMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;

        private final TextView tvMessageTime;

        private final TextView tvDate;

        public ContactMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    private static final int VIEW_TYPE_1 = 1;
    private static final int VIEW_TYPE_2 = 2;

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
        if (!Objects.equals(messages[position].getSender().getUsername(), currentUserUsername)) {
            return VIEW_TYPE_2;
        } else {
            return VIEW_TYPE_1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_1:
                view = mInflater.inflate(R.layout.my_message, parent, false);
                return new MyMessageViewHolder(view);
            case VIEW_TYPE_2:
                view = mInflater.inflate(R.layout.contact_message, parent, false);
                return new ContactMessageViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    // Method to check if two dates are the same day
    private boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int day1 = cal1.get(Calendar.DAY_OF_MONTH);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int day2 = cal2.get(Calendar.DAY_OF_MONTH);

        return (year1 == year2 && month1 == month2 && day1 == day2);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind data to the appropriate view holder based on the view type
        if (messages != null) {
            final Message currentMessage = messages[position];
            String createdDateString = currentMessage.getCreated();
            SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

            switch (holder.getItemViewType()) {
                case VIEW_TYPE_1:
                    MyMessageViewHolder myMessageViewHolder = (MyMessageViewHolder) holder;
                    // Bind data for ViewHolder1
                    myMessageViewHolder.tvContent.setText(currentMessage.getContent());

                    if (position != 0) {
                        final Message prevMessage = messages[position - 1];
                        String prevDateString = prevMessage.getCreated();
                        try {
                            Date myCreatedDate = inputFormat.parse(createdDateString);
                            Date prevCreatedDate = inputFormat.parse(prevDateString);
                            if (myCreatedDate != null && prevCreatedDate != null) {
                                // Check if the created date is the current date
                                if (isSameDate(myCreatedDate, prevCreatedDate)) {
                                    myMessageViewHolder.tvDate.setVisibility(View.GONE);
                                } else {
                                    String date = dateFormat.format(myCreatedDate);
                                    myMessageViewHolder.tvDate.setText(date);
                                }
                            }

                            String time = timeFormat.format(myCreatedDate);
                            myMessageViewHolder.tvMessageTime.setText(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Date myCreatedDate = inputFormat.parse(createdDateString);
                            String date = dateFormat.format(myCreatedDate);
                            myMessageViewHolder.tvDate.setText(date);
                            String time = timeFormat.format(myCreatedDate);
                            myMessageViewHolder.tvMessageTime.setText(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case VIEW_TYPE_2:
                    ContactMessageViewHolder contactMessageViewHolder = (ContactMessageViewHolder) holder;
                    // Bind data for ViewHolder1
                    contactMessageViewHolder.tvContent.setText(currentMessage.getContent());

                    if (position != 0) {
                        final Message prevMessage = messages[position - 1];
                        String prevDateString = prevMessage.getCreated();
                        try {
                            Date myCreatedDate = inputFormat.parse(createdDateString);
                            Date prevCreatedDate = inputFormat.parse(prevDateString);
                            if (myCreatedDate != null && prevCreatedDate != null) {
                                // Check if the created date is the current date
                                if (isSameDate(myCreatedDate, prevCreatedDate)) {
                                    contactMessageViewHolder.tvDate.setVisibility(View.GONE);
                                } else {
                                    String date = dateFormat.format(myCreatedDate);
                                    contactMessageViewHolder.tvDate.setText(date);
                                }
                            }

                            String time = timeFormat.format(myCreatedDate);
                            contactMessageViewHolder.tvMessageTime.setText(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Date myCreatedDate = inputFormat.parse(createdDateString);
                            String date = dateFormat.format(myCreatedDate);
                            contactMessageViewHolder.tvDate.setText(date);
                            String time = timeFormat.format(myCreatedDate);
                            contactMessageViewHolder.tvMessageTime.setText(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
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
