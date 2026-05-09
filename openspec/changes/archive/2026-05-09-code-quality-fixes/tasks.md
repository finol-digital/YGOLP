## 1. Create SoundManager

- [x] 1.1 Create `SoundManager` class in `presentation/` that implements `DefaultLifecycleObserver` and encapsulates all MediaPlayer creation, playback, and release logic with a `play(soundResId, onCompletion)` method and `releaseAll()` method
- [x] 1.2 Add mute-aware guard in `SoundManager.play()` that skips playback when muted, and stop-and-release logic when a sound is already playing
- [x] 1.3 Register `SoundManager` as a lifecycle observer in `MainActivity.onCreate()` so it auto-releases on `onStop`

## 2. Create MainViewModel

- [x] 2.1 Add `lifecycle-viewmodel-compose` dependency to `app/build.gradle.kts` if not already transitively available
- [x] 2.2 Create `MainViewModel` class with `StateFlow` properties for `lifePoints`, `lifePoints2`, `displayedLifePoints`, `displayedLifePoints2`, and `isMuted`
- [x] 2.3 Load DataStore preferences in `MainViewModel.init` using `viewModelScope.launch` (non-blocking) instead of `runBlocking`, with defaults of 0/false
- [x] 2.4 Move `start()`, `restart()`, `changeLifePoints()`, and `toggleMute()` methods from `MainActivity` to `MainViewModel`, passing `SoundManager` as a dependency
- [x] 2.5 Store active `CountDownTimer` references per player in the ViewModel; cancel existing timer before starting a new one in `changeLifePoints()`
- [x] 2.6 Replace `java.util.Random` with a single `kotlin.random.Random` instance used across all timer ticks
- [x] 2.7 Add `onCleared()` override that cancels all active CountDownTimers

## 3. Refactor MainActivity

- [x] 3.1 Remove all `mutableStateOf`/`mutableIntStateOf` properties from `MainActivity`
- [x] 3.2 Remove `runBlocking` calls from `onCreate()`
- [x] 3.3 Obtain `MainViewModel` via `viewModel()` in the `WearApp` composable and wire `StateFlow` properties to existing composable parameters using `collectAsState()`
- [x] 3.4 Update `onKeyDown` to delegate to ViewModel methods via a reference
- [x] 3.5 Remove `onSaveInstanceState` override (ViewModel + DataStore handles persistence)
- [x] 3.6 Update `onStop` to only call `SoundManager.releaseAll()` (timer cancellation handled by ViewModel)

## 4. Fix Swipe Navigation

- [x] 4.1 Add a `swipeHandled` boolean flag in the `pointerInput` block in `LifePointsScreen` that is set to `true` after the first `onSwipePlayer()` call and prevents subsequent calls within the same gesture

## 5. Add Input Validation

- [x] 5.1 In `CalculatorScreen`, clamp the `result` computation to `coerceIn(0, 99999)` so negative and excessively large values are prevented
- [x] 5.2 In `changeLifePoints()` in the ViewModel, add a `coerceIn(0, 99999)` guard on the incoming `lp` parameter

## 6. Remove Dead Code

- [x] 6.1 Delete the `LifePointsActivity` class body from `LifePointsActivity.kt` (keep the file with the `LifePointsScreen` composable and constants)
- [x] 6.2 Delete the `CalculatorActivity` class body from `CalculatorActivity.kt` (keep the file with the `CalculatorScreen` composable and constants)
- [x] 6.3 Remove unused imports from both files after class removal

## 7. Fix Minor Code Issues

- [x] 7.1 Replace `Color(0xFFFBFF0C.toInt())` with `Color(0xFFFBFF0C)` in `LifePointsText`
- [x] 7.2 Fix `CornerRadius` in `PlayerIndicator` to use density-aware pixel conversion instead of raw `dp.value`
- [x] 7.3 Remove unused `EXTRA_LIFE_POINTS`, `EXTRA_CALCULATOR_MODE`, and `EXTRA_PLAYER_ID` constants if no longer referenced after dead Activity removal

## 8. Enable R8 for Release Build

- [x] 8.1 Set `isMinifyEnabled = true` and add `isShrinkResources = true` in the `release` build type in `app/build.gradle.kts`
- [x] 8.2 Review `proguard-rules.pro` and add keep rules for DataStore, Compose, and any reflection usage if needed

## 9. Verify

- [x] 9.1 Build the project and fix any compilation errors
- [x] 9.2 Run on device/emulator and verify: app launches, life points display, calculator works, sound plays, mute toggle works, swipe between players works, restart works
