package com.example.stf.Register;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.UserAPI;
import com.example.stf.entities.User;

import java.util.Objects;
import java.util.function.Consumer;

public class ViewModelRegister extends ViewModel {
    private final UserAPI userAPI;

    public ViewModelRegister(String baseUrl) {
        this.userAPI = new UserAPI(baseUrl);
    }

    public void performRegistration(String usernameValue, String passwordValue,
                                    String passwordVerificationValue,
                                    String displayNameValue,
                                    Consumer<String[]> callback, String photo) {
        // Add your logic to perform the registration process check valdiation
        if (Objects.equals(passwordValue, passwordVerificationValue)) {
            if (!Objects.equals(photo, "data:image/png;base64,null")) {
                User user = new User(usernameValue, passwordValue, displayNameValue, photo);
                userAPI.post(user, callback);
                return;
            } else {
                String[] errors = {"ProfilePic"}; // Password verification failed
                callback.accept(errors);
                return;
            }
        }
        // meaning the password not match.
        String[] errors = {"password"}; // Password verification failed
        callback.accept(errors);
    }
}
