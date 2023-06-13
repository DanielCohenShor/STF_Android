package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Contact;

@Dao
public interface ContactsDao {

    //bring all the chats
    @Query("SELECT * FROM Contact")
    Contact[] index();

    //bring spesific chat
    @Query("SELECT * FROM Contact WHERE user_id =:id")
    Contact get(int id);

    @Query("DELETE FROM Contact")
    void deleteAllContacts();

    @Insert
    void insert(Contact... Contacts);

    @Update
    void update(Contact... Contacts);

    @Delete
    void delete(Contact... Contacts);
}
