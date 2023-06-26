package com.example.stf.Login;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.TokenAPI;
import com.example.stf.api.UserAPI;

import java.util.Objects;
import java.util.function.Consumer;

public class ViewModelLogin extends ViewModel {
    private TokenAPI tokenAPI;
    private UserAPI userAPI;

    public ViewModelLogin(String baseUrl) {
        this.tokenAPI = new TokenAPI(baseUrl);
        this.userAPI = new UserAPI(baseUrl);
    }

    public void setBaseUrl(String baseUrl) {
        if (!Objects.equals(baseUrl, tokenAPI.getBaseUrl())) {
            this.tokenAPI = new TokenAPI(baseUrl);
        }
        if (!Objects.equals(baseUrl, userAPI.getBaseUrl())) {
            this.userAPI = new UserAPI(baseUrl);
        }
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

    public void getDetails(String username, String androidToken, Consumer<String[]> callback) {
        userAPI.get(username, callback);
        userAPI.saveAndroidToken(androidToken);
    }
}
