package com.example.stf.Register;

import android.net.Uri;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.UserAPI;
import com.example.stf.entities.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

public class ViewModelRegister extends ViewModel {
    private UserAPI userAPI;

    public ViewModelRegister() {
        this.userAPI = new UserAPI();
    }

    public void performRegistration(String usernameValue, String passwordValue,
                                    String passwordVerificationValue,
                                    String displayNameValue,
                                    Consumer<String[]> callback, String photo) {
        // Add your logic to perform the registration process check valdiation
        if (Objects.equals(passwordValue, passwordVerificationValue)) {
            if (!Objects.equals(photo, "data:image/png;base64,null")) {
                User user = new User(usernameValue, passwordValue, displayNameValue, photo, 0);
                userAPI.post(user, callback);
                return;
            } else {
                String[] errors = {"photo"}; // Password verification failed
                callback.accept(errors);
                return;
            }
        }
        // meaning the password not match.
        String[] errors = {"password"}; // Password verification failed
        callback.accept(errors);
    }

}
