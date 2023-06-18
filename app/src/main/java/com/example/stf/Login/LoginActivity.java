package com.example.stf.Login;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stf.AppDB;
import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.R;
import com.example.stf.Register.RegisterActivity;
import com.example.stf.SettingsActivity;
import com.example.stf.entities.Settings;

import com.example.stf.R;
import com.example.stf.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
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

    private Button btnLogin;
    private TextView linkToRegister;

    private HashSet<String> createdTextViews = new HashSet<>();

    private String baseUrl;

    private AppDB db;
    private SettingsDao settingsDao;
    private boolean isFirstTime = true;
    private String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initDB();
      
        //generate token for firebase
        generateTokenFireBase();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstTime) {
            // This code will run only the first time
            isFirstTime = false;
        } else {
            AsyncTask.execute(() -> {
                baseUrl = settingsDao.getFirst().getServerUrl();
                viewModelLogin.setBaseUrl(baseUrl);
            });
        }
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            settingsDao = db.settingsDao();
            if (settingsDao.getRowCount() == 0) {
            // http://10.0.2.2:5000/api/
            Settings defaultSettings = new Settings("http://10.0.2.2:5000/api/", false, "");
            settingsDao.insert(defaultSettings);
            baseUrl = settingsDao.getFirst().getServerUrl();
        } else {
            baseUrl = settingsDao.getFirst().getServerUrl();
        }
            viewModelLogin = new ViewModelLogin(baseUrl);
            // init the views item of the activity.
            initViewItem();

            // createListeners
            createListeners();
        });
    }

    public void generateTokenFireBase() {
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        newToken = task.getResult();

                        // Log and toast
                        Toast.makeText(LoginActivity.this, newToken, Toast.LENGTH_SHORT).show();
                    }
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
        });
    }
    private void initViewItem() {
        btnPasswordVisibility = findViewById(R.id.btnShowPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        linkToRegister = findViewById(R.id.linkToRegister2);
        btnSettings = findViewById(R.id.btnSettings);
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
        intent.putExtra("baseUrl", baseUrl);
        startActivity(intent);

    }
}