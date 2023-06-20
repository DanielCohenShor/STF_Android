const tokensController = require('../controllers/Tokens');

const express = require('express');
var router = express.Router();

router.route('/').post(tokensController.validateInfromation);

module.exports = router;