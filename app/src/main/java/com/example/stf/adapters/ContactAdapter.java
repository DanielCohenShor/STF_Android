package com.example.stf.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stf.ContactClickListener;
import com.example.stf.entities.Contact;
import com.example.stf.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView displayName;

        private final CircleImageView profilePic;
        private final TextView lastMessage;
        private final TextView notification;

        private final TextView time;

        public ContactViewHolder(@NonNull View itemView, ContactClickListener contactClickListener) {
            super(itemView);
            displayName = itemView.findViewById(R.id.tvContactName);
            profilePic = itemView.findViewById(R.id.contactImg);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            notification = itemView.findViewById(R.id.tvNotification);
            time = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(v -> {
                if (contactClickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        contactClickListener.onItemClick(pos);
                    }
                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private Contact[] contacts;

    private final ContactClickListener contactClickListener;

    public ContactAdapter(Context context, Contact[] contacts, ContactClickListener contactClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.contacts = contacts;
        this.contactClickListener = contactClickListener;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(itemView, contactClickListener);
    }

    private Bitmap decodeBase64Image(String base64Image) {
        try {
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public String getTime(Contact currentContact) {
        String createdDateString = currentContact.getLastMessage().getCreated();
        SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        try {
            // Parse the created date string
            Date createdDate = inputFormat.parse(createdDateString);
            // Compare the created date with the current date
            if (createdDate != null) {
                // Check if the created date is the current date
                if (isSameDate(createdDate, currentDate)) {
                    try {
                        Date createdTime = inputFormat.parse(createdDateString);
                        return timeFormat.format(createdTime);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // The created date is not the current date
                    return dateFormat.format(createdDate);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (contacts != null) {
            final Contact currentContact = contacts[position];
            // Bind the data to the views in the ContactViewHolder
            holder.displayName.setText(currentContact.getUser().getDisplayName());

            // Set the profile picture using an image loading library or your preferred method
            String base64Image = currentContact.getUser().getProfilePic();
            // Convert the Base64-encoded string to a Bitmap
            Bitmap bitmap = decodeBase64Image(base64Image);

            if (bitmap != null) {
                // Set the Bitmap as the image source for the ImageView
                holder.profilePic.setImageBitmap(bitmap);
            }

            if (currentContact.getLastMessage() != null) {
                holder.lastMessage.setText(currentContact.getLastMessage().getContent());

                holder.time.setText(getTime(currentContact));
            }
            if (currentContact.getUser().getNotfications() != 0) {
                holder.notification.setText(String.valueOf(currentContact.getUser().getNotfications()));
            } else {
                // hide notification
                holder.notification.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.length;
    }

    public void setContacts(Contact[] contacts) {
        this.contacts = contacts;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public Contact getContact(int index) {
        return contacts[index];
    }
}
