package com.example.stf;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.stf.Dao.ChatDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.entities.Chat;
import com.example.stf.entities.Contact;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.entities.Message;

@Database(entities = {Contact.class, Chat.class, Message.class}, version = 8)
@TypeConverters(Converters.class)
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
    public abstract ChatDao chatDao();
    public abstract MessagesDao messagesDao();
}
