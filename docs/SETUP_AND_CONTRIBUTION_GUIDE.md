# Setup & Contribution Guide

How to build, run and test PulseFinance, and how the three of us (Hesara, Sandali, Shazaan) work on it.

---

## 1. Tools

| Tool | Version |
|---|---|
| Android Studio | 2024 or newer |
| Android SDK | Platform 34 (minSdk 26) |
| JDK for Gradle | **17 or 21.** Gradle 8.9 does not run on Java 25, which recent Android Studio versions bundle as their JBR |
| Gradle | 8.9, through `./gradlew` |

---

## 2. First-time setup

1. **Clone:**
   ```bash
   git clone https://github.com/Hesara2003/PulseFinance-SE3092.git
   cd PulseFinance-SE3092
   ```
2. **SDK path:** Android Studio creates `local.properties` on first sync (`sdk.dir=…`). It is gitignored, so each machine has its own.
3. **JDK (terminal builds only):** if `./gradlew` fails with an unsupported Java version, point `JAVA_HOME` at a JDK 17 or 21. For example, use the JDK 21 that Gradle downloads into `~/.gradle/jdks/`:
   ```bash
   export JAVA_HOME=$(ls -d ~/.gradle/jdks/*21*/*/Contents/Home | head -1)   # macOS
   java -version
   ```
4. **Firebase:** `app/google-services.json` for the team project `finance-app-se3092` is committed, so nothing else is needed to build.
   - To use your own Firebase project, follow [README §4](../README.md#4-setting-up-firebase).
   - Google sign-in only works from a keystore whose SHA-1 is registered in that project. Ask Hesara to add your debug SHA-1 (`./gradlew signingReport`).

---

## 3. Build, run, test

```bash
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug         # onto a connected device or emulator
./gradlew testDebugUnitTest    # JVM unit tests
./gradlew lintDebug
```

**From Android Studio:** open the folder, let Gradle sync, choose an emulator or device on Android 8.0+, then press **Run**.

**Signing in during development:**
- **Demo account:** debug builds show a demo drawer on the sign-in screen. **Auto Fill** signs in to the shared, pre-seeded demo account. Ask a teammate for the credentials and add them to your `local.properties` (gitignored):
  ```properties
  pulsefinance.demoEmail=…
  pulsefinance.demoPassword=…
  ```
  CI can pass `PULSEFINANCE_DEMO_EMAIL` / `PULSEFINANCE_DEMO_PASSWORD` instead. Without them the drawer is hidden; release builds never have it. Don't copy the credentials into docs, screenshots or commits (FR-AUTH-7).
- **New account:** a fresh account starts with four empty accounts and nothing else. Record your own test data, or use the demo account to see a populated app.

**Unit tests** live in `app/src/test/`. They cover:
- the Safe-to-Spend summary;
- goal velocity and the USD-linked target;
- savings rate;
- the payslip and APIT bands, including the Firestore config fallback;
- subscription detection;
- Seasonal Peaks;
- the bank SMS parser;
- CSV export.

New domain logic gets a test in the same PR.

---

## 4. Working on the code

Package layout (`com.se3092.pulsefinance`):

```
ui/          Compose screens, ViewModels, components, theme, navigation
domain/      models, use cases, repository interfaces
data/        Room (local/), Firebase and rate APIs (remote/), repository implementations
di/          Hilt modules
```

Conventions the codebase already follows:
- **Screens hold no business logic.** A calculation that answers one of Kavindu's questions belongs in a use case in `domain/usecase/` with a unit test.
- **ViewModels expose one `StateFlow<…UiState>`** built with `stateIn(viewModelScope, WhileSubscribed(5000), …)`.
- **New stored data:**
  1. Add a domain model, a DTO with `toDomain`/`fromDomain`, and a Room entity if it is cached.
  2. Bump `AppDatabase.version`.
  3. Document the collection in [DATA_MODELS_AND_STORAGE.md](DATA_MODELS_AND_STORAGE.md).
  4. Add a rule to `firestore.rules`.
- **Money:** every income entry keeps its original amount, currency and the rate used. Never recompute stored LKR values (ADR-0001).
- **Domain terms** follow [GLOSSARY.md](../GLOSSARY.md), for example "Safe-to-Spend Allowance", "Pending Invoice" and "Freeze Break".

---

## 5. Git workflow

**PulseFinance-SE3092** is the repository we all work in. `Hesara2003/PulseFinance` is a mirror of it.

1. Update `main` and branch from it:
   ```bash
   git switch main && git pull
   git switch -c feat/fr-auth-4-onboarding-capture
   ```
   Branch prefixes: `feat/`, `fix/`, `test/`, `docs/`, `chore/`. Put the requirement ID in the name when there is one.
2. **Commit as you go,** from your own machine and your own Git identity, using Conventional Commits:
   ```
   feat(onboarding): capture gross salary in step 1
   fix(seasonal): average over months with records instead of a fixed 12
   docs(srs): mark FR-AUTH-4 Done
   ```
3. **Open a PR against `main`** and request review from a teammate. Nobody merges their own unreviewed work.
4. **Merge with "Create a merge commit".** Never squash or rebase-merge, and never force-push `main`. The commit history records who did what.
5. **When a requirement is finished,** update its status in [SRS.md](SRS.md) in the same PR.

**CI:** `ci.yml` runs on PRs and pushes to `main` (unit tests, lint, debug build). A push to `main` also triggers `release-apk-to-drive.yml`, which uploads the release APK and AAB to Google Drive ([RELEASE_WORKFLOW_AND_GDRIVE.md](RELEASE_WORKFLOW_AND_GDRIVE.md)).
