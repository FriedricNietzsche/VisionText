# VisionText – OCR Text Extractor with Login & History

## Description
VisionText is a Java Swing desktop application that allows users to securely log in, upload images, and extract text using the OCR.space API. Extracted text can be copied or saved for later, and a history of uploads is stored in Firebase Firestore.

---

## Features
- **Secure Authentication** – Firebase Authentication for login/signup
- **Image Upload** – Drag-and-drop or file picker
- **OCR Processing** – OCR.space API for text extraction
- **History Tracking** – View and manage recent uploads
- **Copy/Save Text** – Quickly copy results or store them for later
- **Cross-Platform** – Runs on any OS with Java installed

---

## Tech Stack
- **Java Swing** – UI Framework
- **Firebase** – Authentication & Firestore Database
- **OCR.space API** – OCR service
- **JUnit** – Unit testing

---

## Architecture Overview
Follows **Clean Architecture** and **SOLID** principles.

![Architecture Diagram](screenshots/architecture.png)

---

## Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/VisionText.git
   cd VisionText
   ```
2. Install dependencies:
   - Java 17+
   - Maven (if using Maven)
3. Configure Firebase:
   - Add your `google-services.json` or Firebase config
4. Configure OCR API key in `config.properties`
5. Run the app:
   ```bash
   mvn exec:java -Dexec.mainClass="com.visiontext.Main"
   ```

---

## Usage Instructions
1. **Login**  
   ![Login Screen](screenshots/login.png)
2. **Dashboard**  
   ![Dashboard Screen](screenshots/dashboard.png)
3. **Upload an Image**  
   ![Upload Screen](screenshots/upload.png)
4. **Extract Text**  
   ![OCR Result Screen](screenshots/ocr_result.png)
5. **View History**  
   ![History Screen](screenshots/history.png)
6. **Settings Menu**  
   ![Settings Screen](screenshots/settings.png)
7. **Change Accent Color**  
   ![Color Picker Screen](screenshots/color_picker.png)
8. **Dark Mode Login**  
   ![Dark Mode Login Screen](screenshots/login_dark.png)

---

## API Documentation

### Firebase Authentication
- Endpoint: `signInWithEmailAndPassword(email, password)`

### OCR.space API
- Endpoint: `POST /parse/image`
- Parameters:
  - `apikey` – Your API key
  - `file` – Image file to process

---

## Testing
- Unit tests for all interactors and services
- Mocked Firebase & OCR API for offline testing
- Code coverage: **>80%**  
![Test Coverage](screenshots/test_coverage.png)

---

## Accessibility Report Summary
- **Principles Applied:**
  - Perceptibility – Clear fonts and button labels
  - Error prevention – Confirmation dialogs before deletion
- **Target Users:** Students & professionals needing quick OCR
- **Limitations:** Not optimized for screen readers

---

## Code Quality
- Followed Java naming conventions
- Checkstyle applied for formatting
- Example PR with review comments and fixes  
![Pull Request Example](screenshots/pr_screenshot.png)

---

## Team Members
- Krisvir Aujla
- Harry Wu
- Habib
- Jok
