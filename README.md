# STF_Android

This is an Android-based messenger app built using XML for the front-end, Express for the back-end, MongoDB for the database, and Firebase for real-time rendering between two online users connected to the app.

## Getting Started
To get started with the app, follow these steps:

1. Clone the project to your local computer.
2. Run the following command to install the dependencies: `npm install`.
3. Once the installation is complete, start the Express server by running the following command:
   - For Windows users: `npm start`
   - For Mac and Linux users: `npm test`
4. This will start the app. Open http://localhost:5000 in your browser to see the app in web format or download the app to your Android phone.
5. Make sure you have MongoDB installed and running on your local machine to handle the database operations for the messenger app.

## Screens

### Login Screen
The login screen allows existing users to log in to their accounts. Users need to enter their username and password to access the app. Only registered users can connect to the app.
- You can access to the settings screen by clicking on the "Settings" button.

### Register Screen
The register screen allows new users to create an account. Users need to provide the following information to register successfully:

- A unique username containing at least one letter.
- A password that is at least 5 characters long and includes both numbers and letters.
- Password verification that matches the entered password.
- A display name that contains at least one letter.
- An image in the image file format.

Registration is only possible if all the details match. An indicator is shown on each field on mouseover.

### Chat Screen
The chat screen allows users to view all the chats they have created and communicate with their contacts. You can only access this screen if you are registered; otherwise, it will lead you to the login screen. The following functionalities are available:

- You can add contacts by clicking the "Add Contact" button.
- You can switch between different contacts.
- Messages can be sent to other users.
- You can log out and log in again.
- You can delete a chat by long-clicking on the chat.
- Notifications are displayed when sending messages.
- Messages jump to the first place upon sending.
- you can search for a specific contact by using the search functionality. This allows you to quickly find and select the desired contact.
- You can access to the settings screen by clicking on the "Settings" button.

### Messages Screen
The messages screen allows the user to view all the messages with their contact. The user can also send a new message to their contact by clicking the "Send Message" button. Messages without content, including empty messages and messages with only spaces, cannot be sent.

### Add New Contact Screen
In the add new contact screen, you can add a new contact by entering their username. If the user exists, you will see the new conversation in the chat screen. If the user does not exist, you will receive an error message.

### Settings Screen
The settings screen allows you to customize various app settings. The available options include:
- Switching between dark and light mode based on your preference.
- Changing the API of the server by entering the server address. The expected format is: `http://d.d.d.d:port/api/`.
- If you are logged in, you can see your name and picture and also log out if you want.

### Change API Screen
The change API screen allows you to modify the server address used by the app. You can enter the new server address in the desired format: http://d.d.d.d:port/api/. After changing the API, you will be redirected back to the login screen to log in again with the updated server address.

### Notifications
If you are connected to the Android app, you will receive notifications when another user sends you a new message and you are not currently in the chat with them. You can click on the notification on your phone to open the conversation with the sender.

#### Please note: Make sure to enable notifications for this app in the settings of your Android device. If notifications are not enabled, you will not receive the notifications.

Note: All the client-side code is built inside the public folder of the server, so the app runs on the server with client-side rendering. The client is built using React and Android Studio.
