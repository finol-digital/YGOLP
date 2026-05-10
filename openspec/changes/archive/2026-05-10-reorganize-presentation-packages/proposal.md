## Why

The `screens` package conflates navigation destinations with UI components. `LifePointsHorizontalPager` is the actual navigation destination but isn't named as a "Screen", while `LifePointsScreen` is a page component within the pager but carries the "Screen" suffix. Route definitions (`Screens.kt`) also live alongside composables when they serve a different purpose. This makes the code harder to reason about as you scan the package.

## What Changes

- Extract route definitions from `screens/Screens.kt` into a new `navigation/Routes.kt`, renaming the sealed class from `Screens` to `Routes`
- Rename `LifePointsHorizontalPager.kt` to `LifePointsScreen.kt` (it's the actual nav destination)
- Rename the current `LifePointsScreen.kt` to `LifePointsPage.kt` (it's a page within the pager)
- Update all references across the codebase

## Capabilities

### New Capabilities

- `presentation-structure`: Package organization conventions for the presentation layer — what goes in `navigation/` vs `screens/`, naming rules for destinations vs components

### Modified Capabilities

## Impact

- `presentation/screens/` — files renamed and moved
- `presentation/navigation/` — new package created with route definitions
- `MainActivity.kt` / `WearApp` composable — imports updated
- No behavioral changes, no API changes, no dependency changes
