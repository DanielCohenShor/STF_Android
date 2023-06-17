package com.example.stf.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Settings;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM Settings")
    Settings get();

    @Insert
    void insert(Settings... settings);

    @Update
    void update(Settings... settings);

    @Delete
    void delete(Settings... settings);
}
