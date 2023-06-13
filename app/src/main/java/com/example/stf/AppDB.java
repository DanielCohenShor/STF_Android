package com.example.stf;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.stf.Dao.ChatDao;
import com.example.stf.entities.Chat;
import com.example.stf.entities.Contact;
import com.example.stf.Dao.ContactsDao;

@Database(entities = {Contact.class, Chat.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
    public abstract ChatDao chatDao();
}
