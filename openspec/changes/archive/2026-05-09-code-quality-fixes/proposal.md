## Why

The YGOLP WearOS app has several code quality issues ranging from potential crashes and resource leaks to architectural concerns that make the codebase harder to maintain. Addressing these now prevents user-facing bugs (ANR from `runBlocking`, MediaPlayer leaks, CountDownTimer leaks) and establishes a healthier foundation before adding new features.

## What Changes

- **Fix `runBlocking` on main thread**: Replace `runBlocking` calls in `onCreate` with proper coroutine-based or synchronous alternative to prevent ANR risk.
- **Fix MediaPlayer lifecycle leaks**: Ensure MediaPlayers are properly released in all paths and cannot be double-started; consolidate MediaPlayer management.
- **Fix CountDownTimer leak**: Store CountDownTimer references so they can be cancelled on activity stop or when a new animation starts, preventing overlapping animations and leaked timers.
- **Replace `java.util.Random` with `kotlin.random.Random`**: Use idiomatic Kotlin `Random.nextInt()` and stop instantiating `Random()` on every tick (every 50ms).
- **Extract state management to a ViewModel**: Move mutable state (`lifePoints`, `displayedLifePoints`, `isMuted`, etc.) out of `MainActivity` into a proper `ViewModel` with `StateFlow`, fixing the tight coupling between Activity and Compose state.
- **Remove dead code**: `LifePointsActivity` and `CalculatorActivity` classes are never launched (not in manifest, no navigation to them). Remove them or convert to preview-only utilities.
- **Add input validation**: Prevent negative life points from calculator results; clamp or reject invalid values.
- **Fix swipe navigation firing repeatedly**: Add a consumed/threshold guard so horizontal drag doesn't trigger multiple navigations.
- **Enable R8/ProGuard for release builds**: Set `isMinifyEnabled = true` for the release build type.
- **Fix minor issues**: Replace `Color(0xFFFBFF0C.toInt())` with `Color(0xFFFBFF0C)`, fix `CornerRadius` using `dp.value` instead of proper pixel conversion.

## Capabilities

### New Capabilities
- `media-player-management`: Centralized, lifecycle-aware MediaPlayer management with proper creation, reuse, and release.
- `state-management`: ViewModel-based state management for life points, display values, and mute state with DataStore persistence.
- `input-validation`: Calculator result validation and clamping to prevent invalid life point values.

### Modified Capabilities
<!-- No existing specs to modify -->

## Impact

- **Code**: All 4 Kotlin source files in `presentation/` package will be modified. `LifePointsActivity` and `CalculatorActivity` classes will be removed or hollowed out (their composable functions stay). A new ViewModel class will be added.
- **Dependencies**: No new external dependencies needed (Lifecycle ViewModel is already transitively available via `activity-compose`). May need to add `lifecycle-viewmodel-compose` explicitly.
- **Build**: Release build config changes (`isMinifyEnabled`). ProGuard rules may need updating.
- **Risk**: Low — these are internal refactors with no user-facing API or behavior changes beyond bug fixes. The life point display animation timing will remain identical.

