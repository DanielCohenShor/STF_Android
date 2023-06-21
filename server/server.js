const express = require('express');
const app = express();
const path = require('path');
const CustomEnv = require('custom-env');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const cors = require('cors');
const http = require('http');
const { Server } = require('socket.io');
const admin = require('firebase-admin');

CustomEnv.env(process.env.NODE_ENV, './config');

// Set the maximum request size limit
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));

app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

mongoose.connect(process.env.CONNECTION_STRING, {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

app.use(cors());

const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: 'http://localhost:3000',
    methods: ['GET', 'POST', 'DELETE']
  }
});
module.exports = { io };

var serviceAccount = require("./stf-speak-talk-friends-firebase-adminsdk-x3kd7-46b5c33735.json");

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  // Or use your server key directly:
  // credential: admin.credential.refreshToken(serverKey),
});

const users = require('./routes/Users');
const tokens = require('./routes/Tokens');
const chats = require('./routes/Chats');
const registerRouter = require('./routes/Register');

app.use(express.static('public/build'));
app.use('/api/Users', users);
app.use('/api/Tokens', tokens);
app.use('/api/Chats', chats);
app.use('/', registerRouter);

server.listen(process.env.PORT);
