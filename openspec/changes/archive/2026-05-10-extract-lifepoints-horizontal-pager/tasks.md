## 1. Create LifePointsHorizontalPager

- [x] 1.1 Create `LifePointsHorizontalPager.kt` in `presentation/screens/` with a composable that takes viewModel state, pagerState, and navigation callback
- [x] 1.2 Move `HorizontalPager` setup from `WearApp` into `LifePointsHorizontalPager`, rendering `LifePointsScreen` per page
- [x] 1.3 Add mute button as fixed overlay (top-center, after HorizontalPager in Box) using existing inline code from `LifePointsScreen`
- [x] 1.4 Add `PlayerIndicator` as fixed overlay (bottom-center, after HorizontalPager in Box) driven by `pagerState.currentPage`

## 2. Simplify LifePointsScreen

- [x] 2.1 Remove mute button rendering (lines 117-132) from `LifePointsScreen`
- [x] 2.2 Remove `PlayerIndicator` call (lines 110-115) from `LifePointsScreen`
- [x] 2.3 Remove `isMuted` and `onToggleMute` parameters from `LifePointsScreen` signature
- [x] 2.4 Remove unused imports (`PlayerIndicator`, mute icons, etc.)

## 3. Update WearApp

- [x] 3.1 Replace inline `HorizontalPager` block in `WearApp` with `LifePointsHorizontalPager` call
- [x] 3.2 Remove unused imports from `MainActivity.kt`

## 4. Verify

- [x] 4.1 Build the project and confirm no compilation errors
