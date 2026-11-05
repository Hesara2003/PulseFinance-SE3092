# Design System

PulseFinance uses a light theme: porcelain white surfaces with one signal green. It is designed for one reader. Kavindu abandoned four money tools, the last because "incomplete entries felt more discouraging than helpful" (SRS F1). The visual language therefore aims to make opening the app feel calm and useful rather than like being graded.

Tokens live in [`ui/theme/Color.kt`](../app/src/main/java/com/se3092/pulsefinance/ui/theme/Color.kt), [`Type.kt`](../app/src/main/java/com/se3092/pulsefinance/ui/theme/Type.kt) and [`Theme.kt`](../app/src/main/java/com/se3092/pulsefinance/ui/theme/Theme.kt). The redesign landed on 16/10/2026 and replaced the original dark theme.

---

## 1. Principles, from the scenario

| Principle | Why, for Kavindu | Where it shows |
|---|---|---|
| **One number first** | He opens the app to answer "can I spend today?" | The dashboard hero is the Safe-to-Spend figure; monthly net in/out sits underneath as context |
| **Amber, not red** | He avoids looking at money he's worried about. Alarm red makes an avoider look away | All attention states (overdue invoice, high-risk subscription, freeze break) use amber `#F59E0B` with deep-amber text `#B45309`. There is no red in the palette |
| **No punishment for gaps** | Gaps killed his Google Sheet | No streaks, lives or "0 spent" days. The MacBook Quest's levels are real rupee amounts, so progress only ever goes forward (NFR-2) |
| **Money in his terms** | Rupees, rent, PickMe, a MacBook | Figures as `LKR 34,000` in a dedicated numeric font. Goal impact as "days of MacBook" rather than percentages |
| **Friction only where it helps** | He is impulsive with discretionary spending | One tap to log a coffee, but a pause with the goal-day cost before spending from a frozen account |

---

## 2. Colour

### 2.1 Core palette

| Token | Hex | Role |
|---|---|---|
| `NeoGreen` | `#00C950` | Signal green: primary fills, progress, the Quest road, the Safe-to-Spend hero gradient (to `#00A844`) |
| `NeoGreenDark` | `#006E28` | Material `primary`; green text on white, money in |
| `NeoGreenLight` / `NeoGreenFixed` | `#3BE366` / `#6BFF84` | Light green accents, the "USD Ref" chip on the goal card |
| `NeoSurface` | `#F7F9FB` | App background |
| `NeoCard` | `#FFFFFF` | Cards and bottom sheets |
| `NeoCardLow` / `NeoContainer` / `NeoContainerHigh` | `#F2F4F6` / `#ECEEF0` / `#E6E8EA` | Nested surfaces, chips, inputs |
| `NeoBorder` / `NeoTrack` | `#E2E8F0` | Card borders, dividers, empty chart tracks |
| `NeoTextPrimary` | `#191C1E` | Primary text |
| `NeoTextSecondary` | `#565E74` | Secondary text |
| `NeoTextTertiary` | `#595F66` | Muted text |
| `OnAccent` | `#FFFFFF` | Text and icons on green |

### 2.2 Attention (no red)

| Token | Hex | Role |
|---|---|---|
| `NeoAmber` | `#F59E0B` | Warning fills: overdue invoice, high risk, freeze |
| `NeoRose` | `#B45309` | Attention text. Deep amber, readable on white; named "Rose" for history |
| `NeoErrorContainer` | `#FEF3C7` | Pale amber background behind attention messages |

### 2.3 Categories

| Token | Hex | Used for |
|---|---|---|
| `FoodColor` | `#F97316` | Food & dining: his coffee and UberEats |
| `TransportColor` | `#3B82F6` | Transport: PickMe |
| `RentColor` / `TechColor` | `#8B5CF6` | Rent; tech & equipment |
| `CryptoColor` | `#EAB308` | Crypto income |

`Color.kt` also keeps compatibility aliases from the old dark theme (`BackgroundDark`, `SurfaceDark`, `EmeraldPrimary`, …). They now map onto the light palette, so older components render in the new theme.

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
- **Elevation:** soft, low-contrast shadows (`#0F172A` at 5–12% alpha) instead of borders on hero elements.
- **Screen gutters:** about 20–22 dp; bottom sheets use 22 dp horizontal padding.

---

## 5. Components

| Component | File | What it shows Kavindu |
|---|---|---|
| Safe-to-Spend hero | `DashboardScreen.kt` (`SafeToSpendHero`) | Green gradient card: today's allowance, days left, the month's net, in and out. Can be masked |
| Committed vs discretionary bar | `components/CommittedVsDiscretionaryBar.kt` | One capsule split into committed and discretionary, with amounts |
| MacBook goal card | `components/MacBookGoalCard.kt` | Saved vs target, required pace pill, "USD Ref" chip with target change since set |
| Quest map | `components/QuestMap.kt` | The goal as 10 levels of LKR 49,000 along a green road, with the next checkpoint |
| Category donut | `components/CategoryDonutChart.kt` | Where the month's spending went, by category colour |
| Transaction visuals | `ui/common/TransactionVisuals.kt` | One icon and colour per income source and expense category, used in every list |
| Income stream card | `components/IncomeStreamCard.kt` | Source, client, original currency and the LKR equivalent, plus status |
| Profile avatar | `components/ProfileAvatar.kt` | His initials on a colour derived from them |
| Empty state | `components/EmptyStateView.kt` | Storyset illustration and one next step, never a blank screen (NFR-9) |
| Floating illustration | `components/FloatingIllustration.kt` | Gently floating Storyset art on sign-in and onboarding |

**Navigation:** a floating, fully rounded bottom dock with **Today · Analytics · Income · Quest · Audit**. The Income Hub is the centre button.

**Illustrations:** Storyset (Freepik) `.webp` files in `res/drawable-nodpi/`: `finance_app`, `insights`, `no_data`, `piggy_bank`, `stressed`, `success`, `wallet`, plus `logo_mark`.

---

## 6. Accessibility

Accessibility has not been checked yet (NFR-8).

- **Already true:** the palette has no red, and attention states use dark amber text on pale amber.
- **Still to verify:** that every green/amber state also has an icon or text label, and that layouts survive a 200% font scale.
