package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Contact;

@Dao
public interface ContactsDao {

    //bring all the contacts
    @Query("SELECT * FROM Contact")
    Contact[] index();

    @Query("DELETE FROM Contact WHERE user_id = :id")
    void deleteByChatId(int id);

    //bring specific contact
    @Query("SELECT * FROM Contact WHERE user_id =:id")
    Contact get(int id);

    @Query("DELETE FROM Contact")
    void deleteAllContacts();

    @Query("SELECT * FROM Contact ORDER BY " +
            "CASE WHEN SUBSTR(last_message_created, 3, 1) = ':' THEN last_message_created " +
            "ELSE SUBSTR(last_message_created, 7, 2) || '.' || SUBSTR(last_message_created, 4, 2) || '.' || SUBSTR(last_message_created, 9) " +
            "END DESC")
    Contact[] indexSortedByDate();

    @Insert
    void insert(Contact... Contacts);

    @Update
    void update(Contact... Contacts);

    @Delete
    void delete(Contact... Contacts);
}
