# PulseFinance — Personal Finance Management System for Android
**Module:** SE3092: Platform Based Development (Year 3 - Semester 2 - 2026)  
**Assignment:** 01 — Problem-First, Scenario-Driven Engineering Challenge  
**Target User Persona:** Kavindu Silva (25, Junior Software Engineer, Colombo, Sri Lanka)  
**Technology Stack:** Kotlin (100%) · Jetpack Compose (Material 3) · MVVM · Firebase Auth & Firestore · Room DB · Hilt DI · Coroutines & StateFlow  
**Compatibility:** Minimum SDK API 26 (Android 8.0 Oreo) · Target SDK 34+  

---

## 1. Scenario Traceability Matrix (Solving Kavindu's Problems)

| Kavindu's Pain Point | Why Past Tools Failed Him | PulseFinance Architectural & UX Solution |
| :--- | :--- | :--- |
| **Irregular Multi-Stream Income** (Salary LKR 120k–140k, Freelance milestones, AdSense USD, Crypto USDT/ETH) | Past apps assumed a single fixed monthly income. Google Sheet formulas broke on row insertion. | **`IncomeStream` Domain Model & Repository:** Segregates *Realized Cash* from *Pending Milestones*. Auto-normalizes USD and USDT to LKR at prevailing rates while preserving original values. |
| **Forgotten Freelance Invoices** (Twice forgot to follow up on SME milestones) | Notes app and bank accounts only showed what arrived, never tracking unbilled/unpaid invoices. | **Overdue Invoice Engine & Alert Banner:** Prominently highlights pending and overdue client milestones directly on the Cockpit dashboard with one-tap status reconciliation. |
| **Discretionary Spending Invisibility** (Shocked by LKR 24k spent on coffee & dining; no categories) | Bank statements only showed card charges, omitting cash and secondary accounts; miscategorized PickMe as 'retail'. | **Committed vs. Discretionary Split Engine:** Distinguishes fixed committed obligations (Rent LKR 34k, utilities) from lifestyle leaks (coffee, PickMe, dining) with visual split gauges and instant auto-tagging. |
| **Zombie Subscriptions** (Gym membership and 2 forgotten subscriptions debited from secondary bank) | Bank native summaries only tracked the primary card, leaving secondary account auto-debits invisible. | **Unified Account Aggregation & Zombie Audit:** Tracks Primary Bank, Secondary Account, Cash, and Binance. Features a dedicated *Zombie Subscription Audit* screen with cancellation action items. |
| **MacBook Pro M4 Goal Stagnation** (LKR 490k target; only saved LKR 11.2k in 12 months) | Goal was emotionally present but financially disconnected from daily purchase decisions. | **Goal Velocity Engine & Opportunity Cost Converter:** Computes exact daily pacing (~LKR 1,330/day) and monthly allocation (~LKR 39,900/mo). Provides an interactive converter showing how skipping a LKR 2,500 dinner directly funds 1.9 days of the MacBook. |
| **Data Entry Friction** (Abandoned Google Sheet in 13 days; Notes app messy) | Rigid forms and excessive manual input caused behavioral dropout. | **3-Tap Speed Logger:** Bottom sheet with instant Sri Lankan tech-worker presets (`☕ Cafe LKR 850`, `🚗 PickMe LKR 1,450`, `🍔 UberEats LKR 2,400`, `🏠 Rent LKR 34,000`). |

---

## 2. Architecture & Layering (Strict MVVM)

The project follows clean architecture principles with unidirectional data flow (UDF):

```
┌─────────────────────────────────────────────────────────────┐
│                    UI LAYER (Jetpack Compose)               │
│  - AppNavGraph (Single-Activity Navigation Compose)         │
│  - DashboardScreen          - AddExpenseBottomSheet         │
│  - IncomeScreen             - AddIncomeBottomSheet          │
│  - GoalScreen               - AnalyticsScreen               │
│  - AuthScreen               - Material 3 Custom Theme       │
└──────────────────────────────┬──────────────────────────────┘
                               │ StateFlow (UI State)
                               │ User Events & Intents
┌──────────────────────────────▼──────────────────────────────┐
│                  VIEWMODEL LAYER (AAC ViewModel)            │
│  - DashboardViewModel        - ExpenseViewModel             │
│  - IncomeViewModel           - GoalViewModel                │
│  - AnalyticsViewModel        - AuthViewModel                │
└──────────────────────────────┬──────────────────────────────┘
                               │ Kotlin Coroutines & Flow
┌──────────────────────────────▼──────────────────────────────┐
│                    DOMAIN / USECASE LAYER                   │
│  - CalculateFinancialSummaryUseCase                         │
│  - CalculateGoalVelocityUseCase                             │
│  - ConvertCurrencyUseCase                                   │
│  - Repository Interfaces (Auth, Expense, Income, Goal, Acc) │
└──────────────────────────────┬──────────────────────────────┘
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
┌─────────────────────────────┐ ┌─────────────────────────────┐
│   LOCAL DATA (Room SQLite)  │ │   REMOTE DATA (Firebase)    │
│  - AppDatabase              │ │  - FirebaseAuthDataSource   │
│  - ExpenseDao, IncomeDao    │ │  - FirestoreDataSource      │
│  - GoalDao, AccountDao      │ │  - Real-time SnapshotFlow   │
│  - Local Entities           │ │  - Remote DTOs              │
└─────────────────────────────┘ └─────────────────────────────┘
```

---

## 3. Firebase Configuration & Setup Steps

1. **Create Firebase Project:**
   - Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project named `pulse-finance`.
2. **Add Android App:**
   - Register the Android application with package name: `com.se3092.pulsefinance`.
   - Download the generated `google-services.json` and replace the placeholder in `app/google-services.json`.
3. **Enable Firebase Authentication:**
   - In the Firebase Console, go to **Build** $\to$ **Authentication** $\to$ **Sign-in method**.
   - Enable **Email/Password** provider.
4. **Enable Cloud Firestore:**
   - In the Firebase Console, go to **Build** $\to$ **Firestore Database** $\to$ **Create database**.
   - Select production mode and your preferred region.
5. **Deploy Security Rules:**
   - Copy the contents of `firestore.rules` (located in the project root) into the Firestore Rules tab in the console, or deploy via Firebase CLI:
     ```bash
     firebase deploy --only firestore:rules
     ```

---

## 4. Build & Run Instructions

### Prerequisites
- Android Studio Ladybug / Koala / Hedgehog (2024+) or newer.
- JDK 17 or higher (configured in Android Studio under Settings $\to$ Build $\to$ Gradle).
- Android SDK Platforms 26 through 34 installed.

### Opening in Android Studio
1. Open Android Studio.
2. Select **Open** and select `/Users/hesaraperera/.gemini/antigravity-ide/scratch/PulseFinance`.
3. Allow Gradle to sync dependencies automatically.
4. Select an emulator or connected physical device running Android 8.0 (API 26) or higher.
5. Click **Run 'app'** (`Shift + F10`).

### Automated Test Accounts & Seeding
- On the first sign-in with the demo persona, the app initializes Kavindu's accounts (once per account; the flag is stored on the Firestore user document) (Commercial Bank Primary, Secondary Account, Cash Wallet, and Binance), three months of recurring subscription debits, his income streams, and his baseline MacBook Pro M4 goal (LKR 490,000 target, LKR 11,200 initial saved).
- Test login credentials:
  - **Email:** `kavindu.silva@pulsemail.io`
  - **Password:** `ColomboDev2026!`
- Any other account starts empty with a single zero-balance "Main Account".

### Google Sign-In Setup
1. In the Firebase Console, enable the **Google** provider under Authentication → Sign-in method.
2. Add your debug/release **SHA-1** fingerprints to the Android app (`./gradlew signingReport`).
3. Download the refreshed `google-services.json` (it now contains a Web OAuth client) into `app/`.
   Alternatively, paste the Web client ID into `google_web_client_id` in `app/src/main/res/values/strings.xml`.

---

## 5. Credits
- Google Sign-In via [Android Credential Manager](https://developer.android.com/identity/sign-in/credential-manager-siwg) and the Google Identity library.
- Google "G" logo used per [Google Sign-In branding guidelines](https://developers.google.com/identity/branding-guidelines).
- Empty-state and onboarding illustrations are original vector drawables in the style of [Storyset](https://storyset.com).
