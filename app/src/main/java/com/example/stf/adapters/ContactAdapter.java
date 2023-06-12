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

import java.util.List;

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

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (contacts != null) {
            final Contact currentContact = contacts[position];
            // Bind the data to the views in the ContactViewHolder
            holder.displayName.setText(currentContact.getUser().getDisplayName());
            // Set the profile picture using an image loading library or your preferred method
            // holder.profilePic.setImageURI(Uri.parse(currentContact.getProfilePicUrl()));
            holder.lastMessage.setText(currentContact.getLastMessage());
            holder.notification.setText(String.valueOf(currentContact.getNotifications()));
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