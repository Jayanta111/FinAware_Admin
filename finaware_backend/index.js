const express = require('express');
const multer = require('multer');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const Quiz = require('./models/Quiz');
const LearningEntry = require('./models/LearningEntry');

const app = express();

const multer = require('multer')
const upload = multer({ dest: 'uploads/' })

app.post('/uploads', upload.single('image'), (req, res) => {
  console.log('req.file:', req.file)  // ⬅️  this is debug
  if (!req.file) {
    return res.status(400).json({ error: 'No file uploaded' })
  }

  const imageUrl = `https://finaware-backend.onrender.com/uploads/${req.file.filename}`
  res.status(200).json({ imageUrl })
})

// ✅ Middleware
app.use(cors());
app.use(express.json());
app.use('/uploads', express.static('uploads')); // serve uploaded files

// ✅ MongoDB Connection
const mongoURI = process.env.MONGO_URI;
if (!mongoURI) {
  console.error('❌ MONGO_URI not defined');
  process.exit(1);
}
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
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'No file uploaded' });
    }
    const imageUrl = `https://finaware-backend.onrender.com/uploads/${req.file.filename}`;
    res.status(200).json({ imageUrl }); // send as JSON
  } catch (err) {
    console.error('❌ Image upload error:', err);
    res.status(500).json({ error: 'Image upload failed' });
  }
});

// ✅ Save new content with auto-generated courseId
app.post('/content', async (req, res) => {
  try {
    const latestEntry = await LearningEntry
      .findOne({ courseId: { $regex: /^course_\d+$/ } })
      .sort({ courseId: -1 });

    let nextIdNumber = 1;
    if (latestEntry && latestEntry.courseId) {
      const currentNumber = parseInt(latestEntry.courseId.replace('course_', ''));
      if (!isNaN(currentNumber)) {
        nextIdNumber = currentNumber + 1;
      }
    }

    const nextCourseId = `course_${nextIdNumber.toString().padStart(3, '0')}`;

    const entry = new LearningEntry({
      ...req.body,
      courseId: nextCourseId,
    });

    await entry.save();
    res.status(201).json({ success: true, courseId: nextCourseId });
  } catch (err) {
    console.error('❌ Error saving entry:', err);
    res.status(500).json({ error: err.message });
  }
});

// ✅ Create or update quiz
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

// ✅ Fetch all content
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

// ✅ Start server
app.listen(8080, () => {
  console.log('🚀 Server running at https://finaware-backend.onrender.com');
});
