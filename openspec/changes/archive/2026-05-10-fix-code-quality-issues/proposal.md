## Why

A thorough code-quality audit of the YGOLP WearOS app revealed 12 issues spanning race conditions, resource leaks, incorrect build configuration, and missing platform best practices. Several are potential runtime bugs (restart race condition, stale NavController, immediate sound completion callbacks), while others degrade release APK quality or accessibility. Fixing these now prevents user-facing defects before the next release.

## What Changes

- Fix race condition in `MainViewModel.restart()` where DataStore writes of 0 and 8000 can interleave, causing displayed LP to reset
- Replace `SoundPool` with `MediaPlayer` for sounds needing completion callbacks, so the scramble animation actually waits for the sound to finish
- Add null-safety/reuse guard to `SoundManager` after `releaseAll()` is called
- Prevent navigation back-stack buildup when swiping between Player 1 and Player 2 screens
- Guard against potential null dereference on `onRestart` in `LifePointsScreen`
- Move `compose-ui-tooling` from `implementation` to `debugImplementation`
- Skip `CountDownTimer` creation when `playSound = false` (duration 0 timer is wasteful)
- Replace Activity-field `navControllerInstance` with a ViewModel-based event mechanism for hardware key handling
- Extract hardcoded colors into the theme/constants
- Add accessibility content descriptions to calculator buttons
- Limit calculator operand input to 5 digits to match `MAX_LIFE_POINTS`
- Replace magic int player identifiers with a proper enum

## Capabilities

### New Capabilities
- `build-hygiene`: Correct dependency scoping (debug vs release), remove unused code, and fix build configuration issues
- `navigation-safety`: Prevent back-stack duplication and stale NavController references
- `accessibility-improvements`: Add content descriptions and TalkBack support to interactive UI elements
- `ui-theming`: Extract hardcoded colors into reusable theme constants

### Modified Capabilities
- `state-management`: Fix restart race condition by sequencing DataStore writes; skip zero-duration timers
- `media-player-management`: Use MediaPlayer (not SoundPool) for sounds requiring completion callbacks; guard post-release usage
- `input-validation`: Limit operand text length to 5 digits; replace magic int player IDs with an enum

## Impact

- **Files modified**: `MainViewModel.kt`, `SoundManager.kt`, `MainActivity.kt`, `LifePointsScreen.kt`, `CalculatorScreen.kt`, `PlayerIndicator.kt`, `app/build.gradle.kts`
- **Dependencies**: No new external dependencies; `SoundPool` partially replaced by `MediaPlayer` (Android SDK built-in)
- **Risk**: Medium — sound timing changes affect the duel-start animation UX; navigation changes affect swipe-dismiss behavior. Both need manual QA on a real WearOS device.

