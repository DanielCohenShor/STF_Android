package com.example.stf.Register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.UserAPI;
import com.example.stf.entities.User;

import java.util.Objects;
import java.util.function.Consumer;

public class ViewModelRegister extends ViewModel {
    private UserAPI userAPI;

    public ViewModelRegister() {
        this.userAPI = new UserAPI();
    }


    public void performRegistration(String usernameValue, String passwordValue,
                                        String passwordVerificationValue,
                                        String displayNameValue, String pictureValue,
                                        Consumer<String[]> callback) {
        // Add your logic to perform the registration process check valdiation
        if (Objects.equals(passwordValue, passwordVerificationValue)) {
            User user = new User(usernameValue, passwordValue, displayNameValue, pictureValue);
            userAPI.post(user, callback);
            return;
        }
        // meaning the password not match.
        String[] errors = {"password"}; // Password verification failed
        callback.accept(errors);
    }
}
