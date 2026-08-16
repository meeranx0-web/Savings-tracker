# Savings Tracker — Android App (Source Project)

## ⚠️ Important: why this is source code, not a pre-built APK

This project was built in a sandboxed environment with **no internet access and no Android SDK/Gradle installed**. Compiling an APK requires downloading the Android build tools, Gradle, and library dependencies from Google's Maven repo — none of that is possible here. So instead of claiming to hand you a working APK I can't actually verify, I built the **complete, ready-to-compile source project** below. Turning it into `SavingsTracker.apk` takes about 2 minutes on your own machine.

## How to get the APK (2 minutes)

1. Install [Android Studio](https://developer.android.com/studio) (free) if you don't have it.
2. Open Android Studio → **Open** → select this `SavingsTracker` folder.
3. Let it sync (Android Studio auto-downloads the Gradle wrapper and dependencies the first time — this needs internet once).
4. Build → **Build APK(s)** (or `Build > Build Bundle(s)/APK(s) > Build APK(s)`).
5. Click the "locate" link in the notification, or find it at:
   `app/build/outputs/apk/debug/app-debug.apk`
6. Rename to `SavingsTracker.apk`, copy it to your phone, and tap to install (enable "install from unknown sources" if prompted).

No emulator needed — you can plug your phone in via USB with USB debugging on and hit ▶ Run to install directly.

## What's implemented

- **76 individual savings boxes**, each with a unique ID, summing to **exactly KSh 10,000** (verified by a runtime assertion in `BoxGenerator.kt`): 20×10, 15×20, 10×50, 10×100, 10×200, 5×400, 5×600, 1×1000.
- **Room database** (`savings_tracker.db`) persists every box's saved state + completion timestamp — survives app close, force-close, and phone restart.
- **DataStore** persists theme preference and challenge start date.
- Home dashboard: total saved, remaining, circular + linear progress, completed/saved/remaining stat cards, days left, dynamically-recalculated daily target, streak badge, Today's Savings card, Smart Pick, 100%-completion celebration card.
- Savings board: boxes grouped into Small/Medium/Large tiers, animated custom check cards (no default checkboxes).
- History: grouped by calendar date (from real completion timestamps), with per-day and overall totals.
- Settings: Light/Dark/System theme, deadline display (Dec 25, 2026), currency (KSh), Reset Challenge with a confirmation dialog that deletes and re-seeds all 76 boxes.
- 4-tab bottom navigation (Home / Savings / History / Settings) via Navigation-Compose.
- 100% offline — no network permission is even declared in the manifest, no login, no cloud.

## Project structure

```
app/src/main/java/com/savingstracker/app/
  data/        Room entity, DAO, database, box generator, repository (all calculations), DataStore settings
  viewmodel/   SavingsViewModel — single source of UI state
  ui/theme/    Colors, typography, light/dark MaterialTheme
  ui/components/  Reusable box card, stat card, circular progress ring
  ui/screens/  Home, Savings, History, Settings
  MainActivity.kt  Bottom nav + NavHost wiring
```

## Note on the box counts

I couldn't fully verify a live device build in this sandbox, but the box-sum invariant is enforced in code (`BoxGenerator` throws at startup if the total isn't exactly 10,000) and I traced the calculation logic by hand — total saved, remaining, percentage, streak, and daily target all derive from the single `SavingsSummary` computation in `SavingsRepository.kt`, so there's one source of truth for every number shown on screen.
