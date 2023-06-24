package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeApiActivity extends AppCompatActivity {

    private ImageButton btnExitChangeApi;

    private EditText etChangeApi;

    private AppCompatButton btnChangeApi;

    private AppDB db;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;
    private String baseUrl;

    private String token;

    private HashSet<String> createdTextViews = new HashSet<>();

    private  SharedPreferences sharedPreferences;

    private ViewModalContacts viewModalContacts;

    private final String SERVERURL = "serverUrl";

    private final String SERVERTOKEN = "serverToken";
    private ContactsListLiveData contactsLiveDataList;

    private MessagesListLiveData messagesListLiveData;



    private void getSharedPreferences() {
        baseUrl = sharedPreferences.getString(SERVERURL, "");
        token = sharedPreferences.getString(SERVERTOKEN, "");
        contactsLiveDataList = ContactsListLiveData.getInstance();
        messagesListLiveData = MessagesListLiveData.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_api);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        getSharedPreferences();
        init();

        initDB();

        createListeners();

    }

    private void init() {
        btnExitChangeApi = findViewById(R.id.btnExitChangeApi);
        etChangeApi = findViewById(R.id.etChangeApi);
        btnChangeApi = findViewById(R.id.btnChangeApi);

        viewModalContacts = new ViewModalContacts(baseUrl);
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();
        });
    }

    private void backToPrevScreen() {
        Intent intent = new Intent(ChangeApiActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        backToPrevScreen();
    }
    public void createListeners() {
        btnExitChangeApi.setOnClickListener(v -> {backToPrevScreen();});
        btnChangeApi.setOnClickListener(this::onButtonShowPopupWindowClick);
    }

    public String checkUrlValidation(String text) {
//        String pattern1 = "(?i)http://((?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)):(?:(102[4-9])|(10[3-9][0-9])|(1[1-9][0-9][0-9])|([2-9][0-9][0-9][0-9])|([1-9][0-9][0-9][0-9][0-9]))";
//        String pattern2 = "(?i)https://((?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)):(?:(102[4-9])|(10[3-9][0-9])|(1[1-9][0-9][0-9])|([2-9][0-9][0-9][0-9])|([1-9][0-9][0-9][0-9][0-9]))";
//
//        // Create a Pattern object
//        Pattern regex1 = Pattern.compile(pattern1);
//        // Create a Pattern object
//        Pattern regex2 = Pattern.compile(pattern2);
//
//        // Create a Matcher object
//        Matcher matcher1 = regex1.matcher(text);
//        // Create a Matcher object
//        Matcher matcher2 = regex2.matcher(text);
//
//        if (matcher1.matches()) {
//            String match = matcher1.group();
//            return match.substring(0, 4).toLowerCase() + match.substring(4);
//        } else if (matcher2.matches()) {
//            String match = matcher2.group();
//            return match.substring(0, 4).toLowerCase() + match.substring(4);
//        } else {
//            //error
//            return "error";
//        }
        return text;
    }

    private void resetSharedPreferences(String newBaseUrl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Reset each value to its default or empty value
        editor.putString("serverUrl", newBaseUrl);
        editor.putString("serverToken", "");
        editor.putString("displayname", "");
        editor.putString("userName", "");
        editor.putString("cuurentChat", "");
        editor.putString("photo", "");
        // Apply the changes
        editor.apply();
    }


    public void onButtonShowPopupWindowClick(View view) {
        String NewBaseUrl = etChangeApi.getText().toString();
        String result = checkUrlValidation(NewBaseUrl);
        if (!Objects.equals(result, "error")) {
            new AlertDialog.Builder(ChangeApiActivity.this)
                    .setTitle("Change API")
                    .setMessage("You will change the current server address: " + baseUrl +
                            " to: " + result+ ". Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        resetSharedPreferences(result);
                        viewModalContacts.removeAndroidToken(token);
                        AsyncTask.execute(() -> {
                            contactsDao.deleteAllContacts();
                            messagesDao.deleteAllMessages();
                            baseUrl = result;
                        });
                        contactsLiveDataList.setContactsList(Collections.emptyList());
                        messagesListLiveData.setMessagesList(Collections.emptyList());
                })
                    .setNegativeButton("No", (dialog, which) -> {
                        // No action needed, dialog will be automatically dismissed
                    })
                    .show();
        } else {
            // Add the error message TextView dynamically
            String tvErrorId = "tvError";
            if (!createdTextViews.contains(tvErrorId)) {
                String message = "Invalid URL address";
                TextView tvError = new TextView(ChangeApiActivity.this);
                tvError.setId(tvErrorId.hashCode());
                tvError.setText(message);
                tvError.setTextColor(Color.RED);
                tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tvError.setPadding(16, 8, 16, 8);

                // Set the appropriate layout params for the error message TextView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

                int styleResId = R.style.INVALID_EDIT_TEXT;
                TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                int drawableResId = styledAttributes.getResourceId(0, 0);
                styledAttributes.recycle();
                etChangeApi.setBackgroundResource(drawableResId);

                // Add the error message TextView to your desired parent view
                LinearLayout parentLayout = findViewById(R.id.linearLayout3);
                parentLayout.addView(tvError, layoutParams);

                etChangeApi.addTextChangedListener(new TextWatcher() {
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

                        etChangeApi.setBackgroundResource(drawableResId);

                        // remove the text view of the error
                        parentLayout.removeView(tvError);
                        createdTextViews.remove(tvErrorId);
                    }
                });
                createdTextViews.add(tvErrorId);
            }
        }
    }
}