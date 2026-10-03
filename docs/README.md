# PulseFinance Documentation Portal

Welcome to the official technical documentation for **PulseFinance** — a problem-first, scenario-driven personal finance management system engineered in 100% Kotlin and Jetpack Compose for Android (API 26+).

---

## 📚 Documentation Index

| Document | Description | Target Audience |
| :--- | :--- | :--- |
| [**Project Master Document**](PROJECT_DOCUMENTATION.md) | Complete, consolidated single-file documentation for the entire project. | Evaluators, Architects, Reviewers |
| [**Architecture & System Design**](ARCHITECTURE.md) | Clean Architecture, MVVM + UDF, Room & Firestore dual-sync, Hilt DI, Coroutines. | Engineers & System Architects |
| [**Vivid Neo-Fintech Design System**](DESIGN_SYSTEM.md) | Design tokens, color palette, typography, glassmorphism, capsule metrics, and micro-interactions. | UI/UX Designers & Mobile Devs |
| [**Features Specification**](FEATURES_SPECIFICATION.md) | Screen-by-screen breakdown matching Stitch prototypes (Cockpit, Income Hub, Goals, Analytics, Zombie Audit). | Product Managers & QA |
| [**Data Models & Storage**](DATA_MODELS_AND_STORAGE.md) | Entity schemas, Room DAOs, Firestore collections, TypeConverters, multi-currency conversion. | Backend & Database Engineers |
| [**Security & API Integration**](API_AND_SECURITY.md) | Firebase Auth, Firestore security rules, currency normalization, and privacy controls. | Security Engineers & DevOps |
| [**Setup & Contribution Guide**](SETUP_AND_CONTRIBUTION_GUIDE.md) | JDK 21 setup, Android Studio configuration, Firebase initialization, building APKs. | Developers & Contributors |

---

## 🎯 Project Overview & Problem Statement

PulseFinance was designed specifically for **Kavindu Silva (25)**, a Junior Software Engineer in Colombo, Sri Lanka who manages an irregular multi-stream income (LKR salary, foreign client USD invoices, crypto USDT earnings) and faces lifestyle inflation, hidden subscription leaks, and stalled savings goals.

### Key Innovations
- **Multi-Stream Income Normalization**: Real-time currency conversion (USD/USDT $\to$ LKR) separating *Realized Liquid Cash* from *Pending Milestone Invoices*.
- **Goal Velocity Engine**: Dynamic pacing and interactive **Opportunity Cost Translation** (e.g., *“Skipping this LKR 2,500 cafe dining funds 1.9 days of your MacBook Pro M4”*).
- **Zombie Subscription Hunter**: Detects recurring debits across multi-bank accounts with one-tap “Nuke” cancellation.
- **Cockpit Safe-To-Spend**: Real-time safe allowance after deducting committed rent (LKR 34k) and goal allocations.

---

## 🚀 Quick Navigation

- Need to run the project locally? Follow the [Setup Guide](SETUP_AND_CONTRIBUTION_GUIDE.md).
- Inspecting the UI tokens and layout? Check the [Design System](DESIGN_SYSTEM.md).
- Reviewing the database schemas? See [Data Models](DATA_MODELS_AND_STORAGE.md).
- Reading the complete full specification? Open [Project Master Document](PROJECT_DOCUMENTATION.md).
