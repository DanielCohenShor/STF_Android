const tokenService = require('../services/Tokens');
const jwt = require("jsonwebtoken")
const STF = process.env.SECRET_TOKEN_KEY; // Accessing the environment variable

const validateInfromation = async (req, res) => {
    const username = req.body.username;
    const password = req.body.password;

    const checkValidate = await tokenService.validateInfromation(username, password);

    if(checkValidate < 0) {
        res.status(404).send('Incorrect username and/or password');
        return
    } else {

        const data = { username: username }
        // Generate the token.
        const token = jwt.sign(data, STF)
        
        // Return the token to the browser
        res.cookie("token", token, {
            httponly: true
        })
        res.status(200).json({ token });
    }
}

module.exports = { validateInfromation };
