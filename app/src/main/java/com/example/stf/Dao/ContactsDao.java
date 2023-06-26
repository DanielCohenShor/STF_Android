package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Contact;

import java.util.List;

@Dao
public interface ContactsDao {
    @Query("SELECT * FROM Contact ORDER BY " +
            "CASE WHEN SUBSTR(last_message_created, 3, 1) = ':' THEN last_message_created " +
            "ELSE SUBSTR(last_message_created, 7, 2) || '.' || SUBSTR(last_message_created, 4, 2) || '.' || SUBSTR(last_message_created, 9) " +
            "END DESC")
    List<Contact> getAllContacts();

    @Query("DELETE FROM Contact WHERE user_id = :id")
    void deleteByChatId(int id);

    //bring specific contact
    @Query("SELECT * FROM Contact WHERE user_id =:id")
    Contact get(int id);

    @Query("DELETE FROM Contact")
    void deleteAllContacts();

    @Insert
    void insert(Contact... Contacts);

    @Update
    void update(Contact... Contacts);
}
