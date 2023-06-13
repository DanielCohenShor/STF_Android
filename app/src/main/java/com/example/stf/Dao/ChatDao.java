package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Chat;

@Dao
public interface ChatDao {

    //bring all the chats
    @Query("SELECT * FROM Chat")
    Chat[] index();

    //bring specific chat
    @Query("SELECT * FROM Chat WHERE chat_id =:id")
    Chat get(int id);

    @Insert
    void insert(Chat... Chats);

    @Update
    void update(Chat... Chats);

    @Delete
    void delete(Chat... Chats);
}
