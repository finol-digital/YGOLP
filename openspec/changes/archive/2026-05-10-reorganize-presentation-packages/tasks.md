## 1. Extract route definitions

- [x] 1.1 Create `presentation/navigation/` package with `Routes.kt` — move the sealed class from `Screens.kt`, rename `Screens` to `Routes`
- [x] 1.2 Delete `screens/Screens.kt`
- [x] 1.3 Update all references from `Screens.*` to `Routes.*` across the codebase

## 2. Rename life points files

- [x] 2.1 Rename `LifePointsScreen.kt` to `LifePointsPage.kt` and rename the composable function from `LifePointsScreen` to `LifePointsPage`
- [x] 2.2 Rename `LifePointsHorizontalPager.kt` to `LifePointsScreen.kt` and rename the composable function from `LifePointsHorizontalPager` to `LifePointsScreen`
- [x] 2.3 Update all call sites and imports for the renamed composables

## 3. Verify

- [x] 3.1 Build the project and confirm no compilation errors
