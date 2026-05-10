### Requirement: Navigation route definitions live in the navigation package
Route definitions (sealed class, route strings, route factory methods) SHALL reside in `presentation/navigation/`, not alongside screen composables.

#### Scenario: Route sealed class location
- **WHEN** a developer looks for route definitions
- **THEN** they are found in `presentation/navigation/Routes.kt` as a sealed class named `Routes`

### Requirement: Navigation destinations use the Screen suffix
Composables that are direct targets of `SwipeDismissableNavHost` routes SHALL be named with a `*Screen` suffix.

#### Scenario: Life points destination naming
- **WHEN** the NavHost routes to the life points pager
- **THEN** the composable is named `LifePointsScreen`

#### Scenario: Calculator destination naming
- **WHEN** the NavHost routes to the calculator
- **THEN** the composable is named `CalculatorScreen`

### Requirement: Sub-screen components use non-Screen suffixes
Composables that are components within a screen (not direct nav destinations) SHALL NOT use the `*Screen` suffix. Pager content SHALL use the `*Page` suffix.

#### Scenario: Life points pager page naming
- **WHEN** a single-player life points view is rendered as a page within `LifePointsScreen`
- **THEN** the composable is named `LifePointsPage`
