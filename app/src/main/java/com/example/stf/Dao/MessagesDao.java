package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Message;

import java.util.List;

@Dao
public interface MessagesDao {
    @Query("DELETE FROM Message WHERE chatId = :chatId")
    void deleteMessagesByChatId(String chatId);

    //bring specific message
    @Query("SELECT * FROM Message WHERE message_id =:id")
    Message get(int id);

    @Query("SELECT * FROM Message WHERE chatId =:id")
    List<Message> getAllMessages(int id);

    @Query("DELETE FROM Message")
    void deleteAllMessages();

    @Insert
    void insert(Message... messages);

    @Update
    void update(Message... messages);
}
