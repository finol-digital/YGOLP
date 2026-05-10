## ADDED Requirements

### Requirement: UI colors are defined as named constants
The system SHALL define all UI colors (life points text yellow, Player 2 gradient colors, indicator white) as named `Color` constants in a centralized location. Composables SHALL reference these constants instead of inline hex values.

#### Scenario: Life points text uses named color
- **WHEN** the `LifePointsText` composable renders
- **THEN** it SHALL use a named constant (e.g., `LpTextYellow`) instead of an inline `Color(0xFFFBFF0C)`

#### Scenario: Player 2 background uses named colors
- **WHEN** the Player 2 `LifePointsScreen` renders its gradient background
- **THEN** it SHALL use named color constants instead of inline `Color.Blue` and `Color.Black`

