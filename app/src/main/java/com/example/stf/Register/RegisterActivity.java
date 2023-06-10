package com.example.stf.Register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stf.R;

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

    private void initViewMOdel() {
        registerViewModel = new ViewModelProvider(this).get(ViewModelRegister.class);
        //create listener for the btnRegister
        btnRegister.setOnClickListener(view -> {
            // Call the registration method in the RegisterViewModel
            registerViewModel.performRegistration(etUsername.getText().toString(),
                    etPassword.getText().toString(), etPasswordVerification.getText().toString(),
                    etDisplayName.getText().toString(), tvPicture.toString());
        });
    }
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



    }
}