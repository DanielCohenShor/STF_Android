package com.example.stf;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.stf.Dao.MessagesDao;
import com.example.stf.entities.Contact;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.entities.Message;

@Database(entities = {Contact.class, Message.class}, version = 13)
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
    public abstract MessagesDao messagesDao();
}
