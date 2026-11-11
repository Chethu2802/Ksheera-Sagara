# Ksheera Sagara (ಕ್ಷೀರ ಸಾಗರ) - Smart Dairy Farm Management

Ksheera Sagara is a smart dairy farm management Android application developed using Kotlin and Jetpack Compose. The application helps farmers and dairy vendors manage milk records, track income and expenses, analyze dairy data, and generate reports through a user-friendly mobile platform.

## Problem Statement
Small dairy farmers in Karnataka maintain milk sales, feed costs, and profits manually in notebooks. This causes calculation errors, lack of financial transparency, and no insight into business growth. There is a need for a simple, offline-first mobile app designed for non-technical farmers to digitize daily dairy operations.

## Project Overview
Ksheera Sagara digitizes the complete dairy workflow. Farmers can register, log daily milk income, add expenses, view auto-calculated profit, analyze cow-wise performance, get AI-based suggestions, and generate PDF reports for records - all offline using Room Database.

## Key Features
- User Login and Signup with secure authentication
- Income and Expense Management with calendar view
- Calendar-Based Milk Entry
- Analytics Dashboard with Charts
- Cow Analysis Module for individual cow tracking
- AI-Based Suggestions and Chatbot
- PDF Report Generation for monthly records
- Notifications and Alerts for daily entry
- Offline Data Storage using Room Database
- Simple and User-Friendly Interface for rural users

## Technologies Used
- Language: Kotlin
- UI: Jetpack Compose, Material Design 3
- Architecture: MVVM Architecture
- Database: Room Database, Firebase
- Tools: Android SDK, Kotlin Coroutines, Android Studio
- AI: Generative AI Tools

## Project Structure
app/
 ├── data/         - Database, Entities, DAOs
 ├── repository/   - Repository layer
 ├── viewmodel/    - ViewModels
 ├── ui/           - Compose Screens (Login, Dashboard, Analytics)
 ├── navigation/   - Navigation Graph
 └── utils/        - Helper classes

## Installation Instructions
1. Clone the repository
   git clone https://github.com/Chethu2802/Ksheera-Sagara.git
2. Open in Android Studio
   File -> Open -> Select Ksheera-Sagara folder
3. Sync Gradle Files
   Let Android Studio sync all dependencies automatically
4. Run the Application
   Connect Android device or start Emulator and click Run

## Screenshots
- Home Dashboard - Shows total profit, income, expense
- Add Income Screen - Add daily milk sale
- Add Expense Screen - Add feed, medical costs
- Profit Analysis Screen - Charts and monthly growth
- History Screen - Past records and PDF export

[Create /screenshots folder and add your emulator screenshots]

## How It Works
1. Farmer opens app and sees dashboard with profit
2. Clicks Add Income to enter today's milk sale
3. Clicks Add Expense to enter feed/medical cost
4. App auto-calculates Profit = Income - Expense
5. Farmer views analytics and generates monthly PDF report

## Future Enhancements
- Cloud backup using Firebase Firestore
- Multi-language support (Kannada, English)
- Milk rate calculator based on FAT and SNF
- UPI payment integration for milk buyers
- SMS notification for payment reminders
- Customer management module
- Deploy to Google Play Store
- Advanced AI recommendations for feed optimization

## My Learning From This Project
- Learned Kotlin and Jetpack Compose from scratch
- Understood Android app lifecycle and MVVM architecture
- Worked with Room database for offline storage
- Designed simple UI for non-technical users (farmers)
- Applied Generative AI during MindMatrix.io internship for prototyping

## Developed By
Chethan H M
BE CSE
ATME College of Engineering, Mysuru
Android + Gen AI Prototyping Intern @ MindMatrix.io
MindMatrix TalentTrack - HireReady 2026

## License
This project is developed for educational and internship purposes.
