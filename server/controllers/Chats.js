const chatsService = require('../services/Chats');
const STF = process.env.SECRET_TOKEN_KEY; // Accessing the environment variable
const jwt = require("jsonwebtoken")
const Chats = require('../models/Chats');

function extractTokenFromBearerString(bearerString) {
    const start = bearerString.indexOf('{');
    const end = bearerString.lastIndexOf('}') + 1;
    const jsonToken = bearerString.slice(start, end);
    const tokenObject = JSON.parse(jsonToken);
    return tokenObject.token;
}

function getUserNameFromToken(tokenFromCookie) {
    // Extract the token from that header
    try {
        const updateToken = extractTokenFromBearerString(tokenFromCookie)
        // Verify the token is valid
        const data = jwt.verify(updateToken, STF);
        return data.username
    } catch (err) {
        return "Invalid Token";
    }
}

const returnAllChats = async (req, res) => {
    if (req.headers.authorization) {
        const username = getUserNameFromToken(req.headers.authorization)
        if (username !== "Invalid Token") {
            const allChats = await chatsService.returnAllChats(username);
            res.status(200).send(allChats);
            return
        } else {
            return res.status(401).send("Invalid Token");
        }
    }
    else {
        return res.status(401).send('Token required');
    }
}

const createChat = async (req, res) => {
    const requestBody = req.body; // Assuming the JSON object is in the request body
    // get the login username
    const username = getUserNameFromToken(req.headers.authorization)
    if (username !== "Invalid Token") {
        const newChat = await chatsService.createChat(requestBody.username, username);
        if (newChat != -1) {
            res.status(200).json(newChat);
        } else {
            res.status(400).send('failed. problem with the DB');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const returnTheConversation = async (req, res) => {
    // get the login username
    const username = getUserNameFromToken(req.headers.authorization)
    if (username !== "Invalid Token") {
        const allMessages = await chatsService.returnTheConversation(req.params.id, username)
        if (allMessages != -1) {
            res.status(200).json(allMessages);
        } else {
            res.status(400).send('failed. problem with the DB');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const addNewMessage = async (req, res) => {
    const content = req.body.msg;
    const username = getUserNameFromToken(req.headers.authorization);
    if (username !== "Invalid Token") {
        const newMessage = await chatsService.addNewMessage(username, content, req.params.id);
        if (newMessage != -1) {
            res.status(200).json(newMessage);
        } else {
            res.status(400).send('failed. problem with the DB');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const returnAllTheMessages = async (req, res) => {
    const username = getUserNameFromToken(req.headers.authorization);
    if (username !== "Invalid Token") {
        const allTheMessages = await chatsService.returnAllTheMessages(req.params.id);
        if (allTheMessages != -1) {
            res.status(200).json(allTheMessages);
        } else {
            res.status(400).send('failed. problem with the DB');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const updateChats = async (req, res) => {
    const username = getUserNameFromToken(req.headers.authorization);
    const id = req.params.id;
    if (username !== "Invalid Token") {
        const chat = await Chats.findOne({ id });
        const userOne = chat.users[0].username
        const userTwo = chat.users[1].username
        const updatedChatsOne = await chatsService.updateChats(userOne ,id);
        const updatedChatsTwo = await chatsService.updateChats(userTwo ,id);
        if (updatedChatsOne != -1 && updatedChatsTwo != -1) {
            res.status(200).send("success");
        } else {
            res.status(400).send('failed. problem with the DB');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const deleteChat = async (req, res) => {
    const username = getUserNameFromToken(req.headers.authorization);
    const id = req.params.id;
    if (username !== "Invalid Token") {
        const isDeleted = await chatsService.deleteChat(username, id);
        if (isDeleted != -1) {
            res.status(200).json(isDeleted)
        } else {
            res.status(404).send('Not Found');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const getNotifications = async (req, res) => {
    const username = getUserNameFromToken(req.headers.authorization);
    if (username !== "Invalid Token") {
        const notifications = await chatsService.getNotifications(username);
        if (notifications != -1) {
            res.status(200).send(notifications);
        } else {
            res.status(404).send('Not Found');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const addNotification = async (req, res) => {
    const username = getUserNameFromToken(req.headers.authorization);
    const id = req.params.id;
    if (username !== "Invalid Token") {
        const okay = await chatsService.addNotification(username, id);
        if (okay != -1) {
            res.status(200).json(okay);
        } else {
            res.status(404).send('Not Found');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

const resetNotifications = async (req, res) => {
    const username = getUserNameFromToken(req.headers.authorization);
    const id = req.params.id;
    if (username !== "Invalid Token") {
        const okay = await chatsService.resetNotifications(username, id);
        if (okay != -1) {
            res.status(200).json(okay);
        } else {
            res.status(404).send('Not Found');
        }
    } else {
        return res.status(401).send('Token required');
    }
}

module.exports = { returnAllChats, createChat, returnTheConversation, addNewMessage, returnAllTheMessages, updateChats, deleteChat, getNotifications, addNotification, resetNotifications, getUserNameFromToken };  