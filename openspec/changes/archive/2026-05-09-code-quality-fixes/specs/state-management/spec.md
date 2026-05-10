## ADDED Requirements

### Requirement: Life point state lives in a ViewModel
The system SHALL store all life point state (`lifePoints`, `lifePoints2`, `displayedLifePoints`, `displayedLifePoints2`, `isMuted`) in a `MainViewModel` class extending `androidx.lifecycle.ViewModel`. The Activity SHALL NOT hold mutable state as class properties.

#### Scenario: ViewModel survives configuration change
- **WHEN** the Activity is destroyed and recreated due to a configuration change
- **THEN** the ViewModel SHALL retain all life point values without re-reading from DataStore

#### Scenario: State is available to Compose
- **WHEN** the WearApp composable is rendered
- **THEN** it SHALL observe `StateFlow` properties from the ViewModel for all displayed values

### Requirement: DataStore loading does not block the main thread
The system SHALL NOT use `runBlocking` on the main thread. DataStore preferences SHALL be loaded via a coroutine in the ViewModel scope using `stateIn()` or a similar non-blocking pattern.

#### Scenario: App launch with empty DataStore
- **WHEN** the app launches for the first time with no saved preferences
- **THEN** the ViewModel SHALL initialize with default values (life points = 0, muted = false) and trigger `start()` without blocking the main thread

#### Scenario: App launch with existing DataStore values
- **WHEN** the app launches with previously saved life point values in DataStore
- **THEN** the ViewModel SHALL load and display those values without any ANR risk

### Requirement: CountDownTimer animations are cancellable
The system SHALL store a reference to any active `CountDownTimer` for each player. When a new animation starts, the system SHALL cancel any existing timer for that player before starting the new one.

#### Scenario: Overlapping life point changes
- **WHEN** `changeLifePoints` is called for player 1 while a previous animation timer is still running for player 1
- **THEN** the previous timer SHALL be cancelled and the new timer SHALL start, preventing display flickering

#### Scenario: Activity stop during animation
- **WHEN** the Activity's `onStop` is called while a CountDownTimer is running
- **THEN** all active timers SHALL be cancelled

### Requirement: Use Kotlin idiomatic Random
The system SHALL use `kotlin.random.Random.nextInt()` instead of `java.util.Random()`. The Random instance SHALL NOT be re-created on every timer tick.

#### Scenario: Timer tick random number generation
- **WHEN** the CountDownTimer `onTick` fires
- **THEN** the system SHALL use a pre-existing Kotlin `Random` reference to generate the random display number

### Requirement: Dead Activity classes are removed
The system SHALL NOT contain `LifePointsActivity` or `CalculatorActivity` class bodies. The composable functions `LifePointsScreen` and `CalculatorScreen` SHALL remain as top-level composable functions in their respective files.

#### Scenario: Build succeeds without dead Activity classes
- **WHEN** the project is built after removing the dead Activity classes
- **THEN** the build SHALL succeed with no unresolved references

### Requirement: Swipe navigation fires at most once per gesture
The system SHALL prevent `onSwipePlayer` from being called more than once per drag gesture. A guard mechanism (flag or threshold) SHALL ensure a single navigation event per horizontal swipe.

#### Scenario: User performs a horizontal swipe
- **WHEN** the user swipes horizontally on the life points screen
- **THEN** `onSwipePlayer` SHALL be invoked exactly once, regardless of how many drag events are generated

