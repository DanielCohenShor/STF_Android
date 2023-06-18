package com.example.stf.Dao;

import android.graphics.Bitmap;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.stf.entities.Settings;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM Settings LIMIT 1")
    Settings getFirst();

    @Query("UPDATE Settings SET displayname = :displayName WHERE serverUrl = :serverUrl")
    void updateDisplayName(String serverUrl, String displayName);

    @Query("UPDATE Settings SET cuurentChat = :cuurentChat WHERE serverUrl = :serverUrl")
    void updateCuurentChat(String serverUrl, String cuurentChat);

    @Query("UPDATE Settings SET displayname = NULL WHERE serverUrl = :serverUrl")
    void deleteDisplayName(String serverUrl);

    @Query("UPDATE Settings SET photo = :photo WHERE serverUrl = :serverUrl")
    void updatePhoto(String serverUrl, String photo);

    @Query("SELECT COUNT(*) FROM Settings")
    int getRowCount();

    @Query("UPDATE Settings SET serverUrl = :newUrl WHERE serverUrl = :oldUrl")
    void updateUrl(String oldUrl, String newUrl);
    @Insert
    void insert(Settings... settings);

    @Update
    void update(Settings... settings);

    @Delete
    void delete(Settings... settings);
}
