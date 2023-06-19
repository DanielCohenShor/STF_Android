package com.example.stf.api;

import com.example.stf.Notifications.UserNotification;
import com.example.stf.PushNotfications.PushNotifications;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.example.stf.entities.User;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.DELETE;

public interface WebServiceAPI {
    String SERVER_KEY="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDU1CVzt+QHMV+U\n3QYJrkKC2a0wEYPbM/yJDj+yh+ZTCTu6CFLu0NN/BUzYLjWYs6oFuU9YAnNozfWb\nJcJZWJhcc4GJEHUj1NjJF8R+ty1SRVXbIRz1t7U0q++gU2i6zlej06Q/g9Jopm9A\nxe6xEfbNlvnCcOlprCnS6/yh9d4qdzJ3k8VOFDk4NdbC9+YzCZ85E6IeUCT8UYum\nsFeXwIZZ8gPWQ+pYHdO52kPUI6trfn81AEfCVxwkM64QSgp3nqiJ63XVRZ7Rsu5V\nrhkaJY4ts1IoDpysU8NKpAh+lcQ7LlTGEfhUlHiMmEjMP0/tcGyi7WZ1i8HM6YlC\nn9VUvAEPAgMBAAECggEAIR7LSJkB9fKY4XWMm9NlYK/tCTwjN74n2yZUraeuJWxG\n7K+gma9Kx+DJxsyuJOTfcoOWQD0lZlZtldGIT1d/t8d2jZ96M0jLhxkunYT2cG//\nfpnxGUts+ipAS27KL2QEHyfE+ZNomkY9raf/z3OFuRR5Kr61PETWv2RfzM6SiStP\nOo25YVyioDKS5F5p7uxEIDl+33s58WH0oqBZelpKOId0zBsCNjgO9lGfniH+DmM7\n7g4xvJCf/xecC4EHwTc/xOeRCkkA5MFFkAca9osyu/6vEckWgqgBToASe5wcd78D\n4uReAT2XAT7TwMWp/NLE0U//QUlcAT2BnIO8AgQPIQKBgQDrmY3V9lmJeHrugci4\nVcxG/J5iCdoKeWFSn0HbJ5JzWCf7oaiM8Dho8lRDqUYOAAuOXpJrBvP9inIwIsFM\nZKd//6LpsIsH8RhAgHjVfhaFuk53D533Kv17BVRma5XOK31ehKmbJKmj7ln4AO3/\nbJB8t/fpfjgWS7pQRNEwp1ZMoQKBgQDnQdV96NZTvdev959hid262otv463uDQRP\nuO3KhaWnM//F9V2nmTIomYhAwhmfTRYZ7Lpqt5Z+0LhQZ45gFOxZPqAAGdE2ngQG\nFZVgLulnaTjG61aOF621r3uLG0OZCnh/kGGeXxzuExi3+jVNvy99sJzCSJV/hWnn\nZoC2dqI/rwKBgQC1NIdYt38qMpYqd3vP+32AY6/Vq8oeIDlweLkIPClxua4W5cFj\nlSdt4GRbHgiZoANDXw2yb8idiyxERZbykMAbvJyqrHRiiVaj1awaWD4uYJsVC9ft\nUQ1g3zqJA9pWljB40AQ7JTmsshhUnTJwMCn5Iu30knXFAD2umx9iEnJXwQKBgB3N\nkDP/089LAgb9hJ7PY7H3BE1RLFMnfky/6nVVL/oSa34Zlw5IYfx1fOfLm/25s4V3\nyraCHnUNdRN3pROg3qzJs1kP+rRtPNuD4JJauV3IY7bPEYKSbqKhkOxmgRmyDcFy\n3AHLBDFyaJcdOc1hPD+YiTtIm2upx/I4dZIOS+6lAoGBALhY+zPFJw9JQ2srQeuN\nTpgMICg6/1whDxZkclB8CQUUNP+JpIDR699FB90isW9UVqm6LyIN32cBl894Puon\nWSjuz/lXe4E15hHmTtMUZKaHHu9M2dgYGm1QOhCmpQvJ8bBX2TFoeKWxWxpMH9ud\nca3oFsVwyNB0ovpkFlA5HDdE";
    String CONTENT_TYPE ="application/json";
    String PROJECT_ID = "stf-speak-talk-friends";

    @GET("Users/{username}")
    Call<User> getUser(@Header("Authorization") String token, @Path("username") String username);

    @POST("Users")
    Call<Void> createUser(@Body User user);

    @POST("Tokens")
    Call<Void> createToken(@Body RequestBody tokenRequest);

    @POST("Users/Android")
    Call<Void> saveAndroidToken(@Header("Authorization") String token, @Body RequestBody androidToken);

    @POST("Users/removeAndroid")
    Call<Void> removeAndroidToken(@Header("Authorization") String token);

    @GET("Chats")
    Call<Contact[]> getContacts(@Header("Authorization") String token);

    @POST("Chats")
    Call<Contact> addContact(@Header("Authorization") String token, @Body RequestBody username);

    @GET("Chats/{id}/Messages")
    Call<Message[]> getMessages(@Header("Authorization") String token, @Path("id") String id);

    @POST("Chats/{id}/Messages")
    Call<Message> addMessage(@Header("Authorization") String token, @Path("id") String id, @Body RequestBody message);

    @GET("Chats/Update/{id}")
    Call<Contact[]> getUpdateContacts(@Header("Authorization") String token, @Path("id") String id);

    @DELETE("Chats/{id}")
    Call<Void> deleteChat(@Header("Authorization") String token, @Path("id") String id);

    @POST("Chats/Notifications")
    Call<UserNotification> getNotifications(@Header("Authorization") String token);

    @GET("Chats/Notifications/{id}")
    Call<Void> resetNotifications(@Header("Authorization") String token, @Path("id") String id);

    @POST("Chats/Notifications/{id}")
    Call<Void> addNotifications(@Header("Authorization") String token, @Path("id") String id);


    //request to create a "room"
    @Headers({"Authorization: key=" +SERVER_KEY, "Content-Type:" + CONTENT_TYPE, "project_id:"+ PROJECT_ID})
    @POST("fcm/notification")
    Call<ResponseBody> createRoom(@Body RequestBody requestBody);

    @Headers({"Authorization: key=" +SERVER_KEY, "Content-Type:" + CONTENT_TYPE, "project_id:"+ PROJECT_ID})
    @GET("fcm/notification")
    Call<ResponseBody> getNotificationKey(@Query("notification_key_name") String notificationKeyName);

    @Headers({"Authorization: key=" + SERVER_KEY, "Content-Type:" + CONTENT_TYPE, "project_id:"+ PROJECT_ID})
    @POST("fcm/notification")
    Call<ResponseBody> addDeviceToNotificationKey(@Body RequestBody requestBody);


    @Headers({"Authorization: key=" +SERVER_KEY, "Content-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<PushNotifications> PostNotifaction(@Body PushNotifications notfication);
    //more req
}
