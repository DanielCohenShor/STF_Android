package com.example.stf;

import androidx.room.TypeConverter;

import com.example.stf.entities.Message;
import com.example.stf.entities.User;
import com.google.gson.Gson;

public class Converters {
    @TypeConverter
    public static User[] fromUserString(String value) {
        // Convert the string value to User[] array
        // Example implementation using Gson:
        return new Gson().fromJson(value, User[].class);
    }

    @TypeConverter
    public static String toUserString(User[] users) {
        // Convert the User[] array to a string value
        // Example implementation using Gson:
        return new Gson().toJson(users);
    }

    @TypeConverter
    public static Message[] fromMessageString(String value) {
        // Convert the string value to Message[] array
        // Example implementation using Gson:
        return new Gson().fromJson(value, Message[].class);
    }

    @TypeConverter
    public static String toMessageString(Message[] messages) {
        // Convert the Message[] array to a string value
        // Example implementation using Gson:
        return new Gson().toJson(messages);
    }
}