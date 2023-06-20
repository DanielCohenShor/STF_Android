const Chats = require("./models/Chats");
const { io } = require("./server");

function socketHandler() {
    io.on('connection', socket => {

        socket.on("sendMessage", (data) => {
            // send to the client the id of the chat with the new message
            socket.to(data.id).emit("receive_message", data);
        })

        socket.on("join_chat", (id) => {
            socket.join(id)
        })

        socket.on("add_contact", async (data) => {
            io.emit("receive_newContact", data);
        })

        socket.on("updateChats", async (data) => {
            socket.to(data).emit("receiveUpdateChats", data);
        })

        socket.on("afterDelete", async (id) => {
            socket.to(id).emit("notifyDelete");
        })

        socket.on('disconnect', () => {});
    })
}

module.exports = socketHandler;
socketHandler();