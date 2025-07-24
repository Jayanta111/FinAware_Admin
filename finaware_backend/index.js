const express = require('express');
const multer = require('multer');
const mongoose = require('mongoose');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

const Quiz = require('./models/Quiz');
const LearningEntry = require('./models/LearningEntry');

const app = express();

// ✅ Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// ✅ Serve static files from uploads folder
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// ✅ MongoDB Connection
const mongoURI = process.env.MONGO_URI;
if (!mongoURI) {
  console.error('❌ MONGO_URI not defined in .env');
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

// ✅ Ensure uploads folder exists
const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir);
}

// ✅ Multer configuration
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadDir);
  },
  filename: function (req, file, cb) {
    const ext = path.extname(file.originalname);
    const fileName = `${Date.now()}${ext}`;
    cb(null, fileName);
  }
});

const fileFilter = (req, file, cb) => {
  if (file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
    cb(null, true);
  } else {
    cb(new Error('Only JPG and PNG are allowed!'), false);
  }
};

const upload = multer({ storage, fileFilter });

// ✅ Upload Endpoint
app.post('/uploads', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ success: false, message: 'No file uploaded' });
  }

  const url = `https://finaware-backend.onrender.com/uploads/${req.file.filename}`;

  res.status(200).json({
    success: true,
    filename: req.file.filename,
    message: 'File uploaded successfully',
    filePath: url
  });
});

// ✅ Create learning content with auto courseId
app.post('/content', async (req, res) => {
  try {
    const latestEntry = await LearningEntry
      .findOne({ courseId: { $regex: /^course_\d+$/ } })
      .sort({ courseId: -1 });

    let nextIdNumber = 1;
    if (latestEntry?.courseId) {
      const currentNumber = parseInt(latestEntry.courseId.replace('course_', ''));
      if (!isNaN(currentNumber)) {
        nextIdNumber = currentNumber + 1;
      }
    }

    const nextCourseId = `course_${nextIdNumber.toString().padStart(3, '0')}`;
    const entry = new LearningEntry({ ...req.body, courseId: nextCourseId });

    await entry.save();
    res.status(201).json({ success: true, courseId: nextCourseId });
  } catch (err) {
    console.error('❌ Error saving content:', err);
    res.status(500).json({ error: err.message });
  }
});

// ✅ Create quiz
app.post('/create-quiz', async (req, res) => {
  try {
    const quiz = new Quiz(req.body);
    await quiz.save();
    res.status(201).json({ success: true, message: 'Quiz saved' });
  } catch (error) {
    console.error('❌ Error saving quiz:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// ✅ Get quizzes
app.get('/quiz', async (req, res) => {
  try {
    const quizzes = await Quiz.find({});
    res.status(200).json(quizzes);
  } catch (error) {
    console.error('❌ Failed to fetch quizzes:', error);
    res.status(500).json({ error: error.message });
  }
});

// ✅ Get learning content
app.get('/content', async (req, res) => {
  try {
    const data = await LearningEntry.find({});
    res.status(200).json(data);
  } catch (err) {
    console.error('❌ Failed to fetch content:', err);
    res.status(500).json({ error: err.message });
  }
});

// ✅ Start server
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`🚀 Server running at https://finaware-backend.onrender.com:`);
});
