const express = require('express');
const multer = require('multer');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config(); // ✅ Load .env

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
app.post('/upload', upload.single('image'), (req, res) => {
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
//Quize
app.post("/create-quiz", async (req, res) => {
  const { courseId, title, questions } = req.body;

  if (!courseId || !title || !questions || !Array.isArray(questions)) {
    return res.status(400).json({ error: "Invalid payload" });
  }

  try {
    await db.collection("quizzes").insertOne({
      courseId,
      title,
      questions,
      createdAt: new Date(),
    });

    res.status(201).json({ message: "Quiz saved successfully" });
  } catch (e) {
    res.status(500).json({ error: "Failed to save quiz" });
  }
});
// ✅ Content fetching route
app.get('/content', async (req, res) => {
  try {
    const entries = await LearningEntry.find({});
    res.status(200).json(entries);
  } catch (err) {
    console.error('❌ Error fetching entries:', err);
    res.status(500).json({ error: err.message });
  }
});
// ✅ Start the server
app.listen(8080, () => {
  console.log('🚀 Server running at https://finaware-backend.onrender.com');
});
