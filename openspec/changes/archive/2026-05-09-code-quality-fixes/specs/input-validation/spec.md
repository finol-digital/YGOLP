## ADDED Requirements

### Requirement: Calculator results are clamped to valid range
The system SHALL clamp calculator results to a minimum of 0 and a maximum of 99999. Life points SHALL NOT become negative.

#### Scenario: Subtraction that would go negative
- **WHEN** the user subtracts a value larger than their current life points (e.g., 8000 - 9000)
- **THEN** the result SHALL be clamped to 0

#### Scenario: Addition that exceeds maximum
- **WHEN** the user adds a value that would bring life points above 99999
- **THEN** the result SHALL be clamped to 99999

#### Scenario: Direct set to zero
- **WHEN** the user sets life points directly to 0 via the "=>" mode
- **THEN** the result SHALL be accepted as 0 (valid value)

### Requirement: Release build enables code shrinking
The release build type SHALL have `isMinifyEnabled = true` and `isShrinkResources = true` to reduce APK size and apply code obfuscation.

#### Scenario: Release build produces minified APK
- **WHEN** the release build variant is assembled
- **THEN** the APK SHALL be processed by R8 with both minification and resource shrinking enabled

### Requirement: Color values use proper Compose API
The system SHALL use `Color(0xFFFBFF0C)` (unsigned long) instead of `Color(0xFFFBFF0C.toInt())`. The `CornerRadius` in `PlayerIndicator` SHALL use density-aware pixel values via `with(density) { x.dp.toPx() }` rather than raw `dp.value`.

#### Scenario: Life points text color renders correctly
- **WHEN** the `LifePointsText` composable is rendered
- **THEN** the text color SHALL be yellow (0xFFFBFF0C) using the proper Compose Color constructor

#### Scenario: Player indicator corners render at correct size
- **WHEN** the `PlayerIndicator` composable is rendered on different screen densities
- **THEN** the corner radius SHALL scale correctly with screen density

