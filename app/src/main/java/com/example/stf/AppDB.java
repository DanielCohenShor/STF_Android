package com.example.stf;

import androidx.room.Database;

@Database(entities = {User.class, Chat.class, Message.class}, version = 1)
public abstract class AppDB {
    public abstract UserDao userDao();
}
