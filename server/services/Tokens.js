const User = require('../models/Users');

// Check if the user already exists
const validateInfromation = async (username, password) => {
    const existingUser = await User.findOne({ username });
    if (!existingUser) {
        return -1;
    }
    if (password !== existingUser.password) {
        return -1;
    }
    return 1;
}

module.exports = { validateInfromation };