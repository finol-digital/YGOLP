## MODIFIED Requirements

### Requirement: CountDownTimer animations are cancellable
The system SHALL store a reference to any active `CountDownTimer` for each player. When a new animation starts, the system SHALL cancel any existing timer for that player before starting the new one. When `playSound` is false, the system SHALL NOT create a `CountDownTimer` and SHALL instead set the displayed life points value directly.

#### Scenario: Overlapping life point changes
- **WHEN** `changeLifePoints` is called for player 1 while a previous animation timer is still running for player 1
- **THEN** the previous timer SHALL be cancelled and the new timer SHALL start, preventing display flickering

#### Scenario: Activity stop during animation
- **WHEN** the Activity's `onStop` is called while a CountDownTimer is running
- **THEN** all active timers SHALL be cancelled

#### Scenario: Silent life point change skips timer
- **WHEN** `changeLifePoints` is called with `playSound = false`
- **THEN** no `CountDownTimer` SHALL be created and `displayedLifePoints` SHALL be set to the final value immediately

## ADDED Requirements

### Requirement: restart() sequences DataStore writes before animation
The `restart()` function SHALL await the DataStore edit that writes zero values before calling `changeLifePoints` for each player. The DataStore `collectLatest` collector SHALL NOT overwrite displayed values after `changeLifePoints` has set them.

#### Scenario: Restart writes are ordered
- **WHEN** `restart()` is called
- **THEN** the DataStore SHALL contain `0` for both players before `changeLifePoints(8000)` is called for either player

#### Scenario: No flicker during restart
- **WHEN** `restart()` completes and the scramble animation finishes
- **THEN** the displayed life points SHALL show 8000 for both players, not 0

### Requirement: Player identity uses a type-safe enum
The system SHALL define a `Player` enum with values `ONE` and `TWO`. All functions and composable parameters that accept a player identifier SHALL use this enum type instead of `Int`. The constants `PLAYER_1` and `PLAYER_2` SHALL be removed.

#### Scenario: Compile-time safety for player parameter
- **WHEN** a function is called with a player argument
- **THEN** the compiler SHALL enforce that only `Player.ONE` or `Player.TWO` is passed

#### Scenario: ViewModel uses Player enum
- **WHEN** `changeLifePoints` is called
- **THEN** it SHALL accept a `Player` enum parameter and branch on `Player.ONE` vs `Player.TWO`

