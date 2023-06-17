package com.example.stf;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.stf.Dao.MessagesDao;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.entities.Contact;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.entities.Message;
import com.example.stf.entities.Settings;

@Database(entities = {Contact.class, Message.class, Settings.class}, version = 19)
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
    public abstract MessagesDao messagesDao();
    public abstract SettingsDao settingsDao();
}
