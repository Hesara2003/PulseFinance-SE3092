# PulseFinance

**Module:** SE3092 Platform Based Development (Year 3, Semester 2, 2026)
**Assignment 01:** Problem-first, scenario-driven engineering
**Team:** Hesara · Sandali · Shazaan
**Built for:** Kavindu Silva, 25, junior software engineer in Colombo
**Stack:** Kotlin · Jetpack Compose (Material 3) · MVVM · Hilt · Room · Firebase Auth + Cloud Firestore · Coroutines/StateFlow
**Runs on:** Android 8.0+ (minSdk 26, targetSdk 34)

Kavindu earned more than LKR 1.6 million in the last twelve months and has LKR 11,200 left. His money arrives four ways: salary, freelance milestones, Google AdSense in USD, and crypto. He has tried four tools and abandoned all of them. He wants a MacBook Pro M4 (about LKR 490,000) within a year but cannot say what he earns, what he spends, or what he would need to save.

PulseFinance is built around that one person. The full problem analysis and requirements are in [docs/SRS.md](docs/SRS.md).

---

## 1. What it does for Kavindu

| His problem (SRS ID) | Why his earlier tools failed | What PulseFinance does |
|---|---|---|
| He can't state his real monthly income (P1) | Every app assumed one salary on the 1st | Records each income stream with its source. Shows realised income separately from pending and overdue invoices, and derives a Safe-to-Spend Allowance per day for the rest of the month (FR-INC-9, FR-DASH-1). |
| Freelance invoices get forgotten (P2) | His Notes app only recorded what arrived | Saves a freelance milestone as a Pending Invoice with a due date. It turns Overdue automatically and appears as a dashboard banner with one-tap "mark received" (FR-INC-3, 4, FR-DASH-5). |
| AdSense is never recorded at what actually arrived (P3) | Spreadsheet formulas broke; rates were guessed | Pre-fills the day's rate as a suggestion; he can enter the LKR his bank actually credited instead. The rate is frozen on save, so old months still match his bank (FR-INC-5, 6, ADR-0001). |
| No running crypto position (P4) | Nothing tracked profit and loss | Records crypto trades as profit (+) or loss (−) with a year-to-date net. The Income Basis ignores crypto profits but counts crypto losses (FR-INC-7, 8, FR-DASH-3). |
| Spending is spread over card, cash, PickMe and a secondary auto-debit account (P5) | His bank app showed only his primary card | Gives him four default accounts (primary, secondary auto-debit, cash, Binance). He can share a bank SMS into the app to get a draft expense, without the READ_SMS permission (FR-ACC-1, FR-EXP-5, 7). |
| Two subscriptions he meant to cancel are still charging (P6) | They were on the secondary account, which he never checked | Detects recurring merchants over 120 days and records his Keep/Cancel decision. It flags "still charging" after a cancel and raises the risk after two "didn't use it" answers (FR-SUB-1–6). |
| No idea what's committed vs discretionary (P7) | Bank categories put PickMe under "retail" | Proposes committed or discretionary per category (rent → committed, dining → discretionary), and shows the split bar and a category donut. This is where the "LKR 24,000 on coffee" moment shows up (FR-EXP-2, FR-DASH-4, FR-ANA-1). |
| Doesn't know the saving needed for the MacBook (P8) | The goal was a wish, not a number | Calculates the required pace (about LKR 1,330/day, LKR 39,900/month) against his actual savings rate, and prices each habit in "days of MacBook". If a USD price is set, the LKR target follows the exchange rate (FR-GOAL-1–6). |
| Gaps in logging made earlier tools feel like failure (P9) | Missing days showed as zeros and broken streaks | One-tap Sri Lankan presets (Coffee LKR 850, PickMe LKR 1,450, Dialog/Mobitel reloads). Works offline. Savings rate is averaged only over days that have data (NFR-1, 2, 3). |
| He never reads his payslip (P10) | No tool handled EPF or APIT | Takes gross salary and shows employee EPF 8%, APIT from bands stored in Firestore, take-home pay and his growing EPF balance (FR-LK-1, 2, 3). |
| Impulsive spending, no friction (P11) | Nothing stood between him and a purchase | Spending Freeze on an account, with an opportunity-cost reminder priced in goal days. 30-day warnings before Avurudu, Vesak, Poson, December and his own peak months (FR-ACC-3–5, FR-LK-4, 5). |

[docs/SRS.md](docs/SRS.md) §10 lists what is done, partly done and still planned.

---

## 2. Documentation

| Document | Contents |
|---|---|
| [docs/SRS.md](docs/SRS.md) | Scenario analysis, problems P1–P11, every requirement with status, traceability |
| [docs/DATA_MODELS_AND_STORAGE.md](docs/DATA_MODELS_AND_STORAGE.md) | Firestore schema, ER diagram, Room cache, rate handling |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Layer and MVVM diagrams, offline sync, DI, platform integrations |
| [docs/adr/](docs/adr/) | Architecture decisions (ADR-0001: fetched rate suggests, received amount decides) |
| [GLOSSARY.md](GLOSSARY.md) | Domain terms used in code and docs (Safe-to-Spend Allowance, Pending Invoice, …) |
| [docs/RELEASE_WORKFLOW_AND_GDRIVE.md](docs/RELEASE_WORKFLOW_AND_GDRIVE.md) | CI and APK release to Google Drive |
| [prototype/](prototype/) | Early HTML mock-ups of the main screens (03/10) |

---

## 3. Architecture in one picture

```
Compose screens ──collect StateFlow──▶ ViewModels ──▶ use cases (Safe-to-Spend, goal pace, payslip, …)
                                            │
                                            ▼
                                 repository interfaces  (domain)
                                            │ @Binds (Hilt)
                                            ▼
                         repository impls ── cacheBackedFlow ──┬──▶ Room cache   (UI always reads here)
                                                               └──▶ Firestore    (source of truth, offline queue)
```

Screens read only from Room, so they open instantly and work offline. Firestore listeners mirror changes into Room while a screen is open. Writes go to both, and Firestore's disk queue uploads them when he is back online. Full diagrams are in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## 4. Setting up Firebase

The app is configured for the Firebase project `finance-app-se3092` (see `.firebaserc`). `app/google-services.json` is in the repo. To use your own project instead:

1. **Project and app:** create a Firebase project, then add an Android app with package `com.se3092.pulsefinance`. Replace `app/google-services.json` with the downloaded file.
2. **Authentication:** enable **Email/Password** and **Google**. For Google sign-in, add your debug keystore's SHA-1 to the Android app in project settings.
3. **Firestore:** create a database in production mode.
4. **Rules:** deploy with `firebase deploy --only firestore:rules`. Each user can read and write only `users/{their uid}`; `config/` is read-only.
5. **Tax bands:** create the document `config/apitBands` with the JSON in [docs/DATA_MODELS_AND_STORAGE.md §2.6](docs/DATA_MODELS_AND_STORAGE.md). Without it, the app uses its bundled bands.

---

## 5. Build and run

**Needs:** Android Studio (2024 or newer), Android SDK 34, and a **JDK 17 or 21** for Gradle 8.9.

1. Clone the repo and open the folder in Android Studio. Let Gradle sync.
2. **Java version:** if building from the terminal and Gradle reports a Java version error, point `JAVA_HOME` at a JDK 17 or 21. Recent Android Studio versions bundle Java 25, which Gradle 8.9 does not support.
3. **Run:** use an emulator or device on Android 8.0+, then click **Run 'app'**.

**Signing in:**
- **New accounts:** register with email or Google. A new account starts with four empty accounts: primary bank, secondary auto-debit, cash and crypto exchange. Nothing else is pre-filled; he records his own data.
- **Demo account (debug builds only):** the sign-in screen has a demo drawer that fills in a pre-seeded demo account. The credentials are injected through `BuildConfig` in debug builds and are empty in release builds (FR-AUTH-7), so they are not repeated here.

**Tests:**

```bash
./gradlew testDebugUnitTest   # domain and repository unit tests
./gradlew assembleDebug       # debug APK
```

**CI:**
- **Pushes and PRs to `main`:** `.github/workflows/ci.yml` runs the unit tests, lint (non-blocking) and a debug build.
- **Every push to `main`:** `.github/workflows/release-apk-to-drive.yml` builds the release APK and AAB and uploads them to the team's Google Drive folder (see [docs/RELEASE_WORKFLOW_AND_GDRIVE.md](docs/RELEASE_WORKFLOW_AND_GDRIVE.md)).

---

## 6. Credits

- Illustrations (onboarding, goal setup and pacing, empty states): [Storyset](https://storyset.com) by Freepik, used under the Storyset free licence with attribution.
- Fonts: [Bricolage Grotesque](https://fonts.google.com/specimen/Bricolage+Grotesque) and [Space Grotesk](https://fonts.google.com/specimen/Space+Grotesk), bundled in `app/src/main/res/font`, under the SIL Open Font License 1.1.
- App logo (piggy bank with coin): icon by **[AUTHOR NAME]** from [Flaticon](https://www.flaticon.com), used under the Flaticon free licence with attribution.
- In-app icons: [Material Symbols](https://fonts.google.com/icons) (Apache 2.0).
