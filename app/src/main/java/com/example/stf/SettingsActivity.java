package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import com.example.stf.Login.LoginActivity;

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
    SharedPreferences.Editor editor;

    private String token;

    private String currentUserDisplayName;

    private String currentUserProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // init the xml and his stuff.
        init();

        showDetails();

        makeDarkMode();

        createListeners();
    }

    private void init() {
        btnExitSettings = findViewById(R.id.btnExitSettings);
        switcher = findViewById(R.id.switchDarkMode);
        currentUserImg = findViewById(R.id.currentUserImg);
        tvCurrentUserDisplayName = findViewById(R.id.tvCurrentUserDisplayName);
        llChangeApi = findViewById(R.id.llChangeApi);
        llLogout = findViewById(R.id.llLogout);
        token = getIntent().getStringExtra("token");
        currentUserDisplayName = getIntent().getStringExtra("currentUserDisplayName");
        currentUserProfilePic = getIntent().getStringExtra("currentUserProfilePic");
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
        // Convert the Base64-encoded string to a Bitmap
        Bitmap bitmap = decodeBase64Image(currentUserProfilePic);

        if (bitmap != null) {
            // Set the Bitmap as the image source for the ImageView
            currentUserImg.setImageBitmap(bitmap);
        }

        tvCurrentUserDisplayName.setText(currentUserDisplayName);

    }

    private void createListeners() {
        btnExitSettings.setOnClickListener(v -> finish());

        llChangeApi.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangeApiActivity.class);
            startActivity(intent);
        });

        llLogout.setOnClickListener(this::onButtonShowPopupWindowClick);
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

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
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        btnCancel.setOnClickListener(v -> popupWindow.dismiss());

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void makeDarkMode() {
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false); // Set default value to true for light mode

        // Update the UI based on the initial value of nightMODE
        if (nightMODE) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        switcher.setOnClickListener(v -> {
            // Update the UI based on the new value of nightMODE
            if (nightMODE) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", true);
            }
            editor.apply();
        });
    }
}