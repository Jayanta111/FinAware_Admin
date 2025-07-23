const mongoose = require('mongoose');

const UserXpSchema = new mongoose.Schema({
  uid: { type: String, required: true, unique: true },
  totalXp: { type: Number, default: 0 },
  quizAttempts: [{
    courseId: String,
    score: Number,
    xpEarned: Number,
    attemptedAt: { type: Date, default: Date.now }
  }]
});

module.exports = mongoose.model('UserXp', UserXpSchema);
