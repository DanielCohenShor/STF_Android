package com.example.stf.Login;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.R;
import com.example.stf.Register.RegisterActivity;
import com.example.stf.SettingsActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private String username;
    private ViewModelLogin viewModelLogin;
    private EditText etUsername;
    private EditText etPassword;
    private ImageButton btnPasswordVisibility;

    private ImageButton btnSettings;

    private ProgressBar progressBar;

    private Button btnLogin;
    private TextView linkToRegister;

    private final HashSet<String> createdTextViews = new HashSet<>();

    private String serverUrl;

    private boolean isFirstTime = true;
    private String newToken;
    private SharedPreferences sharedPreferences;
    private final String SERVERURL = "serverUrl";
    private final String USERNAME = "userName";
    private final String SERVERTOKEN = "serverToken";
    private final String DISPLAYNAME = "displayName";
    private final String PROFILEPIC = "photo";
    private final String CURRENTCHAT = "currentChat";

    private boolean check() {
        // open from background
        if (openFromBackGround()) {
            return false;
        }
        if (isFirstTimeLogin()) {
            // User is logging in for the first time
            createSharedPreferences();
            return true;
        } else if (!isUserConnected()) {
            // User is not connected to the app
            serverUrl = sharedPreferences.getString(SERVERURL, "");
            return true;
        } else {
            // User is already logged in and connected
            Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
    }

    private boolean isFirstTimeLogin() {
        // Check if the photo key exists in SharedPreferences
        boolean hasPhoto = sharedPreferences.contains(PROFILEPIC);

        // Check if the serverUrl key exists in SharedPreferences
        boolean hasServerUrl = sharedPreferences.contains(SERVERURL);

        // Return true if either the photo or serverUrl key is missing
        return !(hasPhoto && hasServerUrl);
    }

    private boolean isUserConnected() {
        // Retrieve the serverToken and username values from SharedPreferences
        String serverToken = sharedPreferences.getString(SERVERTOKEN, "");
        String username = sharedPreferences.getString(USERNAME, "");

        // Check if both serverToken and username are not empty
        return !TextUtils.isEmpty(serverToken) && !TextUtils.isEmpty(username);

    }

    private void createSharedPreferences() {
        if (sharedPreferences.contains(SERVERURL)) {
            // Key exists, retrieve the serverUrl
            serverUrl = sharedPreferences.getString(SERVERURL, "");
        } else {
            // Key does not exist, save the serverUrl and other fields
            SharedPreferences.Editor editor = sharedPreferences.edit();
            serverUrl = "http://192.168.1.37:5000/api/";
            editor.putString(SERVERURL, serverUrl);
            editor.putString(SERVERTOKEN, "");
            editor.putString(DISPLAYNAME, "");
            editor.putString(USERNAME, "");
            editor.putString(CURRENTCHAT, "");
            editor.apply();
        }
    }

    private boolean openFromBackGround() {
        Bundle dataExtras = getIntent().getExtras();
        if (dataExtras != null) {
            String newchatId = null;
            String receiverDisplayName = null;

            for (String key : dataExtras.keySet()) {
                if (Objects.equals(key, "chatId")) {
                    newchatId = dataExtras.getString(key);
                }
                if (Objects.equals(key, "reciverDisplayName")) {
                    receiverDisplayName = dataExtras.getString(key);
                }
            }

            if (newchatId != null && receiverDisplayName != null) {
                // Store the values in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("currentChatFromBAckGround", newchatId);
                editor.putString("receiverDisplayName", receiverDisplayName);
                editor.apply();

                // Start ContactsActivity
                Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);


        if(check()) {
            //generate token for firebase
            generateTokenFireBase();

            // init the views item of the activity.
            initViewItem();

            // createListeners
            createListeners();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstTime) {
            // This code will run only the first time
            isFirstTime = false;
        } else {
            serverUrl = sharedPreferences.getString("serverUrl", "");
            viewModelLogin.setBaseUrl(serverUrl);
        }
    }

    public void generateTokenFireBase() {
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    newToken = task.getResult();

                    // Log and toast
                    Toast.makeText(LoginActivity.this, newToken, Toast.LENGTH_SHORT).show();
                });
    }

    private void createListeners() {
        Context context = this; // 'this' refers to the current Activity instance

        //create listener for the btnRegister
        btnLogin.setOnClickListener(view -> {
            // save the username
            this.username = etUsername.getText().toString();
            // Call the registration method in the RegisterViewModel
            viewModelLogin.performLogin(etUsername.getText().toString(),
                    etPassword.getText().toString(),
                    this::handleLogInCallback);
        });
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

        btnSettings.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void initViewItem() {
        btnPasswordVisibility = findViewById(R.id.btnShowPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        linkToRegister = findViewById(R.id.linkToRegister2);
        btnSettings = findViewById(R.id.btnSettings);
        progressBar = findViewById(R.id.progressBar);
        viewModelLogin = new ViewModelLogin(serverUrl);
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
                int styleResId = R.style.INVALID_EDIT_TEXT;
                TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                int drawableResId = styledAttributes.getResourceId(0, 0);
                styledAttributes.recycle();
                etBorder.setBackgroundResource(drawableResId);

                EditText etBorder1 = findViewById(getResources().getIdentifier("etPassword", "id", getPackageName()));
                etBorder1.setBackgroundResource(drawableResId);

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
                        int styleResId = R.style.EDIT_TEXT;
                        TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                        int drawableResId = styledAttributes.getResourceId(0, 0);
                        styledAttributes.recycle();

                        etBorder.setBackgroundResource(drawableResId);

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
                        int styleResId = R.style.EDIT_TEXT;
                        TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                        int drawableResId = styledAttributes.getResourceId(0, 0);
                        styledAttributes.recycle();
                        etBorder1.setBackgroundResource(drawableResId);
                        // remove the text view of the error
                        parentLayout.removeView(tvError);
                        createdTextViews.remove(tvErrorId);
                    }
                });
                createdTextViews.add(tvErrorId);
            }
        } else {
            // save the token
            progressBar.setVisibility(View.VISIBLE);
            viewModelLogin.setToken(token);
            viewModelLogin.getDetails(username, newToken, this::handleDetailsUser);
        }
    }

    private void handleDetailsUser(String [] userDetails) {
        progressBar.setVisibility(View.GONE);
        // Extract the user details from the array
        String username = userDetails[0];
        String displayName = userDetails[1];
        String profilePic = userDetails[2];
        String token = viewModelLogin.getToken();
        saveSharedPreferences(username, token, displayName, profilePic);

        // Start the new activity and pass the data using Intent extras
        Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveSharedPreferences(String username, String serverToken, String displayName, String profilePic) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVERURL, serverUrl);
        editor.putString(SERVERTOKEN, serverToken);
        editor.putString(DISPLAYNAME, displayName);
        editor.putString(USERNAME, username);
        editor.putString(CURRENTCHAT, "");
        editor.putString(PROFILEPIC, profilePic);
        editor.apply();
    }
}