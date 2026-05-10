## ADDED Requirements

### Requirement: Player 2 screen uses launchSingleTop navigation
The system SHALL navigate to the Player 2 LifePoints screen using `launchSingleTop = true` to prevent duplicate entries on the back stack.

#### Scenario: Repeated swipe to Player 2
- **WHEN** the user swipes from Player 1 to Player 2, swipes back, then swipes to Player 2 again
- **THEN** the navigation back stack SHALL contain at most one Player 2 entry

### Requirement: NavController is not stored as an Activity field
The system SHALL NOT hold a reference to `NavHostController` in the Activity class. Hardware key events SHALL be routed through a ViewModel-based callback mechanism instead of accessing a stored NavController.

#### Scenario: Hardware key on calculator screen after recomposition
- **WHEN** the composable tree recomposes and the user presses STEM_1 on the calculator screen
- **THEN** the key event SHALL be handled correctly without referencing a stale NavController

#### Scenario: Hardware key triggers restart on life points screen
- **WHEN** the user presses STEM_1 while on the LifePoints screen
- **THEN** the ViewModel's `restart()` function SHALL be called

### Requirement: onRestart callback is safely invoked
The `LifePointsScreen` composable SHALL safely invoke the `onRestart` callback only when it is non-null. The clickable modifier SHALL use an explicit null check rather than relying on smart-cast across recomposition boundaries.

#### Scenario: Tapping lost screen triggers restart
- **WHEN** the life points display shows 0 and `onRestart` is non-null and the user taps the screen
- **THEN** `onRestart` SHALL be invoked exactly once

#### Scenario: Life points are positive
- **WHEN** the life points are greater than 0
- **THEN** no clickable restart modifier SHALL be applied to the screen

