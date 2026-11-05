# Accessibility check (NFR-8)

Requirement: status is never shown by colour alone, and every screen stays usable at 200% font size.

## Method

1. **Code audit** of every place a red/amber/green state is drawn (done, below).
2. **Font scale:** Settings > Display > Font size > largest, plus Display size > largest. Walk every screen.
3. **Accessibility Scanner** (Google) on each screen; record findings in the table.

## Status indicators: colour-only audit

| Location | State | Had text/icon? | Result |
|---|---|---|---|
| Goal screen, pacing card | On track (green) / behind (rose) | Colour on the "YOU SAVE" figure, plus an illustration | **Gap**: no text label. Fixed: status chip with icon and text |
| Income stream card | Received / Pending / Overdue | Text label only | Added a matching icon |
| Subscription audit | High risk, status pills | Text label ("High Risk"), plus a lock/phone icon | OK |
| Dashboard, week change | Less / more than last week | Text ("% less", "+% vs") | OK |
| Dashboard, accounts | Frozen | Lock icon and text | OK |

## Large font scale: layout audit

Fixed-height controls clip their text when the font grows. These used `height(..)` and now use `heightIn(min = ..)`:
GoalScreen (5), AuthScreen (2), PayslipBottomSheet, AddIncomeBottomSheet, AddExpenseBottomSheet, IncomeScreen, SetupScreen (2), ExpenseScreen (2), AnalyticsScreen.

## Manual results (device check, 05/11/2026)

Font size and display size set to maximum; every screen walked, with Google Accessibility Scanner run on each.

| Screen | 200% font OK? | Scanner findings | Notes |
|---|---|---|---|
| Auth / onboarding | Yes | None | None |
| Dashboard | Yes | None | None |
| Income | Yes | None | None |
| Expense / subscription audit | Yes | None | None |
| Goal | Yes | None | None |
| Analytics | Yes | None | None |
| Setup | Yes | None | None |

No findings, so no further fixes were needed. NFR-8 is Done in `docs/SRS.md`.
