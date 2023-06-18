package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.Login.LoginActivity;
import com.example.stf.entities.Contact;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnExitSettings;

    private CircleImageView currentUserImg;

    private TextView tvCurrentUserDisplayName;

    private LinearLayout llChangeApi;

    private LinearLayout llLogout;

    SwitchCompat switcher;
    boolean nightMODE;

    private String currentUserDisplayName;

    private String currentUserProfilePic;

    private RelativeLayout llCurrentUserInfo;

    private AppDB db;
    private SettingsDao settingsDao;

    private String baseUrl;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;
    private boolean isFirstTime = true;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // init the xml and his stuff.
        init();

        // i init the db
        initDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstTime) {
            // This code will run only the first time
            isFirstTime = false;
        } else {
            AsyncTask.execute(() -> {
                currentUserDisplayName = settingsDao.getFirst().getDisplayname();
                currentUserProfilePic = settingsDao.getFirst().getPhoto();
                baseUrl = settingsDao.getFirst().getServerUrl();
                runOnUiThread(this::showDetails);
            });
        }
        // Set the switch based on the saved mode
        switcher.setChecked(nightMode);
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            settingsDao = db.settingsDao();
            currentUserDisplayName = settingsDao.getFirst().getDisplayname();
            currentUserProfilePic = settingsDao.getFirst().getPhoto();
            baseUrl = settingsDao.getFirst().getServerUrl();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();


            createListeners();
            runOnUiThread(this::showDetails);
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

    public void exit() {
        if (Objects.equals(currentUserProfilePic, "")) {
            // Clear the activity stack and start the new activity
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onButtonShowPopupWindowClick(View view) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Logout")
                .setMessage("You will logout. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete chat logic here
                    currentUserProfilePic = "";
                    // Delete the local database
                    AsyncTask.execute(() -> {
                        contactsDao.deleteAllContacts();
                        messagesDao.deleteAllMessages();
                        settingsDao.deleteDisplayName(baseUrl);
                        settingsDao.updatePhoto(baseUrl, currentUserProfilePic);
                    });
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
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