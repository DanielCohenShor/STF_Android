package com.example.stf.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stf.R;
import com.example.stf.Register.RegisterActivity;
import com.example.stf.Register.ViewModelRegister;

public class LoginActivity extends AppCompatActivity {

    private ViewModelLogin viewModelLogin;
    private EditText etUsername;
    private EditText etPassword;
    private ImageButton btnPasswordVisibility;
    private Button btnLogin;
    private TextView linkToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // init the views item of the activity.
        initViewItem();
        // createListenres
        createListeners();
        //init the view model
        initViewMOdel();
    }

    private void createListeners() {
        // i dont know for what ?
        Context context = this; // 'this' refers to the current Activity instance
        btnPasswordVisibility.setOnClickListener(v -> {
            boolean isPasswordVisible = false;
            final Drawable showPassword = ContextCompat.getDrawable(context, R.drawable.show_password_icon_drawable);
            final Drawable hidePassword = ContextCompat.getDrawable(context, R.drawable.hide_password_icon_drawable);

            if (isPasswordVisible) {
                btnPasswordVisibility.setImageDrawable(showPassword);
                // Set the input type to hide the password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                btnPasswordVisibility.setImageDrawable(hidePassword);
                // Set the input type to show the password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            isPasswordVisible = !isPasswordVisible;

            // Move the cursor to the end of the text
            etPassword.setSelection(etPassword.getText().length());
        });

        linkToRegister.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
    private void initViewItem() {
        btnPasswordVisibility = findViewById(R.id.btnShowPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        linkToRegister = findViewById(R.id.linkToRegister);
    }
    private void initViewMOdel() {
        viewModelLogin = new ViewModelProvider(this).get(ViewModelLogin.class);
        //create listener for the btnRegister
        btnLogin.setOnClickListener(view -> {
            // Call the registration method in the RegisterViewModel
            viewModelLogin.performLogin(etUsername.getText().toString(),
                    etPassword.getText().toString());
        });
    }
}