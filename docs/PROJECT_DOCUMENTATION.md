# PulseFinance: Project Overview

**Module:** SE3092 Platform Based Development (2026), Assignment 01
**Team:** Hesara · Sandali · Shazaan
**Repository:** `Hesara2003/PulseFinance-SE3092` (private)
**Platform:** Android 8.0+ (minSdk 26, targetSdk 34), Kotlin, Jetpack Compose

This page is the starting point. It summarises the project and points to the document that owns each topic. Details live in those documents only, so they can't drift apart.

---

## 1. The person and the problem

Kavindu Silva is 25, a junior software engineer in Colombo. In twelve months he earned more than LKR 1.6 million and has LKR 11,200 left. He wants a MacBook Pro M4, about LKR 490,000, within a year.

His money arrives four ways:
- a salary that lands on the 25th;
- freelance milestones he sometimes forgets to chase;
- Google AdSense paid in USD at whatever rate his bank uses;
- crypto trades that won in Q1 and lost in Q3.

It leaves through his card, cash, PickMe and UberEats, and a secondary account that auto-debits a gym and two subscriptions he meant to cancel six months ago.

He has tried four tools and abandoned all of them:
1. **A Google Sheet:** formulas broke, and a three-week gap made it feel like failure.
2. **A budget app:** it handled USD only and had no sync.
3. **A Notes file:** it became unreadable figures.
4. **His bank's summary:** it showed his card only and called PickMe "online retail".

The SRS argues that the problem is not discipline. **None of these tools could represent his finances accurately enough to be worth keeping up.** PulseFinance is designed against each of those four failures (F1–F4) and the eleven problems they caused (P1–P11).

→ Full analysis, requirements and traceability: **[SRS.md](SRS.md)**

---

## 2. What was built

| Area | What Kavindu gets | Details |
|---|---|---|
| Income | Each stream recorded with source, currency and the rate his bank actually used. Pending and overdue invoices are kept apart from money received. Crypto profit and loss with a year-to-date net. Payslip breakdown with EPF and APIT | [Features §3](FEATURES_SPECIFICATION.md#3-income-hub) |
| Spending | One-tap Sri Lankan presets. Committed vs discretionary at entry. Four accounts including cash and the secondary auto-debit account. Bank SMS shared into a draft. Spending Freeze | [Features §2, §4](FEATURES_SPECIFICATION.md) |
| Subscriptions | Detected from his own charges. Keep/cancel decisions, "Did you use this?", and the secondary account highlighted | [Features §4](FEATURES_SPECIFICATION.md#4-expenses-and-subscription-audit) |
| Goal | The MacBook as a 10-level quest. Required pace (≈ LKR 1,330/day) against his actual savings rate. Habits priced in goal days. Target linked to the USD rate | [Features §5](FEATURES_SPECIFICATION.md#5-goal-the-macbook-quest) |
| Daily answer | Safe-to-Spend per day for the rest of the month, never counting unpaid invoices | [Features §2](FEATURES_SPECIFICATION.md#2-dashboard) |
| Sri Lankan context | APIT bands from Firestore. EPF balance. Seasonal Peak warnings for Avurudu, Vesak, Poson, December and his own peak months | [Features §2, §3](FEATURES_SPECIFICATION.md) |

**Requirement status, 05/11/2026** (from [SRS.md](SRS.md)):

| Status | Count | Requirements |
|---|---|---|
| Done | 73 of 73 | |

---

## 3. How it is built

- **MVVM with Hilt.** Compose screens collect `StateFlow` from ViewModels. The calculations (Safe-to-Spend, goal pace, payslip, subscriptions, seasonal peaks) are domain use cases with JVM unit tests.
- **Offline-first.**
  - Screens read only from a Room cache.
  - Firestore is the source of truth; its listeners mirror changes into Room while a screen is open.
  - Writes queue in Firestore's disk cache until he is back online.
- **One rate decision.** A fetched exchange rate only *suggests*; the LKR he actually received decides, and it is frozen with the entry ([ADR-0001](adr/0001-fetched-rate-suggests-received-amount-decides.md)).

→ Diagrams and data flow: **[ARCHITECTURE.md](ARCHITECTURE.md)**
→ Firestore collections, fields, rules and Room cache: **[DATA_MODELS_AND_STORAGE.md](DATA_MODELS_AND_STORAGE.md)**
→ Auth, permissions and data protection: **[API_AND_SECURITY.md](API_AND_SECURITY.md)**
→ Colours, type and components: **[DESIGN_SYSTEM.md](DESIGN_SYSTEM.md)**

---

## 4. Working on it

→ Build, run, test and contribute: **[SETUP_AND_CONTRIBUTION_GUIDE.md](SETUP_AND_CONTRIBUTION_GUIDE.md)**
→ CI and the APK upload to Google Drive: **[RELEASE_WORKFLOW_AND_GDRIVE.md](RELEASE_WORKFLOW_AND_GDRIVE.md)**
→ Domain terms (Safe-to-Spend Allowance, Pending Invoice, Freeze Break, …): **[GLOSSARY.md](../GLOSSARY.md)**
