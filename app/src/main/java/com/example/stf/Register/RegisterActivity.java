package com.example.stf.Register;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.stf.Login.LoginActivity;
import com.example.stf.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword,etPasswordVerification, etDisplayName;
    private TextView tvPicture, linkToRegister;
    private Button btnRegister;
    private ImageButton btnConfirmationPasswordVisibility;
    private ImageButton btnPasswordVisibility;
    private ViewModelRegister registerViewModel;

    // Declare a HashSet to store the IDs of the created TextViews
    private HashSet<String> createdTextViews = new HashSet<>();

    // create map that contains all the text of the errors to show for the user
    private HashMap<String, String> errorsText = new HashMap<>();
    private ActivityResultLauncher<Intent> filePickerLauncher;

    private Uri fileUri;

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

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            fileUri = data.getData();
                            // Handle the selected file here
                        }
                    }
                });
    }

    public void onChooseFileClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
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
                String tvId = "tv_error_" + error;

                // check if the error already shown to the user
                if (!createdTextViews.contains(tvId)) {
                    // Add the error message TextView dynamically
                    TextView tvError = new TextView(RegisterActivity.this);
                    tvError.setId(tvId.hashCode());
                    tvError.setText(errorsText.get(error));
                    tvError.setTextColor(Color.RED);
                    tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tvError.setPadding(16, 8, 16, 8);

                    // set the edit text border to red
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
                            etBorder.setBackgroundResource(R.drawable.edittext_background);
                            // remove the text view of the error
                            parentLayout.removeView(tvError);
                            createdTextViews.remove(tvId);
                        }
                    });
                    createdTextViews.add(tvId);
                }
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

        errorsText.put("username", "must contain at least one letter");
//            errorsText.put("username2", "username already exist");
        errorsText.put("password", "must contain at least 5 characters,\nwith a combination of digits and letters");
        errorsText.put("passwordVerification", "must be the same as the password");
        errorsText.put("displayName", "must contain at least one letter");
        errorsText.put("profilePic", "must insert only files of kind: png, jpeg...");
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

        // Define the text with the portion to be highlighted
        String fullText = getResources().getString(R.string.tv_link_to_login);
        String highlightText = "Click here";
        int highlightStart = fullText.indexOf(highlightText);
        int highlightEnd = highlightStart + highlightText.length();

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(fullText);

        // Create a ClickableSpan
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Handle the click event here, e.g., change the color
                int clickedColor = ContextCompat.getColor(RegisterActivity.this, R.color.blue_shade_4);
                ((TextView) widget).setLinkTextColor(clickedColor);

                // Start the new activity here
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                // Perform any other actions you want when the text is clicked
                // For example, navigate to a new activity or perform some task.
            }
        };

        // Apply the ClickableSpan to the desired portion of the text
        spannableString.setSpan(clickableSpan, highlightStart, highlightEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the text clickable and change the color when clicked
        linkToRegister.setText(spannableString);
        linkToRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }
}