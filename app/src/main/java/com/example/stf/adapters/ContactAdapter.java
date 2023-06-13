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

import com.example.stf.Contacts.Contact;
import com.example.stf.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.tvContactName);
            profilePic = itemView.findViewById(R.id.contactImg);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            notification = itemView.findViewById(R.id.tvNotification);
            time = itemView.findViewById(R.id.tvTime);
        }
    }

    private final LayoutInflater mInflater;
    private Contact[] contacts;

    public ContactAdapter(Context context, Contact[] contacts) {
        this.mInflater = LayoutInflater.from(context);
        this.contacts = contacts;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    private Bitmap decodeBase64Image(String base64Image) {
        try {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

                String createdDateString = currentContact.getLastMessage().getCreated();
                SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.US);

                try {
                    Date createdDate = inputFormat.parse(createdDateString);
                    String time = outputFormat.format(createdDate);
                    holder.time.setText(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (currentContact.getNotifications() != 0) {
                holder.notification.setText(String.valueOf(currentContact.getNotifications()));
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
}