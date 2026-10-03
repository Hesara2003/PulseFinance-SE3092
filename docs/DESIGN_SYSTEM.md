# Vivid Neo-Fintech Design System

## 1. Design Philosophy: "Vivid Neo-Fintech"

Traditional banking applications are notorious for dull corporate gray palettes, cluttered tabular lists, and emotionally disconnected number dumps. PulseFinance breaks this paradigm by implementing the **Vivid Neo-Fintech** design language:

- **Cyber-Luxury Contrast**: Pitch-black and obsidian background surfaces (`#080C14` and `#0D1117`) paired with high-voltage neon accents (`#135BFB` Electric Blue, `#00E5FF` Cyan, and `#10B981` Emerald Mint).
- **Capsule Geometry**: Soft pill-shaped tags, rounded 24dp–32dp card containers, and fluid organic curves that feel modern and tactile.
- **Glassmorphism & Depth**: Multi-layer elevation achieved through translucent dark surfaces (`rgba(22, 27, 34, 0.85)`), hairline borders (`#30363D`), and subtle radial gradient glows.
- **Emotional Pacing**: Financial metrics are presented not merely as cold figures, but as energetic velocity indicators (e.g., daily burn gauges, pacing progress, and opportunity cost comparisons).

---

## 2. Color Palette & Token Definitions

All color tokens are centralized in [Color.kt](file:///Users/hesaraperera/Desktop/PulseFinance/app/src/main/java/com/se3092/pulsefinance/ui/theme/Color.kt):

| Token Name | Hex Code | Compose Value | Purpose & Usage |
| :--- | :--- | :--- | :--- |
| **`PrimaryBlue`** | `#135BFB` | `Color(0xFF135BFB)` | Main brand accent, primary CTA buttons, active state indicators. |
| **`ElectricCyan`** | `#00E5FF` | `Color(0xFF00E5FF)` | High-voltage highlights, goal velocity glow, primary gradient endpoints. |
| **`EmeraldGreen`** | `#10B981` | `Color(0xFF10B981)` | Income badges, positive net cash flow, Safe-To-Spend gauge healthy zone. |
| **`WarningAmber`** | `#F59E0B` | `Color(0xFFF59E0B)` | Caution zone, overdue freelance milestones, medium subscription risk. |
| **`DangerRed`** | `#EF4444` | `Color(0xFFEF4444)` | Critical alerts, zombie subscription nuke buttons, high-risk leaks. |
| **`BackgroundDark`** | `#080C14` | `Color(0xFF080C14)` | Deepest app background (OLED energy-saving canvas). |
| **`SurfaceDark`** | `#0D1117` | `Color(0xFF0D1117)` | Primary surface for top bars, sticky headers, and bottom sheets. |
| **`CardBackground`** | `#161B22` | `Color(0xFF161B22)` | Default card background container elevation. |
| **`CardElevated`** | `#21262D` | `Color(0xFF21262D)` | Floating dialogs, elevated tooltips, and interactive pill buttons. |
| **`BorderDark`** | `#30363D` | `Color(0xFF30363D)` | Hairline strokes for cards and dividers (1dp subtle outline). |
| **`TextPrimary`** | `#F0F6FC` | `Color(0xFFF0F6FC)` | High-emphasis body text, major currency balance titles (100% white-silver). |
| **`TextSecondary`** | `#8B949E` | `Color(0xFF8B949E)` | Medium-emphasis labels, subtitles, timestamp strings. |
| **`TextTertiary`** | `#484F58` | `Color(0xFF484F58)` | Low-emphasis placeholders, disabled icons, inactive state tracks. |

---

## 3. Typography Hierarchy

The typography engine is anchored on **Plus Jakarta Sans** and **Inter**, prioritizing optical legibility at high data density:

| Style Role | Font Weight | Size (sp) | Tracking | Primary Application |
| :--- | :--- | :--- | :--- | :--- |
| **Display Large** | ExtraBold (800) | 36sp | -1.0sp | Net Worth & Hero Balance figures (`LKR 510,500`) |
| **Display Medium** | Bold (700) | 28sp | -0.5sp | Section hero headers, Safe-to-spend metric callouts |
| **Headline Large** | Bold (700) | 22sp | -0.2sp | Screen titles (Cockpit, Income Hub, Goal Velocity) |
| **Headline Small** | SemiBold (600) | 18sp | 0.0sp | Card group headers, bottom sheet titles |
| **Title Medium** | SemiBold (600) | 16sp | +0.1sp | Transaction names, stream titles, button labels |
| **Body Large** | Medium (500) | 15sp | +0.15sp | Explanatory descriptions, scenario narrative text |
| **Body Small** | Regular (400) | 13sp | +0.2sp | Secondary metadata, dates, account numbers |
| **Label Small** | Bold (700) | 11sp | +0.5sp | Pill tags (`REALIZED`, `DISCRETIONARY`, `OVERDUE`) |

---

## 4. Component Design Patterns

### 4.1. The Topographic Titanium Debit Card
- **Background**: Deep gradient blending from `#1A2035` through `#0D1117`.
- **Topographic Contours**: Subtle vector contour curves overlaid at 12% alpha.
- **Physical EMV Gold Chip**: Rendered with brushed gold gradient (`#E5A93C` to `#FAD961`) and micro-contact circuit division lines.
- **Cardholder Detail**: Styled with monospaced letter spacing, displaying masked account numbers and holographic Pulse logo.

### 4.2. Safe-To-Spend Arc Gauge (`SafeToSpendGauge.kt`)
- Custom Canvas drawing a 240-degree sweep arc.
- Inactive track rendered in `#21262D`.
- Dynamic active stroke painted using an angled `SweepGradient` (`#135BFB` $\to$ `#00E5FF` $\to$ `#10B981`).
- Center readout displaying live remaining liquid allowance after accounting for committed liabilities (LKR 34,000 rent) and savings targets.

### 4.3. Committed vs. Discretionary Stacked Capsule (`CommittedVsDiscretionaryBar.kt`)
- A single 16dp rounded capsule bar with segmented fill:
  - **Committed Split**: `#135BFB` (Rent, WiFi, loan dues).
  - **Discretionary Split**: `#F59E0B` or `#00E5FF` (Dining, cafe, entertainment).
  - **Unspent Margin**: `#21262D` (Buffer runway).
- Accompanied by interactive legend indicators showing percentage splits and absolute LKR values.

### 4.4. Floating Pill Navigation Dock
- Suspended 16dp above the display bottom edge with 24dp rounded corners.
- Background: `Color(0xFF161B22).copy(alpha = 0.92f)` with blur backdrop filter.
- Active tab pill: Glow pill container highlighted in `#135BFB` with high-contrast `#FFFFFF` icon.
- Inactive tabs: Muted `#8B949E` with 200ms spring scale transition on tap.

---

## 5. Micro-Interactions & Animation Standards

1. **Velocity Slider Response**: Continuous visual feedback on savings goal allocation slider (`MacBookGoalCard.kt`). Adjusting the slider instantly triggers spring interpolation on the daily velocity badge and target completion calendar date.
2. **One-Tap Nuke Animation**: When cancelling a Zombie subscription in `ExpenseScreen.kt`, the card triggers a red flash animation, collapses vertically with `shrinkVertically()`, and updates the annual savings drag counter.
3. **Pill Status Badges**: Overdue invoices pulse with an amber glow border, drawing immediate focus without obstructing user navigation.
4. **Haptic Feel**: Touch targets maintain standard $48\times 48\text{dp}$ interaction boxes with bounded ripple effects.
