const mongoose = require('mongoose');

// One question schema
const QuestionSchema = new mongoose.Schema({
  questionText: { type: String, required: true },
  options: [{ type: String, required: true }], // MCQ options
  correctAnswer: { type: String, required: true }, // Can be the correct option text
});

// Full quiz schema per course
const QuizSchema = new mongoose.Schema({
  courseId: { type: String, required: true },
  title: { type: String, required: true },
  questions: [QuestionSchema],
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Quiz', QuizSchema);
