# System Architecture

PulseFinance is a single-activity Android app (Kotlin, Jetpack Compose, MVVM, Hilt, Room, Firebase). Each architectural choice below answers something in **Kavindu Silva's** scenario. Problem IDs (P1–P11) and requirement IDs refer to [SRS.md](SRS.md); the data itself is described in [DATA_MODELS_AND_STORAGE.md](DATA_MODELS_AND_STORAGE.md).

Everything here reflects the code on `main`. Dashed items in the diagrams are not built yet.

---

## 1. Why it is shaped this way

| Scenario fact | Architectural answer |
|---|---|
| He abandoned a Google Sheet after 13 days because entry was slow (P9). Logging has to work in a PickMe with no signal. | **Offline-first reads.** Every screen reads from a Room cache. Writes go to Room and Firestore's persistent queue at the same time, so a LKR 850 coffee appears instantly and syncs later (NFR-1, NFR-3). |
| He uses more than one device and expects the same numbers on each. | **Firestore is the source of truth.** Snapshot listeners mirror it into Room while a screen is open, so changes show up without a refresh (NFR-4). |
| His income arrives in LKR, USD (AdSense) and USDT/ETH (crypto), and past months must match his bank (P1, P3, P4). | **Rates are frozen per entry** (ADR-0001). The rate service only *suggests* a rate; the stored LKR amount never changes afterwards. |
| He can't tell committed from discretionary spending, or what he must save for the MacBook (P7, P8). | **Calculations live in domain use cases**, not in screens. The Safe-to-Spend Allowance, goal pace and savings rate are plain Kotlin functions with unit tests. |
| He shares bank SMS, needs his account number for invoices, and forgets subscriptions (P2, P5, P6). | **Platform features sit at the edges:** the share sheet (no READ_SMS permission), BiometricPrompt and Android usage stats, each behind a small helper. |

---

## 2. High-level layers

```mermaid
flowchart TB
    subgraph UI["UI — Jetpack Compose, one Activity"]
        MA["MainActivity (FragmentActivity)<br/>receives shared SMS (ACTION_SEND)"]
        NAV["AppNavGraph — auth · dashboard · income · expenses · goal · analytics"]
        MA --> NAV
    end
    subgraph VM["ViewModels — StateFlow&lt;UiState&gt;"]
        VMS["Auth · Dashboard · Income · Expense · Goal · Analytics"]
    end
    subgraph DOM["Domain — models, use cases, repository interfaces"]
        UC["9 use cases"]
        RI["7 repository interfaces"]
    end
    subgraph DATA["Data — repository implementations"]
        IMPL["*RepositoryImpl + cacheBackedFlow"]
        ROOM[("Room cache<br/>AppDatabase v3")]
        FS[("Cloud Firestore<br/>persistent cache")]
        FA[("Firebase Auth<br/>email + Google")]
        RATES["ExchangeRateService<br/>open.er-api.com · CoinGecko"]
    end
    NAV --> VMS
    VMS --> UC
    VMS --> RI
    UC --> RI
    RI -.->|"@Binds"| IMPL
    IMPL --> ROOM
    IMPL --> FS
    IMPL --> FA
    IMPL --> RATES
```

**Dependency direction:** UI → ViewModel → domain ← data. Screens hold no business logic; they collect `uiState` and call ViewModel functions.

**One honest exception:** the domain layer is not entirely free of Android code.
- `ExportDataToCsvUseCase` builds an Android share `Intent`.
- `ConvertCurrencyUseCase` wraps the data-layer `ExchangeRateService`.

Everything else in `domain/` is plain Kotlin and is unit-tested on the JVM.

---

## 3. Screens → ViewModels → use cases → repositories

```mermaid
flowchart LR
    subgraph SCR["Screens"]
        S_AUTH["AuthScreen<br/>+ Onboarding 1–3"]
        S_DASH["DashboardScreen<br/>SafeToSpendGauge · CommittedVsDiscretionaryBar"]
        S_INC["IncomeScreen<br/>AddIncome + Payslip sheets"]
        S_EXP["ExpenseScreen<br/>AddExpense sheet · subscription audit"]
        S_GOAL["GoalScreen<br/>MacBookGoalCard · QuestMap"]
        S_ANA["AnalyticsScreen<br/>CategoryDonutChart"]
    end

    subgraph VMS["ViewModels"]
        V_AUTH["AuthViewModel"]
        V_DASH["DashboardViewModel"]
        V_INC["IncomeViewModel"]
        V_EXP["ExpenseViewModel"]
        V_GOAL["GoalViewModel"]
        V_ANA["AnalyticsViewModel"]
    end

    subgraph UC["Use cases"]
        U_SUM["CalculateFinancialSummary"]
        U_SMS["BankSmsParser"]
        U_SEA["SeasonalPeakChecker"]
        U_CSV["ExportDataToCsv"]
        U_FX["ConvertCurrency"]
        U_PAY["CalculatePayslip"]
        U_SUB["DetectSubscriptions"]
        U_VEL["CalculateGoalVelocity"]
        U_RATE["CalculateSavingsRate"]
    end

    subgraph REPO["Repositories"]
        R_AUTH["Auth"]
        R_ACC["Account"]
        R_INC["Income"]
        R_EXP["Expense"]
        R_GOAL["Goal"]
        R_SUBR["SubscriptionReview"]
        R_TAX["TaxConfig"]
        R_PROF["FinancialProfile<br/>(FR-AUTH-4)"]:::planned
    end

    subgraph EDGE["Platform helpers"]
        H_BIO["BiometricHelper<br/>reveal + 60 s clipboard clear"]
        H_USE["UsageStatsHelper<br/>app last opened"]
    end

    S_AUTH --> V_AUTH
    S_DASH --> V_DASH
    S_INC --> V_INC
    S_EXP --> V_EXP
    S_GOAL --> V_GOAL
    S_ANA --> V_ANA

    V_AUTH --> R_AUTH & R_ACC
    V_AUTH -.-> R_PROF
    V_DASH --> U_SUM & U_SMS & U_SEA & U_CSV
    V_DASH --> R_ACC & R_INC & R_EXP & R_GOAL
    S_DASH --> H_BIO
    V_INC --> U_FX & U_PAY
    V_INC --> R_INC & R_TAX
    V_EXP --> U_SUB
    V_EXP --> R_EXP & R_GOAL & R_ACC & R_SUBR
    V_EXP --> H_USE
    V_GOAL --> U_VEL & U_RATE & U_FX
    V_GOAL --> R_GOAL & R_INC & R_EXP
    V_ANA --> R_INC & R_EXP

    classDef planned fill:#fff,stroke:#999,stroke-dasharray:5 4,color:#666
```

Every ViewModel also uses `AuthRepository` to get the signed-in user's ID; only `AuthViewModel`'s link is drawn.

| Screen | What Kavindu does there | ViewModel uses | Solves |
|---|---|---|---|
| Auth + Onboarding | Signs in with email or Google; in debug builds a demo drawer fills the demo account | `AuthRepository`, `AccountRepository` (creates his four default accounts) | FR-AUTH-1, 2, 7 |
| Dashboard | Sees today's Safe-to-Spend, the overdue "Milestone 2" banner, the committed/discretionary bar, the Seasonal Peak warning; shares a bank SMS; exports CSV | `CalculateFinancialSummary`, `BankSmsParser`, `SeasonalPeakChecker`, `ExportDataToCsv`; Account, Income, Expense, Goal | P1, P2, P5, P7, P11 |
| Income | Logs salary from a payslip (EPF, APIT), AdSense in USD at the credited LKR, freelance invoices, crypto profit or loss | `ConvertCurrency`, `CalculatePayslip`; Income, TaxConfig | P1, P2, P3, P4, P10 |
| Expenses | Logs a PickMe in ≤ 3 taps, reviews detected subscriptions, gets a reminder when spending from a frozen account | `DetectSubscriptions`; Expense, Goal, Account, SubscriptionReview; `UsageStatsHelper` | P5, P6, P11 |
| Goal | Sees the required LKR 1,330/day pace for the MacBook, his actual savings rate, "days of MacBook" for each habit | `CalculateGoalVelocity`, `CalculateSavingsRate`, `ConvertCurrency`; Goal, Income, Expense | P8 |
| Analytics | Week/month/day/year donut and daily bars ("LKR 24,000 on coffee"), searchable ledger | Income, Expense | P7 |

---

## 4. Data flow: reads, writes and sync

### 4.1 Reads: `cacheBackedFlow`

`Account`, `Expense`, `Income` and `Goal` repositories return a `cacheBackedFlow`:

1. The UI collects a Room `Flow`, so it gets cached data immediately, online or not.
2. While someone is collecting, a Firestore snapshot listener runs alongside. Each snapshot is written to Room with `replaceAll` in a transaction, and Room's `Flow` re-emits.
3. When the screen goes away, the listener is removed with it.
4. If the listener fails (no network, permission), the cache keeps being served.

`GoalRepository` also combines the goal with `ExchangeRateService.usdLkrRate`, so a USD-linked MacBook target re-prices when the day's rate arrives (FR-GOAL-6).

`SubscriptionReviewRepository` and `TaxConfigRepository` skip Room and rely on Firestore's own offline cache: one small map and one config document.

### 4.2 Writes: Kavindu logs a coffee with no signal

```mermaid
sequenceDiagram
    autonumber
    actor K as Kavindu
    participant UI as Dashboard (Compose)
    participant VM as DashboardViewModel
    participant Repo as ExpenseRepositoryImpl
    participant Room as Room cache
    participant FS as Firestore (persistent cache)

    K->>UI: Taps the "Coffee · LKR 850" preset, then Save
    UI->>VM: onPresetSelected(...) · submitExpense()
    VM->>Repo: saveExpense(expense)
    Repo->>Room: insertExpense(entity)
    Room-->>UI: Flow re-emits — coffee is on screen, Safe-to-Spend drops
    Repo->>FS: set(users/{uid}/expenses/{id}) — fire-and-forget
    Note over FS: Offline: kept in Firestore's disk queue
    FS-->>FS: Back online: upload to server
    FS-->>Room: Snapshot listener → replaceAll (same data, now confirmed)
```

Writes don't wait for the server. Awaiting the Firestore task would hang until the device reconnects, so the call just logs a failure if one comes back. There is no "synced" flag; Firestore's queue is what guarantees the upload.

### 4.3 Money in: his AdSense payout

1. The add-income sheet pre-fills the day's USD→LKR rate from `ExchangeRateService` (cached in SharedPreferences, refreshed at most once a day).
2. He enters USD 45 and the LKR 13,612.50 his bank actually credited.
3. The app stores rate 302.5 and `amountInLkr` 13,612.50. Neither is ever recomputed (ADR-0001), so October still matches his bank statement in March.

---

## 5. Platform integrations

| Feature | Where | How |
|---|---|---|
| Share a bank SMS into the app (FR-EXP-7) | `MainActivity` → `AppNavGraph` → `DashboardViewModel.handleSharedBankSms` → `BankSmsParser` | Android share sheet (`ACTION_SEND`, `text/plain`). No `READ_SMS` permission. |
| Reveal and copy his account number for a freelance invoice (FR-ACC-6, 7) | `BiometricHelper`, `DashboardScreen` | `BiometricPrompt` (biometric or device PIN); the clipboard is cleared after 60 seconds |
| "Last opened" for Netflix and Spotify (FR-SUB-5) | `UsageStatsHelper`, `ExpenseViewModel` | `UsageStatsManager`; only works if usage access is granted (in-context prompt not built yet, NFR-12) |
| Daily rates (FR-INC-5) | `ExchangeRateService` | `HttpURLConnection` on `Dispatchers.IO`, 4 s timeouts, at most once a day |
| CSV export (FR-DATA-1) | `ExportDataToCsvUseCase` | Builds a CSV and an `ACTION_SEND` share intent |
| APIT bands (FR-LK-2) | `TaxConfigRepositoryImpl` | Firestore `config/apitBands`, validated; falls back to bundled bands |

---

## 6. Dependency injection (Hilt)

| Module | Provides |
|---|---|
| `DatabaseModule` | `AppDatabase` (Room, `fallbackToDestructiveMigration`) and the four DAOs |
| `FirebaseModule` | `FirebaseAuth`; `FirebaseFirestore` with `PersistentCacheSettings` (the offline write queue) |
| `RepositoryModule` | `@Binds` for `Auth`, `Expense`, `Income`, `Goal`, `Account`, `SubscriptionReview` and `TaxConfig` repositories |

Data sources (`FirebaseAuthDataSource`, `FirestoreDataSource`, `ExchangeRateService`) and use cases use constructor `@Inject`. ViewModels are `@HiltViewModel` and obtained with `hiltViewModel()` in `AppNavGraph`.

---

## 7. Threading

- **ViewModels:** combine repository flows and expose `StateFlow` via `stateIn(viewModelScope, WhileSubscribed(5000))`. When he leaves a screen for more than 5 seconds, its Firestore listeners stop.
- **Room:** DAO `suspend` functions and `Flow` queries run on Room's own background executor.
- **Firestore:** listener callbacks arrive on the main thread and are adapted to `Flow` with `callbackFlow`.
- **Network:** rate fetches run on `Dispatchers.IO`.
- **Use cases:** pure functions over lists, small enough to run on the collecting coroutine.
