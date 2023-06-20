const userService = require('../services/Users');
const { getUserNameFromToken } = require('./Chats');
const sharp = require('sharp');
const { promisify } = require('util');
const base64 = require('base64-js');

const createNewUser = async (req, res) => {
    const username = req.body.username;
    const password = req.body.password;
    const displayName = req.body.displayName;
    const profilePic = req.body.profilePic;
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
        
        const smallerProfilePic = await smaller(profilePic);
        res.json(await userService.createNewUser(username, password, displayName, smallerProfilePic));
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

async function smaller(photo) {
    let smallerPhoto = await removePrefix(photo);
    smallerPhoto = await decreasePhoto(smallerPhoto);
    smallerPhoto = addPrefix(smallerPhoto);
    return smallerPhoto;
  }
  
  function removePrefix(input) {
    const prefix = 'data:image/png;base64,';
    return input.substring(prefix.length);
  }
  
  async function decreasePhoto(photo) {
    try {
      const imageBuffer = Buffer.from(photo, 'base64');
      const image = sharp(imageBuffer);
  
      // Resize the image using sharp
      const resizedImage = await image.resize({ width: 800 }).jpeg({ quality: 80 }).toBuffer();
  
      // Encode the resized image back to a Base64 string
      const resizedBase64 = resizedImage.toString('base64');
  
      return resizedBase64;
    } catch (error) {
      console.error('Error resizing image:', error);
      return null;
    }
  }
  
  function addPrefix(input) {
    const prefix = 'data:image/png;base64,';
    return prefix + input;
  }

const returnInformationUser = async (req,res) => {
    if (req.headers.authorization) {
        const username = getUserNameFromToken(req.headers.authorization)
        if (username !== "Invalid Token") {
            const username = req.params.username;    
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

module.exports = { createNewUser, returnInformationUser };