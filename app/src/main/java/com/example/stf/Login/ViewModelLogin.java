package com.example.stf.Login;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelLogin extends ViewModel {

    private MutableLiveData<String> username;
    private MutableLiveData<String> password;

    public MutableLiveData<String> getUsername() {
        if (username == null) {
            return new MutableLiveData<>();
        }
        return username;
    }

    public MutableLiveData<String> getPassword() {
        if (password == null) {
            return new MutableLiveData<>();
        }
        return password;
    }
}
