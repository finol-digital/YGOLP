## Context

YGOLP is a WearOS Yu-Gi-Oh! life points tracker built with Jetpack Compose. The app consists of 4 Kotlin files in a single `presentation` package. All mutable state (life points, display values, mute flag) lives directly as `mutableStateOf` properties on `MainActivity`. MediaPlayer instances are manually managed, and a `CountDownTimer` is used for the randomized life-point-change animation. Two additional Activity classes (`LifePointsActivity`, `CalculatorActivity`) exist but are dead code — unused and unregistered in the manifest.

Key constraints: WearOS devices have limited memory, so resource leaks (MediaPlayer, CountDownTimer) are especially impactful. The round screen form factor means the existing UI layout should not change.

## Goals / Non-Goals

**Goals:**
- Eliminate crash/ANR risk from `runBlocking` on the main thread
- Prevent MediaPlayer and CountDownTimer resource leaks
- Move state management to a ViewModel for proper lifecycle handling
- Remove dead code to reduce maintenance burden
- Add input validation so calculator results can't produce invalid life points
- Fix repeated swipe navigation firing
- Enable R8 minification for release builds
- Fix minor code quality issues (deprecated APIs, hardcoded values)

**Non-Goals:**
- Changing the visual design or UX flow
- Adding new features (e.g., game history, duel log)
- Migrating from Compose Wear Material to Material3
- Adding unit/UI test coverage (separate effort)
- Changing the navigation architecture

## Decisions

### 1. ViewModel for state management
**Decision**: Extract all mutable state to a `MainViewModel` using `StateFlow` backed by DataStore.

**Rationale**: Currently `mutableStateOf` on the Activity class survives recomposition but not process death properly. A ViewModel with `StateFlow` is the standard Compose pattern, survives configuration changes, and cleanly separates concerns.

**Alternative considered**: Keep state on Activity but fix individual bugs. Rejected because this would leave the root architectural issue in place and make future work harder.

### 2. Coroutine-based DataStore loading
**Decision**: Replace `runBlocking` in `onCreate` with a splash/loading approach or `flow.stateIn()` in the ViewModel with a sensible default state while loading.

**Rationale**: `runBlocking` on the main thread risks ANR. Using `stateIn(SharingStarted.Eagerly)` in the ViewModel makes loaded data available synchronously to Compose while loading happens off the main thread.

**Alternative considered**: Using `runBlocking(Dispatchers.IO)`. Rejected because even a dispatcher switch still blocks the main thread waiting for the result.

### 3. MediaPlayer helper class
**Decision**: Create a `SoundManager` helper that owns all MediaPlayer instances, handles create/play/release lifecycle, and is lifecycle-aware.

**Rationale**: Current code has 3 separate MediaPlayer nullable fields with duplicated create/release patterns. A single manager reduces duplication and ensures release on lifecycle events.

### 4. CountDownTimer tracking
**Decision**: Store active CountDownTimer references in the ViewModel, cancelling any existing timer before starting a new one.

**Rationale**: Currently, `CountDownTimer` is created as an anonymous object with no stored reference. If `changeLifePoints` is called twice quickly, two timers run concurrently causing display flickering. Storing the reference allows cancellation.

### 5. Remove dead Activity classes
**Decision**: Delete the `LifePointsActivity` and `CalculatorActivity` **class** bodies while keeping the composable functions (`LifePointsScreen`, `CalculatorScreen`) in their respective files.

**Rationale**: These classes are never instantiated — not in the manifest, not navigated to. They add confusion. The composable functions they contain are used by `MainActivity.WearApp()`.

### 6. Swipe debounce
**Decision**: Add a boolean flag that is set on first swipe trigger and reset after a short delay, preventing the drag gesture from firing navigation multiple times.

**Rationale**: `detectHorizontalDragGestures` fires for every drag event. Without a guard, a single swipe can trigger `onSwipePlayer()` many times, causing navigation stack corruption.

### 7. Enable R8 for release
**Decision**: Set `isMinifyEnabled = true` and `isShrinkResources = true` in the release build type.

**Rationale**: The app ships unused resources and un-obfuscated code in release. R8 reduces APK size and improves runtime performance.

## Risks / Trade-offs

- **[Risk] ViewModel migration changes composition scope** → Mitigation: Keep composable function signatures identical; only change where state is read from. Test on device after migration.
- **[Risk] R8 may strip needed code** → Mitigation: Existing ProGuard rules file is in place. Add keep rules for any reflection-based usage (none currently identified).
- **[Risk] Removing dead Activities could break external deep links** → Mitigation: These Activities are not exported and not registered in the manifest, so no external caller exists.
- **[Risk] SoundManager lifecycle** → Mitigation: Tie it to the Activity lifecycle via `DefaultLifecycleObserver` so it releases MediaPlayers on `onStop`/`onDestroy`.

