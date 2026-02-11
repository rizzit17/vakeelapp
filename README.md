# LegalGPT (Vakeel App)

LegalGPT is a native Android application designed to provide AI-driven legal assistance. The app leverages Machine Learning for document processing and features a modular architecture for risk detection, compliance intelligence, and automated contract generation.

## 🚀 Features

- **Document Scanning (OCR):** Real-time text extraction from legal documents using Google ML Kit and CameraX.
- **Risk Detection:** AI-powered analysis to identify potential legal risks in scanned or uploaded text.
- **Compliance Intelligence:** Real-time checking against legal standards and regulations.
- **Legal Chat:** An interactive interface for legal queries powered by AI models.
- **Contract Generation:** Automated drafting of legal contracts based on user input.
- **Modern UI:** Built entirely with Jetpack Compose and Material3 following edge-to-edge design principles.

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/compose)
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & OkHttp
- **Machine Learning:** [Google ML Kit (Text Recognition)](https://developers.google.com/ml-kit/vision/text-recognition)
- **Camera:** [CameraX](https://developer.android.com/training/camerax)
- **Async Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- **Navigation:** [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## 🏗️ Architecture

The project follows the **MVVM (Model-View-ViewModel)** architectural pattern to ensure a clean separation of concerns and testability:
- **UI:** Jetpack Compose screens.
- **ViewModel:** Manages UI state and business logic using Coroutines.
- **Data Layer:** API interaction via Retrofit and local models.

## 📋 Prerequisites

- Android Studio Koala or newer.
- Android SDK 35 (Compile SDK).
- Minimum SDK 24.

## 🔧 Installation

1. Clone the repository:
   ```bash
   git clone [https://github.com/rizzit17/vakeelapp.git](https://github.com/rizzit17/vakeelapp.git)
Open the project in Android Studio.

Sync the project with Gradle files.

Run the application on an emulator or a physical device.

📁 Project Structure
app/src/main/java/com/example/legalgpt/
├── api/          # Retrofit API interfaces
├── models/       # Data classes and API response models
├── ui/           # Compose themes and reusable components
├── viewmodels/   # Logic for OCR, Risk Detection, and Chat
├── MainActivity  # Entry point with NavGraph integration
└── NavGraph      # Application routing and navigation

📜 License
This project is licensed under the MIT License.
