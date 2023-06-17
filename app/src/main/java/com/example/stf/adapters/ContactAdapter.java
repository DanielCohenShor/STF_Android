package com.example.stf.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stf.ContactClickListener;
import com.example.stf.Notifications.ChatsNotification;
import com.example.stf.Notifications.UserNotification;
import com.example.stf.entities.Contact;
import com.example.stf.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {

    static class ContactViewHolder extends RecyclerView.ViewHolder {
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

            itemView.setOnLongClickListener(v -> {
                if (contactClickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        contactClickListener.onItemLongClick(pos);
                    }
                }
                return false;
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<Contact> contacts; // Original list of contacts
    private List<Contact> filteredContacts; // Filtered list of contacts

    private final ContactClickListener contactClickListener;

    public ContactAdapter(Context context, List<Contact> contacts, ContactClickListener contactClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.contacts = contacts;
        this.filteredContacts = new ArrayList<>(contacts);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

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
        if (filteredContacts != null) {
            final Contact currentContact = filteredContacts.get(position);
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

            if (currentContact.getNotifications() != 0) {
                holder.notification.setText(String.valueOf(currentContact.getNotifications()));
                holder.notification.setVisibility(View.VISIBLE);
            } else {
                // hide notification
                holder.notification.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    // Implement the Filterable interface methods
    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private final Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String searchText = constraint.toString().toLowerCase().trim();
            List<Contact> filteredContactList = new ArrayList<>();

            if (TextUtils.isEmpty(searchText)) {
                // If the search text is empty, show all contacts
                filteredContactList.addAll(contacts);
            } else {
                // Filter the contacts based on the search text
                for (Contact contact : contacts) {
                    if (contact.getUser().getDisplayName().toLowerCase().contains(searchText)) {
                        filteredContactList.add(contact);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredContactList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredContacts = (List<Contact>) results.values;
            notifyDataSetChanged();
        }
    };

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        this.filteredContacts = new ArrayList<>(contacts);
    }

    public Contact getContact(int index) {
        return filteredContacts.get(index);
    }

    public List<Contact> getContacts() {
        return filteredContacts;
    }

    public int getWantedContact(int id) {
        for (int i = 0; i < filteredContacts.size(); i++) {
            if (filteredContacts.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void setNotifications(UserNotification notifications) {
        for (int i = 0; i < notifications.getChats().length; i++) {
            ChatsNotification chat = notifications.getChats()[i];
            int contactIndex = getWantedContact(chat.getId());
            int notification;
            Contact contact = filteredContacts.get(contactIndex);
            if (Objects.equals(chat.getUsers().get(0).get("username"), contact.getUser().getUsername())) {
                notification = Integer.parseInt(Objects.requireNonNull(chat.getUsers().get(0).get("notifications")));
            } else {
                notification = Integer.parseInt(Objects.requireNonNull(chat.getUsers().get(1).get("notifications")));
            }
            contact.setNotifications(notification);
            filteredContacts.set(contactIndex, contact);
        }
    }

    public void resetNotification(int chatId) {
        int contactIndex = getWantedContact(chatId);
        Contact contact = filteredContacts.get(contactIndex);
        contact.setNotifications(0);
        filteredContacts.set(contactIndex, contact);
    }
}
