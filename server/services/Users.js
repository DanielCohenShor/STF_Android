const User = require('../models/Users');

const createNewUser = async (username, password, displayName, profilePic) => {
    const user = new User({ username, password, displayName, profilePic });
    user.chats = [] // in the creation we have 0 chats.
    return await user.save();

};

// Check if username already exists
const validateUsername = async (username) => {
    const existingUser = await User.findOne({ username });
    if (existingUser) {
        return -1;
    }
    return 1;
}

const returnInformationUser = async (username) => {
    const user = await User.findOne({ username })
    const userData = {
        username: user.username,
        profilePic: user.profilePic,
        displayName: user.displayName
    };

    return userData
}

module.exports = { createNewUser, validateUsername, returnInformationUser };