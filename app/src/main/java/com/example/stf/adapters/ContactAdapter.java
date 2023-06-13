package com.example.stf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stf.Contacts.Contact;
import com.example.stf.R;
import com.example.stf.entities.Chat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView displayName;
        private final CircleImageView propilePic;
        private final TextView lastMessage;
        private final TextView notification;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.tvContactName);
            propilePic = itemView.findViewById(R.id.contactImg);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            notification = itemView.findViewById(R.id.tvNotification);
        }
    }

    private final LayoutInflater mInflater;
    private Chat[] contacts;

    public ContactAdapter(Context context, Chat[] contacts) {
        this.mInflater = LayoutInflater.from(context);
        this.contacts = contacts;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (contacts != null) {
            final Chat currentContact = contacts[position];
            // Bind the data to the views in the ContactViewHolder
            holder.displayName.setText(currentContact.getUser().getDisplayName());
            // Set the profile picture using an image loading library or your preferred method
            // holder.profilePic.setImageURI(Uri.parse(currentContact.getProfilePicUrl()));
            if (currentContact.getLastMessage() != null) {
                holder.lastMessage.setText(currentContact.getLastMessage().getContent());
            }
            holder.notification.setText(String.valueOf(currentContact.getUser().getNotfications()));
        }
    }

    @Override
    public int getItemCount() {
        return contacts.length;
    }

    public void setContacts(Chat[] contacts) {
        this.contacts = contacts;
    }

    public Chat[] getContacts() {
        return contacts;
    }
}