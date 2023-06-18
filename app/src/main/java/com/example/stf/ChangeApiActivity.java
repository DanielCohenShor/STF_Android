package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.app.AlertDialog;
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

import com.example.stf.Dao.SettingsDao;

import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeApiActivity extends AppCompatActivity {

    private ImageButton btnExitChangeApi;

    private EditText etChangeApi;

    private AppCompatButton btnChangeApi;

    private AppDB db;
    private SettingsDao settingsDao;
    private String baseUrl;

    private HashSet<String> createdTextViews = new HashSet<>();

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

        btnChangeApi.setOnClickListener(this::onButtonShowPopupWindowClick);
    }

    public String checkUrlValidation(String text) {
        String pattern1 = "(?i)http://((?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?).(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?).(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?).(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)):(?:(102[4-9])|(10[3-9][0-9])|(1[1-9][0-9][0-9])|([2-9][0-9][0-9][0-9]))/api/";
        String pattern2 = "((?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?).(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?).(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?).(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)):(?:(102[4-9])|(10[3-9][0-9])|(1[1-9][0-9][0-9])|([2-9][0-9][0-9][0-9]))";

        // Create a Pattern object
        Pattern regex1 = Pattern.compile(pattern1);
        // Create a Pattern object
        Pattern regex2 = Pattern.compile(pattern2);

        // Create a Matcher object
        Matcher matcher1 = regex1.matcher(text);
        // Create a Matcher object
        Matcher matcher2 = regex2.matcher(text);

        if (matcher1.matches()) {
            String match = matcher1.group();
            return match.substring(0, 4).toLowerCase() + match.substring(4);
        } else if (matcher2.matches()) {
            String match = matcher2.group();
            return "http://" + match + "/api/";
        } else {
            //error
            return "error";
        }
    }

    public void onButtonShowPopupWindowClick(View view) {
        String NewBaseUrl = etChangeApi.getText().toString();
        String result = checkUrlValidation(NewBaseUrl);
        if (!Objects.equals(result, "error")) {
            new AlertDialog.Builder(ChangeApiActivity.this)
                    .setTitle("Change API")
                    .setMessage("You will change the current server address: " + baseUrl +
                            " to: " + result+ ". Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> AsyncTask.execute(() -> settingsDao.updateUrl(baseUrl, result)))
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