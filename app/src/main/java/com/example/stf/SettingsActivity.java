package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Dao.SettingsDao;
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
    boolean nightMODE;
    SharedPreferences sharedPreferences;


    private String currentUserDisplayName;

    private String currentUserProfilePic;

    private RelativeLayout llCurrentUserInfo;

    private AppDB db;
    private SettingsDao settingsDao;

    private String baseUrl;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // init the xml and his stuff.
        init();

        // i init the db
        initDB();

        updateDarkModeUI();

        makeDarkMode();

        createListeners();
    }

    private void updateDarkModeUI() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            switcher.setChecked(true);
            nightMODE = true;
        } else {
            switcher.setChecked(false);
            nightMODE = false;
        }
    }

    private void init() {
        btnExitSettings = findViewById(R.id.btnExitSettings);
        switcher = findViewById(R.id.switchDarkMode);
        currentUserImg = findViewById(R.id.currentUserImg);
        tvCurrentUserDisplayName = findViewById(R.id.tvCurrentUserDisplayName);
        llChangeApi = findViewById(R.id.llChangeApi);
        llLogout = findViewById(R.id.llLogout);
        llCurrentUserInfo = findViewById(R.id.llCurrentUserInfo);
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
            runOnUiThread(this::showDetails);
        });
    }

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

    private void showDetails() {
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

    private void createListeners() {
        btnExitSettings.setOnClickListener(v -> finish());

        llChangeApi.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangeApiActivity.class);
            startActivity(intent);
        });

        if (!Objects.equals(currentUserProfilePic, "") && !Objects.equals(currentUserDisplayName, "")) {
            llLogout.setOnClickListener(this::onButtonShowPopupWindowClick);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_window, null);

        // Retrieve the TextView from the popup layout
        TextView tvPopupHeadline = popupView.findViewById(R.id.tvPopupHeadline);
        TextView tvPopupText = popupView.findViewById(R.id.tvPopupText);

        String popupHeadline = "Logout";
        tvPopupHeadline.setText(popupHeadline);

        String popupText = "make sure you want to logout from the app.";
        tvPopupText.setText(popupText);

        AppCompatButton btnCancel = popupView.findViewById(R.id.btnCancel);
        AppCompatButton btnContinue = popupView.findViewById(R.id.btnContinue);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        btnCancel.setOnClickListener(v -> popupWindow.dismiss());

        btnContinue.setOnClickListener(v -> {
            popupWindow.dismiss();
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
        });
    }

    private void makeDarkMode() {
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean shouldResetMode = sharedPreferences.getBoolean("should_reset_mode", false);
        nightMODE = sharedPreferences.getBoolean("night", false);

        // Reset the mode if the flag is set
        if (shouldResetMode) {
            nightMODE = false;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("night", false);
            editor.putBoolean("should_reset_mode", false);
            editor.apply();
        }

        // Update the UI based on the initial value of nightMODE
        if (nightMODE) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switcher.setOnClickListener(v -> {
            // Update the UI based on the new value of nightMODE
            if (nightMODE) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                nightMODE = false;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nightMODE = true;
            }

            // Set the flag to reset the mode on next launch
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("should_reset_mode", true);
            editor.putBoolean("night", nightMODE);
            editor.apply();

            recreate();
        });
    }
}