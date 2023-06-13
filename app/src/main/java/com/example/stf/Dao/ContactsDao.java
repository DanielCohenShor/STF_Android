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

    //bring specific contact
    @Query("SELECT * FROM Contact WHERE user_id =:id")
    Contact get(int id);

    @Insert
    void insert(Contact... Contacts);

    @Update
    void update(Contact... Contacts);

    @Delete
    void delete(Contact... Contacts);
}
