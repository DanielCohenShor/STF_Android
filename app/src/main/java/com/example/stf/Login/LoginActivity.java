package com.example.stf.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.R;
import com.example.stf.Register.RegisterActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private String username;
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
        btnPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;
            final Drawable showPassword = ContextCompat.getDrawable(context, R.drawable.show_password_icon_drawable);
            final Drawable hidePassword = ContextCompat.getDrawable(context, R.drawable.hide_password_icon_drawable);

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    btnPasswordVisibility.setImageDrawable(showPassword);
                    // Set the input type to hide the password
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    btnPasswordVisibility.setImageDrawable(hidePassword);
                    // Set the input type to show the password
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                isPasswordVisible = !isPasswordVisible;

                // Move the cursor to the end of the text
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        linkToRegister.setOnClickListener(view -> {
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
        linkToRegister = findViewById(R.id.linkToRegister2);
    }
    private void initViewMOdel() {
        viewModelLogin = new ViewModelProvider(this).get(ViewModelLogin.class);
        //create listener for the btnRegister
        btnLogin.setOnClickListener(view -> {
            // save the username
            this.username = etUsername.getText().toString();
            // Call the registration method in the RegisterViewModel
            viewModelLogin.performLogin(etUsername.getText().toString(),
                    etPassword.getText().toString(),
                    this::handleLogInCallback);
        });
    }

    private void handleLogInCallback(String token) {
        if (Objects.equals(token, "Incorrect username and/or password")) {
            // change the ui
            // Add the error message TextView dynamically
            TextView tvError = new TextView(LoginActivity.this);
            tvError.setText(token);
            tvError.setTextColor(Color.RED);
            tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvError.setPadding(16, 8, 16, 8);

            // Set the appropriate layout params for the error message TextView
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            // Add the error message TextView to your desired parent view
            LinearLayout parentLayout = findViewById(R.id.test);
            parentLayout.addView(tvError, layoutParams);
        } else {
            // save the token
            viewModelLogin.setToken(token);
            viewModelLogin.getDetails(username,this::handleDetailsUser);
        }

    }

    private void handleDetailsUser(String [] userDetails) {
        // Extract the user details from the array
        String username = userDetails[0];
        String displayName = userDetails[1];
        String profilePic = userDetails[2];

        // Get the token from the ViewModel (assuming you have a ViewModel instance named viewModelLogin)
        String token = viewModelLogin.getToken();

        // Start the new activity and pass the data using Intent extras
        Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("displayName", displayName);
        intent.putExtra("profilePic", profilePic);
        intent.putExtra("token", token);
        startActivity(intent);

    }
}