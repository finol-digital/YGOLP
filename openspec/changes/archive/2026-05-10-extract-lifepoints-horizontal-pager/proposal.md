## Why

The mute button and player indicator are currently rendered inside each `LifePointsScreen` instance, but they are pager-level concerns: the mute button is a global control and the player indicator reflects pager navigation state. This causes both elements to slide with page content during swipes and results in duplicate rendering across pages. Extracting a `LifePointsHorizontalPager` composable will fix the mute button in place, properly animate the player indicator with pager state, and simplify `LifePointsScreen` to only concern itself with player-specific content.

## What Changes

- Create a new `LifePointsHorizontalPager` composable that owns the `HorizontalPager`, the mute button overlay (fixed, top-center), and the `PlayerIndicator` overlay (bottom-center, state driven by pager)
- Remove the mute button and `PlayerIndicator` from `LifePointsScreen`, along with `isMuted`/`onToggleMute` parameters
- Update `WearApp` in `MainActivity.kt` to use `LifePointsHorizontalPager` instead of inline pager setup

## Capabilities

### New Capabilities
- `pager-chrome`: Pager-level UI overlay composable managing the mute button and player indicator independently from page content

### Modified Capabilities

## Impact

- `LifePointsScreen.kt`: Remove mute button, player indicator, and related parameters (`isMuted`, `onToggleMute`)
- `MainActivity.kt`: Replace inline `HorizontalPager` block in `WearApp` with `LifePointsHorizontalPager` call
- New file: `LifePointsHorizontalPager.kt` in `presentation/screens/`
- `PlayerIndicator.kt`: No changes needed (already a standalone composable)
