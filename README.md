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

**Video Demo**
https://github.com/user-attachments/assets/0cd982f9-1f65-4693-82ac-89542aed424e

1. **Login**  
   <img width="1182" height="792" alt="ebacfb74-4086-48a7-91dc-9e4dbe9f7c2e" src="https://github.com/user-attachments/assets/7dbb82ef-c70b-49cb-a123-b3b20b87a111" />
2. **Dashboard**  
   <img width="1179" height="792" alt="6026e807-d863-4467-82bf-7fd20de74148" src="https://github.com/user-attachments/assets/c039f48c-9c4a-4b37-8cdd-81d2656bc36c" />
3. **Upload an Image**  
   <img width="1181" height="790" alt="aa80808f-1fc5-4d60-9cc2-86969baf26bc" src="https://github.com/user-attachments/assets/2c481667-0dc4-49cc-94c7-490e611325d7" />
4. **Extract Text**  
   <img width="1185" height="786" alt="image" src="https://github.com/user-attachments/assets/d0f60478-d193-4ae5-8af8-6069cea97319" />
   <img width="1184" height="786" alt="image" src="https://github.com/user-attachments/assets/679f7148-17b1-4542-aaf4-70a264664e9c" />
6. **View History**  
   <img width="1181" height="786" alt="image" src="https://github.com/user-attachments/assets/69f81d19-c428-44f0-b0c1-1d62c77265d2" />
   <img width="1182" height="789" alt="image" src="https://github.com/user-attachments/assets/a501a33f-a6c9-46dd-8e78-19c1324c8b40" />
8. **Settings Menu**  
   <img width="1181" height="790" alt="8aad8e8e-94c2-4312-8302-a20eb68b31e1" src="https://github.com/user-attachments/assets/7db1f6b9-e288-409d-b05a-b9f2dad76eda" />
9. **Change Accent Color**  
   <img width="1173" height="788" alt="01b3dea9-bac0-48da-8051-f617426fe39a" src="https://github.com/user-attachments/assets/a9c47b3b-eb3c-4ceb-abdb-43518ad8bf45" />
10. **Dark Mode Login**  
   <img width="1182" height="781" alt="919fdade-39eb-4114-a061-30066881d22f" src="https://github.com/user-attachments/assets/4f87b1c8-1063-401c-af6a-d92f97cca798" />


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
![Test Coverage](https://github.com/FriedricNietzsche/VisionText/blob/master/TestCoverage.md)

---

## Code Quality
- Followed Java naming conventions
- Checkstyle applied for formatting
- Example PR with review comments and fixes
- Pull Request Example:
(<img width="1869" height="2972" alt="image" src="https://github.com/user-attachments/assets/63d45b49-0e14-496b-ab9f-5d9515ff1c1e" />


---

## Team Members
- Krisvir Aujla
- Harry Wu
- Habib
- Jok
