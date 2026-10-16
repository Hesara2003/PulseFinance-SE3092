# System Architecture & Technical Design

## 1. Architectural Philosophy

PulseFinance is engineered on modern Android architectural guidelines recommended by Google and enterprise engineering standards:
1. **Clean Architecture with Strict Layer Separation**: Isolates Presentation, Domain (Business Logic), and Data layers.
2. **Unidirectional Data Flow (UDF)**: UI states flow downwards from ViewModels as immutable `StateFlow<T>`, while user intents and actions flow upwards as events.
3. **Offline-First Reactive Paradigm**: The local SQLite database (via Room) acts as the single source of truth for all query operations. Network writes (via Firebase Firestore) are synchronized asynchronously, ensuring zero UI latency and offline operation.
4. **Dependency Injection**: Powered by Google Dagger-Hilt to guarantee modularity, loose coupling, and painless mock-based testing.

---

## 2. High-Level Architecture Diagram

```mermaid
graph TD
    subgraph UI_Layer [Presentation Layer - Jetpack Compose]
        MainActivity[MainActivity & Single-Activity Host]
        NavGraph[AppNavGraph Navigation Compose]
        Screens[AuthScreen / DashboardScreen / GoalScreen / IncomeScreen / ExpenseScreen / AnalyticsScreen]
        Components[MacBookGoalCard / SafeToSpendGauge / CommittedVsDiscretionaryBar / BottomSheets]
    end

    subgraph ViewModel_Layer [State Management - Jetpack AAC ViewModels]
        AuthVM[AuthViewModel]
        DashVM[DashboardViewModel]
        GoalVM[GoalViewModel]
        IncVM[IncomeViewModel]
        ExpVM[ExpenseViewModel]
        AnaVM[AnalyticsViewModel]
    end

    subgraph Domain_Layer [Domain Layer - Pure Kotlin]
        UseCases[CalculateFinancialSummaryUseCase<br/>CalculateGoalVelocityUseCase<br/>ConvertCurrencyUseCase]
        RepoInterfaces[AuthRepository<br/>AccountRepository<br/>ExpenseRepository<br/>IncomeRepository<br/>GoalRepository]
        DomainModels[FinancialSummary, SavingsGoal, IncomeStream, Expense, Account]
    end

    subgraph Data_Layer [Data Layer - Repositories & Data Sources]
        RepoImpls[AuthRepositoryImpl<br/>AccountRepositoryImpl<br/>ExpenseRepositoryImpl<br/>IncomeRepositoryImpl<br/>GoalRepositoryImpl]
        
        subgraph Local_Cache [Local Storage - Room SQLite]
            AppDB[AppDatabase]
            DAOs[AccountDao / ExpenseDao / IncomeDao / GoalDao]
            Entities[AccountEntity / ExpenseEntity / IncomeEntity / GoalEntity]
        end
        
        subgraph Remote_Cloud [Cloud Backend - Google Firebase]
            FirebaseAuth[FirebaseAuthDataSource]
            Firestore[FirestoreDataSource - Realtime Snapshots]
            DTOs[AccountDto / ExpenseDto / IncomeDto / GoalDto]
        end
    end

    Screens --> NavGraph
    NavGraph --> MainActivity
    Screens --> ViewModel_Layer
    Components --> Screens
    
    ViewModel_Layer -->|StateFlow / Events| Domain_Layer
    Domain_Layer --> UseCases
    UseCases --> RepoInterfaces
    RepoInterfaces -.->|Implemented by| RepoImpls
    
    RepoImpls --> DAOs
    RepoImpls --> Remote_Cloud
    DAOs --> AppDB
    Firestore --> Remote_Cloud
    FirebaseAuth --> Remote_Cloud
```

---

## 2.1. MVVM Mapping Diagram — Screens → ViewModels → Repositories → Data Sources

This diagram is drawn from the code as it stands. It maps every screen to its ViewModel, every ViewModel to the use cases and repositories it injects, and every repository to its data sources. **Solid boxes and arrows are implemented.** **Dashed boxes and arrows are Planned** (see `docs/SRS.md` for the requirement IDs). Every ViewModel also injects `AuthRepository` to get the signed-in user's ID. To keep the diagram readable, that link is drawn only for `AuthViewModel`.

```mermaid
flowchart LR
    %% ================= UI LAYER =================
    subgraph UI["UI Layer — Jetpack Compose (single Activity)"]
        direction TB
        MA["MainActivity<br/>+ AppNavGraph<br/><i>Navigation Compose, hiltViewModel()</i>"]
        S_AUTH["AuthScreen<br/>+ Onboarding 1–3"]
        S_DASH["DashboardScreen<br/><i>SafeToSpendGauge,<br/>CommittedVsDiscretionaryBar</i>"]
        S_INC["IncomeScreen<br/>+ AddIncomeBottomSheet"]
        S_EXP["ExpenseScreen<br/>+ AddExpenseBottomSheet"]
        S_GOAL["GoalScreen<br/><i>MacBookGoalCard</i>"]
        S_ANA["AnalyticsScreen"]
        MA --> S_AUTH & S_DASH & S_INC & S_EXP & S_GOAL & S_ANA
    end

    %% ================= VIEWMODEL LAYER =================
    subgraph VM["ViewModel Layer — StateFlow#lt;UiState#gt;, viewModelScope"]
        direction TB
        VM_AUTH["AuthViewModel"]
        VM_DASH["DashboardViewModel"]
        VM_INC["IncomeViewModel"]
        VM_EXP["ExpenseViewModel"]
        VM_GOAL["GoalViewModel"]
        VM_ANA["AnalyticsViewModel"]
    end

    S_AUTH -->|"collects uiState /<br/>sends events"| VM_AUTH
    S_DASH --> VM_DASH
    S_INC --> VM_INC
    S_EXP --> VM_EXP
    S_GOAL --> VM_GOAL
    S_ANA --> VM_ANA

    %% ================= DOMAIN LAYER =================
    subgraph DOM["Domain Layer — pure Kotlin"]
        direction TB
        subgraph UC["Use Cases"]
            UC_SUM["CalculateFinancialSummary"]
            UC_VEL["CalculateGoalVelocity"]
            UC_RATE["CalculateSavingsRate"]
            UC_FX["ConvertCurrency"]
            UC_SUB["DetectSubscriptions"]
            UC_PAY["CalculatePayslip<br/>(EPF / APIT)"]:::planned
            UC_SMS["ParseBankSms"]:::planned
            UC_SEA["DetectSeasonalPeaks"]:::planned
            UC_CSV["ExportCsv"]:::planned
        end
        subgraph RI["Repository Interfaces"]
            R_AUTH["AuthRepository"]
            R_ACC["AccountRepository"]
            R_INC["IncomeRepository"]
            R_EXP["ExpenseRepository"]
            R_GOAL["GoalRepository"]
            R_SUBR["SubscriptionReviewRepository"]
            R_FXR["ExchangeRateRepository"]:::planned
            R_TAX["TaxConfigRepository"]:::planned
            R_USE["AppUsageRepository"]:::planned
        end
    end

    %% ---- ViewModel → Use case / Repository (implemented) ----
    VM_AUTH --> R_AUTH & R_ACC
    VM_DASH --> UC_SUM
    VM_DASH --> R_ACC & R_INC & R_EXP & R_GOAL
    VM_INC --> UC_FX
    VM_INC --> R_INC
    VM_EXP --> UC_SUB
    VM_EXP --> R_EXP & R_GOAL & R_SUBR
    VM_GOAL --> UC_VEL & UC_RATE
    VM_GOAL --> R_GOAL & R_INC & R_EXP
    VM_ANA --> R_INC & R_EXP

    %% ---- Planned wiring ----
    VM_INC -.-> UC_PAY
    UC_PAY -.-> R_TAX
    VM_INC -.-> R_FXR
    VM_GOAL -.-> R_FXR
    VM_EXP -.-> UC_SMS
    VM_EXP -.->|"Spending Freeze check"| R_ACC
    VM_EXP -.-> R_USE
    VM_DASH -.-> UC_SEA
    VM_ANA -.-> UC_CSV

    %% ================= DATA LAYER =================
    subgraph DATA["Data Layer — Repository implementations (Hilt-bound)"]
        direction TB
        I_AUTH["AuthRepositoryImpl"]
        I_ACC["AccountRepositoryImpl"]
        I_INC["IncomeRepositoryImpl"]
        I_EXP["ExpenseRepositoryImpl"]
        I_GOAL["GoalRepositoryImpl"]
        I_SUBR["SubscriptionReviewRepositoryImpl"]
        CBF["cacheBackedFlow<br/><i>UI reads Room; Firestore<br/>listener mirrors into Room</i>"]
        I_FXR["ExchangeRateRepositoryImpl"]:::planned
        I_TAX["TaxConfigRepositoryImpl"]:::planned
        I_USE["AppUsageRepositoryImpl"]:::planned
    end

    R_AUTH -->|"@Binds"| I_AUTH
    R_ACC -->|"@Binds"| I_ACC
    R_INC -->|"@Binds"| I_INC
    R_EXP -->|"@Binds"| I_EXP
    R_GOAL -->|"@Binds"| I_GOAL
    R_SUBR -->|"@Binds"| I_SUBR
    R_FXR -.-> I_FXR
    R_TAX -.-> I_TAX
    R_USE -.-> I_USE

    I_ACC & I_INC & I_EXP & I_GOAL --> CBF

    %% ================= DATA SOURCES =================
    subgraph LOCAL["Local Data Sources"]
        direction TB
        ROOM[("Room — AppDatabase<br/>AccountDao · IncomeDao<br/>ExpenseDao · GoalDao")]
        RATE_CACHE[("Room — rate cache<br/>(daily)")]:::planned
        USM["Android UsageStatsManager"]:::planned
    end

    subgraph REMOTE["Remote Data Sources — Firebase & APIs"]
        direction TB
        FAD["FirebaseAuthDataSource"]
        FSD["FirestoreDataSource<br/><i>addSnapshotListener</i>"]
        FA[("Firebase Authentication<br/>Email/Password + Google")]
        FS[("Cloud Firestore<br/>users/{uid}/… <br/>persistent offline cache")]
        FX_API["open.er-api.com<br/>CoinGecko"]:::planned
    end

    CBF --> ROOM
    CBF --> FSD
    I_AUTH --> FAD
    I_SUBR --> FSD
    FAD --> FA
    FSD --> FS
    I_FXR -.-> RATE_CACHE
    I_FXR -.-> FX_API
    I_TAX -.->|"config/apit"| FSD
    I_USE -.-> USM

    classDef planned fill:#fff,stroke:#999,stroke-dasharray:5 4,color:#666
    style UI fill:#e8f5e9,stroke:#2e7d32
    style VM fill:#e3f2fd,stroke:#1565c0
    style DOM fill:#fff8e1,stroke:#f9a825
    style DATA fill:#f3e5f5,stroke:#6a1b9a
    style LOCAL fill:#fafafa,stroke:#555
    style REMOTE fill:#fafafa,stroke:#555
```

**How to read it, using the dashboard as an example:**
1. `DashboardScreen` collects `DashboardViewModel.uiState`, a `StateFlow`. The screen holds no logic of its own.
2. `DashboardViewModel` combines flows from `IncomeRepository`, `ExpenseRepository`, `GoalRepository` and `AccountRepository`. It then passes them through `CalculateFinancialSummaryUseCase` to produce the Safe-to-Spend Allowance and the committed/discretionary split.
3. Each repository implementation returns a `cacheBackedFlow`. The UI always reads from **Room**, so it is instant and works offline. While the screen is visible, a **Firestore snapshot listener** mirrors changes into Room, which is what makes updates real-time (NFR-3, NFR-4).
4. A write goes to Room first and is then queued in Firestore's persistent cache. It uploads when the device is back online.
5. `SubscriptionReviewRepositoryImpl` is the one exception: it reads straight from Firestore and relies on Firestore's own offline cache instead of a Room table.

| Screen | ViewModel | Use cases | Repositories |
|---|---|---|---|
| AuthScreen + Onboarding | AuthViewModel | — | Auth, Account |
| DashboardScreen | DashboardViewModel | CalculateFinancialSummary · *DetectSeasonalPeaks (planned)* | Auth, Account, Income, Expense, Goal |
| IncomeScreen | IncomeViewModel | ConvertCurrency · *CalculatePayslip (planned)* | Auth, Income · *ExchangeRate, TaxConfig (planned)* |
| ExpenseScreen | ExpenseViewModel | DetectSubscriptions · *ParseBankSms (planned)* | Auth, Expense, Goal, SubscriptionReview · *Account, AppUsage (planned)* |
| GoalScreen | GoalViewModel | CalculateGoalVelocity, CalculateSavingsRate | Auth, Goal, Income, Expense · *ExchangeRate (planned)* |
| AnalyticsScreen | AnalyticsViewModel | *ExportCsv (planned)* | Auth, Income, Expense |

---

## 3. Layer Breakdown

### 3.1. Presentation Layer (`com.se3092.pulsefinance.ui`)
- **Single-Activity Architecture**: The application is hosted by [MainActivity.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/MainActivity.kt), which delegates UI rendering to [AppNavGraph.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/ui/navigation/AppNavGraph.kt).
- **Jetpack Compose (Material 3)**: Pure declarative UI without XML views. Dynamic animations, blur effects, canvas arc rendering (`SafeToSpendGauge`), and interactive drag/sliders are built with Compose runtime.
- **Navigation Compose**: Type-safe route definitions in [Screen.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/ui/navigation/Screen.kt) supporting bottom navigation dock switching between Cockpit (`dashboard`), Income (`income`), Goals (`goal`), Analytics (`analytics`), and Expenses (`expense`).

### 3.2. ViewModel Layer
- Each feature module owns a lifecycle-aware ViewModel extending `androidx.lifecycle.ViewModel`.
- ViewModels expose an immutable `uiState: StateFlow<T>` collected in Compose using `collectAsStateWithLifecycle()`.
- Coroutine execution is bound to `viewModelScope`, ensuring structured concurrency and auto-cancellation when Composables leave composition.

### 3.3. Domain Layer (`com.se3092.pulsefinance.domain`)
- **Zero Android Framework Dependencies**: Written strictly in standard Kotlin for portability and isolated unit testing.
- **Repository Contracts**: Abstract interfaces defining business operations:
  - [IncomeRepository.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/domain/repository/IncomeRepository.kt)
  - [GoalRepository.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/domain/repository/GoalRepository.kt)
  - [ExpenseRepository.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/domain/repository/ExpenseRepository.kt)
  - [AccountRepository.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/domain/repository/AccountRepository.kt)
  - [AuthRepository.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/domain/repository/AuthRepository.kt)
- **Use Cases (Interactors)**:
  - `CalculateFinancialSummaryUseCase`: Computes real-time totals, committed ratios, safe-to-spend allowances, and net savings.
  - `CalculateGoalVelocityUseCase`: Evaluates daily pacing targets, projected completion dates, and dynamic opportunity cost equations.
  - `ConvertCurrencyUseCase`: Handles normalized exchange conversions between USD/USDT and LKR.

### 3.4. Data Layer (`com.se3092.pulsefinance.data`)
- **Dual-Layer Repository Pattern**:
  - Repositories inject both Room DAOs (`local`) and Firebase Data Sources (`remote`).
  - Read queries return `Flow<List<DomainModel>>` directly from Room SQLite with automatic updates when records change.
  - Write queries insert/update locally first, then fire remote synchronization tasks to Cloud Firestore.

---

## 4. Offline-First Sync Lifecycle

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant UI as Jetpack Compose UI
    participant VM as Feature ViewModel
    participant Repo as RepositoryImpl
    participant Local as Room DAO (SQLite)
    participant Remote as Firebase Firestore

    User->>UI: Logs expense / Edits goal allocation
    UI->>VM: Trigger Intent (e.g. addExpense())
    VM->>Repo: repository.addExpense(expense)
    Repo->>Local: dao.insertExpense(expenseEntity)
    Local-->>UI: Reactive Flow emits updated list immediately (0ms UI lag)
    
    rect rgb(20, 30, 45)
    Note over Repo,Remote: Asynchronous Cloud Synchronization
    Repo->>Remote: firestoreDataSource.syncExpense(dto)
    alt Sync Succeeded
        Remote-->>Repo: Document Written (ACK)
        Repo->>Local: Mark entity as synced = true
    else Network Offline
        Remote--xRepo: Network Unavailable
        Note over Repo,Local: Remains pending in Room; auto-retried on reconnect
    end
    end
```

---

## 5. Dependency Injection Graph (Hilt)

The application uses Google Dagger-Hilt for automated compile-time dependency injection across 3 primary modules:

1. **`DatabaseModule`**:
   - Provides singleton `AppDatabase` via `Room.databaseBuilder()`.
   - Binds `AccountDao`, `ExpenseDao`, `IncomeDao`, and `GoalDao`.
2. **`FirebaseModule`**:
   - Provides singleton instances of `FirebaseAuth` and `FirebaseFirestore`.
   - Injects `FirebaseAuthDataSource` and `FirestoreDataSource`.
3. **`RepositoryModule`**:
   - Binds domain repository interfaces to concrete implementations:
     - `AuthRepository` $\to$ `AuthRepositoryImpl`
     - `AccountRepository` $\to$ `AccountRepositoryImpl`
     - `ExpenseRepository` $\to$ `ExpenseRepositoryImpl`
     - `IncomeRepository` $\to$ `IncomeRepositoryImpl`
     - `GoalRepository` $\to$ `GoalRepositoryImpl`

---

## 6. Concurrency & Threading

- **UI Thread (Main)**: Reserved purely for Compose rendering and state observation.
- **Background Thread (`Dispatchers.IO`)**: Utilized by Room database queries, Firebase network requests, and cryptographic operations.
- **Computation Thread (`Dispatchers.Default`)**: Executed for mathematical aggregations, opportunity cost computations, and currency conversions.
