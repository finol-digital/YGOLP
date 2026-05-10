## 1. Build Hygiene

- [x] 1.1 Change `implementation(libs.compose.ui.tooling)` to `debugImplementation(libs.compose.ui.tooling)` in `app/build.gradle.kts`
- [x] 1.2 Remove any unused imports across all Kotlin source files

## 2. Player Enum Refactor

- [x] 2.1 Create a `Player` enum (`ONE`, `TWO`) in a new file `presentation/model/Player.kt`, replacing the `PLAYER_1`/`PLAYER_2` int constants in `PlayerIndicator.kt`
- [x] 2.2 Update `MainViewModel.changeLifePoints()` to accept `Player` enum instead of `Int`
- [x] 2.3 Update `LifePointsScreen`, `CalculatorScreen`, `PlayerIndicator`, and `Screen` route helpers to use `Player` enum
- [x] 2.4 Update `MainActivity`/`WearApp` navigation code to use `Player` enum (convert to/from Int for nav arguments)

## 3. SoundManager Fixes

- [x] 3.1 Refactor `SoundManager.play()` to use `MediaPlayer` with `setOnCompletionListener` for `duel_start` and `its_time_to_duel` sounds; keep `SoundPool` for `lifepoints_change`
- [x] 3.2 Add stop-and-release logic when playing a sound that is already playing (prevent overlap)
- [x] 3.3 Add a `released` guard flag to `SoundManager` so that `play()` is a no-op after `releaseAll()` has been called

## 4. State Management Fixes

- [x] 4.1 Make `restart()` await the DataStore edit (write zeros) before calling `changeLifePoints(8000)` for each player
- [x] 4.2 Skip `CountDownTimer` creation when `playSound = false` — set `displayedLifePoints` directly to the final value instead of creating a 0ms timer

## 5. Navigation Safety

- [x] 5.1 Add `launchSingleTop = true` to the `navController.navigate()` call for the Player 2 LifePoints screen in `WearApp`
- [x] 5.2 Replace the `navControllerInstance` Activity field with a ViewModel-based callback mechanism for hardware key routing in `onKeyDown`
- [x] 5.3 Guard the `onRestart` invocation in `LifePointsScreen` with an explicit `let` or local-variable null check instead of relying on smart-cast

## 6. UI Theming

- [x] 6.1 Create `presentation/theme/AppColors.kt` with named color constants (`LpTextYellow`, `P2GradientStart`, `P2GradientEnd`, `IndicatorWhite`, etc.)
- [x] 6.2 Replace all inline `Color(...)` and `Color.Blue`/`Color.Black` references in `LifePointsScreen.kt` and `CalculatorScreen.kt` with the named constants

## 7. Accessibility

- [x] 7.1 Add `contentDescription` semantics to `CalculatorButton` and `OperatorButton` composables (digits announce the digit, special buttons announce their function)

## 8. Input Validation

- [x] 8.1 Limit `operandText` in the `append()` function to a maximum of 5 characters, rejecting further input once the limit is reached
