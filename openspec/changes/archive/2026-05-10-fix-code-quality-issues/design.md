## Context

YGOLP is a WearOS Yu-Gi-Oh! life point tracker built with Jetpack Compose, targeting Wear OS 4+. The app uses a single-Activity architecture with Compose Navigation, a `MainViewModel` with DataStore persistence, and a `SoundManager` for audio feedback. A code-quality audit identified 12 issues ranging from race conditions and resource mismanagement to build configuration and accessibility gaps.

The codebase is small (~700 lines of Kotlin across 7 files) making this a contained refactor with low blast radius.

## Goals / Non-Goals

**Goals:**
- Eliminate all identified race conditions and potential crashes
- Ensure release builds don't ship debug tooling
- Improve accessibility for TalkBack users on WearOS
- Clean up navigation to prevent back-stack issues
- Make the codebase more maintainable (enums over magic ints, theme colors)

**Non-Goals:**
- Adding new features (no new screens, no new game modes)
- Migrating to a different navigation library
- Full ambient mode / always-on-display support (noted but out of scope)
- Upgrading Horologist to latest version (separate change)
- Rewriting SoundManager to use ExoPlayer or other advanced audio libraries

## Decisions

### D1: Fix restart race condition by sequencing writes
**Decision:** Make `restart()` suspend and `await` the DataStore edit that zeroes out LPs before calling `changeLifePoints(8000, ...)`.

**Alternative:** Remove the intermediate write-to-zero step entirely and just write 8000 directly. Rejected because the zero-write triggers the scramble animation which is a deliberate UX feature.

### D2: Use `MediaPlayer` with `setOnCompletionListener` for sounds needing callbacks
**Decision:** Replace `SoundPool` usage for `duel_start` and `its_time_to_duel` sounds with `MediaPlayer`, which provides a real completion callback. Keep `SoundPool` for short fire-and-forget sounds like `lifepoints_change`.

**Alternative:** Use a fixed `postDelayed` with hardcoded durations. Rejected because it's fragile and breaks if sound files change.

### D3: Use `launchSingleTop` for player navigation
**Decision:** Add `launchSingleTop = true` to the navigate call for Player 2's LifePoints screen.

**Alternative:** Use `popUpTo` with inclusive. Rejected as over-engineered — `launchSingleTop` is sufficient for this two-screen pattern.

### D4: Move hardware key handling to a shared callback flow
**Decision:** Replace the `navControllerInstance` Activity field with a simple lambda/callback stored in the ViewModel that the composable sets. The Activity's `onKeyDown` invokes the ViewModel's callback.

**Alternative:** Use a `Channel` or `SharedFlow` for key events. Rejected as over-engineering for two button mappings.

### D5: Introduce a `Player` enum
**Decision:** Create a `Player` enum with `ONE` and `TWO` values, replacing the `PLAYER_1`/`PLAYER_2` int constants. This provides type safety across the codebase.

### D6: Extract colors to a theme file
**Decision:** Create a `Theme.kt` with color constants (LP text yellow, P2 gradient colors, indicator white) referenced throughout the UI. Keep using `Color` constants rather than a full `MaterialTheme` override, as WearOS Material theme is limited.

### D7: Limit operand input length
**Decision:** Cap `operandText` at 5 characters in the `append()` function, matching `MAX_LIFE_POINTS = 99999`.

## Risks / Trade-offs

- **[Sound timing changes]** → Switching from SoundPool to MediaPlayer for two sounds changes exact playback timing. Mitigation: Test on physical watch; MediaPlayer has slightly higher latency but provides correct completion callbacks.
- **[Navigation behavior change]** → `launchSingleTop` changes recomposition behavior if user rapidly swipes. Mitigation: Test swipe-dismiss flow manually; behavior should be identical for normal use.
- **[Enum migration touchpoints]** → Replacing `Int` player IDs with an enum touches many files. Mitigation: Compile-time safety ensures no missed references.

