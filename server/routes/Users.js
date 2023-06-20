const usersController = require('../controllers/Users');

const express = require('express');
var router = express.Router();

router.route('/').post(usersController.createNewUser);

router.route('/:username').get(usersController.returnInformationUser);

router.route('/Android').post(usersController.addAndroidToken);

router.route('/removeAndroid').post(usersController.removeAndroidToken);

module.exports = router;