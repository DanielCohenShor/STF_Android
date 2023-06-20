package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.stf.entities.Contact;
import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.Dao.ContactsDao;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AddNewContactActivity extends AppCompatActivity {

    private ImageButton btnExitAddNewContact;

    private AppCompatButton btnAddContact;

    private ViewModalContacts contactsViewModel;

    private EditText etChooseContact;
    private AppDB db;
    private ContactsDao contactsDao;

    private String baseUrl;

    private String currentUsername;

    private SharedPreferences sharedPreferences;
    private String serverToken;
    private String currentUserProfilePic;
    private String currentUserDisplayName;

    private void getSharedPreferences() {
        baseUrl = sharedPreferences.getString("serverUrl", "");
        currentUserProfilePic = sharedPreferences.getString("photo", "");
        currentUserDisplayName = sharedPreferences.getString("displayName", "");
        serverToken = sharedPreferences.getString("serverToken", "");
        currentUsername = sharedPreferences.getString("userName", "");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        getSharedPreferences();

        // init the data base
        initDB();
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            // init the xml and his stuff.
            runOnUiThread(() -> {
                init();

                //create listeners
                createListeners();
            });
        });
    }

    private void init() {
        btnExitAddNewContact = findViewById(R.id.btnExitAddNewContact);
        btnAddContact = findViewById(R.id.btnAddContact);
        etChooseContact = findViewById(R.id.etChooseContact);
        contactsViewModel = new ViewModalContacts(baseUrl);
    }

    private void performAddContact() {
        String newContact = etChooseContact.getText().toString();
        if (!Objects.equals(currentUsername, newContact)) {
            contactsViewModel.performAddContact(serverToken, newContact, this::handleAddContactCallback);
        } else {
            handleAddContactCallback(null);
        }
    }


    public String removePrefix(String input) {
        input = input.substring("data:image/png;base64,".length());
        return input;
    }

    private String decreasePhoto(String photo) {
        Log.d("photo", "in start decrease");
        // Decode the Base64 string to obtain the image byte array
        byte[] imageBytes = Base64.decode(photo, Base64.DEFAULT);
        // Convert the byte array to a Bitmap object
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        if (bitmap != null) {
            // Compress the bitmap using the compressBitmap() method
            Bitmap compressedBitmap = compressBitmap(bitmap);

            if (compressedBitmap != null) {
                // Encode the compressed bitmap back to a Base64 string
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] compressedBytes = outputStream.toByteArray();
                Log.d("photo", "in finish good decrease");
                return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
            } else {
                Log.d("photo", "in finish compression fails decrease");
                return null; // Return null if compression fails
            }
        } else {
            Log.d("photo", "in finish decoding fails decrease");
            return null; // Return null if decoding fails
        }
    }
    private Bitmap compressBitmap(Bitmap bitmap) {
        Log.d("photo", "in compress");
        if (bitmap == null) {
            return null;
        }

        try {
            // Calculate the desired dimensions for the compressed bitmap
            int desiredSize = 75; // Desired size in pixels

            // Get the original dimensions of the bitmap
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();

            // Calculate the aspect ratio of the original bitmap
            float aspectRatio = (float) originalWidth / originalHeight;

            // Calculate the new dimensions for the compressed bitmap
            int newWidth = Math.round(aspectRatio > 1 ? desiredSize : desiredSize * aspectRatio);
            int newHeight = Math.round(aspectRatio > 1 ? desiredSize / aspectRatio : desiredSize);

            // Create the compressed bitmap with the new dimensions
            Bitmap compressedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Compress the bitmap further if needed
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            // Create the final compressed bitmap
            byte[] compressedBytes = outputStream.toByteArray();
            Bitmap finalBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);

            // Return the final compressed bitmap
            return finalBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleAddContactCallback(Contact contact) {
        if (contact != null) {
            // make the edit text to be regular
            int styleResId = R.style.EDIT_TEXT;
            TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
            int drawableResId = styledAttributes.getResourceId(0, 0);
            styledAttributes.recycle();
            etChooseContact.setBackgroundResource(drawableResId);
            //push to the data base local
            AsyncTask.execute(() -> {
//                String base64Photo = removePrefix(contact.getUser().getProfilePic());
//                String compressedPhoto = decreasePhoto(base64Photo);
//                contact.getUser().setProfilePic(compressedPhoto);
                // Perform insert operation on a background thread
                contactsDao.insert(contact);
            });
            // Start the new activity here
            finish();
        } else {

            int styleResId = R.style.INVALID_EDIT_TEXT;
            TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
            int drawableResId = styledAttributes.getResourceId(0, 0);
            styledAttributes.recycle();
            etChooseContact.setBackgroundResource(drawableResId);

            etChooseContact.addTextChangedListener(new TextWatcher() {
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
                    // make the edit text to be regular
                    int styleResId = R.style.EDIT_TEXT;
                    TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                    int drawableResId = styledAttributes.getResourceId(0, 0);
                    styledAttributes.recycle();
                    etChooseContact.setBackgroundResource(drawableResId);
                }
            });
        }
    }


    private void createListeners() {
        btnExitAddNewContact.setOnClickListener(v -> {
            // back to the activity here
            finish();
        });

        btnAddContact.setOnClickListener(view -> performAddContact());

    }
}