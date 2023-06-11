package com.example.stf.Login;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.TokenAPI;
import com.example.stf.api.UserAPI;

import java.util.function.Consumer;

public class ViewModelLogin extends ViewModel {
    private TokenAPI tokenAPI;

    public ViewModelLogin() {
        this.tokenAPI = new TokenAPI();
    }

    public void performLogin(String usernameValue, String passwordValue, Consumer<String> callback) {
        // Add your logic to perform the login process.
        tokenAPI.post(usernameValue, passwordValue, callback);

    }
}
