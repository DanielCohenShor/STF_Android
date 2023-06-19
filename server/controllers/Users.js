const userService = require('../services/Users');
const { getUserNameFromToken } = require('./Chats');

const createNewUser = async (req, res) => {
    const username = req.body.username;
    const password = req.body.password;
    const displayName = req.body.displayName;
    const profilePic = req.body.profilePic;
    // const androidToken = "";
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{5,}$/;
    const usernameRegex = /[a-zA-Z]/;
    const invalidFields = [];
    var invalid = 0;

    // check if Username input is not empty
    if ((!username)) {
        invalidFields.push('username');
    }
    // check if the username meets the requirements of the regex
    else if (!usernameRegex.test(username)) {
        invalidFields.push('username');
    }

    invalid = await userService.validateUsername(username);

    // check if password input is not empty and meets the requirements of the regex
    if (!password || !passwordRegex.test(password)) {
        invalidFields.push('password');
    }

    // check if displayName input is not empty and meets the requirements of the regex
    if (!displayName || !usernameRegex.test(displayName)) {
        invalidFields.push('displayName');
    }

    // check if profilePic input is not empty
    if (!profilePic) {
        invalidFields.push('profilePic');
    }

    if (invalidFields.length === 0 && invalid > 0) {
        res.json(await userService.createNewUser(username, password, displayName, profilePic));
        return
    } else if (invalidFields.length > 0) {
        res.status(400).json({
            errors: invalidFields
        });
        return
    }

    if (invalid < 0) {
        res.status(409).json({
            title: "Conflict",
        });
        return
    }
};

const returnInformationUser = async (req,res) => {
    if (req.headers.authorization) {
        const username = getUserNameFromToken(req.headers.authorization);
        if (username !== "Invalid Token") {
            res.json(await userService.returnInformationUser(username));
            return
        } else {
            return res.status(403).send("Invalid Token");
        }
    }
    else {
        return res.status(401).send('Token required');
    }
};

const addAndroidToken = async (req,res) => {
    if (req.headers.authorization) {
        const username = getUserNameFromToken(req.headers.authorization);
        if (username !== "Invalid Token") {
            const androidToken = req.body.androidToken;
            res.json(await userService.saveAndroidToken(username, androidToken));
            return
        } else {
            return res.status(403).send("Invalid Token");
        }
    }
    else {
        return res.status(401).send('Token required');
    }
};

const removeAndroidToken = async (req,res) => {
    console.log("in")
    if (req.headers.authorization) {
        const username = getUserNameFromToken(req.headers.authorization);
        console.log("in1")
        if (username !== "Invalid Token") {
            res.json(await userService.saveAndroidToken(username, ""));
            return
        } else {
            return res.status(403).send("Invalid Token");
        }
    }
    else {
        return res.status(401).send('Token required');
    }
};

module.exports = { createNewUser, returnInformationUser, addAndroidToken, removeAndroidToken };