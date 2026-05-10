## ADDED Requirements

### Requirement: Calculator buttons have accessible content descriptions
All `CalculatorButton` and `OperatorButton` composables SHALL provide a `contentDescription` via semantics so that TalkBack can announce the button's function.

#### Scenario: TalkBack announces digit button
- **WHEN** TalkBack focus lands on the "7" calculator button
- **THEN** the screen reader SHALL announce "7"

#### Scenario: TalkBack announces operator button
- **WHEN** TalkBack focus lands on the operator toggle button showing "+"
- **THEN** the screen reader SHALL announce "Add" or an equivalent descriptive label

#### Scenario: TalkBack announces special buttons
- **WHEN** TalkBack focus lands on the "C" (clear), "X" (discard), "=" (submit), or "1/2" (halve) button
- **THEN** the screen reader SHALL announce "Clear", "Discard", "Submit", or "Halve" respectively

