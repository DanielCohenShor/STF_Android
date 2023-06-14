package com.example.stf.Register;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etPasswordVerification, etDisplayName;
    private TextView linkToLogin;
    private Button btnRegister;
    private ImageButton btnConfirmationPasswordVisibility;
    private ImageButton btnPasswordVisibility;
    private ViewModelRegister registerViewModel;

    private RoundedImageView riProfilePic;

    // Declare a HashSet to store the IDs of the created TextViews
    private HashSet<String> createdTextViews = new HashSet<>();

    // create map that contains all the text of the errors to show for the user
    private HashMap<String, String> errorsText = new HashMap<>();
    private Uri profilePictureUri;
    private String profilePictureBase64;
    private TextView etProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //init the fileds of the xml activity.
        initView();
        //init the listener for the show password.
        initListeners();
    }

    private void initView() {
        // Initialize the RegisterViewModel
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPasswordVerification = findViewById(R.id.et_passwordVerification);
        etDisplayName = findViewById(R.id.et_displayName);
        btnPasswordVisibility = findViewById(R.id.btnShowPassword);
        btnConfirmationPasswordVisibility = findViewById(R.id.btnShowConfirmationPassword);
        registerViewModel = new ViewModelProvider(this).get(ViewModelRegister.class);
        btnRegister = findViewById(R.id.btnRegister);
        linkToLogin = findViewById(R.id.linkToLogin2);
        riProfilePic = findViewById(R.id.riProfilePic);
        etProfilePic = findViewById(R.id.et_ProfilePic);


        errorsText.put("username", "must contain at least one letter");
        errorsText.put("username exist", "username already exist");
        errorsText.put("password", "must contain at least 5 characters,\nwith a combination of digits and letters");
        errorsText.put("passwordVerification", "must be the same as the password");
        errorsText.put("displayName", "must contain at least one letter");
        errorsText.put("profilePic", "must insert only files of kind: png, jpeg...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            profilePictureUri = data.getData();
            riProfilePic.setImageURI(profilePictureUri);

            // Convert profile picture to Base64
            profilePictureBase64 = convertProfilePictureToBase64();
        }
    }

    private String convertProfilePictureToBase64() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(profilePictureUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            riProfilePic.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_border));
            //clean it
            etProfilePic.setText("");

            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void pickProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private void initListeners() {
        Context context = this; // 'this' refers to the current Activity instance

        // create listener for the btnRegister
        btnRegister.setOnClickListener(view -> performRegistration());

        riProfilePic.setOnClickListener(v -> pickProfilePicture());

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

        linkToLogin.setOnClickListener(view -> navigateToLoginActivity());
    }

    private void performRegistration() {
        registerViewModel.performRegistration(
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                etPasswordVerification.getText().toString(),
                etDisplayName.getText().toString(),
                this::handleRegistrationCallback, "data:image/png;base64," + profilePictureBase64
        );
    }

    private void handleRegistrationCallback(@NonNull String[] errors) {
        if (errors.length == 0) {
            // Start the new activity here
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Handle the errors here
            // Inside the errors array are all the errors
            for (int i = 0; i < errors.length; i++) {
                if (errors[i].equals("password")) {
                    String[] newErrors = Arrays.copyOf(errors, errors.length + 1);
                    newErrors[errors.length] = "passwordVerification";
                    errors = newErrors;
                }
                if (errors[i].equals("photo")) {
                    String[] newErrors = Arrays.copyOf(errors, errors.length + 1);
                    newErrors[errors.length] = "ProfilePic";
                    errors = newErrors;
                }
            }
            //todo: add error for photo show the ui of photo
            for (String error : errors) {
                String layoutId = "ll_" + error;
                String tvId = "tv_error_" + error;
                TextView tvError;
                // check if the error already shown to the user
                if (!createdTextViews.contains(tvId)) {
                    // Add the error message TextView dynamically
                    tvError = new TextView(RegisterActivity.this);
                    tvError.setId(tvId.hashCode());
                    tvError.setText(errorsText.get(error));
                    tvError.setTextColor(Color.RED);
                    tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tvError.setPadding(16, 8, 16, 8);

                    // set the edit text border to red
                    String editTextId = "et_" + error;
                    if(Objects.equals(error, "username exist")) {
                        layoutId = "ll_username";
                        editTextId = "et_username";
                    }

                    if (!editTextId.equals("et_ProfilePic")) {
                        EditText etBorder = findViewById(getResources().getIdentifier(editTextId, "id", getPackageName()));
                        int styleResId = R.style.INVALID_EDIT_TEXT;
                        TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                        int drawableResId = styledAttributes.getResourceId(0, 0);
                        styledAttributes.recycle();
                        etBorder.setBackgroundResource(drawableResId);

                        // Set the appropriate layout params for the error message TextView
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

                        // Add the error message TextView to your desired parent view
                        LinearLayout parentLayout = findViewById(getResources().getIdentifier(layoutId, "id", getPackageName()));
                        parentLayout.addView(tvError, layoutParams);

                        // add listeners to change of the text in the edit text
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
                                int styleResId = R.style.EDIT_TEXT;
                                TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                                int drawableResId = styledAttributes.getResourceId(0, 0);
                                styledAttributes.recycle();

                                etBorder.setBackgroundResource(drawableResId);
                                // remove the text view of the error
                                parentLayout.removeView(tvError);
                                createdTextViews.remove(tvId);
                            }
                        });
                        createdTextViews.add(tvId);
                    } else {
                        int styleResId = R.style.PROFILE_PIC_BORDER;
                        TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                        int drawableResId = styledAttributes.getResourceId(0, 0);
                        styledAttributes.recycle();

                        riProfilePic.setImageResource(drawableResId);

                    }
                }
            }
        }
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}