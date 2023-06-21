package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Login.LoginActivity;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnExitSettings;

    private CircleImageView currentUserImg;

    private TextView tvCurrentUserDisplayName;

    private LinearLayout llChangeApi;

    private LinearLayout llLogout;

    SwitchCompat switcher;

    private String currentUserDisplayName;

    private String currentUserProfilePic;

    private RelativeLayout llCurrentUserInfo;

    private AppDB db;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;
    private boolean isFirstTime = true;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private String serverToken;
    private String serverUrl;

    private ViewModalContacts viewModalContacts;

    private final String SERVERURL = "serverUrl";
    private final String USERNAME = "userName";
    private final String SERVERTOKEN = "serverToken";
    private final String DISPLAYNAME = "displayName";
    private final String PROFILEPIC = "photo";
    private final String CURRENTCHAT = "currentChat";

    private void getSharedPreferences() {
        serverUrl = sharedPreferences.getString(SERVERURL, "");
        currentUserProfilePic = sharedPreferences.getString(PROFILEPIC, "");
        currentUserDisplayName = sharedPreferences.getString(DISPLAYNAME, "");
        serverToken = sharedPreferences.getString(SERVERTOKEN, "");
    }
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        getSharedPreferences();
        // init the xml and his stuff.
        init();

        // i init the db
        initDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switcher.setChecked(nightMode);
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();
            runOnUiThread(() -> {
                createListeners();
                showDetails();
            });
        });
    }


    private void init() {
        // Retrieve the saved mode from SharedPreferences
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = isSystemInNightMode();
        btnExitSettings = findViewById(R.id.btnExitSettings);
        switcher = findViewById(R.id.switchDarkMode);
        currentUserImg = findViewById(R.id.currentUserImg);
        tvCurrentUserDisplayName = findViewById(R.id.tvCurrentUserDisplayName);
        llChangeApi = findViewById(R.id.llChangeApi);
        llLogout = findViewById(R.id.llLogout);
        llCurrentUserInfo = findViewById(R.id.llCurrentUserInfo);
        viewModalContacts = new ViewModalContacts(serverUrl);
    }

    private boolean isSystemInNightMode() {
        if (Objects.equals(getResources().getString(R.string.mode), "Day")) {
            //day mode
            return false;
        } else {
            return true;
        }
    }

    private void createListeners() {
        btnExitSettings.setOnClickListener(v -> exit());

        llChangeApi.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangeApiActivity.class);
            startActivity(intent);
            finish();
        });

        if (!Objects.equals(currentUserProfilePic, "") && !Objects.equals(currentUserDisplayName, "")) {
            llLogout.setOnClickListener(this::onButtonShowPopupWindowClick);
        }

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightMode = true;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightMode = false;
                }

                // Save the selected mode to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("nightMode", nightMode);
                editor.apply();
            }
        });    }


    private Bitmap decodeBase64Image(String base64Image) {
        try {
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void resetSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Reset each value to its default or empty value
        editor.putString(SERVERTOKEN, "");
        editor.putString(DISPLAYNAME, "");
        editor.putString(USERNAME, "");
        editor.putString(CURRENTCHAT, "");
        editor.putString(PROFILEPIC,"");
        // Apply the changes
        editor.apply();
    }



    public void exit() {
        if (Objects.equals(currentUserProfilePic, "")) {
            // Clear the activity stack and start the new activity
            // Delete the local database
            resetSharedPreferences();
            AsyncTask.execute(() -> {
                contactsDao.deleteAllContacts();
                messagesDao.deleteAllMessages();
            });
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onButtonShowPopupWindowClick(View view) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Logout")
                .setMessage("You will logout. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModalContacts.removeAndroidToken(serverToken);
                    // Delete the local database
                    resetSharedPreferences();
                    AsyncTask.execute(() -> {
                        contactsDao.deleteAllContacts();
                        messagesDao.deleteAllMessages();
                    });
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // No action needed, dialog will be automatically dismissed
                })
                .show();
    }

    private void showDetails() {
        // Set the switch based on the saved mode
        switcher.setChecked(nightMode);
        if (!Objects.equals(currentUserProfilePic, "") && !Objects.equals(currentUserDisplayName, "")) {
            // Convert the Base64-encoded string to a Bitmap
            Bitmap bitmap = decodeBase64Image(currentUserProfilePic);

            if (bitmap != null) {
                // Set the Bitmap as the image source for the ImageView
                currentUserImg.setImageBitmap(bitmap);
            }

            tvCurrentUserDisplayName.setText(currentUserDisplayName);
        } else {
            llCurrentUserInfo.setVisibility(View.GONE);
            llLogout.setVisibility(View.GONE);
        }
    }

}