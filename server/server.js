const express = require('express');
const app = express();
//the path of the server
const path = require('path');

// The library that allows using environment variables from config files
const CustomEnv = require('custom-env');

// Define the environment variables
CustomEnv.env(process.env.NODE_ENV, './config');

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

const mongoose = require('mongoose');
mongoose.connect(process.env.CONNECTION_STRING, {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

// Using CORS to allow Socket.IO communication on different ports
const cors = require('cors');
app.use(cors());

// Create the HTTP server
const http = require('http');
const server = http.createServer(app);

// Create the Socket.IO instance
const { Server } = require('socket.io');
const io = new Server(server, {
  cors: {
    origin: 'http://localhost:3000',
    methods: ['GET', 'POST', 'DELETE']
  }
});
// Export the io instance
module.exports = { io };

// Import and handle the sockets
const socketHandler = require('./HandleSocketServer');

//routes:
app.use(express.static('public/build'));

const users = require('./routes/Users');
app.use('/api/Users', users);

const tokens = require('./routes/Tokens');
app.use('/api/Tokens', tokens);

const chats = require('./routes/Chats');
app.use('/api/Chats', chats);

// handle request to the routes of the react
const registerRouter = require('./routes/Register');
app.use('/', registerRouter);

// Start the server
server.listen(process.env.PORT);