# VisionText

VisionText is a Java Swing application that allows users to extract text from images using the OCR.space API, manage their conversion history, and authenticate using Firebase Authentication. The app is designed with Clean Architecture and SOLID principles in mind.

## Features

- **User Management**: Register, login, and logout with Firebase Authentication.
- **OCR Text Extraction**: Upload images (JPG, PNG), extract text using OCR.space, and edit the result.
- **History**: Save and view conversion history, including original filename, extracted text, and timestamp, stored in Firebase Firestore.
- **File Saving**: Save extracted/edited text as local `.txt` files.
- **Error Handling**: User-friendly error messages for authentication, OCR, and file operations.
- **Modern Java Swing UI**: Login, dashboard, and history screens.

## Architecture

- **UserManager**: Handles registration, login, and user history retrieval.
- **OCRService**: Handles image upload and OCR.space API integration.
- **HistoryManager**: Manages Firestore interactions for conversion history.
- **MainAppUI**: All Swing UI screens and actions.
- **ErrorHandler**: Centralized error display and logging.

## Setup

1. **Java Version**: Java 11 or higher recommended.
2. **Dependencies**: Uses OkHttp, Gson, and Firebase Admin SDK (or REST API). See `pom.xml` for Maven dependencies.
3. **API Keys**:
   - OCR.space API key: Set in `config.properties` or as an environment variable.
   - Firebase project credentials: Place your `serviceAccountKey.json` in the project root (if using Admin SDK), or configure REST API keys.
4. **Build & Run**:
   - With Maven: `mvn clean install && mvn exec:java -Dexec.mainClass="app.MainAppUI"`
   - Or use your IDE to run `app.MainAppUI`.

## Project Structure

```
src/
  app/
    MainAppUI.java
    ErrorHandler.java
  domain/
    UserManager.java
    OCRService.java
    HistoryManager.java
  util/
    Config.java
    FirebaseUtil.java
```

## Configuration

- `config.properties` example:
  ```
  ocr.api.key=YOUR_OCR_SPACE_API_KEY
  firebase.api.key=YOUR_FIREBASE_API_KEY
  firebase.project.id=YOUR_FIREBASE_PROJECT_ID
  ```

## License

MIT 