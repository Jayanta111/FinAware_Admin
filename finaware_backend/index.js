const express = require('express');
const multer = require('multer');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config(); // ✅ Load .env
const Quiz = require('./models/Quiz');
const LearningEntry = require('./models/LearningEntry');

const app = express();
const upload = multer({ dest: 'uploads/' });

// Middleware
app.use(cors());
app.use(express.json());
app.use('/uploads', express.static('uploads'));

// ✅ Verify that MONGO_URI is loaded
const mongoURI = process.env.MONGO_URI;
if (!mongoURI) {
  console.error('❌ MONGO_URI not defined in environment');
  process.exit(1); // stop server if not configured
}

// ✅ Connect to MongoDB Atlas
mongoose.connect(mongoURI, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
})
  .then(() => console.log('✅ Connected to MongoDB'))
  .catch(err => {
    console.error('❌ MongoDB error:', err);
    process.exit(1);
  });

// ✅ Image upload route
app.post('/uploads', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'No file uploaded' });
  }
  const imageUrl = `https://finaware-backend.onrender.com/uploads/${req.file.filename}`;
  res.status(200).send(imageUrl);
});

// ✅ Content saving route
app.post('/content', async (req, res) => {
  try {
    const entry = new LearningEntry(req.body);
    await entry.save();
    res.status(201).json({ success: true });
  } catch (err) {
    console.error('❌ Error saving entry:', err);
    res.status(500).json({ error: err.message });
  }
});
// Create or update quiz
app.post('/create-quiz', async (req, res) => {
  try {
    console.log('📥 Received quiz payload:', JSON.stringify(req.body, null, 2));
    const quiz = new Quiz(req.body);
    await quiz.save();
    res.status(201).json({ success: true, message: 'Quiz saved' });
  } catch (error) {
    console.error('❌ Error saving quiz:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});
// content.routes.js or in main file
app.get('/content', async (req, res) => {
  try {
    console.log("📥 GET /content called");
    const data = await LearningEntry.find({});
    res.status(200).json(data);
  } catch (err) {
    console.error("❌ Failed to fetch content:", err);
    res.status(500).json({ error: err.message });
  }
});
// ✅ Start the server
app.listen(8080, () => {
  console.log('🚀 Server running at https://finaware-backend.onrender.com');
});
