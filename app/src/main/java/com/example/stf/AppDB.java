package com.example.stf;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.stf.entities.Contact;
import com.example.stf.Dao.ContactsDao;

@Database(entities = {Contact.class}, version = 5)
public abstract class AppDB extends RoomDatabase{
    public abstract ContactsDao ContactsDao();
}
