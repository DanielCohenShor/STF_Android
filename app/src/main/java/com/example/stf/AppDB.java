package com.example.stf;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.stf.Dao.MessagesDao;
import com.example.stf.entities.Contact;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.entities.Message;

@Database(entities = {Contact.class, Message.class}, version = 25,exportSchema = false)
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
    public abstract MessagesDao messagesDao();
}