package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.Login.LoginActivity;
import com.example.stf.entities.Settings;

public class ChangeApiActivity extends AppCompatActivity {

    private ImageButton btnExitChangeApi;

    private EditText etChangeApi;

    private AppCompatButton btnChangeApi;

    private AppDB db;
    private SettingsDao settingsDao;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_api);

        init();

        initDB();

    }

    private void init() {
        btnExitChangeApi = findViewById(R.id.btnExitChangeApi);
        etChangeApi = findViewById(R.id.etChangeApi);
        btnChangeApi = findViewById(R.id.btnChangeApi);
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            settingsDao = db.settingsDao();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();
            baseUrl = settingsDao.getFirst().getServerUrl();
            createListeners();
        });
    }

    public void createListeners() {
        btnExitChangeApi.setOnClickListener(v -> finish());

        btnChangeApi.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etChangeApi.getText().toString().trim())) {
                onButtonShowPopupWindowClick(v);
            }
        });
    }

    public void onButtonShowPopupWindowClick(View view) {
        String NewBaseUrl = etChangeApi.getText().toString();
        new AlertDialog.Builder(ChangeApiActivity.this)
                .setTitle("Change API")
                .setMessage("You will change the current server address: " + baseUrl +
                        " to: " + NewBaseUrl + ". Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    AsyncTask.execute(() -> {
                        settingsDao.updateUrl(baseUrl, NewBaseUrl);
                        settingsDao.updatePhoto(baseUrl, "");
                        settingsDao.deleteDisplayName(baseUrl);
                        contactsDao.deleteAllContacts();
                        messagesDao.deleteAllMessages();
                        baseUrl = NewBaseUrl;
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // No action needed, dialog will be automatically dismissed
                })
                .show();
    }
}