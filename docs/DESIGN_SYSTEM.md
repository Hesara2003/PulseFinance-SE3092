# Design System

PulseFinance uses a light theme: porcelain white surfaces with one signal green. It is designed for one reader. Kavindu abandoned four money tools, the last because "incomplete entries felt more discouraging than helpful" (SRS F1). The visual language therefore aims to make opening the app feel calm and useful rather than like being graded.

The raw palette is in [`ui/theme/Color.kt`](../app/src/main/java/com/se3092/pulsefinance/ui/theme/Color.kt) and the type scale in [`Type.kt`](../app/src/main/java/com/se3092/pulsefinance/ui/theme/Type.kt). [`Theme.kt`](../app/src/main/java/com/se3092/pulsefinance/ui/theme/Theme.kt) maps the palette onto **Material 3 colour roles**, and screens read colours only through the theme: `MaterialTheme.colorScheme.…` for the standard roles and `MaterialTheme.pulseColors.…` for the few roles Material 3 doesn't have. No screen hard-codes a colour. The light redesign landed on 16/10/2026 and replaced the original dark theme.

---

## 1. Principles, from the scenario

| Principle | Why, for Kavindu | Where it shows |
|---|---|---|
| **One number first** | He opens the app to answer "can I spend today?" | The dashboard hero is the Safe-to-Spend figure; monthly net in/out sits underneath as context |
| **Amber, not red** | He avoids looking at money he's worried about. Alarm red makes an avoider look away | Attention states (overdue invoice, freeze break, errors) use amber `#F59E0B` with deep-amber text `#B45309` — Material 3's `error` role is this deep amber. **One deliberate exception:** the subscription audit marks "Still charging" and the Cancel actions in rose (`#E11D48`), because a forgotten subscription draining money is the one thing he should not look away from (P6) |
| **No punishment for gaps** | Gaps killed his Google Sheet | No streaks, lives or "0 spent" days. The MacBook Quest's levels are real rupee amounts, so progress only ever goes forward (NFR-2) |
| **Money in his terms** | Rupees, rent, PickMe, a MacBook | Figures as `LKR 34,000` in a dedicated numeric font. Goal impact as "days of MacBook" rather than percentages |
| **Friction only where it helps** | He is impulsive with discretionary spending | One tap to log a coffee, but a pause with the goal-day cost before spending from a frozen account |

---

## 2. Colour

### 2.1 Brand palette → Material 3 roles

Screens use the role, never the hex value.

| Role (`MaterialTheme.colorScheme`) | Brand colour | Hex | Used for |
|---|---|---|---|
| `primaryContainer` / `onPrimaryContainer` | `NeoGreen` / white | `#00C950` / `#FFFFFF` | Signal green: filled buttons, selected chips, progress, the Quest road, the Safe-to-Spend hero (gradient to `pulseColors.primaryDeep` `#00A844`), and text on it |
| `primary` | `NeoGreenDark` | `#006E28` | Green text and icons on white, money in, links |
| `background` | `NeoSurface` | `#F7F9FB` | The page behind the cards |
| `surface` | `NeoCard` | `#FFFFFF` | Cards, sheets, dialogs, the dock |
| `surfaceContainerLow` / `surfaceContainer` / `surfaceContainerHigh` / `surfaceContainerHighest` | `NeoCardLow` … | `#F2F4F6` / `#ECEEF0` / `#E6E8EA` / `#E0E3E5` | Icon tiles, inset panels, chips, chart tracks |
| `onSurface` | `NeoTextPrimary` | `#191C1E` | Primary text |
| `onSurfaceVariant` | `NeoTextSecondary` | `#565E74` | Secondary text |
| `outlineVariant` | `NeoBorder` | `#E2E8F0` | Card borders, dividers |
| `secondaryContainer` / `onSecondaryContainer` | | `#DAE2FD` / `#131B2E` | The "Savings Goal" tag |
| `inverseSurface` / `inverseOnSurface` | | `#2D3133` / `#EFF1F3` | The dark "cancel all flagged" button |
| `error` / `errorContainer` | `NeoRose` / `NeoErrorContainer` | `#B45309` / `#FEF3C7` | Attention text and its pale amber background — deep amber, not red |

### 2.2 Extra roles (`MaterialTheme.pulseColors`)

Material 3 has no role for these, so `Theme.kt` adds them as `PulseColors`, provided next to the colour scheme:

| Role | Hex | Used for |
|---|---|---|
| `warning` | `#F59E0B` | Amber fills: overdue, awaiting payment, high risk |
| `textTertiary`, `textQuiet` | `#595F66`, `#8A93A3` | Muted and quiet text |
| `primaryLight`, `primaryFixed`, `onPrimaryFixed`, `primaryGlow`, `primaryDeep` | `#3BE366`, `#6BFF84`, `#002107`, green at 22%, `#00A844` | Light green tints, the FAB glow, the hero gradient |
| `track` | `#E2E8F0` | Empty chart tracks |
| `danger`, `dangerStrong`, `dangerContainer`, `dangerBorder`, `onDanger` | `#E11D48`, `#9F1239`, `#FFF1F2`, `#FFCCD5`, white | Subscription audit only: "Still charging", Cancel, "No, I didn't use it" |
| `success`, `successContainer`, `successBorder` | `#047857`, `#ECFDF5`, `#A7F3D0` | Audit: "Active · Kept", "Yes, I used it", the auto-debit account pill |
| `caution`, `cautionContainer`, `cautionBorder` | `#C2410C`, `#FFF7ED`, `#FED7AA` | Audit: "Needs review" |
| `food`, `transport`, `rent`, `violet` | `#F97316`, `#3B82F6`, `#8B5CF6`, `#8B5CF6` | Category colours in charts: his coffee and UberEats, PickMe, rent |
| `shadow` | `#0F172A` | Card shadows, applied at 2–16% alpha |

Canvas drawing (the avatar, the Quest road, the donut track) isn't a composable, so those composables read their colours from the theme just before the `Canvas`.

---

## 3. Typography

Two bundled font families (SIL Open Font License), plus the system font for body text:

| Family | Use |
|---|---|
| **Space Grotesk** (`Numeric`) | Large money figures: display styles |
| **Bricolage Grotesque** (`Display`) | Headlines, titles, labels |
| System default | Body text and small labels |

| Style | Family | Weight | Size / line | Example |
|---|---|---|---|---|
| `displayLarge` | Space Grotesk | Bold | 44 / 48 sp, −1 sp | Defined for hero figures |
| `displayMedium` | Space Grotesk | Bold | 34 / 40 sp | Defined for secondary figures |
| `headlineLarge` | Bricolage | ExtraBold | 30 / 36 sp | Screen titles |
| `headlineMedium` | Bricolage | Bold | 24 / 30 sp | Section heads |
| `headlineSmall`, `titleLarge` | Bricolage | Bold | 20 / 26 sp | Card and sheet titles |
| `titleMedium` | Bricolage | SemiBold | 16 / 22 sp | Transaction and stream names |
| `titleSmall`, `labelLarge` | Bricolage | SemiBold | 14 / 20 sp | Buttons, chips |
| `bodyLarge` | System | Normal | 16 / 24 sp | Explanations |
| `bodyMedium` | System | Normal | 14 / 20 sp | Secondary copy |
| `labelSmall` | System | Medium | 11 / 16 sp | Tags such as `Overdue` |

In practice, screens set the `Numeric` family directly on money figures with their own sizes (9 places), rather than using the two display styles.

---

## 4. Shape and spacing

- **Corners:** cards 20–24 dp, inner tiles 14–18 dp, chips and dock buttons fully round (`CircleShape`). 24 dp is the most common radius in the code.
- **Elevation:** soft, low-contrast shadows (`pulseColors.shadow`, `#0F172A` at 2–16% alpha) instead of borders on hero elements.
- **Screen gutters:** about 20–22 dp; bottom sheets use 22 dp horizontal padding.

---

## 5. Components

| Component | File | What it shows Kavindu |
|---|---|---|
| Safe-to-Spend hero | `DashboardScreen.kt` (`SafeToSpendHero`) | Green gradient card: today's allowance, days left, the month's net, in and out. Can be masked |
| Today screen sections | `screens/dashboard/DashboardSections.kt` | Greeting, Seasonal Peak card with one-tap set-aside, accounts with Spending Freeze, one-tap presets, Lately, the weekly discretionary bars |
| Subscription audit | `screens/expense/AuditSections.kt`, `SubscriptionCard.kt` | Leak alert, annual drain and share of the goal, subscriptions grouped by account; each card shows status, charges, app activity, "did you use it?" and Keep / Cancel |
| Committed vs discretionary bar | `components/CommittedVsDiscretionaryBar.kt` | One capsule split into committed and discretionary, with amounts |
| Goal card | `components/GoalCard.kt` | Saved vs target, required pace pill, "USD Ref" chip with target change since set; the picture comes from the goal's name |
| Quest map | `components/QuestMap.kt` | The goal as 10 levels along a green road, labelled with the goal's own name, its picture at the end |
| Category donut | `components/CategoryDonutChart.kt` | Where the month's spending went, by category colour |
| Pop-up notice | `components/PulseNotice.kt` (`PulseNoticeHost`, `UiNotice`) | The one confirmation/error pop-up on every screen: a white card that drops in below the status bar with the illustration of what was saved (the cup for a coffee), then dismisses itself. Errors use the amber `error` tone and stay longer |
| Delete confirmation | `components/DeleteTransactionDialog.kt` | "Delete this expense?" with its icon and what it will change; reached by long-pressing a Lately card or ⋮ → Delete in Analytics |
| Income stream card | `components/IncomeStreamCard.kt` | Source, client, original currency and the LKR equivalent, plus status |
| Profile avatar | `components/ProfileAvatar.kt` | His Google profile photo when he signs in with Google; otherwise his initials on a mark derived from them |
| Empty state | `components/EmptyStateView.kt` | Storyset illustration and one next step, never a blank screen (NFR-9) |
| Floating illustration | `components/FloatingIllustration.kt` | Gently floating Storyset art on sign-in and onboarding |

**Icons.** Things Kavindu recognises — a coffee, a PickMe, rent, his bank, a salary, an AdSense payout, his goal — use **illustrated icons** in the Storyset style: flat palette fills, a dark `#1E2A22` outline and a pale blob behind (`res/drawable/ic_preset_*.xml`). `ui/common/TransactionVisuals.kt` picks them:

| Function | Picks |
|---|---|
| `presetIllustration(title, category)` | From the merchant first (coffee → cup, PickMe → taxi, Keells → basket), else the category |
| `categoryIllustration(category)` | One per expense category (chips, breakdowns, goal suggestions) |
| `incomeSourceIllustration(source)` | Salary briefcase, freelance laptop, AdSense, crypto coin |
| `accountIllustration(type)` | Bank, cash (banknote and coin), exchange coin |
| `goalIllustration(title)` | From the goal's name: laptop/phone → laptop, home → house, car/bike → vehicle, anything else → piggy bank |

Plain Material icons are kept for controls and status: chevrons, close, the eye toggle, download, a frozen account's lock.

**Navigation:** a floating, fully rounded bottom dock with **Today · Analytics · Income · Quest · Audit**, using illustrated icons (`ic_nav_*`): the selected tab in colour on a soft green blob, the others greyed; the Income Hub is the centre button, a white disc with a green ring and a wallet. Switching tabs uses a Material **shared-axis** transition: the new page slides a fifth of the way in from the side of the tab tapped while it fades in, in 300 ms.

**Illustrations:** Storyset (Freepik) `.webp` files in `res/drawable-nodpi/`: `finance_app`, `insights`, `no_data`, `piggy_bank`, `stressed`, `success`, `wallet`, plus `logo_mark`.

---

## 6. Accessibility

Accessibility was checked on a device on 05/11/2026 (NFR-8): font size and display size at maximum, Google Accessibility Scanner on every screen, no findings (`docs/qa/accessibility-check.md`).

- **Already true:** attention states use dark amber text on pale amber; icon-only controls carry content descriptions; the dock tabs and the Income button are exposed as tabs (`Role.Tab`), and the pop-up is a polite live region so screen readers announce it.
- **Also true:** every green, amber or red state carries an icon or a text label as well as its colour, and fixed-height controls use `heightIn(min = ...)` so text is not clipped at 200% font scale.
