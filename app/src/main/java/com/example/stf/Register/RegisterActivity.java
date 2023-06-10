package com.example.stf.Register;

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

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword,etPasswordVerification, etDisplayName;
    private TextView tvPicture;
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
        initListener();
        //init the view model
        initViewMOdel();
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

    private void handleRegistrationCallback(String[] errors) {
        if (errors.length == 0) {
            // Start the new activity here
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Handle the errors here
            // todo: replace with pretty UI.
            // Inside the errors array are all the errors
            StringBuilder errorMessage = new StringBuilder();
            for (int i = 0; i < errors.length; i++) {
                errorMessage.append(errors[i]).append("\n");
                if (errors[i].equals("password")) {
                    errorMessage.append("password verification\n");
                }
            }
            // in this part i have inside the errorMassage all the fields that not good.
            // Add the error message TextView dynamically
            TextView tvError = new TextView(RegisterActivity.this);
            tvError.setText(errorMessage.toString());
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
        }
    }

    private void initViewMOdel() {
        registerViewModel = new ViewModelProvider(this).get(ViewModelRegister.class);
        // create listener for the btnRegister
        btnRegister.setOnClickListener(view -> performRegistration());
    }
    //only findview.by.id
    private void initView() {
        // Initialize the RegisterViewModel
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordVerification = findViewById(R.id.etPasswordVerification);
        etDisplayName = findViewById(R.id.etDisplayName);
        tvPicture = findViewById(R.id.tvPicture);
        btnPasswordVisibility = findViewById(R.id.btnShowPassword);
        btnConfirmationPasswordVisibility = findViewById(R.id.btnShowConfirmationPassword);
        btnRegister = findViewById(R.id.btnRegister);

    }

    private void initListener() {
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

        btnConfirmationPasswordVisibility.setOnClickListener(v -> {
            boolean isPasswordVisible = false;
            final Drawable showPassword = ContextCompat.getDrawable(context, R.drawable.show_password_icon_drawable);
            final Drawable hidePassword = ContextCompat.getDrawable(context, R.drawable.hide_password_icon_drawable);

            if (isPasswordVisible) {
                btnConfirmationPasswordVisibility.setImageDrawable(showPassword);
                // Set the input type to hide the password
                etPasswordVerification.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                btnConfirmationPasswordVisibility.setImageDrawable(hidePassword);
                // Set the input type to show the password
                etPasswordVerification.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            isPasswordVisible = !isPasswordVisible;

            // Move the cursor to the end of the text
            etPasswordVerification.setSelection(etPasswordVerification.getText().length());
        });
    }

}