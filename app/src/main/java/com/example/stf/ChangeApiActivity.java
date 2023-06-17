package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

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

import com.example.stf.Dao.SettingsDao;

public class ChangeApiActivity extends AppCompatActivity {

    private ImageButton btnExitChangeApi;

    private EditText etChangeApi;

    private AppCompatButton btnChangeApi;

    private AppDB db;
    private SettingsDao settingsDao;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_api);

        init();

        initDB();

        createListeners();
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
            baseUrl = settingsDao.getFirst().getServerUrl();
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

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // Retrieve the TextView from the popup layout
        TextView tvPopupHeadline = popupView.findViewById(R.id.tvPopupHeadline);
        TextView tvPopupText = popupView.findViewById(R.id.tvPopupText);

        String popupHeadline = "Change API";
        tvPopupHeadline.setText(popupHeadline);

        String popupText = "Changing server address can cause problems, make sure that this is what you want " +
                "and the server address is correct.";
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
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The value of `BaseUrl` will be the value that the user entered
                String NewBaseUrl = etChangeApi.getText().toString();
                AsyncTask.execute(() -> {
                    settingsDao.updateUrl(baseUrl, NewBaseUrl);
                });

            }
        });
    }
}