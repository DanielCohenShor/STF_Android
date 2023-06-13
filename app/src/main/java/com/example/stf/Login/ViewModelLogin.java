package com.example.stf.Login;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.TokenAPI;
import com.example.stf.api.UserAPI;
import com.example.stf.entities.User;

import java.util.function.Consumer;

public class ViewModelLogin extends ViewModel {
    private TokenAPI tokenAPI;
    private UserAPI userAPI;

    public ViewModelLogin() {
        this.tokenAPI = new TokenAPI();
        this.userAPI = new UserAPI();
    }

    public void setToken(String token) {
        userAPI.setToken(token);
    }

    public String getToken() {
        return userAPI.getToken();
    }

    public void performLogin(String usernameValue, String passwordValue, Consumer<String> callback) {
        // Add your logic to perform the login process.
        tokenAPI.post(usernameValue, passwordValue, callback);
    }

    public void getDetails(String username, Consumer<User> callback) {
        userAPI.get(username, callback);
    }
}
