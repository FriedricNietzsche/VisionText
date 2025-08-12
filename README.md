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
![Test Coverage](screenshots/test_coverage.png)

---

## Accessibility Report Summary

1.
- Principle 1: Equitable Use
   - The current reliance on a graphical user interface (GUI) may present challenges for users with visual impairments who use screen readers.
   - Future Feature: The program could be designed to include proper labeling and descriptions for all GUI elements, making it compatible with assistive technologies. An optional text-to-speech function for the final OCR output would also be an excellent feature for accessibility.
- Principle 2: Flexibility in Use
   - The program's simple upload-and-scan workflow offers a clear and straightforward path for all users.
   - Future Feature: To accommodate more preferences, the application could offer customizable settings such as high-contrast modes, adjustable font sizes for the OCR output display, and multiple export formats (e.g., plain text, PDF, HTML) to suit different needs.
- Principle 3: Simple and Intuitive Use
   - The core functionality of uploading an image and receiving an OCR scan is inherently simple. The program's design, which relies purely on a GUI, can be made intuitive by using clear icons and a logical, step-by-step layout.
   - Future Feature: The user flow could be guided with clear prompts and visual cues to ensure even a first-time user can successfully operate the program without a manual.
- Principle 4: Perceptible Information
   - The program will provide clear, visual feedback to the user at all stages, from file upload to the final OCR result display. A progress bar and status message have been implemented to clearly inform the user that the OCR process is running, preventing confusion about whether the program is working. 
   - Future Feature: The final text output should be displayed in a highly readable format with a choice of fonts and sizes.
- Principle 5: Tolerance for Error
   - The application is designed with clear, non-technical error messages if an image upload fails (e.g., wrong file type) or if the OCR process is unable to recognize text.
   - Future Feature: The program could include a "preview" feature before processing to allow users to verify the correct image has been selected. The OCR output could also have an edit mode, allowing users to correct any mistakes without having to re-scan.
- Principle 6: Low Physical Effort
   - The program's user experience is designed for minimal physical effort, requiring only a few mouse clicks or taps to complete the task.
- Principle 7: Size and Space for Approach and Use
   - This principle does not apply to our application as a platform agnostic service, it does not impose any specific restrictions on the size and space required for approach and use.

2.
- Marketing Our Program
   - Our application VisionText was made with students and academic researchers in mind. They frequently deal with the challenge of converting physical media formats such as books, printed articles, and handwritten notes into a digital one. The program's simple and efficient OCR capability would appeal to them as a tool to both save time and potentially reduce the rate of error by eliminating the need for manual transcription. It could also be pitched as an invaluable study aid for creating digital study notes from physical sources, organizing research material, and making legacy documents searchable. In essence, the program would be positioned as an essential companion for anyone who needs to quickly and accurately bridge the gap between physical and digital media formats.

3.
- Potential for Demographic Disparities
   - It is unlikely that VisionText could be used by those who rely on documents that are hard to parse or use non-standard scripts. Many OCR engines, while highly effective on typed and clean documents, struggle with handwriting, scripts from various world languages, or older, faded documents. This creates a disparity where users with access to modern, typed, English-language texts find the tool incredibly useful, while those with different linguistic backgrounds or historical documents find it unreliable and frustrating. It's crucial for the development of VisionText to consider these potential biases and limitations to ensure it is an equitable and useful tool for the widest possible audience.

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
