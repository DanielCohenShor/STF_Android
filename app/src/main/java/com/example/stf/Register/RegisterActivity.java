package com.example.stf.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stf.Login.LoginActivity;
import com.example.stf.R;

import java.util.Arrays;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword,etPasswordVerification, etDisplayName;
    private TextView tvPicture, linkToRegister;
    private Button btnRegister;
    private ImageButton btnConfirmationPasswordVisibility;
    private ImageButton btnPasswordVisibility;
    private ViewModelRegister registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //init the fileds of the xml activity.
        initView();
        //init the listener for the show password.
        initListeners();
        //init the view model
        initViewModel();
    }

    private void performRegistration() {
        registerViewModel.performRegistration(
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                etPasswordVerification.getText().toString(),
                etDisplayName.getText().toString(),
                tvPicture.toString(),
                this::handleRegistrationCallback
        );
    }

    private void handleRegistrationCallback(@NonNull String[] errors) {
        if (errors.length == 0) {
            // Start the new activity here
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Handle the errors here

            // create map that contains all the text of the errors to show for the user
            HashMap<String, String> errorsText = new HashMap<>();
            errorsText.put("username", "must contain at least one letter");
//            errorsText.put("username2", "username already exist");
            errorsText.put("password", "must contain at least 5 characters,\nwith a combination of digits and letters");
            errorsText.put("passwordVerification", "must be the same as the password");
            errorsText.put("displayName", "must contain at least one letter");
            errorsText.put("profilePic", "must insert only files of kind: png, jpeg...");

            // Inside the errors array are all the errors
            for (int i = 0; i < errors.length; i++) {
                if (errors[i].equals("password")) {
                    String[] newErrors = Arrays.copyOf(errors, errors.length + 1);
                    newErrors[errors.length] = "passwordVerification";
                    errors = newErrors;
                }
            }

            for (String error : errors) {
                String layoutId = "ll_" + error;
                // Add the error message TextView dynamically
                TextView tvError = new TextView(RegisterActivity.this);
                tvError.setText(errorsText.get(error));
                tvError.setTextColor(Color.RED);
                tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tvError.setPadding(16, 8, 16, 8);

                String editTextId = "et_" + error;
                EditText etBorder = findViewById(getResources().getIdentifier(editTextId, "id", getPackageName()));
                etBorder.setBackgroundResource(R.drawable.invalid_edit_text);

                // Set the appropriate layout params for the error message TextView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

                // Add the error message TextView to your desired parent view
                LinearLayout parentLayout = findViewById(getResources().getIdentifier(layoutId, "id", getPackageName()));
                parentLayout.addView(tvError, layoutParams);
            }
        }
    }

    private void initViewModel() {
        registerViewModel = new ViewModelProvider(this).get(ViewModelRegister.class);
        // create listener for the btnRegister
        btnRegister.setOnClickListener(view -> performRegistration());
    }
    //only findview.by.id
    private void initView() {
        // Initialize the RegisterViewModel
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPasswordVerification = findViewById(R.id.et_passwordVerification);
        etDisplayName = findViewById(R.id.et_displayName);
        tvPicture = findViewById(R.id.tvProfilePic);
        btnPasswordVisibility = findViewById(R.id.btnShowPassword);
        btnConfirmationPasswordVisibility = findViewById(R.id.btnShowConfirmationPassword);
        btnRegister = findViewById(R.id.btnRegister);
        linkToRegister = findViewById(R.id.linkToRegister);
    }

    private void initListeners() {
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

        btnConfirmationPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;
            final Drawable showPassword = ContextCompat.getDrawable(context, R.drawable.show_password_icon_drawable);
            final Drawable hidePassword = ContextCompat.getDrawable(context, R.drawable.hide_password_icon_drawable);

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    btnConfirmationPasswordVisibility.setImageDrawable(showPassword);
                    // Set the input type to hide the password
                    etPasswordVerification.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    btnConfirmationPasswordVisibility.setImageDrawable(hidePassword);
                    // Set the input type to show the password
                    etPasswordVerification.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                isPasswordVisible = !isPasswordVisible;

                // Move the cursor to the end of the text
                etPasswordVerification.setSelection(etPasswordVerification.getText().length());
            }
        });

        linkToRegister.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}