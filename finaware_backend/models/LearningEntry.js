const mongoose = require("mongoose");

const learningEntrySchema = new mongoose.Schema({
 courseId: {
    type: String,
    required: true,
    unique: true,
  },  title: String,
  imageUrl: String,
  intro: String,
  example: String,
  prevention: String,
  quiz: String,
  language: String,
});

module.exports = mongoose.model("LearningEntry", learningEntrySchema);
