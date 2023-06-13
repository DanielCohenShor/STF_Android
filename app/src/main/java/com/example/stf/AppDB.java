package com.example.stf;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.stf.Contacts.Contact;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.entities.Chat;

@Database(entities = {Contact.class}, version = 5)
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
}
