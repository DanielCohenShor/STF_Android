const chatsController = require('../controllers/Chats');

const express = require('express');
var router = express.Router();

router.route('/').get(chatsController.returnAllChats);

router.route('/').post(chatsController.createChat);

router.route('/:id').get(chatsController.returnTheConversation);

router.route('/:id/Messages').post(chatsController.addNewMessage);

router.route('/:id/Messages').get(chatsController.returnAllTheMessages);

router.route('/Update/:id').get(chatsController.updateChats);

router.route('/:id').delete(chatsController.deleteChat);

router.route('/Notifications').post(chatsController.getNotifications);

router.route('/Notifications/:id').post(chatsController.addNotification);

router.route('/Notifications/:id').get(chatsController.resetNotifications);

module.exports = router;