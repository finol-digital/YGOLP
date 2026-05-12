### Requirement: ExampleInstrumentedTest is replaced with real tests
The placeholder `ExampleInstrumentedTest.kt` SHALL be deleted and replaced with Compose UI test files that exercise actual app functionality.

#### Scenario: No placeholder test remains
- **WHEN** the `app/src/androidTest/` directory is inspected
- **THEN** `ExampleInstrumentedTest.kt` SHALL NOT exist

### Requirement: LifePointsScreen displays life points for both players
The instrumented tests SHALL verify that `LifePointsScreen` renders the life-point values passed to it.

#### Scenario: Default life points are displayed
- **WHEN** `LifePointsScreen` is rendered with both players at 8000 life points
- **THEN** the text "8000" SHALL be displayed on the screen

#### Scenario: Custom life points are displayed
- **WHEN** `LifePointsScreen` is rendered with player 1 at 5000 and player 2 at 3000
- **THEN** the text "5000" SHALL be visible on the first page

### Requirement: LifePointsScreen mute icon toggles
The instrumented tests SHALL verify that tapping the mute icon triggers the mute toggle callback.

#### Scenario: Mute icon is displayed
- **WHEN** `LifePointsScreen` is rendered with `isMuted = false`
- **THEN** an icon with content description "Mute" SHALL be displayed

#### Scenario: Unmute icon is displayed when muted
- **WHEN** `LifePointsScreen` is rendered with `isMuted = true`
- **THEN** an icon with content description "Unmute" SHALL be displayed

### Requirement: CalculatorScreen displays current life points
The instrumented tests SHALL verify that the calculator screen shows the current life-point value.

#### Scenario: Life points shown on calculator
- **WHEN** `CalculatorScreen` is rendered with `lifePoints = 8000`
- **THEN** the text "8000" SHALL be displayed

### Requirement: CalculatorScreen digit entry works
The instrumented tests SHALL verify that tapping digit buttons updates the operand display.

#### Scenario: Tapping digit 5 updates operand
- **WHEN** `CalculatorScreen` is rendered and the user taps the "5" button
- **THEN** the operand display SHALL show "5"

### Requirement: CalculatorScreen submit triggers callback
The instrumented tests SHALL verify that tapping the submit button invokes the `onSubmit` callback with the computed result.

#### Scenario: Submit with SET mode
- **WHEN** `CalculatorScreen` is rendered in SET mode, the user enters "5000" and taps submit
- **THEN** `onSubmit` SHALL be invoked with the value `5000`

### Requirement: CalculatorScreen mode cycling works
The instrumented tests SHALL verify that tapping the operator button cycles through calculator modes.

#### Scenario: Cycle from SET to SUBTRACT
- **WHEN** `CalculatorScreen` is rendered in SET mode and the user taps the operator button
- **THEN** the operator display SHALL change to the subtract operator

