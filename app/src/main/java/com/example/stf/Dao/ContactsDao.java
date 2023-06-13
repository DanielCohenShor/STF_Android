package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Chat;

@Dao
public interface ContactsDao {

    //bring all the chats
    @Query("SELECT * FROM Chat")
    Chat[] index();

    //bring spesific chat
    @Query("SELECT * FROM Chat WHERE id =:id")
    Chat get(int id);

    @Insert
    void insert(Chat... chats);

    @Update
    void update(Chat... chats);

    @Delete
    void delete(Chat... chats);



}
