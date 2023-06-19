const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const message = new Schema({
  id: {
    type: Number,
    default: ""
  },
  created: {
    type: String,
    default: ""
  },
  sender: {
    username: {
      type: String,
      default: ""
    },
    displayName: {
      type: String,
      default: ""
    },
    profilePic: {
      type: String,
      default: ""
    }
  },
  content: {
    type: String,
    default: ""
  }
});

module.exports = mongoose.model('Message', message);