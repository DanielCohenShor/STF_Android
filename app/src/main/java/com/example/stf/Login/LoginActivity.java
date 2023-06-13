package com.example.stf.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
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
import com.example.stf.entities.User;

import java.util.HashSet;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private String username;
    private ViewModelLogin viewModelLogin;
    private EditText etUsername;
    private EditText etPassword;
    private ImageButton btnPasswordVisibility;
    private Button btnLogin;
    private TextView linkToRegister;

    private HashSet<String> createdTextViews = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // init the views item of the activity.
        initViewItem();
        // createListenres
        createListeners();
        //init the view model
        initViewModel();
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
    private void initViewModel() {
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
            String tvErrorId = "tvError";

            if (!createdTextViews.contains(tvErrorId)) {
                // change the ui
                // Add the error message TextView dynamically
                TextView tvError = new TextView(LoginActivity.this);
                tvError.setId(tvErrorId.hashCode());
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

                EditText etBorder = findViewById(getResources().getIdentifier("etUsername", "id", getPackageName()));
                etBorder.setBackgroundResource(R.drawable.invalid_edit_text);

                EditText etBorder1 = findViewById(getResources().getIdentifier("etPassword", "id", getPackageName()));
                etBorder1.setBackgroundResource(R.drawable.invalid_edit_text);

                // Add the error message TextView to your desired parent view
                LinearLayout parentLayout = findViewById(R.id.test);
                parentLayout.addView(tvError, layoutParams);

                etBorder.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // This method is called before the text is changed
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // This method is called when the text is being changed
                    }

                    // This method is called after the text has been changed
                    @Override
                    public void afterTextChanged(Editable s) {
                        // change the border of the text view to none
                        etBorder.setBackgroundResource(R.drawable.edittext_background);
                        // remove the text view of the error
                        parentLayout.removeView(tvError);
                        createdTextViews.remove(tvErrorId);
                    }
                });
                etBorder1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // This method is called before the text is changed
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // This method is called when the text is being changed
                    }

                    // This method is called after the text has been changed
                    @Override
                    public void afterTextChanged(Editable s) {
                        // change the border of the text view to none
                        etBorder1.setBackgroundResource(R.drawable.edittext_background);
                        // remove the text view of the error
                        parentLayout.removeView(tvError);
                        createdTextViews.remove(tvErrorId);
                    }
                });
                createdTextViews.add(tvErrorId);
            }
        } else {
            // save the token
            viewModelLogin.setToken(token);
            viewModelLogin.getDetails(username,this::handleDetailsUser);
        }

    }

    private void handleDetailsUser(User user) {
        // Get the token from the ViewModel (assuming you have a ViewModel instance named viewModelLogin)
        String token = viewModelLogin.getToken();

        // Start the new activity and pass the data using Intent extras
        Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("displayName", user.getDisplayName());
        intent.putExtra("profilePic", user.getProfilePic());
        intent.putExtra("token", token);
        startActivity(intent);

    }
}