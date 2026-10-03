# Setup, Build & Developer Guide

## 1. Prerequisites & Tooling

To build, run, and test PulseFinance, ensure your workstation meets the following requirements:

| Component | Minimum Version | Recommended / Tested |
| :--- | :--- | :--- |
| **Operating System** | macOS 13+ (Ventura/Sonoma/Sequoia), Linux (Ubuntu 22.04+), or Windows 11 | macOS 14+ on Apple Silicon (M-Series) |
| **Java Development Kit** | JDK 17 | **JDK 21** (Eclipse Adoptium Temurin 21.0.7) |
| **Android Studio** | Android Studio Hedgehog (2023.1.1) | **Android Studio Ladybug (2024.2+)** |
| **Android SDK Tools** | API 26 (Android 8.0 Oreo) | API 34+ (Android 14 Upside Down Cake) |
| **Build System** | Gradle 8.5+ | Gradle 8.9 (via bundled `./gradlew`) |
| **Android NDK / CMake** | Not required (100% Kotlin codebase) | — |

---

## 2. Step-by-Step Installation & Project Setup

### 2.1. Clone Repository
```bash
git clone https://github.com/Hesara2003/PulseFinance.git
cd PulseFinance
```

### 2.2. Configure JDK Environment
Ensure your terminal environment points `JAVA_HOME` to JDK 17 or JDK 21:
```bash
# macOS Example:
export JAVA_HOME="/Users/hesaraperera/.gradle/jdks/eclipse_adoptium-21-aarch64-os_x.2/jdk-21.0.7+6/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Verify:
java -version
```

### 2.3. Configure Android SDK (`local.properties`)
Create or verify `local.properties` in the project root:
```properties
sdk.dir=/Users/<your-username>/Library/Android/sdk
```

### 2.4. Firebase Configuration
1. Create a Firebase project named `pulse-finance` on the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name:
   ```
   com.se3092.pulsefinance
   ```
3. Download `google-services.json` and save it to [app/google-services.json](file:///Users/hesaraperera/Desktop/PulseFinance/app/google-services.json).
4. Enable **Email/Password** under Firebase Authentication.
5. Enable **Cloud Firestore** and deploy rules from [firestore.rules](file:///Users/hesaraperera/Desktop/PulseFinance/firestore.rules).

---

## 3. Building & Running the Application

### 3.1. Command Line Interface (CLI)

```bash
# Clean previous build artifacts
./gradlew clean

# Build Debug APK
./gradlew assembleDebug

# Install on connected emulator or device
./gradlew installDebug
```
The output APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 3.2. Android Studio IDE
1. Launch Android Studio.
2. Select **Open** and navigate to the project directory `/Users/hesaraperera/Desktop/PulseFinance`.
3. Allow Gradle to sync project dependencies.
4. Select a virtual device (Pixel 8 / API 34 recommended) or connected physical Android device.
5. Click the green **Run** button (`Shift + F10`).

---

## 4. Test Persona & Demo Verification

To evaluate the app against the assigned academic / client scenario, use the pre-configured credentials:

### Evaluation Credentials
- **Email:** `kavindu.silva@pulsemail.io`
- **Password:** `ColomboDev2026!`
- *(Or tap the **"Fill Demo Persona"** pill on the Auth screen to auto-populate)*

### Initial Preloaded Dataset
Upon launch or login, the app automatically initializes:
1. **Accounts**:
   - Commercial Bank Primary Checking: `LKR 185,500`
   - Secondary Savings Account: `LKR 42,000`
   - Physical Cash Wallet: `LKR 8,000`
   - Binance Crypto Custody: `USDT 270.00 (LKR 83,000)`
2. **MacBook Pro M4 Savings Goal**:
   - Target: `LKR 490,000`
   - Initial Saved: `LKR 11,200`
   - Target Date: October 2026
3. **Overdue Freelance Invoice**:
   - SME Web Development Milestone: `$600 USD (LKR 185,000)` — 4 Days Overdue.
4. **Zombie Subscriptions**:
   - Fitness Gym Membership: `LKR 8,500/mo` (Unused 68 days on secondary bank card).
   - Unused Cloud Dev VPS: `LKR 6,200/mo` (Abandoned dev server).

---

## 5. Coding Standards & Git Workflow

- **Architecture Boundary**: ViewModels must never import Android `View` or `Context` classes.
- **Compose Stability**: All UI models passed into Composables should be marked `@Immutable` or `@Stable`.
- **Commit Messages**: Follow standard Conventional Commits:
  - `feat: implement opportunity cost slider on GoalScreen`
  - `fix: correct USD to LKR exchange normalization formula`
  - `docs: update setup and architecture specifications`
