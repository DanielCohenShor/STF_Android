package com.example.stf.Register;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.UserAPI;
import com.example.stf.entities.User;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.function.Consumer;

public class ViewModelRegister extends ViewModel {
    private final UserAPI userAPI;

    public ViewModelRegister(String baseUrl) {
        this.userAPI = new UserAPI(baseUrl);
    }

    public void performRegistration(String usernameValue, String passwordValue,
                                    String passwordVerificationValue,
                                    String displayNameValue,
                                    Consumer<String[]> callback, String photo) {
        // Add your logic to perform the registration process check valdiation
        if (Objects.equals(passwordValue, passwordVerificationValue)) {
            if (!Objects.equals(photo, "data:image/png;base64,null")) {
//                photo = smaller(photo);
                Log.d("TAG", "test");
                User user = new User(usernameValue, passwordValue, displayNameValue, photo);
                userAPI.post(user, callback);
                return;
            } else {
                String[] errors = {"ProfilePic"}; // Password verification failed
                callback.accept(errors);
                return;
            }
        }
        // meaning the password not match.
        String[] errors = {"password"}; // Password verification failed
        callback.accept(errors);
    }

//    private String smaller(String photo) {
//        String smallerphoto = removePrefix(photo);
//        smallerphoto = decreasePhoto(smallerphoto);
//        smallerphoto = addPrefix(smallerphoto);
//        return smallerphoto;
//    }
//
//    private String removePrefix(String input) {
//        input = input.substring("data:image/png;base64,".length());
//        return input;
//    }
//
//    private String decreasePhoto(String photo) {
//        Log.d("photo", "in start decrease");
//        // Decode the Base64 string to obtain the image byte array
//        byte[] imageBytes = Base64.decode(photo, Base64.DEFAULT);
//        // Convert the byte array to a Bitmap object
//        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//
//        if (bitmap != null) {
//            // Compress the bitmap using the compressBitmap() method
//            Bitmap compressedBitmap = compressBitmap(bitmap);
//
//            if (compressedBitmap != null) {
//                // Encode the compressed bitmap back to a Base64 string
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//                byte[] compressedBytes = outputStream.toByteArray();
//                Log.d("photo", "in finish good decrease");
//                return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
//            } else {
//                Log.d("photo", "in finish compression fails decrease");
//                return null; // Return null if compression fails
//            }
//        } else {
//            Log.d("photo", "in finish decoding fails decrease");
//            return null; // Return null if decoding fails
//        }
//    }
//    private Bitmap compressBitmap(Bitmap bitmap) {
//        Log.d("photo", "in compress");
//        if (bitmap == null) {
//            return null;
//        }
//
//        try {
//            // Calculate the desired dimensions for the compressed bitmap
//            int desiredSize = 75; // Desired size in pixels
//
//            // Get the original dimensions of the bitmap
//            int originalWidth = bitmap.getWidth();
//            int originalHeight = bitmap.getHeight();
//
//            // Calculate the aspect ratio of the original bitmap
//            float aspectRatio = (float) originalWidth / originalHeight;
//
//            // Calculate the new dimensions for the compressed bitmap
//            int newWidth = Math.round(aspectRatio > 1 ? desiredSize : desiredSize * aspectRatio);
//            int newHeight = Math.round(aspectRatio > 1 ? desiredSize / aspectRatio : desiredSize);
//
//            // Create the compressed bitmap with the new dimensions
//            Bitmap compressedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
//
//            // Compress the bitmap further if needed
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//
//            // Create the final compressed bitmap
//            byte[] compressedBytes = outputStream.toByteArray();
//            Bitmap finalBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);
//
//            // Return the final compressed bitmap
//            return finalBitmap;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private String addPrefix(String input) {
//        String prefix = "data:image/png;base64,";
//        return prefix + input;
//    }


}
