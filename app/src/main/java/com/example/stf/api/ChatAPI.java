package com.example.stf.api;

import com.example.stf.AppDB;

import android.util.Log;

import com.example.stf.PushNotfications.PushNotifications;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.function.Consumer;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChatAPI {

    Retrofit retrofit;
    Retrofit retrofitForFireBAse;
    WebServiceAPI webServiceAPI;
    WebServiceAPI webServiceAPIFireBase;

    private String token;

    private AppDB db;

    String baseUrl;

    public ChatAPI(String baseUrl) {
        this.baseUrl = baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitForFireBAse = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webServiceAPI = retrofit.create(WebServiceAPI.class);

        webServiceAPIFireBase = retrofitForFireBAse.create(WebServiceAPI.class);


    }


    public void setToken(String token) {
        this.token = token;
    }

    public void get(String chatId, Consumer<Message[]> callback) {
        Call<Message[]> call = webServiceAPI.getMessages("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Message[]>() {
            @Override
            public void onResponse(Call<Message[]> call, Response<Message[]> response) {
                try {
                    if (response.isSuccessful()) {
                        Message[] messages = response.body();
                        callback.accept(messages);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Message[]> call, Throwable t) {
            }
        });
    }

    public void post(String chatId, String content, Consumer<Message> callback) {
        String reqToken = "Bearer {\"token\":\"" + token + "\"}";

        JsonObject messageRequest = new JsonObject();
        messageRequest.addProperty("msg", content);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(messageRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<Message> call = webServiceAPI.addMessage(reqToken, chatId, requestBody);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                try {
                    if (response.isSuccessful()) {
                        Message newMessage = response.body();
                        callback.accept(newMessage);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }

    public void getUpdate(String chatId) {
        Call<Contact[]> call = webServiceAPI.getUpdateContacts("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Contact[]>() {
            @Override
            public void onResponse(Call<Contact[]> call, Response<Contact[]> response) {
                try {
                    if (response.isSuccessful()) {
                        // okay
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Contact[]> call, Throwable t) {
            }
        });
    }

    public void deleteChat(int chatId, Consumer<Integer> callback) {
        Call<Void> call = webServiceAPI.deleteChat("Bearer {\"token\":\"" + token + "\"}", String.valueOf(chatId));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // delete
                if (response.isSuccessful()) {
                    Log.d("Tag", "inside on success on resposnse");
                    callback.accept(chatId);
                } else {
                    //todo: what to return ?
                    Log.d("Tag", "inside on fail on resposnse");
                    Log.d("Tag", String.valueOf(response.code()));
                    callback.accept(-1);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Tag", "inside on fail");
            }
        });
    }

    /*
    This method is used to create a new room or group for sending notifications.
    Parameters:
    chat_id: A unique identifier for the room or group. It should be a string value.
    token: The registration token of the device that will be added to the room. It should be a string value.
    callback: A callback function that accepts a string parameter. It will be invoked with the result of the operation.
     */

//    public void PostCreateRoom(String chat_id, String token, Consumer<String> callback) throws JSONException {
//        // Create the JSON request body
//        JSONObject requestBodyJson = new JSONObject();
//        requestBodyJson.put("operation", "create");
//        //notification_key_name should be uniqe like the chat id.
//        requestBodyJson.put("notification_key_name", chat_id);
//        JSONArray registrationIds = new JSONArray();
//        registrationIds.put(token);
//
//        // Convert the JSON request body to RequestBody
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());
//        Call<ResponseBody> createRoomCall = webServiceAPI.createRoom(requestBody);
//        createRoomCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                     callback.accept("good");
//                } else {
//                    // Room creation failed
//                    // Handle the error here
//                    callback.accept("bad");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // Room creation failed
//                // Handle the failure here
//            }
//        });
//    }

    /*
    This method is used to retrieve the notification key associated with a specific room or group.
    Parameters:
    chat_id: The unique identifier of the room or group for which to retrieve the notification key. It should be a string value.
    callback: A callback function that accepts a string parameter. It will be invoked with the retrieved notification key.
     */

//    public void GetRoom(String chat_id, Consumer<String> callback) {
//        Call<ResponseBody> call = webServiceAPIFireBase.getNotificationKey(chat_id);
//        // Enqueue the call and define the callbacks
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    // Handle the successful response here
//                    String notificationKey;
//                    try {
//                        String responseString = response.body().toString();
//                        JSONObject responseJson = new JSONObject(responseString);
//                        notificationKey = responseJson.getString("notification_key");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    // with this parameter i can send message to the chat
//                    callback.accept(notificationKey);
//                } else {
//                    // Handle the error response here
//                    // You can access the error code and message using response.code() and response.message()
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // Handle the failure here
//            }
//        });
//    }

    /*
    This method is used to add a device to an existing room or group.
    Parameters:
    operation: The operation to perform. It should be a string value, typically "add" or "remove".
    notification_key_name: The unique identifier of the room or group to which the device will be added. It should be a string value.
    notification_key: The notification key associated with the room or group. It should be a string value.
    registration_ids: An array of registration IDs representing the devices to add to the room. It should be an array of string values.
    callback: A callback function that accepts a string parameter. It will be invoked with the result of the operation.
     */
//    public void PostAddToRoom(String operation, String notification_key_name,
//                              String notification_key, String[] registration_ids,
//                              Consumer<String> callback) throws JSONException {
//        // Create the JSON request body
//        JSONObject requestBodyJson = new JSONObject();
//        requestBodyJson.put("operation", operation);
//        requestBodyJson.put("notification_key_name", notification_key_name);
//        requestBodyJson.put("notification_key", notification_key);
//        JSONArray registrationIds = new JSONArray();
//        registrationIds.put(registration_ids);
//        requestBodyJson.put("registration_ids", registrationIds);
//
//        // Convert the JSON request body to RequestBody
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());
//
//        Call<ResponseBody> addDeviceCall = webServiceAPIFireBase.addDeviceToNotificationKey(requestBody);
//        addDeviceCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    // Device added successfully
//                    // Handle the response here
//                    callback.accept("add");
//                } else {
//                    // Device addition failed
//                    // Handle the error here
//                    callback.accept("fail to add");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // Device addition failed
//                // Handle the failure here
//            }
//        });
//
//    }

    /*
    This method is used to send push notifications to devices or rooms.
    Parameters:

    notifications: An instance of the PushNotifications class that contains the necessary data
    for sending the notification.PushNotifications Class
    The PushNotifications class represents the data required to send a push notification.
     It should be constructed with the necessary information for the notification,
     such as the title, body, recipient devices or rooms, etc.
     */
    public void PostNotifaction(PushNotifications notifications) {
        Call<PushNotifications> call = webServiceAPIFireBase.PostNotifaction(notifications);
        call.enqueue(new Callback<PushNotifications>() {
            @Override
            public void onResponse(Call<PushNotifications> call, Response<PushNotifications> response) {
                // the respons is:
                //Here is an example of "success"— the notification_key has 2 registration tokens
                // associated with it, and the message was successfully sent to both of them:
                //{
                //  "success": 2,
                //  "failure": 0
                //}
                int x = 5;
            }

            @Override
            public void onFailure(Call<PushNotifications> call, Throwable t) {
                int x = 5;
            }
        });

    }

}